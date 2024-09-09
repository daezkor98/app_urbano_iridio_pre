package com.urbanoexpress.iridio3.pe.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.events.DecoEvent;
import com.urbanoexpress.iridio3.R;
import com.urbanoexpress.iridio3.databinding.ActivityResumenRutaRuralBinding;
import com.urbanoexpress.iridio3.pe.presenter.ResumenRutaRuralPresenter;
import com.urbanoexpress.iridio3.pe.ui.dialogs.DatePickerDailogFragment;
import com.urbanoexpress.iridio3.pe.util.Preferences;
import com.urbanoexpress.iridio3.pe.view.ResumenRutaRuralView;

public class ResumenRutaRuralActivity extends AppThemeBaseActivity implements ResumenRutaRuralView,
        DatePickerDailogFragment.OnDatePickerDailogFragmentListener {

    private ActivityResumenRutaRuralBinding binding;
    private ResumenRutaRuralPresenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityResumenRutaRuralBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupViews();

        if (savedInstanceState != null) {
            Preferences.getInstance().init(this, "UserProfile");
        }

        if (presenter == null) {
            presenter = new ResumenRutaRuralPresenter(this);
        }
    }

    @Override
    protected void animOnLeaveActivity() {
        overridePendingTransition(R.anim.not_slide, R.anim.slide_exit_out_bottom);
    }

    @Override
    public void setTextDate(String text) {
        binding.lblDate.setText(text);
    }

    @Override
    public void setTextCourier(String text) {

    }

    @Override
    public void setTextZona(String text) {
        binding.lblZona.setText(text);
    }

    @Override
    public void setTextFecha(String text) {

    }

    @Override
    public void setTextTotalGuias(String text) {

    }

    @Override
    public void setTextTotalPiezas(String text) {

    }

    @Override
    public void setTextPesoSeco(String text) {
        binding.lblPesoSeco.setText(text);
    }

    @Override
    public void setTextTotalCashGuias(String text) {
        binding.lblTotalCashGuias.setText(text);
    }

    @Override
    public void setTextTotalCashImporte(String text) {
        binding.lblTotalCashImporte.setText(text);
    }

    @Override
    public void setVisibilitySwipeRefreshLayout(boolean visible) {
        binding.swipeRefreshLayout.setRefreshing(visible);
    }

    @Override
    public void openDatePicker(int year, int month, int dayOfMonth) {
        DatePickerDailogFragment newFragment = DatePickerDailogFragment.newInstance(
                year, month, dayOfMonth);
        newFragment.show(getSupportFragmentManager(), DatePickerDailogFragment.TAG);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        presenter.onDateSet(view, year, month, dayOfMonth);
    }

    private void setupViews() {
        setupToolbar(binding.toolbar);
        setScreenTitle(getIntent().getExtras().getString("module_name"));

        binding.swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorBlackUrbano);
        binding.swipeRefreshLayout.setOnRefreshListener(() -> presenter.onSwipeRefresh());

        binding.actionSelectDateLayout.setOnClickListener(v -> presenter.onActionSelectDateClick());

        /*setValueArcView(binding.dv1, 20, 5);
        setValueArcView(binding.dv2, 10, 1);
        setValueArcView(binding.dv3, 30, 13);
        setValueArcView(binding.dv4, 2, 1);
        setValueArcView(binding.dv5, 45, 10);
        setValueArcView(binding.dv6, 100, 35);*/

        binding.btnsHeaderContainerLayout.setClipToOutline(true);
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
                ContextCompat.getColor(this, R.color.blue_1_dark))
                .setRange(0, total, 0)
                .setLineWidth(15f)
                .setChartStyle(SeriesItem.ChartStyle.STYLE_DONUT)
                .build();

        int series1Index = arcView.addSeries(seriesItem1);

        arcView.addEvent(new DecoEvent.Builder(value).setIndex(series1Index).setDelay(1000).build());
    }
}