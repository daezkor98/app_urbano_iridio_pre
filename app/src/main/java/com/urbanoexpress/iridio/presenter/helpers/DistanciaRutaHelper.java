package com.urbanoexpress.iridio.presenter.helpers;

import android.content.Context;
import android.location.LocationManager;
import android.util.Log;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.urbanoexpress.iridio.model.entity.Ruta;
import com.urbanoexpress.iridio.model.interactor.GoogleMapsServiceApis;
import com.urbanoexpress.iridio.model.interactor.RutaPendienteInteractor;
import com.urbanoexpress.iridio.model.interactor.callback.RequestCallback;
import com.urbanoexpress.iridio.ui.model.RutaItem;
import com.urbanoexpress.iridio.util.network.Connection;

/**
 * Created by mick on 18/07/16.
 */
public class DistanciaRutaHelper {

    private static final String TAG = DistanciaRutaHelper.class.getSimpleName();

    private Context context;

    private List<RutaItem> rutaItems;
    private List<Ruta> rutas;

    private RutaPendienteInteractor rutaPendienteInteractor;

    private GoogleMapsServiceApis googleMapsServiceApis;

    private LocationManager locationManager = null;

    private int countProcessCalculateDistanciaRutas = 0;

    private Date lastHoraLlegada;

    public DistanciaRutaHelper(Context context) {
        this.context = context;

        rutaPendienteInteractor = new RutaPendienteInteractor(context);
        googleMapsServiceApis = new GoogleMapsServiceApis(context);

        if (locationManager == null)
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    public void initCalculateDistances(List<Ruta> rutas, List<RutaItem> rutaItems) {
        this.rutas = rutas;
        this.rutaItems = rutaItems;
        if (Connection.hasNetworkConnectivity(context)) {
            if (hasCoordsFirstRuta()) {
                processCalculateDistances();
            }
        }
    }

    private void processCalculateDistances() {
        Log.d(TAG, "TOTAL RUTAS: " + rutas.size());
        if (countProcessCalculateDistanciaRutas < rutas.size()) {
            if (isValidCoordRuta(countProcessCalculateDistanciaRutas)) {
                RequestCallback callback = new RequestCallback() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        try {
                            if (processResponceDistance(response)) {
                                countProcessCalculateDistanciaRutas++;
                                processCalculateDistances();
                            }
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(VolleyError error) {

                    }
                };

                String params[];

                if (countProcessCalculateDistanciaRutas == 0) {
                    params = new String[]{
                            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLatitude() + "," +
                            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLongitude(),
                            rutas.get(countProcessCalculateDistanciaRutas).getGpsLatitude() + "," +
                                    rutas.get(countProcessCalculateDistanciaRutas).getGpsLongitude(),
                    };
                } else {
                    params = new String[]{
                            rutas.get(countProcessCalculateDistanciaRutas - 1).getGpsLatitude() + "," +
                                    rutas.get(countProcessCalculateDistanciaRutas - 1).getGpsLongitude(),
                            rutas.get(countProcessCalculateDistanciaRutas).getGpsLatitude() + "," +
                                    rutas.get(countProcessCalculateDistanciaRutas).getGpsLongitude(),
                    };
                }
                Log.d(TAG, "PARAMS: " + countProcessCalculateDistanciaRutas);
                googleMapsServiceApis.distanceMatrix(params, callback);
            } else {
                Log.d(TAG, "FAIL COORDS");
            }
        } else {
            Log.d(TAG, "notifyAllItemChanged");
            try {
                for(int i = 0; i < rutas.size(); i++) {
                    rutaItems.get(i).setHoraLlegadaEstimada(rutas.get(i).getHorarioEntrega());
                }
                //rutaPendienteView.notifyAllItemChanged();
            } catch (IndexOutOfBoundsException ex) {
                ex.printStackTrace();
            }
        }
    }

    // Validar que la primera ruta tenga coordenadas validas
    // para realizar el calculo
    private boolean hasCoordsFirstRuta() {
        if (isValidCoordRuta(0)) {
            return true;
        }
        return false;
    }

    private boolean isValidCoordRuta(int position) {
        if (rutas.size() > 0) {
            if (!rutas.get(position).getGpsLatitude().isEmpty() &&
                    !rutas.get(position).getGpsLongitude().isEmpty()) {
                if (Double.parseDouble(rutas.get(position).getGpsLatitude()) != 0 &&
                        Double.parseDouble(rutas.get(position).getGpsLongitude()) != 0) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean processResponceDistance(JSONObject data) throws JSONException {
        if (data.getString("status").equals("OK")) {
            JSONObject json = data.getJSONArray("rows").getJSONObject(0);
            json = json.getJSONArray("elements").getJSONObject(0);

            int segundosTiempoLLegada = 0;

            if (json.getString("status").equals("OK")) {
                segundosTiempoLLegada = json.getJSONObject("duration").getInt("value");
                String horaLlegada = calculateHoraLlegada(segundosTiempoLLegada);
                updateHoraLlegadaRuta(horaLlegada);
                return true;
            }
        }
        return false;
    }

    private String calculateHoraLlegada(int segundosTiempoLLegada) {
        Calendar calendar = Calendar.getInstance();

        if (countProcessCalculateDistanciaRutas == 0) {
            lastHoraLlegada = new Date();
        }

        calendar.setTime(lastHoraLlegada);

        calendar.set(Calendar.SECOND, calendar.get(Calendar.SECOND) + segundosTiempoLLegada);

        lastHoraLlegada = calendar.getTime();

        String formatHoraLlegada = new SimpleDateFormat("hh:mm").format(lastHoraLlegada);

        Log.d(TAG, "Hora Lleagda de la ruta " + countProcessCalculateDistanciaRutas + ": " + formatHoraLlegada);

        return formatHoraLlegada;
    }

    private void updateHoraLlegadaRuta(String horaLlegada) {
        try {
            rutas.get(countProcessCalculateDistanciaRutas).setHorarioEntrega(horaLlegada);
            rutas.get(countProcessCalculateDistanciaRutas).save();
        } catch (IndexOutOfBoundsException ex) {
            ex.printStackTrace();
        }
    }

}