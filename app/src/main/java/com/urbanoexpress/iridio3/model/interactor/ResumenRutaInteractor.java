package com.urbanoexpress.iridio3.model.interactor;

import android.content.Context;

import com.android.volley.VolleyError;

import org.json.JSONObject;

import com.urbanoexpress.iridio3.data.rest.ApiRequest;
import com.urbanoexpress.iridio3.data.rest.ApiRest;
import com.urbanoexpress.iridio3.model.interactor.callback.RequestCallback;

/**
 * Created by mick on 11/07/16.
 */
public class ResumenRutaInteractor {

    private Context context;

    public ResumenRutaInteractor(Context context) {
        this.context = context;
    }

    public void getResumenRuta(String[] params, final RequestCallback callback) {
        ApiRequest.getInstance().newParams();
        ApiRequest.getInstance().putParams("vp_id_user", params[0]);
        ApiRequest.getInstance().putParams("vp_id_mac", params[1]);
        ApiRequest.getInstance().putParams("vp_linea_negocio", params[2]);
        ApiRequest.getInstance().request(ApiRest.getInstance().getApiBaseUrl() +
                        ApiRest.Api.GET_RESUMEN_RUTA,
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

    public void getResumenRutaRural(String[] params, final RequestCallback callback) {
        ApiRequest.getInstance().newParams();
        ApiRequest.getInstance().putParams("vp_fecha", params[0]);
        ApiRequest.getInstance().putParams("vp_id_user", params[1]);
        ApiRequest.getInstance().putParams("device_phone", params[2]);
        ApiRequest.getInstance().request(ApiRest.getInstance().getApiBaseUrl() +
                        ApiRest.Api.GET_RESUMEN_RUTA_RURAL,
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
