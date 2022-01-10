package com.urbanoexpress.iridio.view;

import com.urbanoexpress.iridio.ui.model.AuditoriaItem;

import java.util.List;

public interface RutaAuditoriaGestionadaView extends BaseView {

    void showDatosAuditoriasGestionadas(List<AuditoriaItem> auditoriasPendientes);
    void setVisibilitySwipeRefreshLayout(boolean visible);
    
}
