package com.urbanoexpress.iridio3.pe.ui.dialogs;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.android.volley.VolleyError;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.messaging.FirebaseMessaging;
import com.urbanoexpress.iridio3.pe.util.async.AsyncTaskCoroutine;
import com.urbanoexpress.iridio3.R;
import com.urbanoexpress.iridio3.databinding.ModalIniciarTerminarRutaDelDiaBinding;
import com.urbanoexpress.iridio3.pe.model.entity.Data;
import com.urbanoexpress.iridio3.pe.model.entity.EstadoRuta;
import com.urbanoexpress.iridio3.pe.model.entity.LogErrorSync;
import com.urbanoexpress.iridio3.pe.model.entity.Ruta;
import com.urbanoexpress.iridio3.pe.model.interactor.RutaPendienteInteractor;
import com.urbanoexpress.iridio3.pe.model.interactor.callback.RequestCallback;
import com.urbanoexpress.iridio3.pe.presenter.RutaGestionadaPresenter;
import com.urbanoexpress.iridio3.pe.presenter.RutaPendientePresenter;
import com.urbanoexpress.iridio3.pe.presenter.RutaPresenter;
import com.urbanoexpress.iridio3.pe.presenter.helpers.ForzarCierreRutaHelper;
import com.urbanoexpress.iridio3.pe.ui.helpers.ModalHelper;
import com.urbanoexpress.iridio3.pe.util.CommonUtils;
import com.urbanoexpress.iridio3.pe.util.LocationUtils;
import com.urbanoexpress.iridio3.pe.util.Preferences;
import com.urbanoexpress.iridio3.pe.util.Session;
import com.urbanoexpress.iridio3.pe.util.constant.LocalAction;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class IniciarTerminarRutaDelDiaDialog extends BaseDialogFragment {
    private static final String TAG = IniciarTerminarRutaDelDiaDialog.class.getSimpleName();

    private ModalIniciarTerminarRutaDelDiaBinding binding;
    private RutaPendienteInteractor interactor;
    private List<EstadoRuta> estadoRutaCierre;

    private int estadoRuta = 0;
    private int ultimoKM = 0;
    private String idMotivoNT = "0";

    private String firebaseToken = "";

    /**
     * Provides access to the Fused Location Provider API.
     */
    private FusedLocationProviderClient mFusedLocationClient;

    public static IniciarTerminarRutaDelDiaDialog newInstance(int estadoRuta, String placa, int ultimoKM, String idMotivoNT) {
        IniciarTerminarRutaDelDiaDialog dialog = new IniciarTerminarRutaDelDiaDialog();
        Bundle bundle = new Bundle();
        bundle.putInt("estadoRuta", estadoRuta);
        bundle.putString("placa", placa);
        bundle.putInt("ultimoKM", ultimoKM);
        bundle.putString("idMotivoNT", idMotivoNT);
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(s -> firebaseToken = s);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        binding = ModalIniciarTerminarRutaDelDiaBinding.inflate(inflater, container, false);

        setupViews();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        interactor = new RutaPendienteInteractor(getActivity());

        return binding.getRoot();
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        dismissProgressDialog();
    }

    private void setupViews() {
        estadoRuta = getArguments().getInt("estadoRuta");
        ultimoKM = getArguments().getInt("ultimoKM");
        idMotivoNT = getArguments().getString("idMotivoNT");

        binding.lblPlaca.setText(getArguments().getString("placa"));

        binding.btnCancelar.setOnClickListener(v -> dismiss());

        binding.btnIniciarTerminarRuta.setOnClickListener(v -> {
            if (binding.txtKilometraje.getText().toString().trim().length() > 0 &&
                    Integer.parseInt(binding.txtKilometraje.getText().toString().trim()) > 0) {
                if (CommonUtils.validateConnectivity(getActivity())) {
                    /*switch (planDeViaje.getEstadoRecorrido()) {
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
                    }*/
                    CommonUtils.showOrHideKeyboard(getActivity(), false, binding.txtKilometraje);

                    getLastLocation();
                }
            } else {
                binding.txtKilometraje.setError(getString(R.string.act_plan_de_viaje_msg_ingrese_kilometraje));
                binding.txtKilometraje.requestFocus();
            }
        });

        binding.btnEditarPlaca.setOnClickListener(v -> {
            CommonUtils.showOrHideKeyboard(getActivity(), false, binding.txtKilometraje);

            ModalHelper.getBuilderAlertDialog(getActivity())
                    .setTitle(R.string.act_ruta_title_confirma_editar_placa)
                    .setMessage(R.string.act_ruta_msg_confirma_editar_placa)
                    .setPositiveButton(R.string.text_editar, (dialog, which) -> {
                        dismiss();
                        EditarPlacaDialog editarPlacaDialog = EditarPlacaDialog.newInstance();
                        editarPlacaDialog.show(getActivity().getSupportFragmentManager(),
                                EditarPlacaDialog.TAG);
                    })
                    .setNegativeButton(R.string.text_cancelar, null)
                    .show();
        });

        if (estadoRuta == EstadoRuta.Estado.FINALIZADO) {
            binding.txtKilometraje.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String textCurrentKM = binding.txtKilometraje.getText().toString();
                    if (!textCurrentKM.isEmpty()) {
                        int currentKM = Integer.parseInt(textCurrentKM);
                        if (currentKM > ultimoKM) {
                            int totalkm = currentKM - ultimoKM;
                            binding.lblTotalKilometraje.setText(String.format("%s km", totalkm));
                        } else {
                            binding.lblTotalKilometraje.setText("0 km");
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }

        switch (estadoRuta) {
            case EstadoRuta.Estado.INICIADO:
                binding.lblTitle.setText("Iniciar Ruta");
                binding.lblUltimoKilometraje.setText(ultimoKM + " km");
                binding.btnIniciarTerminarRuta.setText(R.string.act_plan_de_viaje_btn_iniciar_ruta);
                binding.lblSugerencia.setText(R.string.act_ruta_msg_sugerencia_ingresar_kilometraje_inicio);
                binding.btnEditarPlaca.setVisibility(View.GONE);
                binding.boxTotalKM.setVisibility(View.GONE);
                binding.boxUltimoKM.getChildAt(0).setPadding(
                        getResources().getDimensionPixelOffset(R.dimen.activity_horizontal_margin),
                        0,
                        getResources().getDimensionPixelOffset(R.dimen.activity_horizontal_margin),
                        0
                );
                break;
            case EstadoRuta.Estado.FINALIZADO:
                binding.lblTitle.setText("Terminar Ruta");
                binding.lblUltimoKilometraje.setText(ultimoKM + " km");
                binding.btnIniciarTerminarRuta.setText(R.string.act_plan_de_viaje_btn_terminar_ruta);
                binding.lblSugerencia.setText(R.string.act_ruta_msg_sugerencia_ingresar_kilometraje_cierre);
                binding.btnEditarPlaca.setVisibility(View.GONE);
                binding.boxTotalKM.setVisibility(View.VISIBLE);
                break;
        }

        binding.txtKilometraje.requestFocus();
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        mFusedLocationClient.getLastLocation()
                .addOnCompleteListener(getActivity(), task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        LocationUtils.setCurrentLocation(task.getResult());
                    }
                    new NewEstadoRutaTask().execute();
                });
    }

    private void newEstadoRuta() {
        Log.d(TAG, "New Estado Ruta: " + estadoRuta);

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
            EstadoRuta estado = new EstadoRuta(
                    Preferences.getInstance().getString("idUsuario", ""),
                    idRutas.get(i).getIdRuta(),
                    idRutas.get(i).getLineaNegocio(),
                    new SimpleDateFormat("dd/MM/yyyy").format(date),
                    new SimpleDateFormat("HH:mm:ss").format(date),
                    LocationUtils.getLatitude() + "",
                    LocationUtils.getLongitude() + "",
                    EstadoRuta.TipoRuta.RUTA_DEL_DIA,
                    Data.Delete.NO,
                    estadoRuta
            );
            //if (estadoRuta == EstadoRuta.Estado.FINALIZADO) {
            estado.setDataSync(Data.Sync.MANUAL);
            estadoRutaCierre.add(estado);
            //}
            estado.save();
        }

        for (int i = 0; i < estadoRutas.size(); i++) {
            Log.d(TAG, "ID Ruta: " + estadoRutas.get(i).getIdRuta());
            EstadoRuta estado = new EstadoRuta(
                    Preferences.getInstance().getString("idUsuario", ""),
                    estadoRutas.get(i).getIdRuta(),
                    estadoRutas.get(i).getLineaNegocio(),
                    new SimpleDateFormat("dd/MM/yyyy").format(date),
                    new SimpleDateFormat("HH:mm:ss").format(date),
                    LocationUtils.getLatitude() + "",
                    LocationUtils.getLongitude() + "",
                    EstadoRuta.TipoRuta.RUTA_DEL_DIA,
                    Data.Delete.NO,
                    estadoRuta
            );
            //if (estadoRuta == EstadoRuta.Estado.FINALIZADO) {
            estado.setDataSync(Data.Sync.MANUAL);
            estadoRutaCierre.add(estado);
            //}
            estado.save();
        }

        Log.d(TAG, "Total Estado Ruta Cierre: " + estadoRutaCierre.size());
    }

    private void sendDataEstadoRuta() {
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

                        if (estadoRuta == EstadoRuta.Estado.INICIADO) {
                            sendOnRutaIniciadaReceiver();
                            showToast(R.string.activity_ruta_message_ruta_iniciada_exitosamente);
                        } else {
                            new DeleteDatosRutaTask().execute();
                        }

                        dismiss();
                    } else {
                        dismissProgressDialog();
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
                        showToast(msgError);
                    }
                } catch (JSONException ex) {
                    dismissProgressDialog();
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
                    showToast(R.string.json_object_exception);
                }
            }

            @Override
            public void onError(VolleyError error) {
                error.printStackTrace();
                dismissProgressDialog();
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
                showToast(R.string.volley_error_message);
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

        String[] params = {
                idRutas,
                estadoRuta == EstadoRuta.Estado.INICIADO ? "1" : "2",
                estadoRutaCierre.get(0).getGpsLatitude(),
                estadoRutaCierre.get(0).getGpsLongitude(),
                estadoRutaCierre.get(0).getFecha(),
                estadoRutaCierre.get(0).getHora(),
                binding.txtKilometraje.getText().toString().trim(),
                idLineaNegocio,
                firebaseToken,
                idMotivoNT,
                estadoRutaCierre.get(0).getIdUsuario(),
                Session.getUser().getDevicePhone()
        };

        interactor.uploadEstadoRutaKilometraje(params, callback);
    }

    private class NewEstadoRutaTask extends AsyncTaskCoroutine<String, String> {

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            switch (estadoRuta) {
                case EstadoRuta.Estado.INICIADO:
                    showProgressDialog(R.string.text_iniciando_ruta);
                    break;
                case EstadoRuta.Estado.FINALIZADO:
                    showProgressDialog(R.string.text_terminando_ruta);
                    break;
            }
        }

        @Override
        public String doInBackground(String... strings) {
            newEstadoRuta();
            return null;
        }

        @Override
        public void onPostExecute(String s) {
            super.onPostExecute(s);
            sendDataEstadoRuta();
        }
    }

    private class DeleteDatosRutaTask extends AsyncTaskCoroutine<String, String> {

        @Override
        public String doInBackground(String... strings) {
            ForzarCierreRutaHelper.deleteAllDataRuta(getActivity());
            return "";
        }

        @Override
        public void onPostExecute(String s) {
            super.onPostExecute(s);
            dismissProgressDialog();
            showToast(R.string.activity_ruta_message_ruta_finalizado_exitosamente);
            sendOnRutaFinalizadaReceiver();
        }
    }

    /**
     * Receiver
     * <p>
     * {@link RutaPendientePresenter#rutaIniciadaReceiver}
     */
    private void sendOnRutaIniciadaReceiver() {
        Intent intent = new Intent(LocalAction.INICIAR_RUTA_DEL_DIA_ACTION);
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
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
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
    }
}