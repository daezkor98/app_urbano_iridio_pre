package com.urbanoexpress.iridio.model.interactor;

import android.content.Context;

import com.android.volley.VolleyError;
import com.orm.util.NamingHelper;
import com.urbanoexpress.iridio.data.rest.ApiRequest;
import com.urbanoexpress.iridio.data.rest.ApiRest;
import com.urbanoexpress.iridio.model.entity.Data;
import com.urbanoexpress.iridio.model.entity.EstadoRuta;
import com.urbanoexpress.iridio.model.entity.GestionLlamada;
import com.urbanoexpress.iridio.model.entity.GuiaGestionada;
import com.urbanoexpress.iridio.model.entity.Imagen;
import com.urbanoexpress.iridio.model.entity.IncidenteRuta;
import com.urbanoexpress.iridio.model.entity.PlanDeViaje;
import com.urbanoexpress.iridio.model.entity.Ruta;
import com.urbanoexpress.iridio.model.entity.RutaEliminada;
import com.urbanoexpress.iridio.model.entity.SecuenciaRuta;
import com.urbanoexpress.iridio.model.entity.TrackLocation;
import com.urbanoexpress.iridio.model.interactor.callback.RequestCallback;
import com.urbanoexpress.iridio.util.Session;
import com.urbanoexpress.iridio.util.network.volley.MultipartJsonObjectRequest;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by mick on 02/08/16.
 */
public class DataSyncInteractor {

    private Context context;

    public DataSyncInteractor(Context context) {
        this.context = context;
    }

