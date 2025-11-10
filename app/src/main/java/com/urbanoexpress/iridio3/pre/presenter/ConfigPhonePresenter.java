package com.urbanoexpress.iridio3.pre.presenter;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;
import com.urbanoexpress.iridio3.data.local.PreferencesHelper;
import com.urbanoexpress.iridio3.pre.data.rest.ApiRest;
import com.urbanoexpress.iridio3.pre.util.CommonUtils;
import com.urbanoexpress.iridio3.pre.util.constant.Country;
import com.urbanoexpress.iridio3.pre.view.ConfigPhoneView;

public class ConfigPhonePresenter {

    private ConfigPhoneView view;
    private String isoCountry = "";
    private String firebaseToken = "";

    public ConfigPhonePresenter(ConfigPhoneView view) {
        this.view = view;
    }

    public void init() {
        isoCountry = CommonUtils.getCountryIso(view.getViewContext());
        Log.d("ConfigPhonePresenter", "ISO COUNTRY: " + isoCountry);
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(s -> firebaseToken = s);
    }

    public void onCountrySelected(String iso) {
        isoCountry = iso;
    }

    public void onBtnContinueClick(String codePhonePeru) {
        configCountry(codePhonePeru);
    }

    private void configCountry(String codePhonePeru) {
        view.showProgressDialog();
        PreferencesHelper preferencesHelper = new PreferencesHelper(view.getViewContext());

        //configurar API solo para peru
        ApiRest.getInstance().setApiBaseUrl(
                ApiRest.buildUrbanoApiBaseUrl(preferencesHelper.getApiEnvironment(), Country.PERU));

        view.navigateToVerficationCodeFragment(codePhonePeru,
                view.getTextPhone());


    }
}
