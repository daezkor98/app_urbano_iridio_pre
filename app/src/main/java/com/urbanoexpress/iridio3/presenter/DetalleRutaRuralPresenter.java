package com.urbanoexpress.iridio3.presenter;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.VolleyError;
import com.urbanoexpress.iridio3.R;
import com.urbanoexpress.iridio3.model.entity.Data;
import com.urbanoexpress.iridio3.model.entity.DescargaRuta;
import com.urbanoexpress.iridio3.model.entity.EstadoRuta;
import com.urbanoexpress.iridio3.model.entity.GestionLlamada;
import com.urbanoexpress.iridio3.model.entity.GuiaGestionada;
import com.urbanoexpress.iridio3.model.entity.MotivoDescarga;
import com.urbanoexpress.iridio3.model.entity.Ruta;
import com.urbanoexpress.iridio3.model.interactor.RutaPendienteInteractor;
import com.urbanoexpress.iridio3.model.interactor.callback.RequestCallback;
import com.urbanoexpress.iridio3.model.util.ModelUtils;
import com.urbanoexpress.iridio3.presenter.helpers.ShowDescargaRutaHelper;
import com.urbanoexpress.iridio3.ui.dialogs.DetalleRequerimientoGuiaBottomSheet;
import com.urbanoexpress.iridio3.ui.dialogs.GaleriaGEDialog;
import com.urbanoexpress.iridio3.ui.dialogs.GestionDevolucionGuiaBottomSheet;
import com.urbanoexpress.iridio3.ui.dialogs.PiezasGuiaBottomSheet;
import com.urbanoexpress.iridio3.ui.dialogs.RecolectaGEDialog;
import com.urbanoexpress.iridio3.ui.dialogs.TelefonosGuiaBottomSheet;
import com.urbanoexpress.iridio3.ui.model.DetailsItem;
import com.urbanoexpress.iridio3.util.CommonUtils;
import com.urbanoexpress.iridio3.util.Preferences;
import com.urbanoexpress.iridio3.util.constant.LocalAction;
import com.urbanoexpress.iridio3.view.DetalleRutaRuralView;

