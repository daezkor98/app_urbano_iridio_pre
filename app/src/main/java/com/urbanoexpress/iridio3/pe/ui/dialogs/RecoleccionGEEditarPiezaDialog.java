package com.urbanoexpress.iridio3.pe.ui.dialogs;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.urbanoexpress.iridio3.pe.R;
import com.urbanoexpress.iridio3.pe.databinding.ModalRecoleccionGeEditarPiezasBinding;
import com.urbanoexpress.iridio3.pe.util.CommonUtils;
import com.urbanoexpress.iridio3.pe.util.constant.LocalAction;
import com.urbanoexpress.iridio3.pe.view.BaseModalsView;

/**
 * Created by mick on 04/08/17.
 */

public class RecoleccionGEEditarPiezaDialog extends DialogFragment {

    private ModalRecoleccionGeEditarPiezasBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        binding = ModalRecoleccionGeEditarPiezasBinding.inflate(inflater, container, false);
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

    @Override
    public void onStop() {
        super.onStop();
        CommonUtils.showOrHideKeyboard(getActivity(), false, binding.txtPieza);
    }

    private void setupViews() {
        binding.lblGuia.setText(getArguments().getString("guia"));

        binding.txtPieza.setText(getArguments().getString("pieza"));
        binding.txtPieza.setSelection(0, binding.txtPieza.getText().toString().length());

        binding.btnCancelar.setOnClickListener(view -> {
            CommonUtils.showOrHideKeyboard(getActivity(), false, binding.txtPieza);
            dismiss();
        });

        binding.btnAceptar.setOnClickListener(v -> {
            if (binding.txtPieza.getText().toString().trim().length() > 0) {
                if (Integer.parseInt(binding.txtPieza.getText().toString().trim()) >= 20) {
                    BaseModalsView.showAlertDialog(getActivity(),
                            R.string.activity_detalle_ruta_title_limite_pieza,
                            R.string.activity_detalle_ruta_msg_limite_pieza,
                            R.string.text_asignar, (dialog, i) -> {
                                sendSaveEditarPiezaAction();
                                CommonUtils.showOrHideKeyboard(getActivity(), false, binding.txtPieza);
                                dismiss();
                            },
                            R.string.text_cancelar, (dialog, i) -> dialog.dismiss());
                } else {
                    sendSaveEditarPiezaAction();
                    CommonUtils.showOrHideKeyboard(getActivity(), false, binding.txtPieza);
                    dismiss();
                }
            } else {
                BaseModalsView.showToast(getActivity(),
                        R.string.activity_detalle_ruta_msg_ingrese_pieza,
                        Toast.LENGTH_LONG);
            }
        });
    }

    /**
     * Receiver
     *
     * {@link RecoleccionGEPresenter#saveEditarPiezaReceiver}
     */
    private void sendSaveEditarPiezaAction() {
        Intent intent = new Intent(LocalAction.RECOLECCION_GE_EDITAR_PIEZA_ACTION);
        intent.putExtra("pieza", binding.txtPieza.getText().toString().trim());
        intent.putExtra("position", Integer.parseInt(getArguments().getString("position")));
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
    }

}
