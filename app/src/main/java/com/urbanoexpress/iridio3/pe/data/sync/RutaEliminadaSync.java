package com.urbanoexpress.iridio3.pe.data.sync;

import android.content.Context;

import com.urbanoexpress.iridio3.pe.model.entity.RutaEliminada;
import com.urbanoexpress.iridio3.pe.model.interactor.DataSyncInteractor;

/**
 * Created by mick on 09/09/16.
 */

public class RutaEliminadaSync extends DataSyncModel<RutaEliminada> {

    private static final String TAG = RutaEliminadaSync.class.getSimpleName();

    private static RutaEliminadaSync rutaEliminadaSync;

    private Context context;

    private DataSyncInteractor dataSyncInteractor;

    private RutaEliminadaSync(Context context) {
        this.context = context;
        this.dataSyncInteractor = new DataSyncInteractor(context);
    }

    public static RutaEliminadaSync getInstance(Context context) {
        if (rutaEliminadaSync == null) {
            rutaEliminadaSync = new RutaEliminadaSync(context);
        }
        return rutaEliminadaSync;
    }

    @Override
    public void sync() {

    }

    @Override
    public void finishSync() {

    }

    @Override
    protected void executeSync() {

    }

    @Override
    public void loadData() {
        if (dataSyncInteractor != null) {
            setData(dataSyncInteractor.selectAllRutaEliminada());
            setTotalData(getData().size());
        }
    }
}