package com.urbanoexpress.iridio.presenter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.VolleyError;
import com.urbanoexpress.iridio.AsyncTaskCoroutine;
import com.urbanoexpress.iridio.R;
import com.urbanoexpress.iridio.application.AndroidApplication;
import com.urbanoexpress.iridio.data.rest.ApiRest;
import com.urbanoexpress.iridio.model.entity.Data;
import com.urbanoexpress.iridio.model.entity.Pieza;
import com.urbanoexpress.iridio.model.entity.Ruta;
import com.urbanoexpress.iridio.model.entity.SecuenciaRuta;
import com.urbanoexpress.iridio.model.interactor.ConsideracionesImportantesRutaInteractor;
import com.urbanoexpress.iridio.model.interactor.RutaPendienteInteractor;
import com.urbanoexpress.iridio.model.interactor.callback.RequestCallback;
import com.urbanoexpress.iridio.model.util.ModelUtils;
import com.urbanoexpress.iridio.presenter.helpers.ActionMenuRutaPendienteHelper;
import com.urbanoexpress.iridio.ui.DetalleRutaRuralActivity;
import com.urbanoexpress.iridio.ui.RutaRuralActivity;
import com.urbanoexpress.iridio.ui.adapter.RutaAdapter;
import com.urbanoexpress.iridio.ui.model.RutaItem;
import com.urbanoexpress.iridio.util.CommonUtils;
import com.urbanoexpress.iridio.util.Preferences;
import com.urbanoexpress.iridio.util.Session;
import com.urbanoexpress.iridio.util.constant.LocalAction;
import com.urbanoexpress.iridio.view.BaseModalsView;
import com.urbanoexpress.iridio.view.RutaPendienteView;

