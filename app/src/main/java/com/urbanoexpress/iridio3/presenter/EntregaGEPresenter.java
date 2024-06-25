package com.urbanoexpress.iridio3.presenter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.VolleyError;
import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.google.android.gms.location.LocationServices;
import com.orm.util.NamingHelper;
import com.urbanoexpress.iridio3.AsyncTaskCoroutine;
import com.urbanoexpress.iridio3.R;
import com.urbanoexpress.iridio3.application.AndroidApplication;
import com.urbanoexpress.iridio3.data.local.PreferencesHelper;
import com.urbanoexpress.iridio3.model.entity.Data;
import com.urbanoexpress.iridio3.model.entity.DescargaRuta;
import com.urbanoexpress.iridio3.model.entity.GuiaGestionada;
import com.urbanoexpress.iridio3.model.entity.Imagen;
import com.urbanoexpress.iridio3.model.entity.MotivoDescarga;
import com.urbanoexpress.iridio3.model.entity.Pieza;
import com.urbanoexpress.iridio3.model.entity.Ruta;
import com.urbanoexpress.iridio3.model.entity.TipoDireccion;
import com.urbanoexpress.iridio3.model.interactor.RutaPendienteInteractor;
import com.urbanoexpress.iridio3.model.interactor.callback.RequestCallback;
import com.urbanoexpress.iridio3.model.util.ModelUtils;
import com.urbanoexpress.iridio3.ui.FirmarActivity;
import com.urbanoexpress.iridio3.ui.QRScannerActivity;
import com.urbanoexpress.iridio3.ui.adapter.PiezasAdapter;
import com.urbanoexpress.iridio3.ui.adapter.PremiosAdapter;
import com.urbanoexpress.iridio3.ui.adapter.model.GalleryButtonItem;
import com.urbanoexpress.iridio3.ui.adapter.model.GalleryPhotoItem;
import com.urbanoexpress.iridio3.ui.adapter.model.GalleryWrapperItem;
import com.urbanoexpress.iridio3.ui.dialogs.GestionarGENoRecolectadasDialog;
import com.urbanoexpress.iridio3.ui.dialogs.GestionarRecoleccionGuiaValijaDilog;
import com.urbanoexpress.iridio3.ui.dialogs.ObservarGestionEntregaDialog;
import com.urbanoexpress.iridio3.ui.helpers.ModalHelper;
import com.urbanoexpress.iridio3.ui.model.MotivoDescargaItem;
import com.urbanoexpress.iridio3.ui.model.PiezaItem;
import com.urbanoexpress.iridio3.ui.model.PremioItem;
import com.urbanoexpress.iridio3.util.CameraUtils;
import com.urbanoexpress.iridio3.util.CommonUtils;
import com.urbanoexpress.iridio3.util.CustomSiliCompressor;
import com.urbanoexpress.iridio3.util.FileUtils;
import com.urbanoexpress.iridio3.util.FileUtilss;
import com.urbanoexpress.iridio3.util.LocationUtils;
import com.urbanoexpress.iridio3.util.Preferences;
import com.urbanoexpress.iridio3.util.ValidationUtils;
import com.urbanoexpress.iridio3.util.constant.Country;
import com.urbanoexpress.iridio3.util.constant.LocalAction;
import com.urbanoexpress.iridio3.view.BaseModalsView;
import com.urbanoexpress.iridio3.view.DescargaEntregaView;

import org.apache.commons.lang3.text.WordUtils;
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
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by mick on 14/07/16.
 */

