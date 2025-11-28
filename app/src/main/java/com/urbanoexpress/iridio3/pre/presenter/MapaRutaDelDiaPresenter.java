package com.urbanoexpress.iridio3.pre.presenter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.android.gms.maps.model.LatLng;
import com.urbanoexpress.iridio3.pre.model.entity.ClienteRuta;
import com.urbanoexpress.iridio3.pre.model.entity.Data;
import com.urbanoexpress.iridio3.pre.model.entity.ParadaRuta;
import com.urbanoexpress.iridio3.pre.model.entity.ResumenRuta;
import com.urbanoexpress.iridio3.pre.model.entity.SecuenciaRuta;
import com.urbanoexpress.iridio3.pre.model.entity.WaypointRuta;
import com.urbanoexpress.iridio3.pre.model.interactor.MapaRutaDelDiaInteractor;
import com.urbanoexpress.iridio3.pre.ui.model.GuiasMapaRutaDiaItem;
import com.urbanoexpress.iridio3.pre.util.Preferences;
import com.urbanoexpress.iridio3.pre.util.Session;
import com.urbanoexpress.iridio3.pre.util.async.AsyncTaskCoroutine;
import com.urbanoexpress.iridio3.pre.R;
import com.urbanoexpress.iridio3.pre.model.entity.GuiaGestionada;
import com.urbanoexpress.iridio3.pre.model.entity.Ruta;
import com.urbanoexpress.iridio3.pre.model.interactor.GoogleMapsServiceApis;
import com.urbanoexpress.iridio3.pre.model.interactor.RutaPendienteInteractor;
import com.urbanoexpress.iridio3.pre.model.interactor.callback.RequestCallback;
import com.urbanoexpress.iridio3.pre.model.util.ModelUtils;
import com.urbanoexpress.iridio3.pre.ui.dialogs.GuiasPendientesSinCoordenadasDialog;
import com.urbanoexpress.iridio3.pre.ui.helpers.ModalHelper;
import com.urbanoexpress.iridio3.pre.ui.model.RutaItem;
import com.urbanoexpress.iridio3.pre.util.CommonUtils;
import com.urbanoexpress.iridio3.pre.util.constant.LocalAction;
import com.urbanoexpress.iridio3.pre.view.BaseModalsView;
import com.urbanoexpress.iridio3.pre.view.MapaRutaDelDiaView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by mick on 24/08/17.
 */

public class MapaRutaDelDiaPresenter {

    private MapaRutaDelDiaView view;
    private RutaPendienteInteractor interactor;

    private List<Ruta> guias;
    private List<GuiasMapaRutaDiaItem> guiasMapa = new ArrayList<>();
    private List<RutaItem> guiaItems = new ArrayList<>();
    private ArrayList<RutaItem> guiaSinCoordenadasItems = new ArrayList<>();

    private Ruta guiaSeleccionada; // Actualizar coordenada
    private boolean updateCoordenadaGuia = false;

    private ArrayList<Boolean> guiasRuteadas;
    private boolean rutearGuias = false;
    private int countRuteoGuias = 0;

    Ruta guiaCorrespondiente = null;

    public MapaRutaDelDiaPresenter(MapaRutaDelDiaView view) {
        this.view = view;
        this.interactor = new RutaPendienteInteractor(view.getViewContext());
    }

    public void init() {
        //getParadas();

        if(Session.getUser().getFlag().equals("0")) {
            getMarcadoresRutaDia();
        } else {
            view.onLoading(true);
        }

        LocalBroadcastManager.getInstance(view.getViewContext())
                .registerReceiver(editarCoordenadaGuiaReceiver,
                        new IntentFilter(LocalAction.EDITAR_COORDENADA_GE));
    }

    public void onMapReady() {
        new LoadGuiasOnMapTask().execute();
    }