import org.apache.commons.text.WordUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DetalleRutaRuralPresenter {

    private final String TAG = DetalleRutaRuralPresenter.class.getSimpleName();

    private DetalleRutaRuralView view;
    private RutaPendienteInteractor interactor;

    private List<MotivoDescarga> dbMotivoGestionLlamada = Collections.emptyList();
    private String[] motivoGestionLlamadaItems;

    private Ruta ruta;

    private ShowDescargaRutaHelper showDescargaRutaHelper;

    private TelephonyManager telephonyManager;

    private boolean isButtonPhoneClicked = false;

    private Uri uriPhoneCalled;

    private CallStateListener callStateListener;

    private int selectedIndexTipoResultadoLlamada = 0;

    private int numVecesGestionado;

    private Date callInitDate;
    private Date callEndDate;

    public DetalleRutaRuralPresenter(DetalleRutaRuralView view, Ruta ruta, int numVecesGestionado) {
        this.view = view;
        this.ruta = ruta;
        this.numVecesGestionado = numVecesGestionado;
        this.interactor = new RutaPendienteInteractor(view.getViewContext());
        init();
    }

    private void init() {
        LocalBroadcastManager.getInstance(view.getViewContext())
                .registerReceiver(descargaFinalizadaReceiver,
                        new IntentFilter("OnDescargaFinalizada"));
        LocalBroadcastManager.getInstance(view.getViewContext())
                .registerReceiver(callPhoneReceiver, new IntentFilter(LocalAction.CALL_PHONE_ACTION));

        if (ruta != null) {
            setupDetalleRuta();
            loadDescargaRutaHelper();
            showBtnGestionar();
            registerCallStateListener();
        }
    }

    public void onMenuReady() {
        if (ruta.getTipoZona() == Ruta.ZONA.RURAL) {
            CommonUtils.setVisibilityOptionMenu(
                    view.getMenuToolbar(), R.id.action_agregar_guia, false);

            if (numVecesGestionado == 1) {
                CommonUtils.setVisibilityOptionMenu(view.getMenuToolbar(), R.id.action_galeria, false);
            }
        } else {
            switch (numVecesGestionado) {
                case 1:
                    CommonUtils.setVisibilityOptionMenu(
                            view.getMenuToolbar(), R.id.action_galeria, false);
                    CommonUtils.setVisibilityOptionMenu(view.getMenuToolbar(),
                            R.id.action_agregar_guia, false);
                    break;
                case 2:
                    CommonUtils.setVisibilityOptionMenu(
                            view.getMenuToolbar(), R.id.action_volverConfirmarGestion, false);

                    GuiaGestionada guiaGestionada = interactor.selectRutaGestionada(
                            ruta.getIdServicio(), ruta.getLineaNegocio());
                    if (ModelUtils.isGuiaRecoleccion(ruta.getTipo()) &&
                            guiaGestionada.getGuiaElectronica().length() > 0) {
                        CommonUtils.setVisibilityOptionMenu(view.getMenuToolbar(),
                                R.id.action_agregar_guia, true);
                    } else {
                        CommonUtils.setVisibilityOptionMenu(view.getMenuToolbar(),
                                R.id.action_agregar_guia, false);
                    }
                    break;
            }
        }
    }

    public void onBtnGestionarClick() {
        if (ruta.getTipoZona() == Ruta.ZONA.RURAL) {
            showDescargaRutaHelper.onClickDescarga();
        } else {
            if (validateEstadoRuta()) showDescargaRutaHelper.onClickDescarga();
        }
    }

    public void onBtnIndicacionesClick() {
        if (ruta != null) {
            if (CommonUtils.isValidCoords(ruta.getGpsLatitude(), ruta.getGpsLongitude())) {
                try {
                    //Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + ruta.getGpsLatitude() + "," + ruta.getGpsLongitude()+"(Ubicación de Entrega)");
                    Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + ruta.getGpsLatitude() + "," + ruta.getGpsLongitude());
                    Intent intent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    //intent.setPackage("com.google.android.apps.maps");
                    view.getViewContext().startActivity(intent);
                } catch (ActivityNotFoundException ex) {
                    view.showToast(
                            "Lo sentimos, no hay ninguna aplicación de mapas instalada en el teléfono.");
                }
            } else {
                view.showToast(R.string.activity_detalle_ruta_message_no_hay_gps);
            }
        }
    }

    public void onBtnLlamarClick() {
        TelefonosGuiaBottomSheet bottomSheet = TelefonosGuiaBottomSheet.newInstance(ruta);
        bottomSheet.show(((AppCompatActivity) view.getViewContext()).getSupportFragmentManager(),
                TelefonosGuiaBottomSheet.TAG);
    }

    public void onBtnPiezasClick() {
        PiezasGuiaBottomSheet bottomSheet = PiezasGuiaBottomSheet.newInstance(ruta);
        bottomSheet.show(((AppCompatActivity) view.getViewContext()).getSupportFragmentManager(),
                PiezasGuiaBottomSheet.TAG);
    }

    public void onBtnRequerimientoClick() {
        DetalleRequerimientoGuiaBottomSheet bottomSheet =
                DetalleRequerimientoGuiaBottomSheet.newInstance(ruta);
        bottomSheet.show(((AppCompatActivity) view.getViewContext()).getSupportFragmentManager(),
                DetalleRequerimientoGuiaBottomSheet.TAG);
    }

    public void onBtnDevolverClick() {
        GestionDevolucionGuiaBottomSheet bottomSheet =
                GestionDevolucionGuiaBottomSheet.newInstance(ruta);
        bottomSheet.show(((AppCompatActivity) view.getViewContext()).getSupportFragmentManager(),
                GestionDevolucionGuiaBottomSheet.TAG);
    }

    public void onClickActionGaleria() {
        if (ruta.getEstadoDescarga() == Ruta.EstadoDescarga.GESTIONADO) {
            GaleriaGEDialog fragment = GaleriaGEDialog.newInstance(ruta);
            FragmentManager fm = ((AppCompatActivity) view.getViewContext()).getSupportFragmentManager();
            fragment.show(fm, GaleriaGEDialog.TAG);
        } else {
            view.showMsgGaleriaNodisponible();
        }
    }

    public void onClickActionVolverConfirmarGestion() {
        if (ruta.getTipoZona() == Ruta.ZONA.RURAL) {
            showDescargaRutaHelper.onClickVolverConfirmarGestionGuiaVisitada();
        } else {
            if (validateEstadoRuta()) {
                if (validateOrdenDescarga()) {
                    showDescargaRutaHelper.onClickVolverConfirmarGestion();
                } else {
                    view.showMsgGestionNoPermitidaPorOrdenDescarga();
                }
            }
        }
    }

    public void onActionAgregarGuiaRecoleccion() {
        RecolectaGEDialog fragmentRecolectaConGuia = RecolectaGEDialog.newInstance(
                new ArrayList<Ruta>(Arrays.asList(ruta)), 2, true);
        FragmentManager fragmentManager = ((AppCompatActivity) view.getViewContext())
                .getSupportFragmentManager();
        fragmentRecolectaConGuia.show(fragmentManager, RecolectaGEDialog.TAG);
    }

    public void onBtnDescargarMotivosGestionLlamadaClick() {
        if (CommonUtils.validateConnectivity(view.getViewContext())) {
            view.showProgressDialog();
            final RequestCallback callback = new RequestCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    try {
                        view.dismissProgressDialog();
                        if (response.getBoolean("success")) {
                            interactor.deleteMotivos(MotivoDescarga.Tipo.GESTION_CON_LLAMADA);
                            saveMotivos(response.getJSONArray("data"));

                            view.showToast(
                                    R.string.activity_detalle_ruta_msg_descarga_motivo_gestion_llamada);
                        } else {
                            view.showToast(response.getString("msg_error"));
                        }
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                        view.dismissProgressDialog();
                        view.showToast(R.string.json_object_exception);
                    }
                }

                @Override
                public void onError(VolleyError error) {
                    error.printStackTrace();
                    view.dismissProgressDialog();
                    view.showToast(R.string.volley_error_message);
                }
            };

            String[] params = new String[] {
                    String.valueOf(MotivoDescarga.Tipo.GESTION_CON_LLAMADA),
                    Preferences.getInstance().getString("idUsuario", "")
            };

            interactor.getMotivos(params, callback);
        }
    }

    public void onMotivoGestionLlamadaSelected(int position) {
        selectedIndexTipoResultadoLlamada = position;
    }

    public void onBtnConfirmarResultadoGestionLlamadaClick() {
        if (selectedIndexTipoResultadoLlamada == 0) {
            saveRegistroLlamada("134");
        } else {
            selectedIndexTipoResultadoLlamada = 0;
            view.showDialogSeleccionarMotivoLlamadaNoContactada(motivoGestionLlamadaItems);
        }
    }

    public void onBtnConfirmarMotivoGestionLlamadaClick() {
        saveRegistroLlamada(dbMotivoGestionLlamada.get(selectedIndexTipoResultadoLlamada).getIdMotivo());
    }

    public void onDestroy() {
        LocalBroadcastManager.getInstance(view.getViewContext())
                .unregisterReceiver(descargaFinalizadaReceiver);
        LocalBroadcastManager.getInstance(view.getViewContext()).unregisterReceiver(callPhoneReceiver);

        telephonyManager.listen(callStateListener, PhoneStateListener.LISTEN_NONE);
    }

    private void showBtnGestionar() {
        if (isGuiaGestionFinalizado()) {
            view.hideBtnGestionar();
            return;
        }

        if (ModelUtils.hasGuiaReqDevolucionShipper(ruta)) {
            view.hideBtnGestionar();
            return;
        }

        if (ruta.getTipoZona() == Ruta.ZONA.URBANO) {
            switch (numVecesGestionado) {
                case 1:
                    if (!validateOrdenDescarga()) {
                        view.hideBtnGestionar();
                    }
                    break;
                case 2:
                    if (hasDeliveryPending()) {
                        switch (ruta.getTipoEnvio().toUpperCase()) {
                            case Ruta.TipoEnvio.PAQUETE:
                            case Ruta.TipoEnvio.VALIJA:
                            case Ruta.TipoEnvio.LOGISTICA_INVERSA:
                                break;
                            default:
                                view.hideBtnGestionar();
                                break;
                        }
                    } else {
                        view.hideBtnGestionar();
                    }
                    break;
            }
        }
    }

    private void onBtnCallPhoneClick(String phone) {
        loadMotivosGestionLlamada();

        if (dbMotivoGestionLlamada.size() != 0) {
            try {
                isButtonPhoneClicked = true;
                callInitDate = new Date();

                uriPhoneCalled = Uri.fromParts("tel", phone, null);
                Intent intent = new Intent(Intent.ACTION_DIAL, uriPhoneCalled);
                view.getViewContext().startActivity(intent);
            } catch (ActivityNotFoundException ex) {
                ex.printStackTrace();
                view.showToast("No se encontro una aplicación para hacer llamadas en el dispositivo.");
            }
        } else {
            view.showDialogDescargarMotivosGestionLlamada();
        }
    }

    private void setupDetalleRuta() {
        view.setTextBarra(ruta.getGuia());
        view.setTextDireccion(ruta.getDireccion() + " " +
                view.getViewContext().getString(R.string.text_dot) + " " + ruta.getDistrito());

        showHorario();
        showDetalleRutaList();
        showAlertaEstadoShipper();

        if (ModelUtils.isGuiaEntrega(ruta.getTipo())) {
            view.showActionPiezasContainer();

            if (ruta.getGuiaRequerimiento() != null) {
                if (ruta.getGuiaRequerimiento().equals("1")) {
                    if (ruta.getGuiaRequerimientoCHK() != null) {
                        if (ruta.getGuiaRequerimientoCHK().equals("22")) {
                            view.showActionRequerimientoContainer();
                        } else if (ruta.getGuiaRequerimientoCHK().equals("30")) {
                            view.showAlertaReqDevolucionShipper();
                            view.setTextMsgReqDevolucionShipper("Esta guía por motivo de " +
                                    ruta.getGuiaRequerimientoMotivo().toUpperCase() +
                                    ", debe ser devuelta al shipper.");
                        }
                    }
                }
            }
        }

        if (ruta.getTipoZona() == Ruta.ZONA.RURAL) {
            if (numVecesGestionado == 2) {
                view.showActionDevolverContainer();
            }
        }

        if (CommonUtils.parseDouble(ruta.getImporte()) > 0) {
            view.setTextMedioPago("Medio de pago: " + ruta.getMedioPago());
            view.setTextImporte(ModelUtils.getSimboloMoneda(view.getViewContext()) + " " + ruta.getImporte());
            view.showCobrarClienteContainer();
        }
    }

    private void showHorario() {
        String horario = ruta.getHorarioEntrega();
        int colorHorario = ModelUtils.getLblColorHorario(ruta.getTipo(),
                ruta.getTipoEnvio(), ruta.getFechaRuta(), ruta.getHorarioEntrega());;

        if (ruta.getTipoZona() == Ruta.ZONA.RURAL) {
            horario = CommonUtils.parseDateToElapsedDays(ruta.getFechaRuta());
        } else {
            if (ruta.getResultadoGestion() != 0) {
                GuiaGestionada guiaGestionada = interactor.selectRutaGestionada(ruta.getIdServicio(),
                        ruta.getLineaNegocio());
                if (guiaGestionada != null) {
                    try {
                        Date fechaHoraGestion = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(
                                guiaGestionada.getFecha() + " " + guiaGestionada.getHora());
                        horario = CommonUtils.fomartHorarioAproximado(fechaHoraGestion.getTime(), true);
                    } catch (ParseException ex) {
                        ex.printStackTrace();
                    }
                }
            } else {
                if (ModelUtils.isGuiaEntrega(ruta.getTipo())) {
                    horario = CommonUtils.fomartHorarioAproximado(ruta.getHorarioAproximado(), true);
                }
            }

            ruta.setHorarioEntrega(horario);
        }

        view.setTextHorario(horario);
        view.setColorTextHorario(colorHorario);
        view.setColorIconHorario(colorHorario);
    }

    private void showDetalleRutaList() {
        String habilitantes = getHabilitantes();

        List<DetailsItem> details = new ArrayList<>();
        if (ruta.getTipoZona() == Ruta.ZONA.RURAL) {
            details.add(new DetailsItem("Fecha despacho", ruta.getFechaRuta()));
        }

        details.add(new DetailsItem(
                ModelUtils.isGuiaEntrega(ruta.getTipo()) ? "Destinatario" : "Contacto", ruta.getContacto()));

        if (ModelUtils.isGuiaRecoleccion(ruta.getTipo()) &&
                !TextUtils.isEmpty(ruta.getCentroActividad())) {
            details.add(new DetailsItem("Centro de actividad", ruta.getCentroActividad()));
        }

        details.add(new DetailsItem("Shipper", ruta.getShipper()));

        if (!habilitantes.isEmpty()) {
            details.add(new DetailsItem("Habilitantes", habilitantes));
        }

        details.add(new DetailsItem("Contenido", ruta.getDescripcion()));

        if (ModelUtils.isGuiaEntrega(ruta.getTipo())) {
            details.add(new DetailsItem("Total piezas", ruta.getPiezas()));
        }

        details.add(new DetailsItem("Observaciones", ruta.getObservaciones()));
        details.add(new DetailsItem("Anotaciones", ruta.getAnotaciones()));

        view.showDetalleRuta(details);
    }

    private void showAlertaEstadoShipper() {
        int idResIcon = 0, bg = 0;
        String msg = "";

        if (ruta.getEstadoShipper() != null) {
            switch (ruta.getEstadoShipper()) {
                case "1":  // Inicial
                    idResIcon = R.drawable.ic_estado_cliente_inicial_white;
                    msg = "El estado del shipper es inicial, tomar en cuenta al ";
                    bg = R.color.yellow_2;
                    break;
                case "3":  // Vip
                    idResIcon = R.drawable.ic_estado_cliente_vip_white;
                    msg = "El estado del shipper es vip, tomar en cuenta al ";
                    bg = R.color.green_4;
                    break;
                case "4":  // Critico
                    idResIcon = R.drawable.ic_estado_cliente_critico_white;
                    msg = "El estado del shipper es crítico, tomar en cuenta al ";
                    bg = R.color.colorPrimary;
                    break;
            }
        }

        if (!msg.isEmpty()) {
            if (ModelUtils.isGuiaEntrega(ruta.getTipo())) {
                msg += "entregar.";
            } else if (ModelUtils.isGuiaRecoleccion(ruta.getTipo())) {
                msg += "recolectar.";
            }
            view.showAlertaEstadoShipper(idResIcon, bg, msg);
        }
    }

    private String getHabilitantes() {
        if (ruta.getHabilitantes() != null && !ruta.getHabilitantes().isEmpty()) {
            Stream<String> stream = Stream.of(ruta.getHabilitantes().split("-"));
            String dot = view.getViewContext().getString(R.string.text_dot) + " ";
            return stream.collect(Collectors.joining("\n" + dot, dot, ""));
        }
        return "";
    }

    private void loadDescargaRutaHelper() {
        showDescargaRutaHelper = new ShowDescargaRutaHelper(
                view.getViewContext(), new ArrayList<Ruta>(Arrays.asList(ruta)), numVecesGestionado);
    }

    private boolean validateOrdenDescarga() {
        return ruta.getSecuencia().equals("1");
    }

    private boolean isGuiaGestionFinalizado() {
        DescargaRuta descargaRuta = interactor.selectDescargaRuta(
                ruta.getIdServicio(), ruta.getLineaNegocio());
        if (descargaRuta != null) {
            return descargaRuta.getProcesoDescarga() == DescargaRuta.Entrega.FINALIZADO;
        }
        return false;
    }

    private boolean hasDeliveryPending() {
        DescargaRuta descargaRuta = interactor.selectDescargaRuta(
                ruta.getIdServicio(), ruta.getLineaNegocio());
        if (descargaRuta != null) {
            return descargaRuta.getProcesoDescarga() == DescargaRuta.Entrega.ENTREGAR;
        }
        return false;
    }

    private boolean validateEstadoRuta() {
        List<EstadoRuta> estadoRuta = interactor.selectAllEstadoRuta();

        int estados = 0;
        boolean validateEstado = false;

        if (estadoRuta.size() > 0) {
            for (EstadoRuta estado: estadoRuta) {
                if (estado.getEstado() == EstadoRuta.Estado.INICIADO) {
                    estados = 1;
                }

                if (estado.getEstado() == EstadoRuta.Estado.FINALIZADO) {
                    estados = 2;
                }
            }
        } else {
            Log.d(TAG, "estadoRuta: " + estadoRuta.size());
        }

        switch (estados) {
            case 0:
                view.showMsgIniciarRuta();
                break;
            case 1:
                validateEstado = true;
                break;
            case 2:
                view.showMsgRutaFinalizada();
                break;
        }

        return validateEstado;
    }

    private void registerCallStateListener() {
        callStateListener = new CallStateListener();

        telephonyManager = (TelephonyManager)
                view.getViewContext().getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(callStateListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    /**
     * Listener to detect incoming calls.
     */
    private class CallStateListener extends PhoneStateListener {

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);

            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    // called when someone is ringing to this phone
                    Log.d("MainActivity", "Ringing 2: " + incomingNumber);
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    // called when someone is ringing to this phone
                    Log.d("MainActivity", "OffHook 2: " + incomingNumber);
                    callInitDate = new Date();
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    // called when someone is ringing to this phone
                    callEndDate = new Date();

                    if (isButtonPhoneClicked) {
                        Log.d("MainActivity", "LLAMADA FINALIZADA ACEPTADA: " + incomingNumber);

                        String numberPhoneCalled = uriPhoneCalled.getSchemeSpecificPart().replace(" ", "");
                        Log.d("MainActivity", "LLAMADA FINALIZADA ACEPTADA NUMBERPHONECALLED: " + numberPhoneCalled);

                        if (CommonUtils.isAndroidPie() ||
                                uriPhoneCalled != null && numberPhoneCalled.equals(incomingNumber)) {
                            Log.d("MainActivity", "LLAMADA FINALIZADA VALIDA: " + incomingNumber);

                            selectedIndexTipoResultadoLlamada = 0;

                            view.showDialogSeleccionarResultadoLlamada(
                                    new String[]{"Contactada", "No Contactada"});
                        } else {
                            Log.d("MainActivity", "LLAMADA FINALIZADA INVALIDA: " + incomingNumber);
                            view.showToast(
                                    "El número de celular/telefono no coincide con los numeros registrados de la guía. Número Registrado (" + uriPhoneCalled.getSchemeSpecificPart() + ") " +
                                            "Número Llamada (" + incomingNumber + ")");
                            uriPhoneCalled = null;
                            isButtonPhoneClicked = false;
                            callInitDate = null;
                            callEndDate = null;
                        }
                    } else {
                        Log.d("MainActivity", "LLAMADA FINALIZADA NO ACEPTADA: " + incomingNumber);
                    }
                    break;
            }
        }
    }

    private void saveRegistroLlamada(String idMotivo) {
        if (uriPhoneCalled != null && callInitDate != null && callEndDate != null) {
            GestionLlamada gestionLlamada = new GestionLlamada(
                    Preferences.getInstance().getString("idUsuario", ""),
                    ruta.getIdServicio(),
                    idMotivo,
                    uriPhoneCalled.getSchemeSpecificPart().replace(" ", ""),
                    String.valueOf(callInitDate.getTime()),
                    String.valueOf(callEndDate.getTime()),
                    ruta.getLineaNegocio(),
                    Data.Sync.PENDING
            );
            gestionLlamada.save();

            uriPhoneCalled = null;
            isButtonPhoneClicked = false;
            callInitDate = null;
            callEndDate = null;
        }
    }

    private void loadMotivosGestionLlamada() {
        if (dbMotivoGestionLlamada.size() == 0) {
            dbMotivoGestionLlamada = interactor.selectAllMotivos(
                    MotivoDescarga.Tipo.GESTION_CON_LLAMADA, ruta.getLineaNegocio());

            motivoGestionLlamadaItems = new String[dbMotivoGestionLlamada.size()];

            for (int i = 0; i < dbMotivoGestionLlamada.size(); i++) {
                motivoGestionLlamadaItems[i] = WordUtils.capitalize(
                        dbMotivoGestionLlamada.get(i).getDescripcion().toLowerCase());
            }
        }
    }

    private void saveMotivos(JSONArray data) throws JSONException {
        JSONObject jsonObject;
        for (int i = 0; i < data.length(); i++) {
            jsonObject = data.getJSONObject(i);
            MotivoDescarga motivo = new MotivoDescarga(
                    Preferences.getInstance().getString("idUsuario", ""),
                    MotivoDescarga.Tipo.GESTION_CON_LLAMADA,
                    jsonObject.getString("mot_id"),
                    jsonObject.getString("codigo"),
                    jsonObject.getString("descri"),
                    jsonObject.getString("linea")
            );
            motivo.save();
        }
    }

    /**
     * Broadcast
     *
     * {@link EntregaGEPresenter#sendOnDescargaFinalizadaReceiver()}
     */
    private final BroadcastReceiver descargaFinalizadaReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            view.finishActivity();
        }
    };

    /**
     * Broadcast
     *
     * {@link TelefonosGuiaBottomSheet#onBtnLlamarClick}
     */
    private final BroadcastReceiver callPhoneReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            onBtnCallPhoneClick(intent.getStringExtra("phone"));
        }
    };

}