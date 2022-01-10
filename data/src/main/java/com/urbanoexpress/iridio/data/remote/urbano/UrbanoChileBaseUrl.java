package com.urbanoexpress.iridio.data.remote.urbano;

import com.urbanoexpress.iridio.data.remote.ApiEnvironment;
import com.urbanoexpress.iridio.data.remote.BaseUrl;

public final class UrbanoChileBaseUrl implements BaseUrl {

    private static final String DEVELOPMENT_BASE_URL = "http://devel.urbanoexpress.com.pe/";
    private static final String PRODUCTION_BASE_URL = "https://app.urbanoexpress.cl/";

    private final int apiEnvironment;

    public UrbanoChileBaseUrl(int apiEnvironment) {
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