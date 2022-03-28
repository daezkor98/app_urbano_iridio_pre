package com.urbanoexpress.iridio3.ui;

import androidx.annotation.Nullable;
import android.os.Bundle;
import android.view.View;

import com.urbanoexpress.iridio3.R;
import com.urbanoexpress.iridio3.databinding.ActivityReportarIncidenteBinding;
import com.urbanoexpress.iridio3.ui.dialogs.ReportarIncidenteDialog;

public class ReportarIncidenteActivity extends AppThemeBaseActivity {

    private ActivityReportarIncidenteBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReportarIncidenteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupViews();
    }

    @Override
    protected void animOnLeaveActivity() {
        overridePendingTransition(R.anim.not_slide, R.anim.slide_exit_out_bottom);
    }

    private void setupViews() {
        setupToolbar(binding.toolbar);
        setScreenTitle("Reportar incidente");

        binding.btnRoboUnidad.setOnClickListener(onClickListener);
        binding.btnRevisionCarga.setOnClickListener(onClickListener);
        binding.btnAccidenteCarretera.setOnClickListener(onClickListener);
        binding.btnDesastreNatural.setOnClickListener(onClickListener);
        binding.btnHuelgaCarretera.setOnClickListener(onClickListener);
    }

    private final View.OnClickListener onClickListener = v -> {
        ReportarIncidenteDialog dialog = ReportarIncidenteDialog.newInstance(
                getIntent().getBundleExtra("args").getString("idPlanViaje"),
                Integer.parseInt(v.getTag().toString()));
        dialog.show(getSupportFragmentManager(), ReportarIncidenteDialog.TAG);
    };
}