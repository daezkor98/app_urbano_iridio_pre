package com.urbanoexpress.iridio.data.rest;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import com.urbanoexpress.iridio.util.network.volley.CustomJsonObjectRequest;
import com.urbanoexpress.iridio.util.network.volley.ManagerVolley;
import com.urbanoexpress.iridio.util.network.volley.MultipartJsonObjectRequest;

/**
 * Created by mick on 20/05/16.
 */
public class ApiRequest {

    private static final String TAG = ApiRequest.class.getSimpleName();

    private static ApiRequest apiRequest;
    private Map<String, String> requestParams = null;
    private Map<String, MultipartJsonObjectRequest.DataPart> requestParamsData = null;

    public interface TypeParams {
        int FORM_DATA = 0;
        int MULTIPART = 1;
    }

    private ApiRequest() {}

    public static synchronized ApiRequest getInstance() {
        if (apiRequest == null) {
            apiRequest = new ApiRequest();
        }
        return apiRequest;
    }

    public void request(String url, int typeParams, final ResponseListener responseListener) {
        Log.d(TAG, "URL: " + url);

        Log.d(TAG, "PARAMS");
        Log.d(TAG, "TAGS: " + requestParams.keySet() + "");
        Log.d(TAG, "VALUES: " + requestParams.values() + "");

        Log.d(TAG, "DATA");
        Log.d(TAG, "TAGS: " + requestParamsData.keySet() + "");
        Log.d(TAG, "VALUES: " + requestParamsData.values() + "");

        switch (typeParams) {
            case TypeParams.FORM_DATA:
                requestFormData(url, responseListener);
                break;
            case TypeParams.MULTIPART:
                requestMultiPart(url, responseListener);
                break;
        }
    }

    private void requestFormData(String url, final ResponseListener responseListener) {
        CustomJsonObjectRequest request = new CustomJsonObjectRequest
                (Request.Method.POST, url, requestParams, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "SUCCESS");
                        Log.d(TAG, response + "");
                        responseListener.onResponse(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "ERROR");
                        Log.d(TAG, error.getMessage() + "");
                        Log.d(TAG, error.getStackTrace() + "");
                        responseListener.onErrorResponse(error);
                    }
                });
        ManagerVolley.getInstance(null).addToRequestQueue(request);
    }

    private void requestMultiPart(String url, final ResponseListener responseListener) {
        MultipartJsonObjectRequest request = new MultipartJsonObjectRequest
                (url, requestParams, requestParamsData, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "SUCCESS");
                        Log.d(TAG, response + "");
                        responseListener.onResponse(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "ERROR");
                        Log.d(TAG, error.getMessage() + "");
                        Log.d(TAG, error.getStackTrace() + "");
                        responseListener.onErrorResponse(error);
                    }
                });
        ManagerVolley.getInstance(null).addToRequestQueue(request);
    }

    public void newParams() {
        requestParams = new HashMap<String, String>();
        requestParamsData = new HashMap<String, MultipartJsonObjectRequest.DataPart>();
    }

    public void putParams(String key, String value) {
        requestParams.put(key, value);
    }

    public void putData(String key, MultipartJsonObjectRequest.DataPart data) {
        requestParamsData.put(key, data);
    }

    public interface ResponseListener {
        void onResponse(JSONObject response);
        void onErrorResponse(VolleyError error);
    }
}