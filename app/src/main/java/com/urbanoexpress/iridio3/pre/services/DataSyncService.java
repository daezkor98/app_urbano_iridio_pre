package com.urbanoexpress.iridio3.pre.services;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import androidx.annotation.Nullable;
import android.util.Log;

import java.util.Timer;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.urbanoexpress.iridio3.pre.data.sync.DataSyncTask;
import com.urbanoexpress.iridio3.pre.util.LocationUtils;
import com.urbanoexpress.iridio3.pre.util.NotificationUtils;
import com.urbanoexpress.iridio3.pre.util.PermissionUtils;
import com.urbanoexpress.iridio3.pre.util.RegisterTrackLocationHelper;
import com.urbanoexpress.iridio3.pre.util.Session;

/**
 * Created by mick on 02/08/16.
 */
public class DataSyncService extends Service {

    private static final String TAG = DataSyncService.class.getSimpleName();

    private PowerManager.WakeLock wakeLock;
    private Timer timerData;
    private Timer timerNewsData;

    private DataSyncTask syncData;
    private DataSyncTask syncNewsData;

    private RegisterTrackLocationHelper registerTrackLocationHelper;

    private LocationCallback locationCallback;

    private static final int ID_SERVICE = 150918;

    @Override
    public void onCreate() {
        Log.i(TAG, "SERVICE CREATE");
        super.onCreate();

        PowerManager pm = (PowerManager) getSystemService(this.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "URBANO_DATA_SYNC");
        wakeLock.acquire();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(ID_SERVICE, NotificationUtils.getDataSyncChannelNotification(getBaseContext()));
        }

        try {
            initTask();
        } catch (NullPointerException ex) {
            Log.d(TAG, "ERROR INIT TASK");
            ex.printStackTrace();
        }
    }

    @SuppressLint("MissingPermission")
    private void initTask() {
        if (Session.getUser() != null) {
            timerData = new Timer();
            timerNewsData = new Timer();

            syncData = new DataSyncTask(getBaseContext(), DataSyncTask.Sync.DATA);
            timerData.schedule(syncData, 0,
                    Long.parseLong(Session.getUser().getTiempoRequesDatos()) * 1000);

            registerTrackLocationHelper = new RegisterTrackLocationHelper(getBaseContext());

            if (registerTrackLocationHelper.validateRutaDelDiaIniciado() ||
                    registerTrackLocationHelper.validatePlanDeViajeIniciado()) {
                if (PermissionUtils.checkPermissions(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                    LocationRequest mLocationRequest = new LocationRequest();
                    mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    mLocationRequest.setInterval(Long.parseLong(Session.getUser().getTiempoRequestGPS()) * 1000);
                    mLocationRequest.setFastestInterval(Long.parseLong(Session.getUser().getTiempoRequestGPS()) * 1000);

                    locationCallback = new LocationCallback() {
                        @Override
                        public void onLocationResult(LocationResult locationResult) {
                            if (locationResult != null) {
                                Log.i("RegisterLocationTask", "LATITUDE: " + locationResult.getLastLocation().getLatitude());
                                Log.i("RegisterLocationTask", "LONGITUDE: " + locationResult.getLastLocation().getLongitude());
                                Log.i("RegisterLocationTask", "ACCURACY: " + locationResult.getLastLocation().getAccuracy());

                                if (locationResult.getLastLocation().getAccuracy() <= 35) {
                                    LocationUtils.setCurrentLocation(locationResult.getLastLocation());
                                    registerTrackLocationHelper.registerLocation();
                                }
                            }
                        }
                    };

                    LocationServices.getFusedLocationProviderClient(getBaseContext())
                            .requestLocationUpdates(mLocationRequest, locationCallback, Looper.myLooper());
                }
            }

            syncNewsData = new DataSyncTask(getBaseContext(), DataSyncTask.Sync.NEWS_DATA);
            timerNewsData.schedule(syncNewsData, 0, 3600000); // Cada hora
        } else {
            Log.d(TAG, "NO HAY USUARIO ACTIVO");
            Log.d(TAG, "SERVICE AUTO DELETE");
            stopSelf();
        }
    }

    @Override
    public void onDestroy() {
        try {
            timerData.cancel();
            timerNewsData.cancel();
            LocationServices.getFusedLocationProviderClient(getBaseContext())
                    .removeLocationUpdates(locationCallback);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                timerData.cancel();
                timerNewsData.cancel();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        try {
            if (wakeLock != null && wakeLock.isHeld()) {
                wakeLock.release();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}