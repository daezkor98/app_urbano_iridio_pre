package com.urbanoexpress.iridio3.pe.ui.dialogs;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.urbanoexpress.iridio3.pe.databinding.ModalGePendientesSinCoordenadasBinding;
import com.urbanoexpress.iridio3.pe.model.entity.Ruta;
import com.urbanoexpress.iridio3.pe.ui.model.RutaItem;
import com.urbanoexpress.iridio3.pe.ui.adapter.RutaAdapter;
import com.urbanoexpress.iridio3.pe.util.constant.LocalAction;

import java.util.ArrayList;

/**
 * Created by zerocul on 12/12/17.
 */

public class GuiasPendientesSinCoordenadasDialog extends DialogFragment
        implements RutaAdapter.OnClickGuiaItemListener {

    private ModalGePendientesSinCoordenadasBinding binding;
    private ArrayList<Ruta> guias;
    private ArrayList<RutaItem> items;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        binding = ModalGePendientesSinCoordenadasBinding.inflate(inflater, container, false);
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
    public void onClickGuiaItem(View view, int position) {
        sendOnEditarCoordenadaGuiaReceiver(position);
        dismiss();
    }

    @Override
    public void onClickGuiaIconLinea(View view, int position) {

    }

    @Override
    public void onClickGuiaIconImporte(View view, int position) {

    }

    @Override
    public void onClickGuiaIconTipoEnvio(View view, int position) {

    }

    private void setupViews() {
        binding.rvGuias.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.rvGuias.setHasFixedSize(true);

        guias = (ArrayList<Ruta>) getArguments().getSerializable("guias");
        items = (ArrayList<RutaItem>) getArguments().getSerializable("items");

        Log.d("ACTIVITY", "TOTAL ITEMS: " + guias.size());

        RutaAdapter rutaAdapter = new RutaAdapter(getActivity(), this, items);
        binding.rvGuias.setAdapter(rutaAdapter);
    }

    /**
     * Receiver
     *
     * {@link MapaRutaDelDiaPresenter#editarCoordenadaGuiaReceiver}
     */
    private void sendOnEditarCoordenadaGuiaReceiver(int position) {
        Intent intent = new Intent(LocalAction.EDITAR_COORDENADA_GE);
        intent.putExtra("guia", guias.get(position));
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
    }
}