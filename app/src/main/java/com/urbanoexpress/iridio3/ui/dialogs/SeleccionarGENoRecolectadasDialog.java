package com.urbanoexpress.iridio3.ui.dialogs;

import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import com.urbanoexpress.iridio3.R;
import com.urbanoexpress.iridio3.databinding.ModalSeleccionarGeNoRecolectadasBinding;
import com.urbanoexpress.iridio3.model.entity.Data;
import com.urbanoexpress.iridio3.model.entity.Ruta;
import com.urbanoexpress.iridio3.ui.adapter.RecoleccionGEAdapter;
import com.urbanoexpress.iridio3.ui.model.RecoleccionGEItem;
import com.urbanoexpress.iridio3.util.Preferences;
import com.urbanoexpress.iridio3.view.BaseModalsView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mickve on 23/03/18.
 */

public class SeleccionarGENoRecolectadasDialog extends DialogFragment
        implements RecoleccionGEAdapter.OnRecoleccionGEItemClickListener  {

    public static final String TAG = "SeleccionarGENoRecole";

    private ModalSeleccionarGeNoRecolectadasBinding binding;
    private List<RecoleccionGEItem> guiasElectronicasItems = new ArrayList<>();
    private boolean listenChkSelectAllGEChange = true;
    private boolean listenChkSelectGEChange = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        binding = ModalSeleccionarGeNoRecolectadasBinding.inflate(inflater, container, false);
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
    public void onChkChangeGE(View view, int position) {
        if (listenChkSelectGEChange) {
            Log.d(TAG, "CHECKED CHANGE");
            listenChkSelectAllGEChange = false;
            onChkChangeGE();
            listenChkSelectAllGEChange = true;
        }
    }

    @Override
    public void onLongClickGE(View view, int position) {

    }

    private void setupViews() {
        binding.rvGuiasElectronicas.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.rvGuiasElectronicas.setHasFixedSize(true);


        binding.btnCancelar.setOnClickListener(v -> dismiss());

        binding.btnAceptar.setOnClickListener(v -> {
            if (isSelectedGuiasElectronicas()) {
                ArrayList<Ruta> guias = new ArrayList<Ruta>();

                for (int i = 0; i < guiasElectronicasItems.size(); i++) {
                    if (guiasElectronicasItems.get(i).isSelected()) {
                        guias.add(new Ruta(
                                Preferences.getInstance().getString("idUsuario", ""),
                                getArguments().getString("idServicio"),
                                "",  // id servicio recoleccion
                                "", // mot_id
                                "", // id_agencia
                                "", // zon_id
                                "", // ruta_id
                                guiasElectronicasItems.get(i).getGuiaNumero(),
                                "", // id_medio_pago
                                "", // id_cliente
                                "", // id_manifiesto
                                getArguments().getString("lineaNegocio"),
                                "", // shi_codigo
                                "", // fec_ruta
                                0,
                                guiasElectronicasItems.get(i).getGuiaElectronica(),
                                "R",
                                "",
                                "", // direccion
                                "", // geo_px
                                "", // geo_py
                                "", // radio_gps
                                "", // distrito
                                "", // shipper
                                "", // centro de actividad
                                "", // estado shipper
                                "", // contacto
                                guiasElectronicasItems.get(i).getPiezas(), // piezas
                                "", // horario
                                0L, // horario_aproximado
                                0L, // horario_ordenamiento
                                "", // telefono contacto gestion
                                "", // nombre telefono contacto gestion
                                "", // telefono
                                "", // celular
                                "", // medio_pago
                                "", // importe
                                "P", // tipo_envio
                                "", // anotaciones
                                "", // servicio_sms
                                "", // habilitantes
                                "", // chk ultima gestion
                                "", // gps latitude ultima gestion
                                "", // gps longitude ultima gestion
                                "", // solicita kilometraje
                                "", // requiere datos cliente
                                "", // guia requerimiento
                                "", // guia requerimiento chk
                                "", // guia requerimiento motivo
                                "", // guia requerimiento comentario
                                "", // guia requerimiento horario
                                "", // guia requerimiento nueva direccion
                                "", // premios gestion guia
                                "", // firma cliente gestion guia
                                "", // minimo fotos producto gestion guia
                                "", // descripcion
                                "", // observaciones
                                "", // secuencia ruteo
                                "0", //flagScanPck
                                0, // mostrar alerta
                                Ruta.EstadoDescarga.PENDIENTE,
                                Ruta.ResultadoGestion.NO_DEFINIDO,
                                Data.Delete.NO,
                                Data.Validate.VALID)
                        );
                    }
                }

                GestionarGENoRecolectadasDialog fragment =
                        new GestionarGENoRecolectadasDialog().newInstance(guias);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragment.show(fragmentManager, GestionarGENoRecolectadasDialog.TAG);
                dismiss();
            } else {
                BaseModalsView.showSnackBar(binding.rvGuiasElectronicas,
                        R.string.activity_detalle_ruta_msg_seleccione_guia_electronica,
                        Snackbar.LENGTH_LONG);
            }
        });

        binding.chkSelectAllGE.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (listenChkSelectAllGEChange) {
                Log.d(TAG, "CHECKED ALL");
                listenChkSelectGEChange = false;
                onChkSelectAllGE(isChecked);
                Log.d(TAG, "CHECKED ALL FINISH");
                listenChkSelectGEChange = true;
            }
        });

        guiasElectronicasItems = (ArrayList<RecoleccionGEItem>) getArguments().getSerializable("guias");
        showGuiasElectronicas();
    }

    public void showGuiasElectronicas() {
        RecoleccionGEAdapter adapter = new RecoleccionGEAdapter(getActivity(),
                guiasElectronicasItems);
        adapter.setListener(this);

        binding.rvGuiasElectronicas.setAdapter(adapter);
    }

    public void onChkChangeGE() {
        boolean selectedAllGuias = isSelectedAllGuiasElectronicas();
        binding.chkSelectAllGE.setChecked(selectedAllGuias);
    }

    public void onChkSelectAllGE(boolean isChecked) {
        for (int i = 0; i < guiasElectronicasItems.size(); i++) {
            guiasElectronicasItems.get(i).setSelected(isChecked);
        }
        binding.rvGuiasElectronicas.getAdapter().notifyDataSetChanged();
    }

    public boolean isSelectedAllGuiasElectronicas() {
        for (int i = 0; i < guiasElectronicasItems.size(); i++) {
            if (!guiasElectronicasItems.get(i).isSelected()) {
                return false;
            }
        }
        return true;
    }

    public boolean isSelectedGuiasElectronicas() {
        for (int i = 0; i < guiasElectronicasItems.size(); i++) {
            if (guiasElectronicasItems.get(i).isSelected()) {
                return true;
            }
        }
        return false;
    }

//    /**
//     * Receiver
//     *
//     * {@link RecoleccionGEPresenter#saveEditarPiezaReceiver}
//     */
//    private void sendSaveEditarPiezaAction() {
//        Intent intent = new Intent(LocalAction.RECOLECCION_GE_EDITAR_PIEZA_ACTION);
//        intent.putExtra("pieza", txtPieza.getText().toString().trim());
//        intent.putExtra("position", Integer.parseInt(getArguments().getString("position")));
//        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
//    }

}