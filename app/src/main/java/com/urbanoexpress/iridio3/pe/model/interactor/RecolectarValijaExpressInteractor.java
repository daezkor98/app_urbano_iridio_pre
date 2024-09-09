package com.urbanoexpress.iridio3.pe.model.interactor;

import com.android.volley.VolleyError;
import com.urbanoexpress.iridio3.pe.data.rest.ApiRequest;
import com.urbanoexpress.iridio3.pe.data.rest.ApiRest;
import com.urbanoexpress.iridio3.pe.model.interactor.callback.RequestCallback;

import org.json.JSONObject;

public class RecolectarValijaExpressInteractor {

    public static void readBarra(String[] params, final RequestCallback callback) {
        ApiRequest.getInstance().newParams();
        ApiRequest.getInstance().putParams("vp_barra", params[0]);
        ApiRequest.getInstance().putParams("vp_confirm", params[1]);
        ApiRequest.getInstance().putParams("vp_id_user", params[2]);
        ApiRequest.getInstance().request(ApiRest.getInstance().getApiBaseUrl() +
                        ApiRest.Api.READ_BARRA_RECOLECCION_VALIJA_EXPRESS,
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
