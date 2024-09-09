package com.urbanoexpress.iridio3.pe.util;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.List;

/**
 * Created by mick on 21/12/16.
 */

public class MovementManager implements SensorEventListener {

    /**
     * Constants for sensors
     */
    private static final float SHAKE_THRESHOLD = 1.5f;
    private static final int SHAKE_WAIT_TIME_MS = 250;
    private static final float ROTATION_THRESHOLD = 2.0f;
    private static final int ROTATION_WAIT_TIME_MS = 100;

    private long mShakeTime = 0;
    private long mRotationTime = 0;

    private Activity activity;

    private OnMovementListener listener;

    public MovementManager(Context context) {
        this.activity = (Activity) context;

        try {
            this.listener = (OnMovementListener) context;
        } catch (ClassCastException ex) { }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        synchronized (this) {
            long now = System.currentTimeMillis();

            if ((now - mShakeTime) > SHAKE_WAIT_TIME_MS) {
                mShakeTime = now;

                float gX = sensorEvent.values[0] / SensorManager.GRAVITY_EARTH;
                float gY = sensorEvent.values[1] / SensorManager.GRAVITY_EARTH;
                float gZ = sensorEvent.values[2] / SensorManager.GRAVITY_EARTH;

                // gForce will be close to 1 when there is no movement
                double gForce = Math.sqrt(gX * gX + gY * gY + gZ * gZ);

                // Change background color if gForce exceeds threshold;
                // otherwise, reset the color
                if (gForce > SHAKE_THRESHOLD) {
//                Toast.makeText(getApplicationContext(), "Hay movimiento de " + gForce, Toast.LENGTH_SHORT).show();
                    if (listener != null) listener.onShakeMovement();
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public void onResume() {
        SensorManager sm = (SensorManager) activity.getSystemService(Activity.SENSOR_SERVICE);
        List<Sensor> sensors = sm.getSensorList(Sensor.TYPE_ACCELEROMETER);
        if (sensors.size() > 0) {
            sm.registerListener(this, sensors.get(0), SensorManager.SENSOR_DELAY_GAME);
        }
    }

    public void onStop() {
        SensorManager sm = (SensorManager) activity.getSystemService(Activity.SENSOR_SERVICE);
        sm.unregisterListener(this);
    }

    public interface OnMovementListener {
        void onShakeMovement();
    }
}
