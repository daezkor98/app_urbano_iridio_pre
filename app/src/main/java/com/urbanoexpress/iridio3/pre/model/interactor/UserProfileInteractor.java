package com.urbanoexpress.iridio3.pre.model.interactor;

import com.android.volley.VolleyError;
import com.urbanoexpress.iridio3.pre.data.rest.ApiRequest;
import com.urbanoexpress.iridio3.pre.data.rest.ApiRest;
import com.urbanoexpress.iridio3.pre.data.rest.ApiService;
import com.urbanoexpress.iridio3.pre.model.interactor.callback.RequestCallback;
import com.urbanoexpress.iridio3.pre.util.network.volley.MultipartJsonObjectRequest;

import org.json.JSONObject;

public class UserProfileInteractor {

//    public static void getUserProfile(String[] params, final RequestCallback callback) {
//        ApiRequest.getInstance().newParams();
//        ApiRequest.getInstance().putParams("vp_id_user", params[0]);
//        ApiRequest.getInstance().putParams("device_phone", params[1]);
//        ApiRequest.getInstance().request(ApiRest.getInstance().getApiBaseUrl() +
//                        ApiRest.Api.GET_USER_PROFILE,
//                ApiRequest.TypeParams.FORM_DATA, new ApiRequest.ResponseListener() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        callback.onSuccess(response);
//                    }
//
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        callback.onError(error);
//                    }
//                });
//    }

    public static void getUserProfile(String[] params, final RequestCallback callback) {
        ApiService.getInstance().newParams();
        ApiService.getInstance().putParams("vp_id_user", params[0]);
        ApiService.getInstance().putParams("device_phone", params[1]);
        ApiService.getInstance().request(ApiRest.getInstance().getApiBaseUrl() +
                        ApiRest.Api.GET_USER_PROFILE,
                ApiService.TypeParams.FORM_DATA, new ApiService.ResponseListener() {
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

//    public static void uploadPhoto(String[] params, MultipartJsonObjectRequest.DataPart data,
//                           final RequestCallback callback) {
//        ApiRequest.getInstance().newParams();
//        ApiRequest.getInstance().putParams("vp_photo_name", params[0]);
//        ApiRequest.getInstance().putParams("vp_id_user", params[1]);
//        ApiRequest.getInstance().putParams("device_phone", params[2]);
//        ApiRequest.getInstance().putData("photo", data);
//        ApiRequest.getInstance().request(ApiRest.getInstance().getApiBaseUrl() +
//                        ApiRest.Api.UPLOAD_PHOTO_USER_PROFILE,
//                ApiRequest.TypeParams.MULTIPART, new ApiRequest.ResponseListener() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        callback.onSuccess(response);
//                    }
//
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        callback.onError(error);
//                    }
//                });
//    }

    public static void uploadPhoto(String[] params, MultipartJsonObjectRequest.DataPart data,
                                   final RequestCallback callback) {
        ApiService.getInstance().newParams();
        ApiService.getInstance().putParams("vp_photo_name", params[0]);
        ApiService.getInstance().putParams("vp_id_user", params[1]);
        ApiService.getInstance().putParams("device_phone", params[2]);
        ApiService.getInstance().putData("photo", data);

        ApiService.getInstance().request(
                ApiRest.getInstance().getApiBaseUrl() + ApiRest.Api.UPLOAD_PHOTO_USER_PROFILE,
                ApiService.TypeParams.MULTIPART,
                new ApiService.ResponseListener() {
                    @Override
                    public void onResponse(org.json.JSONObject response) {
                        callback.onSuccess(response);
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onError(error);
                    }
                }
        );
    }

//    public static void changePassword(String[] params, final RequestCallback callback) {
//        ApiRequest.getInstance().newParams();
//        ApiRequest.getInstance().putParams("current_password", params[0]);
//        ApiRequest.getInstance().putParams("new_password", params[1]);
//        ApiRequest.getInstance().putParams("user_id", params[2]);
//        ApiRequest.getInstance().request(ApiRest.getInstance().getApiBaseUrl() +
//                        ApiRest.Api.CHANGE_PASSWORD_USER_PROFILE,
//                ApiRequest.TypeParams.MULTIPART, new ApiRequest.ResponseListener() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        callback.onSuccess(response);
//                    }
//
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        callback.onError(error);
//                    }
//                });
//    }

    public static void changePassword(String[] params, final RequestCallback callback) {
        ApiService.getInstance().newParams();
        ApiService.getInstance().putParams("current_password", params[0]);
        ApiService.getInstance().putParams("new_password", params[1]);
        ApiService.getInstance().putParams("user_id", params[2]);
        ApiService.getInstance().request(ApiRest.getInstance().getApiBaseUrl() +
                        ApiRest.Api.CHANGE_PASSWORD_USER_PROFILE,
                ApiService.TypeParams.FORM_DATA, new ApiService.ResponseListener() {
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
