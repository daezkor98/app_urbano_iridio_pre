package com.urbanoexpress.iridio.presenter;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.orm.util.NamingHelper;
import com.urbanoexpress.iridio.AsyncTaskCoroutine;
import com.urbanoexpress.iridio.R;
import com.urbanoexpress.iridio.model.entity.Data;
import com.urbanoexpress.iridio.model.entity.Imagen;
import com.urbanoexpress.iridio.model.entity.Ruta;
import com.urbanoexpress.iridio.model.interactor.RutaPendienteInteractor;
import com.urbanoexpress.iridio.model.util.ModelUtils;
import com.urbanoexpress.iridio.ui.helpers.ModalHelper;
import com.urbanoexpress.iridio.ui.interfaces.OnClickItemGaleriaListener;
import com.urbanoexpress.iridio.ui.model.GaleriaDescargaRutaItem;
import com.urbanoexpress.iridio.util.CameraUtils;
import com.urbanoexpress.iridio.util.CustomSiliCompressor;
import com.urbanoexpress.iridio.util.FileUtils;
import com.urbanoexpress.iridio.util.FileUtilss;
import com.urbanoexpress.iridio.util.LocationUtils;
import com.urbanoexpress.iridio.util.Preferences;
import com.urbanoexpress.iridio.view.BaseModalsView;
import com.urbanoexpress.iridio.view.GaleriaDescargaView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by mick on 08/08/16.
 */
public class GaleriaDescargaPresenter extends BaseModalsView implements OnClickItemGaleriaListener {

    private static final String TAG = GaleriaDescargaPresenter.class.getSimpleName();

    private GaleriaDescargaView galeriaDescargaView;
    private RutaPendienteInteractor interactor;

    private Ruta ruta;

    private List<Imagen> dbImagenes;

    private List<GaleriaDescargaRutaItem> galeria = new ArrayList<>();

    private File photoCapture;

    private final int MAX_PHOTO_CAPTURE = 10;
    private final int MAX_SIGNING_CAPTURE = 1;

    public static final int REQUEST_IMAGE_GALLERY = 200;

    private int contadorImagenes = 0, contadorFirma = 0;

    private Activity activity;

    private String directoryPhotos = "";

    private String typeCameraCaptureImage;

    private int selectedIndexTypeImageResultImageFromStorage = 0;

    public GaleriaDescargaPresenter(GaleriaDescargaView galeriaDescargaView, Ruta ruta) {
        this.galeriaDescargaView = galeriaDescargaView;
        this.ruta = ruta;
        this.activity = (Activity) galeriaDescargaView.getContextView();
        interactor = new RutaPendienteInteractor(galeriaDescargaView.getContextView());
        init();
    }

    private void init() {
        loadGalery();

        directoryPhotos = ModelUtils.isGuiaEntrega(ruta.getTipo()) ? "Descargas/" : "Recoleccion/";
        directoryPhotos += ruta.getLineaNegocio() + "-" + ruta.getIdGuia() + "/";
    }

