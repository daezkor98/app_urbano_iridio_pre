package com.urbanoexpress.iridio3.pre.view;

import com.google.android.gms.maps.model.LatLng;
import com.urbanoexpress.iridio3.pre.model.entity.ParadaRuta;
import com.urbanoexpress.iridio3.pre.model.entity.Ruta;
import com.urbanoexpress.iridio3.pre.ui.model.GuiasMapaRutaDiaItem;
import com.urbanoexpress.iridio3.pre.ui.model.RutaItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mick on 24/08/17.
 */

public interface MapaRutaDelDiaView extends BaseView2 {

    void displayGuiasOnMap(List<Ruta> guias);
    void displayGuiasOnMapV2(List<GuiasMapaRutaDiaItem> guias);
    void displayRutearGuiasOnMap(List<Ruta> guias);
    void displayRutearGuiasOnMapV2(List<GuiasMapaRutaDiaItem> guias);
    void displayListGuias(List<RutaItem> guias);
    void displayMarkerSelector(String guia);
    void hideMarkerSelector();
    void setVisibilityBoxRuteoGuias(int visibility);
    void setVisibilityFabGuiasSinCoordenadas(int visibility);
    void setVisibilityFabRutearGuias(int visibility);
    void setVisibilityFabRutaMapa(int visibility);
    void setVisibilityMenuFab(int visibility);
    void updateNumberIconMarker(int position, String number);
    void animateCameraMap(ArrayList<LatLng> latLngs);
    void drawRouteOnMap(List<ParadaRuta> paradas);
    void onLoading(boolean show);
}
