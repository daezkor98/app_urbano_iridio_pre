package com.urbanoexpress.iridio3.pe.presenter;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.snackbar.Snackbar;
import com.orm.util.NamingHelper;
import com.urbanoexpress.iridio3.pe.util.async.AsyncTaskCoroutine;
import com.urbanoexpress.iridio3.R;
import com.urbanoexpress.iridio3.pe.model.entity.Data;
import com.urbanoexpress.iridio3.pe.model.entity.GuiaGestionada;
import com.urbanoexpress.iridio3.pe.model.entity.Imagen;
import com.urbanoexpress.iridio3.pe.model.entity.Ruta;
import com.urbanoexpress.iridio3.pe.ui.FirmarActivity;
import com.urbanoexpress.iridio3.pe.ui.QRScannerActivity;
import com.urbanoexpress.iridio3.pe.ui.helpers.ModalHelper;
import com.urbanoexpress.iridio3.pe.ui.interfaces.OnClickItemGaleriaListener;
import com.urbanoexpress.iridio3.pe.ui.model.GaleriaDescargaRutaItem;
import com.urbanoexpress.iridio3.pe.util.CameraUtils;
import com.urbanoexpress.iridio3.pe.util.CommonUtils;
import com.urbanoexpress.iridio3.pe.util.CustomSiliCompressor;
import com.urbanoexpress.iridio3.pe.util.FileUtils;
import com.urbanoexpress.iridio3.pe.util.LocationUtils;
import com.urbanoexpress.iridio3.pe.util.Preferences;
import com.urbanoexpress.iridio3.pe.util.constant.LocalAction;
import com.urbanoexpress.iridio3.pe.view.BaseModalsView;
import com.urbanoexpress.iridio3.pe.view.GestionarRecoleccionGuiaValijaView;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GestionarRecoleccionGuiaValijaPresenter implements OnClickItemGaleriaListener {

    private static final String TAG = GestionarRecoleccionGuiaValijaPresenter.class.getSimpleName();

    private GestionarRecoleccionGuiaValijaView view;

    private List<Imagen> dbImagenes;

    private ArrayList<Imagen> cacheImages = new ArrayList<>();

    private List<GaleriaDescargaRutaItem> galeria = new ArrayList<>();

    private Ruta guiaValija;

    private File photoCapture;

    private final int MAX_PHOTO_CAPTURE = 10;
    private final int MAX_SIGNING_CAPTURE = 1;

    private int contadorImagenes = 0, contadorFirma = 0;

    private int selectedIndexGalery = -1;

    private boolean isRecolectado;

    public GestionarRecoleccionGuiaValijaPresenter(GestionarRecoleccionGuiaValijaView view,
                                                   Ruta guiaValija) {
        this.view = view;
        this.guiaValija = guiaValija;
        init();
    }

    private void init() {
        showTextGuiasElectronicas();
        loadGaleria();

        LocalBroadcastManager.getInstance(view.getContextView())
                .registerReceiver(saveFirmaReceiver, new IntentFilter("OnSaveFirma"));
        LocalBroadcastManager.getInstance(view.getContextView())
                .registerReceiver(resultScannReceiver,
                        new IntentFilter(LocalAction.LOCAL_ACTION_BARCODE_SCAN_RESULT));
    }

    @Override
    public void onClickCamera() {
        if (canTakePhoto()) {
            photoCapture = CameraUtils.openCamera(view.getFragment(),
                    "Recoleccion/" + guiaValija.getLineaNegocio() + "-" +
                            guiaValija.getIdGuia() + "/", "");
        } else {
            BaseModalsView.showAlertDialog(view.getContextView(),
                    R.string.text_advertencia,
                    R.string.activity_detalle_ruta_message_no_puede_tomar_foto,
                    R.string.text_aceptar, null);
        }
    }

    @Override
    public void onClickFirma() {
        if (canTakeSigning()) {
            Activity activity = (Activity) view.getContextView();
            Intent intent = new Intent(view.getContextView(), FirmarActivity.class);
            intent.putExtra("pathDirectory" , "Recoleccion/" + guiaValija.getLineaNegocio() +
                    "-" + guiaValija.getIdGuia() + "/");
            activity.startActivity(intent);
            activity.overridePendingTransition(R.anim.slide_enter_from_bottom, R.anim.not_slide);
        } else {
            BaseModalsView.showAlertDialog(view.getContextView(),
                    R.string.text_advertencia,
                    R.string.activity_detalle_ruta_message_no_puede_firmar,
                    R.string.text_aceptar, null);
        }
    }

    @Override
    public void onClickCargo() {

    }

    @Override
    public void onClickVoucher() {

    }

    @Override
    public void onClickImage() {

    }

    @Override
    public void onClickAddPhotoFromGalery() {

    }

    @Override
    public void onClickDeleteImage(int position) {
        Log.d(TAG, "Position click: " + position);
        // Restar 2 posiciones que corresponden a los botones
        selectedIndexGalery = position - 2;

        //dbImagenes = selectAllImages();

        BaseModalsView.showAlertDialog(view.getContextView(),
                R.string.activity_detalle_ruta_title_eliminar_galeria,
                R.string.activity_detalle_ruta_message_eliminar_galeria,
                R.string.text_aceptar, (dialog, which) -> deleteImageGalery(selectedIndexGalery),
                R.string.text_cancelar, null);
    }

    public void onActivityResultImage() {
        if (compressImage()) {
            insertPhotoToGalery();
            saveImage(photoCapture.getName(), photoCapture.getParent() + File.separator, "imagen");
        } else {
            if (view.getContextView() != null) {
                BaseModalsView.showToast(view.getContextView(),
                        "Lo sentimos, ocurrió un error al tomar la foto.", Toast.LENGTH_LONG);
            }
        }
    }

    public void onDestroy() {
        LocalBroadcastManager.getInstance(view.getContextView())
                .unregisterReceiver(saveFirmaReceiver);
        LocalBroadcastManager.getInstance(view.getContextView())
                .unregisterReceiver(resultScannReceiver);
    }

    public void onCLickBtnScanBarCode() {
        Intent intent = new Intent(view.getContextView(), QRScannerActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("typeImpl", QRScannerPresenter.IMPLEMENT.READ_ONLY);
        intent.putExtra("args", bundle);

        view.getContextView().startActivity(intent);
        ((Activity) view.getContextView()).
                overridePendingTransition(R.anim.slide_enter_from_bottom, R.anim.not_slide);
    }

    public void onClickAceptar(boolean isRecolectado) {
        this.isRecolectado = isRecolectado;

        if (validateDescarga()) {
            new SaveGestionTask().execute();
        }
    }

    class SaveGestionTask extends AsyncTaskCoroutine<String, String> {
        @Override
        public void onPreExecute() {
            super.onPreExecute();
            BaseModalsView.showProgressDialog(view.getContextView(), R.string.text_gestionando_recoleccion);
        }

        @Override
        public String doInBackground(String... strings) {
            saveGestionGE();
            saveFormularioRecoleccion();
            checkUploadDataSyncImages();
            return null;
        }

        @Override
        public void onPostExecute(String s) {
            super.onPostExecute(s);
            BaseModalsView.hideProgressDialog();
            closeDialog();
        }
    }

    public void onClickCancelar() {
        new ClearCacheImageTask().execute();
    }

    private void saveImage(final String fileName, final String directory, String anotaciones) {
        Imagen imagen = new Imagen(
                Preferences.getInstance().getString("idUsuario", ""),
                fileName,
                directory,
                Imagen.Tipo.GESTION_GUIA,
                guiaValija.getIdServicioRecoleccion(),
                CameraUtils.getDateLastCapturePhoto().getTime() + "",
                LocationUtils.getLatitude() + "",
                LocationUtils.getLongitude() + "",
                anotaciones,
                guiaValija.getIdServicioRecoleccion(),
                guiaValija.getLineaNegocio(),
                guiaValija.getDataValidate(),
                Data.Sync.PENDING);
        cacheImages.add(imagen);

        contadorImagenes++;

        Log.d(TAG, "save image");
        Log.d(TAG, "file name: " + fileName);
        Log.d(TAG, "file directory: " + directory);
    }

    private void loadGaleria() {
        new VerifyExistImagesOnDeviceTask().execute();
    }

    private void showGaleria() {
        Log.d(TAG, "showGaleria");
        dbImagenes = selectAllImages();

        if (dbImagenes.size() > 0) {
            GaleriaDescargaRutaItem item;

            for (Imagen imagen : dbImagenes) {
                item = new GaleriaDescargaRutaItem(imagen.getPath() + imagen.getName());
                galeria.add(item);

                contadorImagenes++;
            }
        } else {
            Log.d(TAG, "No hay galeria");
        }

        view.showGaleria(galeria);
    }

    private boolean compressImage() {
        String pathImage = photoCapture.getPath();
        Log.d(TAG, "PATHIMAGE SILICOMPRESSOR: " + pathImage);

        try {
            String compressFilePath = CustomSiliCompressor.with(view.getContextView()).compress(pathImage);

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

    private void insertPhotoToGalery() {
        Log.d(TAG, "Image Path: " + photoCapture.getAbsolutePath());
        GaleriaDescargaRutaItem item =
                new GaleriaDescargaRutaItem(photoCapture.getAbsolutePath());
        galeria.add(item);
        view.notifyGaleryAllItemChanged();
    }

    private void showTextGuiasElectronicas() {
        view.setGuiaElectronica(guiaValija.getGuia());
    }

    private void saveGestionGE() {
        Date date = new Date();
        String fecha = new SimpleDateFormat("dd/MM/yyyy").format(date);
        String hora = new SimpleDateFormat("HH:mm:ss").format(date);

        String idMotivoRecoleccion = isRecolectado ? "168" : "185";

        GuiaGestionada guiaGestionada = new GuiaGestionada(
                Preferences.getInstance().getString("idUsuario", ""),
                guiaValija.getIdServicioRecoleccion(),
                idMotivoRecoleccion,
                guiaValija.getTipoZona(),
                "R",
                guiaValija.getLineaNegocio(),
                fecha, hora,
                LocationUtils.getLatitude() + "",
                LocationUtils.getLongitude() + "",
                "", "", "", "0", "0",
                GuiaGestionada.Recoleccion.RECOLECCION_GUIA_VALIJA + "",
                "", "", "",
                view.getViewTxtComentarios().getText().toString(),
                "0", "",
                Data.Delete.NO,
                guiaValija.getDataValidate(),
                1
        );
        guiaGestionada.save();
    }

    private void saveFormularioRecoleccion() {
        Log.d(TAG, "SAVE FORMULARIO RECOLECCION");

        int sobre, valija, paquete,  otros;

        sobre = view.getViewTxtFrmSobre().getText().toString().trim().isEmpty()
                ? 0 : Integer.parseInt(view.getViewTxtFrmSobre().getText().toString().trim());
        valija = view.getViewTxtFrmValija().getText().toString().trim().isEmpty()
                ? 0 : Integer.parseInt(view.getViewTxtFrmValija().getText().toString().trim());
        paquete = view.getViewTxtFrmPaquete().getText().toString().trim().isEmpty()
                ? 0 : Integer.parseInt(view.getViewTxtFrmPaquete().getText().toString().trim());
        otros = view.getViewTxtFrmOtros().getText().toString().trim().isEmpty()
                ? 0 : Integer.parseInt(view.getViewTxtFrmOtros().getText().toString().trim());

        GuiaGestionada guiaGestionada = new GuiaGestionada(
                Preferences.getInstance().getString("idUsuario", ""),
                guiaValija.getIdServicio(),
                "",
                guiaValija.getTipoZona(),
                guiaValija.getTipo(),
                guiaValija.getLineaNegocio(),
                "",
                "",
                "", "", "",
                sobre + "",
                valija + "",
                paquete + "",
                otros + "",
                GuiaGestionada.Recoleccion.FORMULARIO_RECOLECCION + "",
                view.getViewTxtFrmGuiaRecoleccion().getText().toString() + "",
                "",
                "",
                "",
                "0",
                "",
                Data.Delete.NO,
                guiaValija.getDataValidate(),
                1
        );
        guiaGestionada.save();
    }

    private void deleteImageGalery(int position) {
        FileUtils.deleteFile(cacheImages.get(position).getPath() + cacheImages.get(position).getName());

        contadorImagenes--;

        cacheImages.remove(position);

        galeria.remove(position);

        view.notifyGaleryItemRemove(position + 2); // Add 2 for header
//        noRecolectaView.notifyGaleryAllItemChanged();
    }

    private void checkUploadDataSyncImages() {
        Log.d(TAG, "checkUploadDataSyncImages");
        for (int i = 0; i < cacheImages.size(); i++) {
            Log.d(TAG, "SAVE IMAGEN FROM CACHE: " + cacheImages.get(i).getIdSuperior());
            cacheImages.get(i).save();
        }
    }

    private boolean validateDescarga() {
        if (validateFechaDispositivo()) {
            if (validateFormularioRecoleccion()) {
                if (validateFoto()) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean validateFechaDispositivo() {
        try {
            Date fechaActual = new Date();
            Date fechaRuta = new SimpleDateFormat("dd/MM/yyyy").parse(guiaValija.getFechaRuta());

            if (fechaActual.getTime() >= fechaRuta.getTime()) {
                return true;
            } else {
                ((AppCompatActivity) view.getContextView()).runOnUiThread(() -> {
                    ModalHelper.getBuilderAlertDialog(view.getContextView())
                            .setTitle(R.string.text_configurar_fecha_hora)
                            .setIcon(R.mipmap.ic_launcher)
                            .setMessage(R.string.act_main_message_date_time_incorrect)
                            .setPositiveButton(R.string.text_configurar, (dialog, which) -> {
                                Intent intent = new Intent(Settings.ACTION_DATE_SETTINGS);
                                view.getContextView().startActivity(intent);
                            })
                            .setNegativeButton(R.string.text_cancelar, null)
                            .show();
                });
                return false;
            }
        } catch (ParseException ex) {
            ex.printStackTrace();
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return true;
        }
    }

    private boolean validateFormularioRecoleccion() {
        if (isRecolectado) {
            /*if (view.getViewTxtFrmGuiaRecoleccion().getText().toString().trim().isEmpty()) {
                view.getViewTxtFrmGuiaRecoleccion().setError(
                        view.getContextView().getString(
                                R.string.act_detalle_ruta_msg_error_ingresa_guia_recoleccion));
                view.getViewTxtFrmGuiaRecoleccion().requestFocus();
                return false;
            }*/

                //String guiaRecoleccion = view.getViewTxtFrmGuiaRecoleccion().getText().toString().trim().toUpperCase();

            /*if (!guiaRecoleccion.matches("^(GRE)\\d{1,10}$")) {
                view.getViewTxtFrmGuiaRecoleccion().setError(
                        view.getContextView().getString(
                                R.string.act_detalle_ruta_msg_error_guia_recoleccion_incorrecto));
                view.getViewTxtFrmGuiaRecoleccion().requestFocus();
                return false;
            }*/

            /*if (!guiaRecoleccion.isEmpty()) {
                guiaRecoleccion = guiaRecoleccion.replaceAll("(GRE)+", "");

            long guiasGestionadas = GuiaGestionada.count(GuiaGestionada.class,
                    NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                            NamingHelper.toSQLNameDefault("lineaNegocio") + " = ? and " +
                            NamingHelper.toSQLNameDefault("recoleccion") + " = " + GuiaGestionada.Recoleccion.FORMULARIO_RECOLECCION + " and " +
                            NamingHelper.toSQLNameDefault("guiaElectronica") + " in ('" + guiaRecoleccion + "', 'GRE" + guiaRecoleccion + "')",
                    new String[]{Preferences.getInstance().getString("idUsuario", ""),
                            guiaValija.getLineaNegocio()});

            if (guiasGestionadas >= 1) {
                view.getViewTxtFrmGuiaRecoleccion().setError(
                        view.getContextView().getString(
                                R.string.act_detalle_ruta_msg_error_guia_recoleccion_en_uso));
                view.getViewTxtFrmGuiaRecoleccion().requestFocus();
                return false;
            }

            if (!ValidationUtils.validateDigitoValido(guiaRecoleccion)) {
                view.getViewTxtFrmGuiaRecoleccion().setError(
                        view.getContextView().getString(
                                R.string.act_detalle_ruta_msg_error_guia_recoleccion_incorrecto));
                view.getViewTxtFrmGuiaRecoleccion().requestFocus();
                return false;
            }*/

            int sobre, valija, paquete,  otros;

            sobre = view.getViewTxtFrmSobre().getText().toString().trim().isEmpty()
                    ? 0 : Integer.parseInt(view.getViewTxtFrmSobre().getText().toString().trim());
            valija = view.getViewTxtFrmValija().getText().toString().trim().isEmpty()
                    ? 0 : Integer.parseInt(view.getViewTxtFrmValija().getText().toString().trim());
            paquete = view.getViewTxtFrmPaquete().getText().toString().trim().isEmpty()
                    ? 0 : Integer.parseInt(view.getViewTxtFrmPaquete().getText().toString().trim());
            otros = view.getViewTxtFrmOtros().getText().toString().trim().isEmpty()
                    ? 0 : Integer.parseInt(view.getViewTxtFrmOtros().getText().toString().trim());

            if (!(sobre > 0 || valija > 0 || paquete > 0 || otros > 0)) {
                BaseModalsView.showSnackBar(view.getViewTxtComentarios(),
                        R.string.act_detalle_ruta_msg_error_frm_rec_ingresar_cantidad,
                        Snackbar.LENGTH_LONG);
                /*BaseModalsView.showSnackBar(view.getViewTxtComentarios(),
                        R.string.act_detalle_ruta_msg_error_frm_rec_ingresar_cantidad,
                        Snackbar.LENGTH_INDEFINITE,
                        R.string.text_ok,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        });*/
                return false;
            }
            //}
        }

        return true;
    }

    private boolean validateFoto() {
        if (contadorImagenes == 0) {
            BaseModalsView.showSnackBar(view.getViewTxtComentarios(),
                    R.string.activity_detalle_ruta_message_tomar_al_menos_una_foto,
                    Snackbar.LENGTH_LONG);
        }
        return contadorImagenes > 0;
    }

    private boolean canTakePhoto() {
        return (contadorImagenes + 1) <= MAX_PHOTO_CAPTURE;
    }

    private boolean canTakeSigning() {
        return (contadorFirma + 1) <= MAX_SIGNING_CAPTURE;
    }

    private void closeDialog() {
        ((DialogFragment) view.getFragment()).dismiss();
    }

    private List<Imagen> selectAllImages() {
        return Imagen.find(Imagen.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("idSuperior") + " = ? and " +
                        NamingHelper.toSQLNameDefault("clasificacion") + " = ?",
                Preferences.getInstance().getString("idUsuario", ""),
                guiaValija.getIdServicioRecoleccion(),
                Imagen.Tipo.GESTION_GUIA + "");
    }

    private class VerifyExistImagesOnDeviceTask extends AsyncTaskCoroutine<Void, Boolean> {

        private final String TAG = VerifyExistImagesOnDeviceTask.class.getSimpleName();

        @Override
        public Boolean doInBackground(Void... params) {
            Log.d(TAG, "doInBackground");

            List<Imagen> dbImagenes = selectAllImages();

            for (Imagen imagen : dbImagenes) {
                if (!FileUtils.existFile(imagen.getPath() + imagen.getName())) {
                    imagen.delete();
                }
            }

            return true;
        }

        @Override
        public void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            Log.d(TAG, "onPostExecute");
            showGaleria();
        }

    }

    private class ClearCacheImageTask extends AsyncTaskCoroutine<String, String> {

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            BaseModalsView.showProgressDialog(view.getContextView(), R.string.text_espere_un_momento);
        }

        @Override
        public String doInBackground(String... strings) {
            for (int i = 0; i < cacheImages.size(); i++) {
                //Log.d(TAG, "ELIMINANDO CACHE IMAGEN: " + cacheImages.get(i).getPath() + cacheImages.get(i).getName());
                FileUtils.deleteFile(cacheImages.get(i).getPath() + cacheImages.get(i).getName());
            }
            return null;
        }

        @Override
        public void onPostExecute(String s) {
            super.onPostExecute(s);
            BaseModalsView.hideProgressDialog();
            closeDialog();
        }
    }

    /**
     * Broadcast
     *
     * {@link FirmarActivity#sendOnSaveFirmaReceiver}
     */
    private BroadcastReceiver saveFirmaReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            photoCapture = (File) intent.getSerializableExtra("file");
            insertPhotoToGalery();
            saveImage(photoCapture.getName(), photoCapture.getParent() + File.separator, "firma");
        }
    };

    /**
     * Broadcast
     *
     * {@link CodeScannerImpl#sendOnResultScannListener}
     */
    private BroadcastReceiver resultScannReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                CommonUtils.vibrateDevice(view.getContextView(), 100);
            } catch (NullPointerException ex) {
                ex.printStackTrace();
            }
            view.getViewTxtFrmGuiaRecoleccion().setText(intent.getStringExtra("value"));
        }
    };

}