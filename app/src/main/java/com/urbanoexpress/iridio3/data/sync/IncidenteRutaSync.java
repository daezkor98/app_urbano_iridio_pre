package com.urbanoexpress.iridio3.data.sync;

import android.content.Context;
import android.util.Log;

import com.android.volley.VolleyError;
import com.urbanoexpress.iridio3.model.entity.Data;
import com.urbanoexpress.iridio3.model.entity.IncidenteRuta;
import com.urbanoexpress.iridio3.model.entity.LogErrorSync;
import com.urbanoexpress.iridio3.model.interactor.DataSyncInteractor;
import com.urbanoexpress.iridio3.model.interactor.callback.RequestCallback;
import com.urbanoexpress.iridio3.util.FileUtils;
import com.urbanoexpress.iridio3.util.Session;
import com.urbanoexpress.iridio3.util.network.Connectivity;
import com.urbanoexpress.iridio3.util.network.volley.MultipartJsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class IncidenteRutaSync extends DataSyncModel<IncidenteRuta> {

    private static final String TAG = IncidenteRutaSync.class.getSimpleName();

    private static IncidenteRutaSync guiaGestionadaSync;

    private Context context;

    private DataSyncInteractor dataSyncInteractor;

    private IncidenteRutaSync(Context context) {
        this.context = context;
        this.dataSyncInteractor = new DataSyncInteractor(context);
    }

    public static IncidenteRutaSync getInstance(Context context) {
        if (guiaGestionadaSync == null) {
            guiaGestionadaSync = new IncidenteRutaSync(context);
        }
        return guiaGestionadaSync;
    }

    @Override
    public void sync() {
        Log.d(TAG, "TOTAL RUTAS GESTIONADAS: " + getTotalData());
        Log.d(TAG, "INITIALIZED_DATA_SYNC: " + isSyncDone());
        if (isSyncDone() && Session.getUser() != null) {
            Log.d(TAG, "INIT SYNC RUTAS GESTIONADAS");
            setSyncDone(false);
            loadData();
            executeSync();
        }
    }

    @Override
    public void finishSync() {
        Log.d(TAG, "No hay registros de rutas gestionadas.");
        setCountData(0);
        setTotalData(0);
        setSyncDone(true);
    }

    @Override
    protected void executeSync() {
        Log.d(TAG, "EXECUTE SYNC RUTAS GESTIONADAS " + getCountData());
        if (Connectivity.isConnectedFast(context)) {
            if (getCountData() < getTotalData()) {
                RequestCallback callback = new RequestCallback() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        try {
                            if (response.getBoolean("success")) {
                                Log.d(TAG, "syncRutasGestionadas UPLOADED");

                                getData().get(getCountData()).setDataSync(Data.Sync.SYNCHRONIZED);
                                getData().get(getCountData()).save();
                            } else {
                                Log.d(TAG, "syncRutasGestionadas");
                                Log.d(TAG, "success: false");
                                LogErrorSync errorSync = new LogErrorSync(
                                        Session.getUser().getIdUsuario(),
                                        LogErrorSync.Tipo.INCIDENTE_RUTA,
                                        "Error de servicio",
                                        response.getString("msg_error") + "\n" +
                                                (response.has("sql_query") ? response.getString("msg_error") : ""),
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
                            Log.d(TAG, "syncRutasGestionadas");
                            Log.d(TAG, "JSONException");
                            LogErrorSync errorSync = new LogErrorSync(
                                    Session.getUser().getIdUsuario(),
                                    LogErrorSync.Tipo.INCIDENTE_RUTA,
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
                                Session.getUser().getIdUsuario(),
                                LogErrorSync.Tipo.INCIDENTE_RUTA,
                                "Error de conexión",
                                error.getMessage(),
                                Log.getStackTraceString(error),
                                new Date().getTime() + ""
                        );
                        errorSync.save();
                    }
                };

                String[] params = new String[]{
                        getData().get(getCountData()).getIdRuta(),
                        getData().get(getCountData()).getIdMotivoIncidente(),
                        getData().get(getCountData()).getImageName(),
                        getTipoImagenDescarga(getData().get(getCountData()).getImageName()),
                        getData().get(getCountData()).getFecha(),
                        getData().get(getCountData()).getHora(),
                        getData().get(getCountData()).getComentarios(),
                        getData().get(getCountData()).getGpsLatitude(),
                        getData().get(getCountData()).getGpsLongitude(),
                        getData().get(getCountData()).getLineaNegocio(),
                        getData().get(getCountData()).getIdUsuario(),
                        Session.getUser().getDevicePhone()
                };

                byte[] data = FileUtils.readAllBytes(getData().get(getCountData()).getFullPath());

                if (data == null) {
                    //getData().get(getCountData()).delete();
                    nextData();
                    executeSync();
                } else {
                    MultipartJsonObjectRequest.DataPart imagen =
                            new MultipartJsonObjectRequest.DataPart(
                                    getData().get(getCountData()).getImageName(), data, "image/png");

                    dataSyncInteractor.uploadIncidenteRuta(params, imagen, callback);
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
            setData(dataSyncInteractor.selectAllIncidentesRuta());
            setTotalData(getData().size());
        }
    }

    private String getTipoImagenDescarga(String fileName) {
        if (fileName.contains("Imagen")) {
            return "1";
        } else if (fileName.contains("Firma")) {
            return "2";
        } else if (fileName.contains("Cargo")) {
            return "3";
        } else if (fileName.contains("ge_no_recolectada")) {
            return "9";
        }
        return "0";
    }
}