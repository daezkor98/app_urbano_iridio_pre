package com.urbanoexpress.iridio3.pe.presenter;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.messaging.FirebaseMessaging;
import com.urbanoexpress.iridio3.pe.util.async.AsyncTaskCoroutine;
import com.urbanoexpress.iridio3.pe.R;
import com.urbanoexpress.iridio3.pe.application.AndroidApplication;
import com.urbanoexpress.iridio3.pe.model.entity.Data;
import com.urbanoexpress.iridio3.pe.model.entity.EstadoRuta;
import com.urbanoexpress.iridio3.pe.model.entity.GestionLlamada;
import com.urbanoexpress.iridio3.pe.model.entity.GuiaGestionada;
import com.urbanoexpress.iridio3.pe.model.entity.Imagen;
import com.urbanoexpress.iridio3.pe.model.entity.LogErrorSync;
import com.urbanoexpress.iridio3.pe.model.entity.MotivoDescarga;
import com.urbanoexpress.iridio3.pe.model.entity.Ruta;
import com.urbanoexpress.iridio3.pe.model.entity.TrackLocation;
import com.urbanoexpress.iridio3.pe.model.interactor.ConsideracionesImportantesRutaInteractor;
import com.urbanoexpress.iridio3.pe.model.interactor.DataSyncInteractor;
import com.urbanoexpress.iridio3.pe.model.interactor.RutaPendienteInteractor;
import com.urbanoexpress.iridio3.pe.model.interactor.callback.RequestCallback;
import com.urbanoexpress.iridio3.pe.model.util.ModelUtils;
import com.urbanoexpress.iridio3.pe.presenter.helpers.ForzarCierreRutaHelper;
import com.urbanoexpress.iridio3.pe.services.DataSyncService;
import com.urbanoexpress.iridio3.pe.ui.ConsideracionesImportantesRutaActivity;
import com.urbanoexpress.iridio3.pe.ui.InformacionRutaActivity;
import com.urbanoexpress.iridio3.pe.ui.ManifestarGuiaActivity;
import com.urbanoexpress.iridio3.pe.ui.MapaRutaDelDiaActivity;
import com.urbanoexpress.iridio3.pe.ui.RutaActivity;
import com.urbanoexpress.iridio3.pe.ui.TransferirGuiaActivity;
import com.urbanoexpress.iridio3.pe.ui.adapter.ManifiestoAdapter;
import com.urbanoexpress.iridio3.pe.ui.dialogs.AsignarRutaDialog;
import com.urbanoexpress.iridio3.pe.ui.dialogs.CodigoQRRutaDialog;
import com.urbanoexpress.iridio3.pe.ui.dialogs.EditarPlacaDialog;
import com.urbanoexpress.iridio3.pe.ui.helpers.ModalHelper;
import com.urbanoexpress.iridio3.pe.ui.interfaces.OnClickItemListener;
import com.urbanoexpress.iridio3.pe.ui.model.ManifiestoItem;
import com.urbanoexpress.iridio3.pe.util.CommonUtils;
import com.urbanoexpress.iridio3.pe.util.LocationUtils;
import com.urbanoexpress.iridio3.pe.util.Preferences;
import com.urbanoexpress.iridio3.pe.util.Session;
import com.urbanoexpress.iridio3.pe.util.constant.LocalAction;
import com.urbanoexpress.iridio3.pe.view.BaseModalsView;
import com.urbanoexpress.iridio3.pe.view.RutaView;

import org.apache.commons.lang3.text.WordUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Created by mick on 26/07/16.
 */
public class RutaPresenter extends BaseModalsView implements OnClickItemListener {

    private static final String TAG = RutaPresenter.class.getSimpleName();

    private RutaView view;

    private RutaPendienteInteractor interactor;
    private DataSyncInteractor dataSyncInteractor;

    private List<EstadoRuta> estadoRuta;
    private List<EstadoRuta> estadoRutaCierre;

    private List<Ruta> rutas = Collections.emptyList();
    private List<Ruta> manifiestos = Collections.emptyList();

    private List<MotivoDescarga> dbMotivoNoHuboTiempo = Collections.emptyList();
    private String[] motivoNoHuboTiempoItems;

    private AppCompatActivity activity;

    private AlertDialog dialog;

    private int msgResId = 0;

    private boolean showMessageErrorEstadoRuta = true;

    // Motivos eliminar datos ruta
    private final int MOTIVO_CERRAR_RUTA = 1;
    private final int MOTIVO_ELIMINAR_MANIFIESTO = 2;

    private int selectedIndexMotivoNoHuboTiempo = -1;

    private String firebaseToken = "";

    public RutaPresenter(RutaView view) {
        this.view = view;
        this.activity = (AppCompatActivity) view.getContextView();
        interactor = new RutaPendienteInteractor(view.getContextView());
        dataSyncInteractor = new DataSyncInteractor(view.getContextView());
        init();
    }

