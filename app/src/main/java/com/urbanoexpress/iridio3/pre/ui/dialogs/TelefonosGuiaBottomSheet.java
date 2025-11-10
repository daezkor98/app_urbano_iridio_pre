package com.urbanoexpress.iridio3.pre.ui.dialogs;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.urbanoexpress.iridio3.pre.R;
import com.urbanoexpress.iridio3.pre.application.AndroidApplication;
import com.urbanoexpress.iridio3.pre.databinding.BottomSheetTelefonosGuiaBinding;
import com.urbanoexpress.iridio3.pre.model.entity.Ruta;
import com.urbanoexpress.iridio3.pre.ui.adapter.TelefonoGuiaHeaderItem;
import com.urbanoexpress.iridio3.pre.ui.adapter.TelefonoGuiaV2Adapter;
import com.urbanoexpress.iridio3.pre.ui.model.TelefonoGuiaItem;
import com.urbanoexpress.iridio3.pre.util.constant.LocalAction;

import java.util.ArrayList;
import java.util.List;

public class TelefonosGuiaBottomSheet extends BottomSheetDialogFragment
        implements TelefonoGuiaV2Adapter.OnTelefonoGuiaListener {

    public static final String TAG = "TelefonosGuiaBottomSheet";

    private BottomSheetTelefonosGuiaBinding binding;

    private Ruta guia;

    private List<TelefonoGuiaV2Adapter.WrapperItem> items;

    private BottomSheetBehavior bottomSheetBehavior;

    public static TelefonosGuiaBottomSheet newInstance(Ruta guia) {
        TelefonosGuiaBottomSheet fragment = new TelefonosGuiaBottomSheet();
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

        View view = View.inflate(getContext(), R.layout.bottom_sheet_telefonos_guia, null);

        dialog.setContentView(view);
        binding = BottomSheetTelefonosGuiaBinding.bind(view);

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

    @Override
    public void onBtnLlamarClick(int position) {
        dismiss();
        Intent intent = new Intent(LocalAction.CALL_PHONE_ACTION);
        intent.putExtra("phone", ((TelefonoGuiaItem) items.get(position)).getTelefono());
        LocalBroadcastManager.getInstance(AndroidApplication.getAppContext()).sendBroadcast(intent);
    }

    private void setupViews() {
        binding.rvTelefonos.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.rvTelefonos.setHasFixedSize(true);

        if (guia != null) {
            items = new ArrayList<>();

            String[] nombres = new String[0];

            if (guia.getNombreTelContactoGestion() != null &&
                    !guia.getNombreTelContactoGestion().trim().isEmpty()) {
                nombres = guia.getNombreTelContactoGestion().trim().split(",");
            }

            if (guia.getTelContactoGestion() != null &&
                    !guia.getTelContactoGestion().trim().isEmpty()) {
                String[] telefonos = guia.getTelContactoGestion().trim().split(",");

                if (telefonos.length > 0) items.add(
                        new TelefonoGuiaHeaderItem("Números de auxilio"));

                if (nombres.length == telefonos.length) {
                    for (int i=0; i < telefonos.length; i++) {
                        if (!telefonos[i].isEmpty()) {
                            items.add(new TelefonoGuiaItem(telefonos[i].trim(), nombres[i]));
                        }
                    }
                } else {
                    for (String telefono: telefonos) {
                        if (!telefono.isEmpty()) {
                            items.add(new TelefonoGuiaItem(telefono.trim()));
                        }
                    }
                }
            }

            if (guia.getCelular() != null && !guia.getCelular().trim().isEmpty()) {
                String[] celulares = guia.getCelular().trim().split(",");

                if (celulares.length > 0) items.add(
                        new TelefonoGuiaHeaderItem("Números de celular"));

                for (String celular: celulares) {
                    if (!celular.isEmpty()) {
                        items.add(new TelefonoGuiaItem(celular.trim()));
                    }
                }
            }

            if (guia.getTelefono() != null && !guia.getTelefono().trim().isEmpty()) {
                String[] telefonos = guia.getTelefono().trim().split(",");

                if (telefonos.length > 0) items.add(
                        new TelefonoGuiaHeaderItem("Teléfonos fijo"));

                for (String telefono: telefonos) {
                    if (!telefono.isEmpty()) {
                        items.add(new TelefonoGuiaItem(telefono.trim()));
                    }
                }
            }

            if (items.size() > 0) {
                TelefonoGuiaV2Adapter adapter = new TelefonoGuiaV2Adapter(items, this);
                binding.rvTelefonos.setAdapter(adapter);
            } else {
                binding.msgNoTieneTelefonosContentLayout.setVisibility(View.VISIBLE);
            }

            binding.btnAceptar.setOnClickListener((v) -> {
                dismiss();
            });
        }
    }
}