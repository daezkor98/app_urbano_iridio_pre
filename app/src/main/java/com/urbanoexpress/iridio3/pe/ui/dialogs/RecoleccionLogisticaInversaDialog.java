package com.urbanoexpress.iridio3.pe.ui.dialogs;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.urbanoexpress.iridio3.pe.R;
import com.urbanoexpress.iridio3.pe.databinding.ModalRecoleccionLogisticaInversaBinding;
import com.urbanoexpress.iridio3.pe.model.entity.Ruta;
import com.urbanoexpress.iridio3.pe.presenter.RecoleccionLogisticaInversaPresenter;
import com.urbanoexpress.iridio3.pe.ui.helpers.ModalHelper;
import com.urbanoexpress.iridio3.pe.ui.adapter.GalleryAdapter;
import com.urbanoexpress.iridio3.pe.ui.adapter.model.GalleryWrapperItem;
import com.urbanoexpress.iridio3.pe.util.CameraUtils;
import com.urbanoexpress.iridio3.pe.util.CommonUtils;
import com.urbanoexpress.iridio3.pe.util.GridSpacingItemDecoration;
import com.urbanoexpress.iridio3.pe.util.MetricsUtils;
import com.urbanoexpress.iridio3.pe.view.RecoleccionLogisticaInversaView;

import java.util.ArrayList;
import java.util.List;

