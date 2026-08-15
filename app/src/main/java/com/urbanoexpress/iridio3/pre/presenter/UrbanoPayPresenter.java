package com.urbanoexpress.iridio3.pre.presenter;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.android.volley.VolleyError;
import com.urbanoexpress.iridio3.pre.data.rest.ApiRest;
import com.urbanoexpress.iridio3.pre.data.rest.ApiService;
import com.urbanoexpress.iridio3.pre.model.entity.ConsultarQRRequest;
import com.urbanoexpress.iridio3.pre.model.entity.GenerarQRRequest;
import com.urbanoexpress.iridio3.pre.model.entity.UrbanoPayInput;
import com.urbanoexpress.iridio3.pre.model.entity.UrbanoPayResult;
import com.urbanoexpress.iridio3.pre.model.util.ModelUtils;
import com.urbanoexpress.iridio3.pre.view.UrbanoPayView;

import org.json.JSONObject;

import java.util.Locale;

public class UrbanoPayPresenter {

    private static final String TAG = "UrbanoPayPresenter";

    /** Límite máximo por cada QR generado. Si el monto restante es mayor, se genera QR por este valor. */
    private static final double MAX_MONTO_POR_QR = 500.0;
    private static final int POLLING_INTERVAL_MS = 5000;

    private final UrbanoPayView view;
    private final UrbanoPayInput input;

    private final Handler pollingHandler = new Handler(Looper.getMainLooper());
    private Runnable pollingRunnable;

    private double montoPagadoAcumulado;
    private double montoQRActual;
    private int qrIndex;
    private int qrTotal;
    private String rqIdCode;
    private String lastDocNumero = "";
    private String lastNombre = "";

    /** Flag para evitar mostrar el toast de "Continuando con QR pendiente" más de una vez. */
    private boolean avisadoQRPendiente = false;

    public UrbanoPayPresenter(UrbanoPayView view, UrbanoPayInput input) {
        this.view = view;
        this.input = input;
        init();
    }

    private void init() {
        montoPagadoAcumulado = 0;
        qrIndex = 0;
        qrTotal = calcularTotalQRs(input.getMontoTotal());
        view.setTextTituloEntrega("GESTIONAR:\nEntrega " + input.getGuia());
        view.setTextGuiaInicial(input.getGuia());
        view.showEstadoInicial();
    }

    public void onDestroy() {
        stopPollingPago();
    }

    // ==================== Botones ====================

    public void onBtnGenerarQRClick() {
        requestQR();
    }

    public void onBtnEfectivoClick() {
        stopPollingPago();
        // Si ya pagó parcialmente por QR, resultado = PAGADO_PARCIAL_Y_EFECTIVO
        // Si nunca pagó nada por QR, resultado = EFECTIVO
        UrbanoPayResult.Status status = montoPagadoAcumulado > 0
                ? UrbanoPayResult.Status.PAGADO_PARCIAL_Y_EFECTIVO
                : UrbanoPayResult.Status.EFECTIVO;
        double pendiente = input.getMontoTotal() - montoPagadoAcumulado;
        view.closeWithResult(new UrbanoPayResult(
                status, montoPagadoAcumulado, pendiente,
                rqIdCode, lastDocNumero, lastNombre));
    }

    public void onBtnContinuarPagoClick() {
        stopPollingPago();
        view.closeWithResult(new UrbanoPayResult(
                UrbanoPayResult.Status.PAGADO_TOTAL,
                montoPagadoAcumulado, 0,
                rqIdCode, lastDocNumero, lastNombre));
    }

    public void onBackPressed() {
        stopPollingPago();
        // Back es cancelar solo si no hay pagos QR. Si ya cobró algo, cerrar como PAGADO_PARCIAL_Y_EFECTIVO
        UrbanoPayResult.Status status = montoPagadoAcumulado > 0
                ? UrbanoPayResult.Status.PAGADO_PARCIAL_Y_EFECTIVO
                : UrbanoPayResult.Status.CANCELADO;
        view.closeWithResult(new UrbanoPayResult(
                status, montoPagadoAcumulado,
                input.getMontoTotal() - montoPagadoAcumulado,
                rqIdCode, lastDocNumero, lastNombre));
    }

    // ==================== API ====================

