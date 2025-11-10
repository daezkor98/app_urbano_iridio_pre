package com.urbanoexpress.iridio3.pre.util;

import android.content.Context;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.urbanoexpress.iridio3.pre.model.entity.EstadoRuta;
import com.urbanoexpress.iridio3.pre.model.entity.PlanDeViaje;
import com.urbanoexpress.iridio3.pre.model.entity.Ruta;
import com.urbanoexpress.iridio3.pre.model.entity.TrackLocation;
import com.urbanoexpress.iridio3.pre.model.interactor.DataSyncInteractor;


/**
 * Created by mick on 03/08/16.
 */
public class RegisterTrackLocationHelper {

    private static final String TAG = RegisterTrackLocationHelper.class.getSimpleName();

    private Context context;

    private List<EstadoRuta> estadoRuta = Collections.emptyList();

    private List<PlanDeViaje> planDeViaje = Collections.emptyList();

    private DataSyncInteractor interactor;

    public RegisterTrackLocationHelper(Context context) {
        this.context = context;
        this.interactor = new DataSyncInteractor(context);
        loadDatos();
    }

    public void registerLocation() {
//        loadDatos();
        if (validateRutaDelDiaIniciado()) {
            List<Ruta> idRutas = interactor.selectIdRutas();
            Log.d(TAG, "TOTAL ID RUTAS: " + idRutas.size());
            for (int i = 0; i < idRutas.size(); i++) {
                saveLocation(idRutas.get(i).getIdRuta(),
                        idRutas.get(i).getLineaNegocio(),
                        TrackLocation.Tipo.RUTA_DEL_DIA);
            }
        }

        if (validatePlanDeViajeIniciado()) {
            saveLocation(planDeViaje.get(0).getIdPlanViaje(),
                    "3",
                    TrackLocation.Tipo.PLAN_DE_VIAJE);
        }
    }

    private void loadDatos() {
        Log.d("RegisterTrackLocationHelper", "LOAD DATOS ESTADOS");
        estadoRuta = interactor.selectAllEstadoRuta();
        planDeViaje = interactor.selectPlanViaje();
    }

    private boolean validateEstadoDatos() {
        if (validateRutaDelDiaIniciado() ||
                validatePlanDeViajeIniciado()) {
            return true;
        }
        return false;
    }

    public boolean validateRutaDelDiaIniciado() {
        boolean validate = false;
        for (EstadoRuta estado : estadoRuta) {
            if (estado.getEstado() == EstadoRuta.Estado.INICIADO) {
                Log.d(TAG, "ruta del dia: true");
                validate = true;
            }
            if (estado.getEstado() == EstadoRuta.Estado.FINALIZADO) {
                Log.d(TAG, "ruta del dia: false");
                return false;
            }
        }
        Log.d(TAG, "ruta del dia: " + validate);
        Log.d(TAG, "total estados: " + estadoRuta.size());
        return validate;
    }

    public boolean validatePlanDeViajeIniciado() {
        if (planDeViaje.size() > 0) {
            if (planDeViaje.get(0).getEstadoRecorrido() == PlanDeViaje.EstadoRuta.INICIO_RUTA) {
                Log.d(TAG, "plan de viaje: true");
                return true;
            }
        }
        Log.d(TAG, "plan de viaje: false");
        return false;
    }

    private void saveLocation(String idRuta, String lineaNegocio, int tipo) {
        Date date = new Date(LocationUtils.getCurrentLocation().getTime());

        if (!CommonUtils.isToday(date.getTime())) {
            date = new Date();
        }

        String fecha = new SimpleDateFormat("dd/MM/yyyy").format(date);
        String hora = new SimpleDateFormat("HH:mm:ss").format(date);

        TrackLocation trackLocation = new TrackLocation(
                Session.getUser().getIdUsuario(),
                idRuta,
                lineaNegocio,
                fecha,
                hora,
                LocationUtils.getLatitude()+ "",
                LocationUtils.getLongitude()+ "",
                LocationUtils.getCurrentLocation().getAccuracy()+ "",
                InfoDevice.getBattery(context),
                tipo
        );
        trackLocation.save();
    }

}