public class RecoleccionLogisticaInversaDialog extends BaseDialogFragment
        implements RecoleccionLogisticaInversaView, GalleryAdapter.OnGalleryListener {

    public static final String TAG = RecoleccionLogisticaInversaDialog.class.getSimpleName();

    private ModalRecoleccionLogisticaInversaBinding binding;

    private RecoleccionLogisticaInversaPresenter presenter;

    public RecoleccionLogisticaInversaDialog() { }

    public static RecoleccionLogisticaInversaDialog newInstance(ArrayList<Ruta> rutas) {
        RecoleccionLogisticaInversaDialog fragment = new RecoleccionLogisticaInversaDialog();
        Bundle args = new Bundle();
        args.putSerializable("guias", rutas);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        binding = ModalRecoleccionLogisticaInversaBinding.inflate(inflater, container, false);

        initUI();

        if (presenter == null) {
            presenter = new RecoleccionLogisticaInversaPresenter(this,
                    (ArrayList<Ruta>) getArguments().getSerializable("guias"));
            presenter.init();
        }

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            getDialog().getWindow().setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT,
                    ConstraintLayout.LayoutParams.MATCH_PARENT);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dismissProgressDialog();
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
            showToast(R.string.activity_resumen_ruta_message_error_al_tomar_foto);
        }
    }

    @Override
    public void setTextGuiaElectronica(String text) {
        binding.lblGuiaElectronica.setText(text);
    }

    @Override
    public void setTextFormSobre(String text) {
        binding.txtFrmRecSobre.setText(text);
    }

    @Override
    public void setTextFormValija(String text) {
        binding.txtFrmRecValija.setText(text);
    }

    @Override
    public void setTextFormPaquete(String text) {
        binding.txtFrmRecPaquete.setText(text);
    }

    @Override
    public void setTextFormOtros(String text) {
        binding.txtFrmRecOtros.setText(text);
    }

    @Override
    public void setTextBtnSiguiente(String text) {
        binding.btnSiguiente.setText(text);
    }

    @Override
    public String getTextFormSobre() {
        return binding.txtFrmRecSobre.getText().toString().trim();
    }

    @Override
    public String getTextFormValija() {
        return binding.txtFrmRecValija.getText().toString().trim();
    }

    @Override
    public String getTextFormPaquete() {
        return binding.txtFrmRecPaquete.getText().toString().trim();
    }

    @Override
    public String getTextFormOtros() {
        return binding.txtFrmRecOtros.getText().toString().trim();
    }

    @Override
    public String getTextComentarios() {
        return binding.txtComentarios.getText().toString().trim();
    }

    @Override
    public void showFotosEnGaleria(List<GalleryWrapperItem> items) {
        try {
            GalleryAdapter adapter = new GalleryAdapter(getActivity(), items);
            adapter.setListener(this);
            binding.rvGaleriaFotos.setAdapter(adapter);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void showFotosDomicilioEnGaleria(List<GalleryWrapperItem> items) {
        try {
            GalleryAdapter adapter = new GalleryAdapter(getActivity(), items);
            adapter.setListener(this);
            binding.rvGaleriaDomicilio.setAdapter(adapter);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void showStepFormulario() {
        binding.stepFormularioContainerLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void showStepFotosProducto() {
        binding.stepFotosProductoContainerLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void showStepFotosDomicilio() {
        binding.stepFotosDomicilioContainerLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideStepFormulario() {
        binding.stepFormularioContainerLayout.setVisibility(View.GONE);
    }

    @Override
    public void hideStepFotosProducto() {
        binding.stepFotosProductoContainerLayout.setVisibility(View.GONE);
    }

    @Override
    public void hideStepFotosDomicilio() {
        binding.stepFotosDomicilioContainerLayout.setVisibility(View.GONE);
    }

    @Override
    public void notifyGaleriaFotosItemRemove(int position) {
        binding.rvGaleriaFotos.getAdapter().notifyItemRemoved(position);
    }

    @Override
    public void notifyGaleriaFotosAllItemChanged() {
        binding.rvGaleriaFotos.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void notifyGaleriaDomicilioItemRemove(int position) {
        binding.rvGaleriaDomicilio.getAdapter().notifyItemRemoved(position);
    }

    @Override
    public void notifyGaleriaDomicilioAllItemChanged() {
        binding.rvGaleriaDomicilio.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void setBackgroundBtnMinusPaquetes(int color) {
        binding.actionMinusPaquetesLayout.setBackgroundColor(color);
    }

    @Override
    public void setBackgroundBtnPlusPaquetes(int color) {
        binding.actionPlusPaquetesLayout.setBackgroundColor(color);
    }

    @Override
    public void setBackgroundBtnMinusSobres(int color) {
        binding.actionMinusSobresLayout.setBackgroundColor(color);
    }

    @Override
    public void setBackgroundBtnPlusSobres(int color) {
        binding.actionPlusSobresLayout.setBackgroundColor(color);
    }

    @Override
    public void setBackgroundBtnMinusValijas(int color) {
        binding.actionMinusValijasLayout.setBackgroundColor(color);
    }

    @Override
    public void setBackgroundBtnPlusValijas(int color) {
        binding.actionPlusValijasLayout.setBackgroundColor(color);
    }

    @Override
    public void setBackgroundBtnMinusOtros(int color) {
        binding.actionMinusOtrosLayout.setBackgroundColor(color);
    }

    @Override
    public void setBackgroundBtnPlusOtros(int color) {
        binding.actionPlusOtrosLayout.setBackgroundColor(color);
    }

    @Override
    public Fragment getFragment() {
        return this;
    }

    @Override
    public void hideKeyboard() {
        CommonUtils.showOrHideKeyboard(getActivity(), false, binding.btnSiguiente);
    }

    @Override
    public void showKeyboard() {
        CommonUtils.showOrHideKeyboard(getActivity(), true, null);
    }

    @Override
    public void showMessageCantTakePhoto() {
        ModalHelper.getBuilderAlertDialog(getActivity())
                .setTitle(R.string.text_advertencia)
                .setMessage(R.string.activity_detalle_ruta_message_no_puede_tomar_foto)
                .setPositiveButton(R.string.text_aceptar, null)
                .show();
    }

    @Override
    public void showWrongDateAndTimeMessage() {
        ModalHelper.getBuilderAlertDialog(getActivity())
                .setIcon(R.mipmap.ic_launcher)
                .setTitle(R.string.text_configurar_fecha_hora)
                .setMessage(R.string.act_main_message_date_time_incorrect)
                .setPositiveButton(R.string.text_configurar, (dialog, which) ->
                        startActivity(new Intent(Settings.ACTION_DATE_SETTINGS)))
                .setNegativeButton(R.string.text_cancelar, null)
                .show();
    }

    @Override
    public void onButtonClick(int position) {
        if (presenter != null) presenter.onGalleryButtonClick(position);
    }

    @Override
    public void onDeleteImageClick(int position) {
        ModalHelper.getBuilderAlertDialog(getActivity())
                .setTitle(R.string.activity_detalle_ruta_title_eliminar_galeria)
                .setMessage(R.string.activity_detalle_ruta_message_eliminar_galeria)
                .setPositiveButton(R.string.text_aceptar,
                        (dialog, which) -> {if (presenter != null) presenter.onGalleryDeleteImageClick(position);})
                .setNegativeButton(R.string.text_cancelar, null)
                .show();
    }

    private void initUI() {
        binding.rvGaleriaFotos.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        binding.rvGaleriaFotos.setHasFixedSize(true);
        binding.rvGaleriaFotos.addItemDecoration(new GridSpacingItemDecoration(3,
                MetricsUtils.dpToPx(getActivity(), 2), true));

        binding.rvGaleriaDomicilio.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        binding.rvGaleriaDomicilio.setHasFixedSize(true);
        binding.rvGaleriaDomicilio.addItemDecoration(new GridSpacingItemDecoration(3,
                MetricsUtils.dpToPx(getActivity(), 2), true));

        binding.txtFrmRecSobre.addTextChangedListener(formRecoleccionTextChangedListener);
        binding.txtFrmRecValija.addTextChangedListener(formRecoleccionTextChangedListener);
        binding.txtFrmRecPaquete.addTextChangedListener(formRecoleccionTextChangedListener);
        binding.txtFrmRecOtros.addTextChangedListener(formRecoleccionTextChangedListener);

        binding.actionMinusPaquetesLayout.setOnClickListener(
                (v) -> presenter.onBtnPlusPaquetesClick(0));
        binding.actionPlusPaquetesLayout.setOnClickListener(
                (v) -> presenter.onBtnPlusPaquetesClick(1));

        binding.actionMinusSobresLayout.setOnClickListener(
                (v) -> presenter.onBtnPlusSobresClick(0));
        binding.actionPlusSobresLayout.setOnClickListener(
                (v) -> presenter.onBtnPlusSobresClick(1));

        binding.actionMinusValijasLayout.setOnClickListener(
                (v) -> presenter.onBtnPlusValijasClick(0));
        binding.actionPlusValijasLayout.setOnClickListener(
                (v) -> presenter.onBtnPlusValijasClick(1));

        binding.actionMinusOtrosLayout.setOnClickListener(
                (v) -> presenter.onBtnPlusOtrosClick(0));
        binding.actionPlusOtrosLayout.setOnClickListener(
                (v) -> presenter.onBtnPlusOtrosClick(1));

        binding.txtComentarios.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.lblContadorComentario.setText(s.length() + "/160");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.inputComentariosLayout.setOnClickListener(v -> {
            binding.txtComentarios.requestFocus();
            CommonUtils.showOrHideKeyboard(getActivity(), true, binding.txtComentarios);
        });

        binding.btnSiguiente.setOnClickListener((v) -> presenter.onBtnSiguienteClick());
    }

    TextWatcher formRecoleccionTextChangedListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

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

            binding.lblTotalRecolectado.setText(String.valueOf(totalRecolectado));
        }

        @Override
        public void afterTextChanged(Editable s) { }
    };

}