package com.urbanoexpress.iridio.presenter;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
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
import com.urbanoexpress.iridio.R;
import com.urbanoexpress.iridio.model.entity.Data;
import com.urbanoexpress.iridio.model.entity.DescargaRuta;
import com.urbanoexpress.iridio.model.entity.GuiaGestionada;
import com.urbanoexpress.iridio.model.entity.Imagen;
import com.urbanoexpress.iridio.model.entity.MotivoDescarga;
import com.urbanoexpress.iridio.model.entity.Ruta;
import com.urbanoexpress.iridio.model.interactor.RutaPendienteInteractor;
import com.urbanoexpress.iridio.model.interactor.callback.RequestCallback;
import com.urbanoexpress.iridio.model.util.ModelUtils;
import com.urbanoexpress.iridio.ui.FirmarActivity;
import com.urbanoexpress.iridio.ui.QRScannerActivity;
import com.urbanoexpress.iridio.ui.helpers.ModalHelper;
import com.urbanoexpress.iridio.ui.interfaces.OnClickItemGaleriaListener;
import com.urbanoexpress.iridio.ui.interfaces.OnClickItemListener;
import com.urbanoexpress.iridio.ui.model.GaleriaDescargaRutaItem;
import com.urbanoexpress.iridio.ui.model.MotivoDescargaItem;
import com.urbanoexpress.iridio.util.CameraUtils;
import com.urbanoexpress.iridio.util.CommonUtils;
import com.urbanoexpress.iridio.util.CustomSiliCompressor;
import com.urbanoexpress.iridio.util.FileUtils;
import com.urbanoexpress.iridio.util.LocationUtils;
import com.urbanoexpress.iridio.util.Preferences;
import com.urbanoexpress.iridio.util.constant.LocalAction;
import com.urbanoexpress.iridio.view.BaseModalsView;
import com.urbanoexpress.iridio.view.RecoleccionValijaView;

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

public class RecoleccionValijaPresenter extends BaseModalsView implements OnClickItemGaleriaListener, OnClickItemListener {

    private static final String TAG = RecoleccionValijaPresenter.class.getSimpleName();

    private RecoleccionValijaView view;
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

    private String typeCameraCaptureImage;

    public RecoleccionValijaPresenter(RecoleccionValijaView view,
                                      ArrayList<Ruta> rutas) {
        this.view = view;
        this.rutas = rutas;
        rutaPendienteInteractor = new RutaPendienteInteractor(view.getContextView());
        init();
    }

    private void init() {
        showTextGuiasElectronicas();
        loadDataRutas();
        loadMotivos();
        //selectMotivoEnRecolecciones();
        loadGaleria();

        LocalBroadcastManager.getInstance(view.getContextView())
                .registerReceiver(saveFirmaReceiver, new IntentFilter("OnSaveFirma"));
        LocalBroadcastManager.getInstance(view.getContextView())
                .registerReceiver(resultScannReceiver,
                        new IntentFilter(LocalAction.LOCAL_ACTION_BARCODE_SCAN_RESULT));
    }

    public void onClickUpdateMotivos() {
        if (CommonUtils.validateConnectivity(view.getContextView())) {
            getListaMotivo();
        }
    }

