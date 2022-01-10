package com.urbanoexpress.iridio.services;

import android.app.Service;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import com.urbanoexpress.iridio.util.LocationUtils;
import com.urbanoexpress.iridio.util.RegisterTrackLocationHelper;
import com.urbanoexpress.iridio.util.Session;

/**
 * Created by mick on 18/05/16.
 */
public class LocationService extends Service implements LocationListener{

    private static final String TAG = LocationService.class.getSimpleName();

    private static final String BROADCAST_ACTION = "LocationService";
    private Intent intent;
    private LocationManager locationManager = null;
    private PowerManager.WakeLock wakeLock = null;

    private RegisterTrackLocationHelper registerTrackLocationHelper;

    @Override
    public void onCreate() {
        Log.i(TAG, "SERVICE CREATE");
        super.onCreate();
        intent = new Intent(BROADCAST_ACTION);

        try {
            if (locationManager == null)
                locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            criteria.setBearingAccuracy(Criteria.ACCURACY_FINE);
            criteria.setSpeedAccuracy(Criteria.ACCURACY_HIGH);
            criteria.setPowerRequirement(Criteria.POWER_LOW);
            criteria.setAltitudeRequired(false);
            criteria.setCostAllowed(false);
            String provider = locationManager.getBestProvider(criteria, true);
            locationManager.requestLocationUpdates(provider,
                    Long.parseLong(Session.getUser().getTiempoRequestGPS()) * 1000, 5, this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            registerTrackLocation();
        } catch (NullPointerException ex) {
            Log.d(TAG, "ERROR INIT TASK");
            ex.printStackTrace();
        }

        PowerManager pm = (PowerManager) getSystemService(this.POWER_SERVICE);
        this.wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "URBANO");
        wakeLock.acquire();
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "SERVICE DESTROY");
        wakeLock.release();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(TAG, "onLocationChanged");
        if (location != null) {
            if (location.getAccuracy() <= 200) {

                Log.i(TAG, "LATITUDE: " + location.getLatitude());
                Log.i(TAG, "LONGITUDE: " + location.getLongitude());
                Log.i(TAG, "ACCURACY: " + location.getAccuracy());

                LocationUtils.setCurrentLocation(location);

//                Toast.makeText(getBaseContext(), "Nueva ubicación registrada.", Toast.LENGTH_SHORT).show();

                if (registerTrackLocationHelper != null)
                    registerTrackLocationHelper.registerLocation();
            }
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private void registerTrackLocation() {
        if (Session.getUser() != null) {
            registerTrackLocationHelper = new RegisterTrackLocationHelper(getBaseContext());
        } else {
            Log.d(TAG, "NO HAY USUARIO ACTIVO");
            Log.d(TAG, "SERVICE AUTO DELETE");
            stopSelf();
        }
    }

}
