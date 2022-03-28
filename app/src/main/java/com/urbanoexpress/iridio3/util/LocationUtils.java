package com.urbanoexpress.iridio3.util;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import androidx.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

/**
 * Created by mick on 21/07/16.
 */
public class LocationUtils {

    private static LocationManager locationManager;
    private static LocationListener mLocationListener;
    private static Location currentLocation;

    private static double latitude = 0, longitude = 0;

    public static final int REQUEST_CHECK_GPS_SETTINGS = 360;

    public static void getCurrentLocation(Context context, LocationListener locationListener) {
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        mLocationListener = locationListener;

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setBearingAccuracy(Criteria.ACCURACY_FINE);
        criteria.setSpeedAccuracy(Criteria.ACCURACY_HIGH);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAltitudeRequired(false);
        criteria.setCostAllowed(false);
        String fineProvider = locationManager.getBestProvider(criteria, true);

        locationManager.requestLocationUpdates(fineProvider, 0, 1.5f, mLocationListener);
    }

    public static Location getCurrentLocation() {
        return currentLocation;
    }

    public static LocationManager getLocationManager() {
        return locationManager;
    }

    public static LocationListener getLocationListener() {
        return mLocationListener;
    }

    public static void setCurrentLocation(Location cLocation) {
        currentLocation = cLocation;
        if (cLocation != null) {
            latitude = currentLocation.getLatitude();
            longitude = currentLocation.getLongitude();
        } else {
            latitude = 0;
            longitude = 0;
        }
    }

    public static double getLatitude() {
        return latitude;
    }

    public static double getLongitude() {
        return longitude;
    }

    public static void validateSwitchedOnGPS(Activity activity, final OnSwitchedOnGPSListener callback) {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        SettingsClient client = LocationServices.getSettingsClient(activity);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(activity, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                callback.onSuccess();
                Log.d("TEST", "SUCCESS LISTENER");
            }
        });

        task.addOnFailureListener(activity, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("TEST", "FAILURE LISTENER");
                int statusCode = ((ApiException) e).getStatusCode();
                switch (statusCode) {
                    case CommonStatusCodes.RESOLUTION_REQUIRED:
                        callback.onFailure(e);
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        callback.onFailure(e);
                        break;
                }
            }
        });

        task.addOnCompleteListener(activity, new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                Log.d("TEST", "COMPLETE LISTENER");
            }
        });
    }

    public interface OnSwitchedOnGPSListener {
        void onSuccess();
        void onFailure(Exception ex);
    }
}