    @Override
    public void onClickCamera() {
        if (canTakePhoto()) {
            typeCameraCaptureImage = "Imagen";

            ModalHelper.getBuilderAlertDialog(view.getContextView())
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
                            photoCapture = CameraUtils.openCamera(view.getFragment(),
                            "Recoleccion/" + rutas.get(0).getLineaNegocio() + "-" +
                                    rutas.get(0).getIdGuia() + "/", typeCameraCaptureImage))
                    .setNegativeButton(R.string.text_cancelar, null)
                    .show();
        } else {
            showAlertDialog(view.getContextView(),
                    R.string.text_advertencia,
                    R.string.activity_detalle_ruta_message_no_puede_tomar_foto,
                    R.string.text_aceptar, null);
        }
    }

    @Override
    public void onClickFirma() {
        if (canTakeSigning()) {
            typeCameraCaptureImage = "Firma";
            Activity activity = (Activity) view.getContextView();
            Intent intent = new Intent(view.getContextView(), FirmarActivity.class);
            intent.putExtra("pathDirectory" , "Recoleccion/" + rutas.get(0).getLineaNegocio() +
                    "-" + rutas.get(0).getIdGuia() + "/");
            activity.startActivity(intent);
            activity.overridePendingTransition(R.anim.slide_enter_from_bottom, R.anim.not_slide);
        } else {
            showAlertDialog(view.getContextView(),
                    R.string.text_advertencia,
                    R.string.activity_detalle_ruta_message_no_puede_firmar,
                    R.string.text_aceptar, null);
        }
    }

    @Override
    public void onClickCargo() {
        if (canTakePhoto()) {
            typeCameraCaptureImage = "Cargo";
            photoCapture = CameraUtils.openCamera(view.getFragment(),
                    "Recoleccion/" + rutas.get(0).getLineaNegocio() + "-" +
                            rutas.get(0).getIdGuia() + "/", typeCameraCaptureImage);
        } else {
            BaseModalsView.showAlertDialog(view.getContextView(),
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

        showAlertDialog(view.getContextView(),
                R.string.activity_detalle_ruta_title_eliminar_galeria,
                R.string.activity_detalle_ruta_message_eliminar_galeria,
                R.string.text_aceptar, (dialog, which) -> deleteImageGalery(selectedIndexGalery),
                R.string.text_cancelar, null);
    }

    @Override
    public void onClickIcon(View view, int position) {

    }

    @Override
    public void onClickItem(View view, int position) {

    }

    public void onActivityResultImage() {
        if (compressImage()) {
            insertPhotoToGalery();
            saveImage(photoCapture.getName(), photoCapture.getParent() + File.separator, typeCameraCaptureImage.toLowerCase());
        } else {
            if (view.getContextView() != null) {
                showToast(view.getContextView(),
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

    public void onClickItemMotivo(int position) {
        updateBackgroundSelectListaMotivos(position);
        selectedIndexMotivo = position;
    }

    public void onClickAceptar() {
        if (validateDescarga()) {
            new SaveGestionTask().execute();
        }
    }

    class SaveGestionTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog(view.getContextView(), R.string.text_gestionando_recoleccion);
        }

        @Override
        protected String doInBackground(String... strings) {
            saveGestionGE();
            saveFormularioRecoleccion();
            checkUploadDataSyncImages();
            updateEstadoGestionGE(DescargaRuta.Entrega.FINALIZADO);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            sendOnDescargaFinalizadaReceiver();
            hideProgressDialog();
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
                        rutaPendienteInteractor.deleteMotivos(MotivoDescarga.Tipo.RECOLECTA);
                        saveMotivos(response.getJSONArray("data"));
                        hideProgressDialog();
                        loadMotivos();
                    } else {
                        hideProgressDialog();
                        showToast(view.getContextView(),
                                response.getString("msg_error"), Toast.LENGTH_LONG);
                    }
                } catch (JSONException ex) {
                    ex.printStackTrace();
                    hideProgressDialog();
                    showSnackBar(view.baseFindViewById(R.id.lvMotivos),
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
                showSnackBar(view.baseFindViewById(R.id.lvMotivos),
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

        view.showGaleria(galeria);
    }

    private boolean compressImage() {
        String pathImage = photoCapture.getPath();
        Log.d(TAG, "PATHIMAGE SILICOMPRESSOR: " + pathImage);

        String compressFilePath = "";

        try {
            if (typeCameraCaptureImage.equalsIgnoreCase("Cargo")) {
                compressFilePath = CustomSiliCompressor.with(view.getContextView())
                        .compress(pathImage, 1280.0f, 720.0f, 85);
            } else {
                compressFilePath = CustomSiliCompressor.with(view.getContextView()).compress(pathImage);
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
        view.notifyGaleryAllItemChanged();
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

        view.showListaMotivos(motivoItems);
    }

    private void selectMotivoEnRecolecciones() {
        // Seleccionar el motivo: Recolectado Completo (mot_id => 168)
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
            view.setGuiaElectronica(rutas.get(0).getGuia());
        } else {
            String guias = "";
            for (int i = 0; i < rutas.size(); i++) {
                if (i == rutas.size() - 1) {
                    guias += rutas.get(i).getGuia();
                } else {
                    guias += rutas.get(i).getGuia() + "  •  ";
                }
            }
            view.setGuiaElectronica(guias);
        }
    }

    private void saveGestionGE() {
        Date date = new Date();
        String fecha = new SimpleDateFormat("dd/MM/yyyy").format(date);
        String hora = new SimpleDateFormat("HH:mm:ss").format(date);

        String recoleccion = GuiaGestionada.Recoleccion.LOGISTICA_INVERSA + "";

        if (ModelUtils.isTipoEnvioValija(rutas.get(0).getTipoEnvio())) {
            recoleccion = GuiaGestionada.Recoleccion.VALIJA + "";
        }

        Log.d(TAG, "SAVE DESCARGA: " + rutas.size());
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
                    "",
                    "",
                    "",
                    "0",
                    "0",
                    recoleccion,
                    "",
                    "",
                    "",
                    view.getViewTxtComentarios().getText().toString().trim(),
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

        int sobre, valija, paquete,  otros;

        sobre = view.getViewTxtFrmSobre().getText().toString().trim().isEmpty()
                ? 0 : Integer.parseInt(view.getViewTxtFrmSobre().getText().toString().trim());
        valija = view.getViewTxtFrmValija().getText().toString().trim().isEmpty()
                ? 0 : Integer.parseInt(view.getViewTxtFrmValija().getText().toString().trim());
        paquete = view.getViewTxtFrmPaquete().getText().toString().trim().isEmpty()
                ? 0 : Integer.parseInt(view.getViewTxtFrmPaquete().getText().toString().trim());
        otros = view.getViewTxtFrmOtros().getText().toString().trim().isEmpty()
                ? 0 : Integer.parseInt(view.getViewTxtFrmOtros().getText().toString().trim());

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
                    view.getViewTxtFrmGuiaRecoleccion().getText().toString() + "",
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

        view.notifyGaleryItemRemove(position + 3); // Add 3 for buttons
//        view.notifyGaleryAllItemChanged();
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
                if (validateFormularioRecoleccion()) {
                    if (validateFoto()) {
                        return true;
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
                CommonUtils.vibrateDevice(view.getContextView(), 100);
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
        showSnackBar(view.baseFindViewById(R.id.lvMotivos),
                R.string.activity_detalle_ruta_message_motivo_no_seleccionado,
                Snackbar.LENGTH_LONG);
        CommonUtils.vibrateDevice(view.getContextView(), 100);
        return false;
    }

    private boolean showAlertError(View view, boolean showAlertError) {
        if (showAlertError) {
            view.requestFocus();
        }
        return false;
    }

    private boolean validateFormularioRecoleccion() {
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
            CommonUtils.vibrateDevice(view.getContextView(), 100);
            return false;
        }
        //}

        return true;
    }

    private boolean validateFoto() {
        if (contadorImagenes < 1) {
            BaseModalsView.showSnackBar(view.getViewTxtComentarios(),
                    "Debe tomar una foto del producto como mínimo para realizar la gestión de la recolección.",
                    Snackbar.LENGTH_LONG);
            CommonUtils.vibrateDevice(view.getContextView(), 100);
            return false;
        }

        if (contadorCargo < 1) {
            String msg = "Debe tomar una foto del " +
                    ModelUtils.getNameLblCargoGuia(view.getContextView()).toLowerCase() +
                    " como mínimo para realizar la gestión de la recolección.";
            BaseModalsView.showSnackBar(view.getViewTxtComentarios(), msg, Snackbar.LENGTH_LONG);
            CommonUtils.vibrateDevice(view.getContextView(), 100);
            return false;
        }

        if (contadorDomicilio < 1) {
            BaseModalsView.showSnackBar(view.getViewTxtComentarios(),
                    "Debe tomar una foto del domicilio como mínimo para realizar la gestión de la recolección.",
                    Snackbar.LENGTH_LONG);
            CommonUtils.vibrateDevice(view.getContextView(), 100);
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
        ((DialogFragment) view.getFragment()).dismiss();
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

        view.notifyMotivosAllItemChanged();
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
        LocalBroadcastManager.getInstance(view.getContextView()).sendBroadcast(intent);
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
                CommonUtils.vibrateDevice(view.getContextView(), 100);
            } catch (NullPointerException ex) {
                ex.printStackTrace();
            }
            view.getViewTxtFrmGuiaRecoleccion().setText(intent.getStringExtra("value"));
        }
    };

}