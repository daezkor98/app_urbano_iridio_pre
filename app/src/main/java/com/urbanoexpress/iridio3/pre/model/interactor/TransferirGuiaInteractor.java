package com.urbanoexpress.iridio3.pre.model.interactor;

import com.android.volley.VolleyError;
import com.urbanoexpress.iridio3.pre.data.rest.ApiRequest;
import com.urbanoexpress.iridio3.pre.data.rest.ApiRest;
import com.urbanoexpress.iridio3.pre.data.rest.ApiService;
import com.urbanoexpress.iridio3.pre.model.interactor.callback.RequestCallback;

import org.json.JSONObject;

/**
 * Created by mick on 13/06/17.
 */

public class TransferirGuiaInteractor {

    public static void validateTransferirGuia(String[] params, final RequestCallback callback) {
        ApiService.getInstance().newParams();
        ApiService.getInstance().putParams("vp_ruta",           params[0]);
        ApiService.getInstance().putParams("vp_placa",          params[1]);
        ApiService.getInstance().putParams("vp_dni",            params[2]);
        ApiService.getInstance().putParams("vp_linea_negocio",  params[3]);
        ApiService.getInstance().putParams("vp_id_user",        params[4]);
        ApiService.getInstance().request(ApiRest.getInstance().getApiBaseUrl() +
                        ApiRest.Api.VALIDATE_TRANSFERIR_GUIA,
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

    public static void transferirGuias(String[] params, final RequestCallback callback) {
        ApiService.getInstance().newParams();
        ApiService.getInstance().putParams("vp_guias",          params[0]);
        ApiService.getInstance().putParams("vp_ruta",           params[1]);
        ApiService.getInstance().putParams("vp_placa",          params[2]);
        ApiService.getInstance().putParams("vp_per_id",         params[3]);
        ApiService.getInstance().putParams("vp_zon_id",         params[4]);
        ApiService.getInstance().putParams("vp_linea_negocio",  params[5]);
        ApiService.getInstance().putParams("vp_id_user",        params[6]);
        ApiService.getInstance().request(ApiRest.getInstance().getApiBaseUrl() +
                        ApiRest.Api.TRANSFERIR_GUIAS,
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
