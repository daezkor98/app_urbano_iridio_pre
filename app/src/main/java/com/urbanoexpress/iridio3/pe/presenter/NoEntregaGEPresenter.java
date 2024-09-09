package com.urbanoexpress.iridio3.pe.presenter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.VolleyError;
import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.google.android.gms.location.LocationServices;
import com.orm.util.NamingHelper;
import com.urbanoexpress.iridio3.pe.util.async.AsyncTaskCoroutine;
import com.urbanoexpress.iridio3.R;
import com.urbanoexpress.iridio3.pe.application.AndroidApplication;
import com.urbanoexpress.iridio3.pe.model.entity.Data;
import com.urbanoexpress.iridio3.pe.model.entity.DescargaRuta;
import com.urbanoexpress.iridio3.pe.model.entity.GuiaGestionada;
import com.urbanoexpress.iridio3.pe.model.entity.Imagen;
import com.urbanoexpress.iridio3.pe.model.entity.MotivoDescarga;
import com.urbanoexpress.iridio3.pe.model.entity.Ruta;
import com.urbanoexpress.iridio3.pe.model.interactor.RutaPendienteInteractor;
import com.urbanoexpress.iridio3.pe.model.interactor.callback.RequestCallback;
import com.urbanoexpress.iridio3.pe.model.util.ModelUtils;
import com.urbanoexpress.iridio3.pe.ui.adapter.model.GalleryButtonItem;
import com.urbanoexpress.iridio3.pe.ui.adapter.model.GalleryPhotoItem;
import com.urbanoexpress.iridio3.pe.ui.adapter.model.GalleryWrapperItem;
import com.urbanoexpress.iridio3.pe.ui.model.MotivoDescargaItem;
import com.urbanoexpress.iridio3.pe.util.CameraUtils;
import com.urbanoexpress.iridio3.pe.util.CommonUtils;
import com.urbanoexpress.iridio3.pe.util.CustomSiliCompressor;
import com.urbanoexpress.iridio3.pe.util.FileUtils;
import com.urbanoexpress.iridio3.pe.util.FileUtilss;
import com.urbanoexpress.iridio3.pe.util.LocationUtils;
import com.urbanoexpress.iridio3.pe.util.MyLocation;
import com.urbanoexpress.iridio3.pe.util.Preferences;
import com.urbanoexpress.iridio3.pe.view.DescargaNoEntregaView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by mick on 25/07/16.
 */
public class NoEntregaGEPresenter {

    private static final String TAG = EntregaGEPresenter.class.getSimpleName();

    private DescargaNoEntregaView view;
    private RutaPendienteInteractor rutaPendienteInteractor;

    private List<MotivoDescarga> dbMotivoDescargas = Collections.emptyList();
    private List<MotivoDescargaItem> motivoItems = new ArrayList<>();

    private List<GalleryWrapperItem> galeria = new ArrayList<>();

    private ArrayList<DescargaRuta> descargaRutas = new ArrayList<>();
    private ArrayList<Ruta> rutas;

    private File photoCapture;

    private int selectedIndexMotivo = -1, tipoMotivoDescarga = 0;

    private int numVecesGestionado;

    private String typeCameraCaptureImage;

    private STEPS currentStep;

    private String dirPathPhotos = "";

    private final int REQUEST_IMAGE_GALLERY = 200;

    private boolean showValidationMsgUltimaGestionEfectiva = true;
    private boolean showValidationMsgEstadoShipper = true;

    private enum STEPS {
        MOTIVO_NO_ENTREGA,
        FOTOS_DOMICILIO
    }

    public NoEntregaGEPresenter(DescargaNoEntregaView view, ArrayList<Ruta> rutas,
                                int numVecesGestionado) {
        this.view = view;
        this.rutas = rutas;
        this.numVecesGestionado = numVecesGestionado;
        rutaPendienteInteractor = new RutaPendienteInteractor(view.getViewContext());
    }

