package com.urbanoexpress.iridio3.presenter;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.VolleyError;
import com.google.android.material.snackbar.Snackbar;
import com.orm.util.NamingHelper;
import com.urbanoexpress.iridio3.AsyncTaskCoroutine;
import com.urbanoexpress.iridio3.R;
import com.urbanoexpress.iridio3.model.entity.Data;
import com.urbanoexpress.iridio3.model.entity.DescargaRuta;
import com.urbanoexpress.iridio3.model.entity.GuiaGestionada;
import com.urbanoexpress.iridio3.model.entity.Imagen;
import com.urbanoexpress.iridio3.model.entity.MotivoDescarga;
import com.urbanoexpress.iridio3.model.entity.Ruta;
import com.urbanoexpress.iridio3.model.interactor.RutaPendienteInteractor;
import com.urbanoexpress.iridio3.model.interactor.callback.RequestCallback;
import com.urbanoexpress.iridio3.ui.helpers.ModalHelper;
import com.urbanoexpress.iridio3.ui.interfaces.OnClickItemGaleriaListener;
import com.urbanoexpress.iridio3.ui.model.GaleriaDescargaRutaItem;
import com.urbanoexpress.iridio3.ui.model.MotivoDescargaItem;
import com.urbanoexpress.iridio3.util.CameraUtils;
import com.urbanoexpress.iridio3.util.CommonUtils;
import com.urbanoexpress.iridio3.util.CustomSiliCompressor;
import com.urbanoexpress.iridio3.util.FileUtils;
import com.urbanoexpress.iridio3.util.LocationUtils;
import com.urbanoexpress.iridio3.util.Preferences;
import com.urbanoexpress.iridio3.view.BaseModalsView;
import com.urbanoexpress.iridio3.view.NoRecolectaView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by mick on 18/08/16.
 */

public class NoRecolectaGEPresenter extends BaseModalsView implements OnClickItemGaleriaListener {

    private static final String TAG = EntregaGEPresenter.class.getSimpleName();

    private NoRecolectaView noRecolectaView;
    private RutaPendienteInteractor rutaPendienteInteractor;

    private List<MotivoDescarga> dbMotivoDescargas = Collections.emptyList();
    private List<MotivoDescargaItem> motivoItems = new ArrayList<>();

    private List<Imagen> dbImagenes;

    private List<GaleriaDescargaRutaItem> galeria = new ArrayList<>();

    private ArrayList<DescargaRuta> descargaRutas = new ArrayList<>();
    private ArrayList<Ruta> rutas = new ArrayList<>();

    private File photoCapture;

    private final int MAX_PHOTO_CAPTURE = 10;

    private int contadorImagenes = 0;

    private int selectedIndexMotivo = -1;

    private int selectedIndexGalery = -1;

    private int numVecesGestionado = 0;

    public NoRecolectaGEPresenter(NoRecolectaView noRecolectaView,
                                  ArrayList<Ruta> rutas,
                                  int numVecesGestionado) {
        this.noRecolectaView = noRecolectaView;
        this.rutas = rutas;
        this.numVecesGestionado = numVecesGestionado;
        rutaPendienteInteractor = new RutaPendienteInteractor(noRecolectaView.getContextView());
        init();
    }

    private void init() {
        showTextGuiasElectronicas();
        loadDataRutas();
        loadMotivos();
        loadGaleria();

//        if (Connection.hasNetworkConnectivity(noRecolectaView.getContextView())) {
//            getListaMotivo();
//        }
    }

    public void onClickUpdateMotivos() {
        if (CommonUtils.validateConnectivity(noRecolectaView.getContextView())) {
            getListaMotivo();
        }
    }

    @Override
    public void onClickCamera() {
        if (canTakePhoto()) {
            photoCapture = CameraUtils.openCamera(noRecolectaView.getFragment(),
                    "Recoleccion/" + rutas.get(0).getLineaNegocio() + "-" +
                            rutas.get(0).getIdGuia() + "/", "");
        } else {
            showAlertDialog(noRecolectaView.getContextView(),
                    R.string.text_advertencia,
                    R.string.activity_detalle_ruta_message_no_puede_tomar_foto,
                    R.string.text_aceptar, null);
        }
    }

