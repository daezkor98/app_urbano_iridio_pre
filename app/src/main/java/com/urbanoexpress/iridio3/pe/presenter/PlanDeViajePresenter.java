package com.urbanoexpress.iridio3.pe.presenter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;

import com.android.volley.VolleyError;
import com.orm.util.NamingHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.urbanoexpress.iridio3.pe.R;
import com.urbanoexpress.iridio3.pe.model.entity.Data;
import com.urbanoexpress.iridio3.pe.model.entity.Despacho;
import com.urbanoexpress.iridio3.pe.model.entity.Imagen;
import com.urbanoexpress.iridio3.pe.model.entity.IncidenteRuta;
import com.urbanoexpress.iridio3.pe.model.entity.ParadaProgramada;
import com.urbanoexpress.iridio3.pe.model.entity.PlanDeViaje;
import com.urbanoexpress.iridio3.pe.model.interactor.PlanDeViajeInteractor;
import com.urbanoexpress.iridio3.pe.model.interactor.callback.RequestCallback;
import com.urbanoexpress.iridio3.pe.presenter.helpers.ForzarCierreRutaHelper;
import com.urbanoexpress.iridio3.pe.ui.dialogs.InfoParadaProgramadaBottomSheetDialog;
import com.urbanoexpress.iridio3.pe.ui.dialogs.IniciarTerminarRutaPlanDeViajeDialog;
import com.urbanoexpress.iridio3.pe.ui.model.ParadaProgramadaItem;
import com.urbanoexpress.iridio3.pe.util.CommonUtils;
import com.urbanoexpress.iridio3.pe.util.InfoDevice;
import com.urbanoexpress.iridio3.pe.util.MyLocation;
import com.urbanoexpress.iridio3.pe.util.Preferences;
import com.urbanoexpress.iridio3.pe.util.constant.LocalAction;
import com.urbanoexpress.iridio3.pe.view.PlanDeViajeView;

/**
 * Created by mick on 30/05/16.
 */
public class PlanDeViajePresenter {

    private final String TAG = PlanDeViajePresenter.class.getSimpleName();
    private PlanDeViajeView view;
    private PlanDeViajeInteractor planDeViajeInteractor;

    private List<PlanDeViaje> dbPlanDeViaje;
    private List<ParadaProgramada> dbParadaProgramada;

    private JSONArray jsonArrayRutas;
    private JSONArray jsonArrayPlanViaje;

    public PlanDeViajePresenter(PlanDeViajeView view) {
        this.view = view;
        this.planDeViajeInteractor = new PlanDeViajeInteractor(view.getViewContext());
    }

    public void init() {
        if (InfoDevice.isActiveGPS(view.getViewContext())) {
            view.setVisibilitySwipeRefreshLayout(true);
            getPlanViaje();
        } else {
            view.showNoDatosPlanDeViaje();
        }

        LocalBroadcastManager.getInstance(view.getViewContext())
                .registerReceiver(forzarCierreRutaReceiver,
                        new IntentFilter(LocalAction.FORZAR_CIERRE_RUTA_ACTION));

        LocalBroadcastManager.getInstance(view.getViewContext())
                .registerReceiver(iniciarTerminarPlanDeViajeReceiver,
                        new IntentFilter(LocalAction.INICIAR_TERMINAR_PLAN_DE_VIAJE_ACTION));

        LocalBroadcastManager.getInstance(view.getViewContext())
                .registerReceiver(llegadaSalidaParadaProgramadaReceiver,
                        new IntentFilter(LocalAction.LLEGADA_SALIDA_PARADA_PROGRAMADA_ACTION));
    }

    public void getPlanViaje() {
        InfoDevice.isGPSEnabled(view.getViewContext(), true);

        dbPlanDeViaje = planDeViajeInteractor.selectAllPlanViaje();

        if (CommonUtils.validateConnectivity(view.getViewContext())) {
            if (existPlanDeViaje()) {
                if (isIniciadoRuta()) {
                    getPlanDeViaje();
                } else {
                    validatePlanViajeActivos();
                }
            } else {
                validatePlanViajeActivos();
            }
        } else {
            loadPlanDeViaje();
            loadParadasProgramadas();
            enableButtonsPlanViaje();
            setLabelImagenesSincronizadas();
            setLabelIncidentesSincronizados();
            view.setVisibilitySwipeRefreshLayout(false);
        }
    }

