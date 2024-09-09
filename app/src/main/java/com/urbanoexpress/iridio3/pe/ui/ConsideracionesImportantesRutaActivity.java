package com.urbanoexpress.iridio3.pe.ui;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.urbanoexpress.iridio3.pe.R;
import com.urbanoexpress.iridio3.pe.databinding.ActivityConsideracionesImportantesRutaBinding;
import com.urbanoexpress.iridio3.pe.presenter.ConsideracionesImportantesRutaPresenter;
import com.urbanoexpress.iridio3.pe.ui.model.RutaItem;
import com.urbanoexpress.iridio3.pe.ui.adapter.RutaAdapter;
import com.urbanoexpress.iridio3.pe.view.ConsideracionesImportantesRutaView;

import java.util.List;

public class ConsideracionesImportantesRutaActivity extends AppThemeBaseActivity
        implements ConsideracionesImportantesRutaView {

    private ActivityConsideracionesImportantesRutaBinding binding;
    private ConsideracionesImportantesRutaPresenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityConsideracionesImportantesRutaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupViews();

        if (presenter == null) {
            presenter = new ConsideracionesImportantesRutaPresenter(this);
        }
    }

    @Override
    public void showDatosRutas(List<RutaItem> rutasPendientes) {
        try {
            if (rutasPendientes.size() > 0) {
                binding.rvRutas.setBackgroundColor(Color.parseColor("#f1f1f1"));
            } else {
                binding.rvRutas.setBackgroundColor(Color.parseColor("#ffffff"));
            }
            RutaAdapter rutaAdapter = new RutaAdapter(this, presenter, rutasPendientes);
            binding.rvRutas.setAdapter(rutaAdapter);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void showTotalRecoleccionesExpress(long total) {
        binding.lblTotalRecoleccionesExpress.setText(String.valueOf(total));
    }

    @Override
    public void showTotalGuiasRequerimiento(long total) {
        binding.lblTotalGuiasRequerimientos.setText(String.valueOf(total));
    }

    @Override
    public void setVisibilitySwipeRefreshLayout(boolean visible) {
        binding.swipeRefreshLayout.setRefreshing(visible);
    }

    private void setupViews() {
        setupToolbar(binding.toolbar);
        setScreenTitle(R.string.title_activity_consideraciones_importantes_ruta);

        binding.rvRutas.setLayoutManager(new LinearLayoutManager(this));
        binding.rvRutas.setHasFixedSize(true);

        binding.swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorBlackUrbano);
        binding.swipeRefreshLayout.setOnRefreshListener(() -> presenter.onSwipeRefresh());
    }
}