    @Override
    public void onClickFirma() {

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
        selectedIndexGalery = position - 1; // Subtract 1 for header

        dbImagenes = selectAllImages();

        showAlertDialog(noRecolectaView.getContextView(),
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
            if (noRecolectaView.getContextView() != null) {
                showToast(noRecolectaView.getContextView(),
                        "Lo sentimos, ocurrió un error al tomar la foto.", Toast.LENGTH_LONG);
            }
        }
    }

    public void onClickItemMotivo(int position) {
        updateBackgroundSelectListaMotivos(position);
        selectedIndexMotivo = position;
    }

    public void onClickAceptar() {
        if (validateEstadoShipper()) {
            if (validateDescarga()) {
                new SaveGestionTask().execute();
            }
        }
    }

    class SaveGestionTask extends AsyncTaskCoroutine<String, String> {
        @Override
        public void onPreExecute() {
            super.onPreExecute();
            showProgressDialog(noRecolectaView.getContextView(), R.string.text_gestionando_recoleccion);
        }

        @Override
        public String doInBackground(String... strings) {
            saveGestionGE();
            checkUploadDataSyncImages();
            updateEstadoGestionGE(DescargaRuta.Recoleccion.FINALIZADO);
            return null;
        }

        @Override
        public void onPostExecute(String s) {
            super.onPostExecute(s);
            sendOnDescargaFinalizadaReceiver();
            hideProgressDialog();
            closeDialog();
        }
    }