    public void onClickMarkerMap(int position) {
//        guiaItems.clear();
//
//        int resIcon = ModelUtils.getIconLineaNegocio(guias.get(position).getLineaNegocio());
//        int resIconValija = ModelUtils.isGuiaEntrega(guias.get(position).getTipo())
//                ? R.drawable.ic_valija_entrega
//                : R.drawable.ic_valija_recoleccion;
//
//        int backgroundColorGuia = ModelUtils.isValija(guias.get(position).getTipoEnvio())
//                ? ContextCompat.getColor(activity, R.color.oro_valija)
//                : ContextCompat.getColor(activity, R.color.lightPrimaryText);
//
//        RutaItem rutaItem = new RutaItem(
//                guias.get(position).getIdServicio(),
//                guias.get(position).getIdManifiesto(),
//                (ModelUtils.isGuiaEntrega(guias.get(position).getTipo())
//                        ? guias.get(position).getGuia()
//                        : guias.get(position).getShipper() + " (" + guias.get(position).getGuia() + ")"),
//                guias.get(position).getDistrito(),
//                guias.get(position).getDireccion(),
//                (guias.get(position).getHorario().length() == 0) ? "00:00" : guias.get(position).getHorario(),
//                guias.get(position).getTipo().toUpperCase(),
//                "",
//                resIcon,
//                resIcon,
//                resIconValija,
//                backgroundColorGuia,
//                false,
//                false,
//                guias.get(position).getResultadoGestion(),
//                ModelUtils.isValija(guias.get(position).getTipoEnvio()),
//                false);
//
//        guiaItems.add(rutaItem);
//
//        view.displayListGuias(guiaItems);

        if(Session.getUser().getFlag().equals("0")){
            if (position < 0 || position >= guiasMapa.size()) {
                return;
            }

            GuiasMapaRutaDiaItem guiaMapa = guiasMapa.get(position);
            String barraGuiaMapa = guiaMapa.getBarra();
            for (Ruta guia : guias) {
                if (guia != null && barraGuiaMapa.equals(guia.getGuia())) {
                    guiaCorrespondiente = guia;
                    break;
                }
            }
        } else {
            if (position < 0 || position >= guias.size()) {
                return;
            }
        }

        if (rutearGuias) {
            if (guias.get(position).getSecuencia().isEmpty()) {
                countRuteoGuias++;
                guiasRuteadas.set(position, true);
                guias.get(position).setSecuencia(countRuteoGuias + "");
                view.updateNumberIconMarker(position, countRuteoGuias + "");
            } else {
                view.showToast(R.string.activity_ruta_msg_posicion_definida_guia_ruteo);
            }
        } else {
            try {
                if(Session.getUser().getFlag().equals("0")){
                    new LoadListGuiasTask().execute(guiaCorrespondiente);
                } else {
                    new LoadListGuiasTask().execute(guias.get(position));
                }
            } catch (ArrayIndexOutOfBoundsException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void onClickRutearGuias() {
        new ShowRutearGuiasTask().execute();
    }

    public void onClickGuiasSinCoordenadas() {
        new ShowGuiasPendientesSinCoordenadasTask().execute();
    }

    public void onClickSeleccionarCoordenada(final double latitude, final double longitude) {
        ModalHelper.getBuilderAlertDialog(view.getViewContext())
                .setMessage(R.string.activity_ruta_msg_seleccionar_coordenada)
                .setPositiveButton(R.string.text_seleccionar, (dialog, which) ->
                        new UpdateCoordenadaGETask().execute(latitude + "", longitude + ""))
                .setNegativeButton(R.string.text_cancelar, null)
                .show();
    }

    public void onClickGuardarRuteoGuias() {
        if (countRuteoGuias > 0) {
            ModalHelper.getBuilderAlertDialog(view.getViewContext())
                    .setMessage(R.string.activity_ruta_msg_guardar_ruteo_guias)
                    .setPositiveButton(R.string.text_guardar, (dialog, which) ->
                            new SaveRuteoGuiasTask().execute())
                    .setNegativeButton(R.string.text_cancelar, null)
                    .show();
        } else {
            view.showToast(R.string.activity_ruta_msg_debe_seleccionar_una_guia_ruteo);
        }
    }

    public void onClickItemGuia() {
        if (guiaSeleccionada.getEstadoDescarga() == Ruta.EstadoDescarga.PENDIENTE) {
            showMarkerSelector();
        }
    }

    public void onDestroyActivity() {
        LocalBroadcastManager.getInstance(view.getViewContext())
                .unregisterReceiver(editarCoordenadaGuiaReceiver);
        view.dismissProgressDialog();
    }

    public boolean onBackButtonPressed() {
        if (updateCoordenadaGuia) {
            ModalHelper.getBuilderAlertDialog(view.getViewContext())
                    .setMessage(R.string.activity_ruta_msg_cancelar_seleccion_coordenada)
                    .setPositiveButton(R.string.text_continuar, null)
                    .setNegativeButton(R.string.text_cancelar, (dialog, which) -> {
                        updateCoordenadaGuia = false;
                        view.hideMarkerSelector();
                        new LoadGuiasOnMapTask().execute();
                    })
                    .show();
            return true;
        } else if (rutearGuias) {
            ModalHelper.getBuilderAlertDialog(view.getViewContext())
                    .setMessage(R.string.activity_ruta_msg_cancelar_ruteo_guias)
                    .setPositiveButton(R.string.text_continuar, null)
                    .setNegativeButton(R.string.text_cancelar, (dialog, which) -> {
                        rutearGuias = false;
                        view.setVisibilityBoxRuteoGuias(View.GONE);
                        new LoadGuiasOnMapTask().execute();
                    })
                    .show();
            return true;
        } else {
            return false;
        }
    }

    private class LoadGuiasOnMapTask extends AsyncTaskCoroutine<String, String> {

        private int totalGuiasSinCoordenadas = 0;
        private int totalGuiasSinGestionar = 0;

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            view.showProgressDialog();
        }

        @Override
        public String doInBackground(String... strings) {
            guias = interactor.selectGuiasMapaDistribucion();

            for (int i = 0; i < guias.size(); i++) {
                if (guias.get(i).getEstadoDescarga() == Ruta.EstadoDescarga.GESTIONADO) {
                    GuiaGestionada guiaGestionada =
                            interactor.selectRutaGestionada(guias.get(i).getIdServicio(),
                                    guias.get(i).getLineaNegocio());
                    if (guiaGestionada != null) {
                        if (CommonUtils.isValidCoords(guiaGestionada.getGpsLatitude(),
                                guiaGestionada.getGpslongitude())) {
                            guias.get(i).setGpsLatitude(guiaGestionada.getGpsLatitude());
                            guias.get(i).setGpsLongitude(guiaGestionada.getGpslongitude());
                        } else if (!CommonUtils.isValidCoords(guias.get(i).getGpsLatitude(),
                                guias.get(i).getGpsLongitude())) {
                            guias.remove(i);
                            i -= 1;
                        }
                    } else if (!CommonUtils.isValidCoords(guias.get(i).getGpsLatitude(),
                            guias.get(i).getGpsLongitude())) {
                        guias.remove(i);
                        i -= 1;
                    }
                } else {
                    totalGuiasSinGestionar++;
                    if (!CommonUtils.isValidCoords(guias.get(i).getGpsLatitude(),
                            guias.get(i).getGpsLongitude())) {
                        guias.remove(i);
                        i -= 1;
                        totalGuiasSinCoordenadas++;
                    }
                }
            }

            Log.d("ACTIVITY", "TOTAL GUIAS MAPA: " + guias.size());

            return null;
        }

        @Override
        public void onPostExecute(String s) {
            super.onPostExecute(s);
            if (totalGuiasSinCoordenadas == 0) {
                view.setVisibilityFabGuiasSinCoordenadas(View.GONE);
            } else {
                view.setVisibilityFabGuiasSinCoordenadas(View.VISIBLE);
            }
            if (totalGuiasSinGestionar == 0
                    || totalGuiasSinCoordenadas > 0) {
                view.setVisibilityFabRutearGuias(View.GONE);
            } else {
                view.setVisibilityFabRutearGuias(View.VISIBLE);
            }
            if(Session.getUser().getFlag().equals("1")) {
                view.displayGuiasOnMap(guias);
            }
            if (!CommonUtils.isActivityDestroyed(view.getViewContext())) {
                view.dismissProgressDialog();
            }
        }
    }

    private class ShowGuiasPendientesSinCoordenadasTask extends AsyncTaskCoroutine<String, ArrayList<Ruta>> {

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            view.showProgressDialog();
        }

        @Override
        public ArrayList<Ruta> doInBackground(String... strings) {
            List<Ruta> guias = interactor.selectRutasPendientes();

            guiaSinCoordenadasItems = new ArrayList<>();

            for (int i = 0; i < guias.size(); i++) {
                if (CommonUtils.isValidCoords(guias.get(i).getGpsLatitude(),
                        guias.get(i).getGpsLongitude())) {
                    guias.remove(i);
                    i -= 1;
                } else {
                    guiaSinCoordenadasItems.add(buildGuiaItem(guias.get(i)));
                }
            }

            Log.d("ACTIVITY", "TOTAL GUIAS PENDIENTES SIN COORDENADAS: " + guias.size());
            Log.d("ACTIVITY", "TOTAL GUIAS ITEMS PENDIENTES SIN COORDENADAS: " + guiaSinCoordenadasItems.size());

            return (ArrayList<Ruta>) guias;
        }

        @Override
        public void onPostExecute(ArrayList<Ruta> items) {
            view.dismissProgressDialog();
            GuiasPendientesSinCoordenadasDialog dialog = new GuiasPendientesSinCoordenadasDialog();
            Bundle bundle = new Bundle();
            bundle.putSerializable("guias", items);
            bundle.putSerializable("items", guiaSinCoordenadasItems);
            dialog.setArguments(bundle);
            dialog.show((
                    (AppCompatActivity) view.getViewContext()).getSupportFragmentManager(),
                    "GuiasPendientesSinCoordenadasDialog");
            super.onPostExecute(items);
        }
    }

    private class LoadListGuiasTask extends AsyncTaskCoroutine<Ruta, String> {

        @Override
        public String doInBackground(Ruta... strings) {
            Ruta guia = RutaPendienteInteractor.selectRuta(
                    strings[0].getIdServicio(), strings[0].getLineaNegocio());

            Log.d("ACTIVITY", "TOTAL GUIAS: " + guias.size());
            guiaItems.clear();

            //for (int i = 0; i < guias.size(); i++) {
            if (guia != null) {
                guiaSeleccionada = guia;
                guiaItems.add(buildGuiaItem(guia));
            }
            return null;
        }

        @Override
        public void onPostExecute(String s) {
            view.displayListGuias(guiaItems);
            super.onPostExecute(s);
        }
    }

    private class UpdateCoordenadaGETask extends AsyncTaskCoroutine<String, String> {

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            view.showProgressDialog();
        }

        @Override
        public String doInBackground(String... strings) {
            Ruta guia = RutaPendienteInteractor.selectRuta(
                    guiaSeleccionada.getIdServicio(), guiaSeleccionada.getLineaNegocio());

            if (guia != null) {
                guia.setGpsLatitude(strings[0]);
                guia.setGpsLongitude(strings[1]);
                guia.save();
            }
            return null;
        }

        @Override
        public void onPostExecute(String s) {
            updateCoordenadaGuia = false;
            view.dismissProgressDialog();
            view.hideMarkerSelector();
            view.showToast(R.string.activity_ruta_msg_coordenada_actualizado_exitosamente);
            new LoadGuiasOnMapTask().execute();
            super.onPostExecute(s);
        }
    }

