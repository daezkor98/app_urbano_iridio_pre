package com.urbanoexpress.iridio3.pe.presenter;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.AppCompatActivity;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.VolleyError;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.urbanoexpress.iridio3.pe.util.async.AsyncTaskCoroutine;
import com.urbanoexpress.iridio3.pe.R;
import com.urbanoexpress.iridio3.pe.model.entity.Data;
import com.urbanoexpress.iridio3.pe.model.entity.DescargaRuta;
import com.urbanoexpress.iridio3.pe.model.entity.EstadoRuta;
import com.urbanoexpress.iridio3.pe.model.entity.GestionLlamada;
import com.urbanoexpress.iridio3.pe.model.entity.GuiaGestionada;
import com.urbanoexpress.iridio3.pe.model.entity.MotivoDescarga;
import com.urbanoexpress.iridio3.pe.model.entity.Ruta;
import com.urbanoexpress.iridio3.pe.model.interactor.RutaPendienteInteractor;
import com.urbanoexpress.iridio3.pe.model.interactor.callback.RequestCallback;
import com.urbanoexpress.iridio3.pe.model.util.ModelUtils;
import com.urbanoexpress.iridio3.pe.presenter.helpers.ShowDescargaRutaHelper;
import com.urbanoexpress.iridio3.pe.ui.dialogs.EditarNumeroTelefonoDialog;
import com.urbanoexpress.iridio3.pe.ui.dialogs.GaleriaGEDialog;
import com.urbanoexpress.iridio3.pe.ui.dialogs.RecolectaGEDialog;
import com.urbanoexpress.iridio3.pe.ui.dialogs.TrazarRutaBottomSheet;
import com.urbanoexpress.iridio3.pe.ui.helpers.ModalHelper;
import com.urbanoexpress.iridio3.pe.ui.model.TelefonoGuiaItem;
import com.urbanoexpress.iridio3.pe.util.CommonUtils;
import com.urbanoexpress.iridio3.pe.util.Preferences;
import com.urbanoexpress.iridio3.pe.util.constant.LocalAction;
import com.urbanoexpress.iridio3.pe.view.BaseModalsView;
import com.urbanoexpress.iridio3.pe.view.DetalleRutaView;

import org.apache.commons.lang3.text.WordUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by mick on 09/07/16.
 */
public class DetalleRutaPresenter {

    private final String TAG = DetalleRutaPresenter.class.getSimpleName();

    private DetalleRutaView view;
    private RutaPendienteInteractor interactor;

    private AppCompatActivity activity;

    private ArrayList<TelefonoGuiaItem> telContactoGestionItems = new ArrayList<>();
    private ArrayList<TelefonoGuiaItem> telefonoItems = new ArrayList<>();
    private ArrayList<TelefonoGuiaItem> celularItems = new ArrayList<>();

    private ArrayList<String> nombreTelContactoGestionItems = new ArrayList<>();

    private List<MotivoDescarga> dbMotivoGestionLlamada = Collections.emptyList();
    private String[] motivoGestionLlamadaItems;

    private Bundle args;

    private Ruta ruta;

    private ShowDescargaRutaHelper showDescargaRutaHelper;

    private boolean isEnableFabDescarga = false;

    private TelephonyManager telephonyManager;

    private boolean isButtonPhoneClicked = false;

    private Uri uriPhoneCalled;

    private CallStateListener callStateListener;

    private int selectedIndexTipoResultadoLlamada = 0;

    private Date callInitDate;
    private Date callEndDate;

    public DetalleRutaPresenter(DetalleRutaView view, Bundle args) {
        this.view = view;
        this.args = args;
        this.activity = (AppCompatActivity) view.getViewContext();
        this.interactor = new RutaPendienteInteractor(view.getViewContext());
        init();
    }

