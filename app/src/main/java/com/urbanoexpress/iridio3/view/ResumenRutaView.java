package com.urbanoexpress.iridio3.view;

import com.urbanoexpress.iridio3.model.entity.ResumenRuta;

/**
 * Created by mick on 11/07/16.
 */
public interface ResumenRutaView extends BaseView2 {

    void setDatosResumenRuta(ResumenRuta resumenRuta);
    void setVisibilitySwipeRefreshLayout(boolean visible);
}
