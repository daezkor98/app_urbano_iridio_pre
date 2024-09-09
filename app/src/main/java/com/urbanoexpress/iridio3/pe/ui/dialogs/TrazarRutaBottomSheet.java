package com.urbanoexpress.iridio3.pe.ui.dialogs;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.urbanoexpress.iridio3.R;
import com.urbanoexpress.iridio3.databinding.BottomSheetTrazarRutaBinding;
import com.urbanoexpress.iridio3.pe.model.entity.Ruta;
import com.urbanoexpress.iridio3.pe.view.BaseModalsView;

/**
 * Created by mick on 31/03/17.
 */

public class TrazarRutaBottomSheet extends BottomSheetDialogFragment {

    public static final String TAG = "TrazarRutaBottomSheet";

    private BottomSheetTrazarRutaBinding binding;

    private Ruta guia;

    public static TrazarRutaBottomSheet newInstance(Ruta guia) {
        TrazarRutaBottomSheet trazarRutaBottomSheet = new TrazarRutaBottomSheet();
        Bundle args = new Bundle();
        args.putSerializable("guia", guia);
        trazarRutaBottomSheet.setArguments(args);
        return trazarRutaBottomSheet;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = BottomSheetTrazarRutaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews();
    }

    private void setupViews() {
        guia = (Ruta) getArguments().getSerializable("guia");

        clearBackgroundBtns();

        selectMedioNavegacionPorDefecto();

        binding.btnCoordenada.setOnClickListener(v -> {
            clearBackgroundBtns();
            selectedBtn(v, binding.lblCoordenada);
        });

        binding.btnDireccion.setOnClickListener(v -> {
            clearBackgroundBtns();
            selectedBtn(v, binding.lblDireccion);
        });

        binding.btnLibre.setOnClickListener(v -> {
            clearBackgroundBtns();
            selectedBtn(v, binding.lblLibre);
        });

        binding.fabNavegar.setOnClickListener(v -> {
            String query = "";
            if (binding.btnCoordenada.getTag().equals("selected")) {
                try {
                    Double latitude = Double.parseDouble(guia.getGpsLatitude());
                    Double longitude = Double.parseDouble(guia.getGpsLongitude());
                    if (latitude != 0 && longitude != 0) {
                        query = latitude + "," + longitude;
                    } else {
                        BaseModalsView.showToast(getActivity(),
                                R.string.activity_detalle_ruta_message_no_hay_gps,
                                Toast.LENGTH_SHORT);
                        return;
                    }
                } catch (NumberFormatException ex) {
                    ex.printStackTrace();
                    BaseModalsView.showToast(getActivity(),
                            R.string.activity_detalle_ruta_message_no_hay_gps,
                            Toast.LENGTH_SHORT);
                    return;
                }
            } else if (binding.btnDireccion.getTag().equals("selected")) {
                if (guia.getDireccion().trim().length() > 0) {
                    query = guia.getDireccion().trim().replace(" ", "+");
                } else {
                    BaseModalsView.showToast(getActivity(),
                            R.string.activity_detalle_ruta_message_no_hay_direccion,
                            Toast.LENGTH_SHORT);
                    return;
                }
            } else if (binding.btnLibre.getTag().equals("selected")) {
                query = "0,0";
            } else {
                BaseModalsView.showToast(getActivity(),
                        R.string.activity_detalle_ruta_message_seleccionar_medio_navegacion,
                        Toast.LENGTH_SHORT);
                return;
            }
            openGogleMapsOnModeNavigation(query);
        });
    }

    private void clearBackgroundBtns() {
        binding.btnCoordenada.setBackground(null);
        binding.btnDireccion.setBackground(null);
        binding.btnLibre.setBackground(null);
        binding.btnCoordenada.setTag("");
        binding.btnDireccion.setTag("");
        binding.btnLibre.setTag("");
        binding.lblCoordenada.setTextColor(ContextCompat.getColor(
                getActivity(), R.color.darkPrimaryText));
        binding.lblDireccion.setTextColor(ContextCompat.getColor(
                getActivity(), R.color.darkPrimaryText));
        binding.lblLibre.setTextColor(ContextCompat.getColor(
                getActivity(), R.color.darkPrimaryText));
    }

    private void openGogleMapsOnModeNavigation(String query) {
        try {
            Uri gmmIntentUri = Uri.parse("google.navigation:q=" + query);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            getDialog().dismiss();
            getActivity().startActivity(mapIntent);
        } catch (ActivityNotFoundException ex) {
            BaseModalsView.showToast(getActivity(),
                    "Lo sentimos, la aplicación de Google Maps no está instalada en el teléfono.",
                    Toast.LENGTH_LONG);
        }
    }

    private void selectedBtn(View btn, TextView lbl) {
        btn.setBackground(ContextCompat.getDrawable(
                getActivity(), R.drawable.bg_btns_trazar_ruta));
        lbl.setTextColor(ContextCompat.getColor(
                getActivity(), R.color.lightPrimaryText));
        btn.setTag("selected");
    }

    private void selectMedioNavegacionPorDefecto() {
        try {
            Double latitude = Double.parseDouble(guia.getGpsLatitude());
            Double longitude = Double.parseDouble(guia.getGpsLongitude());
            if (latitude != 0 && longitude != 0) {
                selectedBtn(binding.btnCoordenada, binding.lblCoordenada);
            } else {
                if (guia.getDireccion().trim().length() > 0) {
                    selectedBtn(binding.btnDireccion, binding.lblDireccion);
                } else {
                    selectedBtn(binding.btnLibre, binding.lblLibre);
                }
            }
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
            if (guia.getDireccion().trim().length() > 0) {
                selectedBtn(binding.btnDireccion, binding.lblDireccion);
            } else {
                selectedBtn(binding.btnLibre, binding.lblLibre);
            }
        }
    }

}