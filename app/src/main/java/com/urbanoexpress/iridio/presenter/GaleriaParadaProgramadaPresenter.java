package com.urbanoexpress.iridio.presenter;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import com.orm.util.NamingHelper;
import com.urbanoexpress.iridio.R;
import com.urbanoexpress.iridio.model.entity.Data;
import com.urbanoexpress.iridio.model.entity.Imagen;
import com.urbanoexpress.iridio.model.entity.ParadaProgramada;
import com.urbanoexpress.iridio.model.interactor.RutaPendienteInteractor;
import com.urbanoexpress.iridio.ui.helpers.ModalHelper;
import com.urbanoexpress.iridio.ui.interfaces.OnClickItemGaleriaListener;
import com.urbanoexpress.iridio.ui.model.GaleriaDescargaRutaItem;
import com.urbanoexpress.iridio.util.CameraUtils;
import com.urbanoexpress.iridio.util.CustomSiliCompressor;
import com.urbanoexpress.iridio.util.FileUtils;
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
import java.util.ArrayList;
import java.util.List;

public class GaleriaParadaProgramadaPresenter extends BaseModalsView implements OnClickItemGaleriaListener {

    private static final String TAG = GaleriaParadaProgramadaPresenter.class.getSimpleName();

    private GaleriaDescargaView galeriaDescargaView;
    private RutaPendienteInteractor interactor;

    private String idPlanDeViaje;

    private String idParadaProgramada;

    private int paradaProgramadaEstadoLlegada;

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

    public GaleriaParadaProgramadaPresenter(GaleriaDescargaView galeriaDescargaView, String idPlanDeViaje,
                                            String idParadaProgramada, int paradaProgramadaEstadoLlegada) {
        this.galeriaDescargaView = galeriaDescargaView;
        this.idPlanDeViaje = idPlanDeViaje;
        this.idParadaProgramada = idParadaProgramada;
        this.paradaProgramadaEstadoLlegada = paradaProgramadaEstadoLlegada;
        this.activity = (Activity) galeriaDescargaView.getContextView();
        interactor = new RutaPendienteInteractor(galeriaDescargaView.getContextView());
        init();
    }

    private void init() {
        loadGalery();

        directoryPhotos = "ParadaProgramada/" + idPlanDeViaje + "-" +idParadaProgramada + "/";
    }

