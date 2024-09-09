package com.urbanoexpress.iridio3.pe.ui.dialogs;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.urbanoexpress.iridio3.pe.R;
import com.urbanoexpress.iridio3.pe.databinding.ModalAsignarRutaBinding;
import com.urbanoexpress.iridio3.pe.util.Preferences;
import com.urbanoexpress.iridio3.pe.util.Session;

/**
 * Created by mick on 04/04/17.
 */

public class AsignarRutaDialog extends BaseDialogFragment {

    public static final String TAG = "AsignarRutaDialog";

    private ModalAsignarRutaBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        binding = ModalAsignarRutaBinding.inflate(inflater, container, false);
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
        binding.txtIdRuta.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence source, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence source, int i, int i1, int i2) {
                if (source.length() > 0
                        && !(source.length() == 1 && source.toString().equals("0"))) {
                    binding.btnEliminarIdRuta.setVisibility(View.VISIBLE);
                } else {
                    binding.btnEliminarIdRuta.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.txtIdRuta.setText((Preferences.getInstance().getInt("idRuta", 0) + ""));
        binding.txtIdRuta.setSelection(0, binding.txtIdRuta.getText().toString().length());

        binding.btnEliminarIdRuta.setOnClickListener(view -> {
            binding.txtIdRuta.setText((0 + ""));
            binding.txtIdRuta.setSelection(0, binding.txtIdRuta.getText().toString().length());
        });

        binding.btnCancelar.setOnClickListener(view -> dismiss());

        binding.btnAsignar.setOnClickListener(view -> {
            if (binding.txtIdRuta.getText().toString().trim().length() > 0) {
                Preferences.getInstance().edit().putInt(
                        "idRuta",
                        Integer.parseInt(binding.txtIdRuta.getText().toString().trim())).apply();

                Session.getUser().setIdRuta(
                        Integer.parseInt(binding.txtIdRuta.getText().toString().trim()) + "");
                Session.getUser().save();

                showToast(R.string.activity_ruta_message_ruta_asignada_correctamente);
                dismiss();
            } else {
                showToast(R.string.activity_ruta_message_ingrese_id_ruta);
            }
        });
    }
}
