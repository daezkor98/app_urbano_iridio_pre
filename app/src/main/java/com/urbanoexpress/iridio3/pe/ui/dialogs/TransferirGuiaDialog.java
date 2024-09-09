package com.urbanoexpress.iridio3.pe.ui.dialogs;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.urbanoexpress.iridio3.pe.util.async.AsyncTaskCoroutine;
import com.urbanoexpress.iridio3.R;
import com.urbanoexpress.iridio3.data.local.PreferencesHelper;
import com.urbanoexpress.iridio3.databinding.ModalTransferirGuiaBinding;
import com.urbanoexpress.iridio3.pe.model.entity.PlanDeViaje;
import com.urbanoexpress.iridio3.pe.model.entity.Ruta;
import com.urbanoexpress.iridio3.pe.model.interactor.RutaPendienteInteractor;
import com.urbanoexpress.iridio3.pe.model.interactor.TransferirGuiaInteractor;
import com.urbanoexpress.iridio3.pe.model.interactor.callback.RequestCallback;
import com.urbanoexpress.iridio3.pe.presenter.CodeScannerImpl;
import com.urbanoexpress.iridio3.pe.presenter.QRScannerPresenter;
import com.urbanoexpress.iridio3.pe.presenter.RutaPendientePresenter;
import com.urbanoexpress.iridio3.pe.ui.QRScannerActivity;
import com.urbanoexpress.iridio3.pe.ui.interfaces.OnClickItemListener;
import com.urbanoexpress.iridio3.pe.util.CommonUtils;
import com.urbanoexpress.iridio3.pe.util.Preferences;
import com.urbanoexpress.iridio3.pe.util.ValidationUtils;
import com.urbanoexpress.iridio3.pe.util.constant.Country;
import com.urbanoexpress.iridio3.pe.util.constant.LocalAction;
import com.urbanoexpress.iridio3.pe.view.BaseModalsView;

import org.apache.commons.lang3.text.WordUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by mick on 13/06/17.
 */

public class TransferirGuiaDialog extends DialogFragment implements OnClickItemListener {

    public static final String TAG = "TransferirGuiaDialog";

    private ModalTransferirGuiaBinding binding;

    private String[] guias;
    private String idZona;
    private String idPersonal;
    private String lineaNegocio;

    public static TransferirGuiaDialog newInstance(ArrayList<PlanDeViaje> origenPlanViaje) {
        TransferirGuiaDialog dialog = new TransferirGuiaDialog();
        Bundle bundle = new Bundle();
        bundle.putSerializable("origenPlanViaje", origenPlanViaje);
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            guias = (String[]) getArguments().getSerializable("guias");
            idZona = getArguments().getString("idZona");
            lineaNegocio = getArguments().getString("lineaNegocio");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        binding = ModalTransferirGuiaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews();

        LocalBroadcastManager.getInstance(getActivity())
                .registerReceiver(resultScannReceiver,
                        new IntentFilter(LocalAction.LOCAL_ACTION_BARCODE_SCAN_RESULT));
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
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(resultScannReceiver);
    }

    @Override
    public void onClickIcon(View view, int position) {

    }

    @Override
    public void onClickItem(View view, int position) {
    }

