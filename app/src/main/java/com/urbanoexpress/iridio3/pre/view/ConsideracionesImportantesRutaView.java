package com.urbanoexpress.iridio3.pre.view;

import com.urbanoexpress.iridio3.pre.ui.model.RutaItem;

import java.util.List;

public interface ConsideracionesImportantesRutaView extends BaseView2 {

    void showDatosRutas(List<RutaItem> rutasPendientes);
    void showTotalRecoleccionesExpress(long total);
    void showTotalGuiasRequerimiento(long total);
    void setVisibilitySwipeRefreshLayout(boolean visible);
}
