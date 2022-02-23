package com.urbanoexpress.iridio.presenter;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Log;

import com.android.volley.VolleyError;
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
import com.urbanoexpress.iridio.model.interactor.callback.RequestCallback;
import com.urbanoexpress.iridio.model.util.ModelUtils;
import com.urbanoexpress.iridio.ui.FirmarActivity;
import com.urbanoexpress.iridio.ui.QRScannerActivity;
import com.urbanoexpress.iridio.ui.adapter.model.GalleryButtonItem;
import com.urbanoexpress.iridio.ui.adapter.model.GalleryPhotoItem;
import com.urbanoexpress.iridio.ui.adapter.model.GalleryWrapperItem;
import com.urbanoexpress.iridio.ui.model.PiezaRecolectadaItem;
import com.urbanoexpress.iridio.util.CameraUtils;
import com.urbanoexpress.iridio.util.CommonUtils;
import com.urbanoexpress.iridio.util.CustomSiliCompressor;
import com.urbanoexpress.iridio.util.FileUtils;
import com.urbanoexpress.iridio.util.LocationUtils;
import com.urbanoexpress.iridio.util.Preferences;
import com.urbanoexpress.iridio.util.ValidationUtils;
import com.urbanoexpress.iridio.util.constant.LocalAction;
import com.urbanoexpress.iridio.view.RecoleccionGEView;

import org.apache.commons.text.WordUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.urbanoexpress.iridio.ui.model.PiezaRecolectadaItem.Type;

/**
 * Created by mick on 03/08/17.
 */

public class RecoleccionGEPresenter {

    private static final String TAG = RecoleccionGEPresenter.class.getSimpleName();

    private RecoleccionGEView view;
    private RutaPendienteInteractor rutaPendienteInteractor;

    private ArrayList<Ruta> rutas;
    private ArrayList<DescargaRuta> descargaRutas = new ArrayList<>();

    private List<PiezaRecolectadaItem> piezasRecolectadasItems = new ArrayList<>();

    private List<GalleryWrapperItem> galeria = new ArrayList<>();
    private List<GalleryWrapperItem> galeriaFirma = new ArrayList<>();
    private List<GalleryWrapperItem> galeriaCargo = new ArrayList<>();
    private List<GalleryWrapperItem> galeriaDomicilio = new ArrayList<>();

    private File photoCapture;

    private int numVecesGestionado;

    private boolean guiasElectronicas;

    private String typeCameraCaptureImage;

    private STEPS currentStep;
    private SELECTION_MODE selectionMode;

    private enum STEPS {
        GUIAS,
        FORMULARIO,
        FOTOS_PRODUCTO,
        FIRMA_CLIENTE,
        FOTOS_CARGO,
        FOTOS_DOMICILIO
    }

    public enum SELECTION_MODE {
        CHECK,
        SCAN
    }

    public RecoleccionGEPresenter(RecoleccionGEView view, ArrayList<Ruta> rutas,
                                  int numVecesGestionado, boolean guiasElectronicas) {
        this.view = view;
        this.rutas = rutas;
        this.numVecesGestionado = numVecesGestionado;
        this.guiasElectronicas = guiasElectronicas;
        rutaPendienteInteractor = new RutaPendienteInteractor(view.getViewContext());
    }

    public void init() {
        LocalBroadcastManager.getInstance(view.getViewContext())
                .registerReceiver(saveFirmaReceiver, new IntentFilter("OnSaveFirma"));
        LocalBroadcastManager.getInstance(view.getViewContext())
                .registerReceiver(resultScannReceiver,
                        new IntentFilter(LocalAction.LOCAL_ACTION_BARCODE_SCAN_RESULT));

        view.setTextGuiaElectronica(
                rutas.stream().map(Ruta::getGuia).collect(Collectors.joining("  •  ")));

        loadDataRutas();
        setVisibilityFirstStep();
        loadGaleria();
    }

