package com.urbanoexpress.iridio.view;

import com.urbanoexpress.iridio.model.entity.ResumenRuta;

import java.util.ArrayList;

/**
 * Created by mick on 11/07/16.
 */
public interface ResumenRutaView extends BaseView2 {

    void setDatosResumenRuta(ResumenRuta resumenRuta);
    void setVisibilitySwipeRefreshLayout(boolean visible);
}
