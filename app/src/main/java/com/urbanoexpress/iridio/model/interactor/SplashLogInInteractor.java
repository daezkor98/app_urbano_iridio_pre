package com.urbanoexpress.iridio.model.interactor;

import android.content.Context;

import com.android.volley.VolleyError;

import org.json.JSONObject;

import com.urbanoexpress.iridio.data.rest.ApiRequest;
import com.urbanoexpress.iridio.data.rest.ApiRest;
import com.urbanoexpress.iridio.model.interactor.callback.RequestCallback;
import com.urbanoexpress.iridio.util.Preferences;

/**
 * Created by mick on 20/05/16.
 */
public class SplashLogInInteractor {

    private final String TAG = SplashLogInInteractor.class.getSimpleName();

    private Context context;

    public SplashLogInInteractor(Context context) {
        this.context = context;
    }

    public void logIn(String[] params, final RequestCallback callback) {
        ApiRequest.getInstance().newParams();
        ApiRequest.getInstance().putParams("username",      params[0]);
        ApiRequest.getInstance().putParams("password",      params[1]);
        ApiRequest.getInstance().putParams("firebaseToken", params[2]);
        ApiRequest.getInstance().putParams("device_imei",   params[3]);
        ApiRequest.getInstance().request(ApiRest.getInstance().getApiBaseUrl() +
                        ApiRest.Api.LOGIN,
                ApiRequest.TypeParams.FORM_DATA, new ApiRequest.ResponseListener() {
            @Override
            public void onResponse(JSONObject response) {
                callback.onSuccess(response);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError(error);
            }
        });
    }

    public static void getDataDefault(final RequestCallback callback) {
        ApiRequest.getInstance().newParams();
        ApiRequest.getInstance().putParams("id_user",
                Preferences.getInstance().getString("idUsuario", ""));
        ApiRequest.getInstance().request(ApiRest.getInstance().getApiBaseUrl() +
                        ApiRest.Api.GET_DATA_DEFAULT,
                ApiRequest.TypeParams.FORM_DATA, new ApiRequest.ResponseListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        callback.onSuccess(response);
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onError(error);
                    }
                });
    }

}
