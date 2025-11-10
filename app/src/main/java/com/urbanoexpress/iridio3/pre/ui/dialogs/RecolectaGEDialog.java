package com.urbanoexpress.iridio3.pre.ui.dialogs;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import com.urbanoexpress.iridio3.pre.R;
import com.urbanoexpress.iridio3.pre.databinding.ModalRecolectarGuiaBinding;
import com.urbanoexpress.iridio3.pre.model.entity.Ruta;
import com.urbanoexpress.iridio3.pre.presenter.RecolectaGEPresenter;
import com.urbanoexpress.iridio3.pre.ui.model.MotivoDescargaItem;
import com.urbanoexpress.iridio3.pre.ui.adapter.GaleriaDescargaRutaAdapter;
import com.urbanoexpress.iridio3.pre.ui.adapter.MotivoDescargaAdapter;
import com.urbanoexpress.iridio3.pre.ui.model.GaleriaDescargaRutaItem;
import com.urbanoexpress.iridio3.pre.util.CameraUtils;
import com.urbanoexpress.iridio3.pre.util.CommonUtils;
import com.urbanoexpress.iridio3.pre.util.GridSpacingItemDecoration;
import com.urbanoexpress.iridio3.pre.util.MetricsUtils;
import com.urbanoexpress.iridio3.pre.util.RecyclerTouchListener;
import com.urbanoexpress.iridio3.pre.view.RecolectaView;

/**
 * Created by mick on 18/08/16.
 */

