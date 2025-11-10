package com.urbanoexpress.iridio3.pre.util;

import static android.os.Build.VERSION_CODES.S_V2;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;

/**
 * Created by mick on 10/03/17.
 */

public class PermissionUtils {

    public static boolean checkAppPermissions(Context context) {
        return checkAppPermissions(context, false);
    }

    public static boolean checkBasicPermissions(Context context) {
        return checkAppPermissions(context, true);
    }

    public static boolean checkBackgroundLocationPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return checkPermissions(context, Manifest.permission.ACCESS_BACKGROUND_LOCATION);
        }
        return true;
    }

    public static boolean checkPermissions(Context context, String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return ContextCompat.checkSelfPermission(context, permission)
                    == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    public static ArrayList<String> getBasicPermissions() {
        ArrayList<String> permissions = new ArrayList<>();
        permissions.add(Manifest.permission.CAMERA);
        permissions.add(Manifest.permission.READ_PHONE_STATE);

        if (Build.VERSION.SDK_INT < S_V2) {
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        return permissions;
    }

    private static boolean checkAppPermissions(Context context, boolean isBasic) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ArrayList<String> permissions = getBasicPermissions();

            if (!isBasic) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    permissions.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
                }
            }

            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(context, permission)
                        != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

}