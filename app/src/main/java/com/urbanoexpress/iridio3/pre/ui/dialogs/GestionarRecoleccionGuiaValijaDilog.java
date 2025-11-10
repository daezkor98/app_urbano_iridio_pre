package com.urbanoexpress.iridio3.pre.ui.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.urbanoexpress.iridio3.pre.R;
import com.urbanoexpress.iridio3.pre.databinding.ModalGestionarRecoleccionGuiaValijaBinding;
import com.urbanoexpress.iridio3.pre.model.entity.Ruta;
import com.urbanoexpress.iridio3.pre.presenter.GestionarRecoleccionGuiaValijaPresenter;
import com.urbanoexpress.iridio3.pre.ui.adapter.GaleriaDescargaRutaAdapter;
import com.urbanoexpress.iridio3.pre.ui.model.GaleriaDescargaRutaItem;
import com.urbanoexpress.iridio3.pre.util.CameraUtils;
import com.urbanoexpress.iridio3.pre.util.CommonUtils;
import com.urbanoexpress.iridio3.pre.util.GridSpacingItemDecoration;
import com.urbanoexpress.iridio3.pre.util.MetricsUtils;
import com.urbanoexpress.iridio3.pre.view.GestionarRecoleccionGuiaValijaView;

import java.util.List;

public class GestionarRecoleccionGuiaValijaDilog extends DialogFragment
        implements GestionarRecoleccionGuiaValijaView {

    public static final String TAG = GestionarRecoleccionGuiaValijaDilog.class.getSimpleName();

    private ModalGestionarRecoleccionGuiaValijaBinding binding;
    private GestionarRecoleccionGuiaValijaPresenter presenter;

    public GestionarRecoleccionGuiaValijaDilog() { }

    public static GestionarRecoleccionGuiaValijaDilog newInstance(Ruta guiaValija) {
        GestionarRecoleccionGuiaValijaDilog fragment = new GestionarRecoleccionGuiaValijaDilog();
        Bundle args = new Bundle();
        args.putSerializable("guiaValija", guiaValija);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        binding = ModalGestionarRecoleccionGuiaValijaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setupViews();

        presenter = new GestionarRecoleccionGuiaValijaPresenter(
                GestionarRecoleccionGuiaValijaDilog.this,
                (Ruta) getArguments().getSerializable("guiaValija"));
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
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        presenter.onClickCancelar();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
    public void showGaleria(List<GaleriaDescargaRutaItem> galeria) {
        try {
            GaleriaDescargaRutaAdapter adapter = new GaleriaDescargaRutaAdapter(getActivity(), galeria);
            adapter.setTotalSubtractForHeader(2); // Boton de camara y firma
            adapter.setVisibilityButtonsGalery(true, true, false, false, false);
            adapter.setListener(presenter);
            binding.lvGaleriaDescarga.setAdapter(adapter);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
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
        binding.btnScanBarCode.setImageDrawable(CommonUtils.changeColorDrawable(
                getActivity(),
                R.drawable.ic_barcode_scan_white,
                R.color.gris_7));

        binding.btnCancelar.setOnClickListener(v -> presenter.onClickCancelar());

        binding.btnAceptar.setOnClickListener(v -> presenter.onClickAceptar(binding.rBtnRecolectado.isChecked()));

        binding.lvGaleriaDescarga.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        binding.lvGaleriaDescarga.setHasFixedSize(true);
        binding.lvGaleriaDescarga.addItemDecoration(new GridSpacingItemDecoration(3, MetricsUtils.dpToPx(getActivity(), 2), true));

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

        binding.btnScanBarCode.setOnClickListener(v -> presenter.onCLickBtnScanBarCode());

        binding.rBtnRecolectado.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                binding.boxFormularioRecoleccion.setVisibility(View.VISIBLE);
            } else {
                binding.boxFormularioRecoleccion.setVisibility(View.GONE);
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