    private void setupViews() {
        ArrayList<String> tipoTransferencia = new ArrayList<>(Arrays.asList("Por Ruta", "Sin Ruta"));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                R.layout.spinner_item_text_black, tipoTransferencia);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown);
        binding.spinnerTipoTransferencia.setAdapter(adapter);

        binding.spinnerTipoTransferencia.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                if (position == 0) {
                    binding.boxPorRuta.setVisibility(View.VISIBLE);
                    binding.boxSinRuta.setVisibility(View.GONE);
                    binding.lblMsg.setText(R.string.activity_ruta_msg_transferir_guias_por_ruta);
                } else {
                    binding.boxPorRuta.setVisibility(View.GONE);
                    binding.boxSinRuta.setVisibility(View.VISIBLE);
                    binding.lblMsg.setText(R.string.activity_ruta_msg_transferir_guias_sin_ruta);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        binding.btnCancelar.setOnClickListener(view -> dismiss());

        binding.btnTransferir.setOnClickListener(view -> {
            if (CommonUtils.validateConnectivity(getActivity())) {
                if (validateDatos()) {
                    CommonUtils.showOrHideKeyboard(getActivity(),
                            false, binding.txtPlaca);
                    sendValidateTransferirGuia();
                }
            }
        });

        binding.btnScanBarCode.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), QRScannerActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("typeImpl", QRScannerPresenter.IMPLEMENT.READ_ONLY);
            intent.putExtra("args", bundle);
            getActivity().startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_enter_from_bottom, R.anim.not_slide);
        });

        configFormatTxtDocIdentidad();
    }

    private void sendValidateTransferirGuia() {
        BaseModalsView.showProgressDialog(getActivity(), R.string.text_validando_transferencia_guia);
        RequestCallback callback = new RequestCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if (response.getBoolean("success")) {
                        BaseModalsView.hideProgressDialog();

                        JSONObject data = response.getJSONArray("data").getJSONObject(0);

                        idPersonal = data.getString("per_id");

                        BaseModalsView.showAlertDialog(getActivity(),
                                R.string.activity_ruta_title_confirmar_transferencia,
                                data.getString("mensaje") + "\n\n" +
                                        "Courier: " + WordUtils.capitalize(data.getString("personal").toLowerCase()) + "\n" +
                                        "Unidad: " + data.getString("placa") + "\n\n" +
                                        "¿Desea confirmar la transferencia?",
                                R.string.text_confirmar,
                                (dialog, which) -> uploadTransferirGuias(),
                                R.string.text_cancelar, null);
                    } else {
                        BaseModalsView.hideProgressDialog();
                        BaseModalsView.showToast(getActivity(),
                                response.getString("msg_error"), Toast.LENGTH_LONG);
                    }
                } catch (JSONException ex) {
                    ex.printStackTrace();
                    BaseModalsView.hideProgressDialog();
                    BaseModalsView.showToast(getActivity(),
                            R.string.json_object_exception,
                            Toast.LENGTH_LONG);
                }
            }

            @Override
            public void onError(VolleyError error) {
                error.printStackTrace();
                BaseModalsView.hideProgressDialog();
                BaseModalsView.showToast(getActivity(),
                        R.string.volley_error_message,
                        Toast.LENGTH_LONG);
            }
        };

        String[] params;

        if (binding.spinnerTipoTransferencia.getSelectedItemPosition() == 0) {
            params = new String[]{
                    binding.txtIdRuta.getText().toString().trim(),
                    "",
                    "",
                    lineaNegocio,
                    Preferences.getInstance().getString("idUsuario", "")
            };
        } else {
            params = new String[]{
                    "",
                    binding.txtPlaca.getText().toString().trim(),
                    binding.txtDocIdentificacion.getText().toString().trim(),
                    lineaNegocio,
                    Preferences.getInstance().getString("idUsuario", "")
            };
        }

        TransferirGuiaInteractor.validateTransferirGuia(params, callback);
    }

    private void uploadTransferirGuias() {
        BaseModalsView.showProgressDialog(getActivity(), R.string.text_transfiriendo_guia);
        RequestCallback callback = new RequestCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if (response.getBoolean("success")) {
                        new DeleteGuiasTransferidasTask().execute();
                    } else {
                        BaseModalsView.hideProgressDialog();
                        BaseModalsView.showToast(getActivity(),
                                response.getString("msg_error"), Toast.LENGTH_LONG);
                    }
                } catch (JSONException ex) {
                    ex.printStackTrace();
                    BaseModalsView.hideProgressDialog();
                    BaseModalsView.showToast(getActivity(),
                            R.string.json_object_exception,
                            Toast.LENGTH_LONG);
                }
            }

            @Override
            public void onError(VolleyError error) {
                error.printStackTrace();
                BaseModalsView.hideProgressDialog();
                BaseModalsView.showToast(getActivity(),
                        R.string.volley_error_message,
                        Toast.LENGTH_LONG);
            }
        };

        String[] params;

        if (binding.spinnerTipoTransferencia.getSelectedItemPosition() == 0) {
            params = new String[] {
                    guiasToJSONArray().toString(),
                    binding.txtIdRuta.getText().toString().trim(),
                    "",
                    "",
                    idZona,
                    lineaNegocio,
                    Preferences.getInstance().getString("idUsuario", "")
            };
        } else {
            params = new String[] {
                    guiasToJSONArray().toString(),
                    "",
                    binding.txtPlaca.getText().toString().trim(),
                    idPersonal,
                    idZona,
                    lineaNegocio,
                    Preferences.getInstance().getString("idUsuario", "")
            };
        }

        TransferirGuiaInteractor.transferirGuias(params, callback);
    }

    private class DeleteGuiasTransferidasTask extends AsyncTaskCoroutine<String, String> {
        @Override
        public String doInBackground(String... strings) {

            Log.d(TAG, "Eliminar guias transferidas: " + guias.length);
            for (String idServicio : guias) {
                Ruta guia = RutaPendienteInteractor.selectRuta(idServicio, lineaNegocio);

                Log.d(TAG, "Guia: " + guia.getGuia());
                //guia.setEliminado(Data.Delete.YES);
                //guia.save();
                guia.delete();
            }

            return null;
        }

        @Override
        public void onPostExecute(String s) {
            super.onPostExecute(s);
            sendTransferenciaGuiaFinalizadaAction();
            BaseModalsView.hideProgressDialog();
            BaseModalsView.showToast(getActivity(),
                    R.string.activity_ruta_msg_transferencia_exitosa, Toast.LENGTH_LONG);
            getActivity().finish();
            getActivity().overridePendingTransition(R.anim.not_slide, R.anim.slide_exit_out_right);
        }
    }

    public JSONArray guiasToJSONArray() {
        JSONArray jsonArray = new JSONArray();
        for (String guia : guias) {
            jsonArray.put(guia);
        }
        return jsonArray;
    }

    private boolean validateDatos() {
        boolean validate;
        if (binding.spinnerTipoTransferencia.getSelectedItemPosition() == 0) {
            if (binding.txtIdRuta.getText().toString().isEmpty()) {
                binding.txtIdRuta.setError(
                        getString(R.string.activity_ruta_msg_error_datos_transferencia_ruta));
                return false;
            }
        } else {
            if (binding.txtPlaca.getText().toString().isEmpty()) {
                binding.txtPlaca.setError(
                        getString(R.string.activity_ruta_msg_error_datos_transferencia_placa));
                return false;
            }

            if (binding.txtDocIdentificacion.getText().toString().isEmpty()) {
                binding.txtDocIdentificacion.setError("Ingrese el Doc. de Identidad correctamente.");
                return false;
            } else {
                int country = new PreferencesHelper(getActivity()).getCountry();
                if (country == Country.CHILE) {
                    validate = ValidationUtils.validateCedulaChile(
                            binding.txtDocIdentificacion.getText().toString());

                    if (!validate) {
                        binding.txtDocIdentificacion.setError("El RUT es incorrecto.");
                        return false;
                    }
                }
                if (country == Country.ECUADOR) {
                    validate = ValidationUtils.validateCedulaEcuador(
                            binding.txtDocIdentificacion.getText().toString());

                    if (!validate) {
                        binding.txtDocIdentificacion.setError("La Cédula de Identidad es incorrecta.");
                        return false;
                    }
                }
                if (country == Country.PERU) {
                    if (binding.txtDocIdentificacion.getText().toString().trim().length() != 8) {
                        binding.txtDocIdentificacion.setError("El DNI es incorrecto.");
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private void configFormatTxtDocIdentidad() {
        int country = new PreferencesHelper(getActivity()).getCountry();
        switch (country) {
            case Country.CHILE:
                binding.txtDocIdentificacion.setFilters(new InputFilter[] { new InputFilter.LengthFilter(9) });
                binding.txtDocIdentificacion.setInputType(InputType.TYPE_CLASS_TEXT
                        |InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
                break;
            case Country.ECUADOR:
                binding.txtDocIdentificacion.setFilters(new InputFilter[] { new InputFilter.LengthFilter(10) });
                binding.txtDocIdentificacion.setInputType(InputType.TYPE_CLASS_NUMBER);
                break;
            case Country.PERU:
                binding.txtDocIdentificacion.setFilters(new InputFilter[] { new InputFilter.LengthFilter(8) });
                binding.txtDocIdentificacion.setInputType(InputType.TYPE_CLASS_NUMBER);
                break;
        }
    }

    /**
     * Receiver
     *
     * {@link RutaPendientePresenter#transferenciaGuiaFinalizadaReceiver}
     */
    private void sendTransferenciaGuiaFinalizadaAction() {
        Intent intent = new Intent(LocalAction.TRANSFERENCIA_GUIA_FINALIZADA_ACTION);
        intent.putExtra("guias", guias);
        intent.putExtra("lineaNegocio", lineaNegocio);
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
    }

    /**
     * Broadcast
     *
     * {@link CodeScannerImpl#sendOnResultScannListener}
     */
    private final BroadcastReceiver resultScannReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                CommonUtils.vibrateDevice(getActivity(), 100);
            } catch (NullPointerException ex) {
                ex.printStackTrace();
            }
            binding.txtIdRuta.setText(intent.getStringExtra("value"));
        }
    };
}