    public void onSelectedOrigenPlanViaje(String idPlanViaje) {
        try {
            JSONObject jsonPlanViaje = null;
            for (int i = 0; i < jsonArrayPlanViaje.length(); i++) {
                Log.d(TAG, "ID PLAN VIAJE: " + jsonArrayPlanViaje.getJSONObject(i).get("id_plan_viaje"));
                Log.d(TAG, "PASS ID PLAN VIAJE: " + idPlanViaje);
                if (jsonArrayPlanViaje.getJSONObject(i)
                        .get("id_plan_viaje").equals(idPlanViaje)) {
                    jsonPlanViaje = jsonArrayPlanViaje.getJSONObject(i);
                }
            }

            if (jsonPlanViaje != null) {
                savePlanDeViaje(jsonPlanViaje);
                saveParadasProgramadas(jsonPlanViaje);
            }

            loadPlanDeViaje();
            loadParadasProgramadas();
            enableButtonsPlanViaje();
            setLabelImagenesSincronizadas();
            setLabelIncidentesSincronizados();
            view.setVisibilitySwipeRefreshLayout(false);
        } catch (JSONException ex) {
            ex.printStackTrace();
            view.setVisibilitySwipeRefreshLayout(false);
            view.showToast(R.string.json_object_exception);
        }
    }

    public void onClickGuardarPlaca(String placa) {
        if (CommonUtils.validateConnectivity(view.getViewContext())) {
            actualizarPlaca(placa);
        }
    }

    public void onCLickCloseEditPlaca() {
        view.hideFormEditarPlaca();
    }

    public void onClickIniciarRuta() {
        view.navigateToIniciarTerminarRutaDialog(dbPlanDeViaje.get(0),
                (ArrayList<ParadaProgramada>) dbParadaProgramada);
    }

    public void onClickTerminarRuta() {
        view.navigateToIniciarTerminarRutaDialog(dbPlanDeViaje.get(0),
                (ArrayList<ParadaProgramada>) dbParadaProgramada);
    }

    public void onClickItemParadasProgaramadas(int position) {
        view.navigateToDetailParadaProgramadaDialog(dbPlanDeViaje.get(0),
                dbParadaProgramada.get(position));
    }

    public void onSwipeRefresh() {
        getPlanViaje();
        //mFusedLocationClient.requestLocationUpdates(mLocationRequest, getPendingIntent());
    }

    public void onClickBtnMapaParadas() {
        if (existPlanDeViaje()) {
            view.navigateToParadasOnMapActivity(dbPlanDeViaje.get(0),
                    (ArrayList<ParadaProgramada>) dbParadaProgramada,
                    dbPlanDeViaje.get(0).getOrigen_latitude(),
                    dbPlanDeViaje.get(0).getOrigen_longitude());
        } else {
            view.showToast(R.string.act_plan_de_viaje_no_hay_plan_de_viaje);
        }
    }

    public void onClickBtnAsignarDespachos() {
        if (existPlanDeViaje()) {
            if (isIniciadoRuta()) {
                view.showToast(R.string.act_plan_de_viaje_message_no_puede_asignar_despachos_pendientes);
            } else {
                view.navigateToAsignarDespachoDialog();
            }
        } else {
            view.showToast(R.string.act_plan_de_viaje_no_hay_plan_de_viaje);
        }
    }

    public void onClickBtnForzarCierreRuta() {
        if (existPlanDeViaje()) {
            if (isIniciadoRuta()) {
                new ForzarCierreRutaHelper(view.getViewContext(),
                        ForzarCierreRutaHelper.ActionContext.PLAN_DE_VIAJE).init();
            } else {
                view.showToast(R.string.act_plan_de_viaje_msg_forzar_cierre_ruta_no_inicia_ruta);
            }
        } else {
            view.showToast(R.string.act_plan_de_viaje_no_hay_plan_de_viaje);
        }
    }

    public void onDestroyActivity() {
        LocalBroadcastManager.getInstance(view.getViewContext())
                .unregisterReceiver(forzarCierreRutaReceiver);
        LocalBroadcastManager.getInstance(view.getViewContext())
                .unregisterReceiver(iniciarTerminarPlanDeViajeReceiver);
        LocalBroadcastManager.getInstance(view.getViewContext())
                .unregisterReceiver(llegadaSalidaParadaProgramadaReceiver);
    }

