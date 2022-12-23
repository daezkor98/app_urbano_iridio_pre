package com.urbanoexpress.iridio3.data.remote.urbano;

import com.urbanoexpress.iridio3.data.remote.ApiEnvironment;
import com.urbanoexpress.iridio3.data.remote.BaseUrl;

public final class UrbanoPeruBaseUrl implements BaseUrl {

    private static final String DEVELOPMENT_BASE_URL = "https://pyp3.pe.urbanoexpress.net/";
    private static final String PRODUCTION_BASE_URL = "https://app.urbano.com.pe/";

    private final int apiEnvironment;

    public UrbanoPeruBaseUrl(int apiEnvironment) {
        this.apiEnvironment = apiEnvironment;
    }

    @Override
    public String getBaseUrl() {
        switch (apiEnvironment) {
            case ApiEnvironment.DEVELOPMENT:
                return DEVELOPMENT_BASE_URL;
            case ApiEnvironment.PRODUCTION:
                return PRODUCTION_BASE_URL;
            default:
                return "";
        }
    }
}