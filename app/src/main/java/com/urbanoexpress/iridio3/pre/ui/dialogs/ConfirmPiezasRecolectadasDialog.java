package com.urbanoexpress.iridio3.pre.ui.dialogs;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.urbanoexpress.iridio3.pre.databinding.ModalConfirmPiezasRecolectadasBinding;
import com.urbanoexpress.iridio3.pre.ui.model.PiezaRecolectadaItem;
import com.urbanoexpress.iridio3.pre.util.CommonUtils;
import com.urbanoexpress.iridio3.pre.util.constant.LocalAction;

public class ConfirmPiezasRecolectadasDialog extends DialogFragment {

    public static final String TAG = "ConfirmPiezasRecolectadasDialog";

    private ModalConfirmPiezasRecolectadasBinding binding;
    private PiezaRecolectadaItem pieza;
    private int position;

    public static ConfirmPiezasRecolectadasDialog newInstance(PiezaRecolectadaItem pieza,
                                                              int position) {
        ConfirmPiezasRecolectadasDialog fragment = new ConfirmPiezasRecolectadasDialog();
        Bundle bundle = new Bundle();
        bundle.putParcelable("pieza", pieza);
        bundle.putInt("position", position);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            pieza = getArguments().getParcelable("pieza");
            position = getArguments().getInt("position");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        binding = ModalConfirmPiezasRecolectadasBinding.inflate(inflater, container, false);
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
        binding.totalPiezasEditText.setText(pieza.getPiezas());

        binding.totalPiezasEditText.setOnKeyListener((v, keyCode, keyEvent) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_UP) {
                onConfirmPiezasClick();
            }
            return false;
        });

        binding.cancelButton.setOnClickListener(view -> dismiss());
        binding.confirmButton.setOnClickListener(view -> onConfirmPiezasClick());

        binding.totalPiezasEditText.requestFocus();
        binding.totalPiezasEditText.setSelection(0, binding.totalPiezasEditText.getText().length());
    }

    private void onConfirmPiezasClick() {
        if (binding.totalPiezasEditText.getText().toString().trim().isEmpty()) {
            CommonUtils.vibrateDevice(getActivity(), 100);
            binding.totalPiezasEditText.setError("Ingrese las piezas correctamente.");
            binding.totalPiezasEditText.requestFocus();
            return;
        }

        if (Integer.parseInt(binding.totalPiezasEditText.getText().toString().trim()) < 1) {
            CommonUtils.vibrateDevice(getActivity(), 100);
            binding.totalPiezasEditText.setError("La cantidad de piezas debe ser mayor a cero.");
            binding.totalPiezasEditText.requestFocus();
            return;
        }

        CommonUtils.showOrHideKeyboard(getActivity(), false, binding.totalPiezasEditText);
        pieza.setPiezas(binding.totalPiezasEditText.getText().toString().trim());
        sendConfirmPiezasRecolectdasAction();
        dismiss();
    }

    /**
     * Receiver
     *
     * {@link RecoleccionGEDialog#confirmPiezasRecolectadasReceiver}
     */
    private void sendConfirmPiezasRecolectdasAction() {
        Intent intent = new Intent(LocalAction.CONFIRM_PIEZAS_RECOLECTADAS_ACTION);
        intent.putExtra("pieza", pieza);
        intent.putExtra("position", position);
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
    }
}