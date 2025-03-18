package com.urbanoexpress.iridio3.pe.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import androidx.fragment.app.Fragment;
import androidx.core.content.FileProvider;
import android.util.Log;

import com.urbanoexpress.iridio3.pe.BuildConfig;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by mick on 20/07/16.
 */
public class CameraUtils {

    private static final String TAG = CameraUtils.class.getSimpleName();

    private static final int REQUEST_IMAGE_CAPTURE = 100;

    private static Date dateLastCapturePhoto;

    public static Intent getIntentImageCapture(Context context, File photoFile) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(context,
                    BuildConfig.APPLICATION_ID + ".provider",
                    photoFile);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        } else {
            uri = Uri.fromFile(photoFile);
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        return intent;
    }

    public static File openCamera(Fragment fragment, String directory, String prefix) {
        File photo = FileUtils.generateFile(fragment.getContext(), generateImageName(prefix), directory);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra("android.intent.extra.CAMERA_FACING", 0);
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(fragment.getActivity(), BuildConfig.APPLICATION_ID + ".provider", photo);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        } else {
            uri = Uri.fromFile(photo);
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        fragment.startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        Log.d(TAG, "OPEN CAMERA");
        Log.d(TAG, "FILE PATH: " + uri);
        return photo;
    }

    public static boolean validateOnActivityResult(int requestCode, int resultCode) {
        return (resultCode == Activity.RESULT_OK && requestCode == REQUEST_IMAGE_CAPTURE);
    }

    public static String generateImageName(String prefix) {
        dateLastCapturePhoto = new Date();
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(dateLastCapturePhoto);
        String photoName = "Imagen_" + timeStamp + ".jpg";
        if (!prefix.isEmpty()) {
            photoName = prefix + "_" + timeStamp + ".jpg";
        }
        return photoName;
    }

    public static Date getDateLastCapturePhoto() {
        return dateLastCapturePhoto;
    }
}
