package com.urbanoexpress.iridio3.pe.ui.dialogs;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.urbanoexpress.iridio3.data.local.PreferencesHelper;
import com.urbanoexpress.iridio3.databinding.ModalEditarNumeroTelefonoBinding;
import com.urbanoexpress.iridio3.pe.util.constant.Country;
import com.urbanoexpress.iridio3.pe.util.constant.LocalAction;

public class EditarNumeroTelefonoDialog extends DialogFragment {

    public static final String TAG = "EditarNumeroTelefonoDialog";

    private ModalEditarNumeroTelefonoBinding binding;

    private String phone = "";
    private int type = 0, position = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        binding = ModalEditarNumeroTelefonoBinding.inflate(inflater, container, false);
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
        phone = getArguments().getString("phone");
        type = getArguments().getInt("type");
        position = getArguments().getInt("position");

        Log.d("ACTIVITY", "PHONE: " + phone);
        Log.d("ACTIVITY", "TYPE: " + type);
        Log.d("ACTIVITY", "POSITION: " + position);

        binding.txtTelefono.setText(phone);
        binding.txtTelefono.setSelection(binding.txtTelefono.getText().length());

        if (type == 1) {
            binding.lblTitle.setText("Editar teléfono fijo");
            binding.lblTelefono.setText("Teléfono fijo");
            binding.lblMsg.setText("Ingrese el número del teléfono fijo correctamente.");
        } else {
            binding.lblTitle.setText("Editar teléfono celular");
            binding.lblTelefono.setText("Teléfono celular");
            binding.lblMsg.setText("Ingrese el número del teléfono celular correctamente.");
        }

        binding.btnCancelar.setOnClickListener(v -> dismiss());

        binding.btnGuardar.setOnClickListener(v -> {
            if (validatePhoneNumber()) {
                phone = binding.txtTelefono.getText().toString().trim();
                sendGuardarNumeroTelefonoReceiver();
                dismiss();
            } else {
                String msg = type == 1
                        ? "El número del teléfono fijo es incorrecto."
                        : "El número del teléfono celular es incorrecto.";
                binding.txtTelefono.setError(msg);
                binding.txtTelefono.requestFocus();
            }
        });

        configFormatTxtNumero();
    }

    private void configFormatTxtNumero() {
        int country = new PreferencesHelper(getActivity()).getCountry();
        switch (country) {
            case Country.CHILE:
            case Country.ECUADOR:
                binding.txtTelefono.setFilters(new InputFilter[] { new InputFilter.LengthFilter(10) });
                break;
            case Country.PERU:
                binding.txtTelefono.setFilters(new InputFilter[] { new InputFilter.LengthFilter(9) });
                break;
        }
    }

    private boolean validatePhoneNumber() {
        String phone = binding.txtTelefono.getText().toString().trim();

        if (phone.length() == 0) {
            return false;
        } else {
            int country = new PreferencesHelper(getActivity()).getCountry();
            switch (country) {
                case Country.CHILE:
                    // 9 - 10 telephone length
                    // 9 cell phone length => init 9
                    Log.d("ACTIVITY", "VALIDATION CHILE");
                    if (phone.substring(0, 1).equals("9")) {
                        if (phone.length() != 9) {
                            Log.d("ACTIVITY", "VALIDATION CELL PHONE ERROR");
                            return false;
                        }
                    } else if (phone.length() < 9 || phone.length() > 10) {
                        Log.d("ACTIVITY", "VALIDATION PHONE ERROR");
                        return false;
                    }
                    break;
                case Country.ECUADOR:
                    // 9 - 10 telephone length
                    // 10 cell phone length => init 09
                    Log.d("ACTIVITY", "VALIDATION ECUADOR");
                    if (phone.substring(0, 2).equals("09")) {
                        if (phone.length() != 10) {
                            Log.d("ACTIVITY", "VALIDATION CELL PHONE ERROR");
                            return false;
                        }
                    } else if (phone.length() < 9 || phone.length() > 10) {
                        Log.d("ACTIVITY", "VALIDATION PHONE ERROR");
                        return false;
                    }
                    break;
                case Country.PERU:
                    // 7 - 9 telephone length
                    // 9 cell phone length => init 9
                    Log.d("ACTIVITY", "VALIDATION PERU");
                    if (phone.substring(0, 1).equals("9")) {
                        if (phone.length() != 9) {
                            Log.d("ACTIVITY", "VALIDATION CELL PHONE ERROR");
                            return false;
                        }
                    } else if (phone.length() < 7 || phone.length() > 9) {
                        Log.d("ACTIVITY", "VALIDATION PHONE ERROR");
                        return false;
                    }
                    break;
            }
            return true;
        }
    }

    /**
     * Receiver
     *
     * {@link DetalleRutaPresenter#guardarNumeroTelefonoReceiver}
     */
    private void sendGuardarNumeroTelefonoReceiver() {
        Intent intent = new Intent(LocalAction.GUARDAR_NUMERO_TELEFONO_ACTION);
        intent.putExtra("phone", phone);
        intent.putExtra("type", type);
        intent.putExtra("position", position);
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
    }
}