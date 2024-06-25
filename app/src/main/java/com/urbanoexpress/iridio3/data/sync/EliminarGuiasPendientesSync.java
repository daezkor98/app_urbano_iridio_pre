package com.urbanoexpress.iridio3.data.sync;

import android.content.Context;
import android.util.Log;

import com.android.volley.VolleyError;
import com.urbanoexpress.iridio3.AsyncTaskCoroutine;
import com.urbanoexpress.iridio3.model.entity.DescargaRuta;
import com.urbanoexpress.iridio3.model.entity.LogErrorSync;
import com.urbanoexpress.iridio3.model.entity.Pieza;
import com.urbanoexpress.iridio3.model.entity.Ruta;
import com.urbanoexpress.iridio3.model.interactor.DataSyncInteractor;
import com.urbanoexpress.iridio3.model.interactor.RutaPendienteInteractor;
import com.urbanoexpress.iridio3.model.interactor.callback.RequestCallback;
import com.urbanoexpress.iridio3.util.Session;
import com.urbanoexpress.iridio3.util.network.Connectivity;

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
}