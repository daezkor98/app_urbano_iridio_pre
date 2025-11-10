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
        String LOGIN_v2 = "api/registro/loginV2";
        String GET_RUTAS_V2 = "api/rutas/getGuiasRutaV5";
        String LOGIN_QR ="api/registro/loginRuta/";
        String UPLOAD_SECUENCIA_RUTA_V2 = "api/updateSecuenciaGuiaV3";
        String UPLOAD_ESTADO_RUTA_KILOMETRAJE_V2 = "api/uploadEstadoRutaWithKilometrajeV3";
        String UPLOAD_GUIA_GESTIONADA_V2 = "api/uploadGuiaGestionadaV5";
        String GET_DATOS_QRRUTA = "api/registro/datosRuta";
        String VALIDATE_DATOS_RUTA = "api/rutas/grabarRuta";
        String GET_DATOS_MAPA_RUTA_DEL_DIA = "api/datosMapa/";

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

    public String getApiBaseUrlV2(){
        return "https://api.geo.dev-urbano.dev/iridio/";
        //return "https://bkd-tms.urbanoexpress.com.pe/iridio/";
    }

}

//public final class ApiRest {
//
//    private static ApiRest apiRest;
//    private String apiBaseUrl = null;
//
//    private ApiRest() {
//    }
//
//    public interface Api {
//
//        String CONFIG_PHONE = "api-apps/iridio/configPhone";
//        String VALIDATE_VERIFICATION_CODE = "api-apps/iridio/validateVerificationCode";
//
//        String GET_USER_PROFILE = "api-apps/iridio/getUserProfile";
//        String UPLOAD_PHOTO_USER_PROFILE = "api-apps/iridio/uploadPhotoUserProfile";
//        String CHANGE_PASSWORD_USER_PROFILE = "api-apps/iridio/changePasswordUserProfile";
//        String LOGIN = "api-apps/iridio/loginV2";
//        String VALIDATE_USER_NAME_COURIER = "api-apps/iridio/validateUserNameCourier";
//        String CHANGE_USER_COURIER_PASSWORD = "api-apps/iridio/changeUserCourierPassword";
//
//        String GET_RUTAS = "api-apps/iridio/getGuiasRutaV4";
//        String EDIT_PLACA_RUTA = "api-apps/iridio/editPlacaRuta";
//        String GET_DATA_DEFAULT = "api-apps/iridio/getDataDefault";
//        String GET_MOTIVOS_DESCARGA = "api-apps/iridio/getMotivosDescargaV2";
//        String UPLOAD_ESTADO_RUTA = "api-apps/iridio/uploadEstadoRutaV2";
//        String UPLOAD_ESTADO_RUTA_KILOMETRAJE = "api-apps/iridio/uploadEstadoRutaWithKilometrajeV2";
//        String VALIDATE_SOLICITA_KILOMETRAJE = "api-apps/iridio/validateSolicitaKilometraje";
//        String UPLOAD_GUIA_GESTIONADA = "api-apps/iridio/uploadGuiaGestionadaV5";
//        String UPLOAD_IMAGEN = "api-apps/iridio/uploadGuiaImagen";
//        String UPLOAD_IMAGEN_PARADA_PROGRAMADA = "api-apps/iridio/uploadParadaProgramadaImagen";
//        String UPLOAD_GPS = "api-apps/iridio/uploadGPSRutaV2";
//        String UPLOAD_GESTION_LLAMADA = "api-apps/iridio/uploadGestionLlamada";
//        String UPLOAD_SECUENCIA_RUTA = "api-apps/iridio/updateSecuenciaGuiaV2";
//        String VALIDATE_CLAVE_CIERRE_RUTA = "api-apps/iridio/logInCierreRuta";
//        String VALIDATE_TRANSFERIR_GUIA = "api-apps/iridio/validateTransferirGuia";
//        String TRANSFERIR_GUIAS = "api-apps/iridio/transferirGuias";
//        String GET_GUIAS_ELECTRONICAS_RECOLECCION = "api-apps/iridio/getGuiasElectronicasRecoleccion";
//        String GET_CONTENEDORES_RECOLECCION = "api-apps/iridio/getContenedoresRecoleccion";
//        String VALIDATE_MANIFESTAR_GUIA = "api-apps/iridio/validateManifestarGuia";
//        String MANIFESTAR_GUIA = "api-apps/iridio/manifestarGuia";
//        String READ_BARRA_RECOLECCION = "api-apps/iridio/validarBarraRecoleccion";
//
//        String READ_BARRA_RECOLECCION_VALIJA_EXPRESS = "api-apps/iridio/readBarraRecoleccionValijaExpress";
//
//        String GET_RUTAS_AUDITORIAS = "api-apps/iridio/getAuditoriasRuta";
//
//        String GET_GUIAS_RUTA_RURAL = "api-apps/iridio/getGuiasRutaRural";
//
//        String VERIFY_GUIAS_PENDIENTES_ELIMINADAS = "api-apps/iridio/verifyGuiasPendientesEliminadas";
//        String UPLOAD_SECUENCIA_RUTA_RURAL = "api-apps/iridio/uploadSecuenciaGuiaRural";
//
//        String GET_PLAN_VIAJE = "api-apps/iridio/getPlanDeViaje";
//        String VALIDATE_PLAN_VIAJE_ACTIVOS = "api-apps/iridio/validatePlanDeViajeActivos";
//        String UPDATE_PLACA_PLAN_VIAJE = "api-apps/iridio/actualizarPlacaPlanviaje";
//        String GET_DESPACHOS_PLAN_VIAJE = "api-apps/iridio/getDespachosPlanviaje";
//        String UPDATE_ESTADO_PLAN_VIAJE = "api-apps/iridio/updateEstadoPlanViajeParadaV2";
//        String GET_DESPACHOS_PENDIENTES_PLAN_VIAJE = "api-apps/iridio/getDespachosPendientes";
//        String UPLOAD_DESPACHOS_PENDIENTES_PLAN_VIAJE = "api-apps/iridio/uploadDespachosPendientes";
//        String UPDATE_ESTADO_DESPACHO_PLAN_VIAJE = "api-apps/iridio/updateEstadoDespacho";
//        String UPLOAD_INCIDENTE_RUTA = "api-apps/iridio/uploadIncidenteRuta";
//
//        String GET_RESUMEN_RUTA = "api-apps/iridio/getResumenRuta";
//        String GET_RESUMEN_RUTA_RURAL = "api-apps/iridio/getResumenRutaRural";
//
//        String GET_NOTIFICACIONES = "api-apps/iridio/getNotificaciones";
//        String UPLOAD_MARCAR_NOTIFICACION_COMO_LEIDA = "api-apps/iridio/marcarNotificacionComoLeida";
//
//        String SYNC_NUEVAS_GUIAS = "api-apps/iridio/syncNuevasGuiasRutaDelDia";
//        String VALIDATE_VERSION_APP = "api-apps/iridio/validateVersionAppV2";
//
//        String GET_MY_REVENUES = "api-apps/iridio/MisGanancias";
//        String GET_WEEK_DETAIL = "api-apps/iridio/SemanaDetail";
//        String UPLOAD_FACTURA_MOTORIZADO = "api-apps/iridio/uploadFileMotorizados";
//
//        String UPLOAD_MOTORIZADO_LICENCE = "api-apps/iridio/RegisterLincense";
//
//        String GUIA_YAPE_QR = "api-apps/iridio/getGuiasRutaQR";
//
//        String WAYPOINTS = "iridio/api/registro/Waypoints/";
//        String VALIDATE_VERIFICATION_EMAIL = "iridio/api/registro/addPhone";
//        String LOGIN_v2 = "iridio/api/registro/loginV2";
//        String GET_RUTAS_V2 = "iridio/api/rutas/getGuiasRutaV5";
//        String LOGIN_QR ="iridio/api/registro/loginRuta/";
//        String UPLOAD_SECUENCIA_RUTA_V2 = "iridio/api/updateSecuenciaGuiaV3";
//        String UPLOAD_ESTADO_RUTA_KILOMETRAJE_V2 = "iridio/api/uploadEstadoRutaWithKilometrajeV3";
//        String UPLOAD_GUIA_GESTIONADA_V2 = "iridio/api/uploadGuiaGestionadaV5";
//        String GET_DATOS_QRRUTA = "iridio/api/registro/datosRuta";
//        String VALIDATE_DATOS_RUTA = "iridio/api/rutas/grabarRuta";
//
//        interface Google {
//            String DISTANCE_MATRIX = "api-apps/iridio/googleDistanceMatrix";
//            String DIRECTIONS = "api-apps/iridio/googleDirections";
//            String GEOCODING = "api-apps/iridio/googleGeocoding";
//        }
//    }
//
//    public static synchronized ApiRest getInstance() {
//        if (apiRest == null) {
//            apiRest = new ApiRest();
//        }
//        return apiRest;
//    }
//
//    public static String withEndpoint(String endpoint) {
//        return ApiRest.getInstance().apiBaseUrl + endpoint;
//    }
//
//    public void setApiBaseUrl(BaseUrl url) {
//        if (url != null) {
//            apiBaseUrl = url.getBaseUrl();
//        }
//    }
//
//    public String getApiBaseUrl() {
//        return apiBaseUrl;
//    }
//
//    public static BaseUrl buildUrbanoApiBaseUrl(int apiEnvironment, int country) {
//        switch (country) {
//            case Country.PERU:
//                return new UrbanoPeruBaseUrl(apiEnvironment);
//            case Country.CHILE:
//                return new UrbanoChileBaseUrl(apiEnvironment);
//            default:
//                return null;
//        }
//    }
//
//    public String getApiBaseUrlV2(){
//        return "https://api.geo.dev-urbano.dev/iridio/";
//        //return "https://bkd-tms.urbanoexpress.com.pe/iridio/";
//    }
//
//}
