package com.urbanoexpress.iridio.view;

import com.google.android.gms.common.api.GoogleApiClient;

public interface RutaAuditoriaView extends BaseView {
    void setVisibilityBoxRutaNoIniciada(int visibility);
    GoogleApiClient getGoogleApiClient();
}
