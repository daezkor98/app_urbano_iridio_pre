package com.urbanoexpress.iridio3.pe.presenter;

import android.os.Build;
import androidx.fragment.app.Fragment;
import com.android.volley.VolleyError;
import com.urbanoexpress.iridio3.pe.util.async.AsyncTaskCoroutine;
import com.urbanoexpress.iridio3.pe.R;
import com.urbanoexpress.iridio3.data.local.PreferencesHelper;
import com.urbanoexpress.iridio3.pe.data.rest.ApiRequest;
import com.urbanoexpress.iridio3.pe.data.rest.ApiRest;
import com.urbanoexpress.iridio3.pe.ui.fragment.LogInFragment;
import com.urbanoexpress.iridio3.pe.ui.fragment.RequestPermissionFragment;
import com.urbanoexpress.iridio3.pe.util.Preferences;
import com.urbanoexpress.iridio3.pe.util.constant.Country;
import com.urbanoexpress.iridio3.pe.util.network.Connection;
import com.urbanoexpress.iridio3.pe.view.VerficationCodeView;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VerficationCodePresenter {

    private VerficationCodeView view;
    private String codePhone;
    private String numberPhone;
    private Fragment fragment;

    public VerficationCodePresenter(VerficationCodeView view, String isoCountry, String phone) {
        this.view = view;
        this.codePhone = isoCountry;
        this.numberPhone = phone;

    }

    public void onResume() {
        showNextFragment();
    }

    public void onBtnContinueClick(String email) {
        if (Connection.hasNetworkConnectivity(view.getViewContext())) {
            view.showProgressDialog();
            requestValidateVerificationEmail(email);

        } else {
            view.showMessageNotConnectedToNetwork();
        }
    }

    private void showNextFragment() {
        if (fragment != null) {
            String tag = fragment instanceof RequestPermissionFragment
                    ? RequestPermissionFragment.TAG
                    : LogInFragment.TAG;
            view.replaceFragment(fragment, tag);
        }
    }

    private void requestValidateVerificationEmail(String email) {
        if (Connection.hasNetworkConnectivity(view.getViewContext())) {
            ApiRequest.getInstance().newParams();
            ApiRequest.getInstance().putParams("telefono", numberPhone);
            ApiRequest.getInstance().putParams("codigo", codePhone);
            ApiRequest.getInstance().putParams("email", email);
            ApiRequest.getInstance().putParams("device", Build.MODEL);
            ApiRequest.getInstance().putParams("version", Build.VERSION.RELEASE);

            ApiRequest.getInstance().requestJSon(ApiRest.getInstance().getApiBaseUrlV2() + ApiRest.Api.VALIDATE_VERIFICATION_EMAIL,
                    ApiRequest.TypeParams.FORM_DATA, new ApiRequest.ResponseListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            view.dismissProgressDialog();

                            try {
                                if (response.getBoolean("success")) {
                                    new ConfigCountryTask().execute();
                                } else {
                                    view.showToast(response.getString("msg_error"));
                                }
                            } catch (JSONException ex) {
                                view.showToast(R.string.json_object_exception);
                            }

                        }

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Error ocurrido", error);
                            view.dismissProgressDialog();
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

            Preferences.getInstance().edit().putInt("country", Country.PERU);
            new PreferencesHelper(view.getViewContext()).putCountry(Country.PERU);

            Preferences.getInstance().edit().putString("phone", numberPhone);
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
