package com.urbanoexpress.iridio3.pre.model.interactor;

import android.content.Context;

import com.android.volley.VolleyError;

import org.json.JSONObject;

import com.orm.util.NamingHelper;
import com.urbanoexpress.iridio3.pre.data.rest.ApiRequest;
import com.urbanoexpress.iridio3.pre.data.rest.ApiRest;
import com.urbanoexpress.iridio3.pre.data.rest.ApiService;
import com.urbanoexpress.iridio3.pre.model.entity.Despacho;
import com.urbanoexpress.iridio3.pre.model.entity.IncidenteRuta;
import com.urbanoexpress.iridio3.pre.model.entity.ParadaProgramada;
import com.urbanoexpress.iridio3.pre.model.entity.PlanDeViaje;
import com.urbanoexpress.iridio3.pre.model.interactor.callback.RequestCallback;
import com.urbanoexpress.iridio3.pre.util.Preferences;

import java.util.List;

/**
 * Created by mick on 30/05/16.
 */
public class PlanDeViajeInteractor {

    private final String TAG = PlanDeViajeInteractor.class.getSimpleName();

    private Context context;

    public PlanDeViajeInteractor(Context context) {
        this.context = context;
    }

    public void getPlanDeViaje(String[] params, final RequestCallback callback) {
        ApiService.getInstance().newParams();
        ApiService.getInstance().putParams("id_user", params[0]);
        ApiService.getInstance().putParams("vp_id_plan_viaje", params[1]);
        ApiService.getInstance().request(ApiRest.getInstance().getApiBaseUrl() +
                        ApiRest.Api.GET_PLAN_VIAJE,
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

    public void validatePlanViajeActivos(String[] params, final RequestCallback callback) {
        ApiService.getInstance().newParams();
        ApiService.getInstance().putParams("id_user", params[0]);
        ApiService.getInstance().request(ApiRest.getInstance().getApiBaseUrl() +
                        ApiRest.Api.VALIDATE_PLAN_VIAJE_ACTIVOS,
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

    public void actualizarPlaca(String[] params, final RequestCallback callback) {
        ApiService.getInstance().newParams();
        ApiService.getInstance().putParams("placa",         params[0]);
        ApiService.getInstance().putParams("id_plan_viaje", params[1]);
        ApiService.getInstance().putParams("id_user",       params[2]);
        ApiService.getInstance().request(ApiRest.getInstance().getApiBaseUrl() +
                        ApiRest.Api.UPDATE_PLACA_PLAN_VIAJE,
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

    public void updateEstado(String[] params, final RequestCallback callback) {
        ApiService.getInstance().newParams();
        ApiService.getInstance().putParams("id_plan_viaje",     params[0]);
        ApiService.getInstance().putParams("id_estado",         params[1]);
        ApiService.getInstance().putParams("vp_fecha",          params[2]);
        ApiService.getInstance().putParams("vp_hora",           params[3]);
        ApiService.getInstance().putParams("vp_pos_px",         params[4]);
        ApiService.getInstance().putParams("vp_pos_py",         params[5]);
        ApiService.getInstance().putParams("vp_exactitud",      params[6]);
        ApiService.getInstance().putParams("vp_bateria",        params[7]);
        ApiService.getInstance().putParams("vp_kilometraje",    params[8]);
        ApiService.getInstance().putParams("vp_total_km_ruta",  params[9]);
        ApiService.getInstance().putParams("id_user",           params[10]);
        ApiService.getInstance().request(ApiRest.getInstance().getApiBaseUrl() +
                        ApiRest.Api.UPDATE_ESTADO_PLAN_VIAJE,
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

    public void getDespachosPendientes(String[] params, final RequestCallback callback) {
        ApiService.getInstance().newParams();
        ApiService.getInstance().putParams("id_plan_viaje", params[0]);
        ApiService.getInstance().putParams("id_user",       params[1]);
        ApiService.getInstance().request(ApiRest.getInstance().getApiBaseUrl() +
                        ApiRest.Api.GET_DESPACHOS_PENDIENTES_PLAN_VIAJE,
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

    public void uploadDespachosPendientes(String[] params, final RequestCallback callback) {
        ApiService.getInstance().newParams();
        ApiService.getInstance().putParams("id_plan_viaje", params[0]);
        ApiService.getInstance().putParams("id_destinos",   params[1]);
        ApiService.getInstance().putParams("id_despachos",  params[2]);
        ApiService.getInstance().putParams("id_user",       params[3]);
        ApiService.getInstance().request(ApiRest.getInstance().getApiBaseUrl() +
                        ApiRest.Api.UPLOAD_DESPACHOS_PENDIENTES_PLAN_VIAJE,
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

    public PlanDeViaje selectPlanViajeById(String idPlanViaje) {
        List<PlanDeViaje> planDeViajeList = PlanDeViaje.find(PlanDeViaje.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("idPlanViaje") + " = ?",
                Preferences.getInstance().getString("idUsuario", ""), idPlanViaje);

        if (planDeViajeList.size() > 0) {
            return planDeViajeList.get(0);
        }

        return null;
    }

    public List<PlanDeViaje> selectAllPlanViaje() {
        return PlanDeViaje.find(PlanDeViaje.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ?",
                Preferences.getInstance().getString("idUsuario", ""));
    }

    public List<ParadaProgramada> selectAllParadaProgramada() {
        return ParadaProgramada.find(ParadaProgramada.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ?",
                Preferences.getInstance().getString("idUsuario", ""));
    }

    public ParadaProgramada selectParadaProgramadaByID(String idParada) {
        List<ParadaProgramada> parada = ParadaProgramada.find(ParadaProgramada.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("idStop") + " = ?",
                Preferences.getInstance().getString("idUsuario", ""), idParada);

        if (parada.size() > 0) {
            return parada.get(0);
        }

        return null;
    }

    public static List<Despacho> selectDespachoByIdParada(String idParada, String tipoDespacho) {
        return Despacho.find(Despacho.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                NamingHelper.toSQLNameDefault("idParada") + " = ? and " +
                        NamingHelper.toSQLNameDefault("tipoDespacho") + " = ?",
                Preferences.getInstance().getString("idUsuario", ""),
                idParada, tipoDespacho);
    }

    public static List<IncidenteRuta> selectIncidentesByIdPlanViaje(String idRuta) {
        List<IncidenteRuta> incidentes = IncidenteRuta.find(IncidenteRuta.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("idRuta") + " = ? and " +
                        NamingHelper.toSQLNameDefault("tipoRuta") + " = ?",
                Preferences.getInstance().getString("idUsuario", ""),
                idRuta, IncidenteRuta.TipoRuta.PLAN_DE_VIAJE + "");

        return incidentes;
    }

}
