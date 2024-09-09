package com.urbanoexpress.iridio3.pe.view;

import com.urbanoexpress.iridio3.pe.ui.model.AuditoriaItem;

import java.util.List;

public interface RutaAuditoriaGestionadaView extends BaseView {

    void showDatosAuditoriasGestionadas(List<AuditoriaItem> auditoriasPendientes);
    void setVisibilitySwipeRefreshLayout(boolean visible);
    
}