    private void init() {
        LocalBroadcastManager.getInstance(view.getContextView())
                .registerReceiver(rutaIniciadaReceiver, new IntentFilter(LocalAction.INICIAR_RUTA_DEL_DIA_ACTION));
        LocalBroadcastManager.getInstance(view.getContextView())
                .registerReceiver(rutaFinalizadaReceiver, new IntentFilter("OnRutaFinalizada"));
        LocalBroadcastManager.getInstance(view.getContextView())
                .registerReceiver(transferenciaGuiaFinalizadaReceiver,
                        new IntentFilter(LocalAction.TRANSFERENCIA_GUIA_FINALIZADA_ACTION));
        LocalBroadcastManager.getInstance(view.getContextView())
                .registerReceiver(resultScannReceiver,
                        new IntentFilter(LocalAction.LOCAL_ACTION_BARCODE_SCAN_RESULT));

        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(s -> firebaseToken = s);

        LocationServices.getFusedLocationProviderClient(AndroidApplication.getAppContext())
                .getLastLocation().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                Log.d(TAG, "TEST LATITUDE: " + task.getResult().getLatitude());
                Log.d(TAG, "TEST LONGITUDE: " + task.getResult().getLongitude());
                LocationUtils.setCurrentLocation(task.getResult());
            }
        });

        new ConfigUIEstadoRutaTask().execute();
    }

    public void onClickFab() {
        loadEstadoRuta();
        if (existEstadoRuta()) { // Ya Inicio Ruta
            actionTerminarRuta();
        } else { // Aun no iniciar Ruta
            actionIniciarRuta();
        }
    }

    public void onClickBoxConsideracionesImportantesRuta() {
        activity.startActivity(new Intent(activity, ConsideracionesImportantesRutaActivity.class));
        activity.overridePendingTransition(R.anim.slide_enter_from_right, R.anim.not_slide);
        view.setVisibilityBoxConsideracionesImportantesRuta(View.GONE);
    }

    public void onActionEliminarManifiesto() {
        long totalPendientes = interactor.getTotalRutasPendientes();
        manifiestos = interactor.selectManifiestos();

        if (totalPendientes > 0) {
            RecyclerView lvManifiestos;
            View view = activity.getLayoutInflater().inflate(R.layout.modal_eliminar_manifiesto, null);
            lvManifiestos = (RecyclerView) view.findViewById(R.id.lvManifiestos);
            lvManifiestos.setLayoutManager(new LinearLayoutManager(activity));

            ArrayList<ManifiestoItem> data = new ArrayList<>();

            for (Ruta ruta : manifiestos) {
                ManifiestoItem manifiestoItem = new ManifiestoItem(ruta.getIdManifiesto(), "");
                data.add(manifiestoItem);
            }

            ManifiestoAdapter adapter = new ManifiestoAdapter(data, this);
            lvManifiestos.setAdapter(adapter);

            dialog = new AlertDialog.Builder(activity)
                    .setView(view).create();
            dialog.show();
        } else {
            showToast(view.getContextView(),
                    R.string.fragment_ruta_pendiente_message_no_hay_rutas, Toast.LENGTH_LONG);
        }
    }

    public void onActionInfoRutaDelDia() {
        long totalPendientes = interactor.getTotalRutasPendientes();
        long totalGestionados = interactor.getTotalRutasGestionadas();

        if (totalPendientes > 0 || totalGestionados > 0) {
            activity.startActivity(new Intent(activity, InformacionRutaActivity.class));
            activity.overridePendingTransition(R.anim.slide_enter_from_right, R.anim.not_slide);
        } else {
            showToast(view.getContextView(),
                    R.string.fragment_ruta_pendiente_message_no_hay_rutas,
                    Toast.LENGTH_LONG);
        }
    }

    public void onActionManifestarGuia() {
        long totalGuias = interactor.getTotalRutasPendientes();

        if (totalGuias == 0) {
            totalGuias = interactor.getTotalRutasGestionadas();
        }

        if (totalGuias == 0) {
            showToast(view.getContextView(),
                    R.string.fragment_ruta_pendiente_message_no_hay_rutas,
                    Toast.LENGTH_LONG);
            return;
        }

        activity.startActivity(new Intent(activity, ManifestarGuiaActivity.class));
        activity.overridePendingTransition(R.anim.slide_enter_from_right, R.anim.not_slide);
    }

    public void onActionRecolectarValija() {
        long totalGuias = interactor.getTotalRutasPendientes();

        if (totalGuias == 0) {
            totalGuias = interactor.getTotalRutasGestionadas();
        }

        if (totalGuias == 0) {
            showToast(view.getContextView(),
                    R.string.fragment_ruta_pendiente_message_no_hay_rutas,
                    Toast.LENGTH_LONG);
            return;
        } else {
            estadoRuta = interactor.selectAllEstadoRuta();
            if (!existEstadoRuta()) {
                view.showMessageIniciarRuta();
                return;
            }
        }

        view.navigateToRecolectarValijaActivity();
    }

    public void onActionConsideracionesImportantes() {
        long totalPendientes = interactor.getTotalRutasPendientes();
        long totalGestionados = interactor.getTotalRutasGestionadas();

        if (totalPendientes > 0 || totalGestionados > 0) {
            if (ConsideracionesImportantesRutaInteractor.getTotalRutasImportantes() > 0) {
                onClickBoxConsideracionesImportantesRuta();
            } else {
                showToast(view.getContextView(),
                        R.string.fragment_ruta_pendiente_message_no_hay_consideraciones_importantes_ruta,
                        Toast.LENGTH_LONG);
            }
        } else {
            showToast(view.getContextView(),
                    R.string.fragment_ruta_pendiente_message_no_hay_rutas,
                    Toast.LENGTH_LONG);
        }
    }

    public void onActionTransferirGuias() {
        long totalPendientes = interactor.getTotalRutasPendientes();

        if (totalPendientes > 0) {
            activity.startActivity(new Intent(activity, TransferirGuiaActivity.class));
            activity.overridePendingTransition(R.anim.slide_enter_from_right, R.anim.not_slide);
        } else {
            showToast(view.getContextView(),
                    R.string.fragment_ruta_pendiente_message_no_hay_rutas,
                    Toast.LENGTH_LONG);
        }
    }

    public void onActionAsignarRuta() {
        // No debe haber rutas pendientes
        // No debe haber ruta iniciada
        long totalEstadoRutas = interactor.getTotalAllEstadoRuta();

        if (totalEstadoRutas == 0) {
            if (!isRutasPendientes()) {
                AsignarRutaDialog dialog = new AsignarRutaDialog();
                dialog.show(activity.getSupportFragmentManager(), AsignarRutaDialog.TAG);
            } else {
                BaseModalsView.showToast(activity,
                        R.string.activity_ruta_message_no_puede_asignar_ruta,
                        Toast.LENGTH_LONG);
            }
        } else {
            BaseModalsView.showToast(activity,
                    R.string.activity_ruta_message_ruta_iniciada_asignar_ruta,
                    Toast.LENGTH_LONG);
        }
    }

    public void onActionEditarPlaca() {
        // Debe haber rutas pendientes
        // No debe haber ruta iniciada
        long totalEstadoRutas = interactor.getTotalAllEstadoRuta();

        if (totalEstadoRutas == 0) {
            List<Ruta> rutasPendientes = interactor.selectRutasPendientes();
            if (rutasPendientes.size() > 0) {
                EditarPlacaDialog dialog = EditarPlacaDialog.newInstance();
                dialog.show(activity.getSupportFragmentManager(), EditarPlacaDialog.TAG);
            } else {
                showToast(view.getContextView(),
                        R.string.fragment_ruta_pendiente_message_no_hay_rutas,
                        Toast.LENGTH_LONG);
            }
        } else {
            BaseModalsView.showToast(activity,
                    R.string.activity_ruta_message_ruta_iniciada_editar_placa,
                    Toast.LENGTH_LONG);
        }
    }

    public void onActionCodigoQRRuta() {
        List<Ruta> pendientes = interactor.selectRutasPendientes();

        if (pendientes.size() > 0) {
            CodigoQRRutaDialog dialog = new CodigoQRRutaDialog();
            Bundle bundle = new Bundle();
            bundle.putString("idRuta", pendientes.get(0).getIdRuta());
            dialog.setArguments(bundle);
            dialog.show(activity.getSupportFragmentManager(), "CodigoQRRutaDialog");
        } else {
            showToast(view.getContextView(),
                    R.string.fragment_ruta_pendiente_message_no_hay_rutas,
                    Toast.LENGTH_LONG);
        }
    }

    public void onActionForzarCierreRuta() {
        new ForzarCierreRutaHelper(view.getContextView(),
                ForzarCierreRutaHelper.ActionContext.RUTA_DEL_DIA).init();
    }

    public void onActionMapaRutaDelDia() {
        long totalPendientes = interactor.getTotalRutasPendientes();
        long totalGestionados = interactor.getTotalRutasGestionadas();

        if (totalPendientes > 0 || totalGestionados > 0) {
            activity.startActivity(new Intent(activity, MapaRutaDelDiaActivity.class));
            activity.overridePendingTransition(R.anim.slide_enter_from_right, R.anim.not_slide);
        } else {
            showToast(view.getContextView(),
                    R.string.fragment_ruta_pendiente_message_no_hay_rutas,
                    Toast.LENGTH_LONG);
        }
    }

    public void onDestroyActivity() {
        LocalBroadcastManager.getInstance(activity)
                .unregisterReceiver(rutaFinalizadaReceiver);
        LocalBroadcastManager.getInstance(activity)
                .unregisterReceiver(rutaIniciadaReceiver);
        LocalBroadcastManager.getInstance(activity)
                .unregisterReceiver(transferenciaGuiaFinalizadaReceiver);
        LocalBroadcastManager.getInstance(activity)
                .unregisterReceiver(resultScannReceiver);
    }

    private void setIconFab() {
        loadEstadoRuta();
        FloatingActionButton fab = (FloatingActionButton)
                view.baseFindViewById(R.id.fabIniciarTerminarRuta);
        if (existEstadoRuta()) { // Ya Inicio Ruta
            fab.setImageDrawable(ContextCompat.getDrawable(view.getContextView(),
                    R.drawable.ic_stop_white));
        } else { // Aun no iniciar Ruta
            fab.setImageDrawable(ContextCompat.getDrawable(view.getContextView(),
                    R.drawable.ic_near_me_white));
        }
    }

    public void loadConfigUIEstadoRuta() {
        new ConfigUIEstadoRutaTask().execute();
    }

    private void actionIniciarRuta() {
        showAlertDialog(view.getContextView(),
                R.string.activity_ruta_fab_menu_iniciar_ruta,
                R.string.activity_ruta_message_iniciar_ruta,
                R.string.activity_ruta_fab_menu_iniciar_ruta,
                (dialog, which) -> initNewEstadoRuta(EstadoRuta.Estado.INICIADO),
                R.string.text_cancelar, null);
    }

    private void actionTerminarRuta() {
        if (CommonUtils.validateConnectivity(view.getContextView())) {
            showAlertDialog(view.getContextView(),
                    R.string.activity_ruta_fab_menu_terminar_ruta,
                    R.string.activity_ruta_message_terminar_ruta,
                    R.string.activity_ruta_fab_menu_terminar_ruta, (dialog, which) ->
                            initNewEstadoRuta(EstadoRuta.Estado.FINALIZADO),
                    R.string.text_cancelar, null);
        }
    }

    @SuppressLint("MissingPermission")
    private void initNewEstadoRuta(final int estadoRuta) {
        new Thread(() -> {
            rutas = interactor.selectAllRutas();

            LocationServices.getFusedLocationProviderClient(AndroidApplication.getAppContext())
                    .getLastLocation().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    Log.d(TAG, "LATITUDE: " + task.getResult().getLatitude());
                    Log.d(TAG, "LONGITUDE: " + task.getResult().getLongitude());
                    LocationUtils.setCurrentLocation(task.getResult());
                }

                saveEstadoRuta(estadoRuta);
            });
        }).start();
    }

    private void saveEstadoRuta(int estado) {
        Log.d(TAG, "SAVE ESTADO RUTA: " + estado);
        if (existRutas()) {
            switch (estado) {
                case EstadoRuta.Estado.INICIADO:
                    if (existEstadoRuta()) {
                        if (isRutaFinalizada()) {
                            msgResId = R.string.activity_ruta_message_ruta_ya_finalizada;
                        } else {
                            msgResId = R.string.activity_ruta_message_ruta_ya_iniciada;
                        }
                    } else {
                        if (checkSolicitarKilometraje()) {
                            showMessageErrorEstadoRuta = false;
                            /*activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    IniciarTerminarRutaDelDiaDialog dialog = IniciarTerminarRutaDelDiaDialog
                                            .newInstance(EstadoRuta.Estado.INICIADO);
                                    dialog.show(activity.getSupportFragmentManager(), IniciarTerminarRutaDelDiaDialog.TAG);
                                }
                            });*/

                            validateSolicitaKilometrajeRequest(EstadoRuta.Estado.INICIADO);
                        } else {
                            msgResId = R.string.activity_ruta_message_ruta_iniciada_exitosamente;
                            newEstadoRuta(EstadoRuta.Estado.INICIADO);
                            sendOnRutaIniciadaReceiver();
                        }
                    }
                    break;
                case EstadoRuta.Estado.FINALIZADO:
                    if (existEstadoRuta()) {
                        if (isRutaFinalizada()) {
                            msgResId = R.string.activity_ruta_message_ruta_ya_finalizada;
                        } else {
                            if (isRutasPendientes()) {
                                if (!isPendientesLiquidacionDevolucion()) {
                                    if (!isDatosPorSincronizar()) {
                                        showMessageErrorEstadoRuta = false;
                                        activity.runOnUiThread(() -> showAlertDialog(view.getContextView(),
                                                R.string.activity_ruta_title_hay_rutas_pendientes,
                                                R.string.activity_ruta_message_hay_rutas_pendientes,
                                                R.string.activity_ruta_fab_menu_terminar_ruta,
                                                (dialog, which) -> loadAlertMotivoNoHuboTiempo(),
                                                R.string.text_cancelar, null));
                                    }
                                }
                            } else {
                                if (!isDatosPorSincronizar()) {
                                    showMessageErrorEstadoRuta = false;
                                    //if (checkSolicitarKilometraje()) {
                                        /*activity.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                IniciarTerminarRutaDelDiaDialog dialog = IniciarTerminarRutaDelDiaDialog
                                                        .newInstance(EstadoRuta.Estado.FINALIZADO);
                                                dialog.show(activity.getSupportFragmentManager(), IniciarTerminarRutaDelDiaDialog.TAG);
                                            }
                                        });*/

                                    //validateSolicitaKilometrajeRequest(EstadoRuta.Estado.FINALIZADO);
                                    //} else {
                                    terminarRuta();
                                    //}
                                }
                            }
                        }
                    } else {
                        msgResId = R.string.activity_ruta_message_ruta_no_iniciada;
                    }
                    break;
            }
        } else {
            if (existEstadoRuta()) {
                switch (estado) {
                    case EstadoRuta.Estado.INICIADO:
                        if (isRutaFinalizada()) {
                            msgResId = R.string.activity_ruta_message_ruta_ya_finalizada;
                        } else {
                            msgResId = R.string.activity_ruta_message_ruta_ya_iniciada;
                        }
                        break;
                    case EstadoRuta.Estado.FINALIZADO:
                        if (isRutaFinalizada()) {
                            msgResId = R.string.activity_ruta_message_ruta_ya_finalizada;
                        } else {
                            if (!isDatosPorSincronizar()) {
                                showMessageErrorEstadoRuta = false;
                                //if (checkSolicitarKilometraje()) {
                                    /*activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            IniciarTerminarRutaDelDiaDialog dialog = IniciarTerminarRutaDelDiaDialog
                                                    .newInstance(EstadoRuta.Estado.FINALIZADO);
                                            dialog.show(activity.getSupportFragmentManager(), IniciarTerminarRutaDelDiaDialog.TAG);
                                        }
                                    });*/

                                //validateSolicitaKilometrajeRequest(EstadoRuta.Estado.FINALIZADO);
                                //} else {
                                terminarRuta();
                                //}
                            }
                        }
                        break;
                }
            } else {
                Log.d(TAG, "PASO 4");
                msgResId = R.string.fragment_ruta_pendiente_message_no_hay_rutas;
            }
        }

        if (showMessageErrorEstadoRuta) {
            activity.runOnUiThread(() -> showToast(view.getContextView(), msgResId, Toast.LENGTH_LONG));
        }

        showMessageErrorEstadoRuta = true;
    }

    private void newEstadoRuta(int estado) {
        Log.d(TAG, "New Estado Ruta: " + estado);

        Date date = new Date();
        List<Ruta> idRutas = interactor.selectIdRutas();
        List<EstadoRuta> estadoRutas = new ArrayList<>();
        Log.d(TAG, "Total Rutas: " + idRutas.size());

        if (idRutas.size() == 0) {
            // En caso de no encontrar guias, buscar en el estadoruta
            // esto puede suceder cuando hacen una transferencia de todas las guias
            estadoRutas = interactor.selectAllEstadoRuta();
            Log.d(TAG, "Total EstadoRuta: " + estadoRutas.size());
        }

        estadoRutaCierre = new ArrayList<>();

        for (int i = 0; i < idRutas.size(); i++) {
            Log.d(TAG, "ID Ruta: " + idRutas.get(i).getIdRuta());
            EstadoRuta estadoRuta = new EstadoRuta(
                    Preferences.getInstance().getString("idUsuario", ""),
                    idRutas.get(i).getIdRuta(),
                    idRutas.get(i).getLineaNegocio(),
                    new SimpleDateFormat("dd/MM/yyyy").format(date),
                    new SimpleDateFormat("HH:mm:ss").format(date),
                    String.valueOf(LocationUtils.getLatitude()),
                    String.valueOf(LocationUtils.getLongitude()),
                    EstadoRuta.TipoRuta.RUTA_DEL_DIA,
                    Data.Delete.NO,
                    estado
            );
            if (estado == EstadoRuta.Estado.FINALIZADO) {
                estadoRuta.setDataSync(Data.Sync.MANUAL);
                estadoRutaCierre.add(estadoRuta);
            }
            estadoRuta.save();
        }

        for (int i = 0; i < estadoRutas.size(); i++) {
            Log.d(TAG, "ID Ruta: " + estadoRutas.get(i).getIdRuta());
            EstadoRuta estadoRuta = new EstadoRuta(
                    Preferences.getInstance().getString("idUsuario", ""),
                    estadoRutas.get(i).getIdRuta(),
                    estadoRutas.get(i).getLineaNegocio(),
                    new SimpleDateFormat("dd/MM/yyyy").format(date),
                    new SimpleDateFormat("HH:mm:ss").format(date),
                    String.valueOf(LocationUtils.getLatitude()),
                    String.valueOf(LocationUtils.getLongitude()),
                    EstadoRuta.TipoRuta.RUTA_DEL_DIA,
                    Data.Delete.NO,
                    estado
            );
            if (estado == EstadoRuta.Estado.FINALIZADO) {
                estadoRuta.setDataSync(Data.Sync.MANUAL);
                estadoRutaCierre.add(estadoRuta);
            }
            estadoRuta.save();
        }

        Log.d(TAG, "Total Estado Ruta Cierre: " + estadoRutaCierre.size());
    }

    private void terminarRuta() {
        /*showProgressCierreRuta();
        newEstadoRuta(EstadoRuta.Estado.FINALIZADO);
        sendDataTerminarRuta();*/
        new TerminarRutaTask().execute();
    }

    private class TerminarRutaTask extends AsyncTaskCoroutine<String, String> {

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            activity.runOnUiThread(() ->
                    BaseModalsView.showProgressDialog(view.getContextView(), R.string.text_terminando_ruta));
        }

        @Override
        public String doInBackground(String... strings) {
            newEstadoRuta(EstadoRuta.Estado.FINALIZADO);
            return null;
        }

        @Override
        public void onPostExecute(String s) {
            super.onPostExecute(s);
            sendDataTerminarRuta();
        }
    }

    private void loadEstadoRuta() {
        Log.d(TAG, "loadEstadoRuta");
        estadoRuta = interactor.selectAllEstadoRuta();
        Log.d(TAG, "Total estado ruta: " + estadoRuta.size());

//        List<Ruta> rutas = interactor.selectIdRutas();
//        Log.d(TAG, "Total Rutas: " + rutas.size());
//
//        for (int i = 0; i < rutas.size(); i++) {
//            Log.d(TAG, "ID RUTA: " + rutas.get(i).getIdRuta());
//        }
    }

    private boolean isDatosPorSincronizar() {
        boolean isDriver = "0".equals(Session.getUser().getFlag());

        List<GuiaGestionada> guiaGestionadas = dataSyncInteractor.selectAllRutaGestionada();
        List<Imagen> imagenes = dataSyncInteractor.selectAllImagenDescarga();
        List<EstadoRuta> estadoRuta = dataSyncInteractor.selectAllEstadoRutaSyncPending();
        List<GestionLlamada> gestionLlamadas = dataSyncInteractor.selectAllPendingGestionLlamada();
        List<TrackLocation> trackLocations = dataSyncInteractor.selectAllPendingTrackLocation();

        boolean hasPendingData = Stream.of(guiaGestionadas, imagenes, estadoRuta, gestionLlamadas)
                .allMatch(list ->!list.isEmpty());

        boolean shouldShowToast = (isDriver && hasPendingData) ||
                (!isDriver && hasPendingData && trackLocations.size() > 50);

        if (shouldShowToast) {
            activity.runOnUiThread(() -> showToast(view.getContextView(),
                    R.string.activity_ruta_message_datos_sync_pendientes, Toast.LENGTH_LONG));
            showMessageErrorEstadoRuta = false;
            return true;
        }

        return false;
    }

    /**
     * Receiver
     * <p>
     * {@link RutaPendientePresenter#rutaIniciadaReceiver}
     */
    private void sendOnRutaIniciadaReceiver() {
        Intent intent = new Intent(LocalAction.INICIAR_RUTA_DEL_DIA_ACTION);
        LocalBroadcastManager.getInstance(view.getContextView()).sendBroadcast(intent);
    }

    /**
     * Receiver
     * <p>
     * {@link RutaPendientePresenter#rutaFinalizadaReceiver}
     * {@link RutaGestionadaPresenter#rutaFinalizadaReceiver}
     * {@link RutaPresenter#rutaFinalizadaReceiver}
     */
    private void sendOnRutaFinalizadaReceiver() {
        Intent intent = new Intent("OnRutaFinalizada");
        LocalBroadcastManager.getInstance(view.getContextView()).sendBroadcast(intent);
    }

    /**
     * Receiver
     * <p>
     * {@link RutaPendientePresenter#manifiestoEliminadoReceiver}
     */
    private void sendOnManifiestoEliminadoReceiver(String idManifiesto) {
        Intent intent = new Intent("OnManifiestoEliminado");
        intent.putExtra("idManifiesto", idManifiesto);
        LocalBroadcastManager.getInstance(view.getContextView()).sendBroadcast(intent);
    }

    private boolean isRutaFinalizada() {
        for (int i = 0; i < estadoRuta.size(); i++) {
            if (estadoRuta.get(i).getEstado() == EstadoRuta.Estado.FINALIZADO) {
                return true;
            }
        }
        return false;
    }

    private boolean isRutasPendientes() {
        //List<Ruta> rutasPendientes = interactor.selectRutasPendientes();
        rutas = interactor.selectRutasPendientes();
        if (rutas.size() > 0) {
            return true;
        }
        return false;
    }

    private boolean isPendientesLiquidacionDevolucion() {
        for (int i = 0; i < rutas.size(); i++) {
            if (ModelUtils.validateTipoEnvio(rutas.get(i).getTipoEnvio(), Ruta.TipoEnvio.LIQUIDACION)
                    || ModelUtils.validateTipoEnvio(rutas.get(i).getTipoEnvio(), Ruta.TipoEnvio.DEVOLUCION)) {
                activity.runOnUiThread(() -> showToast(view.getContextView(),
                        R.string.activity_ruta_message_pendientes_liquidacion_devolucion,
                        Toast.LENGTH_LONG));
                showMessageErrorEstadoRuta = false;
                return true;
            }
        }
        return false;
    }

    private boolean existEstadoRuta() {
        return estadoRuta.size() > 0;
    }

    private boolean existRutas() {
        return rutas.size() > 0;
    }

    @Override
    public void onClickIcon(View view, final int position) {
        showAlertDialog(this.view.getContextView(),
                R.string.activity_ruta_title_eliminar_manifiesto,
                R.string.activity_ruta_message_eliminar_manifiesto,
                R.string.text_eliminar, (dialog, which) -> {
                    new Thread(() -> {
                        List<Ruta> rutas = interactor.selectRutasByIDManifiesto(
                                manifiestos.get(position).getIdManifiesto());
                        Log.d(TAG, "Total rutas: " + rutas.size());

                        for (Ruta ruta : rutas) {
                            Log.d(TAG, "Guia: " + ruta.getGuia());
                            ruta.setEliminado(Data.Delete.YES);
                            ruta.save();
                        }

                        if (manifiestos.size() == 1) {
                            showProgressEliminandoDatosRuta();
                            new DeleteDatosRutaTask(MOTIVO_ELIMINAR_MANIFIESTO).execute();
                        } else {
                            sendOnManifiestoEliminadoReceiver(manifiestos.get(position).getIdManifiesto());
                        }
                    }).start();
                    RutaPresenter.this.dialog.dismiss();
                }, R.string.text_cancelar, null);
    }

    @Override
    public void onClickItem(View view, int position) {

    }

    private void sendDataTerminarRuta() {
        RequestCallback callback = new RequestCallback() {
            @Override
            public void onSuccess(final JSONObject response) {
                try {
                    if (response.getBoolean("success")) {
                        Log.d(TAG, "syncEstadoRuta UPLOADED");

                        for (int i = 0; i < estadoRutaCierre.size(); i++) {
                            estadoRutaCierre.get(i).setDataSync(Data.Sync.SYNCHRONIZED);
                            estadoRutaCierre.get(i).save();
                        }

                        new DeleteDatosRutaTask(MOTIVO_CERRAR_RUTA).execute();
                    } else {
                        BaseModalsView.hideProgressDialog();
                        Log.d(TAG, "syncEstadoRuta");
                        Log.d(TAG, "success: false");
                        final String msgError = response.getString("msg_error");
                        LogErrorSync errorSync = new LogErrorSync(
                                "1_"+TAG,
                                Preferences.getInstance().getString("idUsuario", ""),
                                LogErrorSync.Tipo.ESTADO_RUTA,
                                "Error de servicio",
                                msgError,
                                response.getString("code_error"),
                                new Date().getTime() + ""
                        );
                        errorSync.save();

                        interactor.eliminarEstadoRutaSinSincronizar();

                        activity.runOnUiThread(() -> showToast(view.getContextView(),
                                msgError, Toast.LENGTH_SHORT));
                    }
                } catch (JSONException ex) {
                    BaseModalsView.hideProgressDialog();
                    ex.printStackTrace();
                    Log.d(TAG, "syncEstadoRuta");
                    Log.d(TAG, "JSONException");
                    LogErrorSync errorSync = new LogErrorSync(
                            "2_"+TAG,
                            Preferences.getInstance().getString("idUsuario", ""),
                            LogErrorSync.Tipo.ESTADO_RUTA,
                            "Error de conversión de datos",
                            ex.getMessage(),
                            Log.getStackTraceString(ex),
                            new Date().getTime() + ""
                    );
                    errorSync.save();

                    interactor.eliminarEstadoRutaSinSincronizar();

                    activity.runOnUiThread(() -> showToast(view.getContextView(),
                            R.string.json_object_exception, Toast.LENGTH_SHORT));
                }
            }

            @Override
            public void onError(VolleyError error) {
                BaseModalsView.hideProgressDialog();
                error.printStackTrace();
                LogErrorSync errorSync = new LogErrorSync(
                        "3_"+TAG,
                        Preferences.getInstance().getString("idUsuario", ""),
                        LogErrorSync.Tipo.ESTADO_RUTA,
                        "Error de conexión",
                        error.getMessage(),
                        Log.getStackTraceString(error),
                        new Date().getTime() + ""
                );
                errorSync.save();

                interactor.eliminarEstadoRutaSinSincronizar();

                activity.runOnUiThread(() -> showToast(view.getContextView(),
                        R.string.volley_error_message, Toast.LENGTH_SHORT));
            }
        };

        String idRutas = "";
        String idLineaNegocio = "";

        for (int i = 0; i < estadoRutaCierre.size(); i++) {
            if ((i + 1) == estadoRutaCierre.size()) {
                idRutas += estadoRutaCierre.get(i).getIdRuta();
                idLineaNegocio += estadoRutaCierre.get(i).getLineaNegocio();
            } else {
                idRutas += estadoRutaCierre.get(i).getIdRuta() + "|";
                idLineaNegocio += estadoRutaCierre.get(i).getLineaNegocio() + "|";
            }
        }

        String idMotivoNT = selectedIndexMotivoNoHuboTiempo >= 0
                ? dbMotivoNoHuboTiempo.get(selectedIndexMotivoNoHuboTiempo).getIdMotivo() : "0";

        String[] params = {
                idRutas,
                "2",
                estadoRutaCierre.get(0).getGpsLatitude(),
                estadoRutaCierre.get(0).getGpsLongitude(),
                estadoRutaCierre.get(0).getFecha(),
                estadoRutaCierre.get(0).getHora(),
                "0",
                idLineaNegocio,
                firebaseToken,
                idMotivoNT,
                estadoRutaCierre.get(0).getIdUsuario(),
                Session.getUser().getDevicePhone(),
                Session.getUser().getFlag()
        };

        interactor.uploadEstadoRutaKilometraje(params, callback);
    }

    private void validateSolicitaKilometrajeRequest(final int estadoRuta) {
        activity.runOnUiThread(() ->
                BaseModalsView.showProgressDialog(activity, R.string.text_validando_ruta));
        RequestCallback callback = new RequestCallback() {
            @Override
            public void onSuccess(final JSONObject response) {
                try {
                    BaseModalsView.hideProgressDialog();

                    if (response.getBoolean("success")) {
                        if (response.getJSONArray("data").length() > 0) {
                            final JSONObject objc = response.getJSONArray("data").getJSONObject(0);

                            if (estadoRuta == EstadoRuta.Estado.INICIADO) {
                                ModalHelper.getBuilderAlertDialog(activity)
                                        .setTitle("Confirme la placa de su ruta")
                                        .setMessage("Antes de iniciar su ruta confirme que la placa (" +
                                                objc.getString("placa") + ") es la que esta usando en su ruta.")
                                        .setPositiveButton(R.string.text_confirmar, (dialog, which) -> {
                                            /*try {
                                                if (Integer.valueOf(objc.getString("flag_km")) == 1) {
                                                    IniciarTerminarRutaDelDiaDialog iniciarTerminarRutaDelDiaDialog =
                                                            IniciarTerminarRutaDelDiaDialog.newInstance(estadoRuta,
                                                                    objc.getString("placa"),
                                                                    Integer.valueOf(objc.getString("ultimo_km")), "0");
                                                    iniciarTerminarRutaDelDiaDialog.show(
                                                            activity.getSupportFragmentManager(), IniciarTerminarRutaDelDiaDialog.TAG);
                                                } else {*/
                                            msgResId = R.string.activity_ruta_message_ruta_iniciada_exitosamente;
                                            newEstadoRuta(EstadoRuta.Estado.INICIADO);
                                            sendOnRutaIniciadaReceiver();
                                                /*}
                                            } catch (JSONException ex) {
                                                ex.printStackTrace();
                                                activity.runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        showToast(view.getContextView(),
                                                                R.string.json_object_exception,
                                                                Toast.LENGTH_LONG);
                                                    }
                                                });
                                            }*/
                                        })
                                        .setNegativeButton(R.string.text_editar, (dialog, which) -> {
                                            EditarPlacaDialog editarPlacaDialog = EditarPlacaDialog.newInstance();
                                            editarPlacaDialog.show(activity.getSupportFragmentManager(),
                                                    EditarPlacaDialog.TAG);
                                        })
                                        .setNeutralButton(R.string.text_cancelar, null)
                                        .show();
                            } else {
                                /*if (Integer.parseInt(objc.getString("flag_km")) == 1) {
                                    String idMotivoNT = selectedIndexMotivoNoHuboTiempo >= 0
                                            ? dbMotivoNoHuboTiempo.get(selectedIndexMotivoNoHuboTiempo).getIdMotivo() : "0";

                                    IniciarTerminarRutaDelDiaDialog iniciarTerminarRutaDelDiaDialog =
                                            IniciarTerminarRutaDelDiaDialog.newInstance(estadoRuta,
                                                    objc.getString("placa"),
                                                    Integer.parseInt(objc.getString("ultimo_km")), idMotivoNT);
                                    iniciarTerminarRutaDelDiaDialog.show(
                                            activity.getSupportFragmentManager(), IniciarTerminarRutaDelDiaDialog.TAG);
                                } else {*/
                                terminarRuta();
                                //}
                            }
                        } else {
                            showToast(view.getContextView(),
                                    "Ocurrió un error al procesar los datos de la ruta. Por favor, inténtalo de nuevo.",
                                    Toast.LENGTH_LONG);
                        }
                    } else {
                        final String msgError = response.getString("msg_error");
                        activity.runOnUiThread(() ->
                                showToast(view.getContextView(), msgError, Toast.LENGTH_LONG));
                    }
                } catch (JSONException ex) {
                    BaseModalsView.hideProgressDialog();
                    ex.printStackTrace();
                    activity.runOnUiThread(() -> showToast(view.getContextView(),
                            R.string.json_object_exception, Toast.LENGTH_LONG));
                }
            }

            @Override
            public void onError(VolleyError error) {
                BaseModalsView.hideProgressDialog();
                error.printStackTrace();
                activity.runOnUiThread(() -> showToast(view.getContextView(),
                        R.string.volley_error_message, Toast.LENGTH_LONG));
            }
        };

        JSONArray jsonRutas = new JSONArray();

        List<Ruta> rutas = interactor.selectIdRutas();

        List<EstadoRuta> estadoRutas = new ArrayList<>();

        if (rutas.size() == 0) {
            // En caso de no encontrar guias, buscar en el estadoruta
            // esto puede suceder cuando hacen una transferencia de todas las guias
            estadoRutas = interactor.selectAllEstadoRuta();
        }

        try {
            for (int i = 0; i < rutas.size(); i++) {
                jsonRutas.put(new JSONObject()
                        .put("vp_id_ruta", rutas.get(i).getIdRuta())
                        .put("vp_estado_ruta", estadoRuta == EstadoRuta.Estado.INICIADO ? "1" : "2")
                        .put("vp_linea_negocio", rutas.get(i).getLineaNegocio()));
            }

            for (int i = 0; i < estadoRutas.size(); i++) {
                jsonRutas.put(new JSONObject()
                        .put("vp_id_ruta", estadoRutas.get(i).getIdRuta())
                        .put("vp_estado_ruta", estadoRuta == EstadoRuta.Estado.INICIADO ? "1" : "2")
                        .put("vp_linea_negocio", estadoRutas.get(i).getLineaNegocio()));
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
        }

        String[] params = {
                jsonRutas.toString(),
                Preferences.getInstance().getString("idUsuario", "")
        };

        RutaPendienteInteractor.validateSolicitaKilometraje(params, callback);
    }

    private void loadAlertMotivoNoHuboTiempo() {
        loadMotivosGestionLlamada();

        if (motivoNoHuboTiempoItems.length > 0) {
            showAlertMotivoNoHuboTiempo();
        } else {
            requestGetMotivosNoHuboTiempo();
        }
    }

    private void showAlertMotivoNoHuboTiempo() {
        selectedIndexMotivoNoHuboTiempo = 0;

        ModalHelper.getBuilderAlertDialog(activity)
                .setTitle("Seleccione el motivo por el cual no terminó su ruta.")
                .setCancelable(false)
                .setSingleChoiceItems(motivoNoHuboTiempoItems, 0,
                        (dialog, which) -> selectedIndexMotivoNoHuboTiempo = which)
                .setPositiveButton(R.string.text_continuar, (dialog, which) -> {
                    /*if (checkSolicitarKilometraje()) {
                        validateSolicitaKilometrajeRequest(EstadoRuta.Estado.FINALIZADO);
                    } else {*/
                    terminarRuta();
                    //}
                })
                .show();
    }

    private void requestGetMotivosNoHuboTiempo() {
        BaseModalsView.showProgressDialog(view.getContextView(), R.string.text_actualizando_motivos);
        final RequestCallback callback = new RequestCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if (response.getBoolean("success")) {
                        interactor.deleteMotivos(MotivoDescarga.Tipo.NO_HUBO_TIEMPO);
                        saveMotivos(response.getJSONArray("data"));
                        hideProgressDialog();
                        showAlertMotivoNoHuboTiempo();
                    } else {
                        hideProgressDialog();
                        showToast(view.getContextView(),
                                response.getString("msg_error"), Toast.LENGTH_LONG);
                    }
                } catch (JSONException ex) {
                    ex.printStackTrace();
                    hideProgressDialog();
                    activity.runOnUiThread(() -> showSnackBar(view.baseFindViewById(R.id.toolbar),
                            R.string.text_actualizando_motivos_error, Snackbar.LENGTH_LONG,
                            R.string.text_volver_a_intentar, v -> requestGetMotivosNoHuboTiempo()));
                }
            }

            @Override
            public void onError(VolleyError error) {
                error.printStackTrace();
                hideProgressDialog();
                activity.runOnUiThread(() -> showSnackBar(view.baseFindViewById(R.id.toolbar),
                        R.string.text_actualizando_motivos_error, Snackbar.LENGTH_LONG,
                        R.string.text_volver_a_intentar,
                        v -> requestGetMotivosNoHuboTiempo()));
            }
        };

        String[] params = {
                MotivoDescarga.Tipo.NO_HUBO_TIEMPO + "",
                Preferences.getInstance().getString("idUsuario", "")
        };

        interactor.getMotivos(params, callback);
    }

    private void saveMotivos(JSONArray data) throws JSONException {
        JSONObject jsonObject;

        motivoNoHuboTiempoItems = new String[data.length()];

        for (int i = 0; i < data.length(); i++) {
            jsonObject = data.getJSONObject(i);
            MotivoDescarga motivo = new MotivoDescarga(
                    Preferences.getInstance().getString("idUsuario", ""),
                    MotivoDescarga.Tipo.NO_HUBO_TIEMPO,
                    jsonObject.getString("mot_id"),
                    jsonObject.getString("codigo"),
                    jsonObject.getString("descri"),
                    jsonObject.getString("linea")
            );
            motivo.save();
            dbMotivoNoHuboTiempo.add(motivo);

            motivoNoHuboTiempoItems[i] = WordUtils.capitalize(jsonObject.getString("descri").toLowerCase());
        }
    }

    private void loadMotivosGestionLlamada() {
        if (dbMotivoNoHuboTiempo.size() == 0) {
            dbMotivoNoHuboTiempo = interactor.selectAllMotivos(MotivoDescarga.Tipo.NO_HUBO_TIEMPO, "3");

            motivoNoHuboTiempoItems = new String[dbMotivoNoHuboTiempo.size()];

            for (int i = 0; i < dbMotivoNoHuboTiempo.size(); i++) {
                motivoNoHuboTiempoItems[i] = WordUtils.capitalize(dbMotivoNoHuboTiempo.get(i).getDescripcion().toLowerCase());
            }
        }
    }

    private void showProgressEliminandoDatosRuta() {
        activity.runOnUiThread(() -> BaseModalsView.showProgressDialog(view.getContextView(),
                R.string.activity_ruta_message_eliminando_datos_ruta));
    }

    private boolean checkSolicitarKilometraje() {
        return interactor.getTotalGuiasLogistica() > 0;
    }

    /**
     * Broadcast
     * <p>
     * {@link ForzarCierreRutaHelper#sendOnRutaFinalizadaReceiver()}
     */
    private final BroadcastReceiver rutaIniciadaReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "RUTA INICIADA RECEIVER");

            Log.d(TAG, "STOP SERVICE");
            LocationUtils.setCurrentLocation(null);
            activity.stopService(new Intent(view.getContextView(), DataSyncService.class));
            Log.d(TAG, "INIT SERVICE");
            activity.startService(new Intent(view.getContextView(), DataSyncService.class));

            activity.runOnUiThread(() -> {
                FloatingActionButton fab = (FloatingActionButton)
                        view.baseFindViewById(R.id.fabIniciarTerminarRuta);
                fab.setImageDrawable(ContextCompat.getDrawable(view.getContextView(),
                        R.drawable.ic_stop_white));
                view.setVisibilityBoxRutaNoIniciada(View.GONE);
            });
        }
    };

    /**
     * Broadcast
     * <p>
     * {@link ForzarCierreRutaHelper#sendOnRutaFinalizadaReceiver()}
     */
    private final BroadcastReceiver rutaFinalizadaReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Actualizar el icono del fab (iniciar/terminar ruta)
