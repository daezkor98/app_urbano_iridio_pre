package com.urbanoexpress.iridio.ui.dialogs;

import android.os.Bundle;
import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.urbanoexpress.iridio.databinding.BottomSheetInfoIncidenteRutaBinding;
import com.urbanoexpress.iridio.model.entity.IncidenteRuta;
import com.urbanoexpress.iridio.util.CommonUtils;
import com.urbanoexpress.iridio.util.FileUtils;

public class InfoIncidenteRutaBottomSheetDialog extends BottomSheetDialogFragment {

    public static final String TAG = "InfoIncidenteRutaBottomSheetDialog";

    private BottomSheetInfoIncidenteRutaBinding binding;
    private IncidenteRuta incidente;

    public static InfoIncidenteRutaBottomSheetDialog newInstance(IncidenteRuta incidente) {
        InfoIncidenteRutaBottomSheetDialog trazarRutaBottomSheet = new InfoIncidenteRutaBottomSheetDialog();
        Bundle args = new Bundle();
        args.putSerializable("incidente", incidente);
        trazarRutaBottomSheet.setArguments(args);
        return trazarRutaBottomSheet;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = BottomSheetInfoIncidenteRutaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews();
    }

    private void setupViews() {
        incidente = (IncidenteRuta) getArguments().getSerializable("incidente");

        switch (incidente.getIdMotivoIncidente()) {
            case "8":
                binding.lblNombreTipoIncidente.setText("Desastre natural");
                break;
            case "11":
                binding.lblNombreTipoIncidente.setText("Huelga en carretera");
                break;
            case "59":
                binding.lblNombreTipoIncidente.setText("Accidente en carretera");
                break;
            case "60":
                binding.lblNombreTipoIncidente.setText("Revisión de carga");
                break;
            case "61":
                binding.lblNombreTipoIncidente.setText("Robo de unidad");
                break;
        }

        binding.lblHoraIncidente.setText("reportado a la(s) " + CommonUtils.getFormatHora(incidente.getHora()));

        binding.lblComentarios.setText(incidente.getComentarios().isEmpty()
                ? "No hay comentarios." : incidente.getComentarios());

        if (FileUtils.existFile(incidente.getImagePath() + incidente.getImageName())) {
            Glide.with(this)
                    .load(incidente.getImagePath() + incidente.getImageName())
                    .centerCrop()
                    .into(binding.imgIncidente);
        } else {
            binding.imgIncidente.setVisibility(View.GONE);
        }
    }

}