    private void init() {
        LocalBroadcastManager.getInstance(view.getViewContext())
                .registerReceiver(descargaFinalizadaReceiver,
                        new IntentFilter("OnDescargaFinalizada"));
        LocalBroadcastManager.getInstance(view.getViewContext())
                .registerReceiver(guardarNumeroTelefonoReceiver,
                        new IntentFilter(LocalAction.GUARDAR_NUMERO_TELEFONO_ACTION));

        loadDataRuta();
        showDetalleRuta();
        showAlertaEstadoShipper();
        showTelContactoGestion();
        showTelefonos();
        showCelulares();
        showHabilitantes();

        loadDescargaRutaHelper();

        showFabDescarga();

        registerCallStateListener();
    }

    public void onMenuReady() {
        switch (args.getInt("numVecesGestionado")) {
            case 1:
                CommonUtils.setVisibilityOptionMenu(
                        view.getMenuToolbar(), R.id.action_galeria, false);
                break;
            case 2:
                CommonUtils.setVisibilityOptionMenu(
                        view.getMenuToolbar(), R.id.action_volverConfirmarGestion, false);
                break;
        }
    }

    public void onClickFabDescarga() {
        if (validateEstadoRuta()) {
            if (validateOrdenDescarga() || args.getInt("numVecesGestionado") == 2) {
                showDescargaRutaHelper.onClickDescarga();
            } else {
                BaseModalsView.showAlertDialog(view.getViewContext(),
                        R.string.text_accion_no_disponible,
                        R.string.activity_detalle_ruta_message_orden_descarga_no_permitido,
                        R.string.text_aceptar, null);
            }
        }
    }

