package com.urbanoexpress.iridio3.data.sync;

import android.content.Context;
import android.util.Log;

import com.android.volley.VolleyError;
import com.urbanoexpress.iridio3.model.entity.Data;
import com.urbanoexpress.iridio3.model.entity.GestionLlamada;
import com.urbanoexpress.iridio3.model.entity.LogErrorSync;
import com.urbanoexpress.iridio3.model.interactor.DataSyncInteractor;
import com.urbanoexpress.iridio3.model.interactor.callback.RequestCallback;
import com.urbanoexpress.iridio3.util.Session;
import com.urbanoexpress.iridio3.util.network.Connectivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class GestionLlamadaSync extends DataSyncModel<GestionLlamada> {

    private static final String TAG = "GestionLlamadaSync";

    private static GestionLlamadaSync gestionLlamadaSync;

    private Context context;

    private DataSyncInteractor dataSyncInteractor;

    private int countGroupData = 0;
    private int countDataSync = 0;

    private GestionLlamadaSync(Context context) {
        this.context = context;
        this.dataSyncInteractor = new DataSyncInteractor(context);
    }

    public static GestionLlamadaSync getInstance(Context context) {
        if (gestionLlamadaSync == null) {
            gestionLlamadaSync = new GestionLlamadaSync(context);
        }
        return gestionLlamadaSync;
    }

    @Override
    public void sync() {
        Log.d(TAG, "TOTAL GESTION LLAMADA: " + getTotalData());
        Log.d(TAG, "INITIALIZED_DATA_SYNC: " + isSyncDone());
        if (isSyncDone() && Session.getUser() != null) {
            Log.d(TAG, "INIT SYNC GESTION LLAMADA");
            setSyncDone(false);
            loadData();
            executeSync();
        }
    }

    @Override
    public void finishSync() {
        Log.d(TAG, "No hay registros de gestion de llamada.");
        setCountData(0);
        setTotalData(0);
        setSyncDone(true);
        countGroupData = 0;
        countDataSync = 0;
    }

    @Override
    protected void executeSync() {
        Log.d(TAG, "EXECUTE SYNC GESTION LLAMADA " + getCountData());
        if (Connectivity.isConnectedFast(context)) {
            if (getCountData() < getTotalData()) {
                RequestCallback callback = new RequestCallback() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        try {
                            if (response.getBoolean("success")) {
                                Log.d(TAG, "gestionLlamadaSync UPLOADED");
                                while (countDataSync <= countGroupData) {
                                    getData().get(countDataSync).setDataSync(Data.Sync.SYNCHRONIZED);
                                    getData().get(countDataSync).save();
                                    countDataSync++;
                                }
                            } else {
                                LogErrorSync errorSync = new LogErrorSync(
                                        Session.getUser().getIdUsuario(),
                                        LogErrorSync.Tipo.GESTION_LLAMADA,
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
                            LogErrorSync errorSync = new LogErrorSync(
                                    Session.getUser().getIdUsuario(),
                                    LogErrorSync.Tipo.GESTION_LLAMADA,
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
                                LogErrorSync.Tipo.GESTION_LLAMADA,
                                "Error de conexión",
                                error.getMessage(),
                                Log.getStackTraceString(error),
                                new Date().getTime() + ""
                        );
                        errorSync.save();
                    }
                };

                //if (PermissionUtils.checkPermissions(context, Manifest.permission.READ_PHONE_STATE)) {
                    try {
                        JSONArray jsonArrayLlamadas = new JSONArray();

                        for (int i = 0; i < 100; i++) {
                            Date date = new Date(Long.parseLong(getData().get(getCountData()).getFechaLlamada()));
                            String horaInicioLlamada = new SimpleDateFormat("HH:mm:ss").format(date);

                            String horaFinLlamada = "";
                            if (getData().get(getCountData()).getTiempoLlamada().length() > 4) {
                                date = new Date(Long.parseLong(getData().get(getCountData()).getTiempoLlamada()));
                                horaFinLlamada = new SimpleDateFormat("HH:mm:ss").format(date);
                            } else {
                                horaFinLlamada = getData().get(getCountData()).getTiempoLlamada();
                            }

                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("vp_doc_id",         getData().get(countGroupData).getIdServicio());
                            jsonObject.put("vp_mot_id",         getData().get(countGroupData).getIdMotivo());
                            jsonObject.put("vp_telefono",       getData().get(countGroupData).getTelefono());
                            jsonObject.put("vp_fecha_llamada",  horaInicioLlamada);
                            jsonObject.put("vp_tiempo_llamada", horaFinLlamada);
                            jsonObject.put("vp_linea_negocio",  getData().get(countGroupData).getLineaNegocio());
                            jsonArrayLlamadas.put(jsonObject);

                            if ((countGroupData + 1) == getData().size()) {
                                break;
                            }
                            countGroupData++;
                        }

                        String params[] = new String[]{
                                jsonArrayLlamadas.toString(),
                                Session.getUser().getDevicePhone(),
                                getData().get(getCountData()).getIdUsuario(),
                        };

                        dataSyncInteractor.uploadGestionLlamada(params, callback);
                    }
                    catch (JSONException ex) {
                        ex.printStackTrace();
                        finishSync();
                        LogErrorSync errorSync = new LogErrorSync(
                                Session.getUser().getIdUsuario(),
                                LogErrorSync.Tipo.GESTION_LLAMADA,
                                "Error de empaquetado de datos",
                                ex.getMessage(),
                                Log.getStackTraceString(ex),
                                new Date().getTime() + ""
                        );
                        errorSync.save();
                    }
                /*} else {
                    finishSync();
                    LogErrorSync errorSync = new LogErrorSync(
                            Session.getUser().getIdUsuario(),
                            LogErrorSync.Tipo.GESTION_LLAMADA,
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
            setData(dataSyncInteractor.selectAllPendingGestionLlamada());
            setTotalData((int) Math.ceil((double) getData().size() / 100));
        }
    }
}