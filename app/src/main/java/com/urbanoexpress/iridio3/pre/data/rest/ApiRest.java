package com.urbanoexpress.iridio3.pre.data.rest;

import com.urbanoexpress.iridio3.data.remote.BaseUrl;
import com.urbanoexpress.iridio3.data.remote.urbano.UrbanoChileBaseUrl;
import com.urbanoexpress.iridio3.data.remote.urbano.UrbanoPeruBaseUrl;
import com.urbanoexpress.iridio3.pre.util.constant.Country;

/**
 * Created by mick on 19/05/16.
 */
public final class ApiRest {

    private static ApiRest apiRest;
    private String apiBaseUrl = null;

    private ApiRest() {
    }

    public interface Api {

        String GET_USER_PROFILE = "api/getUserProfile";
        String UPLOAD_PHOTO_USER_PROFILE = "api/uploadPhotoUserProfile";
        String CHANGE_PASSWORD_USER_PROFILE = "api/changePasswordUserProfile";
        String VALIDATE_USER_NAME_COURIER = "api/validateUserNameCourier";
        String CHANGE_USER_COURIER_PASSWORD = "api/changeUserCourierPassword";

        String EDIT_PLACA_RUTA = "api/editPlacaRuta";
        String GET_DATA_DEFAULT = "api/getDataDefault";
        String GET_MOTIVOS_DESCARGA = "api/getMotivosDescargaV2";
        String UPLOAD_ESTADO_RUTA = "api/uploadEstadoRutaV2";
        String UPLOAD_ESTADO_RUTA_KILOMETRAJE = "api/uploadEstadoRutaWithKilometrajeV2";
        String VALIDATE_SOLICITA_KILOMETRAJE = "api/validateSolicitaKilometraje";
        String UPLOAD_IMAGEN = "api/uploadGuiaImagen";
        String UPLOAD_IMAGEN_PARADA_PROGRAMADA = "api/uploadParadaProgramadaImagen";
        String UPLOAD_GPS = "api/uploadGPSRutaV2";
        String UPLOAD_GESTION_LLAMADA = "api/uploadGestionLlamada";
        String VALIDATE_CLAVE_CIERRE_RUTA = "api/logInCierreRuta";
        String VALIDATE_TRANSFERIR_GUIA = "api/validateTransferirGuia";
        String TRANSFERIR_GUIAS = "api/transferirGuias";
        String GET_GUIAS_ELECTRONICAS_RECOLECCION = "api/getGuiasElectronicasRecoleccion";
        String GET_CONTENEDORES_RECOLECCION = "api/getContenedoresRecoleccion";
        String VALIDATE_MANIFESTAR_GUIA = "api/validateManifestarGuia";
        String MANIFESTAR_GUIA = "api/manifestarGuia";
        String READ_BARRA_RECOLECCION = "api/validarBarraRecoleccion";

        String READ_BARRA_RECOLECCION_VALIJA_EXPRESS = "api/readBarraRecoleccionValijaExpress";

        String GET_GUIAS_RUTA_RURAL = "api/getGuiasRutaRural";

        String VERIFY_GUIAS_PENDIENTES_ELIMINADAS = "api/verifyGuiasPendientesEliminadas";
        String UPLOAD_SECUENCIA_RUTA_RURAL = "api/uploadSecuenciaGuiaRural";

        String GET_PLAN_VIAJE = "api/getPlanDeViaje";
        String VALIDATE_PLAN_VIAJE_ACTIVOS = "api/validatePlanDeViajeActivos";
        String UPDATE_PLACA_PLAN_VIAJE = "api/actualizarPlacaPlanviaje";
        String GET_DESPACHOS_PLAN_VIAJE = "api/getDespachosPlanviaje";
        String UPDATE_ESTADO_PLAN_VIAJE = "api/updateEstadoPlanViajeParadaV2";
        String GET_DESPACHOS_PENDIENTES_PLAN_VIAJE = "api/getDespachosPendientes";
        String UPLOAD_DESPACHOS_PENDIENTES_PLAN_VIAJE = "api/uploadDespachosPendientes";
        String UPDATE_ESTADO_DESPACHO_PLAN_VIAJE = "api/updateEstadoDespacho";
        String UPLOAD_INCIDENTE_RUTA = "api/uploadIncidenteRuta";

        String GET_RESUMEN_RUTA = "api/getResumenRuta";
        String GET_NOTIFICACIONES = "api/getNotificaciones";
        String UPLOAD_MARCAR_NOTIFICACION_COMO_LEIDA = "api/marcarNotificacionComoLeida";

        String SYNC_NUEVAS_GUIAS = "api/syncNuevasGuiasRutaDelDia";
        String VALIDATE_VERSION_APP = "api/validateVersionAppV2";

        String GET_MY_REVENUES = "api/MisGanancias/";
        String GET_WEEK_DETAIL = "api/SemanaDetail";
        String UPLOAD_FACTURA_MOTORIZADO = "api/uploadFileMotorizados";
        String GUIA_YAPE_QR = "api-apps/iridio/getGuiasRutaQR";

        String WAYPOINTS = "api/registro/Waypoints/";
        String VALIDATE_VERIFICATION_EMAIL = "api/registro/addPhone";
        String LOGIN_V3 = "api/registro/loginV3";
        String GET_RUTAS_V2 = "api/rutas/getGuiasRutaV5";
        String LOGIN_QR ="api/registro/loginRuta/";
        String UPLOAD_SECUENCIA_RUTA_V2 = "api/updateSecuenciaGuiaV3";
        String UPLOAD_ESTADO_RUTA_KILOMETRAJE_V2 = "api/uploadEstadoRutaWithKilometrajeV3";
        String UPLOAD_GUIA_GESTIONADA_V2 = "api/uploadGuiaGestionadaV5";
        String GET_DATOS_QRRUTA = "api/registro/datosRuta";
        String VALIDATE_DATOS_RUTA = "api/rutas/grabarRuta";
        String GET_DATOS_MAPA_RUTA_DEL_DIA = "api/datosMapa/";

        String URL_GENERAR_QR = "https://pay-api.dev-urbano.dev/api/v1/pagos/generar-qr";
        String URL_CONSULTAR_QR = "https://pay-api.dev-urbano.dev/api/v1/pagos/consultar";

        //???
        String UPLOAD_MOTORIZADO_LICENCE = "api-apps/iridio/RegisterLincense";
        String GET_RESUMEN_RUTA_RURAL = "api-apps/iridio/getResumenRutaRural";

        interface Google {
            String DISTANCE_MATRIX = "api/googleDistanceMatrix";
            String DIRECTIONS = "api/googleDirections";
            String GEOCODING = "api/googleGeocoding";
        }
    }

    public static synchronized ApiRest getInstance() {
        if (apiRest == null) {
            apiRest = new ApiRest();
        }
        return apiRest;
    }

    public static String withEndpoint(String endpoint) {
        return ApiRest.getInstance().apiBaseUrl + endpoint;
    }

    public void setApiBaseUrl(BaseUrl url) {
        if (url != null) {
            apiBaseUrl = url.getBaseUrl();
        }
    }

    public String getApiBaseUrl() {
        return apiBaseUrl;
    }

    public static BaseUrl buildUrbanoApiBaseUrl(int apiEnvironment, int country) {
        switch (country) {
            case Country.PERU:
                return new UrbanoPeruBaseUrl(apiEnvironment);
            case Country.CHILE:
                return new UrbanoChileBaseUrl(apiEnvironment);
            default:
                return null;
        }
    }
}
