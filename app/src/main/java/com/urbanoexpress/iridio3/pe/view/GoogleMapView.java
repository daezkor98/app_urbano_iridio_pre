package com.urbanoexpress.iridio3.pe.view;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by mick on 26/08/16.
 */

public interface GoogleMapView extends BaseView2 {

    void addMarker(double latitude, double longitude, int resIcon, Object tag);
    void updateIconMarker(String tag, int resIcon);
    void centerCameraToMarkers();
    void centerCameraToMyLocation(LatLng latLng, float zoom);
    void showFABIniciarNavegacion();

}
