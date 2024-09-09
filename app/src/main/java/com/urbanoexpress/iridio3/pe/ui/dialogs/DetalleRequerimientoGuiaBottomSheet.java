package com.urbanoexpress.iridio3.pe.ui.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.urbanoexpress.iridio3.R;
import com.urbanoexpress.iridio3.databinding.BottomSheetRequerimientoGuiaBinding;
import com.urbanoexpress.iridio3.pe.model.entity.Ruta;
import com.urbanoexpress.iridio3.pe.ui.model.DetailsItem;
import com.urbanoexpress.iridio3.pe.ui.adapter.DetailsAdapter;

import java.util.ArrayList;
import java.util.List;

public class DetalleRequerimientoGuiaBottomSheet extends BottomSheetDialogFragment {

    public static final String TAG = "DetalleRequerimientoGuiaBottomSheet";

    private BottomSheetRequerimientoGuiaBinding binding;
    private Ruta guia;

    public static DetalleRequerimientoGuiaBottomSheet newInstance(Ruta guia) {
        DetalleRequerimientoGuiaBottomSheet fragment = new DetalleRequerimientoGuiaBottomSheet();
        Bundle args = new Bundle();
        args.putSerializable("guia", guia);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_MyApp_BottomSheetDialog);

        if (getArguments() != null) {
            guia = (Ruta) getArguments().getSerializable("guia");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = BottomSheetRequerimientoGuiaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews();
    }

    private void setupViews() {
        binding.rvDetalles.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.rvDetalles.setHasFixedSize(true);

        if (guia != null) {
            List<DetailsItem> items = new ArrayList<>();

            String comentarios = guia.getGuiaRequerimientoComentario().trim().toLowerCase();

            if (comentarios.isEmpty()) {
                comentarios = "ninguno";
            }

            items.add(new DetailsItem("Motivo",
                    guia.getGuiaRequerimientoMotivo().trim()));
            items.add(new DetailsItem("Horario de entrega",
                    guia.getGuiaRequerimientoHorario().trim().toLowerCase()));

            if (Integer.parseInt(guia.getGuiaRequerimientoNuevaDireccion()) == 1) {
                items.add(new DetailsItem("Nueva dirección", guia.getDireccion()));
            }

            items.add(new DetailsItem("Comentarios", comentarios));

            DetailsAdapter adapter = new DetailsAdapter(getActivity(), items);
            binding.rvDetalles.setAdapter(adapter);

            binding.btnAceptar.setOnClickListener((v) -> {
                dismiss();
            });
        }
    }
}