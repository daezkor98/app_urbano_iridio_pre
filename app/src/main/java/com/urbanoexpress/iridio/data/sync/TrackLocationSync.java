package com.urbanoexpress.iridio.data.sync;

import android.content.Context;
import android.util.Log;

import com.android.volley.VolleyError;
import com.urbanoexpress.iridio.model.entity.Data;
import com.urbanoexpress.iridio.model.entity.LogErrorSync;
import com.urbanoexpress.iridio.model.entity.TrackLocation;
import com.urbanoexpress.iridio.model.interactor.DataSyncInteractor;
import com.urbanoexpress.iridio.model.interactor.callback.RequestCallback;
import com.urbanoexpress.iridio.util.Session;
import com.urbanoexpress.iridio.util.network.Connectivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by mick on 09/09/16.
 */

public class TrackLocationSync extends DataSyncModel<TrackLocation> {

    private static final String TAG = "TrackLocationSync";

    private static TrackLocationSync trackLocationSync;

    private Context context;

    private DataSyncInteractor dataSyncInteractor;

    private int countGroupData = 0;
    private int countDataSync = 0;

    private TrackLocationSync(Context context) {
        this.context = context;
        this.dataSyncInteractor = new DataSyncInteractor(context);
    }

    public static TrackLocationSync getInstance(Context context) {
        if (trackLocationSync == null) {
            trackLocationSync = new TrackLocationSync(context);
        }
        return trackLocationSync;
    }

    @Override
    public void sync() {
        Log.d(TAG, "TOTAL GPS: " + getTotalData());
        Log.d(TAG, "INITIALIZED_DATA_SYNC: " + isSyncDone());
        if (isSyncDone() && Session.getUser() != null) {
            Log.d(TAG, "INIT SYNC TRACK");
            setSyncDone(false);
            loadData();
            executeSync();
        }
    }

    @Override
    public void finishSync() {
        Log.d(TAG, "No hay registros de gps.");
        setCountData(0);
        setTotalData(0);
        setSyncDone(true);
        countGroupData = 0;
        countDataSync = 0;
    }

    @Override
    protected void executeSync() {
        Log.d(TAG, "EXECUTE SYNC TRACK " + getCountData());
        if (Connectivity.isConnectedFast(context)) {
            if (getCountData() < getTotalData()) {
                RequestCallback callback = new RequestCallback() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        try {
                            if (response.getBoolean("success")) {
                                while (countDataSync <= countGroupData) {
                                    getData().get(countDataSync).setDataSync(Data.Sync.SYNCHRONIZED);
                                    getData().get(countDataSync).save();
                                    countDataSync++;
                                }
                            } else {
                                LogErrorSync errorSync = new LogErrorSync(
                                        getData().get(getCountData()).getIdUsuario(),
                                        LogErrorSync.Tipo.GPS,
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
                            LogErrorSync errorSync = new LogErrorSync(
                                    getData().get(getCountData()).getIdUsuario(),
                                    LogErrorSync.Tipo.GPS,
                                    "Error de conversión de datos",
                                    ex.getMessage(),
                                    Log.getStackTraceString(ex),
                                    new Date().getTime() + ""
                            );
                            errorSync.save();
                            finishSync();
                        }
                    }

                    @Override
                    public void onError(VolleyError error) {
                        error.printStackTrace();
                        LogErrorSync errorSync = new LogErrorSync(
                                getData().get(getCountData()).getIdUsuario(),
                                LogErrorSync.Tipo.GPS,
                                "Error de conexión",
                                error.getMessage(),
                                Log.getStackTraceString(error),
                                new Date().getTime() + ""
                        );
                        errorSync.save();
                        finishSync();
                    }
                };

                try {
                    JSONArray jsonArrayGPS = new JSONArray();

                    for (int i = 0; i < 100; i++) {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("vp_route",          getData().get(countGroupData).getIdRuta());
                        jsonObject.put("vp_pos_px",         getData().get(countGroupData).getGpsLatitude());
                        jsonObject.put("vp_pos_py",         getData().get(countGroupData).getGpsLongitude());
                        jsonObject.put("vp_fecha",          getData().get(countGroupData).getFecha());
                        jsonObject.put("vp_hora",           getData().get(countGroupData).getHora());
                        jsonObject.put("vp_bateria",        getData().get(countGroupData).getCelularBateria());
                        jsonObject.put("vp_exactitud",      getData().get(countGroupData).getGpsExactitud());
                        jsonObject.put("vp_tipo",           getData().get(countGroupData).getTipo() + "");
                        jsonObject.put("vp_linea_negocio",  getData().get(countGroupData).getLineaNegocio());
                        jsonArrayGPS.put(jsonObject);

                        if ((countGroupData + 1) == getData().size()) {
                            break;
                        }
                        countGroupData++;
                    }

                    String[] params = {
                            jsonArrayGPS.toString(),
                            Session.getUser().getDevicePhone(),
                            getData().get(getCountData()).getIdUsuario(),
                    };

                    dataSyncInteractor.uploadTrackLocation(params, callback);
                } catch (JSONException ex) {
                    ex.printStackTrace();
                    LogErrorSync errorSync = new LogErrorSync(
                            getData().get(getCountData()).getIdUsuario(),
                            LogErrorSync.Tipo.GPS,
                            "Error de empaquetado de datos",
                            ex.getMessage(),
                            Log.getStackTraceString(ex),
                            new Date().getTime() + ""
                    );
                    errorSync.save();
                    finishSync();
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
            setData(dataSyncInteractor.selectAllPendingTrackLocation());
            //setTotalData(getData().size());
            Log.d(TAG, "TOTAL DATA: " + (int) Math.ceil((double) getData().size() / 100));
            setTotalData((int) Math.ceil((double) getData().size() / 100));
        }
    }
}