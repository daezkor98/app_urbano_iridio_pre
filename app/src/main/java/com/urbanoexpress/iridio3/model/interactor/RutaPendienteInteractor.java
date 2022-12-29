package com.urbanoexpress.iridio3.model.interactor;

import android.content.Context;
import android.util.Log;

import com.android.volley.VolleyError;
import com.orm.util.NamingHelper;

import org.json.JSONObject;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.urbanoexpress.iridio3.data.rest.ApiRequest;
import com.urbanoexpress.iridio3.data.rest.ApiRest;
import com.urbanoexpress.iridio3.model.entity.Data;
import com.urbanoexpress.iridio3.model.entity.DescargaRuta;
import com.urbanoexpress.iridio3.model.entity.EstadoRuta;
import com.urbanoexpress.iridio3.model.entity.GuiaGestionada;
import com.urbanoexpress.iridio3.model.entity.MotivoDescarga;
import com.urbanoexpress.iridio3.model.entity.Pieza;
import com.urbanoexpress.iridio3.model.entity.Ruta;
import com.urbanoexpress.iridio3.model.entity.TipoDireccion;
import com.urbanoexpress.iridio3.model.interactor.callback.RequestCallback;
import com.urbanoexpress.iridio3.urbanocore.DevUtilsKt;
import com.urbanoexpress.iridio3.util.Preferences;
import com.urbanoexpress.iridio3.util.Session;

/**
 * Created by mick on 22/06/16.
 */
public class RutaPendienteInteractor {

    private Context context;

    public RutaPendienteInteractor(Context context) {
        this.context = context;
    }

