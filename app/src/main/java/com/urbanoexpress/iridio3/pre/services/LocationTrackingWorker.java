package com.urbanoexpress.iridio3.pre.services;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.urbanoexpress.iridio3.pre.util.LocationUtils;
import com.urbanoexpress.iridio3.pre.util.PermissionUtils;
import com.urbanoexpress.iridio3.pre.util.RegisterTrackLocationHelper;
import com.urbanoexpress.iridio3.pre.util.Session;

import java.util.concurrent.TimeUnit;

/**
 * Worker específico para tracking de ubicación continua
 * Se auto-reprograma para mantener el tracking activo
 */
public class LocationTrackingWorker extends Worker {
    private static final String TAG = "LocationTrackingWorker";

    public LocationTrackingWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.i(TAG, "LocationTrackingWorker ejecutándose");

        try {
            // Verificar si todavía se necesita tracking
            RegisterTrackLocationHelper helper = new RegisterTrackLocationHelper(getApplicationContext());

            if (!helper.validateRutaDelDiaIniciado() &&
                    !helper.validatePlanDeViajeIniciado()) {
                Log.i(TAG, "No hay tracking activo, finalizando");
                return Result.success();
            }

            // Obtener ubicación actual
            getCurrentLocation();

            // Auto-reprogramarse
            rescheduleSelf();

            return Result.success();

        } catch (Exception e) {
            Log.e(TAG, "Error en LocationTrackingWorker", e);
            return Result.retry();
        }
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        if (!PermissionUtils.checkPermissions(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            Log.w(TAG, "Sin permisos de ubicación");
            return;
        }

        FusedLocationProviderClient fusedLocationClient =
                LocationServices.getFusedLocationProviderClient(getApplicationContext());

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null && location.getAccuracy() <= 35) {
                        Log.i(TAG, "Ubicación registrada: " +
                                location.getLatitude() + ", " + location.getLongitude());

                        LocationUtils.setCurrentLocation(location);

                        RegisterTrackLocationHelper helper =
                                new RegisterTrackLocationHelper(getApplicationContext());
                        helper.registerLocation();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error obteniendo ubicación", e);
                });
    }

    private void rescheduleSelf() {
        long interval = getLocationInterval();

        OneTimeWorkRequest nextWorkRequest = new OneTimeWorkRequest.Builder(
                LocationTrackingWorker.class)
                .setInitialDelay(interval, TimeUnit.SECONDS)
                .addTag("location_tracking")
                .build();

        WorkManager.getInstance(getApplicationContext())
                .enqueueUniqueWork(
                        "continuous_location_tracking",
                        ExistingWorkPolicy.REPLACE,
                        nextWorkRequest
                );

        Log.i(TAG, "Siguiente tracking programado en " + interval + " segundos");
    }

    private long getLocationInterval() {
        if (Session.getUser() != null) {
            try {
                return Long.parseLong(Session.getUser().getTiempoRequestGPS());
            } catch (NumberFormatException e) {
                return 30; // 30 segundos por defecto
            }
        }
        return 30;
    }
}