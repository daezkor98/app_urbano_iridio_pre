package com.urbanoexpress.iridio3.pre.view;

import java.util.List;

import com.urbanoexpress.iridio3.pre.ui.model.RutaItem;

/**
 * Created by mick on 27/07/16.
 */
public interface RutaGestionadaView extends BaseView {

    void showRutasGestionadas(List<RutaItem> rutasGestionadas);
    void scrollToPosition(int position);
    void setVisibilitySwipeRefreshLayout(boolean visible);

}
