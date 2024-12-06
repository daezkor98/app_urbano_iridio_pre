package com.urbanoexpress.iridio3.pe.presenter;

import android.util.Log;

import com.android.volley.VolleyError;
import com.google.firebase.messaging.FirebaseMessaging;
import com.urbanoexpress.iridio3.pe.R;
import com.urbanoexpress.iridio3.data.local.PreferencesHelper;
import com.urbanoexpress.iridio3.pe.data.rest.ApiRequest;
import com.urbanoexpress.iridio3.pe.data.rest.ApiRest;
import com.urbanoexpress.iridio3.pe.util.CommonUtils;
import com.urbanoexpress.iridio3.pe.util.constant.Country;
import com.urbanoexpress.iridio3.pe.util.network.Connection;
import com.urbanoexpress.iridio3.pe.view.ConfigPhoneView;

import org.json.JSONException;
import org.json.JSONObject;

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
        selectCountry(isoCountry);
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(s -> firebaseToken = s);

        view.requestHint();
    }

    public void onCountrySelected(String iso) {
        isoCountry = iso;
        selectCountry(iso);
    }

    public void onSelectCountryClick() {
        view.navigateToChoiseCountryBottomSheet();
    }

    public void onBtnContinuarClick() {
        view.setEnabledButtonNext(false);

        if (validatePhone()) {
            if (Connection.hasNetworkConnectivity(view.getViewContext())) {
                configCountry();
            } else {
                view.setEnabledButtonNext(true);
                view.showMessageNotConnectedToNetwork();
            }
        } else {
            view.setEnabledButtonNext(true);
        }
    }

    private void selectCountry(String isoCountry) {
        switch (isoCountry) {
            case "cl":
                view.setIconFlag(R.drawable.flag_chile);
                view.setTextPhonePrefix(" +56");
                view.setHintPhone("9 6123 4567");
                break;
            case "pe":
                view.setIconFlag(R.drawable.flag_peru);
                view.setTextPhonePrefix(" +51");
                view.setHintPhone("912 345 678");
                break;
        }
    }

    private boolean validatePhone() {
        if (view.getTextPhone().isEmpty()) {
            view.setErrorPhone("Debes ingresar tu número de celular.");
            return false;
        }

        if (!view.getTextPhone().matches("^(9)\\d{8}$")) {
            view.setErrorPhone("El número de celular es inválido.");
            return false;
        }

        return true;
    }

    private void configCountry() {
        view.showProgressDialog();
        PreferencesHelper preferencesHelper = new PreferencesHelper(view.getViewContext());

        switch (isoCountry) {
            case "cl":
                ApiRest.getInstance().setApiBaseUrl(
                        ApiRest.buildUrbanoApiBaseUrl(preferencesHelper.getApiEnvironment(), Country.CHILE));
                break;
            case "pe":
                ApiRest.getInstance().setApiBaseUrl(
                        ApiRest.buildUrbanoApiBaseUrl(preferencesHelper.getApiEnvironment(), Country.PERU));
                break;
        }

        /*Code for testing use cases*/
        String phone = view.getTextPhone();
        if (GOOGLE_MOCK_PHONE.equals(phone)) {
            //The given number already has a OTP, so we do not generate another one
            view.navigateToVerficationCodeFragment(isoCountry, view.getTextPhone(), firebaseToken,true);
        } else {
            requestConfigPhone();
        }
        /*End testing code*/
    }

    //GOOGLE MASTER NUMBER USED FOR REVISION AND TESTING
    final String GOOGLE_MOCK_PHONE = "937004445"; // The corresponding OTP code is "572376"

    private void requestConfigPhone() {
        ApiRequest.getInstance().newParams();
        ApiRequest.getInstance().putParams("device_phone", view.getTextPhone());
        ApiRequest.getInstance().putParams("firebase_token", firebaseToken);

        ApiRequest.getInstance().request(ApiRest.getInstance().getApiBaseUrl() + ApiRest.Api.CONFIG_PHONE,
                ApiRequest.TypeParams.FORM_DATA, new ApiRequest.ResponseListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        view.dismissProgressDialog();
                        view.setEnabledButtonNext(true);

                        try {
                            if (response.getBoolean("success")) {
                                view.navigateToVerficationCodeFragment(isoCountry,
                                        view.getTextPhone(), firebaseToken,false);
                            } else {
                                view.showToast(response.getString("msg_error"));
                            }
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                            view.showToast(R.string.json_object_exception);
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        view.dismissProgressDialog();
                        view.setEnabledButtonNext(true);
                        view.showToast(R.string.volley_error_message);
                    }
                });
    }
}
