package com.urbanoexpress.iridio3.pre.model.interactor;

import android.content.Context;
import android.util.Log;

import com.android.volley.VolleyError;
import com.orm.util.NamingHelper;
import com.urbanoexpress.iridio3.pre.data.rest.ApiRequest;
import com.urbanoexpress.iridio3.pre.data.rest.ApiRest;
import com.urbanoexpress.iridio3.pre.model.entity.Auditoria;
import com.urbanoexpress.iridio3.pre.model.entity.Data;
import com.urbanoexpress.iridio3.pre.model.entity.DescargaRuta;
import com.urbanoexpress.iridio3.pre.model.entity.EstadoRuta;
import com.urbanoexpress.iridio3.pre.model.entity.GuiaGestionada;
import com.urbanoexpress.iridio3.pre.model.entity.MotivoDescarga;
import com.urbanoexpress.iridio3.pre.model.entity.TipoDireccion;
import com.urbanoexpress.iridio3.pre.model.interactor.callback.RequestCallback;
import com.urbanoexpress.iridio3.pre.util.Preferences;
import com.urbanoexpress.iridio3.pre.util.Session;

import org.json.JSONObject;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RutaAuditoriaPendienteInteractor {

    private Context context;

    public RutaAuditoriaPendienteInteractor(Context context) {
        this.context = context;
    }

//    public void getRutas(String[] params, final RequestCallback callback) {
//        ApiRequest.getInstance().newParams();
//        ApiRequest.getInstance().putParams("linea_logistica",   params[0]);
//        ApiRequest.getInstance().putParams("vp_id_user",        params[1]);
//        ApiRequest.getInstance().putParams("vp_id_mac",         params[2]);
//        ApiRequest.getInstance().request(ApiRest.getInstance().getApiBaseUrl() +
//                        ApiRest.Api.GET_RUTAS_AUDITORIAS,
//                ApiRequest.TypeParams.FORM_DATA, new ApiRequest.ResponseListener() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        callback.onSuccess(response);
//                    }
//
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        callback.onError(error);
//                    }
//                });
//    }

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

    public static void uploadEstadoRutaV2(String[] params, final RequestCallback callback) {
        ApiRequest.getInstance().newParams();
        ApiRequest.getInstance().putParams("vp_rou_id",         params[0]);
        ApiRequest.getInstance().putParams("vp_rou_estado",     params[1]);
        ApiRequest.getInstance().putParams("vp_gps_px",         params[2]);
        ApiRequest.getInstance().putParams("vp_gps_py",         params[3]);
        ApiRequest.getInstance().putParams("vp_fecha",          params[4]);
        ApiRequest.getInstance().putParams("vp_hora",           params[5]);
        ApiRequest.getInstance().putParams("vp_km",             params[6]);
        ApiRequest.getInstance().putParams("vp_linea_negocio",  params[7]);
        ApiRequest.getInstance().putParams("id_user",           params[8]);
        ApiRequest.getInstance().putParams("imei",              params[9]);
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

    public static Auditoria selectRuta(String idServicio, String lineaNegocio) {
        List<Auditoria> ruta = Auditoria.find(Auditoria.class,
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

    public List<Auditoria> selectAllRutas() {
        return Auditoria.find(Auditoria.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("lineaNegocio") + " in (2, 3)",
                new String[]{Preferences.getInstance().getString("idUsuario", "")},
                "", NamingHelper.toSQLNameDefault("secuencia"), "");
    }

    public List<Auditoria> selectAllRutasActivas() {
        return Auditoria.find(Auditoria.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("eliminado") + " = ? and " +
                        NamingHelper.toSQLNameDefault("lineaNegocio") + " in (2, 3)",
                new String[]{Preferences.getInstance().getString("idUsuario", ""),
                        Data.Delete.NO + ""},
                "", NamingHelper.toSQLNameDefault("secuencia"), "");
    }

    public List<Auditoria> selectRutasPendientes() {
        List<Auditoria> auditorias = Auditoria.find(Auditoria.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("eliminado") + " = ? and " +
                        NamingHelper.toSQLNameDefault("estadoDescarga") + " = ? and " +
                        NamingHelper.toSQLNameDefault("lineaNegocio") + " in (2, 3)",
                Preferences.getInstance().getString("idUsuario", ""),
                Data.Delete.NO + "",
                Auditoria.EstadoDescarga.PENDIENTE + "");

        Collections.sort(auditorias, new Comparator<Auditoria>() {
            @Override
            public int compare(Auditoria lhs, Auditoria rhs) {
                return new Integer(lhs.getSecuencia()).compareTo(new Integer(rhs.getSecuencia()));
            }
        });

        return auditorias;
    }

    public List<Auditoria> selectRutasGestionadas() {
        List<Auditoria> auditorias = Auditoria.find(Auditoria.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("eliminado") + " = ? and " +
                        NamingHelper.toSQLNameDefault("estadoDescarga") + " = ? and " +
                        NamingHelper.toSQLNameDefault("lineaNegocio") + " in (2, 3)",
                new String[]{Preferences.getInstance().getString("idUsuario", ""),
                        Data.Delete.NO + "",
                        Auditoria.EstadoDescarga.GESTIONADO + ""});
        return auditorias;
    }

    public GuiaGestionada selectRutaGestionada(String idServicio, String lineaNegocio) {
        List<GuiaGestionada> guiaGestionada = GuiaGestionada.find(GuiaGestionada.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("idServicio") + " = ? and " +
                        NamingHelper.toSQLNameDefault("lineaNegocio") + " = ? and " +
                        // No incluir la gestion de llegada al Punto de Recolección, guias no recolectadas
                        NamingHelper.toSQLNameDefault("idMotivo") + " != 181 and " +
                        NamingHelper.toSQLNameDefault("dataSync") + " != " + Data.Sync.MANUAL,
                new String[]{Preferences.getInstance().getString("idUsuario", ""),
                        idServicio, lineaNegocio});

        if (guiaGestionada.size() > 0) {
            return guiaGestionada.get(0);
        }

        return null;
    }

    public List<Auditoria> selectGuiasMapaDistribucion() {
        return Auditoria.find(Auditoria.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("eliminado") + " = ? and " +
                        NamingHelper.toSQLNameDefault("estadoDescarga") + " in (10, 20) and " +
                        NamingHelper.toSQLNameDefault("lineaNegocio") + " in (2, 3)",
                new String[] {
                        Preferences.getInstance().getString("idUsuario", ""),
                        Data.Delete.NO + ""
                }, "", "", "");
    }

    public List<Auditoria> selectGuiasByGPS(String gpsLatitude, String gpsLongitude) {
        return Auditoria.find(Auditoria.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("eliminado") + " = ? and " +
                        NamingHelper.toSQLNameDefault("estadoDescarga") + " in (10, 20) and " +
                        NamingHelper.toSQLNameDefault("lineaNegocio") + " in (2, 3) and " +
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
                        NamingHelper.toSQLNameDefault("tipoRuta") + " = ?",
                Preferences.getInstance().getString("idUsuario", ""),
                Data.Delete.NO + "", EstadoRuta.TipoRuta.RUTA_AUDITORIA + "");

        return estadoRuta;
    }

    public DescargaRuta selectDescargaRuta(String idServicio, String lineaNegocio) {
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

    public List<Auditoria> selectManifiestos() {
        return Auditoria.find(Auditoria.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("eliminado") + " = ? and " +
                        NamingHelper.toSQLNameDefault("lineaNegocio") + " in (2, 3)",
                new String[]{Session.getUser().getIdUsuario() + "",
                        Data.Delete.NO + ""},
                NamingHelper.toSQLNameDefault("idManifiesto"), NamingHelper.toSQLNameDefault("idManifiesto"), "");
    }

    public List<Auditoria> selectRutasByIDManifiesto(String idManifiesto) {
        return Auditoria.find(Auditoria.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("idManifiesto") + " = ? and " +
                        NamingHelper.toSQLNameDefault("eliminado") + " = ? and " +
                        NamingHelper.toSQLNameDefault("lineaNegocio") + " in (2, 3)",
                new String[]{Session.getUser().getIdUsuario() + "",
                        idManifiesto,
                        Data.Delete.NO + ""},
                "", NamingHelper.toSQLNameDefault("secuencia"), "");
    }

    public List<Auditoria> selectIdRutas() {
        return Auditoria.find(Auditoria.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        // NamingHelper.toSQLNameDefault("eliminado") + " = ? and " +
                        NamingHelper.toSQLNameDefault("lineaNegocio") + " in (2, 3)",
                new String[]{Preferences.getInstance().getString("idUsuario", "")},
                NamingHelper.toSQLNameDefault("idRuta"), NamingHelper.toSQLNameDefault("secuencia"), "");
    }

    public void eliminarEstadoRutaSinSincronizar() {
        EstadoRuta.deleteAll(EstadoRuta.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("dataSync") + " = ? and " +
                        NamingHelper.toSQLNameDefault("lineaNegocio") + " in (2, 3) and " +
                        NamingHelper.toSQLNameDefault("tipoRuta") + " = ?",
                Preferences.getInstance().getString("idUsuario", ""),
                Data.Sync.MANUAL + "", EstadoRuta.TipoRuta.RUTA_AUDITORIA + "");
    }

    public long selectGuiasConSolicitaKilometraje() {
        return Auditoria.count(Auditoria.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("solicitaKilometraje") + " = ? and " +
                        NamingHelper.toSQLNameDefault("lineaNegocio") + " in (2, 3)",
                new String[]{Preferences.getInstance().getString("idUsuario", ""),
                        "1"},
                "", NamingHelper.toSQLNameDefault("secuencia"), "");
    }

    public long getTotalGuiasLogistica() {
        return Auditoria.count(Auditoria.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("lineaNegocio") + " = 3",
                new String[]{Preferences.getInstance().getString("idUsuario", "")},
                "", NamingHelper.toSQLNameDefault("secuencia"), "");
    }

}
