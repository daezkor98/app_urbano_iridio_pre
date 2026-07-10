package com.urbanoexpress.iridio3.pre.data.sync;

import android.content.Context;
import android.util.Log;

import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.firebase.messaging.FirebaseMessaging;
import com.urbanoexpress.iridio3.pre.model.entity.Data;
import com.urbanoexpress.iridio3.pre.model.entity.EstadoRuta;
import com.urbanoexpress.iridio3.pre.model.entity.LogErrorSync;
import com.urbanoexpress.iridio3.pre.model.interactor.DataSyncInteractor;
import com.urbanoexpress.iridio3.pre.model.interactor.RutaPendienteInteractor;
import com.urbanoexpress.iridio3.pre.model.interactor.callback.RequestCallback;
import com.urbanoexpress.iridio3.pre.util.Session;
import com.urbanoexpress.iridio3.pre.util.network.Connectivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Date;

/**
 * Created by mick on 09/09/16.
 */

public class EstadoRutaSync extends DataSyncModel<EstadoRuta> {

    private static final String TAG = EstadoRutaSync.class.getSimpleName();

    private static EstadoRutaSync estadoRutaSync;

    private Context context;

    private DataSyncInteractor dataSyncInteractor;

    private String firebaseToken = "";
    private String parametrosEnviados = "";

    private EstadoRutaSync(Context context) {
        this.context = context;
        this.dataSyncInteractor = new DataSyncInteractor(context);

        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(s -> firebaseToken = s);
    }

    public static EstadoRutaSync getInstance(Context context) {
        if (estadoRutaSync == null) {
            estadoRutaSync = new EstadoRutaSync(context);
        }
        return estadoRutaSync;
    }

    @Override
    public void sync() {
        Log.d(TAG, "TOTAL ESTADO RUTA: " + getTotalData());
        Log.d(TAG, "INITIALIZED_DATA_SYNC: " + isSyncDone());
        if (isSyncDone() && Session.getUser() != null) {
            Log.d(TAG, "INIT SYNC ESTADO RUTA");
            setSyncDone(false);
            loadData();
            executeSync();
        }
    }

    @Override
    public void finishSync() {
        Log.d(TAG, "No hay registros de estados de ruta.");
        setCountData(0);
        setTotalData(0);
        setSyncDone(true);
    }

    @Override
    protected void executeSync() {
        Log.d(TAG, "EXECUTE SYNC ESTADO RUTA " + getCountData());
        if (Connectivity.isConnectedFast(context)) {
            if (getCountData() < getTotalData()) {
                RequestCallback callback = new RequestCallback() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        try {
                            if (response.getBoolean("success")) {
                                Log.d(TAG, "syncEstadoRuta UPLOADED");

                                getData().get(getCountData()).setDataSync(Data.Sync.SYNCHRONIZED);
                                getData().get(getCountData()).save();
                            } else {
                                Log.d(TAG, "syncEstadoRuta");
                                Log.d(TAG, "success: false");
                                LogErrorSync errorSync = new LogErrorSync(
                                        "1_"+TAG,
                                        Session.getUser().getIdUsuario(),
                                        LogErrorSync.Tipo.ESTADO_RUTA,
                                        "Error de servicio",
                                        response.getString("msg_error"),
                                        response.getString("code_error"),
                                        new Date().getTime() + ""
                                );
                                errorSync.save();
                            }
                            nextData();
                            executeSync();
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                            finishSync();
                            Log.d(TAG, "syncEstadoRuta");
                            Log.d(TAG, "JSONException");
                            LogErrorSync errorSync = new LogErrorSync(
                                    "2_"+TAG,
                                    Session.getUser().getIdUsuario(),
                                    LogErrorSync.Tipo.ESTADO_RUTA,
                                    "Error de conversión de datos",
                                    ex.getMessage(),
                                    Log.getStackTraceString(ex),
                                    new Date().getTime() + ""
                            );
                            errorSync.save();
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
                                "3_"+TAG,
                                Session.getUser().getIdUsuario(),
                                LogErrorSync.Tipo.ESTADO_RUTA,
                                "Error de conexión",
                                error.getMessage(),
                                parametrosEnviados,
//                                Log.getStackTraceString(error),
                                new Date().getTime() + ""
                        );
                        errorSync.save();
                    }
                };

                //if (PermissionUtils.checkPermissions(context, Manifest.permission.READ_PHONE_STATE)) {
                    String params[] = new String[]{
                            getData().get(getCountData()).getIdRuta(),
                            (getData().get(getCountData()).getEstado() == EstadoRuta.Estado.INICIADO)
                                    ? "1" : "2",
                            getData().get(getCountData()).getGpsLatitude(),
                            getData().get(getCountData()).getGpsLongitude(),
                            getData().get(getCountData()).getFecha(),
                            getData().get(getCountData()).getHora(),
                            "0",
                            getData().get(getCountData()).getLineaNegocio(),
                            firebaseToken,
                            "0",
                            getData().get(getCountData()).getIdUsuario(),
                            Session.getUser().getDevicePhone(),
                            Session.getUser().getFlag()
                    };

                    parametrosEnviados = Arrays.toString(params);

                    RutaPendienteInteractor.uploadEstadoRutaKilometraje(params, callback);
                /*} else {
                    finishSync();
                    LogErrorSync errorSync = new LogErrorSync(
                            Session.getUser().getIdUsuario(),
                            LogErrorSync.Tipo.ESTADO_RUTA,
                            "Error de permisos del dispositivo",
                            "No se tiene acceso al permiso Teléfono.",
                            "Manifest.permission.READ_PHONE_STATE",
                            new Date().getTime() + ""
                    );
                    errorSync.save();
                }*/
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
            setData(dataSyncInteractor.selectAllEstadoRutaSyncPending());
            setTotalData(getData().size());
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