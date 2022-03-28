package com.urbanoexpress.iridio3.view;

import com.urbanoexpress.iridio3.model.entity.Ruta;
import com.urbanoexpress.iridio3.ui.model.NotificacionRutaItem;

import java.util.ArrayList;

public interface NotificacionesRutaView extends BaseView2 {

    void displayNotificaciones(ArrayList<NotificacionRutaItem> items);
    void notifyItemChanged(int position);
    void navigateToDetalleRutaActivity(Ruta ruta);
    void setVisibilitySwipeRefreshLayout(boolean visible);
}
