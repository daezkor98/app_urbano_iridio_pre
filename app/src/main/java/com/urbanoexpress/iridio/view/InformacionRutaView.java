package com.urbanoexpress.iridio.view;

/**
 * Created by mick on 11/01/17.
 */

public interface InformacionRutaView extends BaseView2 {

    void setTotalGuias(String totalGuias);
    void setTotalPendientes(String totalPendientes);
    void setTotalGestionados(String totalGestionados);
    void setTotalGestionadosFallidos(String total);
    void setProgressRoute(int progress);
    void setProgressGestiones(int progress);
    void setProgressImagenes(int progress);
    void setProgressLlamadas(int progress);
    void setProgressGPS(int progress);
    void setValueSyncGestiones(int value);
    void setValuePendingGestiones(int value);
    void setValueSyncImagenes(int value);
    void setValuePendingImagenes(int value);
    void setValueSyncLlamadas(int value);
    void setValuePendingLlamadas(int value);
    void setValueSyncGPS(int value);
    void setValuePendingGPS(int value);
    void hideSwipeRefreshLayout();
}
