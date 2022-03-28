package com.urbanoexpress.iridio3.util;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.urbanoexpress.iridio3.R;

/**
 * Created by mick on 07/06/16.
 */
public class MyLocation {

    private static final String TAG = MyLocation.class.getSimpleName();

    private static LocationManager locationManager;
    private static LocationListener locationListener;

    public static boolean arrivedToLocation(Context context, double arriveLatitude, double arriveLongitude) {
        String locationProvider = LocationManager.GPS_PROVIDER;
        locationManager  = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        startLocationListener();

        // Or use LocationManager.GPS_PROVIDER
        Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);

        try {
            double distancia = distanceFrom(LocationUtils.getLatitude(), LocationUtils.getLongitude(),
                    arriveLatitude, arriveLongitude);

            Log.d(TAG, "Distance: " + distancia + "km");
            return intersectedLocations(distancia);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
            Toast.makeText(context, R.string.message_not_found_location, Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public static boolean intersectionLocations(double lat1, double lng1, double lat2, double lng2, double kmInterseccion) {
        try {
            double distancia = distanceFrom(lat1, lng1, lat2, lng2);

            return distancia <= kmInterseccion;
        } catch (NullPointerException ex) {
            ex.printStackTrace();
            //Toast.makeText(context, R.string.message_not_found_location, Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private static boolean intersectedLocations(double distancia) {
        double distanciaMinima = 1.0; // 1km
        if (distancia <= distanciaMinima) {
            return true;
        }
        return false;
    }

    public static double distanceFrom(double lat1, double lng1, double lat2, double lng2) {
        Log.d(TAG, "Current Latitude: " + lat1);
        Log.d(TAG, "Current Longitude: " + lng1);
        Log.d(TAG, "Arrive Latitude: " + lat2);
        Log.d(TAG, "Arrive Longitude: " + lng2);

//        double earthRadius = 3958.75; // miles (or 6371.0 kilometers)
        double earthRadius = 6371.0; // kilometers
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);
        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double dist = earthRadius * c;

        Log.d(TAG, "Distance: " + dist + "km");

        return dist;
    }

    public static double calculateTimeBetweenTwoLocations(double latitudeFrom, double longitudeFrom,
                                                       double latitudeTo, double longitudeTo,
                                                       int velocityKM) {
        // Distancia en metros
        double distance = distanceFrom(latitudeFrom, longitudeFrom, latitudeTo, longitudeTo) * 1000;

//        // agregar un 20% a la distancia
//        distance += distance * 0.5;

        // Velocidad en metros por segundo
        double velocity = (velocityKM * 1000) / 3600;

        return (distance / velocity) / 60;
    }

    private static void startLocationListener() {
        // Define a listener that responds to location updates
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                locationManager.removeUpdates(locationListener);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.d(TAG, "Location Listener Extras: " + extras.toString());
            }

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

        // Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30000, 30, locationListener);
    }

    public static boolean isValidLocations(String latitude, String longitude) {
        if (latitude.trim().isEmpty() && longitude.trim().isEmpty()) {
            return false;
        }

        if (Double.parseDouble(latitude) == 0 && Double.parseDouble(longitude) == 0) {
            return false;
        }

        return true;
    }

}
