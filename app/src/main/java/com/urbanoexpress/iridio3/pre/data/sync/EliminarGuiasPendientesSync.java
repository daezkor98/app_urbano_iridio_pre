package com.urbanoexpress.iridio3.pre.data.sync;

import android.content.Context;
import android.util.Log;

import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.urbanoexpress.iridio3.pre.util.async.AsyncTaskCoroutine;
import com.urbanoexpress.iridio3.pre.model.entity.DescargaRuta;
import com.urbanoexpress.iridio3.pre.model.entity.LogErrorSync;
import com.urbanoexpress.iridio3.pre.model.entity.Pieza;
import com.urbanoexpress.iridio3.pre.model.entity.Ruta;
import com.urbanoexpress.iridio3.pre.model.interactor.DataSyncInteractor;
import com.urbanoexpress.iridio3.pre.model.interactor.RutaPendienteInteractor;
import com.urbanoexpress.iridio3.pre.model.interactor.callback.RequestCallback;
import com.urbanoexpress.iridio3.pre.util.Session;
import com.urbanoexpress.iridio3.pre.util.network.Connectivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;

public class EliminarGuiasPendientesSync extends DataSyncModel<Ruta> {

    private static final String TAG = EliminarGuiasPendientesSync.class.getSimpleName();

    private static EliminarGuiasPendientesSync eliminarGuiasPendientesSync;

    private Context context;

    private DataSyncInteractor dataSyncInteractor;

    private int countGroupData = 0;
    private int countDataSync = 0;

    private EliminarGuiasPendientesSync(Context context) {
        this.context = context;
        this.dataSyncInteractor = new DataSyncInteractor(context);
    }

    public static EliminarGuiasPendientesSync getInstance(Context context) {
        if (eliminarGuiasPendientesSync == null) {
            eliminarGuiasPendientesSync = new EliminarGuiasPendientesSync(context);
        }
        return eliminarGuiasPendientesSync;
    }

    @Override
    public void sync() {
        Log.d(TAG, "INIT ELIMINAR GUIAS PENDIENTES SYNC: " + isSyncDone());
        if (isSyncDone() && Session.getUser() != null) {
            setSyncDone(false);
            loadData();
            executeSync();
        }
    }

    @Override
    public void finishSync() {
        setCountData(0);
        setTotalData(0);
        setSyncDone(true);
        countGroupData = 0;
        countDataSync = 0;
    }