public class RecolectaGEDialog extends DialogFragment
        implements RecolectaView {

    public static final String TAG = RecolectaGEDialog.class.getSimpleName();

    private ModalRecolectarGuiaBinding binding;
    private RecolectaGEPresenter presenter;

    public RecolectaGEDialog() { }

    public static RecolectaGEDialog newInstance(ArrayList<Ruta> rutas, int numVecesGestionado,
                                                boolean guiaElectronicaDisponible) {
        RecolectaGEDialog fragment = new RecolectaGEDialog();
        Bundle args = new Bundle();
        args.putSerializable("guias", rutas);
        args.putInt("numVecesGestionado", numVecesGestionado);
        args.putBoolean("guiaElectronicaDisponible", guiaElectronicaDisponible);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        binding = ModalRecolectarGuiaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setupViews();

        presenter = new RecolectaGEPresenter(
                RecolectaGEDialog.this,
                (ArrayList<Ruta>) getArguments().getSerializable("guias"),
                getArguments().getInt("numVecesGestionado"),
                getArguments().getBoolean("guiaElectronicaDisponible"));
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
    public void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "RESULT");
        try {
            if (CameraUtils.validateOnActivityResult(requestCode, resultCode)) {
                presenter.onActivityResultImage();
            }
        } catch (NullPointerException ex) {
            ex.printStackTrace();
            Toast.makeText(getActivity(),
                    R.string.activity_resumen_ruta_message_error_al_tomar_foto,
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void setGuiaElectronica(String guiaElectronica) {
        binding.lblGuiaElectronica.setText(guiaElectronica);
    }

    @Override
    public void showListaMotivos(List<MotivoDescargaItem> motivos) {
        try {
            MotivoDescargaAdapter adapter = new MotivoDescargaAdapter(getActivity(), motivos);
            binding.lvMotivos.setAdapter(adapter);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void showGaleria(List<GaleriaDescargaRutaItem> galeria) {
        try {
            GaleriaDescargaRutaAdapter adapter = new GaleriaDescargaRutaAdapter(getActivity(), galeria);
            adapter.setTotalSubtractForHeader(3); // Boton de camara, firma y cargo
            adapter.setVisibilityButtonsGalery(true, true, true, false, false);
            adapter.setListener(presenter);
            binding.lvGaleriaDescarga.setAdapter(adapter);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void setVisibilityLayoutInputGuia(int visible) {
        binding.layoutInputGuia.setVisibility(visible);
    }

    @Override
    public void notifyGaleryItemChanged(int position) {
        binding.lvGaleriaDescarga.getAdapter().notifyItemChanged(position);
    }

    @Override
    public void notifyGaleryItemInsert(int position) {
        binding.lvGaleriaDescarga.getAdapter().notifyItemInserted(position);
    }

    @Override
    public void notifyGaleryItemRemove(int position) {
        binding.lvGaleriaDescarga.getAdapter().notifyItemRemoved(position);
    }

    @Override
    public void notifyGaleryAllItemChanged() {
        binding.lvGaleriaDescarga.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void notifyMotivosAllItemChanged() {
        binding.lvMotivos.getAdapter().notifyDataSetChanged();
    }

    @Override
    public EditText getViewTxtGuiaManual() {
        return binding.txtGuiaElectronica;
    }

    @Override
    public EditText getViewTxtFrmGuiaRecoleccion() {
        return binding.txtGuiaRecoleccion;
    }

    @Override
    public EditText getViewTxtFrmSobre() {
        return binding.txtFrmRecSobre;
    }

    @Override
    public EditText getViewTxtFrmValija() {
        return binding.txtFrmRecValija;
    }

    @Override
    public EditText getViewTxtFrmPaquete() {
        return binding.txtFrmRecPaquete;
    }

    @Override
    public EditText getViewTxtFrmOtros() {
        return binding.txtFrmRecOtros;
    }

    @Override
    public EditText getViewTxtComentarios() {
        return binding.txtComentarios;
    }

    @Override
    public Fragment getFragment() {
        return this;
    }

    @Override
    public Context getContextView() {
        return getActivity();
    }

    @Override
    public View baseFindViewById(int id) {
        return binding.getRoot().findViewById(id);
    }

    private void setupViews() {
        binding.btnScanBarCodeGuiaManual.setImageDrawable(CommonUtils.changeColorDrawable(
                getActivity(),
                R.drawable.ic_barcode_scan_white,
                R.color.gris_7));

        binding.btnScanBarCodeGuiaRecoleccion.setImageDrawable(CommonUtils.changeColorDrawable(
                getActivity(),
                R.drawable.ic_barcode_scan_white,
                R.color.gris_7));

        binding.lvMotivos.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.lvMotivos.setHasFixedSize(true);
        binding.lvMotivos.addOnItemTouchListener(
                new RecyclerTouchListener(getActivity(),
                        binding.lvMotivos, new RecyclerTouchListener.ClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        view.setFocusable(true);
                        view.setFocusableInTouchMode(true);
                        presenter.onClickItemMotivo(position);
                    }

                    @Override
                    public void onLongClick(View view, int position) {

                    }
                }));

        binding.btnUpdateMotivos.setOnClickListener(v -> presenter.onClickUpdateMotivos());

        binding.btnCancelar.setOnClickListener(v -> dismiss());

        binding.btnAceptar.setOnClickListener(v -> presenter.onClickAceptar());

        binding.lvGaleriaDescarga.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        binding.lvGaleriaDescarga.setHasFixedSize(true);
        binding.lvGaleriaDescarga.addItemDecoration(
                new GridSpacingItemDecoration(3, MetricsUtils.dpToPx(getActivity(), 2), true));

        binding.btnScanBarCodeGuiaManual.setOnClickListener(v -> presenter.onCLickBtnScanBarCode(1));

        binding.btnScanBarCodeGuiaRecoleccion.setOnClickListener(v -> presenter.onCLickBtnScanBarCode(2));

        binding.txtTotalRecolectado.setKeyListener(null);

        binding.txtFrmRecSobre.addTextChangedListener(formRecoleccionTextChangedListener);
        binding.txtFrmRecValija.addTextChangedListener(formRecoleccionTextChangedListener);
        binding.txtFrmRecPaquete.addTextChangedListener(formRecoleccionTextChangedListener);
        binding.txtFrmRecOtros.addTextChangedListener(formRecoleccionTextChangedListener);

        binding.txtComentarios.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.lblContadorComentario.setText(charSequence.length() + "/160");
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    TextWatcher formRecoleccionTextChangedListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            int sobre, valija, paquete,  otros, totalRecolectado;

            sobre = binding.txtFrmRecSobre.getText().toString().trim().isEmpty()
                    ? 0 : Integer.parseInt(binding.txtFrmRecSobre.getText().toString().trim());
            valija = binding.txtFrmRecValija.getText().toString().trim().isEmpty()
                    ? 0 : Integer.parseInt(binding.txtFrmRecValija.getText().toString().trim());
            paquete = binding.txtFrmRecPaquete.getText().toString().trim().isEmpty()
                    ? 0 : Integer.parseInt(binding.txtFrmRecPaquete.getText().toString().trim());
            otros = binding.txtFrmRecOtros.getText().toString().trim().isEmpty()
                    ? 0 : Integer.parseInt(binding.txtFrmRecOtros.getText().toString().trim());

            totalRecolectado = sobre + valija + paquete + otros;

            binding.txtTotalRecolectado.setText(totalRecolectado + "");
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

}
