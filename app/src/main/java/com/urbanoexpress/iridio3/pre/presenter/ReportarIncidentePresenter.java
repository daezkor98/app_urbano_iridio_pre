package com.urbanoexpress.iridio3.pre.presenter;

import android.Manifest;
import android.content.Intent;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.urbanoexpress.iridio3.pre.util.async.AsyncTaskCoroutine;
import com.urbanoexpress.iridio3.pre.R;
import com.urbanoexpress.iridio3.pre.model.entity.IncidenteRuta;
import com.urbanoexpress.iridio3.pre.ui.RequestPermissionActivity;
import com.urbanoexpress.iridio3.pre.util.CameraUtils;
import com.urbanoexpress.iridio3.pre.util.CustomSiliCompressor;
import com.urbanoexpress.iridio3.pre.util.FileUtils;
import com.urbanoexpress.iridio3.pre.util.LocationUtils;
import com.urbanoexpress.iridio3.pre.util.PermissionUtils;
import com.urbanoexpress.iridio3.pre.util.Preferences;
import com.urbanoexpress.iridio3.pre.util.constant.LocalAction;
import com.urbanoexpress.iridio3.pre.view.BaseModalsView;
import com.urbanoexpress.iridio3.pre.view.ReportarIncidenteView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReportarIncidentePresenter {

    private static final String TAG = ReportarIncidentePresenter.class.getSimpleName();

    private ReportarIncidenteView view;

    private File photoCapture;

    private String directoryPhotos = "";

    private String typeCameraCaptureImage;

    private String idPlanDeViaje;

    private int idMotivoIncidente;

    private boolean fotoDisponible = false;

    private IncidenteRuta incidente;

    /**
     * Provides access to the Fused Location Provider API.
     */
    private FusedLocationProviderClient mFusedLocationClient;

    public ReportarIncidentePresenter(ReportarIncidenteView view, String idPlanDeViaje, int idMotivoIncidente) {
        this.view = view;
        this.idPlanDeViaje = idPlanDeViaje;
        this.idMotivoIncidente = idMotivoIncidente;
        this.mFusedLocationClient = LocationServices.getFusedLocationProviderClient(view.getContextView());
        init();
    }

    private void init() {
        directoryPhotos = "ParadaProgramada/" + idPlanDeViaje + "-150918/";
    }

    public void onClickCamera() {
        typeCameraCaptureImage = "Foto";

        photoCapture = CameraUtils.openCamera(view.getFragment(), directoryPhotos, "");
    }

    public void onClickReportarIncidente() {
        if (PermissionUtils.checkPermissions(view.getContextView(), Manifest.permission.ACCESS_FINE_LOCATION)) {
            if (validateDatos()) {
                new SaveIncidenteTask().execute();
            }
        } else {
            view.getContextView().startActivity(new Intent(view.getContextView(), RequestPermissionActivity.class));
            ((AppCompatActivity) view.getContextView()).overridePendingTransition(R.anim.slide_enter_from_bottom, R.anim.not_slide);
        }
    }

    public void onActivityResultImage() {
        if (compressImage()) {
            fotoDisponible = true;
            Log.d(TAG, "Image Path: " + photoCapture.getAbsolutePath());
            view.showImage(photoCapture.getAbsolutePath());
        } else {
            if (view.getContextView() != null) {
                BaseModalsView.showToast(view.getContextView(),
                        "Lo sentimos, ocurrió un error al tomar la foto.", Toast.LENGTH_LONG);
            }
        }
    }

    private boolean compressImage() {
        String pathImage = photoCapture.getPath();
        Log.d(TAG, "PATHIMAGE SILICOMPRESSOR: " + pathImage);

        String compressFilePath = "";

        try {
            if (typeCameraCaptureImage.equalsIgnoreCase("Foto")) {
                compressFilePath = CustomSiliCompressor.with(view.getContextView()).compress(pathImage);
            } else if (typeCameraCaptureImage.equalsIgnoreCase("Cargo")) {
                compressFilePath = CustomSiliCompressor.with(view.getContextView())
                        .compress(pathImage, 1280.0f, 720.0f, 85);
            }

            Log.d(TAG, "FILEPATH SILICOMPRESSOR: " + compressFilePath);

            if (photoCapture.delete()) {
                if (FileUtils.copyFile(compressFilePath, pathImage, true)) return true;
            }
        } catch (ArithmeticException ex) {
            ex.printStackTrace();
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    private boolean validateFoto() {
        if (!fotoDisponible) {
            BaseModalsView.showToast(view.getContextView(),
                    R.string.dlg_reportar_incidente_msg_tomar_al_menos_una_foto,
                    Toast.LENGTH_LONG);
        }
        return fotoDisponible;
    }

    private boolean validateDatos() {
        if (validateFoto()) {
            return true;
        }
        return false;
    }

    private void registerIncidente() {
        mFusedLocationClient.getLastLocation()
                .addOnCompleteListener((AppCompatActivity) view.getContextView(), new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            LocationUtils.setCurrentLocation(task.getResult());
                        }
                        saveIncidente();
                        sendIncidenteReportadoReceiver();
                    }
                });
    }

    private void saveIncidente() {
        Date date = new Date();
        String fecha = new SimpleDateFormat("dd/MM/yyyy").format(date);
        String hora = new SimpleDateFormat("HH:mm:ss").format(date);

        incidente = new IncidenteRuta(
                Preferences.getInstance().getString("idUsuario", ""),
                idPlanDeViaje,
                IncidenteRuta.TipoRuta.PLAN_DE_VIAJE,
                idMotivoIncidente + "",
                photoCapture.getName(),
                photoCapture.getParent() + File.separator,
                "imagen",
                fecha, hora,
                view.getViewTxtComentarios().getText().toString().trim(),
                LocationUtils.getLatitude() + "",
                LocationUtils.getLongitude() + "",
                "3"
        );
        incidente.save();
    }

    class SaveIncidenteTask extends AsyncTaskCoroutine<String, String> {
        @Override
        public void onPreExecute() {
            super.onPreExecute();
            BaseModalsView.showProgressDialog(view.getContextView(), R.string.text_gestionando_guia);
        }

        @Override
        public String doInBackground(String... strings) {
            registerIncidente();
            return null;
        }

        @Override
        public void onPostExecute(String s) {
            super.onPostExecute(s);
            BaseModalsView.hideProgressDialog();

            ((DialogFragment) view.getFragment()).dismiss();

            BaseModalsView.showToast(view.getContextView(),
                    R.string.dlg_reportar_incidente_msg_reporte_incidente_exitoso,
                    Toast.LENGTH_LONG);

            ((AppCompatActivity) view.getContextView()).finish();
            ((AppCompatActivity) view.getContextView())
                    .overridePendingTransition(R.anim.not_slide, R.anim.slide_exit_out_bottom);
        }
    }

    /**
     * Receiver
     *
     * {@link GoogleMapPresenter#incidenteReportadoReceiver}
     */
    private void sendIncidenteReportadoReceiver() {
        Intent intent = new Intent(LocalAction.INCIDENTE_REPORTADO_ACTION);
        intent.putExtra("incidente", incidente);
        LocalBroadcastManager.getInstance(view.getContextView()).sendBroadcast(intent);
    }
}
