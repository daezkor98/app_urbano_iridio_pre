package com.urbanoexpress.iridio3.pre.presenter;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.orm.util.NamingHelper;

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

import com.urbanoexpress.iridio3.pre.util.async.AsyncTaskCoroutine;
import com.urbanoexpress.iridio3.pre.R;
import com.urbanoexpress.iridio3.pre.model.entity.Data;
import com.urbanoexpress.iridio3.pre.model.entity.DescargaRuta;
import com.urbanoexpress.iridio3.pre.model.entity.Imagen;
import com.urbanoexpress.iridio3.pre.model.entity.MotivoDescarga;
import com.urbanoexpress.iridio3.pre.model.entity.Ruta;
import com.urbanoexpress.iridio3.pre.model.entity.GuiaGestionada;
import com.urbanoexpress.iridio3.pre.model.interactor.RutaPendienteInteractor;
import com.urbanoexpress.iridio3.pre.model.interactor.callback.RequestCallback;
import com.urbanoexpress.iridio3.pre.model.util.ModelUtils;
import com.urbanoexpress.iridio3.pre.ui.FirmarActivity;
import com.urbanoexpress.iridio3.pre.ui.QRScannerActivity;
import com.urbanoexpress.iridio3.pre.ui.dialogs.RecolectaGEDialog;
import com.urbanoexpress.iridio3.pre.ui.helpers.ModalHelper;
import com.urbanoexpress.iridio3.pre.ui.interfaces.OnClickItemGaleriaListener;
import com.urbanoexpress.iridio3.pre.ui.model.GaleriaDescargaRutaItem;
import com.urbanoexpress.iridio3.pre.ui.model.MotivoDescargaItem;
import com.urbanoexpress.iridio3.pre.util.CameraUtils;
import com.urbanoexpress.iridio3.pre.util.CommonUtils;
import com.urbanoexpress.iridio3.pre.util.CustomSiliCompressor;
import com.urbanoexpress.iridio3.pre.util.FileUtils;
import com.urbanoexpress.iridio3.pre.util.LocationUtils;
import com.urbanoexpress.iridio3.pre.util.Preferences;
import com.urbanoexpress.iridio3.pre.util.constant.LocalAction;
import com.urbanoexpress.iridio3.pre.view.BaseModalsView;
import com.urbanoexpress.iridio3.pre.view.RecolectaView;

/**
 * Created by mick on 18/08/16.
 */

public class RecolectaGEPresenter extends BaseModalsView implements OnClickItemGaleriaListener {

    private static final String TAG = EntregaGEPresenter.class.getSimpleName();

    private RecolectaView recolectaView;
    private RutaPendienteInteractor rutaPendienteInteractor;

    private List<MotivoDescarga> dbMotivoDescargas = Collections.emptyList();
    private List<MotivoDescargaItem> motivoItems = new ArrayList<>();

    private List<Imagen> dbImagenes;

    private List<GaleriaDescargaRutaItem> galeria = new ArrayList<>();

    private ArrayList<DescargaRuta> descargaRutas = new ArrayList<>();
    private ArrayList<Ruta> rutas = new ArrayList<>();

    private File photoCapture;

    private final int MAX_PHOTO_CAPTURE = 10;
    private final int MAX_SIGNING_CAPTURE = 1;

    private int contadorImagenes = 0, contadorFirma = 0, contadorCargo = 0, contadorDomicilio = 0;

    private int selectedIndexMotivo = -1;

    private int selectedIndexGalery = -1;

    private int numVecesGestionado = 0;

    private String typeCameraCaptureImage;

    private boolean guiaElectronicaDisponible = false;

    private int clickBtnScanBarCode = 0; // 1 => guia manual; 2 => guia recoleccion

    public RecolectaGEPresenter(RecolectaView recolectaView, ArrayList<Ruta> rutas,
                                int numVecesGestionado, boolean guiaElectronicaDisponible) {
        this.recolectaView = recolectaView;
        this.rutas = rutas;
        this.numVecesGestionado = numVecesGestionado;
        this.guiaElectronicaDisponible = guiaElectronicaDisponible;
        rutaPendienteInteractor = new RutaPendienteInteractor(recolectaView.getContextView());
        init();
    }