    private void getPlanDeViaje() {
        view.setVisibilitySwipeRefreshLayout(true);
        RequestCallback callback = new RequestCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if (response.getBoolean("success")) {
                        savePlanDeViaje(response.getJSONArray("data").getJSONObject(0));
                        saveParadasProgramadas(response.getJSONArray("data").getJSONObject(0));
                    } else {
                        view.showToast(response.getString("msg_error"));
                    }

                    loadPlanDeViaje();
                    loadParadasProgramadas();
                    enableButtonsPlanViaje();
                    setLabelImagenesSincronizadas();
                    setLabelIncidentesSincronizados();
                    view.setVisibilitySwipeRefreshLayout(false);
                } catch (JSONException ex) {
                    ex.printStackTrace();
                    view.setVisibilitySwipeRefreshLayout(false);
                    view.showToast(R.string.json_object_exception);
                }
            }

            @Override
            public void onError(VolleyError error) {
                view.setVisibilitySwipeRefreshLayout(false);
                view.showToast(R.string.volley_error_message);
            }
        };

        String[] params = new String[]{
                Preferences.getInstance().getString("idUsuario", ""),
                dbPlanDeViaje.get(0).getIdPlanViaje()
        };
        planDeViajeInteractor.getPlanDeViaje(params, callback);
    }

    private void validatePlanViajeActivos() {
        view.setVisibilitySwipeRefreshLayout(true);
        RequestCallback callback = new RequestCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if (response.getBoolean("success")) {
                        jsonArrayRutas = response.getJSONObject("data").getJSONArray("rutas");
                        jsonArrayPlanViaje = response.getJSONObject("data").getJSONArray("planViaje");
                        if (jsonArrayRutas.length() == 1) {
                            savePlanDeViaje(jsonArrayPlanViaje.getJSONObject(0));
                            saveParadasProgramadas(jsonArrayPlanViaje.getJSONObject(0));
                        } else {
                            try {
                                view.navigateToSelectOrigenDialog(buildOrigenPlanViaje(jsonArrayRutas, jsonArrayPlanViaje));
                            } catch (IllegalStateException ex) {
                                ex.printStackTrace();
                            }
                        }
                    } else {
                        view.showToast(response.getString("msg_error"));
                    }

                    loadPlanDeViaje();
                    loadParadasProgramadas();
                    enableButtonsPlanViaje();
                    setLabelImagenesSincronizadas();
                    setLabelIncidentesSincronizados();
                    view.setVisibilitySwipeRefreshLayout(false);
                } catch (JSONException ex) {
                    ex.printStackTrace();
                    view.setVisibilitySwipeRefreshLayout(false);
                    view.showToast(R.string.json_object_exception);
                }
            }

            @Override
            public void onError(VolleyError error) {
                error.printStackTrace();
                view.setVisibilitySwipeRefreshLayout(false);
                view.showToast(R.string.volley_error_message);
            }
        };

        String[] params = new String[]{
                Preferences.getInstance().getString("idUsuario", "")
        };
        planDeViajeInteractor.validatePlanViajeActivos(params, callback);
    }

    private void savePlanDeViaje(JSONObject jsonPlanViaje) throws JSONException {
        dbPlanDeViaje = planDeViajeInteractor.selectAllPlanViaje();

        PlanDeViaje planDeViaje;

        if (dbPlanDeViaje.size() > 0) {
            if (isNewPlanDeViaje(dbPlanDeViaje.get(0).getIdPlanViaje(),
                    jsonPlanViaje.getString("id_plan_viaje"))) {
                ForzarCierreRutaHelper.deleteAllDataParadaProgramada(view.getViewContext());
                dbPlanDeViaje.clear();
            }
        }

        if (dbPlanDeViaje.size() > 0) {
            planDeViaje = dbPlanDeViaje.get(0);
            planDeViaje.setIdUnidad(jsonPlanViaje.getString("unidad_id"));
            planDeViaje.setPlaca(jsonPlanViaje.getString("placa"));
            planDeViaje.setOrigen(jsonPlanViaje.getString("origen"));
            planDeViaje.setRuta(jsonPlanViaje.getString("ruta"));
            planDeViaje.setDespachos(jsonPlanViaje.getString("despachos"));
            planDeViaje.setGruias(jsonPlanViaje.getString("guias"));
            planDeViaje.setPiezas(jsonPlanViaje.getString("piezas"));
            planDeViaje.setPeso(jsonPlanViaje.getString("peso"));
            planDeViaje.setPeso_uso(jsonPlanViaje.getString("peso_uso"));
            planDeViaje.setVolumen(jsonPlanViaje.getString("volumen"));
            planDeViaje.setVolumen_uso(jsonPlanViaje.getString("vol_uso"));
            planDeViaje.setOrigen_latitude(jsonPlanViaje.getString("origen_px"));
            planDeViaje.setOrigen_longitude(jsonPlanViaje.getString("origen_py"));
            planDeViaje.setTotalKMRuta(jsonPlanViaje.getString("km"));
            planDeViaje.save();
        } else {
            planDeViaje = new PlanDeViaje(
                    jsonPlanViaje.getString("id_plan_viaje"),
                    Preferences.getInstance().getString("idUsuario", ""),
                    jsonPlanViaje.getString("unidad_id"),
                    jsonPlanViaje.getString("id_origen"),
                    jsonPlanViaje.getString("placa"),
                    jsonPlanViaje.getString("origen"),
                    jsonPlanViaje.getString("ruta"),
                    jsonPlanViaje.getString("despachos"),
                    jsonPlanViaje.getString("guias"),
                    jsonPlanViaje.getString("piezas"),
                    jsonPlanViaje.getString("peso"),
                    jsonPlanViaje.getString("peso_uso"),
                    jsonPlanViaje.getString("volumen"),
                    jsonPlanViaje.getString("vol_uso"),
                    jsonPlanViaje.getString("origen_px"),
                    jsonPlanViaje.getString("origen_py"),
                    "",
                    jsonPlanViaje.getString("km"),
                    PlanDeViaje.EstadoRuta.NO_INICIO_RUTA
            );
            planDeViaje.save();
        }
    }

    private void saveParadasProgramadas(JSONObject planViaje) throws JSONException {
        dbParadaProgramada = planDeViajeInteractor.selectAllParadaProgramada();

        JSONObject jsonObject;

        JSONArray paradas = planViaje.getJSONArray("paradas_programadas");

        if (dbParadaProgramada.size() > 0) {
            for (int i = 0; i < paradas.length(); i++) {
                jsonObject = paradas.getJSONObject(i);
                try {
                    updateParadaProgramada(jsonObject);
                } catch (NullPointerException ex) {
                    saveParadaProgramada(jsonObject);
                }
            }
        } else {
            for (int i = 0; i < paradas.length(); i++) {
                jsonObject = paradas.getJSONObject(i);
                saveParadaProgramada(jsonObject);
            }
        }
    }

    private void saveParadaProgramada(JSONObject json) throws JSONException {
        ParadaProgramada paradaProgramada = new ParadaProgramada(
                Preferences.getInstance().getString("idUsuario", ""),
                json.getString("id_stop"),
                json.getString("agencia"),
                json.getString("sigla"),
                json.getString("estimado"),
                json.getString("llegada"),
                json.getString("salida"),
                json.getString("age_px"),
                json.getString("age_py"),
                json.getString("tipo"),
                ParadaProgramada.Status.NO_LLEGO_AGENCIA,
                ParadaProgramada.Status.NO_REVISO_DESPACHOS
        );
        paradaProgramada.save();
    }

    private void updateParadaProgramada(JSONObject json) throws JSONException{
        ParadaProgramada paradaProgramada = getParadaProgramadaByID(json.getString("id_stop"));
        paradaProgramada.setIdStop(json.getString("id_stop"));
        paradaProgramada.setAgencia(json.getString("agencia"));
        paradaProgramada.setSiglaAgencia(json.getString("sigla"));
        paradaProgramada.setHoraEstimada(json.getString("estimado"));
        paradaProgramada.setHoraLlegada(json.getString("llegada"));
        paradaProgramada.setHoraSalida(json.getString("salida"));
        paradaProgramada.setAgencia_latitude(json.getString("age_px"));
        paradaProgramada.setAgencia_longitude(json.getString("age_py"));
        paradaProgramada.save();
    }

    private void loadPlanDeViaje() {
        dbPlanDeViaje = planDeViajeInteractor.selectAllPlanViaje();

        if (dbPlanDeViaje.size() > 0) {
            view.showDatosPlanDeViaje(dbPlanDeViaje.get(0));
            Log.d(TAG, "SIZE: " + dbPlanDeViaje.size() + "");
            Log.d(TAG, "ESTADO: " + dbPlanDeViaje.get(0).getEstadoRecorrido() + "");
        } else {
            view.showNoDatosPlanDeViaje();
        }
    }

    private void loadParadasProgramadas() {
        dbParadaProgramada = planDeViajeInteractor.selectAllParadaProgramada();

        List<ParadaProgramadaItem> paradasProgramadasItems = new ArrayList<>();

        if (dbParadaProgramada.size() > 0) {
            for (int i = 0; i < dbParadaProgramada.size(); i++) {
                paradasProgramadasItems.add(new ParadaProgramadaItem(
                        dbParadaProgramada.get(i).getSiglaAgencia() != null
                                ? dbParadaProgramada.get(i).getSiglaAgencia()
                                : dbParadaProgramada.get(i).getAgencia(),
                        dbParadaProgramada.get(i).getHoraEstimada(),
                        dbParadaProgramada.get(i).getHoraLlegada(),
                        dbParadaProgramada.get(i).getHoraSalida(),
                        getColorOfLabelHoraLlegada(dbParadaProgramada.get(i).getHoraEstimada(),
                                dbParadaProgramada.get(i).getHoraLlegada())
                ));
            }

            view.showDatosParadasProgramadas(paradasProgramadasItems);
        }
    }

    private void actualizarPlaca(final String placa) {
        if (placa.length() > 0) {
            view.showProgressDialog();
            RequestCallback callback = new RequestCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    view.dismissProgressDialog();
                    try {
                        if (response.getBoolean("success")) {
                            view.hideFormEditarPlaca();
                            view.clearFormEditarPlaca();
                            view.changeTextLabelPlaca(placa);
                            view.showToast(R.string.act_plan_de_viaje_update_plan_viaje);
                            getPlanDeViaje();
                        } else {
                            view.showToast(response.getString("msg_error"));
                        }
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                        view.showToast(R.string.json_object_exception);
                    }
                }

                @Override
                public void onError(VolleyError error) {
                    view.dismissProgressDialog();
                    view.showToast(R.string.volley_error_message);
                }
            };

            String[] params = new String[]{
                    placa,
                    getIDPlanDeViaje(),
                    Preferences.getInstance().getString("idUsuario", "")
            };

            planDeViajeInteractor.actualizarPlaca(params, callback);
        } else {
            view.showToast(R.string.act_plan_de_viaje_error_datos_placa);
        }
    }

    private boolean validateConfirmarLlegada(int position) {
        if (isIniciadoRuta()) {
            if (validateConfirmacionSalidaAnteriorParada(position)) {
                if (InfoDevice.isGPSEnabled(view.getViewContext(), true)) {
//                    if (validateArrivedToLocation(position)) {
                    return true;
//                    }
                }
            }
        } else {
            view.showToast(R.string.act_plan_de_viaje_message_no_inicio_ruta);
        }
        view.dismissProgressDialog();
        return false;
    }

    private boolean validateSalidaLlegada(int position) {
        if (validateRevisionDespachos(position)) {
            if (validateDespachosBajados(position)) {
                if (validateImagenesPorParada(position)) {
                    return true;
                }
            }
        }
        view.dismissProgressDialog();
        return false;
    }

    private boolean validateConfirmacionSalidaAnteriorParada(int position) {
        int posicionAnterior = --position;

        if (position < 0) { // Es la primera parada
            return true;
        }

        if (dbParadaProgramada.get(posicionAnterior).getEstadoLlegada()
                == ParadaProgramada.Status.SALIO_AGENCIA) {
            return true;
        }

        view.showToast(R.string.act_plan_de_viaje_message_no_confirmo_salida_parada_anterior);
        return false;
    }

    private boolean validateArrivedToLocation(int position) {
        boolean validateArrived = MyLocation.arrivedToLocation(view.getViewContext(),
                Double.parseDouble(dbParadaProgramada.get(position).getAgencia_latitude()),
                Double.parseDouble(dbParadaProgramada.get(position).getAgencia_longitude()));

        if (validateArrived) {
            Log.d(TAG, "LLEGO A LA AGENCIA!");
            return true;
        }

        Log.d(TAG, "NO LLEGO A LA AGENCIA!");
        view.showToast(R.string.act_plan_de_viaje_message_no_se_encuentra_en_la_ubicacion);
        return false;
    }

    private boolean validateDespachosBajados(int position) {
        String idParada = dbParadaProgramada.get(position).getIdStop();

        List<Despacho> despachosBajadas = planDeViajeInteractor.selectDespachoByIdParada(
                idParada, Despacho.Type.DESPACHO_BAJADA + "");

        if (despachosBajadas.size() == 0) { // No hay despachos por bajar
            return true;
        }

        for (int i = 0; i < despachosBajadas.size(); i++) {
            if (despachosBajadas.get(i).getProcesoDespacho() == Despacho.Status.DESPACHADO) {
                return true;
            }
        }

        view.showToast(R.string.act_plan_de_viaje_message_no_ha_bajado_despachos);
        return false;
    }

    private boolean validateRevisionDespachos(int position) {
        dbParadaProgramada = planDeViajeInteractor.selectAllParadaProgramada();
        if (dbParadaProgramada.get(position).getEstadoDespachosRevisado()
                == ParadaProgramada.Status.REVISO_DESPACHOS) {
            return true;
        }

        view.showToast(R.string.act_plan_de_viaje_message_no_reviso_despachos);
        return false;
    }

    private boolean validateImagenesPorParada(int positionParada) {
        long totalImagenes = Imagen.count(Imagen.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("clasificacion") + " = ? and " +
                        NamingHelper.toSQLNameDefault("idSuperior") + " = ?",
                new String[]{Preferences.getInstance().getString("idUsuario", ""),
                        Imagen.Tipo.PARADA_PROGRAMADA + "", dbParadaProgramada.get(positionParada).getIdStop()});

        if (totalImagenes >= 1) {
            return true;
        }

        view.showToast(R.string.act_plan_de_viaje_msg_tomar_al_menos_una_foto);
        return false;
    }

    private void setLabelImagenesSincronizadas() {
        String idParadaProgramada = "";

        for (int i = 0; i < dbParadaProgramada.size(); i++) {
            if (i == dbParadaProgramada.size() - 1) {
                idParadaProgramada += dbParadaProgramada.get(i).getIdStop();
            } else {
                idParadaProgramada += dbParadaProgramada.get(i).getIdStop() + ",";
            }
        }

        long totalImagenes = Imagen.count(Imagen.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("clasificacion") + " = ? and " +
                        NamingHelper.toSQLNameDefault("idSuperior") + " in (" + idParadaProgramada + ")",
                new String[]{Preferences.getInstance().getString("idUsuario", ""),
                        Imagen.Tipo.PARADA_PROGRAMADA + ""});

        long totalImagenesSync = Imagen.count(Imagen.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("clasificacion") + " = ? and " +
                        NamingHelper.toSQLNameDefault("idSuperior") + " in (" + idParadaProgramada + ") and " +
                        NamingHelper.toSQLNameDefault("dataSync") + " = ?",
                new String[]{Preferences.getInstance().getString("idUsuario", ""),
                        Imagen.Tipo.PARADA_PROGRAMADA + "", Data.Sync.SYNCHRONIZED + ""});

        view.setImagenesSincronizadas(totalImagenesSync + "/" + totalImagenes);
    }

    private void setLabelIncidentesSincronizados() {
        long totalIncidentes = IncidenteRuta.count(IncidenteRuta.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("tipoRuta") + " = ?",
                new String[]{Preferences.getInstance().getString("idUsuario", ""),
                        IncidenteRuta.TipoRuta.PLAN_DE_VIAJE + ""});

        long totalIncidentesSync = IncidenteRuta.count(IncidenteRuta.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("tipoRuta") + " = ? and " +
                        NamingHelper.toSQLNameDefault("dataSync") + " = ?",
                new String[]{Preferences.getInstance().getString("idUsuario", ""),
                        IncidenteRuta.TipoRuta.PLAN_DE_VIAJE + "",
                        Data.Sync.SYNCHRONIZED + ""});

        view.setIncidentesSincronizados(totalIncidentesSync + "/" + totalIncidentes);
    }

    private int getColorOfLabelHoraLlegada(String horaEstimada, String horaLlegada) {
        Log.d(TAG, "HORA ESTIMADA: " + horaEstimada);
        Log.d(TAG, "HORA DE LLEGADA: " + horaLlegada);

        if (horaLlegada.length() > 0) {
            if (isLateHoraLlegada(horaEstimada, horaLlegada)) {
                return R.color.red_3;
            } else {
                return R.color.green;
            }
        } else {
            Log.d(TAG, "NO HAY HORA DE LLEGADA!");
            return R.color.gris_2;
        }
    }

    private boolean isLateHoraLlegada(String horaEstimada, String horaLlegada) {
        Date dateHoraEstimada, dateHoraLlegada;

        try {
            dateHoraEstimada = new SimpleDateFormat("hh:mm").parse(horaEstimada);
            dateHoraLlegada = new SimpleDateFormat("hh:mm").parse(horaLlegada);

            Calendar calHoraEstimada = Calendar.getInstance();
            Calendar calHoraLlegada = Calendar.getInstance();

            calHoraEstimada.setTime(dateHoraEstimada);
            // El tiempo de tolerancia es de 15 minutos.
            calHoraEstimada.set(Calendar.MINUTE, calHoraEstimada.get(Calendar.MINUTE) + 15);

            calHoraLlegada.setTime(dateHoraLlegada);

            if (calHoraLlegada.before(calHoraEstimada)) {
                Log.d(TAG, "LLEGO A TIEMPO");
                return false;
            } else {
                Log.d(TAG, "LLEGO TARDE");
                return true;
            }
        } catch (ParseException ex) {
            ex.printStackTrace();
            Log.d(TAG, "ERROR CONVIRTIENDO HORAS DE LLEGADA!");
            return false;
        }
    }

    private String getIDPlanDeViaje() {
        if (dbPlanDeViaje != null && dbPlanDeViaje.size() > 0) {
            return dbPlanDeViaje.get(0).getIdPlanViaje();
        }

        return "";
    }

    private ParadaProgramada getParadaProgramadaByID(String idStop) {
        for (int i = 0; i < dbParadaProgramada.size(); i++) {
            if (dbParadaProgramada.get(i).getIdStop().equals(idStop)) {
                return dbParadaProgramada.get(i);
            }
        }
        return null;
    }

    private boolean isIniciadoRuta() {
        try {
            return dbPlanDeViaje.get(0).getEstadoRecorrido() == PlanDeViaje.EstadoRuta.INICIO_RUTA;
        } catch (NullPointerException ex) {
            return false;
        }
    }

    private void updateEstadoRecorridoPlanViaje(int estado) {
        PlanDeViaje planDeViaje = planDeViajeInteractor.selectPlanViajeById(
                dbPlanDeViaje.get(0).getIdPlanViaje());
        planDeViaje.setEstadoRecorrido(estado);
        planDeViaje.save();
    }

    private void updateEstadoRecorridoParadaProgramada(int position, int estado) {
        ParadaProgramada paradaProgramada = ParadaProgramada.findById(
                ParadaProgramada.class, dbParadaProgramada.get(position).getId());
        paradaProgramada.setEstadoLlegada(estado);
        paradaProgramada.save();
    }

    private void enableButtonTerminarRutaOnLlegoAgencia(int position) {
        if (isParadaHub(position)) {
            view.setEnabledBtnTerminarRuta(true, R.drawable.ripple_button_default);
        }
    }

    private void enableButtonsPlanViaje() {
        dbPlanDeViaje = planDeViajeInteractor.selectAllPlanViaje();
        if (dbPlanDeViaje != null && dbPlanDeViaje.size() > 0) {
            switch (dbPlanDeViaje.get(0).getEstadoRecorrido()) {
                case PlanDeViaje.EstadoRuta.NO_INICIO_RUTA:
                    view.setEnabledBtnInciarRuta(true, R.drawable.ripple_button_default);
                    view.setVisibilityBtnEditarPlaca(View.VISIBLE);
                    break;
                case PlanDeViaje.EstadoRuta.INICIO_RUTA:
                    view.setEnabledBtnInciarRuta(false, R.drawable.ripple_button_default_inactive);
                    if (dbParadaProgramada.get(dbParadaProgramada.size() - 1).getEstadoLlegada()
                            == ParadaProgramada.Status.LLEGO_AGENCIA) {
                        view.setEnabledBtnTerminarRuta(true, R.drawable.ripple_button_default);
                    }
                    view.setVisibilityBtnEditarPlaca(View.GONE);
                    break;
                case PlanDeViaje.EstadoRuta.TERMINO_RUTA:
                    view.setEnabledBtnTerminarRuta(false, R.drawable.ripple_button_default_inactive);
                    break;
            }
        }
    }

    private boolean existPlanDeViaje() {
        return dbPlanDeViaje != null && dbPlanDeViaje.size() > 0;
    }

    private boolean isParadaHub(int position) {
        return dbParadaProgramada.get(position).getTipo().equalsIgnoreCase("U");
    }

    private boolean isNewPlanDeViaje(String idPlanViajeCurrent, String idPlanViajeNew) {
        return !idPlanViajeCurrent.equals(idPlanViajeNew);
    }

    private ArrayList<PlanDeViaje> buildOrigenPlanViaje(JSONArray provinciasOrigen, JSONArray planViaje) throws JSONException {
        ArrayList<PlanDeViaje> origenPlanViaje = new ArrayList<>();
        JSONObject origen;
        for (int i = 0; i < provinciasOrigen.length(); i++) {
            origen = provinciasOrigen.getJSONObject(i);
            origenPlanViaje.add(new PlanDeViaje(
                    origen.getString("id_planviaje"),
                    "", "",
                    origen.getString("id_origen"),
                    "",
                    origen.getString("nom_origen"),
                    origen.getString("descri_planviaje"),
                    "", "", "", "", "", "", "",
                    planViaje.getJSONObject(i).getString("origen_px"),
                    planViaje.getJSONObject(i).getString("origen_py"),
                    origen.getString("fecha_planviaje"),
                    planViaje.getJSONObject(i).getString("km"),
                    PlanDeViaje.EstadoRuta.NO_INICIO_RUTA
            ));
        }
        return origenPlanViaje;
    }

    /**
     * Broadcast
     *
     * {@link ForzarCierreRutaHelper#sendOnRutaFinalizadaReceiver}
     */
    private final BroadcastReceiver forzarCierreRutaReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            view.clearPlanDeViaje();
            view.setVisibilityBtnEditarPlaca(View.GONE);
            view.setEnabledBtnInciarRuta(true, R.drawable.ripple_button_default);
            view.setEnabledBtnTerminarRuta(false, R.drawable.ripple_button_default_inactive);
            dbPlanDeViaje.clear();
            dbParadaProgramada.clear();
            List<ParadaProgramadaItem> paradasProgramadas = new ArrayList<>();
            view.showDatosParadasProgramadas(paradasProgramadas);
        }
    };

    /**
     * Broadcast
     *
     * {@link IniciarTerminarRutaPlanDeViajeDialog#sendIniciarTerminarPlanDeViajeReceiver}
     */
    private final BroadcastReceiver iniciarTerminarPlanDeViajeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            enableButtonsPlanViaje();
        }
    };

    /**
     * Broadcast
     *
     * {@link InfoParadaProgramadaBottomSheetDialog#sendLlegadaSalidaParadaProgramadaReceiver}
     */
    private final BroadcastReceiver llegadaSalidaParadaProgramadaReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "llegadaSalidaParadaProgramadaReceiver INIT");
            ParadaProgramada paradaProgramada = (ParadaProgramada) intent.getSerializableExtra("paradaProgramada");


            Log.d(TAG, "Hora Llegada: " + paradaProgramada.getHoraLlegada());
            Log.d(TAG, "Hora Salida: " + paradaProgramada.getHoraSalida());

            boolean isParadaHub = intent.getBooleanExtra("isParadaHub", false);

            if (paradaProgramada.getEstadoLlegada() == ParadaProgramada.Status.LLEGO_AGENCIA) {
                if (isParadaHub) {
                    view.setEnabledBtnTerminarRuta(true, R.drawable.ripple_button_default);
                }
            }

            /*for (int i = 0; i < dbParadaProgramada.size(); i++) {
                if (dbParadaProgramada.get(i).getIdStop().equals(paradaProgramada.getIdStop())) {
                    dbParadaProgramada.set(i, paradaProgramada);
                    break;
                }
            }

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    List<ParadaProgramadaItem> paradasProgramadasItems = new ArrayList<>();

                    for (int i = 0; i < dbParadaProgramada.size(); i++) {
                        paradasProgramadasItems.add(new ParadaProgramadaItem(
                                dbParadaProgramada.get(i).getSiglaAgencia() != null
                                        ? dbParadaProgramada.get(i).getSiglaAgencia()
                                        : dbParadaProgramada.get(i).getAgencia(),
                                dbParadaProgramada.get(i).getHoraEstimada(),
                                dbParadaProgramada.get(i).getHoraLlegada(),
                                dbParadaProgramada.get(i).getHoraSalida(),
                                getColorOfLabelHoraLlegada(dbParadaProgramada.get(i).getHoraEstimada(),
                                        dbParadaProgramada.get(i).getHoraLlegada())
                        ));
                    }

                    view.showDatosParadasProgramadas(paradasProgramadasItems);
                }
            });*/

            loadParadasProgramadas();

            //view.setVisibilitySwipeRefreshLayout(true);
            //onSwipeRefresh(); // Cargar nuevamente para obtener fecha llegada/fecha salida;
        }
    };
}