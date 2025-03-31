package com.urbanoexpress.iridio3.pe.data.sync;

import android.content.Context;
import android.util.Log;

import com.android.volley.VolleyError;
import com.urbanoexpress.iridio3.pe.model.entity.Data;
import com.urbanoexpress.iridio3.pe.model.entity.LogErrorSync;
import com.urbanoexpress.iridio3.pe.model.entity.Ruta;
import com.urbanoexpress.iridio3.pe.model.entity.SecuenciaRuta;
import com.urbanoexpress.iridio3.pe.model.interactor.DataSyncInteractor;
import com.urbanoexpress.iridio3.pe.model.interactor.callback.RequestCallback;
import com.urbanoexpress.iridio3.pe.util.LocationUtils;
import com.urbanoexpress.iridio3.pe.util.Session;
import com.urbanoexpress.iridio3.pe.util.network.Connectivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by mick on 09/09/16.
 */

public class SecuenciaGuiaSync extends DataSyncModel<Ruta> {

    private static final String TAG = "SecuenciaGuiaSync";

    private static SecuenciaGuiaSync secuenciaGuiaSync;

    private Context context;

    private DataSyncInteractor dataSyncInteractor;

    private List<SecuenciaRuta> secuenciaRutas = Collections.emptyList();

    private int countGroupData = 0;
    private int totalGuiasGestionadas = 0;
    private long totalEstadoRutas = 0;

    private SecuenciaGuiaSync(Context context) {
        this.context = context;
        this.dataSyncInteractor = new DataSyncInteractor(context);
    }

    public static SecuenciaGuiaSync getInstance(Context context) {
        if (secuenciaGuiaSync == null) {
            secuenciaGuiaSync = new SecuenciaGuiaSync(context);
        }
        return secuenciaGuiaSync;
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
        countGroupData = 0;
        totalGuiasGestionadas = 0;
    }

    @Override
    protected void executeSync() {
        Log.d(TAG, "EXECUTE SYNC SECUENCIA GUIAS " + getCountData());
        Log.d(TAG, "EXECUTE SYNC SECUENCIA GUIAS " + getTotalData());
        if (Connectivity.isConnectedFast(context)) {
            if (totalEstadoRutas > 0) {
                if (isSecuenciaRutaPending()) {
                    if (getCountData() < getTotalData()) {
                        RequestCallback callback = new RequestCallback() {
                            @Override
                            public void onSuccess(JSONObject response) {
                                try {
                                    if (response.getBoolean("success")) {
                                        Log.d(TAG, "SecuenciaGuiaSync UPLOADED");

//                                    JSONArray data = response.getJSONArray("data");
//
//                                    getData().get(getCountData()).setHorario(data.getJSONObject(0).getString("hora"));
//                                    getData().get(getCountData()).save();
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

                        //if (PermissionUtils.checkPermissions(context, Manifest.permission.READ_PHONE_STATE)) {
                            if (LocationUtils.getCurrentLocation() != null) {
                                JSONArray jsonArraySecuencia = new JSONArray();

                                try {
                                    for (int i = 0; i < 100; i++) {
                                        JSONObject jsonObjectSecuencia = new JSONObject();
                                        Date horarioAproximado = new Date(getData().get(countGroupData).getHorarioAproximado());
                                        Date horarioOrdenamiento = new Date(getData().get(countGroupData).getHorarioOrdenamiento());
                                        int secuencia = Integer.parseInt(getData().get(countGroupData).getSecuencia()) + totalGuiasGestionadas;

                                        jsonObjectSecuencia.put("vp_doc_id", getData().get(countGroupData).getIdServicio());
                                        jsonObjectSecuencia.put("vp_fecha", new SimpleDateFormat("dd/MM/yyyy").format(horarioAproximado));
                                        jsonObjectSecuencia.put("vp_hora", new SimpleDateFormat("HH:mm:ss").format(horarioAproximado));
                                        jsonObjectSecuencia.put("vp_secuencia", secuencia);
                                        jsonObjectSecuencia.put("vp_fecha_ordenamiento", new SimpleDateFormat("dd/MM/yyyy").format(horarioOrdenamiento));
                                        jsonObjectSecuencia.put("vp_hora_ordenamiento", new SimpleDateFormat("HH:mm:ss").format(horarioOrdenamiento));
                                        jsonObjectSecuencia.put("vp_linea_negocio", getData().get(countGroupData).getLineaNegocio());

                                        jsonArraySecuencia.put(jsonObjectSecuencia);

                                        if ((countGroupData + 1) == getData().size()) {
                                            break;
                                        }
                                        countGroupData++;
                                    }
                                } catch (JSONException ex) {
                                    ex.printStackTrace();
                                }

                                String params[] = new String[] {
                                        jsonArraySecuencia.toString(),
                                        "0",
                                        "0",
                                        Session.getUser().getDevicePhone(),
                                        Session.getUser().getIdUsuario(),
                                        Session.getUser().getFlag(),
                                };

                                dataSyncInteractor.uploadSecuenciaRuta(params, callback);
                            } else {
                                Log.d(TAG, "NO HAY GPS SECUENCIA");
                                finishSync();
                            }
                        /*} else {
                            finishSync();
                            LogErrorSync errorSync = new LogErrorSync(
                                    Session.getUser().getIdUsuario(),
                                    LogErrorSync.Tipo.SECUENCIA_GUIA,
                                    "Error de permisos del dispositivo",
                                    "No se tiene acceso al permiso Teléfono.",
                                    "Manifest.permission.READ_PHONE_STATE",
                                    new Date().getTime() + ""
                            );
                            errorSync.save();
                        }*/
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
                Log.d(TAG, "LA RUTA NO HA SIDO INICIADA");
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
        setData(dataSyncInteractor.selectAllRutaPendiente(Ruta.ZONA.URBANO));
        secuenciaRutas = dataSyncInteractor.selectAllSecuenciaRuta();
        totalGuiasGestionadas = (int) dataSyncInteractor.getTotalRutasGestionadas();
        totalEstadoRutas = dataSyncInteractor.getTotalAllEstadoRuta();
        Log.d(TAG, "TOTAL DATA: " + (int) Math.ceil((double) getData().size() / 100));
        setTotalData((int) Math.ceil((double) getData().size() / 100));
//        setTotalData(getData().size());
//        Log.d(TAG, "ID USUARIO: " + Session.getUser().getIdUsuario());
//        Log.d(TAG, "TOTAL DATA: " + getData().size());
//        Log.d(TAG, "TOTAL DATA SECUENCIA: " + secuenciaRutas.size());
    }

}