    @Override
    protected void executeSync() {
        if (Connectivity.isConnectedFast(context)) {
            if (getCountData() < getTotalData()) {
                RequestCallback callback = new RequestCallback() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        try {
                            if (response.getBoolean("success")) {
                                new EliminarGuiasTask(response.getJSONArray("data")).execute();
                            } else {
                                Log.d(TAG, "syncTrackLocation");
                                Log.d(TAG, "success: false");
                                /*LogErrorSync errorSync = new LogErrorSync(
                                        Session.getUser().getIdUsuario(),
                                        LogErrorSync.Tipo.GPS,
                                        "Error de servicio",
                                        response.getString("msg_error"),
                                        response.getString("code_error"),
                                        new Date().getTime() + ""
                                );
                                errorSync.save();*/

                                nextData();
                                executeSync();
                            }
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                            finishSync();
                            Log.d(TAG, "syncTrackLocation");
                            Log.d(TAG, "JSONException");
                            /*LogErrorSync errorSync = new LogErrorSync(
                                    Session.getUser().getIdUsuario(),
                                    LogErrorSync.Tipo.GPS,
                                    "Error de conversión de datos",
                                    ex.getMessage(),
                                    Log.getStackTraceString(ex),
                                    new Date().getTime() + ""
                            );
                            errorSync.save();*/
                        }
                    }

                    @Override
                    public void onError(VolleyError error) {
                        error.printStackTrace();

                        String errorType = getVolleyErrorType(error);

                        if ("NETWORK_ERROR".equals(errorType) || "TIMEOUT_ERROR".equals(errorType) ||
                                "NO_CONNECTION_ERROR".equals(errorType) || "SERVICE_UNAVAILABLE".equals(errorType) ||
                                "STREAM_ERROR".equals(errorType)) {
                            finishSync();
                            return;
                        }

                        finishSync();
                        LogErrorSync errorSync = new LogErrorSync(
                                TAG,
                                Session.getUser().getIdUsuario(),
                                LogErrorSync.Tipo.GPS,
                                "Error de conexión",
                                error.getMessage(),
                                Log.getStackTraceString(error),
                                new Date().getTime() + ""
                        );
                        errorSync.save();
                    }
                };

                try {
                    JSONArray jsonArray = new JSONArray();

                    for (int i = 0; i < 100; i++) {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("vp_doc_id",
                                getData().get(countGroupData).getIdServicio());
                        jsonObject.put("vp_linea_negocio",
                                getData().get(countGroupData).getLineaNegocio());
                        jsonArray.put(jsonObject);

                        if ((countGroupData + 1) == getData().size()) {
                            break;
                        }
                        countGroupData++;
                    }

                    String[] params = new String[]{
                            jsonArray.toString(),
                            Session.getUser().getDevicePhone(),
                            getData().get(getCountData()).getIdUsuario(),
                    };

                    dataSyncInteractor.verifyGuiasPendientesEliminadas(params, callback);
                } catch (JSONException | NullPointerException ex) {
                    ex.printStackTrace();
                    finishSync();
                    Log.d(TAG, "syncTrackLocation");
                    Log.d(TAG, "JSONException");
                    /*LogErrorSync errorSync = new LogErrorSync(
                            Session.getUser().getIdUsuario(),
                            LogErrorSync.Tipo.GPS,
                            "Error de empaquetado de datos",
                            ex.getMessage(),
                            Log.getStackTraceString(ex),
                            new Date().getTime() + ""
                    );
                    errorSync.save();*/
                }
            } else {
                finishSync();
            }
        } else {
            Log.d(TAG, "LA CONEXIÓN NO ES RAPIDA!!!");
            finishSync();
        }
    }

    @Override
    public void loadData() {
        if (dataSyncInteractor != null) {
            setData(dataSyncInteractor.selectAllRutaPendiente(Ruta.ZONA.RURAL));
            setTotalData((int) Math.ceil((double) getData().size() / 100));
        }
    }

    private class EliminarGuiasTask extends AsyncTaskCoroutine<String, String> {

        private JSONArray guias;

        public EliminarGuiasTask(JSONArray guias) {
            this.guias = guias;
        }

        @Override
        public String doInBackground(String... strings) {
            try {
                for (int i=0; i < guias.length(); i++) {
                    if (guias.getJSONObject(i).getString("estado").equalsIgnoreCase("S")) {
                        DescargaRuta descargaRuta = RutaPendienteInteractor.selectDescargaRuta(
                                getData().get(countDataSync).getIdServicio(),
                                getData().get(countDataSync).getLineaNegocio());
                        if (descargaRuta != null) {
                            descargaRuta.delete();
                        }

                        Ruta ruta = RutaPendienteInteractor.selectRuta(
                                getData().get(countDataSync).getIdServicio(),
                                getData().get(countDataSync).getLineaNegocio());
                        if (ruta != null) {
                            ruta.delete();
                        }

                        List<Pieza> piezas = RutaPendienteInteractor.selectPiezas(
                                getData().get(countDataSync).getIdServicio(),
                                getData().get(countDataSync).getLineaNegocio());
                        for (Pieza pieza: piezas) {
                            pieza.delete();
                        }
                    }
                    countDataSync++;
                }
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
            return null;
        }

        @Override
        public void onPostExecute(String s) {
            super.onPostExecute(s);
            nextData();
            executeSync();
        }
    }

    private String getVolleyErrorType(VolleyError error) {
        if (error instanceof TimeoutError) {
            return "TIMEOUT_ERROR";
        } else if (error instanceof NoConnectionError) {
            return "NO_CONNECTION_ERROR";
        } else if (error instanceof ServerError) {
            if (error.networkResponse != null) {
                int statusCode = error.networkResponse.statusCode;
                if (statusCode == 503) {
                    return "SERVICE_UNAVAILABLE";
                }
            }
            return "SERVER_ERROR";
        } else if (error instanceof NetworkError) {
            return "NETWORK_ERROR";
        } else {
            String message = error.getMessage();
            if (message != null) {
                message = message.toLowerCase();
                if (message.contains("timeout")) {
                    return "TIMEOUT_ERROR";
                } else if (message.contains("end of stream")
                        || message.contains("required settings preface")) {
                    // STREAM_ERROR: protocolo HTTP/2 interrumpido o handshake roto
                    return "STREAM_ERROR";
                } else if (message.contains("connection abort") ||
                           message.contains("connection reset") ||
                           message.contains("broken pipe") ||
                           message.contains("socket closed") ||
                           message.contains("unable to resolve host") ||
                           message.contains("no address associated") ||
                           message.contains("failed to connect") ||
                           message.contains("network is unreachable") ||
                           message.contains("ssl") ||
                           message.contains("handshake") ||
                           message.contains("trust anchor")) {
                    // Fallos puros de red / DNS / TLS — no son errores del API
                    return "NETWORK_ERROR";
                } else if (message.contains("connection refused")) {
                    return "NO_CONNECTION_ERROR";
                }
            }
            return "UNKNOWN_ERROR";
        }
    }
}