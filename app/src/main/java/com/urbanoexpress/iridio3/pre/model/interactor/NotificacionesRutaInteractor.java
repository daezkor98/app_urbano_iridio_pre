package com.urbanoexpress.iridio3.pre.model.interactor;

import com.android.volley.VolleyError;
import com.orm.util.NamingHelper;
import com.urbanoexpress.iridio3.pre.data.rest.ApiRequest;
import com.urbanoexpress.iridio3.pre.data.rest.ApiRest;
import com.urbanoexpress.iridio3.pre.data.rest.ApiService;
import com.urbanoexpress.iridio3.pre.model.entity.Ruta;
import com.urbanoexpress.iridio3.pre.model.interactor.callback.RequestCallback;
import com.urbanoexpress.iridio3.pre.util.Preferences;

import org.json.JSONObject;

import java.util.List;

public class NotificacionesRutaInteractor {

    public void getNotificaciones(String[] params, final RequestCallback callback) {
        ApiService.getInstance().newParams();
        ApiService.getInstance().putParams("vp_id_user",    params[0]);
        ApiService.getInstance().putParams("vp_imei",       params[1]);
        ApiService.getInstance().request(ApiRest.getInstance().getApiBaseUrl() +
                        ApiRest.Api.GET_NOTIFICACIONES,
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

    public void requestMarcarNotificacionComoLeida(String[] params, final RequestCallback callback) {
        ApiService.getInstance().newParams();
        ApiService.getInstance().putParams("vp_id_notify",      params[0]);
        ApiService.getInstance().putParams("vp_linea_negocio",  params[1]);
        ApiService.getInstance().putParams("vp_id_user",        params[2]);
        ApiService.getInstance().putParams("vp_imei",           params[3]);
        ApiService.getInstance().request(ApiRest.getInstance().getApiBaseUrl() +
                        ApiRest.Api.UPLOAD_MARCAR_NOTIFICACION_COMO_LEIDA,
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

    public Ruta selectGuia(String guia, String lineaNegocio) {
        List<Ruta> ruta = Ruta.find(Ruta.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("guia") + " = ? and " +
                        NamingHelper.toSQLNameDefault("lineaNegocio") + " = ?",
                Preferences.getInstance().getString("idUsuario", ""),
                guia, lineaNegocio);

        if (ruta.size() > 0) {
            return ruta.get(0);
        }

        return null;
    }

    public static Ruta selectGuia(String idUsuario, String guia, String lineaNegocio) {
        List<Ruta> ruta = Ruta.find(Ruta.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("guia") + " = ? and " +
                        NamingHelper.toSQLNameDefault("lineaNegocio") + " = ?",
                idUsuario, guia, lineaNegocio);

        if (ruta.size() > 0) {
            return ruta.get(0);
        }

        return null;
    }
}