    public static void uploadEstadoRuta(String[] params, final RequestCallback callback) {
        ApiRequest.getInstance().newParams();
        ApiRequest.getInstance().putParams("vp_rou_id", params[0]);
        ApiRequest.getInstance().putParams("vp_rou_estado", params[1]);
        ApiRequest.getInstance().putParams("vp_gps_px", params[2]);
        ApiRequest.getInstance().putParams("vp_gps_py", params[3]);
        ApiRequest.getInstance().putParams("vp_fecha", params[4]);
        ApiRequest.getInstance().putParams("vp_hora", params[5]);
        ApiRequest.getInstance().putParams("vp_linea_negocio", params[6]);
        ApiRequest.getInstance().putParams("id_user", params[7]);
        ApiRequest.getInstance().putParams("imei", params[8]);
        ApiRequest.getInstance().request(ApiRest.getInstance().getApiBaseUrl() +
                        ApiRest.Api.UPLOAD_ESTADO_RUTA,
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

    public void uploadGuiaGestionada(String[] params, final RequestCallback callback) {
        ApiRequest.getInstance().newParams();
        ApiRequest.getInstance().putParams("vp_doc_id", params[0]);
        ApiRequest.getInstance().putParams("vp_mot_id", params[1]);
        ApiRequest.getInstance().putParams("vp_fecha", params[2]);
        ApiRequest.getInstance().putParams("vp_hora", params[3]);
        ApiRequest.getInstance().putParams("vp_doc_px", params[4]);
        ApiRequest.getInstance().putParams("vp_doc_py", params[5]);
        ApiRequest.getInstance().putParams("vp_nombre", params[6]);
        ApiRequest.getInstance().putParams("vp_nro_dni", params[7]);
        ApiRequest.getInstance().putParams("vp_num_pos", params[8]);
        ApiRequest.getInstance().putParams("vp_piezas", params[9]);
        ApiRequest.getInstance().putParams("vp_peso", params[10]);
        ApiRequest.getInstance().putParams("vp_guia_electronica", params[11]);
        ApiRequest.getInstance().putParams("vp_comentario", params[12]);
        ApiRequest.getInstance().putParams("vp_intento", params[13]);
        ApiRequest.getInstance().putParams("vp_recoleccion", params[14]);
        ApiRequest.getInstance().putParams("vp_tipo_zona", params[15]);
        ApiRequest.getInstance().putParams("vp_tipo_guia", params[16]);
        ApiRequest.getInstance().putParams("vp_tipo_direccion", params[17]);
        ApiRequest.getInstance().putParams("vp_tipo_medio_pago", params[18]);
        ApiRequest.getInstance().putParams("vp_mot_id_obs_entrega", params[19]);
        ApiRequest.getInstance().putParams("vp_comentario_obs_entrega", params[20]);
        ApiRequest.getInstance().putParams("vp_linea_negocio", params[21]);
        ApiRequest.getInstance().putParams("vp_user", params[22]);
        ApiRequest.getInstance().putParams("vp_id_mac", params[23]);
        ApiRequest.getInstance().request(ApiRest.getInstance().getApiBaseUrl() +
                        ApiRest.Api.UPLOAD_GUIA_GESTIONADA,
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

    public void uploadImagenDescarga(String[] params, MultipartJsonObjectRequest.DataPart data,
                                     final RequestCallback callback) {
        ApiRequest.getInstance().newParams();
        ApiRequest.getInstance().putParams("vp_doc_id", params[0]);
        ApiRequest.getInstance().putParams("vp_imagen", params[1]);
        ApiRequest.getInstance().putParams("vp_tipo", params[2]);
        ApiRequest.getInstance().putParams("vp_fecha", params[3]);
        ApiRequest.getInstance().putParams("vp_hora", params[4]);
        ApiRequest.getInstance().putParams("vp_img_px", params[5]);
        ApiRequest.getInstance().putParams("vp_img_py", params[6]);
        ApiRequest.getInstance().putParams("id_user", params[7]);
        ApiRequest.getInstance().putParams("id_servicios_adjuntos", params[8]);
        ApiRequest.getInstance().putParams("vp_linea_negocio", params[9]);
        ApiRequest.getInstance().putData("uploadedfile", data);
        ApiRequest.getInstance().request(ApiRest.getInstance().getApiBaseUrl() +
                        ApiRest.Api.UPLOAD_IMAGEN,
                ApiRequest.TypeParams.MULTIPART, new ApiRequest.ResponseListener() {
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

    public void uploadImagenParadaProgramada(String[] params, MultipartJsonObjectRequest.DataPart data,
                                             final RequestCallback callback) {
        ApiRequest.getInstance().newParams();
        ApiRequest.getInstance().putParams("vp_id_parada", params[0]);
        ApiRequest.getInstance().putParams("vp_imagen", params[1]);
        ApiRequest.getInstance().putParams("vp_tipo", params[2]);
        ApiRequest.getInstance().putParams("vp_fecha", params[3]);
        ApiRequest.getInstance().putParams("vp_hora", params[4]);
        ApiRequest.getInstance().putParams("vp_img_px", params[5]);
        ApiRequest.getInstance().putParams("vp_img_py", params[6]);
        ApiRequest.getInstance().putParams("id_user", params[7]);
        ApiRequest.getInstance().putParams("vp_linea_negocio", params[9]);
        ApiRequest.getInstance().putData("uploadedfile", data);
        ApiRequest.getInstance().request(ApiRest.getInstance().getApiBaseUrl() +
                        ApiRest.Api.UPLOAD_IMAGEN_PARADA_PROGRAMADA,
                ApiRequest.TypeParams.MULTIPART, new ApiRequest.ResponseListener() {
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

    public void uploadIncidenteRuta(String[] params, MultipartJsonObjectRequest.DataPart data,
                                    final RequestCallback callback) {
        ApiRequest.getInstance().newParams();
        ApiRequest.getInstance().putParams("vp_id_ruta", params[0]);
        ApiRequest.getInstance().putParams("vp_id_mot_incidente", params[1]);
        ApiRequest.getInstance().putParams("vp_imagen", params[2]);
        ApiRequest.getInstance().putParams("vp_tipo", params[3]);
        ApiRequest.getInstance().putParams("vp_fecha", params[4]);
        ApiRequest.getInstance().putParams("vp_hora", params[5]);
        ApiRequest.getInstance().putParams("vp_comentarios", params[6]);
        ApiRequest.getInstance().putParams("vp_px", params[7]);
        ApiRequest.getInstance().putParams("vp_py", params[8]);
        ApiRequest.getInstance().putParams("vp_linea_negocio", params[9]);
        ApiRequest.getInstance().putParams("vp_id_user", params[10]);
        ApiRequest.getInstance().putParams("vp_id_mac", params[11]);
        ApiRequest.getInstance().putData("uploadedfile", data);
        ApiRequest.getInstance().request(ApiRest.getInstance().getApiBaseUrl() +
                        ApiRest.Api.UPLOAD_INCIDENTE_RUTA,
                ApiRequest.TypeParams.MULTIPART, new ApiRequest.ResponseListener() {
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

    public void uploadTrackLocation(String[] params, final RequestCallback callback) {
        ApiRequest.getInstance().newParams();
        ApiRequest.getInstance().putParams("vp_gps_tracking", params[0]);
        ApiRequest.getInstance().putParams("vp_id_mac", params[1]);
        ApiRequest.getInstance().putParams("vp_id_user", params[2]);
        ApiRequest.getInstance().request(ApiRest.getInstance().getApiBaseUrl() +
                        ApiRest.Api.UPLOAD_GPS,
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

    public void uploadSecuenciaRuta(String[] params, final RequestCallback callback) {
        ApiRequest.getInstance().newParams();
        ApiRequest.getInstance().putParams("vp_secuencia", params[0]);
        ApiRequest.getInstance().putParams("vp_segundos", params[1]);
        ApiRequest.getInstance().putParams("vp_metros", params[2]);
        ApiRequest.getInstance().putParams("vp_imei_cel", params[3]);
        ApiRequest.getInstance().putParams("vp_user", params[4]);
        ApiRequest.getInstance().request(ApiRest.getInstance().getApiBaseUrl() +
                        ApiRest.Api.UPLOAD_SECUENCIA_RUTA,
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

    public void uploadSecuenciaRutaRural(String[] params, final RequestCallback callback) {
        ApiRequest.getInstance().newParams();
        ApiRequest.getInstance().putParams("vp_secuencia", params[0]);
        ApiRequest.getInstance().putParams("device_phone", params[1]);
        ApiRequest.getInstance().putParams("vp_id_user", params[2]);
        ApiRequest.getInstance().request(ApiRest.getInstance().getApiBaseUrl() +
                        ApiRest.Api.UPLOAD_SECUENCIA_RUTA_RURAL,
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

    public void uploadGestionLlamada(String[] params, final RequestCallback callback) {
        ApiRequest.getInstance().newParams();
        ApiRequest.getInstance().putParams("vp_gestion_llamadas", params[0]);
        ApiRequest.getInstance().putParams("vp_id_mac", params[1]);
        ApiRequest.getInstance().putParams("vp_id_user", params[2]);
        ApiRequest.getInstance().request(ApiRest.getInstance().getApiBaseUrl() +
                        ApiRest.Api.UPLOAD_GESTION_LLAMADA,
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

    public void syncNuevasGuias(String[] params, final RequestCallback callback) {
        ApiRequest.getInstance().newParams();
        ApiRequest.getInstance().putParams("vp_id_ruta", params[0]);
        ApiRequest.getInstance().putParams("imei", params[1]);
        ApiRequest.getInstance().putParams("guias", params[2]);
        ApiRequest.getInstance().putParams("linea_valores", params[3]);
        ApiRequest.getInstance().putParams("linea_logistica", params[4]);
        ApiRequest.getInstance().putParams("id_user", params[5]);
        ApiRequest.getInstance().request(ApiRest.getInstance().getApiBaseUrl() +
                        ApiRest.Api.SYNC_NUEVAS_GUIAS,
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

    public void verifyGuiasPendientesEliminadas(String[] params, final RequestCallback callback) {
        ApiRequest.getInstance().newParams();
        ApiRequest.getInstance().putParams("vp_guias", params[0]);
        ApiRequest.getInstance().putParams("device_phone", params[1]);
        ApiRequest.getInstance().putParams("vp_id_user", params[2]);
        ApiRequest.getInstance().request(ApiRest.getInstance().getApiBaseUrl() +
                        ApiRest.Api.VERIFY_GUIAS_PENDIENTES_ELIMINADAS,
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

    public List<EstadoRuta> selectAllEstadoRutaSyncPending() {
        List<EstadoRuta> estadoRuta = EstadoRuta.find(EstadoRuta.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("dataSync") + " = ?",
                Session.getUser().getIdUsuario(), Data.Sync.PENDING + "");

        return estadoRuta;
    }

    public List<EstadoRuta> selectEstadoRutasNoEliminados() {
        List<EstadoRuta> estadoRuta = EstadoRuta.find(EstadoRuta.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("eliminado") + " = ?",
                Session.getUser().getIdUsuario(), Data.Delete.NO + "");

        return estadoRuta;
    }

    public List<Ruta> selectAllRutas() {
        List<Ruta> ruta = Ruta.find(Ruta.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("eliminado") + " = ?",
                Session.getUser().getIdUsuario(), Data.Delete.NO + "");
        return ruta;
    }

    public List<Ruta> selectAllRutaPendiente(int tipoZona) {
        List<Ruta> ruta = Ruta.find(Ruta.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("tipoZona") + " = ? and " +
                        NamingHelper.toSQLNameDefault("eliminado") + " = ? and " +
                        NamingHelper.toSQLNameDefault("estadoDescarga") + " = ?",
                new String[]{Session.getUser().getIdUsuario() + "",
                        String.valueOf(tipoZona),
                        Data.Delete.NO + "",
                        Ruta.EstadoDescarga.PENDIENTE + ""},
                "", NamingHelper.toSQLNameDefault("secuencia"), "");
        return ruta;
    }

    public List<GuiaGestionada> selectAllRutaGestionada() {
        List<GuiaGestionada> guiaGestionadas = GuiaGestionada.find(GuiaGestionada.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("dataSync") + " = ?",
                Session.getUser().getIdUsuario(), Data.Sync.PENDING + "");

        return guiaGestionadas;
    }

    public List<IncidenteRuta> selectAllIncidentesRuta() {
        List<IncidenteRuta> incidenteRutas = IncidenteRuta.find(IncidenteRuta.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("tipoRuta") + " = ? and " +
                        NamingHelper.toSQLNameDefault("dataSync") + " = ?",
                Session.getUser().getIdUsuario(), IncidenteRuta.TipoRuta.PLAN_DE_VIAJE + "",
                Data.Sync.PENDING + "");

        return incidenteRutas;
    }

    public List<Imagen> selectAllImagenDescarga() {
        List<Imagen> imagenes = Imagen.find(Imagen.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("clasificacion") + " = ? and " +
                        NamingHelper.toSQLNameDefault("dataSync") + " = ?",
                Session.getUser().getIdUsuario(), Imagen.Tipo.GESTION_GUIA + "",
                Data.Sync.PENDING + "");

        return imagenes;
    }

    public List<Imagen> selectAllImagenes() {
        List<Imagen> imagenes = Imagen.find(Imagen.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("dataSync") + " = ?",
                Session.getUser().getIdUsuario(), Data.Sync.PENDING + "");

        return imagenes;
    }

    public List<TrackLocation> selectAllPendingTrackLocation() {
        List<TrackLocation> trackLocations = TrackLocation.find(TrackLocation.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("dataSync") + " = ?",
                Session.getUser().getIdUsuario(), Data.Sync.PENDING + "");

        return trackLocations;
    }

    public List<GestionLlamada> selectAllPendingGestionLlamada() {
        return GestionLlamada.find(GestionLlamada.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("dataSync") + " = ?",
                Session.getUser().getIdUsuario(), Data.Sync.PENDING + "");
    }

    public List<RutaEliminada> selectAllRutaEliminada() {
        List<RutaEliminada> rutasEliminadas = RutaEliminada.find(RutaEliminada.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("dataSync") + " = ?",
                Session.getUser().getIdUsuario(), Data.Sync.PENDING + "");

        return rutasEliminadas;
    }

    public List<SecuenciaRuta> selectAllSecuenciaRuta() {
        List<SecuenciaRuta> secuenciaRutas = SecuenciaRuta.find(SecuenciaRuta.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("eliminado") + " = ?",
                Session.getUser().getIdUsuario(), Data.Delete.NO + "");

        return secuenciaRutas;
    }

    public List<Ruta> selectLineasNegocio() {
        return Ruta.find(Ruta.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("lineaNegocio") + " in (2, 3, 4)",
                new String[]{Session.getUser().getIdUsuario()}, NamingHelper.toSQLNameDefault("lineaNegocio"),
                NamingHelper.toSQLNameDefault("secuencia"), "");
    }

    public List<Ruta> selectIdRutas() {
        return Ruta.find(Ruta.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("eliminado") + " = ? and " +
                        NamingHelper.toSQLNameDefault("lineaNegocio") + " in (2, 3, 4)",
                new String[]{Session.getUser().getIdUsuario(), Data.Delete.NO + ""},
                NamingHelper.toSQLNameDefault("idRuta"), NamingHelper.toSQLNameDefault("secuencia"), "");
    }

    public List<PlanDeViaje> selectPlanViaje() {
        return PlanDeViaje.find(PlanDeViaje.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ?",
                Session.getUser().getIdUsuario());
    }

    public List<EstadoRuta> selectAllEstadoRuta() {
        List<EstadoRuta> estadoRuta = EstadoRuta.find(EstadoRuta.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("eliminado") + " = ? ",
                Session.getUser().getIdUsuario(), Data.Delete.NO + "");

        return estadoRuta;
    }

    public long getTotalAllEstadoRuta() {
        return EstadoRuta.count(EstadoRuta.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("eliminado") + " = ? and " +
                        NamingHelper.toSQLNameDefault("lineaNegocio") + " in (2, 3, 4) and " +
                        "(" + NamingHelper.toSQLNameDefault("tipoRuta") + " is null or " +
                        NamingHelper.toSQLNameDefault("tipoRuta") + " = ?)",
                new String[]{Session.getUser().getIdUsuario(), Data.Delete.NO + "", EstadoRuta.TipoRuta.RUTA_DEL_DIA + ""});
    }

    public long getTotalRutasPendientes() {
        return Ruta.count(Ruta.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("eliminado") + " = ? and " +
                        NamingHelper.toSQLNameDefault("estadoDescarga") + " = ? and " +
                        NamingHelper.toSQLNameDefault("lineaNegocio") + " in (2, 3, 4)",
                new String[]{Session.getUser().getIdUsuario(), Data.Delete.NO + "", Ruta.EstadoDescarga.PENDIENTE + ""});
    }

    public long getTotalRutasGestionadas() {
        return Ruta.count(Ruta.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("eliminado") + " = ? and " +
                        NamingHelper.toSQLNameDefault("estadoDescarga") + " = ? and " +
                        NamingHelper.toSQLNameDefault("lineaNegocio") + " in (2, 3, 4)",
                new String[]{Session.getUser().getIdUsuario(), Data.Delete.NO + "", Ruta.EstadoDescarga.GESTIONADO + ""});
    }

}