    public void init() {
        setScreenTitle();

        view.setTextGuiaElectronica(
                rutas.stream().map(Ruta::getGuia).collect(Collectors.joining("  •  ")));

        currentStep = STEPS.MOTIVO_NO_ENTREGA;

        loadDataRutas();
        setTipoMotivoDescarga();
        loadMotivos();
        loadGaleria();

        StringBuilder stringBuilder = new StringBuilder("Descargas/");
        stringBuilder.append(rutas.get(0).getLineaNegocio());
        stringBuilder.append("-");
        stringBuilder.append(rutas.get(0).getIdGuia());
        stringBuilder.append("/");
        dirPathPhotos = stringBuilder.toString();
    }

    public void onClickUpdateMotivos() {
        if (CommonUtils.validateConnectivity(view.getViewContext())) {
            getListaMotivo();
        }
    }

    public void onGalleryButtonClick(int position) {
        typeCameraCaptureImage = "Domicilio";
        switch (((GalleryButtonItem) galeria.get(position)).getAction()) {
            case GalleryButtonItem.Action.CAMERA:
                takePhoto();
                break;
            case GalleryButtonItem.Action.GALLERY:
                selectPhotoFromGallery();
                break;
        }
    }

    public void onGalleryDeleteImageClick(int position) {
        deleteImageGallery(position);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (CameraUtils.validateOnActivityResult(requestCode, resultCode)) {
            onActivityResultImageFromCamera();
        } else if (requestCode == REQUEST_IMAGE_GALLERY && resultCode == Activity.RESULT_OK) {
            onActivityResultImageFromStorage(data);
        }
    }

    public void onActivityResultImageFromCamera() {
        if (compressImage()) {
            insertImageToGallery();
            saveImage(photoCapture.getName(), photoCapture.getParent() + File.separator, typeCameraCaptureImage.toLowerCase());
        } else {
            view.showToast("Lo sentimos, ocurrió un error al tomar la foto.");
        }
    }

    public void onActivityResultImageFromStorage(final Intent data) {
        new ProcessImageFromStorageTask().execute(data);
    }

    public void onClickItemMotivo(int position) {
        updateBackgroundSelectListaMotivos(position);
        selectedIndexMotivo = position;
    }

    public void onBtnSiguienteClick() {
        if (currentStep == STEPS.MOTIVO_NO_ENTREGA) {
            if (validateSelectedMotivo() && validateMotivoDescription()) {
                view.hideStepMotivos();
                view.showStepGaleria();
                view.setTextBtnSiguiente("Gestionar");
                currentStep = STEPS.FOTOS_DOMICILIO;
                view.hideKeyboard();
            }
            return;
        }

        if (currentStep == STEPS.FOTOS_DOMICILIO) {
            if (validateFoto()) {
                gestionarGuia();
            }
        }
    }

    public void onBtnGestionarFromValidationDialogClick() {
        gestionarGuia();
    }

