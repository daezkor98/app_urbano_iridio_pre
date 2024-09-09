package com.urbanoexpress.iridio3.pe.ui.dialogs;

import android.app.Dialog;
import android.content.Context;
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
import com.urbanoexpress.iridio3.pe.util.async.AsyncTaskCoroutine;
import com.urbanoexpress.iridio3.pe.R;
import com.urbanoexpress.iridio3.pe.databinding.BottomSheetGestionDevolucionGuiaBinding;
import com.urbanoexpress.iridio3.pe.model.entity.Data;
import com.urbanoexpress.iridio3.pe.model.entity.DescargaRuta;
import com.urbanoexpress.iridio3.pe.model.entity.GuiaGestionada;
import com.urbanoexpress.iridio3.pe.model.entity.Pieza;
import com.urbanoexpress.iridio3.pe.model.entity.Ruta;
import com.urbanoexpress.iridio3.pe.model.interactor.RutaPendienteInteractor;
import com.urbanoexpress.iridio3.pe.presenter.DetalleRutaPresenter;
import com.urbanoexpress.iridio3.pe.presenter.RutaGestionadaPresenter;
import com.urbanoexpress.iridio3.pe.presenter.RutaPendientePresenter;
import com.urbanoexpress.iridio3.pe.ui.model.PiezaItem;
import com.urbanoexpress.iridio3.pe.ui.adapter.PiezasGuiaDevolucionAdapter;
import com.urbanoexpress.iridio3.pe.util.Preferences;
import com.urbanoexpress.iridio3.pe.view.BaseModalsView;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GestionDevolucionGuiaBottomSheet extends BottomSheetDialogFragment {

    public static final String TAG = "GestionDevolucionGuiaBottomSheet";

    private BottomSheetGestionDevolucionGuiaBinding binding;

    private Ruta guia;
    private List<PiezaItem> items;

    private BottomSheetBehavior bottomSheetBehavior;

    public static GestionDevolucionGuiaBottomSheet newInstance(Ruta guia) {
        GestionDevolucionGuiaBottomSheet fragment = new GestionDevolucionGuiaBottomSheet();
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

        View view = View.inflate(getContext(), R.layout.bottom_sheet_gestion_devolucion_guia, null);

        dialog.setContentView(view);
        binding = BottomSheetGestionDevolucionGuiaBinding.bind(view);

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
                binding.lblSubTitle.setText(guia.getGuia() + " " + getString(R.string.text_dot)
                        + " " + piezas.size() + " pieza");
            } else {
                binding.lblSubTitle.setText(guia.getGuia() + " " + getString(R.string.text_dot)
                        + " " + piezas.size() + " piezas");
            }

            if (piezas.size() > 1) {
                binding.lblTotalGuias.setText("Recuerde que tiene que devolver " + piezas.size() + " piezas");
                binding.msgTotalPiezasContentLayout.setVisibility(View.VISIBLE);
            }

            PiezasGuiaDevolucionAdapter adapter = new PiezasGuiaDevolucionAdapter(items);
            binding.rvPiezas.setAdapter(adapter);

            binding.btnDevolver.setOnClickListener((v) -> {
                dismiss();
                new DevolverGuiaTask(getActivity(), guia.getIdServicio(), guia.getLineaNegocio()).execute();
            });
        }
    }

    /**
     * Receiver
     *
     * {@link DetalleRutaPresenter#descargaFinalizadaReceiver}
     * {@link RutaPendientePresenter#descargaFinalizadaReceiver}
     * {@link RutaGestionadaPresenter#descargaFinalizadaReceiver}
     */
    private void sendOnDescargaFinalizadaReceiver() {
        Intent intent = new Intent("OnDescargaFinalizada");
        Bundle bundle = new Bundle();
        bundle.putSerializable("guias", new ArrayList<>(Arrays.asList(guia)));
        intent.putExtra("args", bundle);
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
    }

    private class DevolverGuiaTask extends AsyncTaskCoroutine<String, String> {

        private Context context;
        private String idServicio;
        private String lineaNegocio;

        public DevolverGuiaTask(Context context, String idServicio, String lineaNegocio) {
            this.context = context;
            this.idServicio = idServicio;
            this.lineaNegocio = lineaNegocio;
        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            if (context != null) {
                BaseModalsView.showProgressDialog(context, R.string.text_espere_un_momento);
            }
        }

        @Override
        public String doInBackground(String... strings) {
            DescargaRuta descargaRuta = RutaPendienteInteractor.selectDescargaRuta(idServicio, lineaNegocio);
            if (descargaRuta != null) {
                /*descargaRuta.setProcesoDescarga(DescargaRuta.Entrega.FINALIZADO);
                descargaRuta.save();*/
                descargaRuta.delete();
            }

            Ruta ruta = RutaPendienteInteractor.selectRuta(idServicio, lineaNegocio);
            if (ruta != null) {
                /*ruta.setResultadoGestion(Ruta.ResultadoGestion.EFECTIVA_COMPLETA);
                ruta.save();*/
                ruta.delete();
            }

            List<Pieza> piezas = RutaPendienteInteractor.selectPiezas(idServicio, lineaNegocio);
            for (Pieza pieza: piezas) {
                pieza.delete();
            }

            GuiaGestionada guiaGestionada = new GuiaGestionada(
                    Preferences.getInstance().getString("idUsuario", ""),
                    idServicio,
                    "dv",
                    Ruta.ZONA.RURAL,
                    "E",
                    "3",
                    LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")),
                    "",
                    "",
                    "",
                    "",
                    "",
                    "", "", "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    Data.Delete.NO,
                    Data.Validate.VALID,
                    2
            );
            guiaGestionada.save();
            return null;
        }

        @Override
        public void onPostExecute(String s) {
            super.onPostExecute(s);
            BaseModalsView.hideProgressDialog();
            sendOnDescargaFinalizadaReceiver();
        }
    }
}