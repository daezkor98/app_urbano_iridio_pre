package com.urbanoexpress.iridio3.pe.presenter;

import android.content.IntentFilter;
import android.os.Build;

import androidx.fragment.app.Fragment;

import com.android.volley.VolleyError;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.Task;
import com.urbanoexpress.iridio3.pe.util.async.AsyncTaskCoroutine;
import com.urbanoexpress.iridio3.pe.R;
import com.urbanoexpress.iridio3.data.local.PreferencesHelper;
import com.urbanoexpress.iridio3.pe.data.rest.ApiRequest;
import com.urbanoexpress.iridio3.pe.data.rest.ApiRest;
import com.urbanoexpress.iridio3.pe.ui.fragment.LogInFragment;
import com.urbanoexpress.iridio3.pe.ui.fragment.RequestPermissionFragment;
import com.urbanoexpress.iridio3.pe.util.CommonUtils;
import com.urbanoexpress.iridio3.pe.util.Preferences;
import com.urbanoexpress.iridio3.pe.util.SmsBroadcastReceiver;
import com.urbanoexpress.iridio3.pe.util.constant.Country;
import com.urbanoexpress.iridio3.pe.util.network.Connection;
import com.urbanoexpress.iridio3.pe.view.VerficationCodeView;

import org.json.JSONException;
import org.json.JSONObject;

public class VerficationCodePresenter implements SmsBroadcastReceiver.OTPReceiveListener {

    private VerficationCodeView view;
    private String isoCountry;
    private String phone;
    private String firebaseToken;
    private Fragment fragment;

    private SmsBroadcastReceiver smsBroadcast;

    public VerficationCodePresenter(VerficationCodeView view, String isoCountry, String phone,
                                    String firebaseToken, boolean isGoogleMock) {
        this.view = view;
        this.isoCountry = isoCountry;
        this.phone = phone;
        this.firebaseToken = firebaseToken;
        if (isGoogleMock) {
            view.showProgressDialog();
            requestValidateVerificationCode(GOOGLE_MOCK_CODE);
        }
    }

