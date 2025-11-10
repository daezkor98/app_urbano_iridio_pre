package com.urbanoexpress.iridio3.pre.model.interactor;

import com.orm.util.NamingHelper;
import com.urbanoexpress.iridio3.pre.model.entity.Data;
import com.urbanoexpress.iridio3.pre.model.entity.EstadoRuta;
import com.urbanoexpress.iridio3.pre.model.entity.GestionLlamada;
import com.urbanoexpress.iridio3.pre.model.entity.GuiaGestionada;
import com.urbanoexpress.iridio3.pre.model.entity.Imagen;
import com.urbanoexpress.iridio3.pre.model.entity.Ruta;
import com.urbanoexpress.iridio3.pre.model.entity.TrackLocation;
import com.urbanoexpress.iridio3.pre.util.Preferences;

/**
 * Created by mickve on 23/01/18.
 */

public class InformacionRutaInteractor {

    private String LINEA_NEGOCIO_QUERY = "";

    public InformacionRutaInteractor(String LINEA_NEGOCIO_QUERY) {
        this.LINEA_NEGOCIO_QUERY = LINEA_NEGOCIO_QUERY;
    }

    public long getTotalGuias() {
        return Ruta.count(Ruta.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("eliminado") + " = ? and " +
                        NamingHelper.toSQLNameDefault("lineaNegocio") + " in " + LINEA_NEGOCIO_QUERY,
                new String[]{Preferences.getInstance().getString("idUsuario", ""),
                        Data.Delete.NO + ""});
    }

    public long getTotalGuiasPendientes() {
        return Ruta.count(Ruta.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("eliminado") + " = ? and " +
                        NamingHelper.toSQLNameDefault("estadoDescarga") + " = ? and " +
                        NamingHelper.toSQLNameDefault("lineaNegocio") + " in " + LINEA_NEGOCIO_QUERY,
                new String[]{Preferences.getInstance().getString("idUsuario", ""),
                        Data.Delete.NO + "", Ruta.EstadoDescarga.PENDIENTE + ""});
    }

    public long getTotalGuiasGestionadas() {
        return Ruta.count(Ruta.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("eliminado") + " = ? and " +
                        NamingHelper.toSQLNameDefault("estadoDescarga") + " = ? and " +
                        NamingHelper.toSQLNameDefault("lineaNegocio") + " in " + LINEA_NEGOCIO_QUERY,
                new String[]{Preferences.getInstance().getString("idUsuario", ""),
                        Data.Delete.NO + "", Ruta.EstadoDescarga.GESTIONADO + ""});
    }

    public long getTotalGuiasGestionadasFallidas() {
        return Ruta.count(Ruta.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("eliminado") + " = ? and " +
                        NamingHelper.toSQLNameDefault("estadoDescarga") + " = ? and " +
                        NamingHelper.toSQLNameDefault("resultadoGestion") + " = ? and " +
                        NamingHelper.toSQLNameDefault("lineaNegocio") + " in " + LINEA_NEGOCIO_QUERY,
                new String[]{Preferences.getInstance().getString("idUsuario", ""),
                        String.valueOf(Data.Delete.NO),
                        String.valueOf(Ruta.EstadoDescarga.GESTIONADO),
                        String.valueOf(Ruta.ResultadoGestion.NO_EFECTIVA)});
    }

    public long getTotalImagenes() {
        return Imagen.count(Imagen.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("clasificacion") + " = ? and " +
                        NamingHelper.toSQLNameDefault("lineaNegocio") + " in " + LINEA_NEGOCIO_QUERY,
                new String[]{Preferences.getInstance().getString("idUsuario", ""),
                        Imagen.Tipo.GESTION_GUIA + ""});
    }

    public long getTotalGestiones() {
        return GuiaGestionada.count(GuiaGestionada.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("eliminado") + " = ? and " +
                        NamingHelper.toSQLNameDefault("lineaNegocio") + " in " + LINEA_NEGOCIO_QUERY,
                new String[]{Preferences.getInstance().getString("idUsuario", ""),
                        Data.Delete.NO + ""});
    }

    public long getTotalTramasGPS() {
        return TrackLocation.count(TrackLocation.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("lineaNegocio") + " in " + LINEA_NEGOCIO_QUERY,
                new String[]{Preferences.getInstance().getString("idUsuario", "")});
    }

    public long getTotalGestionLlamadas() {
        return GestionLlamada.count(GestionLlamada.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("lineaNegocio") + " in " + LINEA_NEGOCIO_QUERY,
                new String[]{Preferences.getInstance().getString("idUsuario", "")});
    }

    public long getTotalEstadoRuta() {
        return EstadoRuta.count(EstadoRuta.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("eliminado") + " = ? and " +
                        NamingHelper.toSQLNameDefault("lineaNegocio") + " in " + LINEA_NEGOCIO_QUERY,
                new String[]{Preferences.getInstance().getString("idUsuario", ""),
                        Data.Delete.NO + ""});
    }

    public long getTotatImagenesBySync(int sync) {
        return Imagen.count(Imagen.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("dataSync") + " = ? and " +
                        NamingHelper.toSQLNameDefault("clasificacion") + " = ? and " +
                        NamingHelper.toSQLNameDefault("lineaNegocio") + " in " + LINEA_NEGOCIO_QUERY,
                new String[]{Preferences.getInstance().getString("idUsuario", ""),
                        sync + "", Imagen.Tipo.GESTION_GUIA + ""});
    }

