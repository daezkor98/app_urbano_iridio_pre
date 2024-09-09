package com.urbanoexpress.iridio3.pe.ui.dialogs;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.orm.util.NamingHelper;
import com.urbanoexpress.iridio3.pe.R;
import com.urbanoexpress.iridio3.pe.databinding.ModalIniciarTerminarRutaPlanDeViajeBinding;
import com.urbanoexpress.iridio3.pe.model.entity.Data;
import com.urbanoexpress.iridio3.pe.model.entity.Imagen;
import com.urbanoexpress.iridio3.pe.model.entity.IncidenteRuta;
import com.urbanoexpress.iridio3.pe.model.entity.ParadaProgramada;
import com.urbanoexpress.iridio3.pe.model.entity.PlanDeViaje;
import com.urbanoexpress.iridio3.pe.model.interactor.PlanDeViajeInteractor;
import com.urbanoexpress.iridio3.pe.model.interactor.callback.RequestCallback;
import com.urbanoexpress.iridio3.pe.services.DataSyncService;
import com.urbanoexpress.iridio3.pe.util.CommonUtils;
import com.urbanoexpress.iridio3.pe.util.InfoDevice;
import com.urbanoexpress.iridio3.pe.util.LocationUtils;
import com.urbanoexpress.iridio3.pe.util.MyLocation;
import com.urbanoexpress.iridio3.pe.util.Preferences;
import com.urbanoexpress.iridio3.pe.util.constant.LocalAction;
import com.urbanoexpress.iridio3.pe.view.BaseModalsView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class IniciarTerminarRutaPlanDeViajeDialog extends DialogFragment {

    public static final String TAG = "IniciarTerminarRutaPlanDeViajeDialog";

    private ModalIniciarTerminarRutaPlanDeViajeBinding binding;

    private PlanDeViaje planDeViaje;

    private List<ParadaProgramada> paradasProgramadas;

    private PlanDeViajeInteractor interactor;

    private int nuevoEstadoRuta = 0;

    /**
     * Provides access to the Fused Location Provider API.
     */
    private FusedLocationProviderClient mFusedLocationClient;

    public static IniciarTerminarRutaPlanDeViajeDialog newInstance(PlanDeViaje planDeViaje,
                                                                   ArrayList<ParadaProgramada> paradasProgramadas) {
        IniciarTerminarRutaPlanDeViajeDialog dialog = new IniciarTerminarRutaPlanDeViajeDialog();
        Bundle bundle = new Bundle();
        bundle.putSerializable("planDeViaje", planDeViaje);
        bundle.putSerializable("paradasProgramadas", paradasProgramadas);
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        interactor = new PlanDeViajeInteractor(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        binding = ModalIniciarTerminarRutaPlanDeViajeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            getDialog().getWindow().setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    private void setupViews() {
        planDeViaje = (PlanDeViaje) getArguments().getSerializable("planDeViaje");
        paradasProgramadas = (ArrayList<ParadaProgramada>) getArguments().getSerializable("paradasProgramadas");

        binding.btnCancelar.setOnClickListener(v -> dismiss());

        binding.btnIniciarTerminarRuta.setOnClickListener(v -> {
            if (binding.txtKilometraje.getText().toString().trim().length() > 0) {
                if (CommonUtils.validateConnectivity(getActivity())) {
                    switch (planDeViaje.getEstadoRecorrido()) {
                        case PlanDeViaje.EstadoRuta.NO_INICIO_RUTA:
                            if (isPlacaValido()) {
                                iniciarRuta();
                            }
                            break;
                        case PlanDeViaje.EstadoRuta.INICIO_RUTA:
                            if (validateImagenesSync()) {
                                if (validateIncidentesSync()) {
                                    terminarRuta();
                                }
                            }
                            break;
                    }
                }
            } else {
                BaseModalsView.showToast(getActivity(),
                        R.string.act_plan_de_viaje_msg_ingrese_kilometraje,
                        Toast.LENGTH_LONG);
            }
        });

        switch (planDeViaje.getEstadoRecorrido()) {
            case PlanDeViaje.EstadoRuta.NO_INICIO_RUTA:
                binding.lblTitle.setText("Iniciar ruta del plan de viaje");
                binding.btnIniciarTerminarRuta.setText(R.string.act_plan_de_viaje_btn_iniciar_ruta);

                nuevoEstadoRuta = PlanDeViaje.EstadoRuta.INICIO_RUTA;
                break;
            case PlanDeViaje.EstadoRuta.INICIO_RUTA:
                binding.lblTitle.setText("Terminar ruta del plan de viaje");
                binding.btnIniciarTerminarRuta.setText(R.string.act_plan_de_viaje_btn_terminar_ruta);

                nuevoEstadoRuta = PlanDeViaje.EstadoRuta.TERMINO_RUTA;
                break;
        }
    }

    private void sendEstadoPlanDeViajeRequest() {
        RequestCallback callback = new RequestCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    getActivity().runOnUiThread(() -> BaseModalsView.hideProgressDialog());
                    if (response.getBoolean("success")) {
                        updateEstadoRecorridoPlanViaje(nuevoEstadoRuta);

                        sendIniciarTerminarPlanDeViajeReceiver();

                        BaseModalsView.showToast(getActivity(),
                                nuevoEstadoRuta == PlanDeViaje.EstadoRuta.INICIO_RUTA
                                        ? R.string.act_plan_de_viaje_message_success_iniciando_ruta
                                        : R.string.act_plan_de_viaje_message_success_finalizando_ruta,
                                Toast.LENGTH_LONG);
                        getActivity().stopService(new Intent(getActivity(), DataSyncService.class));
                        getActivity().startService(new Intent(getActivity(), DataSyncService.class));

                        dismiss();
                    } else {
                        BaseModalsView.showToast(getActivity(),
                                response.getString("msg_error"), Toast.LENGTH_LONG);
                    }
                } catch (JSONException ex) {
                    ex.printStackTrace();
                    getActivity().runOnUiThread(() -> {
                        BaseModalsView.hideProgressDialog();
                        BaseModalsView.showSnackBar(binding.txtKilometraje,
                                R.string.json_object_exception, Snackbar.LENGTH_LONG,
                                R.string.text_volver_a_intentar,
                                v -> {
                                    BaseModalsView.showProgressDialog(getActivity(),
                                            nuevoEstadoRuta == PlanDeViaje.EstadoRuta.INICIO_RUTA
                                                    ? R.string.act_plan_de_viaje_title_iniciando_ruta
                                                    : R.string.act_plan_de_viaje_title_finalizando_ruta,
                                            R.string.text_espere_un_momento);
                                    sendEstadoPlanDeViajeRequest();
                                });
                    });
                }
            }

            @Override
            public void onError(VolleyError error) {
                getActivity().runOnUiThread(() -> {
                    BaseModalsView.hideProgressDialog();
                    BaseModalsView.showSnackBar(binding.txtKilometraje,
                            R.string.volley_error_message, Snackbar.LENGTH_LONG,
                            R.string.text_volver_a_intentar,
                            v -> {
                                BaseModalsView.showProgressDialog(getActivity(),
                                        nuevoEstadoRuta == PlanDeViaje.EstadoRuta.INICIO_RUTA
                                                ? R.string.act_plan_de_viaje_title_iniciando_ruta
                                                : R.string.act_plan_de_viaje_title_finalizando_ruta,
                                        R.string.text_espere_un_momento);
                                sendEstadoPlanDeViajeRequest();
                            });
                });
            }
        };

        Date date = new Date();
        String fecha = new SimpleDateFormat("dd/MM/yyyy").format(date);
        String hora = new SimpleDateFormat("HH:mm:ss").format(date);

        String[] params = new String[]{
                planDeViaje.getIdPlanViaje(),
                nuevoEstadoRuta + "",
                fecha, hora,
                LocationUtils.getLatitude() + "",
                LocationUtils.getLongitude() + "",
                LocationUtils.getCurrentLocation().getAccuracy() + "",
                InfoDevice.getBattery(getActivity()),
                binding.txtKilometraje.getText().toString().trim(),
                planDeViaje.getTotalKMRuta() != null ? planDeViaje.getTotalKMRuta() : "0",
                Preferences.getInstance().getString("idUsuario", "")
        };

        interactor.updateEstado(params, callback);
    }

    private void iniciarRuta() {
        if (planDeViaje != null) {
            getActivity().runOnUiThread(() -> BaseModalsView.showProgressDialog(getActivity(),
                    R.string.act_plan_de_viaje_title_iniciando_ruta,
                    R.string.text_espere_un_momento));

            //Log.d(TAG, "INIT TIME REQUEST LOCATION: " + new SimpleDateFormat("HH:mm:ss:SS").format(new Date(System.currentTimeMillis())));
            mFusedLocationClient.getLastLocation()
                    .addOnCompleteListener(getActivity(), task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            LocationUtils.setCurrentLocation(task.getResult());

                            Log.d(TAG, "Result 1: " + task.getResult());

                            boolean validateIntersectionLocations = MyLocation.intersectionLocations(
                                    LocationUtils.getLatitude(), LocationUtils.getLongitude(),Double.parseDouble(
                                            planDeViaje.getOrigen_latitude()),
                                    Double.parseDouble(
                                            planDeViaje.getOrigen_longitude()), 1.0);

                            if (validateIntersectionLocations) {
                                sendEstadoPlanDeViajeRequest();
                            } else {
                                getActivity().runOnUiThread(() -> {
                                    BaseModalsView.hideProgressDialog();
                                    BaseModalsView.showAlertDialog(getActivity(),
                                            R.string.act_plan_de_viaje_title_ubicacion_fuera_rango_origen_planviaje,
                                            R.string.act_plan_de_viaje_msg_ubicacion_fuera_rango_origen_planviaje,
                                            R.string.activity_ruta_fab_menu_iniciar_ruta, (dialog, i) -> {
                                                dialog.dismiss();
                                                BaseModalsView.showProgressDialog(getActivity(),
                                                        R.string.act_plan_de_viaje_title_iniciando_ruta,
                                                        R.string.text_espere_un_momento);
                                                sendEstadoPlanDeViajeRequest();
                                            }, R.string.text_cancelar, (dialog, i) -> dialog.dismiss());
                                });
                            }
                        } else {
                            Log.w(TAG, "getLastLocation:exception", task.getException());
                            sendEstadoPlanDeViajeRequest();
                        }
                    });
        } else {
            BaseModalsView.showToast(getActivity(),
                    R.string.act_plan_de_viaje_no_hay_plan_de_viaje, Toast.LENGTH_LONG);
        }
    }

    private void terminarRuta() {
        if (planDeViaje != null) {
            BaseModalsView.showProgressDialog(getActivity(),
                    R.string.act_plan_de_viaje_title_finalizando_ruta,
                    R.string.text_espere_un_momento);

            mFusedLocationClient.getLastLocation()
                    .addOnCompleteListener(getActivity(), task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            LocationUtils.setCurrentLocation(task.getResult());
                        }
                        sendEstadoPlanDeViajeRequest();
                    });
        } else {
            BaseModalsView.showToast(getActivity(),
                    R.string.act_plan_de_viaje_no_hay_plan_de_viaje, Toast.LENGTH_LONG);
        }
    }

    private void updateEstadoRecorridoPlanViaje(int estado) {
        PlanDeViaje p = interactor.selectPlanViajeById(
                planDeViaje.getIdPlanViaje());
        p.setEstadoRecorrido(estado);
        p.save();
    }

    private boolean isPlacaValido() {
        if (planDeViaje.getPlaca().isEmpty()) {
            BaseModalsView.showToast(getActivity(),
                    R.string.act_plan_de_viaje_msg_placa_no_valida, Toast.LENGTH_LONG);
        }
        return !planDeViaje.getPlaca().isEmpty();
    }

    private boolean validateImagenesSync() {
        String idParadaProgramada = "";

        for (int i = 0; i < paradasProgramadas.size(); i++) {
            if (i == paradasProgramadas.size() - 1) {
                idParadaProgramada += paradasProgramadas.get(i).getIdStop();
            } else {
                idParadaProgramada += paradasProgramadas.get(i).getIdStop() + ",";
            }
        }

        /*long totalImagenes = Imagen.count(Imagen.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("clasificacion") + " = ? and " +
                        NamingHelper.toSQLNameDefault("idSuperior") + " in (" + idParadaProgramada + ")",
                new String[]{Preferences.getInstance().getString("idUsuario", ""),
                        Imagen.Tipo.PARADA_PROGRAMADA + ""});

        long totalImagenesSync = Imagen.count(Imagen.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("clasificacion") + " = ? and " +
                        NamingHelper.toSQLNameDefault("idSuperior") + " in (" + idParadaProgramada + ") and " +
                        NamingHelper.toSQLNameDefault("dataSync") + " = ?",
                new String[]{Preferences.getInstance().getString("idUsuario", ""),
                        Imagen.Tipo.PARADA_PROGRAMADA + "", Data.Sync.SYNCHRONIZED + ""});

        if (totalImagenes == totalImagenesSync) {
            return true;
        }*/

        long totalImagenesSync = Imagen.count(Imagen.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("clasificacion") + " = ? and " +
                        NamingHelper.toSQLNameDefault("idSuperior") + " in (" + idParadaProgramada + ") and " +
                        NamingHelper.toSQLNameDefault("dataSync") + " = ?",
                new String[]{Preferences.getInstance().getString("idUsuario", ""),
                        Imagen.Tipo.PARADA_PROGRAMADA + "", Data.Sync.PENDING + ""});

        if (totalImagenesSync == 0) {
            return true;
        }

        BaseModalsView.showToast(getActivity(),
                R.string.act_plan_de_viaje_msg_imagenes_sync_pendientes, Toast.LENGTH_LONG);
        return false;
    }

    private boolean validateIncidentesSync() {
        long totalIncidentesPendientesSync = IncidenteRuta.count(IncidenteRuta.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                NamingHelper.toSQLNameDefault("tipoRuta") + " = ? and " +
                        NamingHelper.toSQLNameDefault("dataSync") + " = ?",
                new String[]{Preferences.getInstance().getString("idUsuario", ""),
                        IncidenteRuta.TipoRuta.PLAN_DE_VIAJE + "",
                        Data.Sync.PENDING + ""});

        if (totalIncidentesPendientesSync == 0) {
            return true;
        }

        BaseModalsView.showToast(getActivity(),
                R.string.dlg_reportar_incidente_msg_incidentes_sync_pendientes, Toast.LENGTH_LONG);
        return false;
    }



    /**
     * Receiver
     *
     * {@link PlanDeViajePresenter#iniciarTerminarPlanDeViajeReceiver}
     */
    private void sendIniciarTerminarPlanDeViajeReceiver() {
        Intent intent = new Intent(LocalAction.INICIAR_TERMINAR_PLAN_DE_VIAJE_ACTION);
        intent.putExtra("nuevoEstadoRuta", nuevoEstadoRuta);
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
    }
}