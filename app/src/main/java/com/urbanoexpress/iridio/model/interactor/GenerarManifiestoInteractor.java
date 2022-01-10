package com.urbanoexpress.iridio.model.interactor;

import android.content.Context;

import com.orm.util.NamingHelper;

import java.util.List;

import com.urbanoexpress.iridio.model.entity.Data;
import com.urbanoexpress.iridio.model.entity.Ruta;
import com.urbanoexpress.iridio.util.Preferences;

/**
 * Created by mick on 17/08/16.
 */

public class GenerarManifiestoInteractor {

    private static final String TAG = GenerarManifiestoInteractor.class.getSimpleName();

    private Context context;

    public GenerarManifiestoInteractor(Context context) {
        this.context = context;
    }

    public List<Ruta> selectAllRutaPendiente() {
        List<Ruta> ruta = Ruta.find(Ruta.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("eliminado") + " = ? and " +
                        NamingHelper.toSQLNameDefault("estadoDescarga") + " = ?",
                new String[]{
                        Preferences.getInstance().getString("idUsuario", ""),
                        Data.Delete.NO + "",
                        Ruta.EstadoDescarga.PENDIENTE + ""},
                "", NamingHelper.toSQLNameDefault("secuencia"), "");
        return ruta;
    }

}
