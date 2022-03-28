package com.urbanoexpress.iridio3.application;

import android.content.Context;

import androidx.multidex.MultiDexApplication;

import com.orm.SugarContext;
import com.urbanoexpress.iridio3.data.local.PreferencesHelper;
import com.urbanoexpress.iridio3.data.remote.ApiEnvironment;
import com.urbanoexpress.iridio3.data.remote.BaseUrl;
import com.urbanoexpress.iridio3.data.remote.urbano.UrbanoApiManager;
import com.urbanoexpress.iridio3.data.rest.ApiRest;
import com.urbanoexpress.iridio3.util.Preferences;
import com.urbanoexpress.iridio3.util.network.volley.ManagerVolley;

import dagger.hilt.android.HiltAndroidApp;

/**
 * Created by mick on 19/05/16.
 */
@HiltAndroidApp
public class AndroidApplication extends MultiDexApplication {

    private static Context appContext;

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();

        Preferences.getInstance().init(getApplicationContext(), "GlobalConfigApp");

        PreferencesHelper preferencesHelper = new PreferencesHelper(getApplicationContext());
        int apiEnvironment = ApiEnvironment.PRODUCTION;
        int country = Preferences.getInstance().getInt("country", -1);

        preferencesHelper.putApiEnvironment(apiEnvironment);
        preferencesHelper.putCountry(country);

        BaseUrl baseUrl = ApiRest.buildUrbanoApiBaseUrl(apiEnvironment, country);
        UrbanoApiManager.setApiBaseUrl(baseUrl);
        ApiRest.getInstance().setApiBaseUrl(baseUrl);

        ManagerVolley.getInstance(this);

        SugarContext.init(this);

    }

    @Override
    public void onTerminate() {
        SugarContext.terminate();
        super.onTerminate();
    }

    public static Context getAppContext() {
        return appContext;
    }
}