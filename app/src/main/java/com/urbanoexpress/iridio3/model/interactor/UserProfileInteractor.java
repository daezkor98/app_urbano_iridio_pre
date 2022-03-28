package com.urbanoexpress.iridio3.model.interactor;

import com.android.volley.VolleyError;
import com.urbanoexpress.iridio3.data.rest.ApiRequest;
import com.urbanoexpress.iridio3.data.rest.ApiRest;
import com.urbanoexpress.iridio3.model.interactor.callback.RequestCallback;
import com.urbanoexpress.iridio3.util.network.volley.MultipartJsonObjectRequest;

import org.json.JSONObject;

public class UserProfileInteractor {

    public static void getUserProfile(String[] params, final RequestCallback callback) {
        ApiRequest.getInstance().newParams();
        ApiRequest.getInstance().putParams("vp_id_user", params[0]);
        ApiRequest.getInstance().putParams("device_phone", params[1]);
        ApiRequest.getInstance().request(ApiRest.getInstance().getApiBaseUrl() +
                        ApiRest.Api.GET_USER_PROFILE,
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

    public static void uploadPhoto(String[] params, MultipartJsonObjectRequest.DataPart data,
                           final RequestCallback callback) {
        ApiRequest.getInstance().newParams();
        ApiRequest.getInstance().putParams("vp_photo_name", params[0]);
        ApiRequest.getInstance().putParams("vp_id_user", params[1]);
        ApiRequest.getInstance().putParams("device_phone", params[2]);
        ApiRequest.getInstance().putData("photo", data);
        ApiRequest.getInstance().request(ApiRest.getInstance().getApiBaseUrl() +
                        ApiRest.Api.UPLOAD_PHOTO_USER_PROFILE,
                ApiRequest.TypeParams.MULTIPART, new ApiRequest.ResponseListener() {
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

    public static void changePassword(String[] params, final RequestCallback callback) {
        ApiRequest.getInstance().newParams();
        ApiRequest.getInstance().putParams("current_password", params[0]);
        ApiRequest.getInstance().putParams("new_password", params[1]);
        ApiRequest.getInstance().putParams("user_id", params[2]);
        ApiRequest.getInstance().request(ApiRest.getInstance().getApiBaseUrl() +
                        ApiRest.Api.CHANGE_PASSWORD_USER_PROFILE,
                ApiRequest.TypeParams.MULTIPART, new ApiRequest.ResponseListener() {
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
