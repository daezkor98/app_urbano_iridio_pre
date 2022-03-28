package com.urbanoexpress.iridio3.util;

import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.location.LocationManager;
import android.os.BatteryManager;
import android.os.Build;
import android.provider.Settings;
import androidx.appcompat.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.lang.reflect.Method;

import com.urbanoexpress.iridio3.R;

/**
 * Created by mick on 19/05/16.
 */
public class InfoDevice {

    private static AlertDialog alertDialogGPS;

    public static TelephonyManager getTelephonyManager(Context context){
        return (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    }

    public static LocationManager getLocationManager(Context context){
        return (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    public static String getIMEI(Context context) {
        String deviceID = null;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            deviceID = getTelephonyManager(context).getDeviceId();
        }

        if (deviceID == null) {
            // No ce reconoce el IMEI del dispositivo
            deviceID = getIMEIFromSystemProperties();
        }

//        int phoneType = phonyManager.getPhoneType();
//        switch (phoneType) {
//            case TelephonyManager.PHONE_TYPE_NONE:
//                return deviceID;
//            case TelephonyManager.PHONE_TYPE_GSM:
//                return deviceID;
//            case TelephonyManager.PHONE_TYPE_CDMA:
//                return deviceID;
//            default:
//                return deviceID;
//        }
        return deviceID;
    }

    public static boolean isGPSEnabled(Context context, boolean dialogCancelable) {
        if (alertDialogGPS != null) {
            if (alertDialogGPS.isShowing()) alertDialogGPS.dismiss();
        }
        if (getLocationManager(context).isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            return true;
        }
        showAlertMessageNoGps(context, dialogCancelable);
        return false;
    }

    public static boolean isActiveGPS(Context context) {
        if (getLocationManager(context).isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            return true;
        }
        return false;
    }

    public static void showAlertMessageNoGps(final Context context, boolean dialogCancelable) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.title_dialog_no_gps))
                .setMessage(context.getString(R.string.message_dialog_enable_gps))
                .setPositiveButton(R.string.text_configurar, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                });

        if (dialogCancelable) {
            builder.setCancelable(false);
        } else {
            builder.setNegativeButton(R.string.text_cancelar, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }

        alertDialogGPS = builder.create();
        alertDialogGPS.show();
    }

    public static boolean checkCameraFlashHardware(Context context) {

        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            return true;
        }

        return false;
    }

    public static void setFlashMode(String flashMode) {
        //Open Camera
        Camera mCamera = Camera.open();

        //Get Camera Params for customisation
        Camera.Parameters parameters = mCamera.getParameters();

        parameters.setFlashMode(flashMode);

        mCamera.setParameters(parameters);
        mCamera.startPreview();
    }

    public static String getBattery(Context context) {
        try {
            IntentFilter batIntentFilter =new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent battery = context.registerReceiver(null, batIntentFilter);
            int nivelBateria = battery.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            return Integer.toString(nivelBateria);
        } catch (Exception e) {
            return "0";
        }
    }

    public static boolean isServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private static String getIMEIFromSystemProperties() {
        String serial = null;
        try {
            Class<?> SystemProperties = Class.forName("android.os.SystemProperties");
            Method get = SystemProperties.getMethod("get", String.class);
            serial = (String) get.invoke(SystemProperties, "ro.serisalno"); }
        catch (Exception ignored) {
            Log.d("ERROR", "ReturnSerial");
        }
        return serial;
    }
}
