package com.urbanoexpress.iridio3.pe.data.sync;

import android.content.Context;
import android.util.Log;

import com.android.volley.VolleyError;
import com.google.firebase.messaging.FirebaseMessaging;
import com.urbanoexpress.iridio3.pe.model.entity.Data;
import com.urbanoexpress.iridio3.pe.model.entity.EstadoRuta;
import com.urbanoexpress.iridio3.pe.model.entity.LogErrorSync;
import com.urbanoexpress.iridio3.pe.model.interactor.DataSyncInteractor;
import com.urbanoexpress.iridio3.pe.model.interactor.RutaPendienteInteractor;
import com.urbanoexpress.iridio3.pe.model.interactor.callback.RequestCallback;
import com.urbanoexpress.iridio3.pe.util.Session;
import com.urbanoexpress.iridio3.pe.util.network.Connectivity;

import org.json.JSONException;
import org.json.JSONObject;

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
                        finishSync();
                        LogErrorSync errorSync = new LogErrorSync(
                                "3_"+TAG,
                                Session.getUser().getIdUsuario(),
                                LogErrorSync.Tipo.ESTADO_RUTA,
                                "Error de conexión",
                                error.getMessage(),
                                Log.getStackTraceString(error),
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
}