    public void onClickFabUbicacionEntrega() {
        if (existRuta()) {
            if (CommonUtils.isValidCoords(ruta.getGpsLatitude(), ruta.getGpsLongitude())) {
                try {
                    //Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + ruta.getGpsLatitude() + "," + ruta.getGpsLongitude()+"(Ubicación de Entrega)");
                    Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + ruta.getGpsLatitude() + "," + ruta.getGpsLongitude());
                    Intent intent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    //intent.setPackage("com.google.android.apps.maps");
                    activity.startActivity(intent);
                } catch (ActivityNotFoundException ex) {
                    view.showToast("Lo sentimos, no hay ninguna aplicación de mapas instalada en el teléfono.");
                }
            } else {
                view.showToast(R.string.activity_detalle_ruta_message_no_hay_gps);
            }
        }
    }

    public void onClickActionGaleria() {
        if (ruta.getEstadoDescarga() == Ruta.EstadoDescarga.GESTIONADO) {
            GaleriaGEDialog fragment = GaleriaGEDialog.newInstance(ruta);
            FragmentManager fm = ((AppCompatActivity) view.getViewContext()).getSupportFragmentManager();
            fragment.show(fm, GaleriaGEDialog.TAG);
        } else {
            BaseModalsView.showAlertDialog(view.getViewContext(),
                    R.string.text_accion_no_disponible,
                    R.string.activity_detalle_ruta_message_no_hay_galeria,
                    R.string.text_aceptar, null);
        }
    }

    public void onClickActionVolverConfirmarGestion() {
        if (validateEstadoRuta()) {
            if (validateOrdenDescarga() || args.getInt("numVecesGestionado") == 2) {
                showDescargaRutaHelper.onClickVolverConfirmarGestion();
            } else {
                BaseModalsView.showAlertDialog(view.getViewContext(),
                        R.string.text_accion_no_disponible,
                        R.string.activity_detalle_ruta_message_orden_descarga_no_permitido,
                        R.string.text_aceptar, null);
            }
        }
    }

    public void onActionAgregarGuiaRecoleccion() {
        RecolectaGEDialog fragmentRecolectaConGuia =
                new RecolectaGEDialog().newInstance(
                        new ArrayList<Ruta>(Arrays.asList(ruta)), 2, true);
        FragmentManager fragmentManager = ((AppCompatActivity) view.getViewContext())
                .getSupportFragmentManager();
        fragmentRecolectaConGuia.show(fragmentManager, RecolectaGEDialog.TAG);
    }

    public void onActionTrazarRuta() {
        TrazarRutaBottomSheet trazarRutaBottomSheet = TrazarRutaBottomSheet.newInstance(ruta);
        trazarRutaBottomSheet.show(((AppCompatActivity) view).getSupportFragmentManager(),
                TrazarRutaBottomSheet.TAG);
    }

    public boolean isEnabledFabDescarga() {
        return isEnableFabDescarga;
    }

    public void onDestroy() {
        LocalBroadcastManager.getInstance(view.getViewContext())
                .unregisterReceiver(descargaFinalizadaReceiver);
        LocalBroadcastManager.getInstance(view.getViewContext())
                .unregisterReceiver(guardarNumeroTelefonoReceiver);

        telephonyManager.listen(callStateListener, PhoneStateListener.LISTEN_NONE);
    }

    public void showFabDescarga() {
        if (isGuiaGestionFinalizado()) {
            view.setVisibilityFabDescarga(View.GONE);
        } else {
            switch (args.getInt("numVecesGestionado")) {
                case 1:
                    if (validateOrdenDescarga()) {
                        if (ModelUtils.hasGuiaReqDevolucionShipper(ruta)) {
                            view.setVisibilityFabDescarga(View.GONE);
                        } else {
                            view.setVisibilityFabDescarga(View.VISIBLE);
                            isEnableFabDescarga = true;
                        }
                    } else {
                        view.setVisibilityFabDescarga(View.GONE);
                    }
                    break;
                case 2:
                    if (ModelUtils.hasGuiaReqDevolucionShipper(ruta)) {
                        view.setVisibilityFabDescarga(View.GONE);
                    } else {
                        if (isGuiaGestionNoEntrega()) {
                            switch (ruta.getTipoEnvio().toUpperCase()) {
                                case Ruta.TipoEnvio.PAQUETE:
                                case Ruta.TipoEnvio.VALIJA:
                                case Ruta.TipoEnvio.LOGISTICA_INVERSA:
                                    view.setVisibilityFabDescarga(View.VISIBLE);
                                    isEnableFabDescarga = true;
                                    break;
                                default:
                                    view.setVisibilityFabDescarga(View.GONE);
                                    break;
                            }
                        } else {
                            view.setVisibilityFabDescarga(View.GONE);
                        }
                    }
                    break;
            }
        }
    }

    public void showBtnAgregarGuia() {
        if (args.getInt("numVecesGestionado") == 2) {
            GuiaGestionada guiaGestionada = interactor.selectRutaGestionada(
                    ruta.getIdServicio(), ruta.getLineaNegocio());
            if (ModelUtils.isGuiaRecoleccion(ruta.getTipo()) &&
                    guiaGestionada.getGuiaElectronica().length() > 0) {
                CommonUtils.setVisibilityOptionMenu(view.getMenuToolbar(),
                        R.id.action_agregar_guia, true);
            } else {
                CommonUtils.setVisibilityOptionMenu(view.getMenuToolbar(),
                        R.id.action_agregar_guia, false);
            }
        } else {
            CommonUtils.setVisibilityOptionMenu(view.getMenuToolbar(),
                    R.id.action_agregar_guia, false);
        }
    }

    public void onClickPhoneContent(int position, int type) {
        onClickBtnPhone(position, type);
    }

    public void onClickBtnPhone(int position, int type) {
        loadMotivosGestionLlamada();

        if (dbMotivoGestionLlamada.size() != 0) {
            // type => 1 = telefonos, 2 = celulares, 3 = telefono contacto
            try {
                isButtonPhoneClicked = true;
                callInitDate = new Date();

                String phone = "";

                switch (type) {
                    case 1:
                        phone = telefonoItems.get(position).getTelefono();
                        break;
                    case 2:
                        phone = celularItems.get(position).getTelefono();
                        break;
                    case 3:
                        phone = telContactoGestionItems.get(position).getTelefono();
                        break;
                }

                uriPhoneCalled = Uri.fromParts("tel", phone, null);
                /*Log.d("MainActivity", "getScheme: " + uriPhoneCalled.getScheme());
                Log.d("MainActivity", "getSchemeSpecificPart: " + uriPhoneCalled.getSchemeSpecificPart());
                Log.d("MainActivity", "getSchemeSpecificPart removed space: " + uriPhoneCalled.getSchemeSpecificPart().replace(" ", ""));
                Log.d("MainActivity", "getEncodedSchemeSpecificPart: " + uriPhoneCalled.getEncodedSchemeSpecificPart());*/
                Intent intent = new Intent(Intent.ACTION_DIAL, uriPhoneCalled);
                activity.startActivity(intent);
            } catch (ActivityNotFoundException ex) {
                ex.printStackTrace();
                view.showToast("No se encontro una aplicación para hacer llamadas en el dispositivo.");
            }
        } else {
            BaseModalsView.showAlertDialog(activity, R.string.activity_detalle_ruta_title_motivo_gestion_llamada_pendiente_descarga,
                    R.string.activity_detalle_ruta_msg_motivo_gestion_llamada_pendiente_descarga,
                    R.string.text_descargar, (dialog, which) -> getMotivosGestionLlamada(),
                    R.string.text_cancelar, null);
        }
    }

    public void onClickBtnContacto(int position, int type) {
        try {
            if (nombreTelContactoGestionItems.size() != 0) {
                view.showSnackBar("El nombre del contacto es: " + nombreTelContactoGestionItems.get(position));
            }
        } catch (IndexOutOfBoundsException ex) {
            view.showSnackBar("No tiene nombre del contacto.");
        }
    }

    public void onClickBtnEdit(int position, int type) {
        String phone = "";

        if (type == 1) {
            phone = telefonoItems.get(position).getTelefono();
        } else {
            phone = celularItems.get(position).getTelefono();
        }

        EditarNumeroTelefonoDialog dialog = new EditarNumeroTelefonoDialog();
        Bundle bundle = new Bundle();
        bundle.putString("phone", phone);
        bundle.putInt("type", type);
        bundle.putInt("position", position);
        dialog.setArguments(bundle);
        dialog.show(activity.getSupportFragmentManager(), "EditarNumeroTelefonoDialog");
    }

    private void loadDataRuta() {
        ruta = (Ruta) args.getSerializable("guias");
    }

    private void showDetalleRuta() {
        if (existRuta()) {
            String horario = ruta.getHorarioEntrega();

            if (ruta.getResultadoGestion() != 0) {
                GuiaGestionada guiaGestionada = interactor.selectRutaGestionada(ruta.getIdServicio(),
                        ruta.getLineaNegocio());
                if (guiaGestionada != null) {
                    try {
                        Date fechaHoraGestion = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(
                                guiaGestionada.getFecha() + " " + guiaGestionada.getHora());
                        horario = CommonUtils.fomartHorarioAproximado(fechaHoraGestion.getTime(), true);
                    } catch (ParseException ex) {
                        ex.printStackTrace();
                    }
                }
            } else {
                if (ModelUtils.isGuiaEntrega(ruta.getTipo())) {
                    horario = CommonUtils.fomartHorarioAproximado(ruta.getHorarioAproximado(), true);
                }
            }

            int lblColorHorario = ModelUtils.getLblColorHorario(ruta.getTipo(),
                    ruta.getTipoEnvio(), ruta.getFechaRuta(), ruta.getHorarioEntrega());

            ruta.setHorarioEntrega(horario);
            view.showDetalleRuta(ruta);
            view.setLblColorHorario(lblColorHorario);
        }
    }

    private void showAlertaEstadoShipper() {
        int idResIcon = 0, bg = 0;
        String msg = "";

        if (ruta.getEstadoShipper() != null) {
            if (ruta.getEstadoShipper().equals("1")) { // Inicial
                idResIcon = R.drawable.ic_estado_cliente_inicial_white;
                msg = "El estado del shipper es inicial, tomar en cuenta al ";
                bg = R.color.yellow_2;
            } else if (ruta.getEstadoShipper().equals("2")) { // Normal

            } else if (ruta.getEstadoShipper().equals("3")) { // Vip
                idResIcon = R.drawable.ic_estado_cliente_vip_white;
                msg = "El estado del shipper es vip, tomar en cuenta al ";
                bg = R.color.green_4;
            } else if (ruta.getEstadoShipper().equals("4")) { // Critico
                idResIcon = R.drawable.ic_estado_cliente_critico_white;
                msg = "El estado del shipper es crítico, tomar en cuenta al ";
                bg = R.color.colorPrimary;
            }
        }

        if (!msg.isEmpty()) {
            if (ModelUtils.isGuiaEntrega(ruta.getTipo())) {
                msg += "entregar.";
            } else if (ModelUtils.isGuiaRecoleccion(ruta.getTipo())) {
                msg += "recolectar.";
            }
            view.showAlertaEstadoShipper(idResIcon, bg, msg);
        }
    }

    private void showTelContactoGestion() {
        if (ruta.getTelContactoGestion() != null && !ruta.getTelContactoGestion().trim().isEmpty()) {
            String[] telefonosList = ruta.getTelContactoGestion().trim().split(",");

            for (String telefono: telefonosList) {
                if (!telefono.isEmpty()) {
                    TelefonoGuiaItem telefonoGuiaItem = new TelefonoGuiaItem(telefono.trim());
                    telContactoGestionItems.add(telefonoGuiaItem);
                }
            }

            view.showTelContactoGestion(telContactoGestionItems);
        }

        if (ruta.getNombreTelContactoGestion() != null && !ruta.getNombreTelContactoGestion().trim().isEmpty()) {
            String[] nombresList = ruta.getNombreTelContactoGestion().trim().split(",");

            for (String nombre: nombresList) {
                if (!nombre.isEmpty()) {
                    nombreTelContactoGestionItems.add(nombre);
                }
            }
        }
    }

    private void showTelefonos() {
        if (!ruta.getTelefono().trim().isEmpty()) {
            String[] telefonosList = ruta.getTelefono().trim().split(",");

            for (String telefono: telefonosList) {
                if (!telefono.isEmpty()) {
                    TelefonoGuiaItem telefonoGuiaItem = new TelefonoGuiaItem(telefono.trim());
                    telefonoItems.add(telefonoGuiaItem);
                }
            }

            view.showTelefonos(telefonoItems);
        }
    }

    private void showCelulares() {
        try {
            if (!ruta.getCelular().trim().isEmpty()) {
                String[] celularList = ruta.getCelular().trim().split(",");

                for (String celular: celularList) {
                    if (!celular.isEmpty()) {
                        TelefonoGuiaItem telefonoGuiaItem = new TelefonoGuiaItem(celular.trim());
                        celularItems.add(telefonoGuiaItem);
                    }
                }

                view.showCelulares(celularItems);
            }
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    private void showHabilitantes() {
        if (ruta.getHabilitantes() != null
                && !ruta.getHabilitantes().isEmpty()) {

            String[] habilitantes = ruta.getHabilitantes().split("-");

            ArrayList<String> habilitantesList = new ArrayList<>();

            for (int j = 0; j < habilitantes.length; j++) {
                habilitantesList.add("● " + habilitantes[j]);
            }

            view.showHabilitantes(habilitantesList);
        }
    }

    private void loadDescargaRutaHelper() {
        if (existRuta()) {
            showDescargaRutaHelper = new ShowDescargaRutaHelper(
                    view.getViewContext(),
                    new ArrayList<Ruta>(Arrays.asList(ruta)),
                    args.getInt("numVecesGestionado"));
        }
    }

    private boolean existRuta() {
        if (ruta != null) {
            return true;
        }
        return false;
    }

    private boolean validateOrdenDescarga() {
        if (existRuta()) {
            if (ruta.getSecuencia().equals("1")) {
                return true;
            }
        }
        return false;
    }

    private boolean isGuiaGestionFinalizado() {
        if (existRuta()) {
            DescargaRuta descargaRuta = interactor.selectDescargaRuta(ruta.getIdServicio(),
                    ruta.getLineaNegocio());

            if (descargaRuta != null) {
                if (descargaRuta.getProcesoDescarga() == DescargaRuta.Entrega.FINALIZADO) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isGuiaGestionNoEntrega() {
        if (existRuta()) {
            DescargaRuta descargaRuta = interactor
                    .selectDescargaRuta(ruta.getIdServicio(), ruta.getLineaNegocio());

            if (descargaRuta != null) {
                if (descargaRuta.getProcesoDescarga() == DescargaRuta.Entrega.ENTREGAR) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean validateEstadoRuta() {
        List<EstadoRuta> estadoRuta = interactor.selectAllEstadoRuta();

        int estados = 0;
        boolean validateEstado = false;

        if (estadoRuta.size() > 0) {
            for (EstadoRuta estado: estadoRuta) {
                if (estado.getEstado() == EstadoRuta.Estado.INICIADO) {
                    estados = 1;
                }

                if (estado.getEstado() == EstadoRuta.Estado.FINALIZADO) {
                    estados = 2;
                }
            }
        } else {
            Log.d(TAG, "estadoRuta: " + estadoRuta.size());
        }

        switch (estados) {
            case 0:
                BaseModalsView.showAlertDialog(view.getViewContext(),
                        R.string.activity_detalle_ruta_title_ruta_no_iniciada,
                        R.string.activity_detalle_ruta_message_debe_iniciar_ruta,
                        R.string.text_aceptar, null);
                break;
            case 1:
                validateEstado = true;
                break;
            case 2:
                BaseModalsView.showAlertDialog(view.getViewContext(),
                        R.string.activity_detalle_ruta_title_ruta_finalizada,
                        R.string.activity_detalle_ruta_message_ruta_finalizada,
                        R.string.text_aceptar, null);
                break;
        }

        return validateEstado;
    }

    private void registerCallStateListener() {
        callStateListener = new CallStateListener();

        telephonyManager = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(callStateListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    /**
     * Broadcast
     *
     * {@link EntregaGEPresenter#sendOnDescargaFinalizadaReceiver()}
     */
    private BroadcastReceiver descargaFinalizadaReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ((AppCompatActivity) view.getViewContext()).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    BaseModalsView.hideProgressDialog();
                    ((AppCompatActivity) view.getViewContext()).finish();
                    ((AppCompatActivity) view.getViewContext()).overridePendingTransition(
                            R.anim.not_slide, R.anim.slide_exit_out_right);
                }
            });
        }
    };

    /**
     * Broadcast
     *
     * {@link EditarNumeroTelefonoDialog#sendGuardarNumeroTelefonoReceiver()}
     */
    private BroadcastReceiver guardarNumeroTelefonoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            new SaveNumeroTelefonoTask(intent.getStringExtra("phone"),
                    intent.getIntExtra("type", 0),
                    intent.getIntExtra("position", 0)).execute();
        }
    };

    /**
     * Listener to detect incoming calls.
     */
    private class CallStateListener extends PhoneStateListener {

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);

            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    // called when someone is ringing to this phone
                    Log.d("MainActivity", "Ringing 2: " + incomingNumber);
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    // called when someone is ringing to this phone
                    Log.d("MainActivity", "OffHook 2: " + incomingNumber);
                    callInitDate = new Date();
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    // called when someone is ringing to this phone
                    callEndDate = new Date();

                    if (isButtonPhoneClicked) {
                        Log.d("MainActivity", "LLAMADA FINALIZADA ACEPTADA: " + incomingNumber);

                        String numberPhoneCalled = uriPhoneCalled.getSchemeSpecificPart().replace(" ", "");

                        if (CommonUtils.isAndroidPie() ||
                                uriPhoneCalled != null && numberPhoneCalled.equals(incomingNumber)) {
                            Log.d("MainActivity", "LLAMADA FINALIZADA VALIDA: " + incomingNumber);

                            selectedIndexTipoResultadoLlamada = 0;

                            activity.runOnUiThread(() -> {
                                ModalHelper.getBuilderAlertDialog(activity)
                                        .setTitle("Seleccione el resultado de la llamada realizada")
                                        .setCancelable(false)
                                        .setSingleChoiceItems(new String[]{"Contactada", "No Contactada"}, 0,
                                                (dialog, which) -> selectedIndexTipoResultadoLlamada = which)
                                        .setPositiveButton(R.string.text_continuar, (dialog, which) -> {
                                            if (selectedIndexTipoResultadoLlamada == 0) {
                                                saveRegistroLlamada("134");
                                            } else {
                                                showAlertLLamadaFallida();
                                            }
                                        })
                                        .show();
                            });
                        } else {
                            Log.d("MainActivity", "LLAMADA FINALIZADA INVALIDA: " + incomingNumber);
                            view.showToast(
                                    "El número de celular/telefono no coincide con los numeros registrados de la guía. Número Registrado (" + uriPhoneCalled.getSchemeSpecificPart() + ") " +
                                    "Número Llamada (" + incomingNumber + ")");
                            uriPhoneCalled = null;
                            isButtonPhoneClicked = false;
                            callInitDate = null;
                            callEndDate = null;
                        }
                    } else {
                        Log.d("MainActivity", "LLAMADA FINALIZADA NO ACEPTADA: " + incomingNumber);
                    }
                    break;
            }
        }

        private void showAlertLLamadaFallida() {
            selectedIndexTipoResultadoLlamada = 0;

            ModalHelper.getBuilderAlertDialog(activity)
                    .setTitle("Seleccione el motivo por el cual la llamada no fue contactada")
                    .setCancelable(false)
                    .setSingleChoiceItems(motivoGestionLlamadaItems, 0,
                            (dialog, which) -> selectedIndexTipoResultadoLlamada = which)
                    .setPositiveButton(R.string.text_aceptar, (dialog, which) ->
                            saveRegistroLlamada(dbMotivoGestionLlamada.get(selectedIndexTipoResultadoLlamada).getIdMotivo()))
                    .show();
        }

        private void saveRegistroLlamada(String idMotivo) {
            //Log.d("MainActivity", "Fecha inicial: " + new SimpleDateFormat("HH:mm:ss").format(callInitDate));
            //Log.d("MainActivity", "Fecha final: " + new SimpleDateFormat("HH:mm:ss").format(callEndDate));

            if (uriPhoneCalled != null && callInitDate != null && callEndDate != null) {
                GestionLlamada gestionLlamada = new GestionLlamada(
                        Preferences.getInstance().getString("idUsuario", ""),
                        ruta.getIdServicio(),
                        idMotivo,
                        uriPhoneCalled.getSchemeSpecificPart().replace(" ", ""),
                        callInitDate.getTime() + "",
                        callEndDate.getTime() + "",
                        ruta.getLineaNegocio(),
                        Data.Sync.PENDING
                );
                gestionLlamada.save();

                uriPhoneCalled = null;
                isButtonPhoneClicked = false;
                callInitDate = null;
                callEndDate = null;
            }
        }
    }

    private void loadMotivosGestionLlamada() {
        if (dbMotivoGestionLlamada.size() == 0) {
            dbMotivoGestionLlamada = interactor.selectAllMotivos(MotivoDescarga.Tipo.GESTION_CON_LLAMADA,
                    ruta.getLineaNegocio());

            motivoGestionLlamadaItems = new String[dbMotivoGestionLlamada.size()];

            for (int i = 0; i < dbMotivoGestionLlamada.size(); i++) {
                motivoGestionLlamadaItems[i] = WordUtils.capitalize(dbMotivoGestionLlamada.get(i).getDescripcion().toLowerCase());
            }
        }
    }

    private void getMotivosGestionLlamada() {
        if (CommonUtils.validateConnectivity(view.getViewContext())) {
            BaseModalsView.showProgressDialog(activity, R.string.text_actualizando_motivos);
            final RequestCallback callback = new RequestCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    try {
                        if (response.getBoolean("success")) {
                            interactor.deleteMotivos(MotivoDescarga.Tipo.GESTION_CON_LLAMADA);
                            saveMotivos(response.getJSONArray("data"));
                            BaseModalsView.hideProgressDialog();
                            BaseModalsView.showToast(activity, R.string.activity_detalle_ruta_msg_descarga_motivo_gestion_llamada, Toast.LENGTH_LONG);
                        } else {
                            BaseModalsView.hideProgressDialog();
                            BaseModalsView.showToast(activity, response.getString("msg_error"), Toast.LENGTH_LONG);
                        }
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                        BaseModalsView.hideProgressDialog();
                        view.showSnackBar(R.string.json_object_exception);
                    }
                }

                @Override
                public void onError(VolleyError error) {
                    BaseModalsView.hideProgressDialog();
                    view.showSnackBar(R.string.volley_error_message);
                }
            };

            String params[] = new String[] {
                    MotivoDescarga.Tipo.GESTION_CON_LLAMADA + "",
                    Preferences.getInstance().getString("idUsuario", "")
            };

            interactor.getMotivos(params, callback);
        }
    }

    private void saveMotivos(JSONArray data) throws JSONException {
        JSONObject jsonObject;
        for (int i = 0; i < data.length(); i++) {
            jsonObject = data.getJSONObject(i);
            MotivoDescarga motivo = new MotivoDescarga(
                    Preferences.getInstance().getString("idUsuario", ""),
                    MotivoDescarga.Tipo.GESTION_CON_LLAMADA,
                    jsonObject.getString("mot_id"),
                    jsonObject.getString("codigo"),
                    jsonObject.getString("descri"),
                    jsonObject.getString("linea")
            );
            motivo.save();
        }
    }

    private class SaveNumeroTelefonoTask extends AsyncTaskCoroutine<String, String> {

        private String phone;
        private int type, position;

        public SaveNumeroTelefonoTask(String phone, int type, int position) {
            this.phone = phone;
            this.type = type;
            this.position = position;
        }

        @Override
        public String doInBackground(String... strings) {
            Ruta g = RutaPendienteInteractor.selectRuta(ruta.getIdServicio(), ruta.getLineaNegocio());

            if (g != null) {
                if (type == 1) {
                    telefonoItems.get(position).setTelefono(phone);
                    phone = "";
                    for (TelefonoGuiaItem item: telefonoItems) {
                        phone += item.getTelefono() + ",";
                    }
                    g.setTelefono(phone);
                } else {
                    celularItems.get(position).setTelefono(phone);
                    phone = "";
                    for (TelefonoGuiaItem item: celularItems) {
                        phone += item.getTelefono() + ",";
                    }
                    g.setCelular(phone);
                }
                g.save();
                sendSincronizarNumeroTelefonoListaGuiasPendientesReceiver(ruta.getIdServicio(),
                        ruta.getLineaNegocio(), phone, type);
            } else {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        view.showToast("Ocurrió un error al guardar el número.");
                    }
                });
            }
            return null;
        }

        @Override
        public void onPostExecute(String s) {
            super.onPostExecute(s);
            if (type == 1) {
                view.showTelefonos(telefonoItems);
            } else {
                view.showCelulares(celularItems);
            }
        }
    }

    /**
     * Receiver
     *
     * {@link DetalleRutaPresenter#guardarNumeroTelefonoReceiver}
     */
    private void sendSincronizarNumeroTelefonoListaGuiasPendientesReceiver(String idServicio, String lineaNegocio, String phone, int type) {
        Intent intent = new Intent(LocalAction.SINCRONIZAR_NUMERO_TELEFONO_LISTA_GUIAS_PENDIENTES_ACTION);
        intent.putExtra("idServicio", idServicio);
        intent.putExtra("lineaNegocio", lineaNegocio);
        intent.putExtra("phone", phone);
        intent.putExtra("type", type);
        LocalBroadcastManager.getInstance(activity).sendBroadcast(intent);
    }

}