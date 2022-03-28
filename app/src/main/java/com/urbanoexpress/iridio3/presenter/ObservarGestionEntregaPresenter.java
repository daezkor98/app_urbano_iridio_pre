package com.urbanoexpress.iridio3.presenter;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.VolleyError;
import com.google.android.material.snackbar.Snackbar;
import com.orm.util.NamingHelper;
import com.urbanoexpress.iridio3.AsyncTaskCoroutine;
import com.urbanoexpress.iridio3.R;
import com.urbanoexpress.iridio3.model.entity.Data;
import com.urbanoexpress.iridio3.model.entity.Imagen;
import com.urbanoexpress.iridio3.model.entity.MotivoDescarga;
import com.urbanoexpress.iridio3.model.entity.Ruta;
import com.urbanoexpress.iridio3.model.interactor.RutaPendienteInteractor;
import com.urbanoexpress.iridio3.model.interactor.callback.RequestCallback;
import com.urbanoexpress.iridio3.ui.interfaces.OnClickItemGaleriaListener;
import com.urbanoexpress.iridio3.ui.model.GaleriaDescargaRutaItem;
import com.urbanoexpress.iridio3.ui.model.MotivoDescargaItem;
import com.urbanoexpress.iridio3.util.CameraUtils;
import com.urbanoexpress.iridio3.util.CommonUtils;
import com.urbanoexpress.iridio3.util.CustomSiliCompressor;
import com.urbanoexpress.iridio3.util.FileUtils;
import com.urbanoexpress.iridio3.util.LocationUtils;
import com.urbanoexpress.iridio3.util.Preferences;
import com.urbanoexpress.iridio3.util.constant.LocalAction;
import com.urbanoexpress.iridio3.view.BaseModalsView;
import com.urbanoexpress.iridio3.view.ObservarGestionEntregaView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ObservarGestionEntregaPresenter implements OnClickItemGaleriaListener {

    private static final String TAG = ObservarGestionEntregaPresenter.class.getSimpleName();

    private ObservarGestionEntregaView view;
    private RutaPendienteInteractor rutaPendienteInteractor;

    private List<MotivoDescarga> dbMotivoDescargas = Collections.emptyList();
    private List<MotivoDescargaItem> motivoItems = new ArrayList<>();

    private List<Imagen> dbImagenes;

    private List<GaleriaDescargaRutaItem> galeria = new ArrayList<>();

    private Ruta guia;

    private File photoCapture;

    private final int MAX_PHOTO_CAPTURE = 10;

    private int contadorImagenes = 0;

    private int selectedIndexMotivo = -1;

    private int selectedIndexGalery = -1;

    private String typeCameraCaptureImage;

    public ObservarGestionEntregaPresenter(ObservarGestionEntregaView view, Ruta guia) {
        this.view = view;
        this.guia = guia;
        rutaPendienteInteractor = new RutaPendienteInteractor(view.getContextView());
        init();
    }

    private void init() {
        showTextGuiasElectronicas();
        loadMotivos();
        loadGaleria();

//        if (Connection.hasNetworkConnectivity(view.getContextView())) {
//            getListaMotivo();
//        }
    }

    public void onClickUpdateMotivos() {
        if (CommonUtils.validateConnectivity(view.getContextView())) {
            getListaMotivo();
        }
    }

    @Override
    public void onClickCamera() {
        if (canTakePhoto()) {
            typeCameraCaptureImage = "Observacion_Entrega";
            photoCapture = CameraUtils.openCamera(view.getFragment(),
                    "Descargas/" + guia.getLineaNegocio() + "-" + guia.getIdGuia() + "/",
                    "Observacion_Entrega");
        } else {
            BaseModalsView.showAlertDialog(view.getContextView(),
                    R.string.text_advertencia,
                    R.string.activity_detalle_ruta_message_no_puede_tomar_foto,
                    R.string.text_aceptar, null);
        }
    }

    @Override
    public void onClickFirma() { }

    @Override
    public void onClickCargo() { }

    @Override
    public void onClickVoucher() { }

    @Override
    public void onClickImage() { }

    @Override
    public void onClickAddPhotoFromGalery() { }

    @Override
    public void onClickDeleteImage(int position) {
        Log.d(TAG, "Position click: " + position);
        selectedIndexGalery = position - 1; // Subtract 1 for header

        dbImagenes = selectAllImages();

        int resIdMessage;

        if (dbImagenes.get(selectedIndexGalery).getName().contains("Imagen") ||
                dbImagenes.get(selectedIndexGalery).getName().contains("Observacion_Entrega")) {
            resIdMessage = R.string.activity_detalle_ruta_message_eliminar_galeria;
        } else if (dbImagenes.get(selectedIndexGalery).getName().contains("Cargo")) {
            resIdMessage = R.string.activity_detalle_ruta_message_eliminar_cargo;
        } else if (dbImagenes.get(selectedIndexGalery).getName().contains("Voucher")) {
            resIdMessage = R.string.activity_detalle_ruta_message_eliminar_voucher;
        } else {
            resIdMessage = R.string.activity_detalle_ruta_message_eliminar_firma;
        }

        BaseModalsView.showAlertDialog(view.getContextView(),
                R.string.activity_detalle_ruta_title_eliminar_galeria,
                resIdMessage,
                R.string.text_aceptar,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteImageGalery(selectedIndexGalery);
                    }
                },
                R.string.text_cancelar, null);
    }

    public void onActivityResultImage() {
        if (compressImage()) {
            insertPhotoToGalery();
            saveImage(photoCapture.getName(), photoCapture.getParent() + File.separator, typeCameraCaptureImage.toLowerCase());
        } else {
            if (view.getContextView() != null) {
                BaseModalsView.showToast(view.getContextView(),
                        "Lo sentimos, ocurrió un error al tomar la foto.", Toast.LENGTH_LONG);
            }
        }
    }

    public void onClickItemMotivo(int position) {
        updateBackgroundSelectListaMotivos(position);
        selectedIndexMotivo = position;
    }

    public void onClickAceptar() {
        if (validateDescarga()) {
            new SaveGestionTask().execute();
        }
    }

    class SaveGestionTask extends AsyncTaskCoroutine<String, String> {
        @Override
        public void onPreExecute() {
            super.onPreExecute();
            BaseModalsView.showProgressDialog(view.getContextView(), R.string.text_agregando_observacion_gestion_entrega);
        }

        @Override
        public String doInBackground(String... strings) {
            checkUploadDataSyncImages();
            return null;
        }

        @Override
        public void onPostExecute(String s) {
            super.onPostExecute(s);
            sendDataObservacionGestionEntregaReceiver();
            BaseModalsView.hideProgressDialog();
            closeDialog();
        }
    }

    private void getListaMotivo() {
        BaseModalsView.showProgressDialog(view.getContextView(),
                R.string.text_actualizando_motivos);
        final RequestCallback callback = new RequestCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if (response.getBoolean("success")) {
                        rutaPendienteInteractor.deleteMotivos(MotivoDescarga.Tipo.OBSERVACION_ENTREGA);
                        saveMotivos(response.getJSONArray("data"));
                        BaseModalsView.hideProgressDialog();
                        loadMotivos();
                    } else {
                        BaseModalsView.hideProgressDialog();
                        BaseModalsView.showToast(view.getContextView(),
                                response.getString("msg_error"), Toast.LENGTH_LONG);
                    }
                } catch (JSONException ex) {
                    ex.printStackTrace();
                    BaseModalsView.hideProgressDialog();
                    BaseModalsView.showSnackBar(view.baseFindViewById(R.id.lvMotivos),
                            R.string.json_object_exception, Snackbar.LENGTH_LONG,
                            R.string.text_volver_a_intentar,
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    onClickUpdateMotivos();
                                }
                            });
                }
            }

            @Override
            public void onError(VolleyError error) {
                error.printStackTrace();
                BaseModalsView.hideProgressDialog();
                BaseModalsView.showSnackBar(view.baseFindViewById(R.id.lvMotivos),
                        R.string.volley_error_message, Snackbar.LENGTH_LONG,
                        R.string.text_volver_a_intentar,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onClickUpdateMotivos();
                            }
                        });
            }
        };

        String params[] = new String[]{
                MotivoDescarga.Tipo.OBSERVACION_ENTREGA + "",
                Preferences.getInstance().getString("idUsuario", "")
        };

        rutaPendienteInteractor.getMotivos(params, callback);
    }

    private void saveImage(final String fileName, final String directory, String anotaciones) {
        Imagen imagen = new Imagen(
                Preferences.getInstance().getString("idUsuario", ""),
                fileName,
                directory,
                Imagen.Tipo.GESTION_GUIA,
                guia.getIdServicio(),
                CameraUtils.getDateLastCapturePhoto().getTime() + "",
                LocationUtils.getLatitude() + "",
                LocationUtils.getLongitude() + "",
                anotaciones,
                guia.getIdServicio(),
                guia.getLineaNegocio(),
                guia.getDataValidate(),
                Data.Sync.MANUAL);
        imagen.save();

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
            String compressFilePath = CustomSiliCompressor.with(view.getContextView())
                    .compress(pathImage, 1280.0f, 720.0f, 85);

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

    private void saveMotivos(JSONArray data) throws JSONException{
        JSONObject jsonObject;
        for (int i = 0; i < data.length(); i++) {
            jsonObject = data.getJSONObject(i);
            MotivoDescarga motivo = new MotivoDescarga(
                    Preferences.getInstance().getString("idUsuario", ""),
                    MotivoDescarga.Tipo.OBSERVACION_ENTREGA,
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
                MotivoDescarga.Tipo.OBSERVACION_ENTREGA, guia.getLineaNegocio());

        selectedIndexMotivo = -1;

        motivoItems = new ArrayList<>();
        MotivoDescargaItem item;

        for (MotivoDescarga motivo : dbMotivoDescargas) {
            item = new MotivoDescargaItem(motivo.getDescripcion(), false);
            motivoItems.add(item);
        }

        view.showListaMotivos(motivoItems);
    }

    private void showTextGuiasElectronicas() {
        view.setGuiaElectronica(guia.getGuia());
    }

    private void deleteImageGalery(int position) {
        FileUtils.deleteFile(dbImagenes.get(position).getPath() + dbImagenes.get(position).getName());

        contadorImagenes--;

        dbImagenes.get(position).delete();

        dbImagenes.remove(position);

        galeria.remove(position);

        view.notifyGaleryItemRemove(position + 1);
//        view.notifyGaleryAllItemChanged();
    }

    private void checkUploadDataSyncImages() {
        Log.d(TAG, "checkUploadDataSyncImages");
        dbImagenes = Imagen.find(Imagen.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("idSuperior") + " = ? and " +
                        NamingHelper.toSQLNameDefault("dataSync") + " = ? and " +
                        NamingHelper.toSQLNameDefault("anotaciones") + " = ? and " +
                        NamingHelper.toSQLNameDefault("clasificacion") + " = ?",
                Preferences.getInstance().getString("idUsuario", ""),
                guia.getIdServicio(),
                Data.Sync.MANUAL + "",
                "observacion_entrega",
                Imagen.Tipo.GESTION_GUIA + "");
        Log.d(TAG, "TOTAL IMAGENS: " + dbImagenes.size());

        for (int i = 0; i < dbImagenes.size(); i++) {
            Log.d(TAG, "IMAGEN: " + dbImagenes.get(i).getIdSuperior());
            dbImagenes.get(i).setDataSync(Data.Sync.PENDING);
            dbImagenes.get(i).save();
        }
    }

    private boolean validateDescarga() {
        if (validateSelectedMotivo()) {
            if (validateDatosObservacionEntrega()) {
                if (validateFoto()) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean validateSelectedMotivo() {
        Log.d(TAG, "selectedIndexMotivo: " + selectedIndexMotivo);
        if (selectedIndexMotivo < 0) {
            BaseModalsView.showSnackBar(view.getViewTxtComentarios(),
                    "Debe seleccionar un motivo para realizar la observación de la entrega.",
                    Snackbar.LENGTH_LONG);
        }
        return selectedIndexMotivo >= 0;
    }

    private boolean validateDatosObservacionEntrega() {
        if (view.getViewTxtComentarios().getText().toString().isEmpty()) {
            view.getViewTxtComentarios().setError("Debe ingresar un comentario detallando el motivo de la observación de la entrega.");
            view.getViewTxtComentarios().requestFocus();
            return false;
        }

        return true;
    }

    private boolean validateFoto() {
        if (contadorImagenes == 0) {
            BaseModalsView.showSnackBar(view.getViewTxtComentarios(),
                    "Debe tomar una foto como mínimo para realizar la observación de la entrega.",
                    Snackbar.LENGTH_LONG);
        }
        return contadorImagenes > 0;
    }

    private boolean canTakePhoto() {
        return (contadorImagenes + 1) <= MAX_PHOTO_CAPTURE;
    }

    private void closeDialog() {
        ((DialogFragment) view.getFragment()).dismiss();
    }

    private List<Imagen> selectAllImages() {
        return Imagen.find(Imagen.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("idSuperior") + " = ? and " +
                        NamingHelper.toSQLNameDefault("anotaciones") + " = ? and " +
                        NamingHelper.toSQLNameDefault("clasificacion") + " = ?",
                Preferences.getInstance().getString("idUsuario", ""),
                guia.getIdServicio(),
                "observacion_entrega",
                Imagen.Tipo.GESTION_GUIA + "");
    }

    private void updateBackgroundSelectListaMotivos(int position) {
        for (int i = 0; i < motivoItems.size(); i++) {
            motivoItems.get(i).setSelected(false);
        }
        motivoItems.get(position).setSelected(true);

        view.notifyMotivosAllItemChanged();
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

    /**
     * Receiver
     *
     * {@link EntregaGEPresenter#dataObservacionGestionEntregaReceiver}
     */
    private void sendDataObservacionGestionEntregaReceiver() {
        Intent intent = new Intent(LocalAction.DATA_OBSERVACION_GESTION_ENTREGA);
        Bundle bundle = new Bundle();
        bundle.putString("idMotivoObservacionEntrega", dbMotivoDescargas.get(selectedIndexMotivo).getIdMotivo());
        bundle.putString("comentarioObservacionEntrega", view.getViewTxtComentarios().getText().toString().trim());
        intent.putExtra("args", bundle);
        LocalBroadcastManager.getInstance(view.getContextView()).sendBroadcast(intent);
    }

}