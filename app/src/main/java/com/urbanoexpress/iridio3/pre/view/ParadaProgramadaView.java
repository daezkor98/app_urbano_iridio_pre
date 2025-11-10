package com.urbanoexpress.iridio3.pre.view;

import java.util.List;

import com.urbanoexpress.iridio3.pre.ui.model.DespachoItem;

/**
 * Created by mick on 02/06/16.
 */
public interface ParadaProgramadaView extends BaseView2 {

    void showDatosDespachoBajadas(List<DespachoItem> despachoBajadas);
    void showDatosDespachoSubidas(List<DespachoItem> despachoSubidas);
    void showBoxInfo();
    void showActionMode(int menuActionMode);
    void hideActionMode();
    void setTitleActionMode(String title);
    void setTitleActivity(String title);

    void setVisibilitySwipeRefreshLayout(boolean visible);

    void navigateToGaleriaParadaProgramadaModal(String idPlanDeViaje, String idParadaProgramada,
                                           int paradaProgramadaEstadoLlegada);
}