    public void onGalleryButtonClick(int position) {
        switch (currentStep) {
            case FOTOS_PRODUCTO:
            case FOTOS_CARGO:
            case FOTOS_DOMICILIO:
                takePhoto();
                break;
            case FIRMA_CLIENTE:
                takeSigning();
                break;
        }
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

    public void onDestroy() {
        LocalBroadcastManager.getInstance(view.getViewContext())
                .unregisterReceiver(saveFirmaReceiver);
        LocalBroadcastManager.getInstance(view.getViewContext())
                .unregisterReceiver(resultScannReceiver);
    }

    public void onCLickBtnScanBarCode() {
        Intent intent = new Intent(view.getViewContext(), QRScannerActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("typeImpl", QRScannerPresenter.IMPLEMENT.READ_ONLY);
        intent.putExtra("args", bundle);

        view.getViewContext().startActivity(intent);
        ((Activity) view.getViewContext()).
                overridePendingTransition(R.anim.slide_enter_from_bottom, R.anim.not_slide);
    }

    public void processBarra(String barra) {
        if (!validarDatosBarra(barra)) {
            return;
        }

        if (!CommonUtils.validateConnectivity(view.getViewContext())) {
            CommonUtils.vibrateDevice(view.getViewContext(), 100);
            return;
        }

        requestReadBarraRecoleccion(barra);
    }

    public void onEditarPiezaClick(int position) {
        view.showModalConfirmarPiezasRecolectadas(piezasRecolectadasItems.get(position), position);
    }

    public void onEliminarPiezaClick(int position) {
        view.showModalConfirmarEliminacionPieza(
                "Eliminar recolección",
                "¿Estás seguro de eliminar la recolección con barra "
                        + piezasRecolectadasItems.get(position).getBarra() + "?", position);
    }

    public void onSelectionPiezaChanged(int position) {
        view.setTextTotalRecolectados(getTotalPiezasRecolectadas() + " recolectados");
    }

    public void onSelectionModeClick(SELECTION_MODE selectionMode) {
        this.selectionMode = selectionMode;
        piezasRecolectadasItems.clear();
        if (selectionMode == SELECTION_MODE.SCAN) {
            view.setEnabledFormPaquete(false);
            view.setEnabledFormSobre(false);
            view.setEnabledFormValija(false);
            view.setEnabledFormOtros(false);
            view.setSelectionModeScan();
            view.showPiezasRecolectadas(piezasRecolectadasItems);
        } else if (selectionMode == SELECTION_MODE.CHECK) {
            view.setEnabledFormPaquete(true);
            view.setEnabledFormSobre(true);
            view.setEnabledFormValija(true);
            view.setEnabledFormOtros(true);
            view.setSelectionModeCheck();
            view.showGuiasRecolectadas(piezasRecolectadasItems);
            onLoadGuiasElectronicasClick();
        }
        view.setTextTotalRecolectados(getTotalPiezasRecolectadas() + " recolectados");
    }

    public void onSelectAllGuiasCheckedChange(boolean isChecked) {
        if (piezasRecolectadasItems.size() > 0) {
            piezasRecolectadasItems.stream().forEach(item -> item.setSelected(isChecked));
            view.notifyPiezasAllItemChanged();
            view.setTextTotalRecolectados(getTotalPiezasRecolectadas() + " recolectados");
        }
    }

    public void onConfirmarEliminacionPiezaClick(int position) {
        piezasRecolectadasItems.remove(position);
        view.notifyPiezasItemRemoved(position);
        view.setTextTotalRecolectados(getTotalPiezasRecolectadas() + " recolectados");
    }

    public void onBtnSiguienteClick() {
        if (currentStep == STEPS.GUIAS) {
            if (validateSelectedGuias()) {
                view.hideStepGuias();
                view.showStepFormulario();
                currentStep = STEPS.FORMULARIO;
                view.setValueFormPaquete(getTotalPiezasRecolectadas());
                view.setVisibilityMoreActionsMenu(false);
            }
            return;
        }

        if (currentStep == STEPS.FORMULARIO) {
            if (validateFormularioRecoleccion()) {
                view.hideStepFormulario();
                view.showStepFotosProducto();
                view.showGallery(galeria);
                currentStep = STEPS.FOTOS_PRODUCTO;
                view.hideKeyboard();
            }
            return;
        }

        if (currentStep == STEPS.FOTOS_PRODUCTO) {
            if (validateFotosProducto()) {
                view.showStepFirmaCliente();
                view.showGallery(galeriaFirma);
                currentStep = STEPS.FIRMA_CLIENTE;
            }
            return;
        }

        if (currentStep == STEPS.FIRMA_CLIENTE) {
            view.showStepFotosCargo();
            view.showGallery(galeriaCargo);
            currentStep = STEPS.FOTOS_CARGO;
            return;
        }

        if (currentStep == STEPS.FOTOS_CARGO) {
            if (validateFotosCargo()) {
                view.showStepFotosDomicilio();
                view.showGallery(galeriaDomicilio);
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

    public void onConfirmPiezasRecolectadasClick(PiezaRecolectadaItem pieza, int position) {
        piezasRecolectadasItems.get(position).setPiezas(pieza.getPiezas());
        view.notifyPiezasItemChanged(position);
        view.setTextTotalRecolectados(getTotalPiezasRecolectadas() + " recolectados");
    }

    public void onLoadGuiasElectronicasClick() {
        if (CommonUtils.validateConnectivity(view.getViewContext())) {
            view.showProgressDialog(R.string.text_cargando_guias_electronicas);

            final RequestCallback callback = new RequestCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    try {
                        view.dismissProgressDialog();

                        if (response.getBoolean("success")) {
                            handleDataGuiasElectroncias(response.getJSONArray("data"));
                            if (piezasRecolectadasItems.size() > 0) {
                                view.showGuiasRecolectadas(piezasRecolectadasItems);
                            } else {
                                view.showToast(R.string.activity_detalle_ruta_msg_recoleccion_no_hay_guias_electrocnicas);
                            }
                            view.setTextTotalRecolectados(getTotalPiezasRecolectadas() + " recolectados");
                        } else {
                            view.showToast(response.getString("msg_error"));
                        }
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                        view.dismissProgressDialog();
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

            String[] params = {
                    rutas.get(0).getIdGuia(),
                    rutas.get(0).getLineaNegocio(),
                    Preferences.getInstance().getString("idUsuario", "")
            };

            rutaPendienteInteractor.getGuiasElectronicasRecoleccion(params, callback);
        }
    }

    private void requestReadBarraRecoleccion(String barra) {
        view.showProgressDialog("Verificando barra...");

        RequestCallback callback = new RequestCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                view.dismissProgressDialog();

                try {
                    view.clearBarra();

                    if (response.getBoolean("success")) {
                        JSONObject data = response.getJSONObject("data");

                        PiezaRecolectadaItem pieza = new PiezaRecolectadaItem(
                                data.getString("barra"),
                                data.getString("barra"),
                                data.getString("barra_guia"),
                                WordUtils.capitalize(data.getString("estado").toLowerCase()),
                                data.getString("cant_pza"),
                                "",
                                data.getString("tipo_barra").equals("WYB") ? Type.WYB : Type.PCK,
                                true
                        );

                        addToListPiezaRecolectada(pieza);
                    } else {
                        view.showMsgError(response.getString("msg_error"));
                        CommonUtils.vibrateDevice(view.getViewContext(), 100);
                    }
                } catch (JSONException ex) {
                    ex.printStackTrace();
                    handleRequestError(R.string.json_object_exception);
                }
            }

            @Override
            public void onError(VolleyError error) {
                error.printStackTrace();
                handleRequestError(R.string.volley_error_message);
            }
        };

        String[] params = {
                barra,
                rutas.get(0).getIdGuia(),
                Preferences.getInstance().getString("idUsuario", "")};

        RutaPendienteInteractor.readBarraRecoleccion(params, callback);
    }

    private void addToListPiezaRecolectada(PiezaRecolectadaItem pieza) {
        if (existPiezaRecolectada(pieza)) {
            view.showMsgError("El paquete " + pieza.getBarra() + " ya se ha recolectado.");
            CommonUtils.vibrateDevice(view.getViewContext(), 100);
        } else {
            piezasRecolectadasItems.add(pieza);
            view.notifyPiezasItemInserted(piezasRecolectadasItems.size());
            view.showMsgSuccess("El paquete " + pieza.getBarra() + " se ha recolectado exitosamente.");
            view.setTextTotalRecolectados(getTotalPiezasRecolectadas() + " recolectados");
            CommonUtils.playSoundOnScanBarcode(view.getViewContext(), R.raw.scan_barcode_add_pck);
        }
    }

    private void handleDataGuiasElectroncias(JSONArray data) throws JSONException {
        for (int i = 0; i < data.length(); i++) {
            JSONObject jsonObject = data.getJSONObject(i);
            piezasRecolectadasItems.add(new PiezaRecolectadaItem(
                    jsonObject.getString("gui_numero"),
                    jsonObject.getString("guia"),
                    jsonObject.getString("guia"),
                    WordUtils.capitalize(
                            jsonObject.getString("estado").toLowerCase()),
                    jsonObject.getString("piezas"),
                    "",
                    Type.WYB,
                    false
            ));
        }
    }

    private void handleRequestError(int idResError) {
        view.dismissProgressDialog();
        view.clearBarra();
        view.showToast(idResError);
        CommonUtils.vibrateDevice(view.getViewContext(), 100);
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
        if (guiasElectronicas) {
            currentStep = STEPS.GUIAS;
            selectionMode = SELECTION_MODE.SCAN;
            view.showStepGuias();
            view.showPiezasRecolectadas(piezasRecolectadasItems);
            view.setEnabledFormPaquete(false);
            view.setEnabledFormSobre(false);
            view.setEnabledFormValija(false);
            view.setEnabledFormOtros(false);
        } else {
            currentStep = STEPS.FORMULARIO;
            view.showStepFormulario();
            view.setVisibilityMoreActionsMenu(false);
        }
    }

    private void takePhoto() {
        boolean canTakePhoto = false;

        switch (currentStep) {
            case FOTOS_PRODUCTO:
                canTakePhoto = canTakeFotoProducto();
                typeCameraCaptureImage = "Imagen";
                break;
            case FOTOS_CARGO:
                canTakePhoto = canTakeFotoCargo();
                typeCameraCaptureImage = "Cargo";
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

    private void takeSigning() {
        if (canTakeFirmaCliente()) {
            typeCameraCaptureImage = "Firma";
            Activity activity = (Activity) view.getViewContext();
            Intent intent = new Intent(view.getViewContext(), FirmarActivity.class);
            intent.putExtra("pathDirectory" , "Descargas/" + rutas.get(0).getLineaNegocio() +
                    "-" + rutas.get(0).getIdGuia() + "/");
            activity.startActivity(intent);
            activity.overridePendingTransition(R.anim.slide_enter_from_bottom, R.anim.not_slide);
        } else {
            view.showMessageCantTakeSigning();
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

        Log.d(TAG, "SAVE DESCARGA: " + rutas.size());
        for (int i = 0; i < rutas.size(); i++) {
            if (guiasElectronicas) {
                for (int j = 0; j < piezasRecolectadasItems.size(); j++) {
                    if ((selectionMode == SELECTION_MODE.SCAN) ||
                            (selectionMode == SELECTION_MODE.CHECK && piezasRecolectadasItems.get(j).isSelected())) {
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
                                "", "", "",
                                piezasRecolectadasItems.get(j).getPiezas(),
                                "0",
                                String.valueOf(GuiaGestionada.Recoleccion.GUIA_ELECTRONICA),
                                piezasRecolectadasItems.get(j).getBarra(),
                                "",
                                "",
                                view.getTextComentarios(),
                                "0", "",
                                Data.Delete.NO,
                                rutas.get(i).getDataValidate(),
                                numVecesGestionado
                        );
                        guiaGestionada.save();
                    }
                }
            } else {
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
                        "", "", "",
                        "",
                        "",
                        String.valueOf(GuiaGestionada.Recoleccion.SIN_GUIA),
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
                    String.valueOf(view.getValueFormSobre()),
                    String.valueOf(view.getValueFormValija()),
                    String.valueOf(view.getValueFormPaquete()),
                    String.valueOf(view.getValueFormOtros()),
                    String.valueOf(GuiaGestionada.Recoleccion.FORMULARIO_RECOLECCION),
                    view.getTextFormGuiaRecoleccion(),
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

    private int getTotalPiezasRecolectadas() {
        int totalPiezas = 0;
        if (selectionMode == SELECTION_MODE.SCAN) {
            totalPiezas = piezasRecolectadasItems.stream().mapToInt(item -> {
                try {
                    return Integer.parseInt(item.getPiezas());
                } catch (NumberFormatException ex) {
                    return 0;
                }
            }).sum();
        } else if (selectionMode == SELECTION_MODE.CHECK) {
            totalPiezas = piezasRecolectadasItems.stream().mapToInt(item -> {
                if (item.isSelected()) {
                    try {
                        return Integer.parseInt(item.getPiezas());
                    } catch (NumberFormatException ex) { }
                }
                return 0;
            }).sum();
        }
        return totalPiezas;
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

    private boolean validarDatosBarra(String barra) {
        if (barra.trim().length() == 0) {
            view.setErrorBarra(view.getViewContext().getString(R.string.msg_ingrese_barra_correctamente));
            CommonUtils.vibrateDevice(view.getViewContext(), 100);
            return false;
        }
        return true;
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

    private boolean validateSelectedGuias() {
        if (selectionMode == SELECTION_MODE.SCAN) {
            if (piezasRecolectadasItems.size() == 0) {
                view.showSnackBar(R.string.activity_detalle_ruta_msg_necesitas_recolectar);
                CommonUtils.vibrateDevice(view.getViewContext(), 100);
                return false;
            }
        } else if (selectionMode == SELECTION_MODE.CHECK) {
            if (!piezasRecolectadasItems.stream().anyMatch(PiezaRecolectadaItem::isSelected)) {
                view.showSnackBar(R.string.activity_detalle_ruta_msg_seleccione_guia_electronica);
                CommonUtils.vibrateDevice(view.getViewContext(), 100);
                return false;
            }
        }
        return true;
    }

    private boolean existPiezaRecolectada(PiezaRecolectadaItem pieza) {
        if (pieza.getType() == Type.PCK) {
            return piezasRecolectadasItems.stream().anyMatch(
                    item -> item.getBarra().equals(pieza.getBarra()));
        } else {
            return piezasRecolectadasItems.stream().anyMatch(
                    item -> item.getGuiaBarra().equals(pieza.getGuiaBarra()));
        }
    }

    private boolean validateFormularioRecoleccion() {
        /*if (ApiRest.getInstance().getCOUNTRY() == ApiRest.Country.PERU) {
            if (view.getTextFormGuiaRecoleccion().isEmpty()) {
                view.setErrorFormGuiaRecoleccion(view.getViewContext().getString(
                        R.string.act_detalle_ruta_msg_error_ingresa_guia_recoleccion));
                return false;
            }
        }*/

        if (!view.getTextFormGuiaRecoleccion().isEmpty()) {
            String guiaRecoleccion = view.getTextFormGuiaRecoleccion().toUpperCase();

            if (!guiaRecoleccion.matches("^(GRE)\\d{1,10}$")) {
                view.setErrorFormGuiaRecoleccion(view.getViewContext().getString(
                        R.string.act_detalle_ruta_msg_error_guia_recoleccion_incorrecto));
                return false;
            }

            guiaRecoleccion = guiaRecoleccion.replaceAll("(GRE)+", "");

            if (!ValidationUtils.validateDigitoValido(guiaRecoleccion)) {
                view.setErrorFormGuiaRecoleccion(view.getViewContext().getString(
                        R.string.act_detalle_ruta_msg_error_guia_recoleccion_incorrecto));
                return false;
            }
        }

        if (!(view.getValueFormSobre() > 0 || view.getValueFormValija() > 0
                || view.getValueFormPaquete() > 0 || view.getValueFormOtros() > 0)) {
            view.showSnackBar(R.string.act_detalle_ruta_msg_error_frm_rec_ingresar_cantidad);
            CommonUtils.vibrateDevice(view.getViewContext(), 100);
            return false;
        }
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

    private boolean validateFotosCargo() {
        if ((galeriaCargo.size() - 1) < 1) {
            view.showSnackBar("Debe tomar una foto del " +
                    ModelUtils.getNameLblCargoGuia(view.getViewContext()).toLowerCase() +
                    " como mínimo para realizar la gestión de la recolección.");
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

    private boolean canTakeFirmaCliente() {
        return galeriaFirma.size() <= 1;
    }

    private boolean canTakeFotoCargo() {
        return galeriaCargo.size() <= 10;
    }

    private boolean canTakeFotoDomicilio() {
        return galeriaDomicilio.size() <= 10;
    }

    private void showGalerias() {
        List<Imagen> images = selectAllImages();

        galeria.clear();
        galeriaCargo.clear();
        galeriaFirma.clear();
        galeriaDomicilio.clear();

        GalleryButtonItem buttonItem = new GalleryButtonItem(R.drawable.ic_camera_grey);
        galeria.add(buttonItem);
        galeriaCargo.add(buttonItem);
        galeriaDomicilio.add(buttonItem);

        buttonItem = new GalleryButtonItem(R.drawable.ic_firma_grey);
        galeriaFirma.add(buttonItem);

        if (images.size() > 0) {
            for (Imagen imagen : images) {
                GalleryPhotoItem item = new GalleryPhotoItem(imagen.getPath() + imagen.getName());

                switch (imagen.getAnotaciones().toLowerCase()) {
                    case "imagen":
                        galeria.add(item);
                        break;
                    case "firma":
                        galeriaFirma.add(item);
                        break;
                    case "cargo":
                        galeriaCargo.add(item);
                        break;
                    case "domicilio":
                        galeriaDomicilio.add(item);
                        break;
                }
            }
        }
    }

    private void insertImageToGallery() {
        Log.d(TAG, "Image Path: " + photoCapture.getAbsolutePath());
        GalleryPhotoItem item = new GalleryPhotoItem(photoCapture.getAbsolutePath());

        if (typeCameraCaptureImage.equalsIgnoreCase("Imagen")) {
            galeria.add(item);
            view.notifyGalleryItemInserted(galeria.size());
        } else if (typeCameraCaptureImage.equalsIgnoreCase("Firma")) {
            galeriaFirma.add(item);
            view.notifyGalleryItemInserted(galeriaFirma.size());
        } else if (typeCameraCaptureImage.equalsIgnoreCase("Cargo")) {
            galeriaCargo.add(item);
            view.notifyGalleryItemInserted(galeriaCargo.size());
        } else if (typeCameraCaptureImage.equalsIgnoreCase("Domicilio")) {
            galeriaDomicilio.add(item);
            view.notifyGalleryItemInserted(galeriaDomicilio.size());
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
                break;
            case FIRMA_CLIENTE:
                FileUtils.deleteFile(((GalleryPhotoItem) galeriaFirma.get(position)).getPathImage());

                indexSplit = ((GalleryPhotoItem) galeriaFirma.get(position)).getPathImage().lastIndexOf("/") + 1;
                name = ((GalleryPhotoItem) galeriaFirma.get(position)).getPathImage().substring(indexSplit);
                path = ((GalleryPhotoItem) galeriaFirma.get(position)).getPathImage().substring(0, indexSplit);

                galeriaFirma.remove(position);
                break;
            case FOTOS_CARGO:
                FileUtils.deleteFile(((GalleryPhotoItem) galeriaCargo.get(position)).getPathImage());

                indexSplit = ((GalleryPhotoItem) galeriaCargo.get(position)).getPathImage().lastIndexOf("/") + 1;
                name = ((GalleryPhotoItem) galeriaCargo.get(position)).getPathImage().substring(indexSplit);
                path = ((GalleryPhotoItem) galeriaCargo.get(position)).getPathImage().substring(0, indexSplit);

                galeriaCargo.remove(position);
                break;
            case FOTOS_DOMICILIO:
                FileUtils.deleteFile(((GalleryPhotoItem) galeriaDomicilio.get(position)).getPathImage());

                indexSplit = ((GalleryPhotoItem) galeriaDomicilio.get(position)).getPathImage().lastIndexOf("/") + 1;
                name = ((GalleryPhotoItem) galeriaDomicilio.get(position)).getPathImage().substring(indexSplit);
                path = ((GalleryPhotoItem) galeriaDomicilio.get(position)).getPathImage().substring(0, indexSplit);

                galeriaDomicilio.remove(position);
                break;
        }

        view.notifyGalleryItemRemoved(position);

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

    /**
     * Broadcast
     *
     * {@link FirmarActivity#sendOnSaveFirmaReceiver}
     */
    private final BroadcastReceiver saveFirmaReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            photoCapture = (File) intent.getSerializableExtra("file");
            insertImageToGallery();
            saveImage(photoCapture.getName(), photoCapture.getParent() + File.separator, "firma");
        }
    };

    /**
     * Broadcast
     *
     * {@link CodeScannerImpl#sendOnResultScannListener}
     */
    private final BroadcastReceiver resultScannReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                CommonUtils.vibrateDevice(view.getViewContext(), 100);
            } catch (NullPointerException ex) {
                ex.printStackTrace();
            }
            if (currentStep == STEPS.GUIAS) {
                requestReadBarraRecoleccion(intent.getStringExtra("value"));
            } else if (currentStep == STEPS.FORMULARIO) {
                view.setTextFormGuiaRecoleccion(intent.getStringExtra("value"));
            }
        }
    };

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
    private class VerifyExistImagesOnDeviceTask extends AsyncTask<Void, Integer, Boolean> {

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