public class EntregaGEPresenter implements PiezasAdapter.OnPiezaListener,
        PremiosAdapter.OnPremioListener {

    private static final String TAG = EntregaGEPresenter.class.getSimpleName();

    private DescargaEntregaView view;
    private RutaPendienteInteractor rutaPendienteInteractor;

    private List<MotivoDescarga> dbMotivoDescargas = Collections.emptyList();
    private List<MotivoDescargaItem> motivoItems = new ArrayList<>();

    private List<PiezaItem> piezaItems = new ArrayList<>();
    private List<PremioItem> premioItems = new ArrayList<>();

    private List<TipoDireccion> dbTipoDireccion = Collections.emptyList();

    private List<GalleryWrapperItem> galeria = new ArrayList<>();
    private List<GalleryWrapperItem> galeriaFirma = new ArrayList<>();
    private List<GalleryWrapperItem> galeriaCargo = new ArrayList<>();//aqui

    //yape
    private List<GalleryWrapperItem> galeriaComprobantePago = new ArrayList<>();//aqui
    private List<GalleryWrapperItem> galeriaDomicilio = new ArrayList<>();

    private ArrayList<DescargaRuta> descargaRutas = new ArrayList<>();
    private ArrayList<Ruta> rutas;//Es la ruta seleccionada, usualmente contiene un solo item

    private File photoCapture;

    private int minFotosProducto = 0;

    private int selectedIndexMotivo = -1;

    private int selectedIndexTipoDocIdentificacion = 0;

    private int selectedIndexTipoDireccion = -1;

    private int selectedIndexTipoMedioPago = -1;

    private int numVecesGestionado = 0;

    private STEPS currentStep;

    private String dirPathPhotos = "";

    private String typeCameraCaptureImage;

    private boolean observarEntrega = false;

    private boolean pckReadFromScanner = false;

    private String idMotivoObservacionEntrega = "", comentarioObservacionEntrega = "";

    private int tipoMotivo;

    private final String CHK_ENTREGA = "9";
    private final String CHK_ENTREGA_DEVOLUCION = "10";
    private final int REQUEST_IMAGE_GALLERY = 200;

    private enum STEPS {
        PIEZAS,
        PRODUCTOS_A_ENTREGAR,
        TIPO_ENTREGA,
        DATOS_ENTREGA,
        FOTOS_PRODUCTO,
        FIRMA_CLIENTE,
        FOTOS_CARGO,
        FOTOS_DOMICILIO,

        FOTOS_COMPROBANTE_PAGO,

        YAPE_QR
    }

    public EntregaGEPresenter(DescargaEntregaView view, ArrayList<Ruta> rutas, int numVecesGestionado) {
        this.view = view;
        this.rutas = rutas;
        this.numVecesGestionado = numVecesGestionado;
        rutaPendienteInteractor = new RutaPendienteInteractor(view.getViewContext());
    }

    public void init() {
        LocalBroadcastManager.getInstance(view.getViewContext())
                .registerReceiver(saveFirmaReceiver, new IntentFilter("OnSaveFirma"));
        LocalBroadcastManager.getInstance(view.getViewContext())
                .registerReceiver(dataObservacionGestionEntregaReceiver,
                        new IntentFilter(LocalAction.DATA_OBSERVACION_GESTION_ENTREGA));
        LocalBroadcastManager.getInstance(view.getViewContext())
                .registerReceiver(resultScannReceiver,
                        new IntentFilter(LocalAction.LOCAL_ACTION_BARCODE_SCAN_RESULT));

        tipoMotivo = ModelUtils.getTipoMotivoDescarga(rutas.get(0).getTipoEnvio(), 1);

        setMinFotosProducto();

        view.setTextGuiaElectronica(
                rutas.stream().map(Ruta::getGuia).collect(Collectors.joining("  •  ")));

        loadDataRutas();
        loadDataPiezas();
        loadDataProductosAEntregar();
        loadTipoDocIdentificacion();
        loadTipoDireccion();
        loadTipoMedioPago();
        loadGaleria();

        setVisibilityFirstStep();
        setTitleStepFotoCargo();
        setVisibilityInputTipoDocIndentidad();
        setVisibilityInputDatosCliente();
        setVisibilityInputTipoDireccion();
        setVisibilityInputTipoMedioPago();
        setVisibilityInputObservarEntrega();

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

    private void requestYapeQR() {
        view.showProgressDialog("Cargando QR");
        HashMap<String, String> requestParams = new HashMap<String, String>();
        requestParams.put("vp_man_id_det", rutas.get(0).getIdServicio());
        rutaPendienteInteractor.getQuiaYapeQR(requestParams, new RequestCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                view.dismissProgressDialog();
                try {
                    JSONObject ar = response.getJSONArray("data").getJSONObject(0);
                    String QR = ar.getString("qr");
                    view.displayQR(QR);
                    view.setTextImporte(ModelUtils.getSimboloMoneda(view.getViewContext()) + " " + rutas.get(0).getImporte());
                } catch (Exception e) {
                    Log.e(TAG, "onSuccess: ", e);
                    BaseModalsView.showToast(view.getViewContext(),
                            "Hubo un error, ", Toast.LENGTH_LONG);
                }

            }

            @Override
            public void onError(VolleyError error) {
                BaseModalsView.showToast(view.getViewContext(),
                        "Hubo un error, ", Toast.LENGTH_LONG);
            }
        });
    }

    public void onBtnScanPCKClick() {
        Intent intent = new Intent(view.getViewContext(), QRScannerActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("typeImpl", QRScannerPresenter.IMPLEMENT.CONTINUOUS);
        intent.putExtra("args", bundle);
        view.getViewContext().startActivity(intent);
        ((AppCompatActivity) view.getViewContext()).
                overridePendingTransition(R.anim.slide_enter_from_bottom, R.anim.not_slide);
    }

    public void onGalleryButtonClick(int position) {
        switch (currentStep) {
            case FOTOS_PRODUCTO:
            case FOTOS_DOMICILIO:
                if (currentStep == STEPS.FOTOS_PRODUCTO) {
                    typeCameraCaptureImage = "Imagen";
                } else {
                    typeCameraCaptureImage = "Domicilio";
                }
                switch (((GalleryButtonItem) galeria.get(position)).getAction()) {
                    case GalleryButtonItem.Action.CAMERA:
                        takePhoto();
                        break;
                    case GalleryButtonItem.Action.GALLERY:
                        selectPhotoFromGallery();
                        break;
                }
                break;
            case FOTOS_CARGO:
                typeCameraCaptureImage = "Cargo";
                takePhoto();
                break;
            case FIRMA_CLIENTE:
                typeCameraCaptureImage = "Firma";
                takeSigning();
                break;
            case FOTOS_COMPROBANTE_PAGO:
                typeCameraCaptureImage = "Pago";
                takePhoto();
                break;
        }
    }

    public void onGalleryDeleteImageClick(int position) {
        deleteImageFromGallery(position);
    }

    @Override
    public void onPremioClick(int position) {
        boolean selected = !premioItems.get(position).isSelected();
        premioItems.get(position).setSelected(selected);
        view.notifyPremioItemChanged(position);
    }

    //check change
    @Override
    public void onPiezaClick(int position) {
        if (piezaItems.get(position).isSelectable()) {
            boolean selected = !piezaItems.get(position).isSelected();
            piezaItems.get(position).setSelected(selected);
            view.notifyPiezaItemChanged(position);

            setVisibilityContainerMsgEntregaParcial();
        } else if (piezaItems.get(position).isBarcodeScanningIsMandatory()) {
            view.showSnackBar(R.string.activity_detalle_ruta_msg_scan_pieza);
            CommonUtils.vibrateDevice(view.getViewContext(), 100);
        } else {
            view.showSnackBar(R.string.activity_detalle_ruta_msg_no_puede_seleccionar_pieza);
            CommonUtils.vibrateDevice(view.getViewContext(), 100);
        }
    }

    public void onChkSelectAllProductos(boolean isChecked) {
        if (premioItems.size() > 0) {
            for (int i = 0; i < premioItems.size(); i++) {
                premioItems.get(i).setSelected(isChecked);
            }
            view.notifyPremiosAllItemChanged();
        }
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

    public void onResume() {
        if (pckReadFromScanner) {
            pckReadFromScanner = false;
            view.notifyPiezasAllItemChanged();
        }
    }

    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        LocalBroadcastManager.getInstance(view.getViewContext())
                .unregisterReceiver(saveFirmaReceiver);
        LocalBroadcastManager.getInstance(view.getViewContext())
                .unregisterReceiver(dataObservacionGestionEntregaReceiver);
        LocalBroadcastManager.getInstance(view.getViewContext())
                .unregisterReceiver(resultScannReceiver);
    }

    public void onClickItemMotivo(int position) {
        // Si selecciona el motivo (ENTREGA A TITULAR)
        // pintar el nombre del cliente
        if (rutas.size() == 1) {
            if (dbMotivoDescargas.get(position).getCodigo().equals("ET") ||
                    dbMotivoDescargas.get(position).getCodigo().equals("PT")) {
                view.setTextNombre(rutas.get(0).getContacto());
            } else {
                view.setTextNombre("");
            }

            view.setTextDNI("");
            view.setTextComentarios("");
        }

        updateBackgroundSelectListaMotivos(position);

        selectedIndexMotivo = position;
    }

    public void onCheckedChangeObservarEntrega(boolean isChecked) {
        observarEntrega = isChecked;
    }

    @SuppressLint("MissingPermission")
    public void gestionarGuia() {
        if (!validateFechaDispositivo()) {
            return;
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

    //Here we set the step order
    public void onBtnSiguienteClick() {
        if (currentStep == STEPS.PIEZAS) {
            if (validateSelectedPiezas()) {
                if (premioItems.size() > 0) {
                    view.setVisibilityBoxStepPiezas(View.GONE);
                    view.setVisibilityBoxStepProductosEntregados(View.VISIBLE);
                    view.notifyPremiosAllItemChanged();
                    currentStep = STEPS.PRODUCTOS_A_ENTREGAR;
                } else {
                    tipoMotivo = getTipoMotivoParaPiezas();
                    view.setVisibilityBoxStepPiezas(View.GONE);
                    view.setVisibilityBoxStepTipoEntrega(View.VISIBLE);
                    loadMotivos();
                    currentStep = STEPS.TIPO_ENTREGA;
                }
                view.setVisibilityWarningScanBarcodeMandatory(View.GONE);
            }
            return;
        }

        if (currentStep == STEPS.PRODUCTOS_A_ENTREGAR) {
            if (validateSelectedProductosEntregados()) {
                view.setVisibilityBoxStepProductosEntregados(View.GONE);
                view.setVisibilityBoxStepTipoEntrega(View.VISIBLE);
                loadMotivos();
                currentStep = STEPS.TIPO_ENTREGA;
            }
            return;
        }

        if (currentStep == STEPS.TIPO_ENTREGA) {
            if (validateSelectedMotivo()) {
                view.setVisibilityBoxStepTipoEntrega(View.GONE);
                view.setVisibilityBoxStepDatosEntrega(View.VISIBLE);
                currentStep = STEPS.DATOS_ENTREGA;
            }
            return;
        }

        if (currentStep == STEPS.DATOS_ENTREGA) {
            if (validateDatosEntrega()) {
                view.setVisibilityBoxStepDatosEntrega(View.GONE);

                if (isMedioPagoYape()) {
                    showYapeQRStep();
                } else if (isMedioPagoEfectivo()) {
                    showPagoDialog();
                } else if (minFotosProducto == 0) {
                    view.setVisibilityBoxStepFotoCargoEntrega(View.VISIBLE);
                    view.notifyGaleriaCargoAllItemChanged();
                    currentStep = STEPS.FOTOS_CARGO;
                } else if (rutas.get(0).getTipoEnvio().toUpperCase().equals(Ruta.TipoEnvio.LIQUIDACION)) {
                    view.setVisibilityBoxStepFotoCargoEntrega(View.VISIBLE);
                    view.notifyGaleriaCargoAllItemChanged();
                    view.setTextBtnSiguiente("Gestionar");
                    currentStep = STEPS.FOTOS_CARGO;
                } else {
                    view.setVisibilityBoxStepFotosEntrega(View.VISIBLE);
                    view.notifyGaleriaFotosAllItemChanged();
                    currentStep = STEPS.FOTOS_PRODUCTO;
                }
                view.hideKeyboard();
            }
            return;
        }

        if (currentStep == STEPS.FOTOS_PRODUCTO) {
            if (validateFotosProducto()) {
                view.setVisibilityBoxStepFotosEntrega(View.GONE);

                boolean isGuiaDevolucion = rutas.get(0).getTipoEnvio().toUpperCase().equals(Ruta.TipoEnvio.DEVOLUCION);

                if (isGuiaDevolucion || isRequiredFirmaCliente()) {
                    view.setVisibilityBoxStepFirmaEntrega(View.VISIBLE);
                    view.notifyGaleriaFirmaAllItemChanged();
                    currentStep = STEPS.FIRMA_CLIENTE;
                } else if (hasHabilitantes()) {
                    view.setVisibilityBoxStepFotoCargoEntrega(View.VISIBLE);
                    view.notifyGaleriaCargoAllItemChanged();
                    currentStep = STEPS.FOTOS_CARGO;
                } else {
                    view.setVisibilityBoxStepFotosDomicilio(View.VISIBLE);
                    view.notifyGaleriaDomicilioAllItemChanged();
                    view.setTextBtnSiguiente("Gestionar");
                    currentStep = STEPS.FOTOS_DOMICILIO;
                }
            }
            return;
        }

        if (currentStep == STEPS.YAPE_QR) {
            view.setVisibilityBoxYapeQR(View.GONE);

            view.setVisibilityBoxYapeQR(View.GONE);
            view.setVisibilityBoxStepFotoComprobantePago(View.VISIBLE);
            currentStep = STEPS.FOTOS_COMPROBANTE_PAGO;
            return;
        }

        if (currentStep == STEPS.FIRMA_CLIENTE) {
            boolean isGuiaDevolucion = rutas.get(0).getTipoEnvio().toUpperCase().equals(Ruta.TipoEnvio.DEVOLUCION);

            if (isGuiaDevolucion || validateFirmaCliente()) {
                view.setVisibilityBoxStepFirmaEntrega(View.GONE);
                view.setVisibilityBoxStepFotoCargoEntrega(View.VISIBLE);
                view.notifyGaleriaCargoAllItemChanged();
                currentStep = STEPS.FOTOS_CARGO;
            }
            return;
        }

        if (currentStep == STEPS.FOTOS_CARGO) {

            if (rutas.get(0).getTipoEnvio().toUpperCase().equals(Ruta.TipoEnvio.LIQUIDACION)) {
                if (validateFotosCargo()) {
                    gestionarGuia();
                }
                return;
            }

            if (rutas.get(0).getTipoEnvio().toUpperCase().equals(Ruta.TipoEnvio.DEVOLUCION)) {
                view.setVisibilityBoxStepFotoCargoEntrega(View.GONE);
                view.setVisibilityBoxStepFotosDomicilio(View.VISIBLE);
                view.notifyGaleriaDomicilioAllItemChanged();
                view.setTextBtnSiguiente("Gestionar");
                currentStep = STEPS.FOTOS_DOMICILIO;
                return;
            }

            boolean hasHabilitantes = hasHabilitantes();

            if (!hasHabilitantes || validateFotosCargo()) {
                view.setVisibilityBoxStepFotoCargoEntrega(View.GONE);
                view.setVisibilityBoxStepFotosDomicilio(View.VISIBLE);
                view.notifyGaleriaDomicilioAllItemChanged();
                view.setTextBtnSiguiente("Gestionar");
                currentStep = STEPS.FOTOS_DOMICILIO;
                return;
            }


        }

        if (currentStep == STEPS.FOTOS_COMPROBANTE_PAGO) {
            if (minFotosProducto == 0) {
                view.setVisibilityBoxStepFotoComprobantePago(View.GONE);
                view.setVisibilityBoxStepFotoCargoEntrega(View.VISIBLE);
                view.notifyGaleriaCargoAllItemChanged();
                currentStep = STEPS.FOTOS_CARGO;
            } else if (rutas.get(0).getTipoEnvio().toUpperCase().equals(Ruta.TipoEnvio.LIQUIDACION)) {
                view.setVisibilityBoxStepFotoComprobantePago(View.GONE);
                view.setVisibilityBoxStepFotoCargoEntrega(View.VISIBLE);
                view.notifyGaleriaCargoAllItemChanged();
                view.setTextBtnSiguiente("Gestionar");
                currentStep = STEPS.FOTOS_CARGO;
            } else if (validateFotosComprobantePago()) {
                view.setVisibilityBoxStepFotoComprobantePago(View.GONE);
                view.setVisibilityBoxStepFotosEntrega(View.VISIBLE);
                view.notifyGaleriaFotosAllItemChanged();
                currentStep = STEPS.FOTOS_PRODUCTO;
            }
            return;
        }

        if (currentStep == STEPS.FOTOS_DOMICILIO) {
            if (validateFotosDomicilio()) {
                gestionarGuia();
            }
        }
    }

    public void onSelectedTipoDocIdentificacion(int position) {
        selectedIndexTipoDocIdentificacion = position;
    }

    public void onSelectedTipoDireccion(int position) {
        selectedIndexTipoDireccion = position;
    }

    public void onSelectedTipoMedioPago(int position) {
        selectedIndexTipoMedioPago = position;
    }

    private void getListaMotivo() {
        view.showProgressDialog(R.string.text_actualizando_motivos);
        final RequestCallback callback = new RequestCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                view.dismissProgressDialog();

                try {
                    if (response.getBoolean("success")) {
                        rutaPendienteInteractor.deleteMotivos(tipoMotivo);
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
                String.valueOf(tipoMotivo),
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

    private void takeSigning() {
        if (canTakeFirmaCliente()) {
            Activity activity = (Activity) view.getViewContext();
            Intent intent = new Intent(view.getViewContext(), FirmarActivity.class);
            intent.putExtra("pathDirectory", dirPathPhotos);
            activity.startActivity(intent);
            activity.overridePendingTransition(R.anim.slide_enter_from_bottom, R.anim.not_slide);
        } else {
            view.showMessageCantTakeSigning();
        }
    }

    private void saveMotivos(JSONArray data) throws JSONException {
        JSONObject jsonObject;
        for (int i = 0; i < data.length(); i++) {
            jsonObject = data.getJSONObject(i);
            MotivoDescarga motivo = new MotivoDescarga(
                    Preferences.getInstance().getString("idUsuario", ""),
                    tipoMotivo,
                    jsonObject.getString("mot_id"),
                    jsonObject.getString("codigo"),
                    jsonObject.getString("descri"),
                    jsonObject.getString("linea")
            );
            motivo.save();
        }
    }

    private void loadMotivos() {
        dbMotivoDescargas = rutaPendienteInteractor.selectAllMotivos(tipoMotivo,
                rutas.get(0).getLineaNegocio());

        selectedIndexMotivo = -1;

        motivoItems = new ArrayList<>();
        MotivoDescargaItem item;

        for (MotivoDescarga motivo : dbMotivoDescargas) {
            item = new MotivoDescargaItem(motivo.getDescripcion(), false);
            motivoItems.add(item);
        }

        view.showListaMotivos(motivoItems);
    }

    private void loadDataPiezas() {

        boolean barcodeScanningIsMandatory = true;

        for (Ruta ruta : rutas) {
            List<Pieza> piezas = rutaPendienteInteractor.selectPiezas(ruta.getIdServicio(),
                    ruta.getLineaNegocio());

            boolean selected = piezas.size() == 1;
            barcodeScanningIsMandatory &= (ruta.getFlagScanPck().equals("1") && piezas.size() < 9);

            for (Pieza pieza : piezas) {
                boolean selectable = true;
                if (pieza.getChkEstado().equals(CHK_ENTREGA)) selectable = false;
                if (pieza.getChkEstado().equals(CHK_ENTREGA_DEVOLUCION)) selectable = false;
                if (pieza.getChkEstado().equals("6") && pieza.getEstadoManifiesto() != 1)
                    selectable = false;
                if (barcodeScanningIsMandatory) selectable = false;

                piezaItems.add(new PiezaItem(
                        pieza.getIdPieza(),
                        pieza.getIdServicioGuia(),
                        pieza.getBarra(),
                        pieza.getChkEstado(),
                        pieza.getDescripcionEstado().toLowerCase(),
                        pieza.getFechaEstado(),
                        pieza.getEstadoManifiesto() == 1,
                        selected,
                        selectable,
                        barcodeScanningIsMandatory));
            }
        }

        view.showPiezas(piezaItems);

        if (barcodeScanningIsMandatory) {
            view.setVisibilityWarningScanBarcodeMandatory(View.VISIBLE);
        }
    }

    private void loadDataProductosAEntregar() {
        for (int i = 0; i < rutas.size(); i++) {
            if (rutas.get(i).getPremiosGestionGuia() != null) {
                if (!rutas.get(i).getPremiosGestionGuia().isEmpty()) {
                    String[] productos = rutas.get(i).getPremiosGestionGuia().split(",");

                    for (int j = 0; j < productos.length; j++) {
                        String[] producto = productos[j].split("\\|");

                        if (producto.length == 3) { // Validar que esten todos los datos del producto
                            premioItems.add(
                                    new PremioItem(
                                            rutas.get(i).getIdRuta(),
                                            rutas.get(i).getIdGuia(),
                                            producto[0].trim(),
                                            producto[1].trim(),
                                            producto[2].trim(),
                                            false
                                    )
                            );
                        }
                    }
                }
            }
        }

        view.showPremios(premioItems);
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

    private void loadTipoDocIdentificacion() {
        if (rutas.get(0).getLineaNegocio().equals("2")) {
            ArrayList<String> tipoDocIdentificacionItems = new ArrayList<>();
            int country = new PreferencesHelper(view.getViewContext()).getCountry();
            switch (country) {
                case Country.CHILE:
                case Country.ECUADOR:
                    tipoDocIdentificacionItems.add("Cédula de Identidad/CI");
                    break;
                case Country.PERU:
                    tipoDocIdentificacionItems.add("Documento Nacional de Identidad/DNI");
                    break;
            }

            tipoDocIdentificacionItems.add("Otros Doc. de Identidad");

            view.showTipoDocIdentificacion(tipoDocIdentificacionItems);
        }
    }

    private void loadTipoDireccion() {
        if (rutas.get(0).getLineaNegocio().equals("2")) {
            ArrayList<String> tipoDireccionItems = new ArrayList<>();

            dbTipoDireccion = rutaPendienteInteractor.selectAllTipoDireccion();

            for (TipoDireccion tipoDireccion : dbTipoDireccion) {
                tipoDireccionItems.add(WordUtils.capitalize(tipoDireccion.getDescripcion().toLowerCase()));
            }

            view.showTipoDireccion(tipoDireccionItems);
        }
    }

    private void loadTipoMedioPago() {//llamdado primero
        if (isMedioPagoNoDefinido()) {
            ArrayList<String> tipoMedioPagoItems = new ArrayList<>();

            tipoMedioPagoItems.add("Seleccione");
            tipoMedioPagoItems.add("Efectivo");
            tipoMedioPagoItems.add("Tarjeta");

            view.showTipoMedioPago(tipoMedioPagoItems);
        }
    }

    private void saveImage(String fileName, String directory, String anotaciones) {
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
        Log.d(TAG, "anotaciones: " + anotaciones);
    }

    private void saveGestionGE(String fecha, String hora) {
        String tipoDireccion = "";

        if (rutas.get(0).getLineaNegocio().equals("2")) {
            tipoDireccion = dbTipoDireccion.get(selectedIndexTipoDireccion).getIdTipoDirecion();
        }

        String tipoMedioPago = "0";

        if (isMedioPagoNoDefinido()) {//Luego de fotos de domicilio (4?)
            tipoMedioPago = selectedIndexTipoMedioPago + "";
        }

        String comentarioGestionEntrega = view.getTextComentarios();

        if (observarEntrega) {
            comentarioGestionEntrega = comentarioObservacionEntrega;
        } else {
            idMotivoObservacionEntrega = "0";
            comentarioObservacionEntrega = "";
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
                    view.getTextNombre(),
                    view.getTextDNI(),
                    view.getTextNumVoucher(),
                    "", "", "", "",
                    tipoDireccion,
                    tipoMedioPago,
                    comentarioGestionEntrega,
                    idMotivoObservacionEntrega, comentarioObservacionEntrega,
                    Data.Delete.NO,
                    rutas.get(i).getDataValidate(),
                    numVecesGestionado
            );
            guiaGestionada.save();
        }
    }

    private void savePiezas(String fecha, String hora) {
        for (int i = 0; i < piezaItems.size(); i++) {
            if (piezaItems.get(i).isSelected()) {
                if (tipoMotivo == MotivoDescarga.Tipo.ENTREGA_PARCIAL) {
                    Pieza pieza = rutaPendienteInteractor.selectPieza(piezaItems.get(i).getIdPieza(),
                            piezaItems.get(i).getIdServicioGuia(), rutas.get(0).getLineaNegocio());
                    if (pieza != null) {
                        pieza.setChkEstado(CHK_ENTREGA);
                        pieza.setDescripcionEstado("entregado");
                        pieza.save();
                    }
                } else if (tipoMotivo == MotivoDescarga.Tipo.ENTREGA_DEVOLUCION_PARCIAL) {
                    Pieza pieza = rutaPendienteInteractor.selectPieza(piezaItems.get(i).getIdPieza(),
                            piezaItems.get(i).getIdServicioGuia(), rutas.get(0).getLineaNegocio());
                    if (pieza != null) {
                        pieza.setChkEstado(CHK_ENTREGA_DEVOLUCION);
                        pieza.setDescripcionEstado("entregado");
                        pieza.save();
                    }
                }

                GuiaGestionada guiaGestionada = new GuiaGestionada(
                        Preferences.getInstance().getString("idUsuario", ""),
                        piezaItems.get(i).getIdServicioGuia(),
                        "pck",
                        rutas.get(0).getTipoZona(),
                        "E",
                        rutas.get(0).getLineaNegocio(),
                        fecha, hora,
                        "",
                        "",
                        "",
                        "",
                        "",
                        "", "", "",
                        piezaItems.get(i).getIdPieza(),
                        dbMotivoDescargas.get(selectedIndexMotivo).getIdMotivo(),
                        "",
                        "",
                        "",
                        "",
                        Data.Delete.NO,
                        Data.Validate.VALID,
                        numVecesGestionado
                );
                guiaGestionada.save();
            }
        }
    }

    private void saveProductosEntregados() {
        for (int i = 0; i < premioItems.size(); i++) {
            if (premioItems.get(i).isSelected()) {
                GuiaGestionada guiaGestionada = new GuiaGestionada(
                        Preferences.getInstance().getString("idUsuario", ""),
                        premioItems.get(i).getIdProducto(),
                        "sku",
                        rutas.get(0).getTipoZona(),
                        "E",
                        rutas.get(0).getLineaNegocio(),
                        "", "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "", "", "",
                        premioItems.get(i).getGuiaNumero(),
                        premioItems.get(i).getIdRuta(),
                        "",
                        "",
                        "",
                        "",
                        Data.Delete.NO,
                        Data.Validate.VALID,
                        numVecesGestionado
                );
                guiaGestionada.save();
            }
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

    private void setVisibilityFirstStep() {
        if (rutas.size() == 1 && piezaItems.size() > 1) {
            view.setVisibilityBoxStepPiezas(View.VISIBLE);
            view.notifyPiezasAllItemChanged();
            currentStep = STEPS.PIEZAS;
        } else if (premioItems.size() > 0) {
            view.setVisibilityBoxStepProductosEntregados(View.VISIBLE);
            view.notifyPremiosAllItemChanged();
            currentStep = STEPS.PRODUCTOS_A_ENTREGAR;
        } else {
            view.setVisibilityBoxStepTipoEntrega(View.VISIBLE);
            //view.notifyMotivosAllItemChanged();
            loadMotivos();
            currentStep = STEPS.TIPO_ENTREGA;
        }
    }

    private void setVisibilityInputDatosCliente() {
        if (!isRequiereDatosCliente()) {
            view.setVisibilityLayoutInputRecibidoPor(View.GONE);
            view.setVisibilityLayoutInputTipoDocIndentidad(View.GONE);
            view.setVisibilityLayoutInputDocIndentidad(View.GONE);
        }
    }

    private void setVisibilityInputTipoDocIndentidad() {
        if (rutas.get(0).getLineaNegocio().equals("2")) {
            view.setVisibilityLayoutInputTipoDocIndentidad(View.VISIBLE);
        } else {
            view.setVisibilityLayoutInputTipoDocIndentidad(View.GONE);
        }
    }

    private void setVisibilityInputTipoDireccion() {
        if (rutas.get(0).getLineaNegocio().equals("2")) {
            view.setVisibilityLayoutInputTipoDireccion(View.VISIBLE);
        } else {
            view.setVisibilityLayoutInputTipoDireccion(View.GONE);
        }
    }

    private void setVisibilityInputTipoMedioPago() {//segundo
        if (isMedioPagoNoDefinido()) {
            view.setVisibilityLayoutInputTipoMedioPago(View.VISIBLE);
        } else {
            view.setVisibilityLayoutInputTipoMedioPago(View.GONE);
        }
    }

    private void setVisibilityInputObservarEntrega() {
        boolean showInputObservarEntrega = false;

        if (rutas.size() == 1) {
            if (rutas.get(0).getLineaNegocio().equals("3")) {
                if (rutas.get(0).getTipoEnvio().toUpperCase().equals(Ruta.TipoEnvio.PAQUETE)) {
                    showInputObservarEntrega = true;
                }
            }
        }

        if (showInputObservarEntrega) {
            view.setVisibilityLayoutInputObservarEntrega(View.VISIBLE);
        } else {
            view.setVisibilityLayoutInputObservarEntrega(View.GONE);
        }
    }
    /*
    private void setVisibilityInputVoucher() {
        if (isMedioPagoConTarjeta()) {
            view.setVisibilityLayoutInputVoucher(View.VISIBLE);
        } else {
            view.setVisibilityLayoutInputVoucher(View.GONE);
        }
    }
    */

    private void setTitleStepFotoCargo() {
        view.setTitleStepFotoCargo(
                "FOTOS DEL " + ModelUtils.getNameLblCargoGuia(view.getViewContext()).toUpperCase());
    }

    private void setMinFotosProducto() {
        if (rutas.get(0).getMinimoFotosProductoGestionGuia() != null) {
            try {
                minFotosProducto = Integer.parseInt(rutas.get(0).getMinimoFotosProductoGestionGuia());
                if (minFotosProducto > 10) {
                    minFotosProducto = 10;
                }
            } catch (NumberFormatException ex) {
            }
        }
    }

    private void showPagoDialog() {
        View viewModal = view.getFragment().getLayoutInflater().inflate(R.layout.modal_recaudo_importe, null);

        final RadioButton rBtnPagoYape = (RadioButton) viewModal.findViewById(R.id.rBtnSi);

        ModalHelper.getBuilderAlertDialog(view.getViewContext())
                .setView(viewModal)
                .setPositiveButton( R.string.text_siguiente, (dialog, which) -> {
                    if (rBtnPagoYape.isChecked()) {
                        showYapeQRStep();
                    } else {
                        continueDatosEntregaStep();
                    }
                }).show();
    }

    private void continueDatosEntregaStep(){
        if (minFotosProducto == 0) {
            view.setVisibilityBoxStepFotoCargoEntrega(View.VISIBLE);
            view.notifyGaleriaCargoAllItemChanged();
            currentStep = STEPS.FOTOS_CARGO;
        } else if (rutas.get(0).getTipoEnvio().toUpperCase().equals(Ruta.TipoEnvio.LIQUIDACION)) {
            view.setVisibilityBoxStepFotoCargoEntrega(View.VISIBLE);
            view.notifyGaleriaCargoAllItemChanged();
            view.setTextBtnSiguiente("Gestionar");
            currentStep = STEPS.FOTOS_CARGO;
        } else {
            view.setVisibilityBoxStepFotosEntrega(View.VISIBLE);
            view.notifyGaleriaFotosAllItemChanged();
            currentStep = STEPS.FOTOS_PRODUCTO;
        }
    }

    private void showYapeQRStep(){
        view.setVisibilityBoxYapeQR(View.VISIBLE);
        currentStep = STEPS.YAPE_QR;
        requestYapeQR();
    }

    private boolean isMedioPagoEfectivo() {
        switch (Integer.parseInt(rutas.get(0).getIdMedioPago())) {
            case 1:
                return true;
        }
        return false;
    }
    /*

    private boolean isMedioPagoConTarjeta() {
        switch (Integer.parseInt(rutas.get(0).getIdMedioPago())) {
            case 2:
            case 3:
            case 4:
            case 5:
            return true;
        }
        return false;
    }*/

    private boolean isMedioPagoNoDefinido() {
        String pago = rutas.get(0).getIdMedioPago();
        switch (Integer.parseInt(rutas.get(0).getIdMedioPago())) {
            case 8:
                return true;
        }
        return false;
    }

    private boolean isMedioPagoYape() {
        String medioPago = rutas.get(0).getIdMedioPago();
        return medioPago.equals("3");

    }

    private boolean hasHabilitantes() {
        for (int i = 0; i < rutas.size(); i++) {
            if (rutas.get(i).getHabilitantes() != null
                    && !rutas.get(i).getHabilitantes().isEmpty()) {
                return true;
            }
        }
        Log.i(TAG, "hasHabilitantes_hasHabilitantes false");
        return false;
    }

    private boolean isRequiredFirmaCliente() {
        for (int i = 0; i < rutas.size(); i++) {
            if (rutas.get(i).getFirmaClienteGestionGuia() != null) {
                try {
                    if (Integer.parseInt(rutas.get(i).getFirmaClienteGestionGuia()) == 1) {
                        return true;
                    }
                } catch (NumberFormatException ex) {
                }
            }
        }
        return false;
    }

    private int getTipoMotivoParaPiezas() {
        boolean isAllSelected = piezaItems.stream()
                .filter(p -> !p.isSelected())
                .noneMatch(p -> !p.getChk().equals(CHK_ENTREGA)
                        && !p.getChk().equals(CHK_ENTREGA_DEVOLUCION));

        if (!isAllSelected) {
            if (rutas.get(0).getTipoEnvio().equalsIgnoreCase(Ruta.TipoEnvio.DEVOLUCION)) {
                tipoMotivo = MotivoDescarga.Tipo.ENTREGA_DEVOLUCION_PARCIAL;
            } else {
                tipoMotivo = MotivoDescarga.Tipo.ENTREGA_PARCIAL;
            }
        }

        return tipoMotivo;
    }

    private void setVisibilityContainerMsgEntregaParcial() {
        if (piezaItems.stream().anyMatch(PiezaItem::isSelected)) {
            boolean isAllSelected = piezaItems.stream()
                    .filter(p -> !p.isSelected())
                    .noneMatch(p -> !p.getChk().equals(CHK_ENTREGA)
                            && !p.getChk().equals(CHK_ENTREGA_DEVOLUCION));

            if (!isAllSelected) {
                view.setVisibilityContainerMsgEntregaParcial(View.VISIBLE);
                return;
            }
        }

        view.setVisibilityContainerMsgEntregaParcial(View.GONE);
    }

    private boolean validateSelectedPiezas() {
        if (piezaItems.stream().anyMatch(PiezaItem::isSelected)) {
            return true;
        }

        view.showSnackBar(R.string.activity_detalle_ruta_message_piezas_no_seleccionado);
        CommonUtils.vibrateDevice(view.getViewContext(), 100);
        return false;
    }

    private boolean validateSelectedProductosEntregados() {
        if (premioItems.stream().anyMatch(PremioItem::isSelected)) {
            return true;
        }

        view.showSnackBar(R.string.activity_detalle_ruta_message_producto_no_seleccionado);
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

    private boolean validateDatosEntrega() {
        if (isRequiereDatosCliente()) {
            if (view.getTextNombre().isEmpty()) {
                view.setErrorTxtNombre("Ingrese el destinatario.");
                CommonUtils.vibrateDevice(view.getViewContext(), 100);
                return false;
            }

            if (view.getTextDNI().isEmpty()) {
                view.setErrorTxtDNI("Ingrese el Doc. de Identidad.");
                CommonUtils.vibrateDevice(view.getViewContext(), 100);
                return false;
            } else {
                if (rutas.get(0).getLineaNegocio().equals("2")
                        && selectedIndexTipoDocIdentificacion == 0) {
                    int country = new PreferencesHelper(view.getViewContext()).getCountry();
                    if (country == Country.CHILE) {
                        boolean validate = ValidationUtils.validateCedulaChile(view.getTextDNI());

                        if (!validate) {
                            view.setErrorTxtDNI("La Cédula de Identidad es incorrecta.");
                            CommonUtils.vibrateDevice(view.getViewContext(), 100);
                            return false;
                        }
                    }
                    if (country == Country.ECUADOR) {
                        boolean validate = ValidationUtils.validateCedulaEcuador(view.getTextDNI());

                        if (!validate) {
                            view.setErrorTxtDNI("La Cédula de Identidad es incorrecta.");
                            CommonUtils.vibrateDevice(view.getViewContext(), 100);
                            return false;
                        }
                    }
                    if (country == Country.PERU) {
                        if (view.getTextDNI().length() != 8) {
                            view.setErrorTxtDNI("El DNI es incorrecto.");
                            CommonUtils.vibrateDevice(view.getViewContext(), 100);
                            return false;
                        }
                    }
                }
            }

            if (rutas.get(0).getLineaNegocio().equals("2")
                    && selectedIndexTipoDireccion < 0) {
                view.showSnackBar(
                        R.string.activity_detalle_ruta_message_tipo_direccion_no_seleccionado);
                CommonUtils.vibrateDevice(view.getViewContext(), 100);
                return false;
            }
        }

        if (isMedioPagoNoDefinido()) {//luego de datos de entrega 3?
            if (selectedIndexTipoMedioPago < 1) {
                view.showSnackBar(
                        R.string.activity_detalle_ruta_message_tipo_medio_pago_no_seleccionado);
                CommonUtils.vibrateDevice(view.getViewContext(), 100);
                return false;
            }
        }

        if (!validateGestionRecoleccionGuiaValija()) {
            return false;
        }

        return validateObservarGestionEntrega();
    }

    private boolean validateFotosProducto() {
        int totalBtns = rutas.get(0).getTipoZona() == Ruta.ZONA.RURAL ? 2 : 1;
        if ((galeria.size() - totalBtns) < minFotosProducto) {
            view.showSnackBar(
                    "Debe tomar " + minFotosProducto + " foto(s) del producto como mínimo para realizar la gestión de la guía.");
            CommonUtils.vibrateDevice(view.getViewContext(), 100);
            return false;
        }

        return true;
    }

    private boolean validateFirmaCliente() {
        if ((galeriaFirma.size() - 1) < 1) {
            view.showSnackBar(
                    "Debe solicitar una firma del cliente para realizar la gestión de la guía.");
            CommonUtils.vibrateDevice(view.getViewContext(), 100);
            return false;
        }

        return true;
    }

    private boolean validateFotosCargo() {
        if ((galeriaCargo.size() - 1) < 1) {
            String msg = "Debe tomar una foto del " +
                    ModelUtils.getNameLblCargoGuia(view.getViewContext()).toLowerCase() +
                    " como mínimo para realizar la gestión de la guía.";
            view.showSnackBar(msg);
            CommonUtils.vibrateDevice(view.getViewContext(), 100);
            return false;
        }

        return true;
    }

    private boolean validateFotosDomicilio() {
        if ((galeriaDomicilio.size() - 1) < 1) {
            view.showSnackBar(
                    "Debe tomar una foto del domicilio como mínimo para realizar la gestión de la guía.");
            CommonUtils.vibrateDevice(view.getViewContext(), 100);
            return false;
        }

        return true;
    }

    private boolean validateFotosComprobantePago() {
        if (galeriaComprobantePago.size() - 1 < 1) {
            view.showSnackBar("Debe tomar al menos una foto al comprobante de pago");
            CommonUtils.vibrateDevice(view.getViewContext(), 100);
            return false;
        }

        return true;
    }

    private boolean validateGestionRecoleccionGuiaValija() {
        if (rutas.size() == 1) {
            if (rutas.get(0).getIdServicioRecoleccion() != null) {
                if (!rutas.get(0).getIdServicioRecoleccion().isEmpty() &&
                        Integer.parseInt(rutas.get(0).getIdServicioRecoleccion()) > 0) {
                    // validar si la gestion de la recoleccion adjunta ya se realizó.
                    long totalGestiones = rutaPendienteInteractor.getTotalGestionesByIdServicio(
                            rutas.get(0).getIdServicioRecoleccion(), rutas.get(0).getLineaNegocio());

                    if (totalGestiones == 0) {
                        GestionarRecoleccionGuiaValijaDilog fragment =
                                GestionarRecoleccionGuiaValijaDilog.newInstance(rutas.get(0));
                        FragmentManager fragmentManager = view.getFragment().getFragmentManager();
                        fragment.show(fragmentManager, GestionarGENoRecolectadasDialog.TAG);
                        return false;
                    }
                }
            }
        }

        return true;
    }

    private boolean validateObservarGestionEntrega() {
        if (observarEntrega) {
            if (idMotivoObservacionEntrega.isEmpty() && comentarioObservacionEntrega.isEmpty()) {
                ObservarGestionEntregaDialog fragment =
                        ObservarGestionEntregaDialog.newInstance(rutas.get(0));
                FragmentManager fragmentManager = view.getFragment().getFragmentManager();
                fragment.show(fragmentManager, ObservarGestionEntregaDialog.TAG);
                return false;
            }
        }

        return true;
    }

    private boolean isRequiereDatosCliente() {
        boolean allNull = true; // flag para soportar versiones antiguas que no tienen el flag

        for (int i = 0; i < rutas.size(); i++) {
            if (rutas.get(i).getRequiereDatosCliente() != null) {
                allNull = false;

                if (rutas.get(i).getRequiereDatosCliente().equals("1")) {
                    return true;
                }
            }
            /*if (Objects.equals(rutas.get(i).getRequiereDatosCliente(), "1")) {
                return true;
            }*/
        }

        return allNull;
    }

    private boolean canTakePhoto() {
        switch (currentStep) {
            case FOTOS_PRODUCTO:
                return canTakeFotoProducto();
            case FOTOS_CARGO:
                return canTakeFotoCargo();
            case FOTOS_DOMICILIO:
                return canTakeFotoDomicilio();
            case FOTOS_COMPROBANTE_PAGO:
                return canTakeFotoComprobantePago();
            default:
                return false;
        }
    }

    private boolean canTakeFotoProducto() {
        int totalBtns = rutas.get(0).getTipoZona() == Ruta.ZONA.RURAL ? 2 : 1;
        int totalImages = galeria.size() - totalBtns;
        return totalImages < 10;
    }

    private boolean canTakeFirmaCliente() {
        return galeriaFirma.size() <= 1;
    }

    private boolean canTakeFotoCargo() {
        return galeriaCargo.size() <= 10;
    }

    private boolean canTakeFotoComprobantePago() {
        return galeriaCargo.size() <= 2;
    }


    private boolean canTakeFotoDomicilio() {
        int totalBtns = rutas.get(0).getTipoZona() == Ruta.ZONA.RURAL ? 2 : 1;
        int totalImages = galeriaDomicilio.size() - totalBtns;
        return totalImages < 10;
    }

    private void showGalerias() {
        List<Imagen> images = selectAllImages();

        galeria.clear();
        galeriaCargo.clear();
        galeriaFirma.clear();
        galeriaDomicilio.clear();
        galeriaComprobantePago.clear();

        GalleryButtonItem buttonItem = new GalleryButtonItem(R.drawable.ic_camera_grey);
        galeria.add(buttonItem);
        galeriaCargo.add(buttonItem);
        galeriaDomicilio.add(buttonItem);
        galeriaComprobantePago.add(buttonItem);

        buttonItem = new GalleryButtonItem(R.drawable.ic_firma_grey);
        galeriaFirma.add(buttonItem);

        if (rutas.get(0).getTipoZona() == Ruta.ZONA.RURAL) {
            buttonItem = new GalleryButtonItem(R.drawable.ic_image_grey,
                    GalleryButtonItem.Action.GALLERY);
            galeria.add(buttonItem);
            galeriaDomicilio.add(buttonItem);
        }

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
                case "pago":
                    galeriaComprobantePago.add(item);
                    break;
            }
        }

        view.showFotosEnGaleria(galeria);
        view.showImagenFirmaEnGaleria(galeriaFirma);
        view.showFotosCargoEnGaleria(galeriaCargo);
        view.showFotosDomicilioEnGaleria(galeriaDomicilio);
        view.showFotosComprobantePago(galeriaComprobantePago);
    }

    private void insertImageToGallery() {
        Log.d(TAG, "Image Path: " + photoCapture.getAbsolutePath());
        GalleryPhotoItem item = new GalleryPhotoItem(photoCapture.getAbsolutePath());

        if (typeCameraCaptureImage.equalsIgnoreCase("Imagen")) {
            galeria.add(item);
            view.notifyGaleriaFotosAllItemChanged();
        } else if (typeCameraCaptureImage.equalsIgnoreCase("Firma")) {
            galeriaFirma.add(item);
            view.notifyGaleriaFirmaAllItemChanged();
        } else if (typeCameraCaptureImage.equalsIgnoreCase("Cargo")) {
            galeriaCargo.add(item);
            view.notifyGaleriaCargoAllItemChanged();
        } else if (typeCameraCaptureImage.equalsIgnoreCase("Domicilio")) {
            galeriaDomicilio.add(item);
            view.notifyGaleriaDomicilioAllItemChanged();
        } else if (typeCameraCaptureImage.equalsIgnoreCase("Pago")) {
            galeriaComprobantePago.add(item);
            view.notifyGaleriaPagoAllItemChanged();
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
            case FIRMA_CLIENTE:
                FileUtils.deleteFile(((GalleryPhotoItem) galeriaFirma.get(position)).getPathImage());

                indexSplit = ((GalleryPhotoItem) galeriaFirma.get(position)).getPathImage().lastIndexOf("/") + 1;
                name = ((GalleryPhotoItem) galeriaFirma.get(position)).getPathImage().substring(indexSplit);
                path = ((GalleryPhotoItem) galeriaFirma.get(position)).getPathImage().substring(0, indexSplit);

                galeriaFirma.remove(position);
                view.notifyGaleriaFirmaItemRemove(position);
                break;
            case FOTOS_CARGO:
                FileUtils.deleteFile(((GalleryPhotoItem) galeriaCargo.get(position)).getPathImage());

                indexSplit = ((GalleryPhotoItem) galeriaCargo.get(position)).getPathImage().lastIndexOf("/") + 1;
                name = ((GalleryPhotoItem) galeriaCargo.get(position)).getPathImage().substring(indexSplit);
                path = ((GalleryPhotoItem) galeriaCargo.get(position)).getPathImage().substring(0, indexSplit);

                galeriaCargo.remove(position);
                view.notifyGaleriaCargoItemRemove(position);
                break;
            case FOTOS_DOMICILIO:
                FileUtils.deleteFile(((GalleryPhotoItem) galeriaDomicilio.get(position)).getPathImage());

                indexSplit = ((GalleryPhotoItem) galeriaDomicilio.get(position)).getPathImage().lastIndexOf("/") + 1;
                name = ((GalleryPhotoItem) galeriaDomicilio.get(position)).getPathImage().substring(indexSplit);
                path = ((GalleryPhotoItem) galeriaDomicilio.get(position)).getPathImage().substring(0, indexSplit);

                galeriaDomicilio.remove(position);
                view.notifyGaleriaDomicilioItemRemove(position);
                break;
            case FOTOS_COMPROBANTE_PAGO:
                FileUtils.deleteFile(((GalleryPhotoItem) galeriaComprobantePago.get(position)).getPathImage());

                indexSplit = ((GalleryPhotoItem) galeriaComprobantePago.get(position)).getPathImage().lastIndexOf("/") + 1;
                name = ((GalleryPhotoItem) galeriaComprobantePago.get(position)).getPathImage().substring(indexSplit);
                path = ((GalleryPhotoItem) galeriaComprobantePago.get(position)).getPathImage().substring(0, indexSplit);

                galeriaComprobantePago.remove(position);
                view.notifyGaleriaPagoItemRemove(position);
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
            if (typeCameraCaptureImage.equalsIgnoreCase("Cargo") ||
                    typeCameraCaptureImage.equalsIgnoreCase("Voucher")) {
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
            if (tipoMotivo == MotivoDescarga.Tipo.ENTREGA_PARCIAL ||
                    tipoMotivo == MotivoDescarga.Tipo.ENTREGA_DEVOLUCION_PARCIAL) {
                ruta.setResultadoGestion(Ruta.ResultadoGestion.EFECTIVA_PARCIAL);
            } else {
                ruta.setResultadoGestion(Ruta.ResultadoGestion.EFECTIVA_COMPLETA);
            }
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
     * {@link DetalleRutaRuralPresenter#descargaFinalizadaReceiver}
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
     * <p>
     * {@link FirmarActivity#sendOnSaveFirmaReceiver}
     */
    private BroadcastReceiver saveFirmaReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            photoCapture = (File) intent.getSerializableExtra("file");
            saveImage(photoCapture.getName(), photoCapture.getParent() + File.separator, "firma");
            insertImageToGallery();
        }
    };

    /**
     * Broadcast
     * <p>
     * {@link ObservarGestionEntregaPresenter#sendDataObservacionGestionEntregaReceiver}
     */
    private BroadcastReceiver dataObservacionGestionEntregaReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getBundleExtra("args");
            idMotivoObservacionEntrega = bundle.getString("idMotivoObservacionEntrega");
            comentarioObservacionEntrega = bundle.getString("comentarioObservacionEntrega");
        }
    };

    /**
     * Broadcast
     * <p>
     * {@link CodeScannerImpl#sendOnResultScannListener}
     */
    private BroadcastReceiver resultScannReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                boolean isPCKFound = false;
                for (int i = 0; i < piezaItems.size(); i++) {
                    if (piezaItems.get(i).getBarra().equals(intent.getStringExtra("value"))) {
                        if (piezaItems.get(i).isSelectable() || piezaItems.get(i).isBarcodeScanningIsMandatory()) {
                            isPCKFound = true;
                            pckReadFromScanner = true;
                            piezaItems.get(i).setSelected(true);
                            CommonUtils.playSoundOnScanBarcode(view.getViewContext(), R.raw.scan_barcode_add_pck);
                        }
                        break;
                    }
                }
                if (!isPCKFound) {
                    CommonUtils.playSoundOnScanBarcode(view.getViewContext(), R.raw.scan_barcode_error);
                }
                LocalBroadcastManager.getInstance(view.getViewContext()).sendBroadcast(
                        new Intent(LocalAction.NEXT_BARCODE_SCAN_CONTINUOS));
            } catch (NullPointerException ex) {
                ex.printStackTrace();
            }
        }
    };

    class SaveGestionTask extends AsyncTaskCoroutine<String, String> {
        @Override
        public void onPreExecute() {
            super.onPreExecute();
            view.showProgressDialog(R.string.text_gestionando_guia);
        }

        @Override
        public String doInBackground(String... strings) {
            LocalDateTime localDateTime = LocalDateTime.now();
            String fecha = DateTimeFormatter.ofPattern("dd/MM/yyyy").format(localDateTime);
            String hora = DateTimeFormatter.ofPattern("HH:mm:ss").format(localDateTime);

            saveGestionGE(fecha, hora);
            savePiezas(fecha, hora);
            saveProductosEntregados();
            checkUploadDataSyncImages();

            if (rutas.get(0).getTipoZona() == Ruta.ZONA.RURAL) {
                for (int i = 0; i < descargaRutas.size(); i++) {
                    if (descargaRutas.get(i) != null) {
                        descargaRutas.get(i).delete();
                    }
                }
                for (int i = 0; i < rutas.size(); i++) {
                    Ruta ruta = rutaPendienteInteractor.selectRuta(
                            rutas.get(i).getIdServicio(), rutas.get(i).getLineaNegocio());
                    if (ruta != null) {
                        ruta.delete();

                        List<Pieza> piezas = RutaPendienteInteractor.selectPiezas(
                                rutas.get(i).getIdServicio(), rutas.get(i).getLineaNegocio());
                        for (Pieza pieza : piezas) {
                            pieza.delete();
                        }
                    }
                }
            } else {
                if (tipoMotivo == MotivoDescarga.Tipo.ENTREGA_PARCIAL) {
                    updateEstadoGestionGE(DescargaRuta.Entrega.ENTREGAR);
                } else {
                    updateEstadoGestionGE(DescargaRuta.Entrega.FINALIZADO);
                }
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
            showGalerias();
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