    private void requestQR() {
        view.showProgressDialog("Generando QR...");
        // Calcular el monto solicitado (el server puede confirmarlo o corregirlo en la respuesta)
        montoQRActual = calcularMontoSiguienteQR();

        GenerarQRRequest request = new GenerarQRRequest(
                input.getGuia(),
                montoQRActual,
                input.getMontoTotal(),
                input.getNombreCliente(),
                input.getDniCliente()
        );

        ApiService.getInstance().requestJson(ApiRest.Api.URL_GENERAR_QR, request, new ApiService.ResponseListener() {
            @Override
            public void onResponse(JSONObject response) {
                view.dismissProgressDialog();
                try {
                    JSONObject data = response.optJSONObject("data");
                    if (data == null) {
                        view.showToast("Error al procesar el QR");
                        view.showEstadoInicial();
                        return;
                    }

                    // ==================== Sincronizar estado con el server ====================
                    // El server es la fuente de verdad: nos dice cuántas cuotas se pagaron, cuánto
                    // se acumuló, cuál es el total de la guía, y datos del último pagador.
                    //
                    // IMPORTANTE: cuando el server responde con el formato "fresh" (QR nuevo con
                    // http_code=200), NO trae los campos monto_pagado/cuotas_pagadas/guia_total.
                    // En ese caso usamos el estado LOCAL como fallback (que ya fue actualizado por
                    // consultarPago si hubo un pago previo detectado).
                    double guiaTotal = data.optDouble("guia_total", input.getMontoTotal());
                    double montoPagadoServer = data.optDouble("monto_pagado", montoPagadoAcumulado);
                    int cuotasPagadas = data.optInt("cuotas_pagadas", qrIndex);
                    double saldoPendiente = data.optDouble("saldo_pendiente", guiaTotal - montoPagadoServer);
                    double montoQREsta = data.optDouble("monto", montoQRActual);

                    montoPagadoAcumulado = montoPagadoServer;
                    qrIndex = cuotasPagadas + 1;   // esta es la cuota actual (1-based)
                    qrTotal = calcularTotalQRs(guiaTotal);
                    montoQRActual = montoQREsta;

                    // Extraer datos del último pagador (para el comprobante final)
                    JSONObject ultima = data.optJSONObject("ultima_cuota_pagada");
                    if (ultima != null) {
                        // El nombre y doc_numero pueden venir en el historial detallado
                        actualizarDatosPagadorDesdeHistorial(data.optJSONArray("historial"));
                    }

                    // ¿La guía ya está totalmente pagada? (saldo <= 0)
                    if (saldoPendiente <= 0.01) {
                        stopPollingPago();
                        String simboloMoneda = ModelUtils.getSimboloMoneda(view.getViewContext());
                        String montoFmt = simboloMoneda + " " + formatMonto(montoPagadoAcumulado);
                        view.setDatosComprobante("¡PAGADO!", input.getGuia(),
                                montoFmt, lastDocNumero, lastNombre);
                        view.showEstadoPagado();
                        return;
                    }

                    // ==================== Mostrar el QR ====================
                    // El campo del hash varía según el server:
                    //  - QR nuevo (http_code 200, "QR Hash guardado correctamente"): usa "hash"
                    //  - QR pendiente previo ("Guía ya cuenta con un QR generado pendiente de pago"): usa "qr_hash"
                    String hash = data.optString("qr_hash", null);
                    if (hash == null || hash.isEmpty()) hash = data.optString("hash", "");
                    rqIdCode = data.getString("rq_id_code");
                    String simboloMoneda = ModelUtils.getSimboloMoneda(view.getViewContext());
                    view.displayQR(hash);
                    view.setTextMonto(simboloMoneda + " " + formatMonto(montoQRActual));
                    view.setTextRqIdCode("ID Operación " + rqIdCode);
                    if (qrTotal > 1) {
                        view.setTextProgresoPago("Pago " + qrIndex + " de " + qrTotal
                                + " · Pagado " + simboloMoneda + " " + formatMonto(montoPagadoAcumulado)
                                + " de " + simboloMoneda + " " + formatMonto(guiaTotal));
                        view.setVisibilityProgreso(true);
                    } else {
                        view.setVisibilityProgreso(false);
                    }
                    view.showEstadoQR();

                    // Si el server indica que hay un QR pendiente (guía ya con pagos previos y este
                    // QR ya se había generado antes), avisar al motorizado que está retomando la sesión.
                    String sqlMsn = response.optString("sql_msn", "");
                    boolean qrPendienteDePagoPrevio = sqlMsn.toLowerCase()
                            .contains("qr generado pendiente de pago");
                    if (qrPendienteDePagoPrevio && !avisadoQRPendiente) {
                        avisadoQRPendiente = true;
                        if (cuotasPagadas > 0) {
                            view.showToast("Continuando pago. Ya se cobró "
                                    + simboloMoneda + " " + formatMonto(montoPagadoAcumulado)
                                    + " · Falta " + simboloMoneda + " " + formatMonto(saldoPendiente));
                        } else {
                            view.showToast("Continuando con QR pendiente de pago");
                        }
                    }

                    startPollingPago();
                } catch (Exception e) {
                    Log.e(TAG, "requestQR onResponse: ", e);
                    view.showToast("Error al procesar el QR");
                    view.showEstadoInicial();
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                view.dismissProgressDialog();
                view.showToast("Error al generar el QR, intente nuevamente");
                view.showEstadoInicial();
            }
        });
    }

    /**
     * Recorre el {@code historial} de cuotas y toma el {@code doc_numero} y {@code name}
     * de la ÚLTIMA cuota marcada como pagada. Se usan para el comprobante final.
     */
    private void actualizarDatosPagadorDesdeHistorial(org.json.JSONArray historial) {
        if (historial == null) return;
        for (int i = historial.length() - 1; i >= 0; i--) {
            JSONObject cuota = historial.optJSONObject(i);
            if (cuota == null) continue;
            if (cuota.optBoolean("pagado", false)) {
                lastDocNumero = cuota.optString("doc_numero", lastDocNumero);
                lastNombre = cuota.optString("name", lastNombre);
                return;
            }
        }
    }

    private void startPollingPago() {
        pollingRunnable = this::consultarPago;
        pollingHandler.postDelayed(pollingRunnable, POLLING_INTERVAL_MS);
    }

    private void stopPollingPago() {
        if (pollingRunnable != null) {
            pollingHandler.removeCallbacks(pollingRunnable);
            pollingRunnable = null;
        }
    }

    /**
     * Consulta el status del QR actual usando {@code /api/v1/pagos/status-qr} con
     * {@code {"rq_id_code": ...}}. Cuando el QR se marca como pagado, avanza:
     * <ul>
     *   <li>Suma su monto al acumulado</li>
     *   <li>Si ya se completó el total de la guía → muestra comprobante</li>
     *   <li>Si aún falta → genera el siguiente QR automáticamente</li>
     * </ul>
     */
    private void consultarPago() {
        if (rqIdCode == null) return;
        ConsultarQRRequest request = new ConsultarQRRequest(rqIdCode);
        ApiService.getInstance().requestJson(ApiRest.Api.URL_STATUS_QR, request,
                new ApiService.ResponseListener() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject payload = response.optJSONObject("data");
                    if (payload == null) payload = response;

                    String estado = payload.optString("estado", "");
                    boolean pagado = "PAGADO".equalsIgnoreCase(estado)
                            || payload.optBoolean("pagado", false);
                    if (!pagado) {
                        pollingHandler.postDelayed(pollingRunnable, POLLING_INTERVAL_MS);
                        return;
                    }

                    stopPollingPago();
                    double montoPagadoQR = payload.optDouble("monto", montoQRActual);
                    montoPagadoAcumulado += montoPagadoQR;
                    lastDocNumero = payload.optString("doc_numero", lastDocNumero);
                    lastNombre = payload.optString("name", lastNombre);

                    if (montoPagadoAcumulado >= input.getMontoTotal() - 0.01) {
                        String simboloMoneda = ModelUtils.getSimboloMoneda(view.getViewContext());
                        String montoFmt = simboloMoneda + " " + formatMonto(montoPagadoAcumulado);
                        view.setDatosComprobante("¡PAGADO!", input.getGuia(),
                                montoFmt, lastDocNumero, lastNombre);
                        view.showEstadoPagado();
                    } else {
                        String simboloMoneda = ModelUtils.getSimboloMoneda(view.getViewContext());
                        double restante = input.getMontoTotal() - montoPagadoAcumulado;
                        view.showToast("Pago " + qrIndex + " recibido. Falta " + simboloMoneda + " "
                                + formatMonto(restante));
                        requestQR();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "consultarPago: ", e);
                    pollingHandler.postDelayed(pollingRunnable, POLLING_INTERVAL_MS);
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                pollingHandler.postDelayed(pollingRunnable, POLLING_INTERVAL_MS);
            }
        });
    }

    // ==================== Helpers ====================

    private int calcularTotalQRs(double total) {
        if (total <= 0) return 0;
        return (int) Math.ceil(total / MAX_MONTO_POR_QR);
    }

    private double calcularMontoSiguienteQR() {
        double restante = input.getMontoTotal() - montoPagadoAcumulado;
        return Math.min(MAX_MONTO_POR_QR, restante);
    }

    private String formatMonto(double monto) {
        if (monto == Math.floor(monto)) {
            return String.valueOf((long) monto);
        }
        return String.format(Locale.US, "%.2f", monto);
    }
}