    //Used to pass google testint
    final String GOOGLE_MOCK_CODE = "760164";
    @Override
    public void onOTPReceived(String value) {
        try {
            view.getViewContext().unregisterReceiver(smsBroadcast);
            try {
                char[] codes = value.toCharArray();
                view.setTextCode1(String.valueOf(codes[0]));
                view.setTextCode2(String.valueOf(codes[1]));
                view.setTextCode3(String.valueOf(codes[2]));
                view.setTextCode4(String.valueOf(codes[3]));
                view.setTextCode5(String.valueOf(codes[4]));
                view.setTextCode6(String.valueOf(codes[5]));
                onBtnContinuarClick();
            } catch (ArrayIndexOutOfBoundsException ex) {
                ex.printStackTrace();
                view.showToast("Lo sentimos, ocurrió un error al extraer el código de verificación.");
            }
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onOTPTimeOut() {
        view.showToast("Lo sentimos, no se pudo detectar el código de verificación automaticamente a tiempo.");
    }

    public void init() {
        String phoneWithPrefix = "";

        if (isoCountry.equals("cl")) {
            phoneWithPrefix = "+56 " + phone;
        } else if (isoCountry.equals("pe")) {
            phoneWithPrefix = "+51 " + phone;
        }

        String html = "Revisa tus mensajes. Hemos enviado un código de 6 dígitos al " +
                "<font color='#448AFF'><b>" + phoneWithPrefix + "</b></font>";
        view.setHtmlLblMsg(html);

        startSMSListener();

        smsBroadcast = new SmsBroadcastReceiver();
        smsBroadcast.initOTPListener(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION);
        //view.getViewContext().registerReceiver(smsBroadcast, intentFilter);
    }

    public void onResume() {
        showNextFragment();
    }

    public void onDestroy() {
        try {
            view.getViewContext().unregisterReceiver(smsBroadcast);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
    }

    public void onBtnContinuarClick() {
        view.hideKeyboard();

        view.setEnabledButtonNext(false);

        if (validateCodes()) {
            if (Connection.hasNetworkConnectivity(view.getViewContext())) {
                view.showProgressDialog();
                requestValidateVerificationCode(getJoinedVerificationCode());
            } else {
                view.setEnabledButtonNext(true);
                view.showMessageNotConnectedToNetwork();
            }
        } else {
            view.setEnabledButtonNext(true);
        }
    }

    private void startSMSListener() {
        SmsRetrieverClient client = SmsRetriever.getClient(view.getViewContext());

        Task<Void> task = client.startSmsRetriever();

        task.addOnSuccessListener(aVoid -> {
        });

        task.addOnFailureListener(e -> view.showToast(e.getMessage()));
    }

    private String getJoinedVerificationCode() {
        return new StringBuilder().append(view.getTextCode1())
                .append(view.getTextCode2())
                .append(view.getTextCode3())
                .append(view.getTextCode4())
                .append(view.getTextCode5())
                .append(view.getTextCode6())
                .toString();
    }

    private boolean validateCodes() {
        if (view.getTextCode1().isEmpty() || view.getTextCode2().isEmpty() ||
                view.getTextCode3().isEmpty() || view.getTextCode4().isEmpty() ||
                view.getTextCode5().isEmpty() || view.getTextCode6().isEmpty()) {
            view.showToast(R.string.fragment_request_verification_code_ingrese_codigo_verificacion);
            return false;
        }
        return true;
    }

    private void showNextFragment() {
        if (fragment != null) {
            String tag = fragment instanceof RequestPermissionFragment
                    ? RequestPermissionFragment.TAG
                    : LogInFragment.TAG;
            view.replaceFragment(fragment, tag);
        }
    }

    private void requestValidateVerificationCode(String verificationCode) {
        if (Connection.hasNetworkConnectivity(view.getViewContext())) {
            ApiRequest.getInstance().newParams();
            ApiRequest.getInstance().putParams("device_phone", phone);
            ApiRequest.getInstance().putParams("verification_code", verificationCode);
            ApiRequest.getInstance().putParams("firebase_token", firebaseToken);
            ApiRequest.getInstance().putParams("device_model", Build.MODEL);
            ApiRequest.getInstance().putParams("version_os", Build.VERSION.RELEASE);
            ApiRequest.getInstance().putParams("version_name",
                    CommonUtils.getPackageInfo(view.getViewContext()).versionName);

            ApiRequest.getInstance().request(ApiRest.getInstance().getApiBaseUrl() + ApiRest.Api.VALIDATE_VERIFICATION_CODE,
                    ApiRequest.TypeParams.FORM_DATA, new ApiRequest.ResponseListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            view.dismissProgressDialog();
                            view.setEnabledButtonNext(true);

                            try {
                                if (response.getBoolean("success")) {
                                    new ConfigCountryTask().execute();
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
        } else {
            view.showMessageNotConnectedToNetwork();
        }
    }

    private class ConfigCountryTask extends AsyncTaskCoroutine<String, String> {

        @Override
        public String doInBackground(String... strings) {
            switch (isoCountry) {
                case "cl":
                    Preferences.getInstance().edit().putInt("country", Country.CHILE);
                    new PreferencesHelper(view.getViewContext()).putCountry(Country.CHILE);
                    break;
                case "pe":
                    Preferences.getInstance().edit().putInt("country", Country.PERU);
                    new PreferencesHelper(view.getViewContext()).putCountry(Country.PERU);
                    break;
            }

            Preferences.getInstance().edit().putString("phone", phone);
            Preferences.getInstance().edit().commit();
            return null;
        }

        @Override
        public void onPostExecute(String s) {
            super.onPostExecute(s);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                fragment = RequestPermissionFragment.newInstance();
            } else {
                fragment = LogInFragment.newInstance();
            }

            showNextFragment();
        }
    }
}
