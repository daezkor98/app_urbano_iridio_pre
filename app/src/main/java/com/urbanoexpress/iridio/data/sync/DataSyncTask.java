package com.urbanoexpress.iridio.data.sync;

import android.content.Context;
import android.util.Log;
import java.util.TimerTask;

/**
 * Created by mick on 02/08/16.
 */
public class DataSyncTask extends TimerTask {

    private static final String TAG = DataSyncTask.class.getSimpleName();

    private Context context;

    private int sync;

    public interface Sync {
        int DATA = 100;
        int NEWS_DATA = 200;
    }

    public DataSyncTask(Context context, int sync) {
        this.context = context;
        this.sync = sync;
    }

    @Override
    public void run() {
        sync();
    }

    private void sync() {
        Log.d(TAG, "INIT SYNC TYPE: " + ((sync == Sync.DATA) ? "DATA" : "GPS"));
        switch (sync) {
            case Sync.DATA:
                EstadoRutaSync.getInstance(context).sync();
                SecuenciaGuiaSync.getInstance(context).sync();
                SecuenciaGuiaRuralSync.getInstance(context).sync();
                GuiaGestionadaSync.getInstance(context).sync();
                ImagenesDescargasSync.getInstance(context).sync();
                TrackLocationSync.getInstance(context).sync();
                GestionLlamadaSync.getInstance(context).sync();
                IncidenteRutaSync.getInstance(context).sync();
                break;
            case Sync.NEWS_DATA:
                //NewVersionAppSync.getInstance(context).sync();
                //NuevasGuiasSync.getInstance(context).sync();
                EliminarGuiasPendientesSync.getInstance(context).sync();
                break;
        }
        Log.d(TAG, "END SYNC");
    }

}