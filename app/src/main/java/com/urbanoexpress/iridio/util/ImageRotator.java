package com.urbanoexpress.iridio.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;

import androidx.exifinterface.media.ExifInterface;

import java.io.IOException;
import java.io.InputStream;

public class ImageRotator {

    /**
     * Rotate an image if required.
     *
     * @param context   Context
     * @param img       The image bitmap
     * @param imageUri  Image URI
     * @return The resulted Bitmap after manipulation
     */
    public static Bitmap rotateImageIfRequired(Context context, Bitmap img, Uri imageUri) throws IOException {

//        return img;



        ExifInterface exif;

        if (Build.VERSION.SDK_INT > 23) {
            InputStream input = context.getContentResolver().openInputStream(imageUri);
            exif = new ExifInterface(input);
        } else {
            exif = new ExifInterface(imageUri.getPath());
        }

        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(img, 270);
            default:
                return img;
        }
    }

    /**
     * Rotate an image if required.
     *
     * @param img           The image bitmap
     * @param photoFilePath Image Path
     * @return The resulted Bitmap after manipulation
     */
    public static Bitmap rotateImageIfRequired(Bitmap img, String photoFilePath) throws IOException {
        ExifInterface exif = new ExifInterface(photoFilePath);
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(img, 270);
            default:
                return img;
        }
    }

    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }
}