import org.apache.commons.text.WordUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class RutaRuralPendientePresenter implements RutaAdapter.OnClickGuiaItemListener {

    private static final String TAG = RutaRuralPendientePresenter.class.getSimpleName();

    private RutaPendienteView view;
    private RutaPendienteInteractor interactor;

    private List<RutaItem> rutaItems;
    private List<Ruta> dbRuta;

    private ActionMenuRutaPendienteHelper actionMenuRutaPendienteHelper;

    private boolean isMostrarAlerta = false;

    private long totalGuias = 0;

    public RutaRuralPendientePresenter(RutaPendienteView view) {
        this.view = view;
        interactor = new RutaPendienteInteractor(view.getViewContext());
        actionMenuRutaPendienteHelper = new ActionMenuRutaPendienteHelper(view);
    }

    public void init() {
        LocalBroadcastManager.getInstance(view.getViewContext())
                .registerReceiver(descargaFinalizadaReceiver, new IntentFilter("OnDescargaFinalizada"));
        LocalBroadcastManager.getInstance(view.getViewContext())
                .registerReceiver(manifiestoEliminadoReceiver, new IntentFilter("OnManifiestoEliminado"));
        LocalBroadcastManager.getInstance(view.getViewContext())
                .registerReceiver(transferenciaGuiaFinalizadaReceiver,
                        new IntentFilter(LocalAction.TRANSFERENCIA_GUIA_FINALIZADA_ACTION));
        LocalBroadcastManager.getInstance(view.getViewContext())
                .registerReceiver(buscarGuiaReceiver,
                        new IntentFilter(LocalAction.BUSCAR_GUIA_ACTION));

        new ShowGuiasTask(false, true).execute();
    }

    @Override
    public void onClickGuiaItem(View view, int position) {
        if (this.view.isActiveActionMode()) {
            onClickGuiaIconLinea(view, position);
        } else {
            try {
                AppCompatActivity activity = (AppCompatActivity) this.view.getViewContext();
                Bundle args = new Bundle();
                args.putSerializable("guias", dbRuta.get(position));
                args.putInt("numVecesGestionado", 1);
                activity.startActivity(new Intent(activity, DetalleRutaRuralActivity.class).putExtra("args", args));
                activity.overridePendingTransition(R.anim.slide_enter_from_right, R.anim.not_slide);
            } catch (IndexOutOfBoundsException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void onClickGuiaIconLinea(View v, int position) {
        if (!this.view.isRefreshingSwipeRefreshLayout()) {
            actionMenuRutaPendienteHelper.selectItem(position);
        }
    }

    @Override
    public void onClickGuiaIconImporte(View v, int position) {
        view.showSnackBar("Importe por cobrar: " + ModelUtils.getSimboloMoneda(view.getViewContext()) +
                " " + dbRuta.get(position).getImporte());
    }

    @Override
    public void onClickGuiaIconTipoEnvio(View v, int position) {
        if (dbRuta.get(position).getGuiaRequerimiento() != null) {
            if (dbRuta.get(position).getGuiaRequerimiento().equals("1")) {
                if (dbRuta.get(position).getGuiaRequerimientoCHK() != null) {
                    if (dbRuta.get(position).getGuiaRequerimientoCHK().equals("22")) {
                        String comentarios = dbRuta.get(position).getGuiaRequerimientoComentario().trim().toLowerCase();

                        if (comentarios.isEmpty()) {
                            comentarios = "ninguno";
                        }

                        String message = "La guía tiene un requerimiento con los siguientes datos:" +
                                "\n\nComentarios: " + comentarios +
                                "\n\nHorario de entrega: " + dbRuta.get(position).getGuiaRequerimientoHorario().trim().toLowerCase();

                        if (Integer.parseInt(dbRuta.get(position).getGuiaRequerimientoNuevaDireccion()) == 1) {
                            message += "\n\nNueva dirección: " + dbRuta.get(position).getDireccion();
                        }

                        BaseModalsView.showAlertDialog(this.view.getViewContext(),
                                dbRuta.get(position).getGuiaRequerimientoMotivo().trim(), message,
                                view.getViewContext().getString(R.string.text_aceptar), null);
                    } else if (dbRuta.get(position).getGuiaRequerimientoCHK().equals("30")) {
                        String message = "Esta guía por motivo de " +
                                dbRuta.get(position).getGuiaRequerimientoMotivo().toUpperCase() + ", debe ser devuelta al shipper.";

                        BaseModalsView.showAlertDialog(this.view.getViewContext(),
                                "Devolución al Shipper", message,
                                view.getViewContext().getString(R.string.text_aceptar), null);
                    }
                } else {
                    // Icono a modo de compatibilidad a versiones de iridio que no soportan requirimiento chk.
                    String comentarios = dbRuta.get(position).getGuiaRequerimientoComentario().trim().toLowerCase();

                    if (comentarios.isEmpty()) {
                        comentarios = "ninguno";
                    }

                    String message = "La guía tiene un requerimiento con los siguientes datos:" +
                            "\n\nComentarios: " + comentarios +
                            "\n\nHorario de entrega: " + dbRuta.get(position).getGuiaRequerimientoHorario().trim().toLowerCase();

                    if (Integer.parseInt(dbRuta.get(position).getGuiaRequerimientoNuevaDireccion()) == 1) {
                        message += "\n\nNueva dirección: " + dbRuta.get(position).getDireccion();
                    }

                    BaseModalsView.showAlertDialog(this.view.getViewContext(),
                            dbRuta.get(position).getGuiaRequerimientoMotivo().trim(), message,
                            view.getViewContext().getString(R.string.text_aceptar), null);
                }
            }
        }
    }

    public void onSwipeRefresh() {
        if (CommonUtils.validateConnectivity(view.getViewContext())) {
            getRutas();
        } else {
            view.setVisibilitySwipeRefreshLayout(false);
        }
    }

    public void onClickHomeButtonOnSelectedItems() {
        actionMenuRutaPendienteHelper.deselectAllItems();
    }

    public void onActionGestionMultiple() {
        actionMenuRutaPendienteHelper.gestionMultiple();
    }

    public void onActionSelectAllRutasPendientes() {
        actionMenuRutaPendienteHelper.selectAllItems();
    }

    public void onActionOrdenarGuias() {
        actionMenuRutaPendienteHelper.ordenarGuias();
    }

    public void onActionDefinirPosicionGuia() {
        actionMenuRutaPendienteHelper.definirPosicionGuia();
    }

    public void onActionGuardarOrdenGuias() {
        actionMenuRutaPendienteHelper.guardarOrdenGuias();
    }

    public void onDestroyActivity() {
        LocalBroadcastManager.getInstance(view.getViewContext())
                .unregisterReceiver(descargaFinalizadaReceiver);
        LocalBroadcastManager.getInstance(view.getViewContext())
                .unregisterReceiver(manifiestoEliminadoReceiver);
        LocalBroadcastManager.getInstance(view.getViewContext())
                .unregisterReceiver(transferenciaGuiaFinalizadaReceiver);
        LocalBroadcastManager.getInstance(view.getViewContext())
                .unregisterReceiver(buscarGuiaReceiver);
    }

    private void getRutas() {
        view.setVisibilitySwipeRefreshLayout(true);
        RequestCallback callback = new RequestCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                new SaveGuiasTask().execute(response);
            }

            @Override
            public void onError(VolleyError error) {
                view.setVisibilitySwipeRefreshLayout(false);
                view.showSnackBar(R.string.volley_error_message);
            }
        };

        String[] params = new String[] {
                Preferences.getInstance().getString("lineaLogistica", ""),
                Preferences.getInstance().getString("idUsuario", ""),
                Session.getUser().getDevicePhone()
        };

        interactor.getGuiasRutaRural(params, callback);
    }

    private class SaveGuiasTask extends AsyncTaskCoroutine<JSONObject, String> {

        private JSONObject response;
        private String msgErrorData = "";
        private String msgErrorJSON = "";

        @Override
        public String doInBackground(JSONObject... jsonObjects) {
            response = jsonObjects[0];

            try {
                if (response.getBoolean("success")) {
                    saveGuias(response.getJSONArray("data"));
                } else {
                    msgErrorData = response.getString("msg_error");
                }
            } catch (JSONException ex) {
                ex.printStackTrace();
                msgErrorJSON = AndroidApplication.getAppContext().getString(R.string.json_object_exception);
            }

            return null;
        }

        @Override
        public void onPostExecute(String s) {
            super.onPostExecute(s);
            if (!msgErrorData.isEmpty()) {
                view.showToast(msgErrorData);
            }

            if (!msgErrorJSON.isEmpty()) {
                view.showSnackBar(R.string.json_object_exception);
            }

            new ShowGuiasTask(true, false).execute();
        }
    }

    private void saveGuias(JSONArray data) throws JSONException {
        totalGuias = interactor.getTotalGuiasLogistica();

        if (data.length() > 0) {
            for (int i=0; i < data.length(); i++) {
                JSONObject jsonGuia = data.getJSONObject(i);

                Log.d(TAG, "ID SERVICIO: " + jsonGuia.getString("id_servicio") + " "
                        + jsonGuia.getString("guia"));

                Ruta guia = interactor.selectRuta(jsonGuia.getString("id_servicio"),
                        jsonGuia.getString("linea_negocio"));

                if (guia != null) {
                    Log.d(TAG, "EXISTE GUIA");
                    updateGuia(guia, jsonGuia);
                } else {
                    Log.d(TAG, "NO EXISTE GUIA");
                    saveGuia(jsonGuia);
                }
            }

            SecuenciaRuta secuenciaRuta = new SecuenciaRuta(
                    Preferences.getInstance().getString("idUsuario", ""), Data.Delete.NO);
            secuenciaRuta.save();
        }
    }

    private void saveGuia(JSONObject jsonRuta) throws JSONException {
        Ruta ruta = new Ruta(
                Preferences.getInstance().getString("idUsuario", ""),
                jsonRuta.getString("id_servicio"),
                jsonRuta.getString("man_id_det_rec"),
                jsonRuta.getString("mot_id"),
                jsonRuta.getString("id_agencia"),
                jsonRuta.getString("zon_id"),
                jsonRuta.getString("ruta_id"),
                jsonRuta.getString("guia_id"),
                jsonRuta.getString("id_medio_pago"),
                jsonRuta.getString("id_cliente"),
                jsonRuta.getString("id_manifiesto"),
                jsonRuta.getString("linea_negocio"),
                jsonRuta.getString("shi_codigo"),
                jsonRuta.getString("fec_ruta"),
                Ruta.ZONA.RURAL,
                jsonRuta.getString("guia"),
                jsonRuta.getString("tipo"),
                String.valueOf(++totalGuias),
                WordUtils.capitalize(jsonRuta.getString("direccion").toLowerCase()),
                jsonRuta.getString("geo_px"),
                jsonRuta.getString("geo_py"),
                jsonRuta.getString("radio_gps"),
                WordUtils.capitalize(jsonRuta.getString("distrito").toLowerCase()),
                WordUtils.capitalize(jsonRuta.getString("shipper").toLowerCase()),
                "",
                jsonRuta.getString("estado_shipper").toLowerCase(),
                WordUtils.capitalize(jsonRuta.getString("contacto").toLowerCase()),
                jsonRuta.getString("piezas"),
                jsonRuta.getString("horario"),
                0L,
                Instant.now().toEpochMilli(),
                jsonRuta.getString("tel_contacto"),
                jsonRuta.getString("nom_contacto"),
                jsonRuta.getString("telefono"),
                jsonRuta.getString("celular"),
                jsonRuta.getString("medio_pago"),
                jsonRuta.getString("importe"),
                jsonRuta.getString("tipo_envio"),
                jsonRuta.getString("anotaciones"),
                jsonRuta.getString("servicio_sms"),
                jsonRuta.getString("habilitantes"),
                jsonRuta.getString("chk_id_ult_entrega"),
                jsonRuta.getString("px_ult_entrega"),
                jsonRuta.getString("py_ult_entrega"),
                jsonRuta.getString("solicita_km"),
                jsonRuta.getString("flag_receptor"),
                jsonRuta.getString("flag_gestion"),
                jsonRuta.getString("chk_id_gestion"),
                WordUtils.capitalize(jsonRuta.getString("ges_motivo").toLowerCase()),
                jsonRuta.getString("ges_comentario").toLowerCase(),
                jsonRuta.getString("ges_arco_horario"),
                jsonRuta.getString("flag_direccion"),
                jsonRuta.getString("premios"),
                jsonRuta.getString("flag_firma"),
                jsonRuta.getString("cant_fotos"),
                jsonRuta.getString("descripcion"),
                jsonRuta.getString("observacion"),
                jsonRuta.getString("secuencia_ruteo"),
                Integer.parseInt(jsonRuta.getString("flag_alerta")),
                Ruta.EstadoDescarga.PENDIENTE,
                Ruta.ResultadoGestion.NO_DEFINIDO,
                Data.Delete.NO,
                Data.Validate.VALID
        );

        if (jsonRuta.has("pck")) {
            JSONArray pcks = jsonRuta.getJSONArray("pck");

            for (int i = 0; i < pcks.length(); i++) {
                JSONObject pck = pcks.getJSONObject(i);
                Pieza pieza = new Pieza(
                        Preferences.getInstance().getString("idUsuario", ""),
                        jsonRuta.getString("id_servicio"),
                        pck.getString("pck_numero"),
                        pck.getString("pck_barra"),
                        pck.getString("pck_chk_id"),
                        pck.getString("pck_estado"),
                        pck.getString("pck_fecha"),
                        jsonRuta.getString("linea_negocio"),
                        pck.getString("pck_ruta").equalsIgnoreCase("s") ? 1 : 0
                );
                pieza.save();
            }
        }

        ruta.save();
    }

    private void updateGuia(Ruta guia, JSONObject jsonGuia) throws JSONException {
        guia.setIdServicioRecoleccion(jsonGuia.getString("man_id_det_rec"));
        guia.setIdMotivo(jsonGuia.getString("mot_id"));
        guia.setIdAgencia(jsonGuia.getString("id_agencia"));
        guia.setIdZona(jsonGuia.getString("zon_id"));
        guia.setIdRuta(jsonGuia.getString("ruta_id"));
        guia.setIdGuia(jsonGuia.getString("guia_id"));
        guia.setIdMedioPago(jsonGuia.getString("id_medio_pago"));
        guia.setIdCliente(jsonGuia.getString("id_cliente"));
        guia.setIdManifiesto(jsonGuia.getString("id_manifiesto"));
        guia.setLineaNegocio(jsonGuia.getString("linea_negocio"));
        guia.setShiCodigo(jsonGuia.getString("shi_codigo"));
        guia.setFechaRuta(jsonGuia.getString("fec_ruta"));
        guia.setGuia(jsonGuia.getString("guia"));
        guia.setTipo(jsonGuia.getString("tipo"));
        guia.setDireccion(WordUtils.capitalize(jsonGuia.getString("direccion").toLowerCase()));
        guia.setGpsLatitude(jsonGuia.getString("geo_px"));
        guia.setGpsLongitude(jsonGuia.getString("geo_py"));
        guia.setGpsRadio(jsonGuia.getString("radio_gps"));
        guia.setDistrito(WordUtils.capitalize(jsonGuia.getString("distrito").toLowerCase()));
        guia.setShipper(WordUtils.capitalize(jsonGuia.getString("shipper").toLowerCase()));
        guia.setEstadoShipper(jsonGuia.getString("estado_shipper").toLowerCase());
        guia.setContacto(WordUtils.capitalize(jsonGuia.getString("contacto").toLowerCase()));
        guia.setPiezas(jsonGuia.getString("piezas"));
        guia.setHorarioEntrega(jsonGuia.getString("horario"));
        guia.setTelContactoGestion(jsonGuia.getString("tel_contacto"));
        guia.setNombreTelContactoGestion(jsonGuia.getString("nom_contacto"));
        guia.setTelefono(jsonGuia.getString("telefono"));
        guia.setCelular(jsonGuia.getString("celular"));
        guia.setMedioPago(jsonGuia.getString("medio_pago"));
        guia.setImporte(jsonGuia.getString("importe"));
        guia.setTipoEnvio(jsonGuia.getString("tipo_envio"));
        guia.setAnotaciones(jsonGuia.getString("anotaciones"));
        guia.setServicio_sms(jsonGuia.getString("servicio_sms"));
        guia.setHabilitantes(jsonGuia.getString("habilitantes"));
        guia.setIdCHKUltimaGestion(jsonGuia.getString("chk_id_ult_entrega"));
        guia.setGpsLatitudeUltimaGestion(jsonGuia.getString("px_ult_entrega"));
        guia.setGpsLongitudeUltimaGestion(jsonGuia.getString("py_ult_entrega"));
        guia.setSolicitaKilometraje(jsonGuia.getString("solicita_km"));
        guia.setRequiereDatosCliente(jsonGuia.getString("flag_receptor"));
        guia.setGuiaRequerimiento(jsonGuia.getString("flag_gestion"));
        guia.setGuiaRequerimientoCHK(jsonGuia.getString("chk_id_gestion"));
        guia.setGuiaRequerimientoMotivo(WordUtils.capitalize(jsonGuia.getString("ges_motivo").toLowerCase()));
        guia.setGuiaRequerimientoComentario(jsonGuia.getString("ges_comentario").toLowerCase());
        guia.setGuiaRequerimientoHorario(jsonGuia.getString("ges_arco_horario"));
        guia.setGuiaRequerimientoNuevaDireccion(jsonGuia.getString("flag_direccion"));
        guia.setPremiosGestionGuia(jsonGuia.getString("premios"));
        guia.setFirmaClienteGestionGuia(jsonGuia.getString("flag_firma"));
        guia.setMinimoFotosProductoGestionGuia(jsonGuia.getString("cant_fotos"));
        guia.setDescripcion(jsonGuia.getString("descripcion"));
        guia.setObservaciones(jsonGuia.getString("observacion"));
        guia.setSecuenciaRuteo(jsonGuia.getString("secuencia_ruteo"));
        guia.setMostrarAlerta(Integer.parseInt(jsonGuia.getString("flag_alerta")));
        guia.save();

        if (jsonGuia.has("pck")) {
            JSONArray pcks = jsonGuia.getJSONArray("pck");

            for (int i = 0; i < pcks.length(); i++) {
                JSONObject jsonPCK = pcks.getJSONObject(i);
                Pieza pieza = interactor.selectPiezaByBarra(jsonPCK.getString("pck_barra"));

                if (pieza != null) {
                    pieza.setChkEstado(jsonPCK.getString("pck_chk_id"));
                    pieza.setDescripcionEstado(jsonPCK.getString("pck_estado"));
                    pieza.setFechaEstado(jsonPCK.getString("pck_fecha"));
                    pieza.setEstadoManifiesto(jsonPCK.getString("pck_ruta")
                            .equalsIgnoreCase("s") ? 1 : 0);
                    pieza.save();
                }
            }
        }
    }

    private void removeRutaOnDescargaFinalizada(ArrayList<Ruta> rutas) {
        for (int i = 0; i < rutas.size(); i++) {
            for (int j = 0; j < dbRuta.size(); j++) {
                if (rutas.get(i).getIdServicio().equals(dbRuta.get(j).getIdServicio()) &&
                        rutas.get(i).getLineaNegocio().equals(dbRuta.get(j).getLineaNegocio())) {
                    rutaItems.remove(j);
                    dbRuta.remove(j);
                    view.notifyItemRemove(j);
                }
            }
        }
    }

    private class ShowGuiasTask extends AsyncTaskCoroutine<String, Boolean> {

        private boolean showMsgDialogNoHayRutaPendiente;

        private boolean getGuiasAfterLoad;

        public ShowGuiasTask(boolean showMsgDialogNoHayRutaPendiente, boolean getGuiasAfterLoad) {
            this.showMsgDialogNoHayRutaPendiente = showMsgDialogNoHayRutaPendiente;
            this.getGuiasAfterLoad = getGuiasAfterLoad;
        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            view.setVisibilitySwipeRefreshLayout(true);
        }

        public Boolean doInBackground(String... urls) {
            dbRuta = interactor.selectRutasPendientes();

            isMostrarAlerta = ConsideracionesImportantesRutaInteractor.isMostrarAlerta();

            rutaItems = new ArrayList<>();

            if (dbRuta.size() > 0) {
                for (int i=0; i < dbRuta.size(); i++) {
                    int resIcon = ModelUtils.getIconTipoGuia(dbRuta.get(i));

                    int idResIconTipoEnvio = ModelUtils.getIconTipoEnvio(dbRuta.get(i));

                    int backgroundColorGuia = ModelUtils.getBackgroundColorGE(
                            dbRuta.get(i).getTipoEnvio(), view.getViewContext());

                    String horario = dbRuta.get(i).getHorarioEntrega();

                    if (ModelUtils.isGuiaEntrega(dbRuta.get(i).getTipo())) {
                        horario = CommonUtils.parseDateToElapsedDays(dbRuta.get(i).getFechaRuta());
                    }

                    int lblColorHorario = ModelUtils.getLblColorHorario(dbRuta.get(i).getTipo(),
                            dbRuta.get(i).getTipoEnvio(), dbRuta.get(i).getFechaRuta(),
                            dbRuta.get(i).getHorarioEntrega());

                    StringBuilder guia = new StringBuilder(dbRuta.get(i).getGuia());
                    if (ModelUtils.isGuiaRecoleccion(dbRuta.get(i).getTipo())) {
                        guia = new StringBuilder(dbRuta.get(i).getShipper());
                        guia.append(" (").append(dbRuta.get(i).getGuia()).append(")");
                    }

                    try {
                        if (dbRuta.get(i).getSecuenciaRuteo() != null &&
                                Integer.parseInt(dbRuta.get(i).getSecuenciaRuteo()) > 0) {
                            guia.append(" (").append(dbRuta.get(i).getSecuenciaRuteo()).append(")");
                        }
                    } catch (NumberFormatException ex) {
                        ex.printStackTrace();
                    }

                    String simboloMoneda = ModelUtils.getSimboloMoneda(view.getViewContext());

                    RutaItem rutaItem = new RutaItem(
                            dbRuta.get(i).getIdServicio(),
                            dbRuta.get(i).getIdManifiesto(),
                            guia.toString(),
                            dbRuta.get(i).getDistrito(),
                            dbRuta.get(i).getDireccion(),
                            horario,
                            dbRuta.get(i).getPiezas(),
                            String.valueOf(i + 1),
                            simboloMoneda,
                            resIcon,
                            resIcon,
                            idResIconTipoEnvio,
                            backgroundColorGuia,
                            lblColorHorario,
                            dbRuta.get(i).getResultadoGestion(),
                            false,
                            true,
                            ModelUtils.isTipoEnvioValija(dbRuta.get(i).getTipoEnvio()),
                            ModelUtils.isShowIconImportePorCobrar(dbRuta.get(i).getImporte()),
                            false
                    );
                    rutaItems.add(rutaItem);
                    /*dbRuta.get(i).setSecuencia(String.valueOf(i + 1));
                    dbRuta.get(i).save();*/
                }

                actionMenuRutaPendienteHelper.setDbRuta(dbRuta);
                actionMenuRutaPendienteHelper.setRutaItems(rutaItems);
            }
            return true;
        }

        public void onPostExecute(Boolean result) {
            showAlerta();
            view.showDatosRutasPendientes(rutaItems);

            setTitleTabPendientes();

            if (showMsgDialogNoHayRutaPendiente && rutaItems.size() == 0) {
                view.showToast(R.string.fragment_ruta_pendiente_message_no_hay_rutas);
            }

            if (getGuiasAfterLoad && rutaItems.size() == 0) {
                if (view.getViewContext() != null &&
                        CommonUtils.validateConnectivity(view.getViewContext())) {
                    Log.d(TAG, "INIT GET GUIAS");
                    getRutas();
                } else {
                    view.setVisibilitySwipeRefreshLayout(false);
                }
            } else {
                view.setVisibilitySwipeRefreshLayout(false);
            }
        }
    }

    private void setTitleTabPendientes() {
        try {
            if (rutaItems.size() > 0) {
                ((RutaRuralActivity) view.getViewContext()).setTitleTabPendientes(
                        String.format("Pendientes (%s)", rutaItems.size()));
            }
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    private void showAlerta() {
        try {
            if (isMostrarAlerta) {
                ((RutaRuralActivity) view.getViewContext()).setVisibilityBoxConsideracionesImportantesRuta(View.VISIBLE);
            } else {
                ((RutaRuralActivity) view.getViewContext()).setVisibilityBoxConsideracionesImportantesRuta(View.GONE);
            }
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Broadcast
     *
     * {@link EntregaGEPresenter#sendOnDescargaFinalizadaReceiver}
     * {@link NoEntregaGEPresenter#sendOnDescargaFinalizadaReceiver}
     * {@link RecolectaGEPresenter#sendOnDescargaFinalizadaReceiver}
     * {@link NoRecolectaGEPresenter#sendOnDescargaFinalizadaReceiver}
     */
    private BroadcastReceiver descargaFinalizadaReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<Ruta> rutas = (ArrayList<Ruta>)
                    intent.getExtras().getBundle("args").getSerializable("guias");

            int msg = R.string.activity_detalle_ruta_message_descarga_finalizado_exitosamente;
            if (rutas.get(0).getTipo().equalsIgnoreCase("R")) {
                msg = R.string.activity_detalle_ruta_message_recoleccion_finalizado_exitosamente;
            }

            isMostrarAlerta = ConsideracionesImportantesRutaInteractor.isMostrarAlerta();
            showAlerta();

            removeRutaOnDescargaFinalizada(rutas);
            setTitleTabPendientes();
            view.showToast(msg);
        }
    };

    /**
     * Broadcast
     *
     * {@link RutaPresenter#sendOnManifiestoEliminadoReceiver}
     */
    private BroadcastReceiver manifiestoEliminadoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String idManifiesto = intent.getStringExtra("idManifiesto");

            for (int i = 0; i < rutaItems.size(); i++) {
                if (rutaItems.get(i).getIdManifiesto().equals(idManifiesto)) {
                    rutaItems.remove(i);
                    dbRuta.remove(i);
                    i -= 1;
                }
            }

            view.notifyAllItemChanged();
            setTitleTabPendientes();

            isMostrarAlerta = ConsideracionesImportantesRutaInteractor.isMostrarAlerta();
            showAlerta();
        }
    };

    /**
     * Broadcast
     *
     * {@link TransferirGuiaDialog#sendTransferenciaGuiaFinalizadaAction}
     */
    private BroadcastReceiver transferenciaGuiaFinalizadaReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String[] guias = (String[]) intent.getSerializableExtra("guias");
            String lineaNegocio = intent.getStringExtra("lineaNegocio");

            //Log.d(TAG, "TOTAL GUIAS: " + guias.length);

            for (String guia : guias) {
                //Log.d(TAG, "MAN_ID_DET: " + guias[i]);

                rutaItems.removeIf(rutaItem -> rutaItem.getIdServicio().equals(guia));
                dbRuta.removeIf(ruta -> ruta.getIdServicio().equals(guia));

                /*for (int j = 0; j < dbRuta.size(); j++) {
                    if (dbRuta.get(j).getIdServicio().equals(guia) &&
                            dbRuta.get(j).getLineaNegocio().equals(lineaNegocio)) {
                        //Log.d(TAG, "GUIA ELIMINADA: " + rutaItems.get(j).getGuia());
                        rutaItems.remove(j);
                        dbRuta.remove(j);
                    }
                }*/
            }
            view.notifyAllItemChanged();
            setTitleTabPendientes();

            isMostrarAlerta = ConsideracionesImportantesRutaInteractor.isMostrarAlerta();
            showAlerta();
        }
    };

    /**
     * Broadcast
     *
     * {@link RutaPresenter#resultScannReceiver}
     */
    private BroadcastReceiver buscarGuiaReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, final Intent intent) {
            if (intent.getIntExtra("tipoBusqueda", 0) == 0) {
                String barra = intent.getStringExtra("value").trim();
                Pieza pieza = interactor.selectPiezaByBarra(barra);

                if (pieza != null) {
                    for (int i = 0; i < dbRuta.size(); i++) {
                        if (dbRuta.get(i).getIdServicio().equals(pieza.getIdServicioGuia())) {
                            onClickGuiaItem(null, i);
                        }
                    }
                } else {
                    for (int i = 0; i < dbRuta.size(); i++) {
                        if (dbRuta.get(i).getGuia().equalsIgnoreCase(barra)) {
                            onClickGuiaItem(null, i);
                        }
                    }
                }
            }
        }
    };

}