    @Override
    public void onClickCamera() {
        if (canTakePhoto()) {
            typeCameraCaptureImage = "Imagen";

            photoCapture = CameraUtils.openCamera(
                    galeriaDescargaView.getFragment(), directoryPhotos, "");
        } else {
            showAlertDialog(galeriaDescargaView.getContextView(),
                    R.string.text_advertencia,
                    R.string.activity_detalle_ruta_message_no_puede_tomar_foto,
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
    public void onClickFirma() {

    }

    @Override
    public void onClickImage() {

    }

    @Override
    public void onClickAddPhotoFromGalery() {
        if (canTakePhoto()) {
            typeCameraCaptureImage = "Imagen";

            Fragment fragment = galeriaDescargaView.getFragment();

            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/jpeg");
    //            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoCapture));
                fragment.startActivityForResult(intent, REQUEST_IMAGE_GALLERY);
            } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("image/jpeg");
    //            Intent intent = new Intent(Intent.ACTION_PICK,
    //                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    //            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoCapture));
                fragment.startActivityForResult(intent, REQUEST_IMAGE_GALLERY);
            }
        } else {
            showAlertDialog(galeriaDescargaView.getContextView(),
                    R.string.text_advertencia,
                    R.string.activity_detalle_ruta_message_no_puede_tomar_foto,
                    R.string.text_aceptar, null);
        }
    }

    @Override
    public void onClickDeleteImage(int position) {
        Log.d(TAG, "Position click: " + position);
        // Restar 2 posiciones que corresponden a los botones
        final int index = position - 2;

        dbImagenes = selectAllImages();

        showAlertDialog(galeriaDescargaView.getContextView(),
                R.string.activity_detalle_ruta_title_eliminar_galeria,
                R.string.activity_detalle_ruta_message_eliminar_galeria,
                R.string.text_eliminar,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteImageGalery(index);
                    }
                },
                R.string.text_cancelar, null);
    }

    private void loadGalery() {
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

                if (imagen.getAnotaciones().equals("imagen")) {
                    contadorImagenes++;
                } else if (imagen.getAnotaciones().equals("firma")) {
                    contadorFirma++;
                }
            }
        } else {
            Log.d(TAG, "No hay galeria");
        }

        galeriaDescargaView.showGaleria(galeria);
    }

    private ArrayList<Imagen> selectAllImages() {
        ArrayList<Imagen> imgs = new ArrayList<>();
        List<Imagen> imagenes = Imagen.find(Imagen.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("clasificacion") + " = ?",
                Preferences.getInstance().getString("idUsuario", ""),
                Imagen.Tipo.GESTION_GUIA + "");

        for (int i = 0; i < imagenes.size(); i++) {
            if (imagenes.get(i).getIdServiciosAdjuntos().contains(ruta.getIdServicio())) {
                imgs.add(imagenes.get(i));
            }
        }
        return imgs;
    }

    private boolean canTakePhoto() {
        return (contadorImagenes + 1) <= MAX_PHOTO_CAPTURE;
    }

    public void onActivityResultImageFromCamera() {
        if (compressImage()) {
            insertPhotoToGalery();
            saveImage(photoCapture.getName(), photoCapture.getParent() + File.separator, typeCameraCaptureImage.toLowerCase());
        } else {
            if (galeriaDescargaView.getContextView() != null) {
                showToast(galeriaDescargaView.getContextView(),
                        "Lo sentimos, ocurrió un error al tomar la foto.", Toast.LENGTH_LONG);
            }
        }
    }

    public void onActivityResultImageFromStorage(final Intent data) {
        selectedIndexTypeImageResultImageFromStorage = 0;

        String[] nombreTipoFotos = new String[]{"Foto Producto", "Foto " +
                ModelUtils.getNameLblCargoGuia(galeriaDescargaView.getContextView()), "Foto Domicilio"};

        ModalHelper.getBuilderAlertDialog(galeriaDescargaView.getContextView())
                .setTitle("Seleccione el tipo de imagen que esta subiendo")
                .setSingleChoiceItems(nombreTipoFotos, 0,
                        (dialog, which) -> selectedIndexTypeImageResultImageFromStorage = which)
                .setPositiveButton(R.string.text_aceptar,
                        (dialog, which) -> new ProcessImageFromStorageTask().execute(data))
                .setNegativeButton(R.string.text_cancelar, null)
                .show();
    }

    private boolean compressImage() {
        String pathImage = photoCapture.getPath();
        Log.d(TAG, "PATHIMAGE SILICOMPRESSOR: " + pathImage);

        String compressFilePath = "";

        try {
            if (typeCameraCaptureImage.equalsIgnoreCase("Cargo")) {
                compressFilePath = CustomSiliCompressor.with(galeriaDescargaView.getContextView())
                        .compress(pathImage, 1280.0f, 720.0f, 85);
            } else {
                compressFilePath = CustomSiliCompressor.with(galeriaDescargaView.getContextView()).compress(pathImage);
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

    private void insertPhotoToGalery() {
        Log.d(TAG, "Image Path: " + photoCapture.getAbsolutePath());
        GaleriaDescargaRutaItem item =
                new GaleriaDescargaRutaItem(photoCapture.getAbsolutePath());
        galeria.add(item);
        galeriaDescargaView.notifyGaleryAllItemChanged();
    }

    private void saveImage(final String fileName, final String directory, String anotaciones) {
        Imagen imagen = new Imagen(
                Preferences.getInstance().getString("idUsuario", ""),
                fileName,
                directory,
                Imagen.Tipo.GESTION_GUIA,
                ruta.getIdServicio(),
                CameraUtils.getDateLastCapturePhoto().getTime() + "",
                LocationUtils.getLatitude() + "",
                LocationUtils.getLongitude() + "",
                anotaciones,
                ruta.getIdServicio(),
                ruta.getLineaNegocio(),
                ruta.getDataValidate(),
                Data.Sync.PENDING);
        imagen.save();

        if (anotaciones.equals("imagen")) {
            contadorImagenes++;
        } else {
            contadorFirma++;
        }

        Log.d(TAG, "save image");
        Log.d(TAG, "file name: " + fileName);
        Log.d(TAG, "file directory: " + directory);
    }

    private boolean isPhoto(int position) {
        return dbImagenes.get(position).getName().contains("Imagen");
    }

    private void deleteImageGalery(int position) {
        FileUtils.deleteFile(dbImagenes.get(position).getPath() + dbImagenes.get(position).getName());

        dbImagenes.get(position).delete();

        if (isPhoto(position)) contadorImagenes--;
        else contadorFirma--;

        dbImagenes.remove(position);

        galeria.remove(position);

        galeriaDescargaView.notifyGaleryItemRemove(position + 2); // Add 2 for header
//        descargaEntregaView.notifyGaleryAllItemChanged();
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

    private class ProcessImageFromStorageTask extends AsyncTaskCoroutine<Intent, Boolean> {

        private String msgError = "Lo sentimos, ocurrió un error al seleccionar la imagen.";

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            BaseModalsView.showProgressDialog(activity, R.string.text_cargando_imagen);
        }

        @Override
        public Boolean doInBackground(Intent... intents) {
            //Log.d(TAG, "TIME 1: " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS").format(new Date(System.currentTimeMillis())));
            //return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
            String dateTimeMetaData = getDateTimeMetaDataFromUri(intents[0].getData());

            if (dateTimeMetaData != null) {
                try {
                    Date dateImage = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss").parse(dateTimeMetaData);
                    boolean isTodayDate = isTodayDate(dateImage);

                    if (!isTodayDate) {
                        msgError = "Lo sentimos, la información de la imagen seleccionada esta desactualizada.";
                        return false;
                    }
                } catch (ParseException ex) {
                    ex.printStackTrace();
                }
            } else {
                String pathImageSelected = FileUtilss.getRealPath(galeriaDescargaView.getContextView(), intents[0].getData());
                Date dateCreated = null;
                File file = new File(pathImageSelected);

                if (Build.VERSION.SDK_INT < 26) {
                    dateCreated = new Date(file.lastModified());
                } else {
                    try {
                        BasicFileAttributes attr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
                        dateCreated = new Date(attr.creationTime().toMillis());
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }

                try {
                    boolean isTodayDate = isTodayDate(dateCreated);

                    if (!isTodayDate) {
                        msgError = "Lo sentimos, los metadatos de la imagen seleccionada son incorrectos.";
                        return false;
                    }
                } catch (NullPointerException ex) {
                    ex.printStackTrace();
                }
            }

            if (selectedIndexTypeImageResultImageFromStorage == 0) {
                typeCameraCaptureImage = "Imagen";
            } else if (selectedIndexTypeImageResultImageFromStorage == 1) {
                typeCameraCaptureImage = "Cargo";
            } else if (selectedIndexTypeImageResultImageFromStorage == 2) {
                typeCameraCaptureImage = "Domicilio";
            }

            String imageName = CameraUtils.generateImageName(typeCameraCaptureImage);
            photoCapture = FileUtils.generateFile(galeriaDescargaView.getContextView(), imageName, directoryPhotos);

            try {
                InputStream is = activity.getContentResolver().openInputStream(intents[0].getData());
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
            } catch (NullPointerException ex) {
                ex.printStackTrace();
            }
            return false;
        }

        private boolean isTodayDate(Date date) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            Calendar calendarCurrent = Calendar.getInstance();

            return calendarCurrent.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH) &&
                    calendarCurrent.get(Calendar.MONTH) == calendar.get(Calendar.MONTH) &&
                    calendarCurrent.get(Calendar.YEAR) == calendar.get(Calendar.YEAR);
        }

        private String getDateTimeMetaDataFromUri(Uri uri) {
            try {
                InputStream inputStream = galeriaDescargaView.getContextView().getContentResolver().openInputStream(uri);
                try {
                    uri.getEncodedUserInfo();
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
            BaseModalsView.hideProgressDialog();

            if (status) {
                insertPhotoToGalery();
            } else {
                if (galeriaDescargaView.getContextView() != null) {
                    showToast(galeriaDescargaView.getContextView(), msgError, Toast.LENGTH_LONG);
                }
            }
        }
    }

}
