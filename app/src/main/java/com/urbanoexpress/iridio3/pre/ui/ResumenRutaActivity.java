package com.urbanoexpress.iridio3.pre.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.urbanoexpress.iridio3.pre.R;
import com.urbanoexpress.iridio3.pre.databinding.ActivityResumenRutaBinding;
import com.urbanoexpress.iridio3.pre.model.entity.ResumenRuta;
import com.urbanoexpress.iridio3.pre.presenter.ResumenRutaPresenter;
import com.urbanoexpress.iridio3.pre.util.LocationUtils;
import com.urbanoexpress.iridio3.pre.util.Preferences;
import com.urbanoexpress.iridio3.pre.view.ResumenRutaView;

public class ResumenRutaActivity extends AppThemeBaseActivity implements ResumenRutaView {

    private ActivityResumenRutaBinding binding;
    private ResumenRutaPresenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityResumenRutaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupViews();

        if (savedInstanceState != null) {
            Preferences.getInstance().init(this, "UserProfile");
        }

        if (presenter == null) {
            presenter = new ResumenRutaPresenter(this);
            presenter.init();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocationUtils.validateSwitchedOnGPS(this, new LocationUtils.OnSwitchedOnGPSListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(Exception ex) {
                startActivity(new Intent(ResumenRutaActivity.this, TurnOnGPSActivity.class));
            }
        });
    }

    @Override
    protected void animOnLeaveActivity() {
        overridePendingTransition(R.anim.not_slide, R.anim.slide_exit_out_bottom);
    }

    @Override
    public void setDatosResumenRuta(ResumenRuta resumenRuta) {
        if (Integer.parseInt(resumenRuta.getIdRuta()) > 0) {
            binding.lblRuta.setText(resumenRuta.getIdRuta());
            binding.lblZona.setText(resumenRuta.getCodZona());

            if (resumenRuta.getFechaInicioRuta() != null
                    && !resumenRuta.getFechaInicioRuta().isEmpty()) {
                binding.lblHoraInicio.setTextColor(ContextCompat.getColor(
                        this, R.color.gris_2));
                binding.lblTiempoRuta.setTextColor(ContextCompat.getColor(
                        this, R.color.gris_2));
                binding.lblHoraInicio.setText(resumenRuta.getFechaInicioRuta());
                binding.lblTiempoRuta.setText(resumenRuta.getTiempoRuta());
            } else {
                binding.lblHoraInicio.setTextColor(ContextCompat.getColor(
                        this, R.color.colorPrimary));
                binding.lblTiempoRuta.setTextColor(ContextCompat.getColor(
                        this, R.color.colorPrimary));
                binding.lblHoraInicio.setText(R.string.activity_resumen_ruta_msg_no_inicio_ruta);
                binding.lblTiempoRuta.setText(R.string.activity_resumen_ruta_msg_no_inicio_ruta);
            }
        } else {
            binding.lblRuta.setText(R.string.fragment_ruta_pendiente_message_no_hay_rutas);
            binding.lblZona.setText(resumenRuta.getCodZona());
            binding.lblHoraInicio.setText("");
            binding.lblTiempoRuta.setText("");
        }

        binding.lblCourier.setText(resumenRuta.getCourier());
        binding.lblPlaca.setText(resumenRuta.getPlaca());
        binding.lblChofer.setText(resumenRuta.getChofer());
        binding.lblTotalGuias.setText(resumenRuta.getTotalGuias());
        binding.lblTotalPiezas.setText(resumenRuta.getTotalPiezas());
        binding.lblTotalVolumen.setText(resumenRuta.getVolumen());
        binding.lblPesoSeco.setText(resumenRuta.getPesoSeco());
        binding.lblTotalCashGuias.setText(resumenRuta.getTotalCashGuias());
        binding.lblTotalCashImporte.setText(resumenRuta.getTotalCashImporte());
        binding.lblTotalCardGuias.setText(resumenRuta.getTotalCardGuias());
        binding.lblTotalCardImporte.setText(resumenRuta.getTotalCardImporte());
    }

    @Override
    public void setVisibilitySwipeRefreshLayout(boolean visible) {
        binding.swipeRefreshLayout.setRefreshing(visible);
    }

    private void setupViews() {
        setupToolbar(binding.toolbar);
        setScreenTitle(getIntent().getExtras().getString("module_name"));

        binding.swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorBlackUrbano);
        binding.swipeRefreshLayout.setOnRefreshListener(() -> presenter.onSwipeRefresh());
    }
}
