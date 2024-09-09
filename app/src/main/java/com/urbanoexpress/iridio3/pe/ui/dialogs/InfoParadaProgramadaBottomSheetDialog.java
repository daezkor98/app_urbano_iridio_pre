package com.urbanoexpress.iridio3.pe.ui.dialogs;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.orm.util.NamingHelper;
import com.urbanoexpress.iridio3.pe.R;
import com.urbanoexpress.iridio3.pe.databinding.BottomSheetInfoParadaProgramadaBinding;
import com.urbanoexpress.iridio3.pe.model.entity.Despacho;
import com.urbanoexpress.iridio3.pe.model.entity.Imagen;
import com.urbanoexpress.iridio3.pe.model.entity.ParadaProgramada;
import com.urbanoexpress.iridio3.pe.model.entity.PlanDeViaje;
import com.urbanoexpress.iridio3.pe.model.interactor.PlanDeViajeInteractor;
import com.urbanoexpress.iridio3.pe.model.interactor.callback.RequestCallback;
import com.urbanoexpress.iridio3.pe.ui.ParadaProgramadaActivity;
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
import java.util.Date;
import java.util.List;

public class InfoParadaProgramadaBottomSheetDialog extends BottomSheetDialogFragment {

    public static final String TAG = "InfoParadaProgramadaBottomSheetDialog";

    private BottomSheetInfoParadaProgramadaBinding binding;
    private PlanDeViaje planDeViaje;
    private ParadaProgramada paradaProgramada;

    private PlanDeViajeInteractor interactor;

    private String horaParada;

    /**
     * Provides access to the Fused Location Provider API.
     */
    private FusedLocationProviderClient mFusedLocationClient;

    public static InfoParadaProgramadaBottomSheetDialog newInstance(PlanDeViaje planDeViaje, ParadaProgramada paradaProgramada) {
        InfoParadaProgramadaBottomSheetDialog trazarRutaBottomSheet = new InfoParadaProgramadaBottomSheetDialog();
        Bundle args = new Bundle();
        args.putSerializable("planDeViaje", planDeViaje);
        args.putSerializable("paradaProgramada", paradaProgramada);
        trazarRutaBottomSheet.setArguments(args);
        return trazarRutaBottomSheet;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = BottomSheetInfoParadaProgramadaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews();
    }

    private void setupViews() {
        interactor = new PlanDeViajeInteractor(getActivity());

        planDeViaje = (PlanDeViaje) getArguments().getSerializable("planDeViaje");

        paradaProgramada = interactor.selectParadaProgramadaByID(((ParadaProgramada)
                getArguments().getSerializable("paradaProgramada")).getIdStop());

        binding.lblNombreParada.setText(paradaProgramada.getAgencia());

        showInfoTiempo();

        setupButtons();

        binding.btnDetalle.setOnClickListener(v -> {
            Bundle args = new Bundle();

            args.putSerializable("plan_de_viaje", planDeViaje);
            args.putSerializable("parada_programada", paradaProgramada);

            dismiss();

            startActivity(new Intent(getActivity(), ParadaProgramadaActivity.class).putExtra("args", args));
            getActivity().overridePendingTransition(R.anim.slide_enter_from_right, R.anim.not_slide);
        });

        binding.btnIndicaciones.setOnClickListener(v -> {
            try {
                Double latitude = Double.parseDouble(paradaProgramada.getAgencia_latitude());
                Double longitude = Double.parseDouble(paradaProgramada.getAgencia_longitude());
                if (latitude != 0 && longitude != 0) {
                    openGogleMapsOnModeNavigation(latitude + "," + longitude);
                } else {
                    BaseModalsView.showToast(getActivity(),
                            R.string.activity_detalle_ruta_message_no_hay_gps,
                            Toast.LENGTH_SHORT);
                }
            } catch (NumberFormatException ex) {
                ex.printStackTrace();
                BaseModalsView.showToast(getActivity(),
                        R.string.activity_detalle_ruta_message_no_hay_gps,
                        Toast.LENGTH_SHORT);
            }
        });

        binding.fabMarcarLlegadaSalidaParada.setOnClickListener(v -> {
            if (CommonUtils.validateConnectivity(getActivity())) {
                showAlertConfirmStatusParadaProgramada();
            }
        });
    }