//            setIconFab();
            Log.d(TAG, "RUTA FINALIZADA RECEIVER");

            Log.d(TAG, "STOP SERVICE");
            LocationUtils.setCurrentLocation(null);
            activity.stopService(new Intent(view.getContextView(), DataSyncService.class));
            Log.d(TAG, "INIT SERVICE");
            activity.startService(new Intent(view.getContextView(), DataSyncService.class));
//            new ConfigUIEstadoRutaTask().execute();

            activity.runOnUiThread(() -> {
                ((RutaActivity) view.getContextView()).getToolbar()
                        .setTitle(R.string.title_activity_ruta);
                FloatingActionButton fab = (FloatingActionButton)
                        view.baseFindViewById(R.id.fabIniciarTerminarRuta);
                fab.setImageDrawable(ContextCompat.getDrawable(view.getContextView(),
                        R.drawable.ic_near_me_white));
                view.setVisibilityBoxRutaNoIniciada(View.GONE);
                view.setVisibilityBoxConsideracionesImportantesRuta(View.GONE);
            });
        }
    };

    /**
     * Broadcast
     * <p>
     * {@link TransferirGuiaDialog#sendTransferenciaGuiaFinalizadaAction}
     */
    private final BroadcastReceiver transferenciaGuiaFinalizadaReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            new ConfigUIEstadoRutaTask().execute();
        }
    };

    /**
     * Broadcast
     * <p>
     * {@link CodeScannerImpl#sendOnResultScannListener}
     */
    private final BroadcastReceiver resultScannReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                CommonUtils.vibrateDevice(view.getContextView(), 100);
            } catch (NullPointerException ex) {
                ex.printStackTrace();
            }

            TabLayout tabLayout = (TabLayout) view.baseFindViewById(R.id.tabLayout);

            Intent newIntent = new Intent(LocalAction.BUSCAR_GUIA_ACTION);
            newIntent.putExtra("tipoBusqueda", tabLayout.getSelectedTabPosition());
            newIntent.putExtra("value", intent.getStringExtra("value"));
            LocalBroadcastManager.getInstance(activity).sendBroadcast(newIntent);
        }
    };

    private class DeleteDatosRutaTask extends AsyncTaskCoroutine<String, String> {

        private int motivoEliminarDatosRuta = 0;

        public DeleteDatosRutaTask(int motivoEliminarDatosRuta) {
            this.motivoEliminarDatosRuta = motivoEliminarDatosRuta;
        }

        @Override
        public String doInBackground(String... strings) {
            ForzarCierreRutaHelper.deleteAllDataRuta(view.getContextView());
            return "";
        }

        @Override
        public void onPostExecute(String s) {
            super.onPostExecute(s);
            sendOnRutaFinalizadaReceiver();

            if (motivoEliminarDatosRuta == MOTIVO_CERRAR_RUTA) {
                showToast(view.getContextView(),
                        R.string.activity_ruta_message_ruta_finalizado_exitosamente,
                        Toast.LENGTH_SHORT);
            } else if (motivoEliminarDatosRuta == MOTIVO_ELIMINAR_MANIFIESTO) {
                showToast(view.getContextView(),
                        R.string.activity_ruta_message_ruta_eliminada_correctamente,
                        Toast.LENGTH_SHORT);
            }
            BaseModalsView.hideProgressDialog();
        }
    }

    private class ConfigUIEstadoRutaTask extends AsyncTaskCoroutine<String, String> {

        private long totalEstadoRutas = 0;
        private long totalRutasPendientes = 0;

        @Override
        public String doInBackground(String... strings) {
            Log.d(TAG, "Load Estado Ruta");
            totalEstadoRutas = interactor.getTotalAllEstadoRuta();
            Log.d(TAG, "Total estado ruta: " + totalEstadoRutas);

            //if (totalEstadoRutas == 0) {
            Log.d(TAG, "Load Rutas Pendientes");
            totalRutasPendientes = interactor.getTotalRutasPendientes();
            Log.d(TAG, "Total rutas pendientes: " + totalRutasPendientes);
            //}
            return null;
        }

        @Override
        public void onPostExecute(String s) {
            super.onPostExecute(s);
            FloatingActionButton fab = (FloatingActionButton)
                    view.baseFindViewById(R.id.fabIniciarTerminarRuta);
            if (totalEstadoRutas > 0) { // Ya Inicio Ruta
                fab.setImageDrawable(ContextCompat.getDrawable(view.getContextView(),
                        R.drawable.ic_stop_white));
                if (totalRutasPendientes == 0) {
                    view.setMsgBoxRutaNoIniciada(view.getContextView().getString(
                            R.string.activity_ruta_message_ruta_no_finalizado));
                    view.setVisibilityBoxRutaNoIniciada(View.VISIBLE);
                } else {
                    view.setVisibilityBoxRutaNoIniciada(View.GONE);
                }
            } else { // Aun no iniciar Ruta
                fab.setImageDrawable(ContextCompat.getDrawable(view.getContextView(),
                        R.drawable.ic_near_me_white));
                if (totalRutasPendientes > 0) {
                    view.setMsgBoxRutaNoIniciada(view.getContextView().getString(
                            R.string.activity_ruta_message_ruta_no_iniciada));
                    view.setVisibilityBoxRutaNoIniciada(View.VISIBLE);
                }
            }
        }
    }

}