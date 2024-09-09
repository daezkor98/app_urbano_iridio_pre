package com.urbanoexpress.iridio3.pe.data.sync;

import android.content.Context;
import android.util.Log;

import com.android.volley.VolleyError;
import com.urbanoexpress.iridio3.pe.model.entity.Data;
import com.urbanoexpress.iridio3.pe.model.entity.LogErrorSync;
import com.urbanoexpress.iridio3.pe.model.entity.GuiaGestionada;
import com.urbanoexpress.iridio3.pe.model.interactor.DataSyncInteractor;
import com.urbanoexpress.iridio3.pe.model.interactor.callback.RequestCallback;
import com.urbanoexpress.iridio3.pe.util.Session;
import com.urbanoexpress.iridio3.pe.util.network.Connectivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by mick on 09/09/16.
 */

public class GuiaGestionadaSync extends DataSyncModel<GuiaGestionada> {

    private static final String TAG = GuiaGestionadaSync.class.getSimpleName();

    private static GuiaGestionadaSync guiaGestionadaSync;

    private Context context;

    private DataSyncInteractor dataSyncInteractor;

    private GuiaGestionadaSync(Context context) {
        this.context = context;
        this.dataSyncInteractor = new DataSyncInteractor(context);
    }

    public static GuiaGestionadaSync getInstance(Context context) {
        if (guiaGestionadaSync == null) {
            guiaGestionadaSync = new GuiaGestionadaSync(context);
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
                                getData().get(getCountData()).setDataSync(Data.Sync.SYNCHRONIZED);
                                getData().get(getCountData()).save();
                            } else {
                                LogErrorSync errorSync = new LogErrorSync(
                                        "1_"+TAG,
                                        getData().get(getCountData()).getIdUsuario(),
                                        LogErrorSync.Tipo.GUIA_GESTIONADA,
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
                            LogErrorSync errorSync = new LogErrorSync(
                                    "2_"+TAG,
                                    getData().get(getCountData()).getIdUsuario(),
                                    LogErrorSync.Tipo.GUIA_GESTIONADA,
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
                                "3_"+TAG,
                                getData().get(getCountData()).getIdUsuario(),
                                LogErrorSync.Tipo.GUIA_GESTIONADA,
                                "Error de conexión",
                                error.getMessage(),
                                Log.getStackTraceString(error),
                                new Date().getTime() + ""
                        );
                        errorSync.save();
                        finishSync();
                    }
                };

                String tipoMedioPago = "0", idMotivoObservacionEntrega = "0", comentarioObservacionEntrega = "";

                if (getData().get(getCountData()).getTipoMedioPago() != null) {
                    tipoMedioPago = getData().get(getCountData()).getTipoMedioPago();
                }

                if (getData().get(getCountData()).getIdMotivoObservacionEntrega() != null) {
                    idMotivoObservacionEntrega = getData().get(getCountData()).getIdMotivoObservacionEntrega();
                }

                if (getData().get(getCountData()).getComentarioObservacionEntrega() != null) {
                    comentarioObservacionEntrega = getData().get(getCountData()).getComentarioObservacionEntrega();
                }

                try {
                    String[] params = {
                            getData().get(getCountData()).getIdServicio(),
                            getData().get(getCountData()).getIdMotivo(),
                            getData().get(getCountData()).getFecha(),
                            getData().get(getCountData()).getHora(),
                            getData().get(getCountData()).getGpsLatitude(),
                            getData().get(getCountData()).getGpslongitude(),
                            getData().get(getCountData()).getNombre(),
                            getData().get(getCountData()).getDni(),
                            getData().get(getCountData()).getNumVaucherPOS(),
                            getData().get(getCountData()).getPiezas(),
                            getData().get(getCountData()).getPeso(),
                            getData().get(getCountData()).getGuiaElectronica(),
                            getData().get(getCountData()).getComentario(),
                            String.valueOf(getData().get(getCountData()).getNumVecesGestionado()),
                            getData().get(getCountData()).getRecoleccion(),
                            String.valueOf(getData().get(getCountData()).getTipoZona()),
                            getData().get(getCountData()).getTipoGuia(),
                            getData().get(getCountData()).getTipoDireccion(),
                            tipoMedioPago,
                            idMotivoObservacionEntrega,
                            comentarioObservacionEntrega,
                            getData().get(getCountData()).getLineaNegocio(),
                            getData().get(getCountData()).getIdUsuario(),
                            Session.getUser().getDevicePhone()
                    };

                    dataSyncInteractor.uploadGuiaGestionada(params, callback);
                } catch (NullPointerException ex) {
                    ex.printStackTrace();
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
            setData(dataSyncInteractor.selectAllRutaGestionada());
            setTotalData(getData().size());
        }
    }
}