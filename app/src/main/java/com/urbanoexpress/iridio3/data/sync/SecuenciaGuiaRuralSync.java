package com.urbanoexpress.iridio3.data.sync;

import android.content.Context;
import android.util.Log;

import com.android.volley.VolleyError;
import com.urbanoexpress.iridio3.model.entity.Data;
import com.urbanoexpress.iridio3.model.entity.LogErrorSync;
import com.urbanoexpress.iridio3.model.entity.Ruta;
import com.urbanoexpress.iridio3.model.entity.SecuenciaRuta;
import com.urbanoexpress.iridio3.model.interactor.DataSyncInteractor;
import com.urbanoexpress.iridio3.model.interactor.callback.RequestCallback;
import com.urbanoexpress.iridio3.util.Session;
import com.urbanoexpress.iridio3.util.network.Connectivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class SecuenciaGuiaRuralSync extends DataSyncModel<Ruta> {

    private static final String TAG = "SecuenciaGuiaRuralSync";

    private static SecuenciaGuiaRuralSync SecuenciaGuiaRuralSync;

    private Context context;

    private DataSyncInteractor dataSyncInteractor;

    private List<SecuenciaRuta> secuenciaRutas = Collections.emptyList();

    private int dataGroupCounter = 0;

    private SecuenciaGuiaRuralSync(Context context) {
        this.context = context;
        this.dataSyncInteractor = new DataSyncInteractor(context);
    }

    public static SecuenciaGuiaRuralSync getInstance(Context context) {
        if (SecuenciaGuiaRuralSync == null) {
            SecuenciaGuiaRuralSync = new SecuenciaGuiaRuralSync(context);
        }
        return SecuenciaGuiaRuralSync;
    }

    @Override
    public void sync() {
        Log.d(TAG, "TOTAL SECUENCIA GUIA: " + getTotalData());
        Log.d(TAG, "INITIALIZED_DATA_SYNC: " + isSyncDone());
        if (isSyncDone() && Session.getUser() != null) {
            Log.d(TAG, "INIT SYNC SECUENCIA GUIA");
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
        dataGroupCounter = 0;
    }

    @Override
    protected void executeSync() {
        Log.d(TAG, "EXECUTE SYNC SECUENCIA GUIAS " + getCountData());
        Log.d(TAG, "EXECUTE SYNC SECUENCIA GUIAS " + getTotalData());
        if (Connectivity.isConnectedFast(context)) {
            if (isSecuenciaRutaPending()) {
                if (getCountData() < getTotalData()) {
                    RequestCallback callback = new RequestCallback() {
                        @Override
                        public void onSuccess(JSONObject response) {
                            try {
                                if (response.getBoolean("success")) {
                                    Log.d(TAG, "SecuenciaGuiaSync UPLOADED");
                                } else {
                                    Log.d(TAG, "SecuenciaGuiaSync");
                                    Log.d(TAG, "success: false");
                                    LogErrorSync errorSync = new LogErrorSync(
                                            "1_"+TAG,
                                            Session.getUser().getIdUsuario(),
                                            LogErrorSync.Tipo.SECUENCIA_GUIA,
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
                                Log.d(TAG, "SecuenciaGuiaSync");
                                Log.d(TAG, "JSONException");
                                LogErrorSync errorSync = new LogErrorSync(
                                        "2_"+TAG,
                                        Session.getUser().getIdUsuario(),
                                        LogErrorSync.Tipo.SECUENCIA_GUIA,
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
                                    LogErrorSync.Tipo.SECUENCIA_GUIA,
                                    "Error de conexión",
                                    error.getMessage(),
                                    Log.getStackTraceString(error),
                                    new Date().getTime() + ""
                            );
                            errorSync.save();
                        }
                    };

                    JSONArray jsonArraySecuencia = new JSONArray();

                    try {
                        for (int i = 0; i < 100; i++) {
                            JSONObject json = new JSONObject();

                            LocalDateTime localDateTime = LocalDateTime.ofInstant(
                                    Instant.ofEpochMilli(getData().get(dataGroupCounter)
                                            .getHorarioOrdenamiento()),
                                    ZoneId.systemDefault());

                            json.put("vp_doc_id", getData().get(dataGroupCounter).getIdServicio());
                            json.put("vp_fecha", DateTimeFormatter.ofPattern("dd/MM/yyyy").format(localDateTime));
                            json.put("vp_hora", DateTimeFormatter.ofPattern("HH:mm:ss").format(localDateTime));
                            json.put("vp_linea_negocio", getData().get(dataGroupCounter).getLineaNegocio());

                            jsonArraySecuencia.put(json);

                            if ((dataGroupCounter + 1) == getData().size()) {
                                break;
                            }
                            dataGroupCounter++;
                        }
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }

                    String[] params = new String[] {
                            jsonArraySecuencia.toString(),
                            Session.getUser().getDevicePhone(),
                            Session.getUser().getIdUsuario()
                    };

                    dataSyncInteractor.uploadSecuenciaRutaRural(params, callback);
                } else {
                    Log.d(TAG, "FINISH SYNC DATOS SECUENCIA");
                    deleteSecuencia();
                    finishSync();
                }
            } else {
                Log.d(TAG, "NO HAY REGISTRO DE SECUENCIA RUTA");
                finishSync();
            }
        } else {
            Log.d(TAG, "LA CONEXIÓN NO ES RAPIDA!!!");
            finishSync();
        }
    }

    private void deleteSecuencia() {
        for (int i = 0; i < secuenciaRutas.size(); i++) {
            secuenciaRutas.get(i).setEliminado(Data.Delete.YES);
            secuenciaRutas.get(i).save();
        }
    }

    private boolean isSecuenciaRutaPending() {
        if (secuenciaRutas.size() > 0) {
            return true;
        }
        return false;
    }

    @Override
    public void loadData() {
        secuenciaRutas = dataSyncInteractor.selectAllSecuenciaRuta();
        setData(dataSyncInteractor.selectAllRutaPendiente(Ruta.ZONA.RURAL));
        setTotalData((int) Math.ceil((double) getData().size() / 100));
    }

}