package com.urbanoexpress.iridio3.pe.model.interactor;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.urbanoexpress.iridio3.pe.data.rest.ApiRequest;
import com.urbanoexpress.iridio3.pe.data.rest.ApiRest;
import com.urbanoexpress.iridio3.pe.model.interactor.callback.RequestCallback;

import org.json.JSONObject;

public class MapaRutaDelDiaInteractor {

    public static void getCoordenadas(String[] params, Context context, final RequestCallback callback) {
        String url = ApiRest.getInstance().getApiBaseUrlV2() +
                ApiRest.Api.WAYPOINTS + params[0] + "/" + params[1];

        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JSONObject jsonBody = new JSONObject();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        callback.onSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onError(error);
                    }
                }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
        };

        requestQueue.add(jsonObjectRequest);
    }
}