    public void getRutas(String[] params, final RequestCallback callback) {
        ApiRequest.getInstance().newParams();
        ApiRequest.getInstance().putParams("vp_id_ruta",                params[0]);
        ApiRequest.getInstance().putParams("linea_valores",             params[1]);
        ApiRequest.getInstance().putParams("linea_logistica",           params[2]);
        ApiRequest.getInstance().putParams("linea_logistica_especial",  params[3]);
        ApiRequest.getInstance().putParams("id_user",                   params[4]);
        ApiRequest.getInstance().putParams("imei",                      params[5]);
        ApiRequest.getInstance().request(ApiRest.getInstance().getApiBaseUrl() +
                        ApiRest.Api.GET_RUTAS,
                ApiRequest.TypeParams.FORM_DATA, new ApiRequest.ResponseListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        DevUtilsKt.logJson(response,"onResponse getGuiasRutaV4:");
                        callback.onSuccess(response);
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onError(error);
                    }
                });
    }

    public void getGuiasRutaRural(String[] params, final RequestCallback callback) {
        ApiRequest.getInstance().newParams();
        ApiRequest.getInstance().putParams("linea_logistica",   params[0]);
        ApiRequest.getInstance().putParams("id_user",           params[1]);
        ApiRequest.getInstance().putParams("device_phone",      params[2]);
        ApiRequest.getInstance().request(ApiRest.getInstance().getApiBaseUrl() +
                        ApiRest.Api.GET_GUIAS_RUTA_RURAL,
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

    public void getMotivos(String[] params, final RequestCallback callback) {
        ApiRequest.getInstance().newParams();
        ApiRequest.getInstance().putParams("vp_chk_id", params[0]);
        ApiRequest.getInstance().putParams("id_user",   params[1]);
        ApiRequest.getInstance().request(ApiRest.getInstance().getApiBaseUrl() +
                        ApiRest.Api.GET_MOTIVOS_DESCARGA,
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

    public static void editPlaca(String[] params, final RequestCallback callback) {
        ApiRequest.getInstance().newParams();
        ApiRequest.getInstance().putParams("vp_id_ruta",        params[0]);
        ApiRequest.getInstance().putParams("vp_placa",          params[1]);
        ApiRequest.getInstance().putParams("vp_linea_negocio",  params[2]);
        ApiRequest.getInstance().putParams("vp_id_user",        params[3]);
        ApiRequest.getInstance().request(ApiRest.getInstance().getApiBaseUrl() +
                        ApiRest.Api.EDIT_PLACA_RUTA,
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

    public static void getGuiasElectronicasRecoleccion(String[] params, final RequestCallback callback) {
        ApiRequest.getInstance().newParams();
        ApiRequest.getInstance().putParams("vp_id_recoleccion", params[0]);
        ApiRequest.getInstance().putParams("vp_linea_negocio",  params[1]);
        ApiRequest.getInstance().putParams("vp_id_user",        params[2]);
        ApiRequest.getInstance().request(ApiRest.getInstance().getApiBaseUrl() +
                        ApiRest.Api.GET_GUIAS_ELECTRONICAS_RECOLECCION,
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

    public static void getContenedoresRecoleccion(String[] params, final RequestCallback callback) {
        ApiRequest.getInstance().newParams();
        ApiRequest.getInstance().putParams("vp_id_recoleccion", params[0]);
        ApiRequest.getInstance().putParams("vp_linea_negocio",  params[1]);
        ApiRequest.getInstance().putParams("vp_id_user",        params[2]);
        ApiRequest.getInstance().request(ApiRest.getInstance().getApiBaseUrl() +
                        ApiRest.Api.GET_CONTENEDORES_RECOLECCION,
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

    public static void uploadEstadoRutaKilometraje(String[] params, final RequestCallback callback) {
        ApiRequest.getInstance().newParams();
        ApiRequest.getInstance().putParams("vp_rou_id",         params[0]);
        ApiRequest.getInstance().putParams("vp_rou_estado",     params[1]);
        ApiRequest.getInstance().putParams("vp_gps_px",         params[2]);
        ApiRequest.getInstance().putParams("vp_gps_py",         params[3]);
        ApiRequest.getInstance().putParams("vp_fecha",          params[4]);
        ApiRequest.getInstance().putParams("vp_hora",           params[5]);
        ApiRequest.getInstance().putParams("vp_km",             params[6]);
        ApiRequest.getInstance().putParams("vp_linea_negocio",  params[7]);
        ApiRequest.getInstance().putParams("firebaseToken",     params[8]);
        ApiRequest.getInstance().putParams("motivo_nt",         params[9]);
        ApiRequest.getInstance().putParams("id_user",           params[10]);
        ApiRequest.getInstance().putParams("imei",              params[11]);
        ApiRequest.getInstance().request(ApiRest.getInstance().getApiBaseUrl() +
                        ApiRest.Api.UPLOAD_ESTADO_RUTA_KILOMETRAJE,
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

    public static void validateSolicitaKilometraje(String[] params, final RequestCallback callback) {
        ApiRequest.getInstance().newParams();
        ApiRequest.getInstance().putParams("vp_rutas",      params[0]);
        ApiRequest.getInstance().putParams("vp_id_user",    params[1]);
        ApiRequest.getInstance().request(ApiRest.getInstance().getApiBaseUrl() +
                        ApiRest.Api.VALIDATE_SOLICITA_KILOMETRAJE,
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

    public static void readBarraRecoleccion(String[] params, final RequestCallback callback) {
        ApiRequest.getInstance().newParams();
        ApiRequest.getInstance().putParams("vp_barra",      params[0]);
        ApiRequest.getInstance().putParams("vp_srec_id",    params[1]);
        ApiRequest.getInstance().putParams("vp_id_user",    params[2]);
        ApiRequest.getInstance().request(ApiRest.getInstance().getApiBaseUrl() +
                        ApiRest.Api.READ_BARRA_RECOLECCION,
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

    public List<MotivoDescarga> selectAllMotivos(int tipoMotivo, String lineaNegocio) {
        return MotivoDescarga.find(MotivoDescarga.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("tipo") + " = ? and " +
                        NamingHelper.toSQLNameDefault("lineaNegocio") + " in (0, ?)",
                Preferences.getInstance().getString("idUsuario", ""),
                tipoMotivo + "", lineaNegocio);
    }

    public List<TipoDireccion> selectAllTipoDireccion() {
        return TipoDireccion.find(TipoDireccion.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ?",
                Preferences.getInstance().getString("idUsuario", ""));
    }

    public void deleteMotivos(int tipoMotivo) {
        MotivoDescarga.deleteAll(MotivoDescarga.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("tipo") + " = ?",
                Preferences.getInstance().getString("idUsuario", ""),
                tipoMotivo + "");
    }

    public static Ruta selectRuta(String idServicio, String lineaNegocio) {
        List<Ruta> ruta = Ruta.find(Ruta.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("idServicio") + " = ? and " +
                        NamingHelper.toSQLNameDefault("lineaNegocio") + " = ?",
                Preferences.getInstance().getString("idUsuario", ""),
                idServicio, lineaNegocio);

        if (ruta.size() > 0) {
            return ruta.get(0);
        }

        return null;
    }

    public List<Ruta> selectAllRutas() {
        return Ruta.find(Ruta.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("lineaNegocio") + " in (2, 3, 4)",
                new String[]{Preferences.getInstance().getString("idUsuario", "")},
                "", NamingHelper.toSQLNameDefault("secuencia"), "");
    }

    public List<Ruta> selectAllRutasActivas() {
        return Ruta.find(Ruta.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("eliminado") + " = ? and " +
                        NamingHelper.toSQLNameDefault("lineaNegocio") + " in (2, 3, 4)",
                new String[]{Preferences.getInstance().getString("idUsuario", ""),
                        Data.Delete.NO + ""},
                "", NamingHelper.toSQLNameDefault("secuencia"), "");
    }

    public List<Ruta> selectRutasPendientes() {
        List<Ruta> ruta = Ruta.find(Ruta.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("eliminado") + " = ? and " +
                        NamingHelper.toSQLNameDefault("estadoDescarga") + " = ? and " +
                        NamingHelper.toSQLNameDefault("lineaNegocio") + " in (2, 3, 4)",
                Preferences.getInstance().getString("idUsuario", ""),
                String.valueOf(Data.Delete.NO), String.valueOf(Ruta.EstadoDescarga.PENDIENTE));

        Collections.sort(ruta, new Comparator<Ruta>() {
            @Override
            public int compare(Ruta lhs, Ruta rhs) {
                return new Integer(lhs.getSecuencia()).compareTo(new Integer(rhs.getSecuencia()));
            }
        });

        return ruta;
    }

    public List<Ruta> selectRutasGestionadas() {
        List<Ruta> ruta = Ruta.find(Ruta.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("eliminado") + " = ? and " +
                        NamingHelper.toSQLNameDefault("estadoDescarga") + " = ? and " +
                        NamingHelper.toSQLNameDefault("lineaNegocio") + " in (2, 3, 4)",
                new String[]{Preferences.getInstance().getString("idUsuario", ""),
                        Data.Delete.NO + "",
                        Ruta.EstadoDescarga.GESTIONADO + ""},
                "", NamingHelper.toSQLNameDefault("resultadoGestion") + " DESC", "");
        return ruta;
    }

    public List<Ruta> selectRutasVisitadas() {
        List<Ruta> ruta = Ruta.find(Ruta.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("eliminado") + " = ? and " +
                        NamingHelper.toSQLNameDefault("tipo") + " = ? and " +
                        NamingHelper.toSQLNameDefault("estadoDescarga") + " = ? and " +
                        NamingHelper.toSQLNameDefault("resultadoGestion") + " = ? and " +
                        NamingHelper.toSQLNameDefault("lineaNegocio") + " in (2, 3, 4)",
                new String[]{Preferences.getInstance().getString("idUsuario", ""),
                        String.valueOf(Data.Delete.NO),
                        "E",
                        String.valueOf(Ruta.EstadoDescarga.GESTIONADO),
                        String.valueOf(Ruta.ResultadoGestion.NO_EFECTIVA)},
                "", "", "");
        return ruta;
    }

    public static GuiaGestionada selectRutaGestionada(String idServicio, String lineaNegocio) {
        List<GuiaGestionada> guiaGestionada = GuiaGestionada.find(GuiaGestionada.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("idServicio") + " = ? and " +
                        NamingHelper.toSQLNameDefault("lineaNegocio") + " = ? and " +
                        // No incluir la gestion de llegada al Punto de Recolección, guias no recolectadas
                        NamingHelper.toSQLNameDefault("idMotivo") + " != 181 and " +
                        NamingHelper.toSQLNameDefault("dataSync") + " != " + Data.Sync.MANUAL,
                Preferences.getInstance().getString("idUsuario", ""), idServicio, lineaNegocio);

        if (guiaGestionada.size() > 0) {
            return guiaGestionada.get(0);
        }

        return null;
    }

    public List<Ruta> selectGuiasMapaDistribucion() {
        return Ruta.find(Ruta.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("eliminado") + " = ? and " +
                        NamingHelper.toSQLNameDefault("estadoDescarga") + " in (10, 20) and " +
                        NamingHelper.toSQLNameDefault("lineaNegocio") + " in (2, 3, 4)",
                new String[] {
                        Preferences.getInstance().getString("idUsuario", ""),
                        Data.Delete.NO + ""
                }, "", "", "");
    }

    public List<Ruta> selectGuiasByGPS(String gpsLatitude, String gpsLongitude) {
        return Ruta.find(Ruta.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("eliminado") + " = ? and " +
                        NamingHelper.toSQLNameDefault("estadoDescarga") + " in (10, 20) and " +
                        NamingHelper.toSQLNameDefault("lineaNegocio") + " in (2, 3, 4) and " +
                        NamingHelper.toSQLNameDefault("gpsLatitude") + " = ? and " +
                        NamingHelper.toSQLNameDefault("gpsLongitude") + " = ? ",
                        Preferences.getInstance().getString("idUsuario", ""),
                        Data.Delete.NO + "", gpsLatitude, gpsLongitude);
    }

    public List<EstadoRuta> selectAllEstadoRuta() {
        List<EstadoRuta> estadoRuta = EstadoRuta.find(EstadoRuta.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("eliminado") + " = ? and " +
                        NamingHelper.toSQLNameDefault("lineaNegocio") + " in (2, 3, 4) and " +
                        "(" + NamingHelper.toSQLNameDefault("tipoRuta") + " is null or " +
                        NamingHelper.toSQLNameDefault("tipoRuta") + " = ?)",
                Preferences.getInstance().getString("idUsuario", ""),
                Data.Delete.NO + "", EstadoRuta.TipoRuta.RUTA_DEL_DIA + "");

        return estadoRuta;
    }

    public static DescargaRuta selectDescargaRuta(String idServicio, String lineaNegocio) {
        List<DescargaRuta> descargaRutaList = DescargaRuta.find(DescargaRuta.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("idServicio") + " = ? and " +
                        NamingHelper.toSQLNameDefault("lineaNegocio") + " = ? ",
                Preferences.getInstance().getString("idUsuario", ""),
                idServicio, lineaNegocio);

        if (descargaRutaList.size() > 0) {
            return descargaRutaList.get(0);
        }

        return null;
    }

    public GuiaGestionada selectGuiaGestionada(String idServicio, String lineaNegocio) {
        List<GuiaGestionada> guiaGestionadaList = GuiaGestionada.find(GuiaGestionada.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("idServicio") + " = ? and " +
                        NamingHelper.toSQLNameDefault("lineaNegocio") + " = ? ",
                Preferences.getInstance().getString("idUsuario", ""),
                idServicio, lineaNegocio);

        if (guiaGestionadaList.size() > 0) {
            Log.d("ACTIVITY", "TOTAL GUIAS GESTIONADAS: " + guiaGestionadaList.size());
            return guiaGestionadaList.get(0);
        }

        return null;
    }

    public List<Ruta> selectManifiestos() {
        return Ruta.find(Ruta.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("eliminado") + " = ? and " +
                        NamingHelper.toSQLNameDefault("lineaNegocio") + " in (2, 3, 4)",
                new String[]{Session.getUser().getIdUsuario() + "",
                        Data.Delete.NO + ""},
                NamingHelper.toSQLNameDefault("idManifiesto"), NamingHelper.toSQLNameDefault("idManifiesto"), "");
    }

    public List<Ruta> selectRutasByIDManifiesto(String idManifiesto) {
        return Ruta.find(Ruta.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("idManifiesto") + " = ? and " +
                        NamingHelper.toSQLNameDefault("eliminado") + " = ? and " +
                        NamingHelper.toSQLNameDefault("lineaNegocio") + " in (2, 3, 4)",
                new String[]{Session.getUser().getIdUsuario() + "",
                        idManifiesto,
                        Data.Delete.NO + ""},
                "", NamingHelper.toSQLNameDefault("secuencia"), "");
    }

    public List<Ruta> selectIdRutas() {
        return Ruta.find(Ruta.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        // NamingHelper.toSQLNameDefault("eliminado") + " = ? and " +
                        NamingHelper.toSQLNameDefault("lineaNegocio") + " in (2, 3, 4)",
                new String[]{Preferences.getInstance().getString("idUsuario", "")},
                NamingHelper.toSQLNameDefault("idRuta"), NamingHelper.toSQLNameDefault("secuencia"), "");
    }

    public void eliminarEstadoRutaSinSincronizar() {
        EstadoRuta.deleteAll(EstadoRuta.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("dataSync") + " = ? and " +
                        NamingHelper.toSQLNameDefault("lineaNegocio") + " in (2, 3, 4) and " +
                        "(" + NamingHelper.toSQLNameDefault("tipoRuta") + " is null or " +
                        NamingHelper.toSQLNameDefault("tipoRuta") + " = ?)",
                Preferences.getInstance().getString("idUsuario", ""),
                Data.Sync.MANUAL + "", EstadoRuta.TipoRuta.RUTA_DEL_DIA + "");
    }

    public long selectGuiasConSolicitaKilometraje() {
        return Ruta.count(Ruta.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("solicitaKilometraje") + " = ? and " +
                        NamingHelper.toSQLNameDefault("lineaNegocio") + " in (2, 3, 4)",
                new String[]{Preferences.getInstance().getString("idUsuario", ""),
                        "1"},
                "", NamingHelper.toSQLNameDefault("secuencia"), "");
    }

    public long getTotalGuiasLogistica() {
        return Ruta.count(Ruta.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("lineaNegocio") + " = 3",
                new String[]{Preferences.getInstance().getString("idUsuario", "")},
                "", NamingHelper.toSQLNameDefault("secuencia"), "");
    }

    public long getTotalAllEstadoRuta() {
        return EstadoRuta.count(EstadoRuta.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("eliminado") + " = ? and " +
                        NamingHelper.toSQLNameDefault("lineaNegocio") + " in (2, 3, 4) and " +
                        "(" + NamingHelper.toSQLNameDefault("tipoRuta") + " is null or " +
                        NamingHelper.toSQLNameDefault("tipoRuta") + " = ?)",
                new String[]{Preferences.getInstance().getString("idUsuario", ""),
                Data.Delete.NO + "", EstadoRuta.TipoRuta.RUTA_DEL_DIA + ""});
    }

    public Pieza selectPieza(String idPieza, String idServicio, String lineaNegocio) {
        List<Pieza> list = Pieza.find(Pieza.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("idPieza") + " = ? and " +
                        NamingHelper.toSQLNameDefault("idServicioGuia") + " = ? and " +
                        NamingHelper.toSQLNameDefault("lineaNegocio") + " = ?",
                Preferences.getInstance().getString("idUsuario", ""),
                idPieza, idServicio, lineaNegocio);

        if (list.size() > 0) { return list.get(0); }
        return null;
    }

    public Pieza selectPiezaByBarra(String barra) {
        List<Pieza> list = Pieza.find(Pieza.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("barra") + " = ?",
                Preferences.getInstance().getString("idUsuario", ""), barra);

        if (list.size() > 0) { return list.get(0); }
        return null;
    }

    public static Ruta findValijaByBarra(String barra) {
        List<Ruta> list = Ruta.find(Ruta.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("eliminado") + " = ? and " +
                        NamingHelper.toSQLNameDefault("estadoDescarga") + " = ? and " +
                        NamingHelper.toSQLNameDefault("guia") + " = ? and " +
                        NamingHelper.toSQLNameDefault("lineaNegocio") + " = 3",
                Preferences.getInstance().getString("idUsuario", ""),
                String.valueOf(Data.Delete.NO), String.valueOf(Ruta.EstadoDescarga.PENDIENTE), barra);

        if (list.size() > 0) { return list.get(0); }
        return null;
    }

    public static List<Pieza> selectPiezas(String idServicio, String lineaNegocio) {
        return Pieza.find(Pieza.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("idServicioGuia") + " = ? and " +
                        NamingHelper.toSQLNameDefault("lineaNegocio") + " = ?",
                Preferences.getInstance().getString("idUsuario", ""), idServicio, lineaNegocio);
    }

    public static long getTotalPiezasByGuia(String idServicio, String lineaNegocio) {
        return Pieza.count(Pieza.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("idServicioGuia") + " = ? and " +
                        NamingHelper.toSQLNameDefault("lineaNegocio") + " = ?",
                new String[]{Preferences.getInstance().getString("idUsuario", ""),
                        idServicio, lineaNegocio});
    }

    public long getTotalRutasPendientes() {
        return Ruta.count(Ruta.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("eliminado") + " = ? and " +
                        NamingHelper.toSQLNameDefault("estadoDescarga") + " = ? and " +
                        NamingHelper.toSQLNameDefault("lineaNegocio") + " in (2, 3, 4)",
                new String[]{Preferences.getInstance().getString("idUsuario", ""),
                Data.Delete.NO + "", Ruta.EstadoDescarga.PENDIENTE + ""});
    }

    public long getTotalRutasGestionadas() {
        return Ruta.count(Ruta.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("eliminado") + " = ? and " +
                        NamingHelper.toSQLNameDefault("estadoDescarga") + " = ? and " +
                        NamingHelper.toSQLNameDefault("lineaNegocio") + " in (2, 3, 4)",
                new String[]{Preferences.getInstance().getString("idUsuario", ""),
                        Data.Delete.NO + "", Ruta.EstadoDescarga.GESTIONADO + ""});
    }

    public long getTotalGestionesByIdServicio(String idServicio, String lineaNegocio) {
        return GuiaGestionada.count(GuiaGestionada.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("idServicio") + " = ? and " +
                        NamingHelper.toSQLNameDefault("lineaNegocio") + " = ? ",
                new String[]{Preferences.getInstance().getString("idUsuario", ""),
                        idServicio, lineaNegocio});
    }

}