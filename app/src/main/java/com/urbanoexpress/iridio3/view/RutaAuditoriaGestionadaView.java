package com.urbanoexpress.iridio3.view;

import com.urbanoexpress.iridio3.ui.model.AuditoriaItem;

import java.util.List;

public interface RutaAuditoriaGestionadaView extends BaseView {

    void showDatosAuditoriasGestionadas(List<AuditoriaItem> auditoriasPendientes);
    void setVisibilitySwipeRefreshLayout(boolean visible);
    
}