    private class ShowRutearGuiasTask extends AsyncTaskCoroutine<String, ArrayList<Ruta>> {

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            view.showProgressDialog();
        }

        @Override
        public ArrayList<Ruta> doInBackground(String... strings) {
            guias = interactor.selectRutasPendientes();

            countRuteoGuias = 0;
            guiasRuteadas = new ArrayList<>();

            for (int i = 0; i < guias.size(); i++) {
                if (CommonUtils.isValidCoords(guias.get(i).getGpsLatitude(),
                        guias.get(i).getGpsLongitude())) {
                    guiasRuteadas.add(false);
                    guias.get(i).setSecuencia("");
                } else {
                    guias.remove(i);
                    i -= 1;
                }
            }

            Log.d("ACTIVITY", "TOTAL GUIAS PENDIENTES CON COORDENADAS: " + guias.size());

            return (ArrayList<Ruta>) guias;
        }

        @Override
        public void onPostExecute(ArrayList<Ruta> items) {
            rutearGuias = true;
            view.dismissProgressDialog();
            view.setVisibilityBoxRuteoGuias(View.VISIBLE);
            view.displayRutearGuiasOnMap(guias);
            super.onPostExecute(items);
        }
    }

    private class SaveRuteoGuiasTask extends AsyncTaskCoroutine<String, String> {

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            view.showProgressDialog();
        }

        @Override
        public String doInBackground(String... strings) {

            for (int i = 0; i < guiasRuteadas.size(); i++) {
                if (!guiasRuteadas.get(i)) {
                    countRuteoGuias++;
                    guias.get(i).setSecuencia(countRuteoGuias + "");
                }
                guias.get(i).save();
            }
            return null;
        }

        @Override
        public void onPostExecute(String s) {
            rutearGuias = false;
            view.dismissProgressDialog();
            view.setVisibilityBoxRuteoGuias(View.GONE);
            view.showToast(R.string.activity_ruta_msg_ruteo_guardado_exitosamente);
            new LoadGuiasOnMapTask().execute();
            sendOnActualizarOrdenGuiasReceiver();
            super.onPostExecute(s);
        }
    }

    private RutaItem buildGuiaItem(Ruta guia) {
        int resIcon = ModelUtils.getIconTipoGuia(guia);

        int resIconTipoEnvio = ModelUtils.getIconTipoEnvio(guia);

        int backgroundColorGuia = ModelUtils.getBackgroundColorGE(guia.getTipoEnvio(), view.getViewContext());

        int lblColorHorario = ModelUtils.getLblColorHorario(guia.getTipo(),
                guia.getTipoEnvio(), guia.getFechaRuta(), guia.getHorarioEntrega());

        String horario = guia.getHorarioEntrega();

        if (guia.getResultadoGestion() != 0) {
            Log.d("ACTIVYT", "OK A");
            GuiaGestionada guiaGestionada =
                    interactor.selectRutaGestionada(guia.getIdServicio(),
                            guia.getLineaNegocio());
            if (guiaGestionada != null) {
                Log.d("ACTIVYT", "OK A1");
                try {
                    Date fechaHoraGestion = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(
                            guiaGestionada.getFecha() + " " + guiaGestionada.getHora());
                    horario = CommonUtils.fomartHorarioAproximado(fechaHoraGestion.getTime(), false);
                } catch (ParseException ex) {
                    Log.d("ACTIVYT", "ERROR 1");
                    ex.printStackTrace();
                }
            }
        } else {
            Log.d("ACTIVYT", "OK B");
            if (ModelUtils.isGuiaEntrega(guia.getTipo())) {
                horario = CommonUtils.fomartHorarioAproximado(guia.getHorarioAproximado(), false);
            }
        }

        String simboloMoneda = ModelUtils.getSimboloMoneda(view.getViewContext());

        return new RutaItem(
                guia.getIdServicio(),
                guia.getIdManifiesto(),
                (ModelUtils.isGuiaEntrega(guia.getTipo())
                        ? guia.getGuia()
                        : guia.getShipper() + " (" + guia.getGuia() + ")"),
                guia.getDistrito(),
                guia.getDireccion(),
                horario,
                guia.getPiezas(),
                guia.getSecuencia(),
                simboloMoneda,
                resIcon,
                resIcon,
                resIconTipoEnvio,
                backgroundColorGuia,
                lblColorHorario,
                guia.getResultadoGestion(),
                guia.getResultadoGestion() != 0,
                guia.getResultadoGestion() == 0,
                ModelUtils.isTipoEnvioValija(guia.getTipoEnvio()),
                CommonUtils.parseDouble(guia.getImporte()) > 0,
                false);
    }

    private void showMarkerSelector() {
        updateCoordenadaGuia = true;

        String direccion = "";
        boolean existeDistrito = false;

        if (guiaSeleccionada.getDireccion() != null &&
                !guiaSeleccionada.getDireccion().isEmpty()) {
            direccion = guiaSeleccionada.getDireccion();
            Log.d("ACTIVITY", "DIRECCION 2");
        }

        if (guiaSeleccionada.getDistrito() != null &&
                !guiaSeleccionada.getDistrito().isEmpty()) {
            existeDistrito = true;
            direccion += " - " + guiaSeleccionada.getDistrito();
            Log.d("ACTIVITY", "DIRECCION 1");
        }

        view.displayMarkerSelector(direccion);

        if (!CommonUtils.isValidCoords(guiaSeleccionada.getGpsLatitude(),
                guiaSeleccionada.getGpsLongitude()) && existeDistrito) {
            Log.d("ACTIVITY", "ENFOCAR VISTA DEL MAPA");
            getUbicacionDistritoGuia();
        } else {
            Log.d("ACTIVITY", "NO ENFOCAR VISTA DEL MAPA");
        }
    }

    private void getUbicacionDistritoGuia() {
        if (CommonUtils.validateConnectivity(view.getViewContext())) {
            RequestCallback callback = new RequestCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    try {
                        processResponceGeocoding(response);
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }
                }

                @Override
                public void onError(VolleyError error) {
                    error.printStackTrace();
                }
            };

            String[] params = new String[] {
                    guiaSeleccionada.getDistrito() + ", " + CommonUtils.getNameCountryCurrent(view.getViewContext())
            };

            GoogleMapsServiceApis.geocoding(params, callback);
        }
    }

    private boolean processResponceGeocoding(JSONObject data) throws JSONException {
        if (data.getString("status").equals("OK")) {
            JSONObject json = data.getJSONArray("results").getJSONObject(0);
            json = json.getJSONObject("geometry").getJSONObject("bounds");

            LatLng northeast = new LatLng(json.getJSONObject("northeast").getDouble("lat"),
                    json.getJSONObject("northeast").getDouble("lng"));

            LatLng southwest = new LatLng(json.getJSONObject("southwest").getDouble("lat"),
                    json.getJSONObject("southwest").getDouble("lng"));

            ArrayList<LatLng> latLngs = new ArrayList<>();
            latLngs.add(northeast);
            latLngs.add(southwest);

            view.animateCameraMap(latLngs);
        }
        return false;
    }

    private void getParadas(){
        view.onLoading(true);
        RequestCallback callback = new RequestCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try{
                    if(response.getBoolean("success")) {
                        JSONObject data = response.getJSONObject("data");
                        JSONArray paradasArray = data.getJSONArray("data");
                        List<ParadaRuta> paradas = new ArrayList<>();

                        for (int i = 0; i < paradasArray.length(); i++) {
                            JSONObject paradaJson = paradasArray.getJSONObject(i);
                            JSONArray datosArray = paradaJson.getJSONArray("datos");

                            List<ClienteRuta> clientes = new ArrayList<>();

                            for (int j = 0; j < datosArray.length(); j++) {
                                JSONObject clienteJson = datosArray.getJSONObject(j);

                                String nombre = clienteJson.getString("cliente");
                                String guia = clienteJson.getString("barra_guia");
                                int totalPiezas = clienteJson.getInt("total_piezas");

                                clientes.add(new ClienteRuta(nombre, guia, totalPiezas));
                            }

                            double dirPx = paradaJson.getDouble("dir_px");
                            double dirPy = paradaJson.getDouble("dir_py");
                            int geoId = paradaJson.getInt("geo_id");
                            String direccion = paradaJson.getString("direccion");

                            JSONArray waypointsArray = paradaJson.getJSONArray("waypoint");
                            List<WaypointRuta> waypoints = new ArrayList<>();

                            for (int j = 0; j < waypointsArray.length(); j++) {
                                JSONObject waypointJson = waypointsArray.getJSONObject(j);

                                double coorX = waypointJson.getDouble("coor_x");
                                double coorY = waypointJson.getDouble("coor_y");
                                int secuencia = waypointJson.getInt("secuencia");
                                int idWayPoint = waypointJson.getInt("id_way_point");

                                waypoints.add(new WaypointRuta(coorX, coorY, secuencia, idWayPoint));
                            }

                            ParadaRuta parada = new ParadaRuta(clientes, dirPx, dirPy, geoId, direccion, waypoints);
                            paradas.add(parada);
                        }

                        long delayMillis = 3000;

                        new Handler(Looper.getMainLooper()).postDelayed(() -> {
                            if (paradas.isEmpty()) {
                                view.onLoading(false);
                            } else {
                                view.setVisibilityFabRutaMapa(View.GONE);
                                view.drawRouteOnMap(paradas);
                            }
                        }, delayMillis);
                    }
                } catch (JSONException e){
                    e.printStackTrace();
                    view.onLoading(false);
                    BaseModalsView.showToast(view.getViewContext(),
                            R.string.json_object_exception,
                            Toast.LENGTH_LONG);
                }
            }

            @Override
            public void onError(VolleyError error) {
                error.printStackTrace();
                view.onLoading(false);
                BaseModalsView.showToast(view.getViewContext(),
                        R.string.volley_error_message,
                        Toast.LENGTH_SHORT);
            }
        };

        String[] params = new String[]{
                Preferences.getInstance().getString("idUsuario", ""),
                Preferences.getInstance().getString("id_ruta", "0")
        };
        MapaRutaDelDiaInteractor.getCoordenadas(params, view.getViewContext(), callback);
    }

    private void getMarcadoresRutaDia(){
        view.onLoading(true);
        RequestCallback callback = new RequestCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try{
                    if(response.getBoolean("success")) {
                        JSONArray data = response.getJSONArray("data");
                        guiasMapa.clear();
                        for(int i = 0; i < data.length(); i++){
                            JSONObject item = data.getJSONObject(i);
                            double dirPx = 0.0;
                            double dirPy = 0.0;

                            if (!item.isNull("dir_px")) {
                                dirPx = item.getDouble("dir_px");
                            }
                            if (!item.isNull("dir_py")) {
                                dirPy = item.getDouble("dir_py");
                            }

                            GuiasMapaRutaDiaItem guias = new GuiasMapaRutaDiaItem(
                                    item.getString("barra"),
                                    item.getString("secuencia"),
                                    item.getInt("flag_valida_gestion"),
                                    dirPx,
                                    dirPy
                            );
                            guiasMapa.add(guias);
                        }

                        long delayMillis = 1000;

                        new Handler(Looper.getMainLooper()).postDelayed(() -> {
                            if (guiasMapa.isEmpty()) {
                                view.onLoading(false);
                            } else {
                                view.setVisibilityFabRutaMapa(View.GONE);
                                view.displayGuiasOnMapV2(guiasMapa);
                            }
                        }, delayMillis);

                    } else {
                        view.onLoading(false);
                        BaseModalsView.showToast(view.getViewContext(),
                                response.getString("msg_error"),
                                Toast.LENGTH_LONG);
                    }
                } catch (JSONException e){
                    e.printStackTrace();
                    view.onLoading(false);
                    BaseModalsView.showToast(view.getViewContext(),
                            R.string.json_object_exception,
                            Toast.LENGTH_LONG);
                }
            }

            @Override
            public void onError(VolleyError error) {
                error.printStackTrace();
                view.onLoading(false);
                BaseModalsView.showToast(view.getViewContext(),
                        R.string.volley_error_message,
                        Toast.LENGTH_SHORT);
            }
        };

        MapaRutaDelDiaInteractor.getDatosMapaRutaDia(Preferences.getInstance().getString("id_ruta", "0"), callback);
    }

    private void registerNewSecuencia() {
        SecuenciaRuta secuenciaRuta = new SecuenciaRuta(
                Preferences.getInstance().getString("idUsuario", ""),
                Data.Delete.NO);
        secuenciaRuta.save();
    }

    /**
     * Broadcast
     *
     * {@link GuiasPendientesSinCoordenadasDialog#sendOnEditarCoordenadaGuiaReceiver}
     */
    private BroadcastReceiver editarCoordenadaGuiaReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            guiaSeleccionada = (Ruta) intent.getSerializableExtra("guia");
            Log.d("ACTIVITY", guiaSeleccionada.getIdServicio());
            Log.d("ACTIVITY", guiaSeleccionada.getLineaNegocio());
            showMarkerSelector();
        }
    };

    /**
     * Receiver
     *
     * {@link RutaPendientePresenter#guardarOrdenGuiasReceiver}
     */
    private void sendOnActualizarOrdenGuiasReceiver() {
        Intent intent = new Intent(LocalAction.GUARDAR_ORDEN_GUIAS_ACTION);
        LocalBroadcastManager.getInstance(view.getViewContext()).sendBroadcast(intent);
    }
}