    @SuppressLint("MissingPermission")
    private void gestionarGuia() {
        if (!validateFechaDispositivo()) {
            return;
        }

        if (showValidationMsgUltimaGestionEfectiva) {
            if (ModelUtils.isGuiaEntrega(rutas.get(0).getTipo())) {
                if (!validateUltimaGestionEfectiva()) {
                    return;
                }
            }
        }

        if (showValidationMsgEstadoShipper) {
            if (!validateEstadoShipper()) {
                return;
            }
        }

        if (rutas.get(0).getTipoZona() == Ruta.ZONA.RURAL) {
            LocationServices.getFusedLocationProviderClient(AndroidApplication.getAppContext())
                    .getLastLocation().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    LocationUtils.setCurrentLocation(task.getResult());
                }
                new SaveGestionTask().execute();
            });
            return;
        }

        new SaveGestionTask().execute();
    }

    private void getListaMotivo() {
        view.showProgressDialog(R.string.text_actualizando_motivos);

        final RequestCallback callback = new RequestCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                view.dismissProgressDialog();

                try {
                    if (response.getBoolean("success")) {
                        rutaPendienteInteractor.deleteMotivos(tipoMotivoDescarga);
                        saveMotivos(response.getJSONArray("data"));
                        loadMotivos();
                    } else {
                        view.showToast(response.getString("msg_error"));
                    }
                } catch (JSONException ex) {
                    ex.printStackTrace();
                    view.showToast(R.string.json_object_exception);
                }
            }

            @Override
            public void onError(VolleyError error) {
                error.printStackTrace();
                view.dismissProgressDialog();
                view.showToast(R.string.volley_error_message);
            }
        };

        String[] params = new String[]{
                String.valueOf(tipoMotivoDescarga),
                Preferences.getInstance().getString("idUsuario", "")
        };

        rutaPendienteInteractor.getMotivos(params, callback);
    }

    private void takePhoto() {
        if (canTakePhoto()) {
            photoCapture = CameraUtils.openCamera(
                    view.getFragment(), dirPathPhotos, typeCameraCaptureImage);
        } else {
            view.showMessageCantTakePhoto();
        }
    }

    private void selectPhotoFromGallery() {
        if (canTakePhoto()) {
            Fragment fragment = view.getFragment();

            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/jpeg");
                fragment.startActivityForResult(intent, REQUEST_IMAGE_GALLERY);
            } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/jpeg");
                fragment.startActivityForResult(intent, REQUEST_IMAGE_GALLERY);
            }
        } else {
            view.showMessageCantTakePhoto();
        }
    }

    private void saveMotivos(JSONArray data) throws JSONException {
        JSONObject jsonObject;
        for (int i = 0; i < data.length(); i++) {
            jsonObject = data.getJSONObject(i);
            MotivoDescarga motivo = new MotivoDescarga(
                    Preferences.getInstance().getString("idUsuario", ""),
                    tipoMotivoDescarga,
                    jsonObject.getString("mot_id"),
                    jsonObject.getString("codigo"),
                    jsonObject.getString("descri"),
                    jsonObject.getString("linea")
            );
            motivo.save();
        }
    }

    private void loadMotivos() {
        dbMotivoDescargas = rutaPendienteInteractor.selectAllMotivos(
                tipoMotivoDescarga, rutas.get(0).getLineaNegocio());

        selectedIndexMotivo = -1;

        motivoItems = new ArrayList<>();
        MotivoDescargaItem item;

        for (MotivoDescarga motivo : dbMotivoDescargas) {
            item = new MotivoDescargaItem(motivo.getDescripcion(), false);
            motivoItems.add(item);
        }

        view.showListaMotivos(motivoItems);
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

    private void setTipoMotivoDescarga() {
        if (ModelUtils.isGuiaEntrega(rutas.get(0).getTipo())) {
            tipoMotivoDescarga = ModelUtils.getTipoMotivoDescarga(rutas.get(0).getTipoEnvio(), 2);
        } else {
            tipoMotivoDescarga = MotivoDescarga.Tipo.NO_RECOLECTA;
        }
    }

    private void setScreenTitle() {
        if (ModelUtils.isGuiaEntrega(rutas.get(0).getTipo())) {
            view.setScreenTitle("Gestionar: No Entrega");
        } else {
            view.setScreenTitle("Gestionar: No Recolectado");
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

        String recoleccion = "";

        if (ModelUtils.isGuiaRecoleccion(rutas.get(0).getTipo())) {
            recoleccion = String.valueOf(GuiaGestionada.Recoleccion.NO_EFECTIVA);
        }

        for (int i = 0; i < rutas.size(); i++) {
            GuiaGestionada guiaGestionada = new GuiaGestionada(
                    Preferences.getInstance().getString("idUsuario", ""),
                    rutas.get(i).getIdServicio(),
                    dbMotivoDescargas.get(selectedIndexMotivo).getIdMotivo(),
                    rutas.get(i).getTipoZona(),
                    rutas.get(i).getTipo(),
                    rutas.get(i).getLineaNegocio(),
                    fecha, hora,
                    String.valueOf(LocationUtils.getLatitude()),
                    String.valueOf(LocationUtils.getLongitude()),
                    "", "", "", "", "",
                    recoleccion,
                    "", "", "",
                    view.getTextComentarios(),
                    "0", "",
                    Data.Delete.NO,
                    rutas.get(i).getDataValidate(),
                    numVecesGestionado
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

    private boolean validateUltimaGestionEfectiva() {
        showValidationMsgUltimaGestionEfectiva = false;
        // validar si con el cliente y contrato, su la ultima gestion fue efectiva.
        for (int i = 0; i < rutas.size(); i++) {
            if (rutas.get(i).getIdCHKUltimaGestion() != null && rutas.get(i).getIdCHKUltimaGestion().equals("9")) {
                if (CommonUtils.isValidCoords(rutas.get(i).getGpsLatitudeUltimaGestion(), rutas.get(i).getGpsLongitudeUltimaGestion()) &&
                        CommonUtils.isValidCoords(rutas.get(i).getGpsLatitude(), rutas.get(i).getGpsLongitude())) {

                    boolean validateIntersectionLocations = MyLocation.intersectionLocations(
                            Double.parseDouble(rutas.get(i).getGpsLatitudeUltimaGestion()),
                            Double.parseDouble(rutas.get(i).getGpsLongitudeUltimaGestion()),
                            Double.parseDouble(rutas.get(i).getGpsLatitude()),
                            Double.parseDouble(rutas.get(i).getGpsLongitude()), 0.5);

                    // validar que las coordenadas no sea mas de 5 cuadras (En caso sea mas. significa que es otra direccion y no es necesario alertar)
                    if (validateIntersectionLocations) {
                        view.showDialogUltimaGestionEfectiva(rutas.get(i).getGpsLatitudeUltimaGestion(),
                                rutas.get(i).getGpsLongitudeUltimaGestion());
                        return false;
                    }
                } else {
                    view.showDialogUltimaGestionEfectiva(rutas.get(i).getGpsLatitudeUltimaGestion(),
                            rutas.get(i).getGpsLongitudeUltimaGestion());
                    return false;
                }
            }
        }
        return true;
    }

    private boolean validateEstadoShipper() {
        showValidationMsgEstadoShipper = false;
        // validar si con el cliente y contrato, su la ultima gestion fue efectiva.
        for (int i = 0; i < rutas.size(); i++) {
            if (rutas.get(i).getEstadoShipper() != null && rutas.get(i).getEstadoShipper().equals("4")) {
                view.showDialogEstadoShipper();
                return false;
            }
        }
        return true;
    }

    private boolean validateMotivoDescription() {
        String motivoDescrip = view.getTextComentarios();
        if (motivoDescrip.length() > 9) {
            return true;
        }
        view.showSnackBar(R.string.activity_detalle_ruta_message_motivo_no_descrito);
        CommonUtils.vibrateDevice(view.getViewContext(), 100);
        return false;
    }


    private boolean validateSelectedMotivo() {

        if (selectedIndexMotivo >= 0) {
            return true;
        }
        view.showSnackBar(R.string.activity_detalle_ruta_message_motivo_no_seleccionado);
        CommonUtils.vibrateDevice(view.getViewContext(), 100);
        return false;
    }

    private boolean validateFoto() {
        if ((galeria.size() - 1) < 1) {
            view.showSnackBar(R.string.activity_detalle_ruta_message_tomar_al_menos_una_foto);
            CommonUtils.vibrateDevice(view.getViewContext(), 100);
            return false;
        }
        return true;
    }

    private boolean canTakePhoto() {
        int totalBtns = rutas.get(0).getTipoZona() == Ruta.ZONA.RURAL ? 2 : 1;
        int totalImages = galeria.size() - totalBtns;
        return totalImages < 10;
    }

    private void showGaleria() {
        List<Imagen> images = selectAllImages();

        GalleryButtonItem buttonItem = new GalleryButtonItem(R.drawable.ic_camera_grey);
        galeria.add(buttonItem);

        if (rutas.get(0).getTipoZona() == Ruta.ZONA.RURAL) {
            buttonItem = new GalleryButtonItem(R.drawable.ic_image_grey,
                    GalleryButtonItem.Action.GALLERY);
            galeria.add(buttonItem);
        }

        if (images.size() > 0) {
            for (Imagen imagen : images) {
                GalleryPhotoItem item = new GalleryPhotoItem(imagen.getPath() + imagen.getName());

                if (imagen.getAnotaciones().toLowerCase().equals("domicilio")) {
                    galeria.add(item);
                }
            }
        }

        view.showGaleria(galeria);
    }

    private void insertImageToGallery() {
        Log.d(TAG, "Image Path: " + photoCapture.getAbsolutePath());
        GalleryPhotoItem item = new GalleryPhotoItem(photoCapture.getAbsolutePath());
        galeria.add(item);
        view.notifyGalleryAllItemChanged();
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

    private void deleteImageGallery(int position) {
        FileUtils.deleteFile(((GalleryPhotoItem) galeria.get(position)).getPathImage());

        int indexSplit = ((GalleryPhotoItem) galeria.get(position)).getPathImage().lastIndexOf("/") + 1;
        String name = ((GalleryPhotoItem) galeria.get(position)).getPathImage().substring(indexSplit);
        String path = ((GalleryPhotoItem) galeria.get(position)).getPathImage().substring(0, indexSplit);

        galeria.remove(position);
        view.notifyGalleryItemRemove(position);

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
            ruta.setIdMotivo(dbMotivoDescargas.get(selectedIndexMotivo).getIdMotivo());
            ruta.setResultadoGestion(Ruta.ResultadoGestion.NO_EFECTIVA);
            ruta.setMostrarAlerta(0);
            ruta.save();
            Log.d(TAG, "ESTADO DESCARGA RUTA: " + ruta.getEstadoDescarga());
        }
    }

    private void updateBackgroundSelectListaMotivos(int position) {
        for (int i = 0; i < motivoItems.size(); i++) {
            motivoItems.get(i).setSelected(false);
        }
        motivoItems.get(position).setSelected(true);

        view.notifyMotivosAllItemChanged();
    }

    /**
     * Receiver
     * <p>
     * {@link DetalleRutaPresenter#descargaFinalizadaReceiver}
     * {@link RutaPendientePresenter#descargaFinalizadaReceiver}
     * {@link RutaGestionadaPresenter#descargaFinalizadaReceiver}
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
            view.showProgressDialog(R.string.text_gestionando_guia);
        }

        @Override
        public String doInBackground(String... strings) {
            saveGestionGE();
            checkUploadDataSyncImages();
            if (ModelUtils.isGuiaEntrega(rutas.get(0).getTipo())) {
                // Actualizamos al proceso ENTREGAR, para que en la lista
                // de gestionados se vuelva a gestionar.
                updateEstadoGestionGE(DescargaRuta.Entrega.ENTREGAR);
            } else {
                updateEstadoGestionGE(DescargaRuta.Recoleccion.FINALIZADO);
            }
            return null;
        }

        @Override
        public void onPostExecute(String s) {
            super.onPostExecute(s);
            view.dismiss();
            sendOnDescargaFinalizadaReceiver();
        }
    }

    private class VerifyExistImagesOnDeviceTask extends AsyncTaskCoroutine<Void, Boolean> {

        @Override
        public Boolean doInBackground(Void... params) {
            List<Imagen> images = selectAllImages();

            for (Imagen imagen : images) {
                if (!FileUtils.existFile(imagen.getPath() + imagen.getName())) {
                    imagen.delete();
                }
            }

            return true;
        }

        @Override
        public void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            showGaleria();
        }
    }

    private class ProcessImageFromStorageTask extends AsyncTaskCoroutine<Intent, Boolean> {

        private String msgError = "Lo sentimos, ocurrió un error al seleccionar la imagen.";

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            view.showProgressDialog(R.string.text_cargando_imagen);
        }

        @Override
        public Boolean doInBackground(Intent... intents) {
            //Log.d(TAG, "TIME 1: " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS").format(new Date(System.currentTimeMillis())));
            //return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
            String dateTimeMetaData = getDateTimeMetaDataFromUri(intents[0].getData());

            if (dateTimeMetaData != null) {
                LocalDateTime localDateTime = LocalDateTime.from(
                        DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss").parse(dateTimeMetaData));

                if (!isValidDateTimeMetaData(localDateTime)) {
                    msgError = "Lo sentimos, la información de la imagen seleccionada esta desactualizada.";
                    return false;
                }
            } else {
                String pathImageSelected = FileUtilss.getRealPath(view.getViewContext(),
                        intents[0].getData());
                LocalDateTime dateTime = null;
                File file = new File(pathImageSelected);

                if (Build.VERSION.SDK_INT < 26) {
                    dateTime = LocalDateTime.ofInstant(
                            Instant.ofEpochMilli(file.lastModified()), ZoneId.systemDefault());
                } else {
                    try {
                        BasicFileAttributes attr = Files.readAttributes(file.toPath(),
                                BasicFileAttributes.class);
                        dateTime = LocalDateTime.ofInstant(
                                attr.creationTime().toInstant(), ZoneId.systemDefault());
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }

                if (dateTime != null) {
                    if (!isValidDateTimeMetaData(dateTime)) {
                        msgError = "Lo sentimos, los metadatos de la imagen seleccionada son incorrectos.";
                        return false;
                    }
                }
            }

            String imageName = CameraUtils.generateImageName(typeCameraCaptureImage);
            photoCapture = FileUtils.generateFile(view.getViewContext(), imageName, dirPathPhotos);

            try {
                InputStream is = view.getViewContext().getContentResolver().openInputStream(
                        intents[0].getData());
                Bitmap bmp = BitmapFactory.decodeStream(is);
                is.close();

                ByteArrayOutputStream stream = new ByteArrayOutputStream();

                bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] byteArray = stream.toByteArray(); // convert camera photo to byte array

                // save it in your external storage.
                FileOutputStream fos = new FileOutputStream(photoCapture);

                fos.write(byteArray);
                fos.flush();
                fos.close();

                if (compressImage()) {
                    saveImage(photoCapture.getName(), photoCapture.getParent() + File.separator, typeCameraCaptureImage.toLowerCase());
                    //Log.d(TAG, "TIME 2: " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS").format(new Date(System.currentTimeMillis())));
                    return true;
                } else {
                    Log.d(TAG, "ERROR AL COMPRIMIR LA IMAGEN");
                }
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
                Log.d(TAG, "ERROR IMAGEN NO ENCONTRADO");
            } catch (OutOfMemoryError ex) {
                ex.printStackTrace();
                Log.d(TAG, "POCA MEMORIA AL GENERAR IMAGEN");
            } catch (IOException ex) {
                ex.printStackTrace();
                Log.d(TAG, "ERROR GENERANDO IMAGEN");
            }
            return false;
        }

        private boolean isValidDateTimeMetaData(LocalDateTime dateTime) {
            LocalDateTime current = LocalDateTime.now();

            return current.getDayOfMonth() == dateTime.getDayOfMonth() &&
                    current.getMonthValue() == dateTime.getMonthValue() &&
                    current.getYear() == dateTime.getYear();
        }

        private String getDateTimeMetaDataFromUri(Uri uri) {
            try {
                InputStream inputStream = view.getViewContext().getContentResolver().openInputStream(uri);
                try {
                    Metadata metadata = ImageMetadataReader.readMetadata(inputStream);

                    for (Directory directory : metadata.getDirectories()) {
                        for (Tag tag : directory.getTags()) {
                            if (tag.getTagName().equals("Date/Time")) {
                                return tag.getDescription();
                            }
                        }
                    }
                } catch (ImageProcessingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public void onPostExecute(Boolean status) {
            super.onPostExecute(status);
            //Log.d(TAG, "TIME 3: " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS").format(new Date(System.currentTimeMillis())));
            view.dismissProgressDialog();

            if (status) {
                insertImageToGallery();
            } else {
                view.showToast(msgError);
            }
        }
    }

}