package com.urbanoexpress.iridio.presenter;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Log;

import com.orm.util.NamingHelper;
import com.urbanoexpress.iridio.AsyncTaskCoroutine;
import com.urbanoexpress.iridio.R;
import com.urbanoexpress.iridio.application.AndroidApplication;
import com.urbanoexpress.iridio.model.entity.Data;
import com.urbanoexpress.iridio.model.entity.DescargaRuta;
import com.urbanoexpress.iridio.model.entity.GuiaGestionada;
import com.urbanoexpress.iridio.model.entity.Imagen;
import com.urbanoexpress.iridio.model.entity.Ruta;
import com.urbanoexpress.iridio.model.interactor.RutaPendienteInteractor;
import com.urbanoexpress.iridio.model.util.ModelUtils;
import com.urbanoexpress.iridio.ui.adapter.model.GalleryButtonItem;
import com.urbanoexpress.iridio.ui.adapter.model.GalleryPhotoItem;
import com.urbanoexpress.iridio.ui.adapter.model.GalleryWrapperItem;
import com.urbanoexpress.iridio.util.CameraUtils;
import com.urbanoexpress.iridio.util.CommonUtils;
import com.urbanoexpress.iridio.util.CustomSiliCompressor;
import com.urbanoexpress.iridio.util.FileUtils;
import com.urbanoexpress.iridio.util.LocationUtils;
import com.urbanoexpress.iridio.util.Preferences;
import com.urbanoexpress.iridio.view.RecoleccionLogisticaInversaView;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RecoleccionLogisticaInversaPresenter {

    private static final String TAG = RecoleccionLogisticaInversaPresenter.class.getSimpleName();

    private RecoleccionLogisticaInversaView view;
    private RutaPendienteInteractor rutaPendienteInteractor;

    private ArrayList<Ruta> rutas;
    private ArrayList<DescargaRuta> descargaRutas = new ArrayList<>();

    private List<GalleryWrapperItem> galeria = new ArrayList<>();
    private List<GalleryWrapperItem> galeriaDomicilio = new ArrayList<>();

    private File photoCapture;

    private String typeCameraCaptureImage;

    private STEPS currentStep;

    private enum STEPS {
        FORMULARIO,
        FOTOS_PRODUCTO,
        FOTOS_DOMICILIO
    }

    public RecoleccionLogisticaInversaPresenter(RecoleccionLogisticaInversaView view,
                                                ArrayList<Ruta> rutas) {
        this.view = view;
        this.rutas = rutas;
        rutaPendienteInteractor = new RutaPendienteInteractor(view.getViewContext());
    }

    public void init() {
        view.setTextGuiaElectronica(
                rutas.stream().map(Ruta::getGuia).collect(Collectors.joining("  •  ")));

        loadDataRutas();
        setVisibilityFirstStep();
        loadGaleria();
    }

    public void onGalleryButtonClick(int position) {
        takePhoto();
    }

    public void onGalleryDeleteImageClick(int position) {
        deleteImageFromGallery(position);
    }

    public void onActivityResultImage() {
        if (compressImage()) {
            insertImageToGallery();
            saveImage(photoCapture.getName(), photoCapture.getParent() + File.separator, typeCameraCaptureImage.toLowerCase());
        } else {
            if (view.getViewContext() != null) {
                view.showToast("Lo sentimos, ocurrió un error al tomar la foto.");
            }
        }
    }

    public void onBtnPlusPaquetesClick(int action) {
        int total = view.getTextFormPaquete().isEmpty()
                ? 0 : Integer.parseInt(view.getTextFormPaquete());

        if (action == 0) {
            total--;
        } else if (action == 1) {
            total++;
        }

        if (total <= 0) {
            view.setTextFormPaquete("0");
            view.setBackgroundBtnMinusPaquetes(
                    ContextCompat.getColor(view.getViewContext(), R.color.gris_9));
        } else if (total < 99999) {
            view.setTextFormPaquete(String.valueOf(total));
            view.setBackgroundBtnMinusPaquetes(
                    ContextCompat.getColor(view.getViewContext(), R.color.colorPrimary));
            view.setBackgroundBtnPlusPaquetes(
                    ContextCompat.getColor(view.getViewContext(), R.color.colorPrimary));
        } else {
            view.setTextFormPaquete("99999");
            view.setBackgroundBtnPlusPaquetes(
                    ContextCompat.getColor(view.getViewContext(), R.color.gris_9));
        }
    }

    public void onBtnPlusSobresClick(int action) {
        int total = view.getTextFormSobre().isEmpty()
                ? 0 : Integer.parseInt(view.getTextFormSobre());

        if (action == 0) {
            total--;
        } else if (action == 1) {
            total++;
        }

        if (total <= 0) {
            view.setTextFormSobre("0");
            view.setBackgroundBtnMinusSobres(
                    ContextCompat.getColor(view.getViewContext(), R.color.gris_9));
        } else if (total < 99999) {
            view.setTextFormSobre(String.valueOf(total));
            view.setBackgroundBtnMinusSobres(

                    ContextCompat.getColor(view.getViewContext(), R.color.colorPrimary));
            view.setBackgroundBtnPlusSobres(
                    ContextCompat.getColor(view.getViewContext(), R.color.colorPrimary));
        } else {
            view.setTextFormSobre("99999");
            view.setBackgroundBtnPlusSobres(
                    ContextCompat.getColor(view.getViewContext(), R.color.gris_9));
        }
    }

    public void onBtnPlusValijasClick(int action) {
        int total = view.getTextFormValija().isEmpty()
                ? 0 : Integer.parseInt(view.getTextFormValija());

        if (action == 0) {
            total--;
        } else if (action == 1) {
            total++;
        }

        if (total <= 0) {
            view.setTextFormValija("0");
            view.setBackgroundBtnMinusValijas(
                    ContextCompat.getColor(view.getViewContext(), R.color.gris_9));
        } else if (total < 99999) {
            view.setTextFormValija(String.valueOf(total));
            view.setBackgroundBtnMinusValijas(
                    ContextCompat.getColor(view.getViewContext(), R.color.colorPrimary));
            view.setBackgroundBtnPlusValijas(
                    ContextCompat.getColor(view.getViewContext(), R.color.colorPrimary));
        } else {
            view.setTextFormValija("99999");
            view.setBackgroundBtnPlusValijas(
                    ContextCompat.getColor(view.getViewContext(), R.color.gris_9));
        }
    }

    public void onBtnPlusOtrosClick(int action) {
        int total = view.getTextFormOtros().isEmpty()
                ? 0 : Integer.parseInt(view.getTextFormOtros());

        if (action == 0) {
            total--;
        } else if (action == 1) {
            total++;
        }

        if (total <= 0) {
            view.setTextFormOtros("0");
            view.setBackgroundBtnMinusOtros(
                    ContextCompat.getColor(view.getViewContext(), R.color.gris_9));
        } else if (total < 99999) {
            view.setTextFormOtros(String.valueOf(total));
            view.setBackgroundBtnMinusOtros(
                    ContextCompat.getColor(view.getViewContext(), R.color.colorPrimary));
            view.setBackgroundBtnPlusOtros(
                    ContextCompat.getColor(view.getViewContext(), R.color.colorPrimary));
        } else {
            view.setTextFormOtros("99999");
            view.setBackgroundBtnPlusOtros(
                    ContextCompat.getColor(view.getViewContext(), R.color.gris_9));
        }
    }

    public void onBtnSiguienteClick() {
        if (currentStep == STEPS.FORMULARIO) {
            if (validateFormularioRecoleccion()) {
                view.hideStepFormulario();
                view.showStepFotosProducto();
                view.notifyGaleriaFotosAllItemChanged();
                currentStep = STEPS.FOTOS_PRODUCTO;
                view.hideKeyboard();
            }
            return;
        }

        if (currentStep == STEPS.FOTOS_PRODUCTO) {
            if (validateFotosProducto()) {
                view.hideStepFotosProducto();
                view.showStepFotosDomicilio();
                view.notifyGaleriaDomicilioAllItemChanged();
                view.setTextBtnSiguiente("Gestionar");
                currentStep = STEPS.FOTOS_DOMICILIO;
            }
            return;
        }

        if (currentStep == STEPS.FOTOS_DOMICILIO) {
            if (validateFotosDomicilio()) {
                if (validateFechaDispositivo()) {
                    new SaveGestionTask().execute();
                }
            }
        }
    }

    private void loadDataRutas() {
        for (int i = 0; i < rutas.size(); i++) {
            DescargaRuta descargaRuta = rutaPendienteInteractor.selectDescargaRuta(
                    rutas.get(i).getIdServicio(), rutas.get(i).getLineaNegocio());
            descargaRutas.add(descargaRuta);
        }
    }

    private void loadGaleria() {
        new VerifyExistImagesOnDeviceTask().execute();
    }

    private void setVisibilityFirstStep() {
        currentStep = STEPS.FORMULARIO;
        view.showStepFormulario();
    }

    private void takePhoto() {
        boolean canTakePhoto = false;

        switch (currentStep) {
            case FOTOS_PRODUCTO:
                canTakePhoto = canTakeFotoProducto();
                typeCameraCaptureImage = "Imagen";
                break;
            case FOTOS_DOMICILIO:
                canTakePhoto = canTakeFotoDomicilio();
                typeCameraCaptureImage = "Domicilio";
                break;
        }

        if (canTakePhoto) {
            photoCapture = CameraUtils.openCamera(view.getFragment(),
                    "Descargas/" + rutas.get(0).getLineaNegocio() + "-" +
                            rutas.get(0).getIdGuia() + "/", typeCameraCaptureImage);
        } else {
            view.showMessageCantTakePhoto();
        }
    }

    private void saveImage(final String fileName, final String directory, String anotaciones) {
        Imagen imagen = new Imagen(
                Preferences.getInstance().getString("idUsuario", ""),
                fileName,
                directory,
                Imagen.Tipo.GESTION_GUIA,
                rutas.get(0).getIdServicio(),
                String.valueOf(CameraUtils.getDateLastCapturePhoto().getTime()),
                String.valueOf(LocationUtils.getLatitude()),
                String.valueOf(LocationUtils.getLongitude()),
                anotaciones,
                rutas.stream().map(Ruta::getIdServicio).collect(Collectors.joining("|")),
                rutas.get(0).getLineaNegocio(),
                rutas.get(0).getDataValidate(),
                Data.Sync.MANUAL);
        imagen.save();

        for (int i = 0; i < rutas.size(); i++) {
            Ruta ruta = rutaPendienteInteractor.selectRuta(rutas.get(i).getIdServicio(),
                    rutas.get(i).getLineaNegocio());
            ruta.setIdImagen(ruta.getIdServicio());
            rutas.get(i).setIdImagen(ruta.getIdServicio());
            ruta.save();
        }

        Log.d(TAG, "save image");
        Log.d(TAG, "file name: " + fileName);
        Log.d(TAG, "file directory: " + directory);
    }

    private void saveGestionGE() {
        LocalDateTime now = LocalDateTime.now();
        String fecha = DateTimeFormatter.ofPattern("dd/MM/yyyy").format(now);
        String hora = DateTimeFormatter.ofPattern("HH:mm:ss").format(now);

        String recoleccion = String.valueOf(GuiaGestionada.Recoleccion.LOGISTICA_INVERSA);

        if (ModelUtils.isTipoEnvioValija(rutas.get(0).getTipoEnvio())) {
            recoleccion = String.valueOf(GuiaGestionada.Recoleccion.VALIJA);
        }

        Log.d(TAG, "SAVE DESCARGA: " + rutas.size());
        for (int i = 0; i < rutas.size(); i++) {
            GuiaGestionada guiaGestionada = new GuiaGestionada(
                    Preferences.getInstance().getString("idUsuario", ""),
                    rutas.get(i).getIdServicio(),
                    "168",
                    rutas.get(i).getTipoZona(),
                    rutas.get(i).getTipo(),
                    rutas.get(i).getLineaNegocio(),
                    fecha, hora,
                    String.valueOf(LocationUtils.getLatitude()),
                    String.valueOf(LocationUtils.getLongitude()),
                    "",
                    "",
                    "",
                    "0",
                    "0",
                    recoleccion,
                    "",
                    "",
                    "",
                    view.getTextComentarios(),
                    "0", "",
                    Data.Delete.NO,
                    rutas.get(i).getDataValidate(),
                    1
            );
            guiaGestionada.save();
        }
    }

    private void saveFormularioRecoleccion() {
        Log.d(TAG, "SAVE FORMULARIO RECOLECCION: " + rutas.size());

        for (int i = 0; i < rutas.size(); i++) {
            GuiaGestionada guiaGestionada = new GuiaGestionada(
                    Preferences.getInstance().getString("idUsuario", ""),
                    rutas.get(i).getIdServicio(),
                    "",
                    rutas.get(i).getTipoZona(),
                    rutas.get(i).getTipo(),
                    rutas.get(i).getLineaNegocio(),
                    "",
                    "",
                    "", "", "",
                    view.getTextFormSobre().isEmpty() ? "0" : view.getTextFormSobre(),
                    view.getTextFormValija().isEmpty() ? "0" : view.getTextFormValija(),
                    view.getTextFormPaquete().isEmpty() ? "0" : view.getTextFormPaquete(),
                    view.getTextFormOtros().isEmpty() ? "0" : view.getTextFormOtros(),
                    String.valueOf(GuiaGestionada.Recoleccion.FORMULARIO_RECOLECCION),
                    "",
                    "",
                    "",
                    "",
                    "0",
                    "",
                    Data.Delete.NO,
                    rutas.get(i).getDataValidate(),
                    1
            );
            guiaGestionada.save();
        }
    }

    private void checkUploadDataSyncImages() {
        Log.d(TAG, "checkUploadDataSyncImages");
        List<Imagen> images = Imagen.find(Imagen.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("idSuperior") + " = ? and " +
                        NamingHelper.toSQLNameDefault("dataSync") + " = ? and " +
                        NamingHelper.toSQLNameDefault("clasificacion") + " = ?",
                Preferences.getInstance().getString("idUsuario", ""),
                rutas.get(0).getIdImagen(),
                Data.Sync.MANUAL + "",
                Imagen.Tipo.GESTION_GUIA + "");
        Log.d(TAG, "TOTAL IMAGENS: " + images.size());

        for (int i = 0; i < images.size(); i++) {
            Log.d(TAG, "IMAGEN: " + images.get(i).getIdSuperior());
            images.get(i).setDataSync(Data.Sync.PENDING);
            images.get(i).save();
        }
    }

    private boolean validateFechaDispositivo() {
        try {
            LocalDate current = LocalDate.now();
            LocalDate fechaRuta = LocalDate.parse(rutas.get(0).getFechaRuta(),
                    DateTimeFormatter.ofPattern("dd/MM/yyyy"));

            if (current.isEqual(fechaRuta) || current.isAfter(fechaRuta)) {
                return true;
            } else {
                view.showWrongDateAndTimeMessage();
                CommonUtils.vibrateDevice(view.getViewContext(), 100);
                return false;
            }
        } catch (DateTimeParseException ex) {
            ex.printStackTrace();
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return true;
        }
    }

    private boolean validateFormularioRecoleccion() {
        /*if (recoleccionGEView.getViewTxtFrmGuiaRecoleccion().getText().toString().trim().isEmpty()) {
            recoleccionGEView.getViewTxtFrmGuiaRecoleccion().setError(
                    recoleccionGEView.getContext()().getString(
                            R.string.act_detalle_ruta_msg_error_ingresa_guia_recoleccion));
            recoleccionGEView.getViewTxtFrmGuiaRecoleccion().requestFocus();
            return false;
        }*/

        //String guiaRecoleccion = recoleccionGEView.getViewTxtFrmGuiaRecoleccion().getText().toString().trim().toUpperCase();


        /*if (!guiaRecoleccion.matches("^(GRE)\\d{1,10}$")) {
            recoleccionGEView.getViewTxtFrmGuiaRecoleccion().setError(
                    recoleccionGEView.getContext()().getString(
                            R.string.act_detalle_ruta_msg_error_guia_recoleccion_incorrecto));
            recoleccionGEView.getViewTxtFrmGuiaRecoleccion().requestFocus();
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
                            rutas.get(0).getLineaNegocio()});

            if (guiasGestionadas >= 1) {
                recoleccionGEView.getViewTxtFrmGuiaRecoleccion().setError(
                        recoleccionGEView.getContext()().getString(
                                R.string.act_detalle_ruta_msg_error_guia_recoleccion_en_uso));
                recoleccionGEView.getViewTxtFrmGuiaRecoleccion().requestFocus();
                return false;
            }

            if (!ValidationUtils.validateDigitoValido(guiaRecoleccion)) {
                recoleccionGEView.getViewTxtFrmGuiaRecoleccion().setError(
                        recoleccionGEView.getContext()().getString(
                                R.string.act_detalle_ruta_msg_error_guia_recoleccion_incorrecto));
                recoleccionGEView.getViewTxtFrmGuiaRecoleccion().requestFocus();
                return false;
            }*/

        int sobre, valija, paquete,  otros;

        sobre = view.getTextFormSobre().isEmpty() ? 0 : Integer.parseInt(view.getTextFormSobre());
        valija = view.getTextFormValija().isEmpty() ? 0 : Integer.parseInt(view.getTextFormValija());
        paquete = view.getTextFormPaquete().isEmpty() ? 0 : Integer.parseInt(view.getTextFormPaquete());
        otros = view.getTextFormOtros().isEmpty() ? 0 : Integer.parseInt(view.getTextFormOtros());

        if (!(sobre > 0 || valija > 0 || paquete > 0 || otros > 0)) {
            view.showSnackBar(R.string.act_detalle_ruta_msg_error_frm_rec_ingresar_cantidad);
            CommonUtils.vibrateDevice(view.getViewContext(), 100);
            return false;
        }
        //}

        return true;
    }

    private boolean validateFotosProducto() {
        if ((galeria.size() - 1) < 1) {
            view.showSnackBar(
                    "Debe tomar una foto del producto como mínimo para realizar la gestión de la recolección.");
            CommonUtils.vibrateDevice(view.getViewContext(), 100);
            return false;
        }
        return true;
    }

    private boolean validateFotosDomicilio() {
        if ((galeriaDomicilio.size() - 1) < 1) {
            view.showSnackBar(
                    "Debe tomar una foto del domicilio como mínimo para realizar la gestión de la recolección.");
            CommonUtils.vibrateDevice(view.getViewContext(), 100);
            return false;
        }
        return true;
    }

    private boolean canTakeFotoProducto() {
        return galeria.size() <= 10;
    }

    private boolean canTakeFotoDomicilio() {
        return galeriaDomicilio.size() <= 10;
    }

    private void showGalerias() {
        List<Imagen> images = selectAllImages();

        galeria.clear();
        galeriaDomicilio.clear();

        GalleryButtonItem buttonItem = new GalleryButtonItem(R.drawable.ic_camera_grey);
        galeria.add(buttonItem);
        galeriaDomicilio.add(buttonItem);

        for (Imagen imagen : images) {
            GalleryPhotoItem item = new GalleryPhotoItem(imagen.getPath() + imagen.getName());

            switch (imagen.getAnotaciones().toLowerCase()) {
                case "imagen":
                    galeria.add(item);
                    break;
                case "domicilio":
                    galeriaDomicilio.add(item);
                    break;
            }
        }

        view.showFotosEnGaleria(galeria);
        view.showFotosDomicilioEnGaleria(galeriaDomicilio);
    }

    private void insertImageToGallery() {
        Log.d(TAG, "Image Path: " + photoCapture.getAbsolutePath());
        GalleryPhotoItem item = new GalleryPhotoItem(photoCapture.getAbsolutePath());

        if (typeCameraCaptureImage.equalsIgnoreCase("Imagen")) {
            galeria.add(item);
            view.notifyGaleriaFotosAllItemChanged();
        } else if (typeCameraCaptureImage.equalsIgnoreCase("Domicilio")) {
            galeriaDomicilio.add(item);
            view.notifyGaleriaDomicilioAllItemChanged();
        }
    }

    private List<Imagen> selectAllImages() {
        return Imagen.find(Imagen.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("idSuperior") + " = ? and " +
                        NamingHelper.toSQLNameDefault("clasificacion") + " = ?",
                Preferences.getInstance().getString("idUsuario", ""),
                rutas.get(0).getIdImagen(),
                Imagen.Tipo.GESTION_GUIA + "");
    }

    private void deleteImageFromGallery(int position) {
        String name = "", path = "";
        int indexSplit;

        switch (currentStep) {
            case FOTOS_PRODUCTO:
                FileUtils.deleteFile(((GalleryPhotoItem) galeria.get(position)).getPathImage());

                indexSplit = ((GalleryPhotoItem) galeria.get(position)).getPathImage().lastIndexOf("/") + 1;
                name = ((GalleryPhotoItem) galeria.get(position)).getPathImage().substring(indexSplit);
                path = ((GalleryPhotoItem) galeria.get(position)).getPathImage().substring(0, indexSplit);

                galeria.remove(position);
                view.notifyGaleriaFotosItemRemove(position);
                break;
            case FOTOS_DOMICILIO:
                FileUtils.deleteFile(((GalleryPhotoItem) galeriaDomicilio.get(position)).getPathImage());

                indexSplit = ((GalleryPhotoItem) galeriaDomicilio.get(position)).getPathImage().lastIndexOf("/") + 1;
                name = ((GalleryPhotoItem) galeriaDomicilio.get(position)).getPathImage().substring(indexSplit);
                path = ((GalleryPhotoItem) galeriaDomicilio.get(position)).getPathImage().substring(0, indexSplit);

                galeriaDomicilio.remove(position);
                view.notifyGaleriaDomicilioItemRemove(position);
                break;
        }

        List<Imagen> images = Imagen.find(Imagen.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("name") + " = ? and " +
                        NamingHelper.toSQLNameDefault("path") + " = ? and " +
                        NamingHelper.toSQLNameDefault("clasificacion") + " = ?",
                Preferences.getInstance().getString("idUsuario", ""),
                name,
                path,
                Imagen.Tipo.GESTION_GUIA + "");

        if (images.size() > 0) {
            images.get(0).delete();
        }
    }

    private boolean compressImage() {
        String pathImage = photoCapture.getPath();
        Log.d(TAG, "PATHIMAGE SILICOMPRESSOR: " + pathImage);

        String compressFilePath;

        try {
            if (typeCameraCaptureImage.equalsIgnoreCase("Cargo")) {
                compressFilePath = CustomSiliCompressor.with(view.getViewContext())
                        .compress(pathImage, 1280.0f, 720.0f, 85);
            } else {
                compressFilePath = CustomSiliCompressor.with(view.getViewContext()).compress(pathImage);
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

    private void updateEstadoGestionGE(int procesoDescarga) {
        for (int i = 0; i < descargaRutas.size(); i++) {
            descargaRutas.get(i).setProcesoDescarga(procesoDescarga);
            descargaRutas.get(i).save();
        }
        for (int i = 0; i < rutas.size(); i++) {
            Ruta ruta = rutaPendienteInteractor.selectRuta(
                    rutas.get(i).getIdServicio(), rutas.get(i).getLineaNegocio());
            Log.d(TAG, "UPDATE ESTADO DESCARGA ON RUTA");
            ruta.setEstadoDescarga(Ruta.EstadoDescarga.GESTIONADO);
            ruta.setIdMotivo("168");
            ruta.setResultadoGestion(Ruta.ResultadoGestion.EFECTIVA_COMPLETA);
            ruta.setMostrarAlerta(0);
            ruta.save();
            Log.d(TAG, "ESTADO DESCARGA RUTA: " + ruta.getEstadoDescarga());
        }
    }

    /**
     * Receiver
     *
     * {@link com.urbanoexpress.iridio.presenter.DetalleRutaPresenter#descargaFinalizadaReceiver}
     * {@link com.urbanoexpress.iridio.presenter.RutaPendientePresenter#descargaFinalizadaReceiver}
     * {@link com.urbanoexpress.iridio.presenter.RutaGestionadaPresenter#descargaFinalizadaReceiver}
     */
    private void sendOnDescargaFinalizadaReceiver() {
        Intent intent = new Intent("OnDescargaFinalizada");
        Bundle bundle = new Bundle();
        bundle.putSerializable("guias", rutas);
        intent.putExtra("args", bundle);
        LocalBroadcastManager.getInstance(AndroidApplication.getAppContext()).sendBroadcast(intent);
    }

    private class SaveGestionTask extends AsyncTaskCoroutine<String, String> {

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            view.showProgressDialog(R.string.text_gestionando_recoleccion);
        }

        @Override
        public String doInBackground(String... strings) {
            saveGestionGE();
            saveFormularioRecoleccion();
            checkUploadDataSyncImages();
            updateEstadoGestionGE(DescargaRuta.Entrega.FINALIZADO);
            return null;
        }

        @Override
        public void onPostExecute(String s) {
            super.onPostExecute(s);
            view.dismiss();
            sendOnDescargaFinalizadaReceiver();
        }
    }

    //TODO: find replacement
//    private class VerifyExistImagesOnDeviceTask extends AsyncTask<Void, Integer, Boolean> {
    private class VerifyExistImagesOnDeviceTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            List<Imagen> images = selectAllImages();

            for (Imagen imagen : images) {
                if (!FileUtils.existFile(imagen.getPath() + imagen.getName())) {
                    imagen.delete();
                }
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            showGalerias();
        }
    }

}