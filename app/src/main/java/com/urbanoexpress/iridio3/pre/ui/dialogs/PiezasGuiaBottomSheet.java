package com.urbanoexpress.iridio3.pre.ui.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.urbanoexpress.iridio3.pre.R;
import com.urbanoexpress.iridio3.pre.databinding.BottomSheetPiezasGuiaBinding;
import com.urbanoexpress.iridio3.pre.model.entity.Pieza;
import com.urbanoexpress.iridio3.pre.model.entity.Ruta;
import com.urbanoexpress.iridio3.pre.model.interactor.RutaPendienteInteractor;
import com.urbanoexpress.iridio3.pre.ui.model.PiezaItem;
import com.urbanoexpress.iridio3.pre.ui.adapter.PiezasGuiaDevolucionAdapter;

import java.util.ArrayList;
import java.util.List;

public class PiezasGuiaBottomSheet extends BottomSheetDialogFragment {

    public static final String TAG = "PiezasGuiaBottomSheet";

    private BottomSheetPiezasGuiaBinding binding;

    private Ruta guia;
    private List<PiezaItem> items;

    private BottomSheetBehavior bottomSheetBehavior;

    public static PiezasGuiaBottomSheet newInstance(Ruta guia) {
        PiezasGuiaBottomSheet fragment = new PiezasGuiaBottomSheet();
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

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        View view = View.inflate(getContext(), R.layout.bottom_sheet_piezas_guia, null);

        dialog.setContentView(view);
        binding = BottomSheetPiezasGuiaBinding.bind(view);

        bottomSheetBehavior = BottomSheetBehavior.from((View) view.getParent());
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    dismiss();
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        setupViews();

        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    private void setupViews() {
        binding.rvPiezas.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.rvPiezas.setHasFixedSize(true);

        if (guia != null) {
            List<Pieza> piezas = RutaPendienteInteractor.selectPiezas(guia.getIdServicio(),
                    guia.getLineaNegocio());

            items = new ArrayList<>();

            for (Pieza pieza: piezas) {
                items.add(new PiezaItem(
                        pieza.getIdPieza(),
                        pieza.getIdServicioGuia(),
                        pieza.getBarra(),
                        pieza.getChkEstado(),
                        pieza.getDescripcionEstado().toLowerCase(),
                        pieza.getFechaEstado(),
                        pieza.getEstadoManifiesto() == 1,
                        false,
                        false,
                        false));
            }

            if (piezas.size() == 1) {
                binding.lblSubTitle.setText(piezas.size() + " pieza");
            } else {
                binding.lblSubTitle.setText(piezas.size() + " piezas");
            }

            PiezasGuiaDevolucionAdapter adapter = new PiezasGuiaDevolucionAdapter(items);
            binding.rvPiezas.setAdapter(adapter);

            binding.btnAceptar.setOnClickListener((v) -> {
                dismiss();
            });
        }
    }
}