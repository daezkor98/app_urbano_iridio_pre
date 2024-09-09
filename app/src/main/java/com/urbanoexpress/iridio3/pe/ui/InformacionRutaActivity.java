package com.urbanoexpress.iridio3.pe.ui;

import android.graphics.Color;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.events.DecoEvent;
import com.urbanoexpress.iridio3.pe.R;
import com.urbanoexpress.iridio3.pe.databinding.ActivityInformacionRutaBinding;
import com.urbanoexpress.iridio3.pe.presenter.InformacionRutaPresenter;
import com.urbanoexpress.iridio3.pe.util.Preferences;
import com.urbanoexpress.iridio3.pe.view.InformacionRutaView;

public class InformacionRutaActivity extends AppThemeBaseActivity implements InformacionRutaView {

    private ActivityInformacionRutaBinding binding;
    private InformacionRutaPresenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInformacionRutaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupViews();

        if (savedInstanceState != null) {
            Preferences.getInstance().init(this, "UserProfile");
        }

        if (presenter == null) {
            presenter = new InformacionRutaPresenter(this);
            presenter.init();
        }
    }

    @Override
    public void setTotalGuias(String totalGuias) {
        binding.totalRutasText.setText(totalGuias);
    }

    @Override
    public void setTotalPendientes(String totalPendientes) {
        binding.totalPendientesText.setText(totalPendientes);
    }

    @Override
    public void setTotalGestionados(String totalGestionados) {
        binding.totalGestionadosText.setText(totalGestionados);
    }

    @Override
    public void setTotalGestionadosFallidos(String total) {
        binding.totalGestionadosFallidosText.setText(total);
    }

    @Override
    public void setProgressRoute(int progress) {
        new Handler().postDelayed(() -> {
            binding.progressBar.setProgress(0);
            binding.progressBar.setProgressCompat(progress, true);
        }, 500);
        binding.progressText.setText(progress + "%");
    }

    @Override
    public void setProgressGestiones(int progress) {
        binding.progressGestionesText.setText(String.valueOf(progress));
        setValueArcView(binding.dvGestiones, 100, progress);
    }

    @Override
    public void setProgressImagenes(int progress) {
        binding.progressImagenesText.setText(String.valueOf(progress));
        setValueArcView(binding.dvImagenes, 100, progress);
    }

    @Override
    public void setProgressLlamadas(int progress) {
        binding.progressLlamadasText.setText(String.valueOf(progress));
        setValueArcView(binding.dvLlamadas, 100, progress);
    }

    @Override
    public void setProgressGPS(int progress) {
        binding.progressGpsText.setText(String.valueOf(progress));
        setValueArcView(binding.dvTramas, 100, progress);
    }

    @Override
    public void setValueSyncGestiones(int value) {
        binding.syncGestionesText.setText("Sincronizados: " + value);
    }

    @Override
    public void setValuePendingGestiones(int value) {
        binding.pendingGestionesText.setText("Pendientes: " + value);
    }

    @Override
    public void setValueSyncImagenes(int value) {
        binding.syncImagenesText.setText("Sincronizados: " + value);
    }

    @Override
    public void setValuePendingImagenes(int value) {
        binding.pendingImagenesText.setText("Pendientes: " + value);
    }

    @Override
    public void setValueSyncLlamadas(int value) {
        binding.syncLlamadasText.setText("Sincronizados: " + value);
    }

    @Override
    public void setValuePendingLlamadas(int value) {
        binding.pendingLlamadasText.setText("Pendientes: " + value);
    }

    @Override
    public void setValueSyncGPS(int value) {
        binding.syncGpsText.setText("Sincronizados: " + value);
    }

    @Override
    public void setValuePendingGPS(int value) {
        binding.pendingGpsText.setText("Pendientes: " + value);
    }

    @Override
    public void hideSwipeRefreshLayout() {
        if (binding.swipeRefreshLayout.isRefreshing()) {
            binding.swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void setupViews() {
        setupToolbar(binding.toolbar);
        setScreenTitle(R.string.title_activity_informacion_ruta);

        configArcView(binding.dvImagenes, 1);
        configArcView(binding.dvGestiones, 1);
        configArcView(binding.dvTramas, 1);
        configArcView(binding.dvLlamadas, 1);

        binding.progressBar.setInterpolator(new AccelerateDecelerateInterpolator());

        binding.swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorBlackUrbano);
        binding.swipeRefreshLayout.setOnRefreshListener(() -> presenter.onSwipeRefresh());
    }

    private void configArcView(DecoView arcView, int total) {
        // Create background track
        arcView.addSeries(new SeriesItem.Builder(Color.argb(255, 218, 218, 218))
                .setRange(0, total, total)
                .setInitialVisibility(false)
                .setLineWidth(15f)
                .build());

        arcView.addEvent(new DecoEvent.Builder(DecoEvent.EventType.EVENT_SHOW, true)
                .setDelay(0)
                .setDuration(750)
                .build());
    }

    private void setValueArcView(DecoView arcView, long total, long value) {
        //        Create data series track
        arcView.deleteAll();

        arcView.addSeries(new SeriesItem.Builder(Color.argb(255, 218, 218, 218))
                .setRange(0, total, total)
                .setInitialVisibility(false)
                .setLineWidth(15f)
                .build());

        arcView.addEvent(new DecoEvent.Builder(DecoEvent.EventType.EVENT_SHOW, true)
                .setDelay(0)
                .setDuration(750)
                .build());

        SeriesItem seriesItem1 = new SeriesItem.Builder(
                ContextCompat.getColor(this, R.color.blue_4))
                .setRange(0, total, 0)
                .setLineWidth(15f)
                .setChartStyle(SeriesItem.ChartStyle.STYLE_DONUT)
                .build();

        int series1Index = arcView.addSeries(seriesItem1);

        arcView.addEvent(new DecoEvent.Builder(value).setIndex(series1Index).setDelay(500)
                .setDuration(1000)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .build());
    }

}
