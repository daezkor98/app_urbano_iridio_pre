package com.urbanoexpress.iridio3.pe.model.interactor;

import com.android.volley.VolleyError;
import com.urbanoexpress.iridio3.pe.data.rest.ApiRequest;
import com.urbanoexpress.iridio3.pe.data.rest.ApiRest;
import com.urbanoexpress.iridio3.pe.model.interactor.callback.RequestCallback;

import org.json.JSONObject;

/**
 * Created by mick on 13/06/17.
 */

public class TransferirGuiaInteractor {

    public static void validateTransferirGuia(String[] params, final RequestCallback callback) {
        ApiRequest.getInstance().newParams();
        ApiRequest.getInstance().putParams("vp_ruta",           params[0]);
        ApiRequest.getInstance().putParams("vp_placa",          params[1]);
        ApiRequest.getInstance().putParams("vp_dni",            params[2]);
        ApiRequest.getInstance().putParams("vp_linea_negocio",  params[3]);
        ApiRequest.getInstance().putParams("vp_id_user",        params[4]);
        ApiRequest.getInstance().request(ApiRest.getInstance().getApiBaseUrl() +
                        ApiRest.Api.VALIDATE_TRANSFERIR_GUIA,
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

    public static void transferirGuias(String[] params, final RequestCallback callback) {
        ApiRequest.getInstance().newParams();
        ApiRequest.getInstance().putParams("vp_guias",          params[0]);
        ApiRequest.getInstance().putParams("vp_ruta",           params[1]);
        ApiRequest.getInstance().putParams("vp_placa",          params[2]);
        ApiRequest.getInstance().putParams("vp_per_id",         params[3]);
        ApiRequest.getInstance().putParams("vp_zon_id",         params[4]);
        ApiRequest.getInstance().putParams("vp_linea_negocio",  params[5]);
        ApiRequest.getInstance().putParams("vp_id_user",        params[6]);
        ApiRequest.getInstance().request(ApiRest.getInstance().getApiBaseUrl() +
                        ApiRest.Api.TRANSFERIR_GUIAS,
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
