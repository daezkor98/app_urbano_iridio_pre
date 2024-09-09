package com.urbanoexpress.iridio3.pe.ui.dialogs;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.urbanoexpress.iridio3.R;
import com.urbanoexpress.iridio3.databinding.ModalReportarIncidenteBinding;
import com.urbanoexpress.iridio3.pe.presenter.ReportarIncidentePresenter;
import com.urbanoexpress.iridio3.pe.util.CameraUtils;
import com.urbanoexpress.iridio3.pe.util.MetricsUtils;
import com.urbanoexpress.iridio3.pe.view.ReportarIncidenteView;

public class ReportarIncidenteDialog extends DialogFragment implements ReportarIncidenteView {

    public static final String TAG = ReportarIncidenteDialog.class.getSimpleName();

    private ModalReportarIncidenteBinding binding;
    private ReportarIncidentePresenter presenter;

    public ReportarIncidenteDialog() { }

    public static ReportarIncidenteDialog newInstance(String idPlanViaje, int idMotivoIncidente) {
        ReportarIncidenteDialog fragment = new ReportarIncidenteDialog();
        Bundle args = new Bundle();
        args.putString("idPlanViaje", idPlanViaje);
        args.putInt("idMotivoIncidente", idMotivoIncidente);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        binding = ModalReportarIncidenteBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setupViews();

        Log.d(TAG, "idPlanViaje:  " + getArguments().getString("idPlanViaje", "0"));
        presenter = new ReportarIncidentePresenter(ReportarIncidenteDialog.this,
                getArguments().getString("idPlanViaje", "0"),
                getArguments().getInt("idMotivoIncidente", 0));
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
    public void showImage(String imagePath) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                MetricsUtils.dpToPx(getActivity(), 100),
                MetricsUtils.dpToPx(getActivity(), 100));
        binding.imgBtnCamera.setLayoutParams(layoutParams);

        Glide.with(this)
                .load(imagePath)
                .centerCrop()
                .into(binding.imgBtnCamera);

        //imgBtnCamera.setEnabled(false);
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

    private void configImagenIncidente() {
        int idMotivoIncidente =  getArguments().getInt("idMotivoIncidente");
        switch (idMotivoIncidente) {
            case 8:
                binding.imgIncidente.setImageResource(R.drawable.img_incidente_desastre_natural);
                binding.lblTituloIncidente.setText("Desastre natural");
                break;
            case 11:
                binding.imgIncidente.setImageResource(R.drawable.img_incidente_huelga);
                binding.lblTituloIncidente.setText("Huelga en carretera");
                break;
            case 59:
                binding.imgIncidente.setImageResource(R.drawable.img_incidente_accidente_carro);
                binding.lblTituloIncidente.setText("Accidente en carretera");
                break;
            case 60:
                binding.imgIncidente.setImageResource(R.drawable.img_incidente_revision_policial);
                binding.lblTituloIncidente.setText("Revisión de carga");
                break;
            case 61:
                binding.imgIncidente.setImageResource(R.drawable.img_incidente_robo);
                binding.lblTituloIncidente.setText("Robo de unidad");
                break;
        }
    }

    private void setupViews() {
        configImagenIncidente();

        binding.imgBtnCamera.setOnClickListener(v -> presenter.onClickCamera());

        binding.btnCancelar.setOnClickListener(v -> dismiss());

        binding.btnReportar.setOnClickListener(v -> presenter.onClickReportarIncidente());
    }

}