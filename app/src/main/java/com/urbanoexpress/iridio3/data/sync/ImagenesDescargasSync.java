package com.urbanoexpress.iridio3.data.sync;

import android.content.Context;
import android.util.Log;

import com.android.volley.VolleyError;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.urbanoexpress.iridio3.model.entity.Data;
import com.urbanoexpress.iridio3.model.entity.Imagen;
import com.urbanoexpress.iridio3.model.entity.LogErrorSync;
import com.urbanoexpress.iridio3.model.interactor.DataSyncInteractor;
import com.urbanoexpress.iridio3.model.interactor.callback.RequestCallback;
import com.urbanoexpress.iridio3.util.FileUtils;
import com.urbanoexpress.iridio3.util.Session;
import com.urbanoexpress.iridio3.util.network.Connectivity;
import com.urbanoexpress.iridio3.util.network.volley.MultipartJsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by mick on 09/09/16.
 */

public class ImagenesDescargasSync extends DataSyncModel<Imagen> {

    private static final String TAG = ImagenesDescargasSync.class.getSimpleName();

    private static ImagenesDescargasSync imagenesDescargasSync;

    private Context context;

    private DataSyncInteractor dataSyncInteractor;

    private ImagenesDescargasSync(Context context) {
        this.context = context;
        this.dataSyncInteractor = new DataSyncInteractor(context);
    }

    public static ImagenesDescargasSync getInstance(Context context) {
        if (imagenesDescargasSync == null) {
            imagenesDescargasSync = new ImagenesDescargasSync(context);
        }
        return imagenesDescargasSync;
    }

    @Override
    public void sync() {
        Log.d(TAG, "TOTAL IMAGENES: " + getTotalData());
        Log.d(TAG, "INITIALIZED_DATA_SYNC: " + isSyncDone());
        if (isSyncDone() && Session.getUser() != null) {
            Log.d(TAG, "INIT SYNC IMAGENES");
            setSyncDone(false);
            loadData();
            executeSync();
        }
    }

    @Override
    public void finishSync() {
        Log.d(TAG, "No hay registros de imagenes de descargas.");
        setCountData(0);
        setTotalData(0);
        setSyncDone(true);
    }

    @Override
    protected void executeSync() {
        Log.d(TAG, "EXECUTE SYNC IMAGENES " + getCountData());
        if (Connectivity.isConnectedFast(context)) {
            if (getCountData() < getTotalData()) {
                RequestCallback callback = new RequestCallback() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        Log.i("_._"+TAG, "onSuccess: " + response);
                        try {
                            if (response.getBoolean("success")) {
                                getData().get(getCountData()).setDataSync(Data.Sync.SYNCHRONIZED);
                                getData().get(getCountData()).save();
                            } else {
                                LogErrorSync errorSync = new LogErrorSync(
                                        "1_"+TAG,
                                        getData().get(getCountData()).getIdUsuario(),
                                        LogErrorSync.Tipo.IMAGEN,
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
                                    "2_"+TAG,
                                    getData().get(getCountData()).getIdUsuario(),
                                    LogErrorSync.Tipo.IMAGEN,
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
                        Log.i("_._"+TAG, "onError: " + error.toString());
                        error.printStackTrace();
                        LogErrorSync errorSync = new LogErrorSync(
                                "3_"+TAG,
                                getData().get(getCountData()).getIdUsuario(),
                                LogErrorSync.Tipo.IMAGEN,
                                "Error de conexión",
                                error.getMessage(),
                                Log.getStackTraceString(error),
                                new Date().getTime() + ""
                        );
                        errorSync.save();
                        finishSync();
                    }
                };

                Date date = new Date(Long.parseLong(getData().get(getCountData()).getFechaCreacion()));
                String fecha = new SimpleDateFormat("dd/MM/yyyy").format(date);
                String hora = new SimpleDateFormat("HH:mm:ss").format(date);

                String[] params = {
                        getData().get(getCountData()).getIdSuperior(),
                        getData().get(getCountData()).getName(),
                        getTipoImagenDescarga(getData().get(getCountData()).getName()),
                        fecha,
                        hora,
                        getData().get(getCountData()).getGpsLatitude(),
                        getData().get(getCountData()).getGpsLongitude(),
                        getData().get(getCountData()).getIdUsuario(),
                        getData().get(getCountData()).getIdServiciosAdjuntos(),
                        getData().get(getCountData()).getLineaNegocio(),
                };

                byte[] data = FileUtils.readAllBytes(getData().get(getCountData()).getFullPath());

                Log.i("_._" +TAG, "executeSync: ImageData: " + data.length);

                if (data == null) {
                    FirebaseCrashlytics.getInstance().log("ImageWithNullData");
                    nextData();
                    executeSync();
                } else {
                    MultipartJsonObjectRequest.DataPart imagen =
                            new MultipartJsonObjectRequest.DataPart(
                                    getData().get(getCountData()).getName(), data, "image/png");

                    if (getData().get(getCountData()).getClasificacion() == Imagen.Tipo.GESTION_GUIA) {
                        dataSyncInteractor.uploadImagenDescarga(params, imagen, callback);
                    } else if (getData().get(getCountData()).getClasificacion() == Imagen.Tipo.PARADA_PROGRAMADA) {
                        dataSyncInteractor.uploadImagenParadaProgramada(params, imagen, callback);
                    }
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
            setData(dataSyncInteractor.selectAllImagenes());
            setTotalData(getData().size());
        }
    }

    private String getTipoImagenDescarga(String fileName) {
        Log.i(TAG, "getTipoImagenDescarga: " + fileName);
        if (fileName.contains("Imagen")) {
            return "1";
        } else if (fileName.contains("Firma")) {
            return "2";
        } else if (fileName.contains("Cargo")) {
            return "3";
        } else if (fileName.contains("Voucher")) {
            return "4";
        } else if (fileName.contains("Domicilio")) {
            return "7";
        } else if (fileName.contains("Observacion_Entrega")) {
            return "6";
        } else if (fileName.contains("ge_no_recolectada")) {
            return "9";
        } else if (fileName.contains("Pago")) {
            return "10";
        }
        return "0";
    }
}