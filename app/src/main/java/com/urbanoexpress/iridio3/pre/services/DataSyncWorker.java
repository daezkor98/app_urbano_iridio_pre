package com.urbanoexpress.iridio3.pre.services;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.urbanoexpress.iridio3.pre.data.sync.DataSyncTask;
import com.urbanoexpress.iridio3.pre.util.LocationUtils;
import com.urbanoexpress.iridio3.pre.util.PermissionUtils;
import com.urbanoexpress.iridio3.pre.util.RegisterTrackLocationHelper;
import com.urbanoexpress.iridio3.pre.util.Session;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Worker ÚNICO para todas las sincronizaciones
 * Compatible con TODOS los dispositivos
 */
public class DataSyncWorker extends Worker {
    private static final String TAG = "DataSyncWorker";

    private ExecutorService executorService;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private RegisterTrackLocationHelper registerTrackLocationHelper;

    public DataSyncWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.executorService = Executors.newSingleThreadExecutor();
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.i(TAG, "DataSyncWorker iniciado");

        try {
            if (Session.getUser() == null) {
                Log.d(TAG, "No hay usuario activo");
                return Result.success();
            }

            // 1. Sincronización de datos principal
            syncMainData();

            // 2. Verificar y manejar tracking de ubicación
            handleLocationTracking();

            // 3. Sincronizar noticias si corresponde
            syncNewsIfNeeded();

            Log.i(TAG, "DataSyncWorker completado exitosamente");
            return Result.success();

        } catch (Exception e) {
            Log.e(TAG, "Error en DataSyncWorker", e);
            return Result.retry(); // Reintentar automáticamente
        } finally {
            cleanup();
        }
    }

    private void syncMainData() {
        try {
            Log.d(TAG, "Sincronizando datos principales");
            DataSyncTask syncTask = new DataSyncTask(getApplicationContext(), DataSyncTask.Sync.DATA);

            // Si tu DataSyncTask es TimerTask, ejecútalo directamente
            syncTask.run();

        } catch (Exception e) {
            Log.e(TAG, "Error en sincronización principal", e);
        }
    }

    private void syncNewsIfNeeded() {
        try {
            Log.d(TAG, "Verificando sincronización de noticias");
            DataSyncTask newsTask = new DataSyncTask(getApplicationContext(), DataSyncTask.Sync.NEWS_DATA);
            newsTask.run();
        } catch (Exception e) {
            Log.e(TAG, "Error en sincronización de noticias", e);
        }
    }

    @SuppressLint("MissingPermission")
    private void handleLocationTracking() {
        try {
            registerTrackLocationHelper = new RegisterTrackLocationHelper(getApplicationContext());

            if (registerTrackLocationHelper.validateRutaDelDiaIniciado() ||
                    registerTrackLocationHelper.validatePlanDeViajeIniciado()) {

                Log.i(TAG, "Tracking de ubicación requerido");

                if (!PermissionUtils.checkPermissions(getApplicationContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION)) {
                    Log.w(TAG, "Permisos de ubicación no disponibles");
                    return;
                }

                // Obtener ubicación actual (una sola vez)
                getCurrentLocation();

                // Si necesitas tracking continuo, programa otro worker
                scheduleContinuousLocationTracking();

            } else {
                Log.i(TAG, "No hay ruta activa, omitiendo tracking");
            }

        } catch (Exception e) {
            Log.e(TAG, "Error en manejo de ubicación", e);
        }
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        Log.i(TAG, "Ubicación obtenida: " + location.getLatitude() + ", " + location.getLongitude());

                        if (location.getAccuracy() <= 35) {
                            LocationUtils.setCurrentLocation(location);
                            registerTrackLocationHelper.registerLocation();
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error obteniendo ubicación", e);
                });
    }

    private void scheduleContinuousLocationTracking() {
        // Programa un worker específico para tracking continuo
        // Se ejecuta con mayor frecuencia
        OneTimeWorkRequest locationWorkRequest = new OneTimeWorkRequest.Builder(
                LocationTrackingWorker.class)
                .setInitialDelay(getLocationInterval(), TimeUnit.SECONDS)
                .addTag("location_tracking")
                .build();

        WorkManager.getInstance(getApplicationContext())
                .enqueueUniqueWork(
                        "continuous_location_tracking",
                        ExistingWorkPolicy.REPLACE,
                        locationWorkRequest
                );

        Log.i(TAG, "Worker de tracking de ubicación programado");
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

    private void cleanup() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdownNow();
        }

        if (fusedLocationClient != null && locationCallback != null) {
            try {
                fusedLocationClient.removeLocationUpdates(locationCallback);
            } catch (Exception e) {
                Log.e(TAG, "Error removiendo location updates", e);
            }
        }
    }

    @Override
    public void onStopped() {
        Log.i(TAG, "DataSyncWorker detenido");
        cleanup();
        super.onStopped();
    }
}