    public long getTotalGestionesBySync(int sync) {
        return GuiaGestionada.count(GuiaGestionada.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("dataSync") + " = ? and " +
                        NamingHelper.toSQLNameDefault("eliminado") + " = ? and " +
                        NamingHelper.toSQLNameDefault("lineaNegocio") + " in " + LINEA_NEGOCIO_QUERY,
                new String[]{Preferences.getInstance().getString("idUsuario", ""),
                        sync + "", Data.Delete.NO + ""});
    }

    public long getTotalTramasBySync(int sync) {
        return TrackLocation.count(TrackLocation.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("dataSync") + " = ? and " +
                        NamingHelper.toSQLNameDefault("lineaNegocio") + " in " + LINEA_NEGOCIO_QUERY,
                new String[]{Preferences.getInstance().getString("idUsuario", ""),
                        sync + ""});
    }

    public long getTotalGestionLlamadasBySync(int sync) {
        return GestionLlamada.count(GestionLlamada.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("dataSync") + " = ? and " +
                        NamingHelper.toSQLNameDefault("lineaNegocio") + " in " + LINEA_NEGOCIO_QUERY,
                new String[]{Preferences.getInstance().getString("idUsuario", ""),
                        sync + ""});
    }

    public long getTotalEstadoRutaBySync(int sync) {
        return EstadoRuta.count(EstadoRuta.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("dataSync") + " = ? and " +
                        NamingHelper.toSQLNameDefault("lineaNegocio") + " in " + LINEA_NEGOCIO_QUERY,
                new String[]{Preferences.getInstance().getString("idUsuario", ""),
                        sync + ""});
    }

    public long getTotalGuiasPendientes(String idRuta) {
        return Ruta.count(Ruta.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("eliminado") + " = ? and " +
                        NamingHelper.toSQLNameDefault("estadoDescarga") + " = ? and " +
                        NamingHelper.toSQLNameDefault("idRuta") + " = ? and " +
                        NamingHelper.toSQLNameDefault("lineaNegocio") + " in " + LINEA_NEGOCIO_QUERY,
                new String[]{Preferences.getInstance().getString("idUsuario", ""),
                        Data.Delete.NO + "", Ruta.EstadoDescarga.PENDIENTE + "", idRuta});
    }

    public long getTotatImagenesBySync(int sync, String idRuta) {
        return Imagen.count(Imagen.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("dataSync") + " = ? and " +
                        NamingHelper.toSQLNameDefault("clasificacion") + " = ? and " +
                        NamingHelper.toSQLNameDefault("idRuta") + " = ? and " +
                        NamingHelper.toSQLNameDefault("lineaNegocio") + " in " + LINEA_NEGOCIO_QUERY,
                new String[]{Preferences.getInstance().getString("idUsuario", ""),
                        sync + "", Imagen.Tipo.GESTION_GUIA + "", idRuta});
    }

    public long getTotalGestionesBySync(int sync, String idRuta) {
        return GuiaGestionada.count(GuiaGestionada.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("dataSync") + " = ? and " +
                        NamingHelper.toSQLNameDefault("eliminado") + " = ? and " +
                        NamingHelper.toSQLNameDefault("idRuta") + " = ? and " +
                        NamingHelper.toSQLNameDefault("lineaNegocio") + " in " + LINEA_NEGOCIO_QUERY,
                new String[]{Preferences.getInstance().getString("idUsuario", ""),
                        sync + "", Data.Delete.NO + "", idRuta});
    }

    public long getTotalTramasBySync(int sync, String idRuta) {
        return TrackLocation.count(TrackLocation.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("dataSync") + " = ? and " +
                        NamingHelper.toSQLNameDefault("idRuta") + " = ? and " +
                        NamingHelper.toSQLNameDefault("lineaNegocio") + " in " + LINEA_NEGOCIO_QUERY,
                new String[]{Preferences.getInstance().getString("idUsuario", ""),
                        sync + "", idRuta});
    }

    public long getTotalEstadoRutaBySync(int sync, String idRuta) {
        return EstadoRuta.count(EstadoRuta.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("dataSync") + " = ? and " +
                        NamingHelper.toSQLNameDefault("idRuta") + " = ? and " +
                        NamingHelper.toSQLNameDefault("lineaNegocio") + " in " + LINEA_NEGOCIO_QUERY,
                new String[]{Preferences.getInstance().getString("idUsuario", ""),
                        sync + "", idRuta});
    }

//    public long selectIdRutas() {
//        return Ruta.find(Ruta.class,
//                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
//                        NamingHelper.toSQLNameDefault("eliminado") + " = ? and " +
//                        NamingHelper.toSQLNameDefault("lineaNegocio") + " in (2, 3, 4)",
//                new String[]{Preferences.getInstance().getString("idUsuario", ""),
//                        Data.Delete.NO + ""},
//                NamingHelper.toSQLNameDefault("idRuta"), NamingHelper.toSQLNameDefault("secuencia"), "");
//    }

}