    private void init() {
        showTextGuiasElectronicas();
        loadDataRutas();
        loadMotivos();
        selectMotivoEnRecolecciones();
        loadGaleria();
        setVisibilityInputRecolecta();
        setVisibilityFormularioRecoleccion();

//        if (Connection.hasNetworkConnectivity(recolectaView.getContextView())) {
//            getListaMotivo();
//        }

        LocalBroadcastManager.getInstance(recolectaView.getContextView())
                .registerReceiver(saveFirmaReceiver, new IntentFilter("OnSaveFirma"));
        LocalBroadcastManager.getInstance(recolectaView.getContextView())
                .registerReceiver(resultScannReceiver,
                        new IntentFilter(LocalAction.LOCAL_ACTION_BARCODE_SCAN_RESULT));
    }

    public void onClickUpdateMotivos() {
        if (CommonUtils.validateConnectivity(recolectaView.getContextView())) {
            getListaMotivo();
        }
    }

    @Override
    public void onClickCamera() {
        if (canTakePhoto()) {
            typeCameraCaptureImage = "Imagen";

            ModalHelper.getBuilderAlertDialog(recolectaView.getContextView())
                    .setTitle("Seleccione el tipo de imagen que esta subiendo")
                    .setSingleChoiceItems(new String[]{"Foto Producto", "Foto Domicilio"}, 0,
                            (dialog, which) -> {
                        if (which == 0) {
                            typeCameraCaptureImage = "Imagen";
                        } else if (which == 1) {
                            typeCameraCaptureImage = "Domicilio";
                        }
                    })
                    .setPositiveButton(R.string.text_aceptar, (dialog, which) ->
                            photoCapture = CameraUtils.openCamera(recolectaView.getFragment(),
                            "Recoleccion/" + rutas.get(0).getLineaNegocio() + "-" +
                                    rutas.get(0).getIdGuia() + "/", typeCameraCaptureImage))
                    .setNegativeButton(R.string.text_cancelar, null)
                    .show();
        } else {
            showAlertDialog(recolectaView.getContextView(),
                    R.string.text_advertencia,
                    R.string.activity_detalle_ruta_message_no_puede_tomar_foto,
                    R.string.text_aceptar, null);
        }
    }

    @Override
    public void onClickFirma() {
        if (canTakeSigning()) {
            typeCameraCaptureImage = "Firma";
            Activity activity = (Activity) recolectaView.getContextView();
            Intent intent = new Intent(recolectaView.getContextView(), FirmarActivity.class);
            intent.putExtra("pathDirectory" , "Recoleccion/" + rutas.get(0).getLineaNegocio() +
                    "-" + rutas.get(0).getIdGuia() + "/");
            activity.startActivity(intent);
            activity.overridePendingTransition(R.anim.slide_enter_from_bottom, R.anim.not_slide);
        } else {
            showAlertDialog(recolectaView.getContextView(),
                    R.string.text_advertencia,
                    R.string.activity_detalle_ruta_message_no_puede_firmar,
                    R.string.text_aceptar, null);
        }
    }

    @Override
    public void onClickCargo() {
        if (canTakePhoto()) {
            typeCameraCaptureImage = "Cargo";
            photoCapture = CameraUtils.openCamera(recolectaView.getFragment(),
                    "Recoleccion/" + rutas.get(0).getLineaNegocio() +
                            "-" + rutas.get(0).getIdGuia() + "/", typeCameraCaptureImage);
        } else {
            BaseModalsView.showAlertDialog(recolectaView.getContextView(),
                    R.string.text_advertencia,
                    R.string.activity_detalle_ruta_message_no_puede_tomar_foto,
                    R.string.text_aceptar, null);
        }
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
        // Restar 3 posiciones que corresponden a los botones
        selectedIndexGalery = position - 3;

        dbImagenes = selectAllImages();

        showAlertDialog(recolectaView.getContextView(),
                R.string.activity_detalle_ruta_title_eliminar_galeria,
                R.string.activity_detalle_ruta_message_eliminar_galeria,
                R.string.text_aceptar, (dialog, which) -> deleteImageGalery(selectedIndexGalery),
                R.string.text_cancelar, null);
    }

