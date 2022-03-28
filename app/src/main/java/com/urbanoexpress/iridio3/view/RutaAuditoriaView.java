package com.urbanoexpress.iridio3.view;

import com.google.android.gms.common.api.GoogleApiClient;

public interface RutaAuditoriaView extends BaseView {
    void setVisibilityBoxRutaNoIniciada(int visibility);
    GoogleApiClient getGoogleApiClient();
}
