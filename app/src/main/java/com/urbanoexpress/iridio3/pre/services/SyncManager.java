package com.urbanoexpress.iridio3.pre.services;

import android.content.Context;
import android.util.Log;

import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.urbanoexpress.iridio3.pre.util.Session;

import java.util.concurrent.TimeUnit;

/**
 * Manager SIMPLE para controlar todas las sincronizaciones
 * ÚNICO para TODOS los dispositivos
 */
public class SyncManager {
    private static final String TAG = "SyncManager";

    // Nombres únicos para los trabajos
    private static final String PERIODIC_SYNC_WORK = "periodic_data_sync";
    private static final String LOCATION_TRACKING_WORK = "location_tracking_work";

    /**
     * Iniciar/Reiniciar todas las sincronizaciones
     */
    public static void startAllSyncs(Context context) {
        Log.i(TAG, "Iniciando todas las sincronizaciones");

        if (Session.getUser() == null) {
            Log.w(TAG, "Usuario no logueado, omitiendo sync");
            return;
        }

        // 1. Sincronización periódica principal
        startPeriodicDataSync(context);

        // 2. Sincronización inmediata inicial
        startImmediateSync(context);

        Log.i(TAG, "Todas las sincronizaciones iniciadas");
    }

    /**
     * Iniciar sincronización periódica de datos
     */
    private static void startPeriodicDataSync(Context context) {
        try {
            Constraints constraints = new Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build();

            // Calcular intervalo desde configuración del usuario
            long intervaloMinutos = calculateSyncInterval();

            PeriodicWorkRequest periodicWork = new PeriodicWorkRequest.Builder(
                    DataSyncWorker.class,
                    intervaloMinutos, TimeUnit.MINUTES,
                    5, TimeUnit.MINUTES) // Flexibilidad de 5 minutos
                    .setConstraints(constraints)
                    .setInitialDelay(1, TimeUnit.MINUTES) // Iniciar después de 1 minuto
                    .build();

            WorkManager.getInstance(context.getApplicationContext())
                    .enqueueUniquePeriodicWork(
                            PERIODIC_SYNC_WORK,
                            ExistingPeriodicWorkPolicy.KEEP, // Mantener si ya existe
                            periodicWork
                    );

            Log.i(TAG, "Sincronización periódica programada cada " +
                    intervaloMinutos + " minutos");

        } catch (Exception e) {
            Log.e(TAG, "Error programando sincronización periódica", e);
        }
    }

    /**
     * Iniciar una sincronización inmediata
     */
    public static void startImmediateSync(Context context) {
        try {
            // Forzar una ejecución inmediata del worker
            WorkManager.getInstance(context.getApplicationContext())
                    .enqueue(new OneTimeWorkRequest.Builder(DataSyncWorker.class)
                            .setInitialDelay(0, TimeUnit.SECONDS)
                            .build());

            Log.i(TAG, "Sincronización inmediata iniciada");

        } catch (Exception e) {
            Log.e(TAG, "Error iniciando sync inmediata", e);
        }
    }

    /**
     * Detener todas las sincronizaciones
     */
    public static void stopAllSyncs(Context context) {
        try {
            WorkManager.getInstance(context.getApplicationContext())
                    .cancelAllWork();

            Log.i(TAG, "Todas las sincronizaciones detenidas");

        } catch (Exception e) {
            Log.e(TAG, "Error deteniendo syncs", e);
        }
    }

    /**
     * Detener solo el tracking de ubicación
     */
    public static void stopLocationTracking(Context context) {
        try {
            WorkManager.getInstance(context.getApplicationContext())
                    .cancelAllWorkByTag("location_tracking");

            Log.i(TAG, "Tracking de ubicación detenido");

        } catch (Exception e) {
            Log.e(TAG, "Error deteniendo tracking", e);
        }
    }

    /**
     * Calcular intervalo de sincronización desde configuración del usuario
     */
    private static long calculateSyncInterval() {
        long intervaloMinutos = 15; // Valor por defecto

        if (Session.getUser() != null) {
            try {
                long intervaloUsuario = Long.parseLong(Session.getUser().getTiempoRequesDatos());
                intervaloMinutos = Math.max(15, intervaloUsuario / 60); // Mínimo 15 min

                Log.d(TAG, "Intervalo usuario: " + intervaloUsuario +
                        " segundos -> " + intervaloMinutos + " minutos");

            } catch (NumberFormatException e) {
                Log.w(TAG, "Error parseando tiempo de sync, usando 15 min");
            }
        }

        return intervaloMinutos;
    }

    /**
     * Verificar si hay trabajos activos
     */
    public static boolean areSyncsActive(Context context) {
        try {
            // Esta es una verificación simple
            // En producción podrías consultar WorkManager.getWorkInfosByTag()
            return Session.getUser() != null;
        } catch (Exception e) {
            Log.e(TAG, "Error verificando syncs activos", e);
            return false;
        }
    }
}