    private void showInfoTiempo() {
        switch (paradaProgramada.getEstadoLlegada()) {
            case ParadaProgramada.Status.NO_LLEGO_AGENCIA:
                mFusedLocationClient.getLastLocation()
                        .addOnCompleteListener(getActivity(), task -> {
                            if (task.isSuccessful() && task.getResult() != null) {
                                if (CommonUtils.isValidCoords(task.getResult().getLatitude(), task.getResult().getLongitude()) &&
                                        CommonUtils.isValidCoords(paradaProgramada.getAgencia_latitude(), paradaProgramada.getAgencia_longitude())) {
                                    int tiempoEstimado = (int) Math.ceil(MyLocation.calculateTimeBetweenTwoLocations(
                                            task.getResult().getLatitude(),
                                            task.getResult().getLongitude(),
                                            Double.parseDouble(paradaProgramada.getAgencia_latitude()),
                                            Double.parseDouble(paradaProgramada.getAgencia_longitude()), 50));

                                    if (tiempoEstimado >= 60) {
                                        int horas = tiempoEstimado / 60;
                                        int minutos = tiempoEstimado % 60;
                                        binding.lblInfoTiempo.setText(horas + " h " + (minutos > 0 ? minutos + " min" : ""));
                                    } else {
                                        binding.lblInfoTiempo.setText(tiempoEstimado + " min");
                                    }
                                } else {
                                    binding.lblInfoTiempo.setText("Tiempo no definido.");
                                }
                            } else {
                                binding.lblInfoTiempo.setText("Tiempo no definido.");
                            }
                        });
                break;
            case ParadaProgramada.Status.LLEGO_AGENCIA:
                String horaLlegada = CommonUtils.getFormatHora(paradaProgramada.getHoraLlegada());
                binding.lblInfoTiempo.setText("llegada a la(s) " + horaLlegada);
                break;
            case ParadaProgramada.Status.SALIO_AGENCIA:
                String horaSalida = CommonUtils.getFormatHora(paradaProgramada.getHoraSalida());
                binding.lblInfoTiempo.setText("salida a la(s) " + horaSalida);
                break;
        }
    }

    private void setupButtons() {
        switch (paradaProgramada.getEstadoLlegada()) {
            case ParadaProgramada.Status.NO_LLEGO_AGENCIA:
                binding.fabMarcarLlegadaSalidaParada.show();
                binding.boxBtnIndicaciones.setVisibility(View.VISIBLE);
                break;
            case ParadaProgramada.Status.LLEGO_AGENCIA:
                if (isParadaHub()) {
                    binding.fabMarcarLlegadaSalidaParada.hide();
                    binding.boxBtnIndicaciones.setVisibility(View.GONE);
                } else {
                    binding.fabMarcarLlegadaSalidaParada.show();
                    binding.boxBtnIndicaciones.setVisibility(View.VISIBLE);
                }
                break;
            case ParadaProgramada.Status.SALIO_AGENCIA:
                binding.fabMarcarLlegadaSalidaParada.hide();
                binding.boxBtnIndicaciones.setVisibility(View.GONE);
                break;
        }
    }

