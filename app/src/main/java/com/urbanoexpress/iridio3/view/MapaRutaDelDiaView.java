package com.urbanoexpress.iridio3.view;

import com.google.android.gms.maps.model.LatLng;
import com.urbanoexpress.iridio3.model.entity.Ruta;
import com.urbanoexpress.iridio3.ui.model.RutaItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mick on 24/08/17.
 */

public interface MapaRutaDelDiaView extends BaseView2 {

    void displayGuiasOnMap(List<Ruta> guias);
    void displayRutearGuiasOnMap(List<Ruta> guias);
    void displayListGuias(List<RutaItem> guias);
    void displayMarkerSelector(String guia);
    void hideMarkerSelector();
    void setVisibilityBoxRuteoGuias(int visibility);
    void setVisibilityFabGuiasSinCoordenadas(int visibility);
    void setVisibilityFabRutearGuias(int visibility);
    void setVisibilityMenuFab(int visibility);
    void updateNumberIconMarker(int position, String number);
    void animateCameraMap(ArrayList<LatLng> latLngs);
}
