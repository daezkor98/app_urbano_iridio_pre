package com.urbanoexpress.iridio.data.remote.urbano;

import com.urbanoexpress.iridio.data.remote.ApiEnvironment;
import com.urbanoexpress.iridio.data.remote.BaseUrl;

public final class UrbanoPeruBaseUrl implements BaseUrl {

    private static final String DEVELOPMENT_BASE_URL = "https://dev.urbanoexpress.com.pe/pyp/";
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