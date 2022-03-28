package com.urbanoexpress.iridio3.model.interactor;

import android.content.Context;

import com.android.volley.VolleyError;

import org.json.JSONObject;

import com.urbanoexpress.iridio3.data.rest.ApiRequest;
import com.urbanoexpress.iridio3.data.rest.ApiRest;
import com.urbanoexpress.iridio3.model.interactor.callback.RequestCallback;

/**
 * Created by mick on 18/07/16.
 */
public class GoogleMapsServiceApis {

    private Context context;

    public GoogleMapsServiceApis(Context context) {
        this.context = context;
    }

    public static void distanceMatrix(String[] params, final RequestCallback callback) {
        ApiRequest.getInstance().newParams();
        ApiRequest.getInstance().putParams("origins", params[0]);
        ApiRequest.getInstance().putParams("destinations", params[1]);
        ApiRequest.getInstance().request(ApiRest.getInstance().getApiBaseUrl() +
                        ApiRest.Api.Google.DISTANCE_MATRIX,
                ApiRequest.TypeParams.FORM_DATA, new ApiRequest.ResponseListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (callback != null) callback.onSuccess(response);
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (callback != null) callback.onError(error);
                    }
                });
    }

    public static void directions(String[] params, final RequestCallback callback) {
        ApiRequest.getInstance().newParams();
        ApiRequest.getInstance().putParams("origin", params[0]);
        ApiRequest.getInstance().putParams("destination", params[1]);
        ApiRequest.getInstance().request(ApiRest.getInstance().getApiBaseUrl() +
                        ApiRest.Api.Google.DIRECTIONS,
                ApiRequest.TypeParams.FORM_DATA, new ApiRequest.ResponseListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (callback != null) callback.onSuccess(response);
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (callback != null) callback.onError(error);
                    }
                });
    }

    public static void geocoding(String[] params, final RequestCallback callback) {
        ApiRequest.getInstance().newParams();
        ApiRequest.getInstance().putParams("address", params[0]);
        ApiRequest.getInstance().request(ApiRest.getInstance().getApiBaseUrl() +
                        ApiRest.Api.Google.GEOCODING,
                ApiRequest.TypeParams.FORM_DATA, new ApiRequest.ResponseListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (callback != null) callback.onSuccess(response);
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (callback != null) callback.onError(error);
                    }
                });
    }

}
