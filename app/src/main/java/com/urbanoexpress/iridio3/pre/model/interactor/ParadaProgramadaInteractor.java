package com.urbanoexpress.iridio3.pre.model.interactor;

import android.content.Context;

import com.android.volley.VolleyError;

import org.json.JSONObject;

import com.orm.util.NamingHelper;
import com.urbanoexpress.iridio3.pre.data.rest.ApiRequest;
import com.urbanoexpress.iridio3.pre.data.rest.ApiRest;
import com.urbanoexpress.iridio3.pre.data.rest.ApiService;
import com.urbanoexpress.iridio3.pre.model.entity.ParadaProgramada;
import com.urbanoexpress.iridio3.pre.model.interactor.callback.RequestCallback;
import com.urbanoexpress.iridio3.pre.util.Preferences;

import java.util.List;

/**
 * Created by mick on 02/06/16.
 */
public class ParadaProgramadaInteractor {

    private final String TAG = ParadaProgramadaInteractor.class.getSimpleName();

    private Context context;

    public ParadaProgramadaInteractor(Context context) {
        this.context = context;
    }

    public void getDespachos(String[] params, final RequestCallback callback) {
        ApiService.getInstance().newParams();
        ApiService.getInstance().putParams("id_parada", params[0]);
        ApiService.getInstance().putParams("id_user",   params[1]);
        ApiService.getInstance().request(ApiRest.getInstance().getApiBaseUrl() +
                        ApiRest.Api.GET_DESPACHOS_PLAN_VIAJE,
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

    public void updateEstadoDespachos(String[] params, final RequestCallback callback) {
        ApiService.getInstance().newParams();
        ApiService.getInstance().putParams("id_parada",     params[0]);
        ApiService.getInstance().putParams("id_destinos",    params[1]);
        ApiService.getInstance().putParams("id_despachos",  params[2]);
        ApiService.getInstance().putParams("id_estado",     params[3]);
        ApiService.getInstance().putParams("id_user",       params[4]);
        ApiService.getInstance().request(ApiRest.getInstance().getApiBaseUrl() +
                        ApiRest.Api.UPDATE_ESTADO_DESPACHO_PLAN_VIAJE,
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

    public ParadaProgramada selectParadaProgramadaById(String idParada) {
        List<ParadaProgramada> paradaProgramadaList = ParadaProgramada.find(ParadaProgramada.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("idStop") + " = ?",
                Preferences.getInstance().getString("idUsuario", ""), idParada);

        if (paradaProgramadaList.size() > 0) {
            return paradaProgramadaList.get(0);
        }

        return null;
    }

}