    private void getListaMotivo() {
        BaseModalsView.showProgressDialog(noRecolectaView.getContextView(),
                R.string.text_actualizando_motivos);
        final RequestCallback callback = new RequestCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if (response.getBoolean("success")) {
                        rutaPendienteInteractor.deleteMotivos(MotivoDescarga.Tipo.NO_RECOLECTA);
                        saveMotivos(response.getJSONArray("data"));
                        hideProgressDialog();
                        loadMotivos();
                    } else {
                        hideProgressDialog();
                        showToast(noRecolectaView.getContextView(),
                                response.getString("msg_error"), Toast.LENGTH_LONG);
                    }
                } catch (JSONException ex) {
                    ex.printStackTrace();
                    hideProgressDialog();
                    showSnackBar(noRecolectaView.baseFindViewById(R.id.lvMotivos),
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
                hideProgressDialog();
                showSnackBar(noRecolectaView.baseFindViewById(R.id.lvMotivos),
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
                MotivoDescarga.Tipo.NO_RECOLECTA + "",
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
                rutas.get(0).getIdServicio(),
                CameraUtils.getDateLastCapturePhoto().getTime() + "",
                LocationUtils.getLatitude() + "",
                LocationUtils.getLongitude() + "",
                anotaciones,
                generateIdServicioAdjuntos(),
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

        noRecolectaView.showGaleria(galeria);
    }

    private boolean compressImage() {
        String pathImage = photoCapture.getPath();
        Log.d(TAG, "PATHIMAGE SILICOMPRESSOR: " + pathImage);

        try {
            String compressFilePath = CustomSiliCompressor.with(noRecolectaView.getContextView()).compress(pathImage);

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
        noRecolectaView.notifyGaleryAllItemChanged();
    }

    private void saveMotivos(JSONArray data) throws JSONException{
        JSONObject jsonObject;
        for (int i = 0; i < data.length(); i++) {
            jsonObject = data.getJSONObject(i);
            MotivoDescarga motivo = new MotivoDescarga(
                    Preferences.getInstance().getString("idUsuario", ""),
                    MotivoDescarga.Tipo.NO_RECOLECTA,
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
                MotivoDescarga.Tipo.NO_RECOLECTA, rutas.get(0).getLineaNegocio());

        selectedIndexMotivo = -1;

        motivoItems = new ArrayList<>();
        MotivoDescargaItem item;

        for (MotivoDescarga motivo : dbMotivoDescargas) {
            item = new MotivoDescargaItem(motivo.getDescripcion(), false);
            motivoItems.add(item);
        }

        noRecolectaView.showListaMotivos(motivoItems);
    }

    private void loadDataRutas() {
        for (int i = 0; i < rutas.size(); i++) {
            DescargaRuta descargaRuta = rutaPendienteInteractor.selectDescargaRuta(
                    rutas.get(i).getIdServicio(), rutas.get(i).getLineaNegocio());
            descargaRutas.add(descargaRuta);
        }
    }

    private void showTextGuiasElectronicas() {
        if (rutas.size() == 1) {
            noRecolectaView.setGuiaElectronica(rutas.get(0).getGuia());
        } else {
            String guias = "";
            for (int i = 0; i < rutas.size(); i++) {
                if (i == rutas.size() - 1) {
                    guias += rutas.get(i).getGuia();
                } else {
                    guias += rutas.get(i).getGuia() + "  •  ";
                }
            }
            noRecolectaView.setGuiaElectronica(guias);
        }
    }

    private void saveGestionGE() {
        Date date = new Date();
        String fecha = new SimpleDateFormat("dd/MM/yyyy").format(date);
        String hora = new SimpleDateFormat("HH:mm:ss").format(date);

        for (int i = 0; i < rutas.size(); i++) {
            GuiaGestionada guiaGestionada = new GuiaGestionada(
                    Preferences.getInstance().getString("idUsuario", ""),
                    rutas.get(i).getIdServicio(),
                    dbMotivoDescargas.get(selectedIndexMotivo).getIdMotivo(),
                    rutas.get(i).getTipoZona(),
                    rutas.get(i).getTipo(),
                    rutas.get(i).getLineaNegocio(),
                    fecha, hora,
                    LocationUtils.getLatitude() + "",
                    LocationUtils.getLongitude() + "",
                    "", "", "", "", "",
                    GuiaGestionada.Recoleccion.NO_EFECTIVA + "",
                    "", "", "",
                    noRecolectaView.getViewTxtComentarios().getText().toString(),
                    "0", "",
                    Data.Delete.NO,
                    rutas.get(i).getDataValidate(),
                    numVecesGestionado
            );
            guiaGestionada.save();
        }
    }

    private void deleteImageGalery(int position) {
        FileUtils.deleteFile(dbImagenes.get(position).getPath() + dbImagenes.get(position).getName());

        dbImagenes.get(position).delete();

        contadorImagenes--;

        dbImagenes.remove(position);

        galeria.remove(position);

        noRecolectaView.notifyGaleryItemRemove(position + 1); // Add 2 for header
//        noRecolectaView.notifyGaleryAllItemChanged();
    }

    private void checkUploadDataSyncImages() {
        Log.d(TAG, "checkUploadDataSyncImages");
        dbImagenes = Imagen.find(Imagen.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("idSuperior") + " = ? and " +
                        NamingHelper.toSQLNameDefault("dataSync") + " = ? and " +
                        NamingHelper.toSQLNameDefault("clasificacion") + " = ?",
                Preferences.getInstance().getString("idUsuario", ""),
                rutas.get(0).getIdImagen(),
                Data.Sync.MANUAL + "",
                Imagen.Tipo.GESTION_GUIA + "");
        Log.d(TAG, "TOTAL IMAGENS: " + dbImagenes.size());

        for (int i = 0; i < dbImagenes.size(); i++) {
            Log.d(TAG, "IMAGEN: " + dbImagenes.get(i).getIdSuperior());
            dbImagenes.get(i).setDataSync(Data.Sync.PENDING);
            dbImagenes.get(i).save();
        }
    }

    private boolean validateDescarga() {
        if (validateFechaDispositivo()) {
            if (validateSelectedMotivo()) {
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
            Date fechaRuta = new SimpleDateFormat("dd/MM/yyyy").parse(rutas.get(0).getFechaRuta());

            if (fechaActual.getTime() >= fechaRuta.getTime()) {
                return true;
            } else {
                ((AppCompatActivity) noRecolectaView.getContextView()).runOnUiThread(() -> {
                    ModalHelper.getBuilderAlertDialog(noRecolectaView.getContextView())
                            .setTitle(R.string.text_configurar_fecha_hora)
                            .setIcon(R.mipmap.ic_launcher)
                            .setMessage(R.string.act_main_message_date_time_incorrect)
                            .setPositiveButton(R.string.text_configurar, (dialog, which) -> {
                                Intent intent = new Intent(Settings.ACTION_DATE_SETTINGS);
                                noRecolectaView.getContextView().startActivity(intent);
                            })
                            .setNegativeButton(R.string.text_cancelar, (dialog, which) -> { })
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

    private boolean validateEstadoShipper() {
        // validar si con el cliente y contrato, su la ultima gestion fue efectiva.
        for (int i = 0; i < rutas.size(); i++) {
            if (rutas.get(i).getEstadoShipper() != null && rutas.get(i).getEstadoShipper().equals("4")) {
                showAlertEstadoShipper(R.drawable.ic_estado_cliente_critico,
                        noRecolectaView.getContextView().getString(R.string.act_ruta_msg_estado_shipper_critico_recoleccion));
                return false;
            }
        }
        return true;
    }

    private void showAlertEstadoShipper(final int idResIcon, final String msg) {
        ModalHelper.getBuilderAlertDialog(noRecolectaView.getContextView())
                .setTitle(R.string.act_ruta_title_estado_shipper)
                .setMessage(msg)
                .setIcon(idResIcon)
                .setPositiveButton(R.string.text_gestionar, (dialog, which) -> {
                    dialog.dismiss();
                    if (validateDescarga()) {
                        new SaveGestionTask().execute();
                    }
                })
                .setNegativeButton(R.string.text_cancelar, null)
                .show();
    }

    private boolean validateSelectedMotivo() {
        if (selectedIndexMotivo < 0) {
            showSnackBar(noRecolectaView.getViewTxtComentarios(),
                    R.string.activity_detalle_ruta_message_motivo_no_seleccionado,
                    Snackbar.LENGTH_LONG);
        }
        return selectedIndexMotivo >= 0;
    }

    private boolean validateFoto() {
        if (contadorImagenes == 0) {
            showSnackBar(noRecolectaView.getViewTxtComentarios(),
                    R.string.activity_detalle_ruta_message_tomar_al_menos_una_foto,
                    Snackbar.LENGTH_LONG);
        }
        return contadorImagenes > 0;
    }

    private boolean canTakePhoto() {
        return (contadorImagenes + 1) <= MAX_PHOTO_CAPTURE;
    }

    private void closeDialog() {
        ((DialogFragment) noRecolectaView.getFragment()).dismiss();
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

    private String generateIdServicioAdjuntos() {
        String idServiciosAdjuntos = "";
        for (int i = 0; i < rutas.size(); i++) {
            if ((i + 1) == rutas.size()) {
                idServiciosAdjuntos += rutas.get(i).getIdServicio();
            } else {
                idServiciosAdjuntos += rutas.get(i).getIdServicio() + "|";
            }
        }
        return idServiciosAdjuntos;
    }

    private void updateBackgroundSelectListaMotivos(int position) {
        for (int i = 0; i < motivoItems.size(); i++) {
            motivoItems.get(i).setSelected(false);
        }
        motivoItems.get(position).setSelected(true);

        noRecolectaView.notifyMotivosAllItemChanged();
    }

    /**
     * Receiver
     *
     * {@link DetalleRutaPresenter#descargaFinalizadaReceiver}
     * {@link RutaPendientePresenter#descargaFinalizadaReceiver}
     */
    private void sendOnDescargaFinalizadaReceiver() {
        Intent intent = new Intent("OnDescargaFinalizada");
        Bundle bundle = new Bundle();
        bundle.putSerializable("guias", rutas);
        intent.putExtra("args", bundle);
        LocalBroadcastManager.getInstance(noRecolectaView.getContextView()).sendBroadcast(intent);
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

}

