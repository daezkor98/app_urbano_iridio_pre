package com.urbanoexpress.iridio3.ui.dialogs;

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

import com.urbanoexpress.iridio3.R;
import com.urbanoexpress.iridio3.databinding.ModalDefinirPosicionDeLaGuiaBinding;
import com.urbanoexpress.iridio3.ui.interfaces.OnClickItemListener;
import com.urbanoexpress.iridio3.util.constant.LocalAction;
import com.urbanoexpress.iridio3.view.BaseModalsView;

/**
 * Created by mick on 22/08/17.
 */

public class DefinirPosicionGuiaDialog extends DialogFragment implements OnClickItemListener {

    private ModalDefinirPosicionDeLaGuiaBinding binding;
    private int maxPosicion, actualPosicion;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        binding = ModalDefinirPosicionDeLaGuiaBinding.inflate(inflater, container, false);
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
    public void onClickIcon(View view, int position) {

    }

    @Override
    public void onClickItem(View view, int position) {
    }

    private void setupViews() {
        maxPosicion = getArguments().getInt("maxPosicion");
        actualPosicion = getArguments().getInt("actualPosicion");

        binding.btnCancelar.setOnClickListener(v -> dismiss());

        binding.btnAceptar.setOnClickListener(v -> {
            if (!binding.txtPosicion.getText().toString().isEmpty()) {
                if (Integer.parseInt(binding.txtPosicion.getText().toString()) > 0 &&
                        Integer.parseInt(binding.txtPosicion.getText().toString()) <= maxPosicion) {
                    dismiss();
                    sendOnDefinirPosicionGuiaReceiver(
                            Integer.parseInt(binding.txtPosicion.getText().toString()) - 1);
                } else {
                    BaseModalsView.showToast(getActivity(),
                            R.string.activity_ruta_msg_max_limite_posicion_guia,
                            Toast.LENGTH_SHORT);
                }
            } else {
                BaseModalsView.showToast(getActivity(),
                        R.string.activity_ruta_msg_ingrese_la_posicion_correctamente,
                        Toast.LENGTH_SHORT);
            }
        });

        binding.txtPosicion.setText(String.valueOf(actualPosicion + 1));
        binding.txtPosicion.setSelection(0, binding.txtPosicion.getText().length());
        binding.txtPosicion.requestFocus();
    }

    /**
     * Receiver
     *
     * {@link RutaPendientePresenter#definirPosicionGuiaReceiver}
     */
    private void sendOnDefinirPosicionGuiaReceiver(int nuevaPosicion) {
        Intent intent = new Intent(LocalAction.DEFINIR_POSICION_DE_GUIA_ACTION);
        intent.putExtra("actualPosicion", getArguments().getInt("actualPosicion"));
        intent.putExtra("nuevaPosicion", nuevaPosicion);
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
    }
}
