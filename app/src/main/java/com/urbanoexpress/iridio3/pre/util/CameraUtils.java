package com.urbanoexpress.iridio3.pre.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.widget.Toast;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.FileProvider;
import android.util.Log;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.urbanoexpress.iridio3.pre.BuildConfig;

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
        // Verificar que el permiso de cámara no haya sido revocado
        if (ContextCompat.checkSelfPermission(fragment.requireContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            Log.w(TAG, "openCamera: permiso de cámara revocado");
            Toast.makeText(fragment.requireContext(),
                    "Permiso de cámara denegado. Actívelo en Ajustes del dispositivo.",
                    Toast.LENGTH_LONG).show();
            return null;
        }

        File photo = FileUtils.generateFile(fragment.getContext(), generateImageName(prefix), directory);

        // Persiste la ruta para recuperarla si el proceso muere mientras la cámara está abierta
        // commit() síncrono garantiza que se escriba antes de que ColorOS/OEMs agresivos maten el proceso
        fragment.getContext().getSharedPreferences("camera_state", android.content.Context.MODE_PRIVATE)
                .edit()
                .putString("last_photo_path", photo.getAbsolutePath())
                .commit();

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra("android.intent.extra.CAMERA_FACING", 0);
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(fragment.getActivity(), BuildConfig.APPLICATION_ID + ".provider", photo);
            // FLAG_GRANT_READ_URI_PERMISSION requerido desde targetSdk 35: OEMs (ColorOS, MIUI)
            // necesitan permiso de lectura además de escritura para confirmar el archivo escrito
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
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
        // dateLastCapturePhoto es un static field que solo se setea en generateImageName().
        // Si el proceso de la app fue matado mientras la cámara estaba abierta (común en
        // dispositivos con poca RAM como Samsung A7), al revivir este campo es null.
        // Devolver new Date() como fallback evita el NPE en los 14 saveImage() que lo usan
        // y permite que la foto se guarde con un timestamp razonable (now en vez de
        // perder la foto y mostrar error al motorizado).
        if (dateLastCapturePhoto == null) {
            FirebaseCrashlytics.getInstance().recordException(
                    new IllegalStateException("CameraUtils.getDateLastCapturePhoto: dateLastCapturePhoto era null "
                            + "(probable muerte de proceso durante captura). Usando new Date() como fallback."));
            return new Date();
        }
        return dateLastCapturePhoto;
    }

    /**
     * Recupera el File de la última foto tomada desde SharedPreferences.
     * Sirve para los casos en que Android mata el proceso mientras la cámara está abierta
     * (dispositivos con poca RAM), lo que provoca que photoCapture sea null en onActivityResult.
     */
    public static File restoreLastPhotoCapture(android.content.Context context) {
        String path = context.getSharedPreferences("camera_state", android.content.Context.MODE_PRIVATE)
                .getString("last_photo_path", null);
        if (path != null) {
            File file = new File(path);
            return file.exists() ? file : null;
        }
        return null;
    }

    /**
     * Comprime una foto capturada manejando todos los errores posibles (OOM, NPE, IO, etc.)
     * y reportándolos a Crashlytics.
     *
     * Reemplaza el patrón duplicado en todos los presenters donde el catch solo cubría
     * ArithmeticException/IllegalArgumentException y dejaba pasar OOM/NPE/IOException
     * silenciosamente (printStackTrace al logcat, sin reporte a Crashlytics).
     *
     * @param context Context de la app
     * @param photoCapture Archivo de la foto. Si es null o no existe, retorna false.
     * @param useLowerResolution true para fotos tipo Cargo/Voucher/Foto (1280x720, calidad 85)
     * @return true si la compresión y copia fueron exitosas
     */
    public static boolean safeCompressImage(android.content.Context context,
                                             File photoCapture,
                                             boolean useLowerResolution) {
        // Validación previa: archivo debe existir y tener contenido
        // Cubre el caso ColorOS/OneUI donde la cámara devuelve RESULT_OK antes de escribir el archivo
        if (photoCapture == null) {
            Log.e(TAG, "safeCompressImage: photoCapture es null");
            FirebaseCrashlytics.getInstance().recordException(
                    new IllegalStateException("safeCompressImage: photoCapture is null"));
            return false;
        }
        if (!photoCapture.exists()) {
            Log.e(TAG, "safeCompressImage: archivo no existe " + photoCapture.getAbsolutePath());
            FirebaseCrashlytics.getInstance().recordException(
                    new java.io.FileNotFoundException("safeCompressImage: file does not exist: "
                            + photoCapture.getAbsolutePath()));
            return false;
        }
        if (photoCapture.length() == 0) {
            Log.e(TAG, "safeCompressImage: archivo vacío " + photoCapture.getAbsolutePath());
            FirebaseCrashlytics.getInstance().recordException(
                    new IllegalStateException("safeCompressImage: file is empty (OEM race condition?): "
                            + photoCapture.getAbsolutePath()));
            return false;
        }

        String pathImage = photoCapture.getPath();
        Log.d(TAG, "safeCompressImage path: " + pathImage);

        try {
            String compressFilePath;
            if (useLowerResolution) {
                compressFilePath = com.urbanoexpress.iridio3.pre.util.CustomSiliCompressor
                        .with(context).compress(pathImage, 1280.0f, 720.0f, 85);
            } else {
                compressFilePath = com.urbanoexpress.iridio3.pre.util.CustomSiliCompressor
                        .with(context).compress(pathImage);
            }

            Log.d(TAG, "safeCompressImage compressed: " + compressFilePath);

            if (compressFilePath == null) {
                FirebaseCrashlytics.getInstance().recordException(
                        new IllegalStateException("safeCompressImage: SiliCompressor returned null path"));
                return false;
            }

            if (photoCapture.delete()) {
                if (FileUtils.copyFile(compressFilePath, pathImage, true)) {
                    return true;
                }
                FirebaseCrashlytics.getInstance().recordException(
                        new java.io.IOException("safeCompressImage: copyFile failed"));
            } else {
                FirebaseCrashlytics.getInstance().recordException(
                        new java.io.IOException("safeCompressImage: photoCapture.delete() returned false: "
                                + pathImage));
            }
        } catch (OutOfMemoryError oom) {
            // Crítico en dispositivos gama baja (4GB RAM) después de varias fotos en memoria
            Log.e(TAG, "safeCompressImage OOM: " + oom.getMessage(), oom);
            FirebaseCrashlytics.getInstance().recordException(
                    new RuntimeException("safeCompressImage OOM", oom));
            // Sugerir GC para liberar memoria antes del siguiente intento
            System.gc();
        } catch (Throwable t) {
            // Captura todo lo demás: NPE, IOException, IllegalArgument, Arithmetic, etc.
            Log.e(TAG, "safeCompressImage error: " + t.getMessage(), t);
            FirebaseCrashlytics.getInstance().recordException(t);
        }
        return false;
    }

    /**
     * Deriva el tipo de imagen a partir del nombre del archivo.
     * Ej: "Cargo_2026-05-19_10-31-03.jpg" → "Cargo"
     * Usado para restaurar typeCameraCaptureImage tras muerte del proceso.
     */
    public static String getTypeFromFileName(String fileName) {
        if (fileName == null) return "Imagen";
        if (fileName.startsWith("Cargo"))              return "Cargo";
        if (fileName.startsWith("Firma"))              return "Firma";
        if (fileName.startsWith("Voucher"))            return "Voucher";
        if (fileName.startsWith("Domicilio"))          return "Domicilio";
        if (fileName.startsWith("Pago"))               return "Pago";
        if (fileName.startsWith("ProductoxCliente"))   return "ProductoxCliente";
        if (fileName.startsWith("Observacion"))        return "Observacion_Entrega";
        return "Imagen";
    }
}