    private void openGogleMapsOnModeNavigation(String query) {
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + query);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        dismiss();
        startActivity(mapIntent);
    }

    private void showAlertConfirmStatusParadaProgramada() {
        int[] message = getMessageConfirmStatusParadaProgramada();

        switch (paradaProgramada.getEstadoLlegada()) {
            case ParadaProgramada.Status.NO_LLEGO_AGENCIA:
                BaseModalsView.showAlertDialog(getActivity(),
                        message[0], message[1], R.string.text_confirmar,
                        (dialog, which) -> confirmStatusParadaProgramada(), R.string.text_cancelar, null);
                break;
            case ParadaProgramada.Status.LLEGO_AGENCIA:
                if (!isParadaHub()) {
                    BaseModalsView.showAlertDialog(getActivity(),
                            message[0], message[1], R.string.text_confirmar,
                            (dialog, which) -> confirmStatusParadaProgramada(), R.string.text_cancelar, null);
                } else {
                    BaseModalsView.showAlertDialog(getActivity(),
                            R.string.act_plan_de_viaje_title_parada_programada_final,
                            R.string.act_plan_de_viaje_message_parada_programada_final,
                            R.string.text_aceptar, null);
                }
                break;
            case ParadaProgramada.Status.SALIO_AGENCIA:
                BaseModalsView.showAlertDialog(getActivity(),
                        message[0], message[1], R.string.text_aceptar, null);
                break;
        }

    }

    private int[] getMessageConfirmStatusParadaProgramada() {
        int[] message = new int[]{0, 0};

        switch (paradaProgramada.getEstadoLlegada()) {
            case ParadaProgramada.Status.NO_LLEGO_AGENCIA:
                message = new int[] {R.string.act_plan_de_viaje_title_confirmar_llegada,
                        R.string.act_plan_de_viaje_message_confirmar_llegada};
                break;
            case ParadaProgramada.Status.LLEGO_AGENCIA:
                message = new int[] {R.string.act_plan_de_viaje_title_confirmar_salida,
                        R.string.act_plan_de_viaje_message_confirmar_salida};
                break;
            case ParadaProgramada.Status.SALIO_AGENCIA:
                message = new int[] {R.string.act_plan_de_viaje_title_parada_programada_finalizada,
                        R.string.act_plan_de_viaje_message_parada_programada_finalizada};
                break;
        }

        return message;
    }

    private void confirmStatusParadaProgramada() {
        switch (paradaProgramada.getEstadoLlegada()) {
            case ParadaProgramada.Status.NO_LLEGO_AGENCIA:
                confirmarLlegadaAgencia();
                break;
            case ParadaProgramada.Status.LLEGO_AGENCIA:
                confirmarSalidaAgencia();
                break;
        }
    }

    private boolean isParadaHub() {
        return paradaProgramada.getTipo().equalsIgnoreCase("U");
    }

    private boolean isIniciadoRuta() {
        try {
            if (planDeViaje.getEstadoRecorrido() == PlanDeViaje.EstadoRuta.INICIO_RUTA) {
                return true;
            }
            CommonUtils.vibrateDevice(getActivity(), 100);
            return false;
        } catch (NullPointerException ex) {
            CommonUtils.vibrateDevice(getActivity(), 100);
            return false;
        }
    }

    private boolean validateConfirmacionSalidaAnteriorParada() {
        List<ParadaProgramada> paradasProgramadas = interactor.selectAllParadaProgramada();

        int posicionParadaAnterior = 0;

        for (int i = 0; i < paradasProgramadas.size(); i++) {
            if (paradasProgramadas.get(i).getIdStop().equals(paradaProgramada.getIdStop())) {
                posicionParadaAnterior = --i;
                break;
            }
        }

        if (posicionParadaAnterior < 0) { // Es la primera parada
            Log.d(TAG, "ES LA PRIMERA PARADA");
            return true;
        }

        if (paradasProgramadas.get(posicionParadaAnterior).getEstadoLlegada()
                == ParadaProgramada.Status.SALIO_AGENCIA) {
            return true;
        }

        CommonUtils.vibrateDevice(getActivity(), 100);
        BaseModalsView.showToast(getActivity(),
                R.string.act_plan_de_viaje_message_no_confirmo_salida_parada_anterior, Toast.LENGTH_LONG);

        return false;
    }

    private boolean validateArrivedToLocation() {
        boolean validateIntersectionLocations = MyLocation.intersectionLocations(
                LocationUtils.getLatitude(), LocationUtils.getLongitude(),Double.parseDouble(
                        paradaProgramada.getAgencia_latitude()),
                Double.parseDouble(
                        paradaProgramada.getAgencia_longitude()), 1.0);

        if (validateIntersectionLocations) {
            Log.d(TAG, "LLEGO A LA AGENCIA!");
            return true;
        }

        Log.d(TAG, "NO LLEGO A LA AGENCIA!");
        CommonUtils.vibrateDevice(getActivity(), 100);
        BaseModalsView.showToast(getActivity(),
                R.string.act_plan_de_viaje_message_no_se_encuentra_en_la_ubicacion, Toast.LENGTH_LONG);

        return false;
    }

    private boolean validateRevisionDespachos() {
        ParadaProgramada parada = interactor
                .selectParadaProgramadaByID(paradaProgramada.getIdStop());

        if (parada.getEstadoDespachosRevisado()
                == ParadaProgramada.Status.REVISO_DESPACHOS) {
            return true;
        }

        CommonUtils.vibrateDevice(getActivity(), 100);
        BaseModalsView.showToast(getActivity(),
                R.string.act_plan_de_viaje_message_no_reviso_despachos, Toast.LENGTH_LONG);
        return false;
    }

    private boolean validateDespachosBajados() {
        List<Despacho> despachosBajadas = PlanDeViajeInteractor.selectDespachoByIdParada(
                paradaProgramada.getIdStop(), Despacho.Type.DESPACHO_BAJADA + "");

        if (despachosBajadas.size() == 0) { // No hay despachos por bajar
            return true;
        }

        for (int i = 0; i < despachosBajadas.size(); i++) {
            if (despachosBajadas.get(i).getProcesoDespacho() == Despacho.Status.DESPACHADO) {
                return true;
            }
        }

        CommonUtils.vibrateDevice(getActivity(), 100);
        BaseModalsView.showToast(getActivity(),
                R.string.act_plan_de_viaje_message_no_ha_bajado_despachos, Toast.LENGTH_LONG);
        return false;
    }

    private boolean validateImagenesPorParada() {
        long totalImagenes = Imagen.count(Imagen.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("clasificacion") + " = ? and " +
                        NamingHelper.toSQLNameDefault("idSuperior") + " = ?",
                new String[]{Preferences.getInstance().getString("idUsuario", ""),
                        Imagen.Tipo.PARADA_PROGRAMADA + "", paradaProgramada.getIdStop()});

        if (totalImagenes >= 1) {
            return true;
        }

        CommonUtils.vibrateDevice(getActivity(), 100);
        BaseModalsView.showToast(getActivity(),
                R.string.act_plan_de_viaje_msg_tomar_al_menos_una_foto, Toast.LENGTH_LONG);
        return false;
    }

    private void updateEstadoRecorridoParadaProgramada(int estado) {
        ParadaProgramada parada = ParadaProgramada.findById(
                ParadaProgramada.class, paradaProgramada.getId());

        parada.setEstadoLlegada(estado);
        paradaProgramada.setEstadoLlegada(estado);

        if (estado == ParadaProgramada.Status.LLEGO_AGENCIA) {
            parada.setHoraLlegada(horaParada);
            paradaProgramada.setHoraLlegada(horaParada);
        } else {
            parada.setHoraSalida(horaParada);
            paradaProgramada.setHoraSalida(horaParada);
        }

        parada.save();
    }

    private boolean validateConfirmarLlegada() {
        if (isIniciadoRuta()) {
            if (validateConfirmacionSalidaAnteriorParada()) {
                if (InfoDevice.isGPSEnabled(getActivity(), true)) {
//                    if (validateArrivedToLocation()) {
                    return true;
//                    }
                }
            }
        } else {
            CommonUtils.vibrateDevice(getActivity(), 100);
            BaseModalsView.showToast(getActivity(),
                    R.string.act_plan_de_viaje_message_no_inicio_ruta, Toast.LENGTH_LONG);
        }

        BaseModalsView.hideProgressDialog();
        return false;
    }

    private boolean validateSalidaLlegada() {
        if (validateRevisionDespachos()) {
            if (validateDespachosBajados()) {
                if (validateImagenesPorParada()) {
                    return true;
                }
            }
        }

        BaseModalsView.hideProgressDialog();
        return false;
    }

    private void confirmarLlegadaAgencia() {
        BaseModalsView.showProgressDialog(getActivity(),
                R.string.act_plan_de_viaje_title_confirmando_llegada,
                R.string.text_espere_un_momento);

        if (validateConfirmarLlegada()) {
            RequestCallback callback = new RequestCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    try {
                        BaseModalsView.hideProgressDialog();
                        if (response.getBoolean("success")) {
                            updateEstadoRecorridoParadaProgramada(ParadaProgramada.Status.LLEGO_AGENCIA);
                            showInfoTiempo();
                            setupButtons();
                            BaseModalsView.showToast(getActivity(),
                                    R.string.act_plan_de_viaje_message_success_confirmar_llegada, Toast.LENGTH_SHORT);
                            sendLlegadaSalidaParadaProgramadaReceiver();
                        } else {
                            CommonUtils.vibrateDevice(getActivity(), 100);
                            BaseModalsView.showToast(getActivity(),
                                    response.getString("msg_error"), Toast.LENGTH_LONG);
                        }
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                        BaseModalsView.hideProgressDialog();
                        CommonUtils.vibrateDevice(getActivity(), 100);
                        BaseModalsView.showToast(getActivity(), R.string.json_object_exception, Toast.LENGTH_LONG);
                        /*BaseModalsView.showSnackBar(view.baseFindViewById(R.id.toolbar),
                                R.string.json_object_exception, Snackbar.LENGTH_LONG,
                                R.string.text_volver_a_intentar,
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        BaseModalsView.showProgressDialog(view.getContextView(),
                                                R.string.act_plan_de_viaje_title_confirmando_llegada,
                                                R.string.text_espere_un_momento);
                                        ApiRequest.getInstance().againRequest();
                                    }
                                });*/
                    }
                }

                @Override
                public void onError(VolleyError error) {
                    error.printStackTrace();
                    BaseModalsView.hideProgressDialog();
                    CommonUtils.vibrateDevice(getActivity(), 100);
                    BaseModalsView.showToast(getActivity(), R.string.volley_error_message, Toast.LENGTH_LONG);
                    /*BaseModalsView.showSnackBar(view.baseFindViewById(R.id.toolbar),
                            R.string.volley_error_message, Snackbar.LENGTH_LONG,
                            R.string.text_volver_a_intentar,
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    BaseModalsView.showProgressDialog(view.getContextView(),
                                            R.string.act_plan_de_viaje_title_confirmando_llegada,
                                            R.string.text_espere_un_momento);
                                    ApiRequest.getInstance().againRequest();
                                }
                            });*/
                }
            };

            Date date = new Date();
            String fecha = new SimpleDateFormat("dd/MM/yyyy").format(date);
            String hora = new SimpleDateFormat("HH:mm:ss").format(date);

            horaParada = new SimpleDateFormat("HH:mm").format(date);

            String[] params = new String[]{
                    paradaProgramada.getIdStop(),
                    ParadaProgramada.Status.LLEGO_AGENCIA + "",
                    fecha, hora, "0", "0", "0",
                    InfoDevice.getBattery(getActivity()),
                    "0", "0",
                    Preferences.getInstance().getString("idUsuario", "")
            };

            interactor.updateEstado(params, callback);
        }
    }

    private void confirmarSalidaAgencia() {
        BaseModalsView.showProgressDialog(getActivity(),
                R.string.act_plan_de_viaje_title_confirmando_salida,
                R.string.text_espere_un_momento);

        if (validateSalidaLlegada()) {
            RequestCallback callback = new RequestCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    try {
                        BaseModalsView.hideProgressDialog();
                        if (response.getBoolean("success")) {
                            updateEstadoRecorridoParadaProgramada(ParadaProgramada.Status.SALIO_AGENCIA);
                            showInfoTiempo();
                            setupButtons();
                            BaseModalsView.showToast(getActivity(),
                                    R.string.act_plan_de_viaje_message_success_confirmar_salida, Toast.LENGTH_SHORT);
                            sendLlegadaSalidaParadaProgramadaReceiver();
                        } else {
                            CommonUtils.vibrateDevice(getActivity(), 100);
                            BaseModalsView.showToast(getActivity(),
                                    response.getString("msg_error"), Toast.LENGTH_SHORT);
                        }
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                        BaseModalsView.hideProgressDialog();
                        CommonUtils.vibrateDevice(getActivity(), 100);
                        BaseModalsView.showToast(getActivity(), R.string.json_object_exception, Toast.LENGTH_LONG);
                        /*showSnackBar(view.baseFindViewById(R.id.toolbar),
                                R.string.json_object_exception, Snackbar.LENGTH_LONG,
                                R.string.text_volver_a_intentar,
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        showProgressDialog(getActivity(),
                                                R.string.act_plan_de_viaje_title_confirmando_salida,
                                                R.string.text_espere_un_momento);
                                        ApiRequest.getInstance().againRequest();
                                    }
                                });*/
                    }
                }

                @Override
                public void onError(VolleyError error) {
                    error.printStackTrace();
                    BaseModalsView.hideProgressDialog();
                    CommonUtils.vibrateDevice(getActivity(), 100);
                    BaseModalsView.showToast(getActivity(), R.string.volley_error_message, Toast.LENGTH_LONG);
                    /*showSnackBar(view.baseFindViewById(R.id.toolbar),
                            R.string.volley_error_message, Snackbar.LENGTH_LONG,
                            R.string.text_volver_a_intentar,
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    showProgressDialog(getActivity(),
                                            R.string.act_plan_de_viaje_title_confirmando_salida,
                                            R.string.text_espere_un_momento);
                                    ApiRequest.getInstance().againRequest();
                                }
                            });*/
                }
            };

            Date date = new Date();
            String fecha = new SimpleDateFormat("dd/MM/yyyy").format(date);
            String hora = new SimpleDateFormat("HH:mm:ss").format(date);

            horaParada = new SimpleDateFormat("HH:mm").format(date);

            String[] params = new String[]{
                    paradaProgramada.getIdStop(),
                    ParadaProgramada.Status.SALIO_AGENCIA + "",
                    fecha, hora, "0", "0", "0",
                    InfoDevice.getBattery(getActivity()),
                    "0", "0",
                    Preferences.getInstance().getString("idUsuario", "")
            };

            interactor.updateEstado(params, callback);
        }
    }

    /**
     * Receiver
     *
     * {@link PlanDeViajePresenter#llegadaSalidaParadaProgramadaReceiver}
     * {@link GoogleMapPresenter#llegadaSalidaParadaProgramadaReceiver}
     */
    private void sendLlegadaSalidaParadaProgramadaReceiver() {
        Intent intent = new Intent(LocalAction.LLEGADA_SALIDA_PARADA_PROGRAMADA_ACTION);
        intent.putExtra("paradaProgramada", paradaProgramada);
        intent.putExtra("isParadaHub", isParadaHub());
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
    }

}