    @Override
    public void onClickCamera() {
        if (canTakePhoto()) {
            typeCameraCaptureImage = "Foto";

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
            typeCameraCaptureImage = "Foto";

            Fragment fragment = galeriaDescargaView.getFragment();

            String imageName = CameraUtils.generateImageName("");
            photoCapture = FileUtils.generateFile(galeriaDescargaView.getContextView(),
                    imageName, directoryPhotos);

            Log.d(TAG, "NEW IMAGE");
            Log.d(TAG, "PATH: " + photoCapture.getAbsolutePath());

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
        // Restar 1 posiciones que corresponden a los botones
        final int index = position - 1;

        if (paradaProgramadaEstadoLlegada != ParadaProgramada.Status.SALIO_AGENCIA) {
            dbImagenes = selectAllImages();

            if (dbImagenes.size() > 1) {
                int resIdMessage;

                if (dbImagenes.get(index).getName().contains("Imagen")) {
                    resIdMessage = R.string.activity_detalle_ruta_message_eliminar_galeria;
                } else if (dbImagenes.get(index).getName().contains("Cargo")) {
                    resIdMessage = R.string.activity_detalle_ruta_message_eliminar_cargo;
                } else {
                    resIdMessage = R.string.activity_detalle_ruta_message_eliminar_firma;
                }

                showAlertDialog(galeriaDescargaView.getContextView(),
                        R.string.activity_detalle_ruta_title_eliminar_galeria,
                        resIdMessage,
                        R.string.text_eliminar,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteImageGalery(index);
                            }
                        },
                        R.string.text_cancelar, null);
            } else {
                showToast(galeriaDescargaView.getContextView(),
                        "Lo sentimos, no puede eliminar todas las fotos (Mínimo una foto).", Toast.LENGTH_LONG);
            }
        } else {
            showToast(galeriaDescargaView.getContextView(),
                "Lo sentimos, no puede eliminar las fotos por que ya marcaste salida de la parada.", Toast.LENGTH_LONG);
        }
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
        }

        galeriaDescargaView.showGaleria(galeria);
    }

    private ArrayList<Imagen> selectAllImages() {
        List<Imagen> imagenes = Imagen.find(Imagen.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("clasificacion") + " = ? and " +
                        NamingHelper.toSQLNameDefault("idSuperior") + " = ?",
                Preferences.getInstance().getString("idUsuario", ""),
                Imagen.Tipo.PARADA_PROGRAMADA + "", idParadaProgramada);
        return (ArrayList<Imagen>) imagenes;
    }

    private boolean canTakePhoto() {
        return (contadorImagenes + 1) <= MAX_PHOTO_CAPTURE;
    }

    public void onActivityResultImageFromCamera() {
        if (compressImage()) {
            insertPhotoToGalery();
            saveImage(photoCapture.getName(), photoCapture.getParent() + File.separator, "imagen");
        } else {
            if (galeriaDescargaView.getContextView() != null) {
                showToast(galeriaDescargaView.getContextView(),
                        "Lo sentimos, ocurrió un error al tomar la foto.", Toast.LENGTH_LONG);
            }
        }
    }

    public void onActivityResultImageFromStorage(final Intent data) {
        ModalHelper.getBuilderAlertDialog(galeriaDescargaView.getContextView())
                .setTitle("Seleccione el tipo de imagen")
                .setSingleChoiceItems(new String[]{"Foto", "Cargo"}, 0,
                        (dialog, which) -> selectedIndexTypeImageResultImageFromStorage = which)
                .setPositiveButton(R.string.text_aceptar, (dialog, which) ->
                        new ProcessImageFromStorageTask().execute(data))
                .setNegativeButton(R.string.text_cancelar, null)
                .show();
    }

    private boolean compressImage() {
        String pathImage = photoCapture.getPath();
        Log.d(TAG, "PATHIMAGE SILICOMPRESSOR: " + pathImage);

        String compressFilePath = "";

        try {
            if (typeCameraCaptureImage.equalsIgnoreCase("Foto")) {
                compressFilePath = CustomSiliCompressor.with(galeriaDescargaView.getContextView()).compress(pathImage);
            } else if (typeCameraCaptureImage.equalsIgnoreCase("Cargo")) {
                compressFilePath = CustomSiliCompressor.with(galeriaDescargaView.getContextView())
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
                Imagen.Tipo.PARADA_PROGRAMADA,
                idParadaProgramada,
                CameraUtils.getDateLastCapturePhoto().getTime() + "",
                LocationUtils.getLatitude() + "",
                LocationUtils.getLongitude() + "",
                anotaciones,
                "",
                "3",
                Data.Validate.VALID,
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

        galeriaDescargaView.notifyGaleryItemRemove(position + 1); // Add 1 for header
//        descargaEntregaView.notifyGaleryAllItemChanged();
    }

    private class VerifyExistImagesOnDeviceTask extends AsyncTask<Void, Integer, Boolean> {

        private final String TAG = VerifyExistImagesOnDeviceTask.class.getSimpleName();

        @Override
        protected Boolean doInBackground(Void... params) {
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
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            Log.d(TAG, "onPostExecute");
            showGaleria();
        }

    }

    private class ProcessImageFromStorageTask extends AsyncTask<Intent, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            BaseModalsView.showProgressDialog(activity, R.string.text_cargando_imagen);
        }

        @Override
        protected Boolean doInBackground(Intent... intents) {
            //Log.d(TAG, "TIME 1: " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS").format(new Date(System.currentTimeMillis())));
            //return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
            if (selectedIndexTypeImageResultImageFromStorage == 1) {
                typeCameraCaptureImage = "Cargo";
            }

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
                    saveImage(photoCapture.getName(), photoCapture.getParent() + File.separator, "imagen");
                    //Log.d(TAG, "TIME 2: " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS").format(new Date(System.currentTimeMillis())));
                    return true;
                } else {
                    Log.d(TAG, "ERROR AL COMPRIMIR LA IMAGEN");
                }
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
                Log.d(TAG, "ERROR IMAGEN NO ENCONTRA");
            } catch (OutOfMemoryError ex) {
                ex.printStackTrace();
                Log.d(TAG, "POCA MEMORIA AL GENERAR IMAGEN");
            } catch (IOException ex) {
                ex.printStackTrace();
                Log.d(TAG, "ERROR GENERANDO IMAGEN");
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean status) {
            super.onPostExecute(status);
            //Log.d(TAG, "TIME 3: " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS").format(new Date(System.currentTimeMillis())));
            BaseModalsView.hideProgressDialog();

            if (status) {
                insertPhotoToGalery();
            } else {
                if (galeriaDescargaView.getContextView() != null) {
                    showToast(galeriaDescargaView.getContextView(),
                            "Lo sentimos, ocurrió un error al seleccionar la imagen.", Toast.LENGTH_LONG);
                }
            }
        }
    }

}