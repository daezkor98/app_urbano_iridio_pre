package com.urbanoexpress.iridio3.pre.model.interactor;

import com.android.volley.VolleyError;
import com.urbanoexpress.iridio3.pre.data.rest.ApiRequest;
import com.urbanoexpress.iridio3.pre.data.rest.ApiRest;
import com.urbanoexpress.iridio3.pre.data.rest.ApiService;
import com.urbanoexpress.iridio3.pre.model.interactor.callback.RequestCallback;

import org.json.JSONObject;

public class RecolectarValijaExpressInteractor {

    public static void readBarra(String[] params, final RequestCallback callback) {
        ApiService.getInstance().newParams();
        ApiService.getInstance().putParams("vp_barra", params[0]);
        ApiService.getInstance().putParams("vp_confirm", params[1]);
        ApiService.getInstance().putParams("vp_id_user", params[2]);
        ApiService.getInstance().request(ApiRest.getInstance().getApiBaseUrl() +
                        ApiRest.Api.READ_BARRA_RECOLECCION_VALIJA_EXPRESS,
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