    public void onActivityResultImage() {
        if (compressImage()) {
            insertPhotoToGalery();
            saveImage(photoCapture.getName(), photoCapture.getParent() + File.separator, typeCameraCaptureImage.toLowerCase());
        } else {
            if (recolectaView.getContextView() != null) {
                showToast(recolectaView.getContextView(),
                        "Lo sentimos, ocurrió un error al tomar la foto.", Toast.LENGTH_LONG);
            }
        }
    }

    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        LocalBroadcastManager.getInstance(recolectaView.getContextView())
                .unregisterReceiver(saveFirmaReceiver);
        LocalBroadcastManager.getInstance(recolectaView.getContextView())
                .unregisterReceiver(resultScannReceiver);
    }

    public void onClickItemMotivo(int position) {
        updateBackgroundSelectListaMotivos(position);
        selectedIndexMotivo = position;
    }

    public void onClickAceptar() {
        if (validateDescarga()) {
            //if (guiaElectronicaDisponible && numVecesGestionado == 1) {
            if (guiaElectronicaDisponible) {
                new SaveGestionGuiaModoGuiaManualTask().execute();
            } else {
                new SaveGestionGuiaTask().execute();
            }
        }
    }

    class SaveGestionGuiaTask extends AsyncTaskCoroutine<String, String> {

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            showProgressDialog(recolectaView.getContextView(), R.string.text_gestionando_guia);
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
            sendOnDescargaFinalizadaReceiver();
            hideProgressDialog();
            closeDialog();
        }
    }

    class SaveGestionGuiaModoGuiaManualTask extends AsyncTaskCoroutine<String, String> {

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            showProgressDialog(recolectaView.getContextView(), R.string.text_gestionando_guia);
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
            hideProgressDialog();
            final AppCompatActivity activity = (AppCompatActivity) recolectaView.getContextView();
            ModalHelper.getBuilderAlertDialog(activity)
                    .setTitle(R.string.activity_detalle_ruta_title_agregar_otra_guia)
                    .setMessage(R.string.activity_detalle_ruta_message_agregar_otra_guia)
                    .setPositiveButton(R.string.text_si, (dialog, which) -> {
                        dialog.dismiss();
                        closeDialog();
                        RecolectaGEDialog fragmentRecolectaConGuia =
                                new RecolectaGEDialog().newInstance(
                                        rutas, 2, true);
                        FragmentManager fragmentManager = activity.getSupportFragmentManager();
                        fragmentRecolectaConGuia.show(fragmentManager, RecolectaGEDialog.TAG);
                    })
                    .setNegativeButton(R.string.text_no, (dialog, which) -> {
                        dialog.dismiss();
                        new SaveEstadoGestionModoGuiaManualTask().execute();
                    })
                    .setCancelable(false)
                    .show();
        }
    }

    class SaveEstadoGestionModoGuiaManualTask extends AsyncTaskCoroutine<String, String> {

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            showProgressDialog(recolectaView.getContextView(), R.string.text_gestionando_guia);
        }

        @Override
        public String doInBackground(String... strings) {
            updateEstadoGestionGE(DescargaRuta.Entrega.FINALIZADO);
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

    public void onCLickBtnScanBarCode(int value) {
        clickBtnScanBarCode = value;
        Intent intent = new Intent(recolectaView.getContextView(), QRScannerActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("typeImpl", QRScannerPresenter.IMPLEMENT.READ_ONLY);
        intent.putExtra("args", bundle);

        recolectaView.getContextView().startActivity(intent);
        ((Activity) recolectaView.getContextView()).
                overridePendingTransition(R.anim.slide_enter_from_bottom, R.anim.not_slide);
    }

    private void getListaMotivo() {
        BaseModalsView.showProgressDialog(recolectaView.getContextView(),
                R.string.text_actualizando_motivos);
        final RequestCallback callback = new RequestCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if (response.getBoolean("success")) {
                        rutaPendienteInteractor.deleteMotivos(MotivoDescarga.Tipo.RECOLECTA);
                        saveMotivos(response.getJSONArray("data"));
                        hideProgressDialog();
                        loadMotivos();
                    } else {
                        hideProgressDialog();
                        showToast(recolectaView.getContextView(),
                                response.getString("msg_error"), Toast.LENGTH_LONG);
                    }
                } catch (JSONException ex) {
                    ex.printStackTrace();
                    hideProgressDialog();
                    showSnackBar(recolectaView.baseFindViewById(R.id.lvMotivos),
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
                showSnackBar(recolectaView.baseFindViewById(R.id.lvMotivos),
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
                MotivoDescarga.Tipo.RECOLECTA + "",
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

        if (anotaciones.equals("imagen")) {
            contadorImagenes++;
        } else if (anotaciones.equals("firma")) {
            contadorFirma++;
        }  else if (anotaciones.equals("cargo")) {
            contadorCargo++;
        } else if (anotaciones.equals("domicilio")) {
            contadorDomicilio++;
        }

        Log.d(TAG, "save image");
        Log.d(TAG, "file name: " + fileName);
        Log.d(TAG, "file directory: " + directory);
    }

    private void loadGaleria() {
        new VerifyExistImagesOnDeviceTask().execute();
    }

    private void showGaleria() {
        dbImagenes = selectAllImages();
        Log.d(TAG, "showGaleria: " + dbImagenes + " imagenes.");

        if (dbImagenes.size() > 0) {
            GaleriaDescargaRutaItem item;

            for (Imagen imagen : dbImagenes) {
                item = new GaleriaDescargaRutaItem(imagen.getPath() + imagen.getName());
                galeria.add(item);

                if (imagen.getName().contains("Imagen")) {
                    contadorImagenes++;
                } else if (imagen.getName().contains("Firma")) {
                    contadorFirma++;
                }  else if (imagen.getName().contains("Cargo")) {
                    contadorCargo++;
                } else if (imagen.getName().contains("Domicilio")) {
                    contadorDomicilio++;
                }
            }
        } else {
            Log.d(TAG, "No hay galeria");
        }

        recolectaView.showGaleria(galeria);
    }

    private boolean compressImage() {
        String pathImage = photoCapture.getPath();
        Log.d(TAG, "PATHIMAGE SILICOMPRESSOR: " + pathImage);

        String compressFilePath = "";

        try {
            if (typeCameraCaptureImage.equalsIgnoreCase("Cargo")) {
                compressFilePath = CustomSiliCompressor.with(recolectaView.getContextView())
                        .compress(pathImage, 1280.0f, 720.0f, 85);
            } else {
                compressFilePath = CustomSiliCompressor.with(recolectaView.getContextView()).compress(pathImage);
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
        recolectaView.notifyGaleryAllItemChanged();
    }

    private void saveMotivos(JSONArray data) throws JSONException {
        JSONObject jsonObject;
        for (int i = 0; i < data.length(); i++) {
            jsonObject = data.getJSONObject(i);
            MotivoDescarga motivo = new MotivoDescarga(
                    Preferences.getInstance().getString("idUsuario", ""),
                    MotivoDescarga.Tipo.RECOLECTA,
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
                MotivoDescarga.Tipo.RECOLECTA, rutas.get(0).getLineaNegocio());

        selectedIndexMotivo = -1;

        motivoItems = new ArrayList<>();
        MotivoDescargaItem item;

        for (MotivoDescarga motivo : dbMotivoDescargas) {
            item = new MotivoDescargaItem(motivo.getDescripcion(), false);
            motivoItems.add(item);
        }

        recolectaView.showListaMotivos(motivoItems);
    }

    private void selectMotivoEnRecolecciones() {
        // Seleccionar el motivo: Recolectado Completo (mot_id => 168)
        // Cuando es una recoleccion efectiva (Con guia o sin guia)
        if (ModelUtils.isGuiaRecoleccion(rutas.get(0).getTipo())) {
            if (descargaRutas.get(0).getProcesoDescarga() ==
                    DescargaRuta.Recoleccion.RECOLECTAR_SIN_GUIA_ELECTRONICA ||
                    descargaRutas.get(0).getProcesoDescarga() ==
                            DescargaRuta.Recoleccion.RECOLECTAR_CON_GUIA_ELECTRONICA ||
                    descargaRutas.get(0).getProcesoDescarga() ==
                            DescargaRuta.Recoleccion.RECOLECTAR_CON_GUIA_MANUAL ||
                    descargaRutas.get(0).getProcesoDescarga() ==
                            DescargaRuta.Recoleccion.FINALIZADO) {
                for (int i = 0; i < dbMotivoDescargas.size(); i++) {
                    if (dbMotivoDescargas.get(i).getIdMotivo().equals("168")) {
                        onClickItemMotivo(i);
                        break;
                    }
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

    private void showTextGuiasElectronicas() {
        if (rutas.size() == 1) {
            recolectaView.setGuiaElectronica(rutas.get(0).getGuia());
        } else {
            String guias = "";
            for (int i = 0; i < rutas.size(); i++) {
                if (i == rutas.size() - 1) {
                    guias += rutas.get(i).getGuia();
                } else {
                    guias += rutas.get(i).getGuia() + "  •  ";
                }
            }
            recolectaView.setGuiaElectronica(guias);
        }
    }

    private void saveGestionGE() {
        Date date = new Date();
        String fecha = new SimpleDateFormat("dd/MM/yyyy").format(date);
        String hora = new SimpleDateFormat("HH:mm:ss").format(date);

//        Long rangoSerie[] = getRangoSerie();
//        Preferences.getInstance().edit()
//                .putLong("inicioSerieRecoleccion", rangoSerie[1]).commit();

        Log.d(TAG, "SAVE DESCARGA: " + rutas.size());
        for (int i = 0; i < rutas.size(); i++) {
            if (guiaElectronicaDisponible) {
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
                        "", "", "",
                        "",
                        "0",
                        GuiaGestionada.Recoleccion.GUIA_MANUAL + "",
                        recolectaView.getViewTxtGuiaManual().getText().toString(),
                        "",
                        "",
                        recolectaView.getViewTxtComentarios().getText().toString(),
                        "0", "",
                        Data.Delete.NO,
                        rutas.get(i).getDataValidate(),
                        numVecesGestionado
                );
                guiaGestionada.save();
            } else {
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
                        "", "", "",
                        "",
                        "",
                        GuiaGestionada.Recoleccion.SIN_GUIA + "",
                        "", "", "",
                        recolectaView.getViewTxtComentarios().getText().toString(),
                        "0", "",
                        Data.Delete.NO,
                        rutas.get(i).getDataValidate(),
                        numVecesGestionado
                );
                guiaGestionada.save();
            }
        }
    }

    private void saveFormularioRecoleccion() {
        if (numVecesGestionado == 1) {
            Log.d(TAG, "SAVE FORMULARIO RECOLECCION: " + rutas.size());

            int sobre, valija, paquete,  otros;

            sobre = recolectaView.getViewTxtFrmSobre().getText().toString().trim().isEmpty()
                    ? 0 : Integer.parseInt(recolectaView.getViewTxtFrmSobre().getText().toString().trim());
            valija = recolectaView.getViewTxtFrmValija().getText().toString().trim().isEmpty()
                    ? 0 : Integer.parseInt(recolectaView.getViewTxtFrmValija().getText().toString().trim());
            paquete = recolectaView.getViewTxtFrmPaquete().getText().toString().trim().isEmpty()
                    ? 0 : Integer.parseInt(recolectaView.getViewTxtFrmPaquete().getText().toString().trim());
            otros = recolectaView.getViewTxtFrmOtros().getText().toString().trim().isEmpty()
                    ? 0 : Integer.parseInt(recolectaView.getViewTxtFrmOtros().getText().toString().trim());

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
                        sobre + "",
                        valija + "",
                        paquete + "",
                        otros + "",
                        GuiaGestionada.Recoleccion.FORMULARIO_RECOLECCION + "",
                        recolectaView.getViewTxtFrmGuiaRecoleccion().getText().toString() + "",
                        "",
                        "",
                        "",
                        "0",
                        "",
                        Data.Delete.NO,
                        rutas.get(i).getDataValidate(),
                        numVecesGestionado
                );
                guiaGestionada.save();
            }
        }
    }

    private void deleteImageGalery(int position) {
        FileUtils.deleteFile(dbImagenes.get(position).getPath() + dbImagenes.get(position).getName());

        dbImagenes.get(position).delete();

        if (dbImagenes.get(selectedIndexGalery).getName().contains("Imagen")) {
            contadorImagenes--;
        } else if (dbImagenes.get(selectedIndexGalery).getName().contains("Firma")) {
            contadorFirma--;
        }  else if (dbImagenes.get(selectedIndexGalery).getName().contains("Cargo")) {
            contadorCargo--;
        } else if (dbImagenes.get(selectedIndexGalery).getName().contains("Domicilio")) {
            contadorDomicilio--;
        }

        dbImagenes.remove(position);

        galeria.remove(position);

        recolectaView.notifyGaleryItemRemove(position + 3); // Add 3 for buttons
//        recolectaView.notifyGaleryAllItemChanged();
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
                if (validateDatosEntrega()) {
                    if (validateFormularioRecoleccion()) {
                        if (validateFoto()) {
                            return true;
                        }
                    }
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
                ((AppCompatActivity) recolectaView.getContextView()).runOnUiThread(() -> {
                    ModalHelper.getBuilderAlertDialog(recolectaView.getContextView())
                            .setTitle(R.string.text_configurar_fecha_hora)
                            .setIcon(R.mipmap.ic_launcher)
                            .setMessage(R.string.act_main_message_date_time_incorrect)
                            .setPositiveButton(R.string.text_configurar, (dialog, which) -> {
                                Intent intent = new Intent(Settings.ACTION_DATE_SETTINGS);
                                recolectaView.getContextView().startActivity(intent);
                            })
                            .setNegativeButton(R.string.text_cancelar, null)
                            .show();
                });
                CommonUtils.vibrateDevice(recolectaView.getContextView(), 100);
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

    private boolean validateSelectedMotivo() {
        if (selectedIndexMotivo >= 0) {
            return true;
        }
        BaseModalsView.showSnackBar(recolectaView.baseFindViewById(R.id.lvMotivos),
                R.string.activity_detalle_ruta_message_motivo_no_seleccionado,
                Snackbar.LENGTH_INDEFINITE,
                R.string.text_ok,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
        CommonUtils.vibrateDevice(recolectaView.getContextView(), 100);
        return false;
    }

    private boolean validateDatosEntrega() {
        boolean validate = true;

        if (guiaElectronicaDisponible) {
            if (recolectaView.getViewTxtGuiaManual().getText().toString().isEmpty()) {
                recolectaView.getViewTxtGuiaManual().setError("Ingrese correctamente la guía manual.");
                validate = showAlertError(
                        recolectaView.getViewTxtGuiaManual(), validate);
                CommonUtils.vibrateDevice(recolectaView.getContextView(), 100);
            }
//            else {
//                if (ValidationUtils.isFormatoCorrectoGuia(
//                        recolectaView.getViewTxtGuia().getText().toString())) {
//                    try {
//                        Long guia = Long.parseLong(recolectaView.getViewTxtGuia().getText().toString());
//                        validateDateSytem = ValidationUtils.validateGuia(guia + "", 1);
//                        inputGuia = guia + "";
//                        if (!validateDateSytem) {
//                            recolectaView.getViewTxtGuia().setError("La guia no es valido.");
//                            validateDateSytem = showAlertError(
//                                    recolectaView.getViewTxtGuia(), validateDateSytem);
//                        }
//                    } catch (NumberFormatException ex) {
//                        ex.printStackTrace();
//                        String guia = recolectaView.getViewTxtGuia().getText().toString();
//                        guia = guia.substring(1, guia.length()); // Quitar la G
//                        inputGuia = guia + "";
//                        Log.d(TAG, "VALIDATE GUIA: " + guia);
//                        validateDateSytem = ValidationUtils.validateGuia(guia + "", 1);
//                        if (!validateDateSytem) {
//                            recolectaView.getViewTxtGuia().setError("La guia no es valido.");
//                            validateDateSytem = showAlertError(
//                                    recolectaView.getViewTxtGuia(), validateDateSytem);
//                        }
//                    }
//                } else {
//                    recolectaView.getViewTxtGuia().setError("El formato de la guia es incorrecta.");
//                    validateDateSytem = showAlertError(
//                            recolectaView.getViewTxtGuia(), validateDateSytem);
//                }
//            }
        }

        Log.d(TAG, "validateDatosEntrega result: " + validate);
        return validate;
    }

    private boolean validateFormularioRecoleccion() {
        if (numVecesGestionado == 1) {
            /*if (recolectaView.getViewTxtFrmGuiaRecoleccion().getText().toString().trim().isEmpty()) {
                recolectaView.getViewTxtFrmGuiaRecoleccion().setError(
                        recolectaView.getContextView().getString(
                                R.string.act_detalle_ruta_msg_error_ingresa_guia_recoleccion));
                recolectaView.getViewTxtFrmGuiaRecoleccion().requestFocus();
                return false;
            }*/

            //String guiaRecoleccion = recolectaView.getViewTxtFrmGuiaRecoleccion().getText().toString().trim().toUpperCase();


            /*if (!guiaRecoleccion.matches("^(GRE)\\d{1,10}$")) {
                recolectaView.getViewTxtFrmGuiaRecoleccion().setError(
                        recolectaView.getContextView().getString(
                                R.string.act_detalle_ruta_msg_error_guia_recoleccion_incorrecto));
                recolectaView.getViewTxtFrmGuiaRecoleccion().requestFocus();
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
                    recolectaView.getViewTxtFrmGuiaRecoleccion().setError(
                            recolectaView.getContextView().getString(
                                    R.string.act_detalle_ruta_msg_error_guia_recoleccion_en_uso));
                    recolectaView.getViewTxtFrmGuiaRecoleccion().requestFocus();
                    return false;
                }

                if (!ValidationUtils.validateDigitoValido(guiaRecoleccion)) {
                    recolectaView.getViewTxtFrmGuiaRecoleccion().setError(
                            recolectaView.getContextView().getString(
                                    R.string.act_detalle_ruta_msg_error_guia_recoleccion_incorrecto));
                    recolectaView.getViewTxtFrmGuiaRecoleccion().requestFocus();
                    return false;
                }*/

                int sobre, valija, paquete,  otros;

                sobre = recolectaView.getViewTxtFrmSobre().getText().toString().trim().isEmpty()
                        ? 0 : Integer.parseInt(recolectaView.getViewTxtFrmSobre().getText().toString().trim());
                valija = recolectaView.getViewTxtFrmValija().getText().toString().trim().isEmpty()
                        ? 0 : Integer.parseInt(recolectaView.getViewTxtFrmValija().getText().toString().trim());
                paquete = recolectaView.getViewTxtFrmPaquete().getText().toString().trim().isEmpty()
                        ? 0 : Integer.parseInt(recolectaView.getViewTxtFrmPaquete().getText().toString().trim());
                otros = recolectaView.getViewTxtFrmOtros().getText().toString().trim().isEmpty()
                        ? 0 : Integer.parseInt(recolectaView.getViewTxtFrmOtros().getText().toString().trim());

                if (!(sobre > 0 || valija > 0 || paquete > 0 || otros > 0)) {
                    BaseModalsView.showSnackBar(recolectaView.baseFindViewById(R.id.lvMotivos),
                            R.string.act_detalle_ruta_msg_error_frm_rec_ingresar_cantidad,
                            Snackbar.LENGTH_INDEFINITE,
                            R.string.text_ok,
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            });
                    CommonUtils.vibrateDevice(recolectaView.getContextView(), 100);
                    return false;
                }
            //}
        }

        return true;
    }

    private boolean showAlertError(View recolectaView, boolean showAlertError) {
        if (showAlertError) {
            recolectaView.requestFocus();
        }
        return false;
    }

    private boolean validateFoto() {
        if (contadorImagenes < 1) {
            BaseModalsView.showSnackBar(recolectaView.getViewTxtComentarios(),
                    "Debe tomar una foto del producto como mínimo para realizar la gestión de la recolección.",
                    Snackbar.LENGTH_LONG);
            CommonUtils.vibrateDevice(recolectaView.getContextView(), 100);
            return false;
        }

        if (contadorCargo < 1) {
            String msg = "Debe tomar una foto del " +
                    ModelUtils.getNameLblCargoGuia(recolectaView.getContextView()).toLowerCase() +
                    " como mínimo para realizar la gestión de la recolección.";
            BaseModalsView.showSnackBar(recolectaView.getViewTxtComentarios(), msg, Snackbar.LENGTH_LONG);
            CommonUtils.vibrateDevice(recolectaView.getContextView(), 100);
            return false;
        }

        if (contadorDomicilio < 1) {
            BaseModalsView.showSnackBar(recolectaView.getViewTxtComentarios(),
                    "Debe tomar una foto del domicilio como mínimo para realizar la gestión de la recolección.",
                    Snackbar.LENGTH_LONG);
            CommonUtils.vibrateDevice(recolectaView.getContextView(), 100);
            return false;
        }

        return true;
    }

    private boolean canTakePhoto() {
        return (contadorImagenes + 1) <= MAX_PHOTO_CAPTURE;
    }

    private boolean canTakeSigning() {
        return (contadorFirma + 1) <= MAX_SIGNING_CAPTURE;
    }

    private boolean isPhoto(int position) {
        return dbImagenes.get(position).getName().contains("Imagen");
    }

    private void closeDialog() {
        ((DialogFragment) recolectaView.getFragment()).dismiss();
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
            ruta.setResultadoGestion(Ruta.ResultadoGestion.EFECTIVA_COMPLETA);
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

        recolectaView.notifyMotivosAllItemChanged();
    }

    private void setVisibilityInputRecolecta() {
        if (!guiaElectronicaDisponible) {
            recolectaView.baseFindViewById(R.id.layoutHeaderDatosAdjuntos).setVisibility(View.GONE);
            recolectaView.setVisibilityLayoutInputGuia(View.GONE);
        }
    }

    private void setVisibilityFormularioRecoleccion() {
        if (numVecesGestionado == 2) {
            recolectaView.baseFindViewById(R.id.boxFormularioRecoleccion).setVisibility(View.GONE);
        }
    }

    /**
     * Receiver
     *
     * {@link DetalleRutaPresenter#descargaFinalizadaReceiver}
     * {@link RutaPendientePresenter#descargaFinalizadaReceiver}
     * {@link RutaGestionadaPresenter#descargaFinalizadaReceiver}
     */
    private void sendOnDescargaFinalizadaReceiver() {
        Intent intent = new Intent("OnDescargaFinalizada");
        Bundle bundle = new Bundle();
        bundle.putSerializable("guias", rutas);
        intent.putExtra("args", bundle);
        LocalBroadcastManager.getInstance(recolectaView.getContextView()).sendBroadcast(intent);
    }

    private class VerifyExistImagesOnDeviceTask extends AsyncTask<Void, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
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
            showGaleria();
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
                CommonUtils.vibrateDevice(recolectaView.getContextView(), 100);
            } catch (NullPointerException ex) {
                ex.printStackTrace();
            }
            if (clickBtnScanBarCode == 1) {
                recolectaView.getViewTxtGuiaManual().setText(intent.getStringExtra("value"));
            } else {
                recolectaView.getViewTxtFrmGuiaRecoleccion().setText(intent.getStringExtra("value"));
            }
        }
    };

}
