package com.urbanoexpress.iridio3.pe.model.interactor;

import android.util.Log;

import com.orm.util.NamingHelper;
import com.urbanoexpress.iridio3.pe.model.entity.Data;
import com.urbanoexpress.iridio3.pe.model.entity.Ruta;
import com.urbanoexpress.iridio3.pe.util.Preferences;

import java.util.List;

public class ConsideracionesImportantesRutaInteractor {

    public static List<Ruta> selectRutasImportantes() {
        return Ruta.find(Ruta.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("eliminado") + " = ? and " +
                        NamingHelper.toSQLNameDefault("lineaNegocio") + " in (2, 3, 4) and " +
                        "((" + NamingHelper.toSQLNameDefault("tipo") + " = ? and " +
                        NamingHelper.toSQLNameDefault("tipoEnvio") + " = ?) or " +
                        "(" + NamingHelper.toSQLNameDefault("tipo") + " = ? and " +
                        NamingHelper.toSQLNameDefault("guiaRequerimiento") + " = ?))",
                new String[]{Preferences.getInstance().getString("idUsuario", ""),
                        Data.Delete.NO + "",
                        "R", "E", // Recolecciones express
                        "E", "1"}, // Guias con requerimiento
                "", NamingHelper.toSQLNameDefault("mostrarAlerta") + " DESC", "");
    }

    public static long getTotalRecoleccionesExpress() {
        return Ruta.count(Ruta.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("eliminado") + " = ? and " +
                        NamingHelper.toSQLNameDefault("lineaNegocio") + " in (2, 3, 4) and " +
                        NamingHelper.toSQLNameDefault("tipo") + " = ? and " +
                        NamingHelper.toSQLNameDefault("tipoEnvio") + " = ?",
                new String[]{Preferences.getInstance().getString("idUsuario", ""),
                        Data.Delete.NO + "",
                        "R", "E"});
    }

    public static long getTotalGuiasRequerimiento() {
        return Ruta.count(Ruta.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("eliminado") + " = ? and " +
                        NamingHelper.toSQLNameDefault("lineaNegocio") + " in (2, 3, 4) and " +
                        NamingHelper.toSQLNameDefault("tipo") + " = ? and " +
                        NamingHelper.toSQLNameDefault("guiaRequerimiento") + " = ?",
                new String[]{Preferences.getInstance().getString("idUsuario", ""),
                        Data.Delete.NO + "",
                        "E", "1"});
    }

    public static boolean isMostrarAlerta() {
        long total = Ruta.count(Ruta.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("eliminado") + " = ? and " +
                        NamingHelper.toSQLNameDefault("mostrarAlerta") + " = ? and " +
                        NamingHelper.toSQLNameDefault("lineaNegocio") + " in (2, 3, 4)",
                new String[]{Preferences.getInstance().getString("idUsuario", ""),
                        Data.Delete.NO + "", "1"});
        Log.d("MAINACTIVITY", "SHOW ALERTA: " + total);
        return total > 0;
    }

    public static long getTotalRutasImportantes() {
        return getTotalRecoleccionesExpress() + getTotalGuiasRequerimiento();
    }
}
