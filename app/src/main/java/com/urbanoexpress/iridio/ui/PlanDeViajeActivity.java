package com.urbanoexpress.iridio.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.urbanoexpress.iridio.R;
import com.urbanoexpress.iridio.databinding.ActivityPlanDeViajeBinding;
import com.urbanoexpress.iridio.model.entity.ParadaProgramada;
import com.urbanoexpress.iridio.model.entity.PlanDeViaje;
import com.urbanoexpress.iridio.presenter.PlanDeViajePresenter;
import com.urbanoexpress.iridio.ui.adapter.ParadaProgramadaAdapter;
import com.urbanoexpress.iridio.ui.dialogs.AsignarDespachoPlanViajeDialog;
import com.urbanoexpress.iridio.ui.dialogs.InfoParadaProgramadaBottomSheetDialog;
import com.urbanoexpress.iridio.ui.dialogs.IniciarTerminarRutaPlanDeViajeDialog;
import com.urbanoexpress.iridio.ui.dialogs.SelectOrigenPlanViajeDialog;
import com.urbanoexpress.iridio.ui.interfaces.OnClickItemListener;
import com.urbanoexpress.iridio.ui.model.ParadaProgramadaItem;
import com.urbanoexpress.iridio.util.InfoDevice;
import com.urbanoexpress.iridio.util.LocationUtils;
import com.urbanoexpress.iridio.util.PermissionUtils;
import com.urbanoexpress.iridio.util.Preferences;
import com.urbanoexpress.iridio.view.PlanDeViajeView;

import java.util.ArrayList;
import java.util.List;

public class PlanDeViajeActivity extends AppThemeBaseActivity implements PlanDeViajeView,
        SelectOrigenPlanViajeDialog.OnSelectedOrigenPlanViaje, OnClickItemListener, LocationListener {

    private ActivityPlanDeViajeBinding binding;
    private PlanDeViajePresenter presenter;
    private ProgressDialog progressDialog;
    protected LocationManager mLocationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlanDeViajeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

        setupViews();

        if (savedInstanceState != null) {
            Preferences.getInstance().init(this, "UserProfile");
        }

        if (presenter == null) {
            presenter = new PlanDeViajePresenter(this);
            presenter.init();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!PermissionUtils.checkAppPermissions(this)) {
            startActivity(new Intent(this, RequestPermissionActivity.class));
            overridePendingTransition(R.anim.slide_enter_from_bottom, R.anim.not_slide);
        }

        LocationUtils.validateSwitchedOnGPS(PlanDeViajeActivity.this, new LocationUtils.OnSwitchedOnGPSListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(Exception ex) {
                startActivity(new Intent(PlanDeViajeActivity.this, TurnOnGPSActivity.class));
            }
        });
    }

    @Override
    protected void onDestroy() {
        mLocationManager.removeUpdates(this);
        presenter.onDestroyActivity();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.menu_plan_de_viaje, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_asignar_despachos) {
            presenter.onClickBtnAsignarDespachos();
            return true;
        } else if (item.getItemId() == R.id.action_forzar_terminar_ruta) {
            presenter.onClickBtnForzarCierreRuta();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onBackButtonPressed() {
        if (binding.txtPlaca.hasFocus()) {
            hideFormEditarPlaca();
            return true;
        }
        return false;
    }

    @Override
    protected void animOnLeaveActivity() {
        overridePendingTransition(R.anim.not_slide, R.anim.slide_exit_out_bottom);
    }

    @Override
    public void showDatosPlanDeViaje(PlanDeViaje planDeViaje) {
        binding.lblPlaca.setText(planDeViaje.getPlaca());
        binding.lblOrigen.setText(planDeViaje.getOrigen());
        binding.lblRuta.setText(planDeViaje.getRuta());
        binding.lblTotalDespachos.setText(planDeViaje.getDespachos());
        binding.lblTotalPiezas.setText(planDeViaje.getPiezas());

        int roundPeso = 0, roundVolumen = 0;

        try {
            roundPeso = Math.round(Float.parseFloat(planDeViaje.getPeso_uso()));
            roundVolumen = Math.round(Float.parseFloat(planDeViaje.getVolumen_uso()));
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
        }

        binding.lblTotalPeso.setText(planDeViaje.getPeso() + " Kg");
        binding.lblTotalVolumen.setText(planDeViaje.getVolumen() + " mt³");
        binding.lblPorcentajeTPeso.setText(roundPeso + "%");
        binding.lblPorcentajeTVolumen.setText(roundVolumen + "%");
        binding.pgTotalPeso.setProgress(roundPeso);
        binding.pgTotalVolumen.setProgress(roundVolumen);

        if (!binding.fabMapaParadasProgramadas.isShown()) {
            new Handler().postDelayed(() -> binding.fabMapaParadasProgramadas.show(), 500);
        }
    }

    @Override
    public void showDatosParadasProgramadas(List<ParadaProgramadaItem> paradasProgramadas) {
        ParadaProgramadaAdapter adapter = new ParadaProgramadaAdapter(PlanDeViajeActivity.this, paradasProgramadas);
        binding.lvParadasProgramadas.setAdapter(adapter);
    }

    @Override
    public void showNoDatosPlanDeViaje() {
        binding.lblPlaca.setText(getString(R.string.act_plan_de_viaje_no_definido));
        binding.lblOrigen.setText(getString(R.string.act_plan_de_viaje_no_definido));
        binding.lblRuta.setText(getString(R.string.act_plan_de_viaje_no_definido));
    }

    @Override
    public void changeTextLabelPlaca(String placa) {
        binding.lblPlaca.setText(placa);
    }

    @Override
    public void setImagenesSincronizadas(String imagenesSincronizadas) {
        binding.lblImagenesSincronizadas.setText(imagenesSincronizadas);
    }

    @Override
    public void setIncidentesSincronizados(String incidentesSincronizados) {
        binding.lblIncidentesSincronizados.setText(incidentesSincronizados);
    }

    @Override
    public void hideFormEditarPlaca() {
        binding.txtPlaca.setVisibility(View.GONE);
        binding.btnGuardarPlaca.setVisibility(View.GONE);
        binding.btnCloseEditarPlaca.setVisibility(View.GONE);
        binding.lblPlaca.setVisibility(View.VISIBLE);
        binding.btnEditarPlaca.setVisibility(View.VISIBLE);
    }

    @Override
    public void clearFormEditarPlaca(){
        binding.txtPlaca.setText("");
    }

    @Override
    public void setVisibilitySwipeRefreshLayout(boolean visible) {
        binding.swipeRefreshLayout.setRefreshing(visible);
    }

    @Override
    public void setEnabledBtnInciarRuta(boolean enabled, int drawable) {
        binding.btnIniciarRuta.setEnabled(enabled);
        changeBackgroundView(binding.btnIniciarRuta, drawable);
    }

    @Override
    public void setEnabledBtnTerminarRuta(boolean enabled, int drawable) {
        binding.btnTerminarRuta.setEnabled(enabled);
        changeBackgroundView(binding.btnTerminarRuta, drawable);
    }

    @Override
    public void setVisibilityBtnEditarPlaca(int visibility) {
        binding.btnEditarPlaca.setVisibility(visibility);
    }

    @Override
    public void clearPlanDeViaje() {
        showNoDatosPlanDeViaje();
        binding.lblTotalDespachos.setText("0");
        binding.lblTotalPiezas.setText("0");
        binding.lblTotalPeso.setText("0");
        binding.lblTotalVolumen.setText("0");
        binding.lblPorcentajeTPeso.setText("0%");
        binding.lblPorcentajeTVolumen.setText("0%");
        binding.pgTotalPeso.setProgress(0);
        binding.pgTotalVolumen.setProgress(0);
    }

    @Override
    public void navigateToIniciarTerminarRutaDialog(PlanDeViaje planDeViaje,
                                                    ArrayList<ParadaProgramada> paradasProgramadas) {
        IniciarTerminarRutaPlanDeViajeDialog dialog = IniciarTerminarRutaPlanDeViajeDialog
                .newInstance(planDeViaje, paradasProgramadas);
        dialog.show(getSupportFragmentManager(), IniciarTerminarRutaPlanDeViajeDialog.TAG);
    }

    @Override
    public void navigateToDetailParadaProgramadaDialog(PlanDeViaje planDeViaje,
                                                       ParadaProgramada paradaProgramada) {
        InfoParadaProgramadaBottomSheetDialog dialog = InfoParadaProgramadaBottomSheetDialog
                .newInstance(planDeViaje, paradaProgramada);
        dialog.show(getSupportFragmentManager(), InfoParadaProgramadaBottomSheetDialog.TAG);
    }

    @Override
    public void navigateToSelectOrigenDialog(ArrayList<PlanDeViaje> planDeViajes) {
        SelectOrigenPlanViajeDialog dialog = SelectOrigenPlanViajeDialog.newInstance(planDeViajes);
        dialog.show(getSupportFragmentManager(), SelectOrigenPlanViajeDialog.TAG);
    }

    @Override
    public void navigateToAsignarDespachoDialog() {
        AsignarDespachoPlanViajeDialog dialog = AsignarDespachoPlanViajeDialog.newInstance();
        dialog.show(getSupportFragmentManager(), AsignarDespachoPlanViajeDialog.TAG);
    }

    @Override
    public void navigateToParadasOnMapActivity(PlanDeViaje planDeViaje,
                                               ArrayList<ParadaProgramada> paradasProgramadas,
                                               String latitude, String longitude) {
        Bundle args = new Bundle();
        args.putSerializable("planDeViaje", planDeViaje);
        args.putSerializable("paradasProgramadas", paradasProgramadas);
        args.putString("origen_gpx_latitude", latitude);
        args.putString("origen_gpx_longitude", longitude);
        startActivity(new Intent(this, GoogleMapActivity.class).putExtra("args", args));
    }

    @Override
    public void onSelectedOrigenPlanViaje(String idPlanViaje) {
        presenter.onSelectedOrigenPlanViaje(idPlanViaje);
    }

    @Override
    public void onClickIcon(View view, int position) {

    }

    @Override
    public void onClickItem(View view, int position) {
        presenter.onClickItemParadasProgaramadas(position);
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {
        if (provider.equals(LocationManager.GPS_PROVIDER)) {
            InfoDevice.showAlertMessageNoGps(this, true);
        }
    }

    private void setupViews() {
        setupToolbar(binding.toolbar);
        setScreenTitle(getIntent().getExtras().getString("module_name"));

        binding.lblPlaca.getParent().requestChildFocus(binding.lblPlaca, binding.lblPlaca);

        binding.lvParadasProgramadas.setLayoutManager(new LinearLayoutManager(PlanDeViajeActivity.this));
        binding.lvParadasProgramadas.setHasFixedSize(true);

        binding.btnEditarPlaca.setOnClickListener(v -> showFormEditarPlaca());

        binding.btnGuardarPlaca.setOnClickListener(v ->
                presenter.onClickGuardarPlaca(binding.txtPlaca.getText().toString().trim()));

        binding.btnCloseEditarPlaca.setOnClickListener(v -> presenter.onCLickCloseEditPlaca());

        binding.btnIniciarRuta.setOnClickListener(v -> presenter.onClickIniciarRuta());

        binding.btnTerminarRuta.setOnClickListener(v -> presenter.onClickTerminarRuta());

        binding.swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorBlackUrbano);
        binding.swipeRefreshLayout.setOnRefreshListener(() -> presenter.onSwipeRefresh());

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);

        binding.fabMapaParadasProgramadas.setOnClickListener(v -> presenter.onClickBtnMapaParadas());
    }

    private void showFormEditarPlaca() {
        binding.txtPlaca.setVisibility(View.VISIBLE);
        binding.btnGuardarPlaca.setVisibility(View.VISIBLE);
        binding.btnCloseEditarPlaca.setVisibility(View.VISIBLE);
        binding.lblPlaca.setVisibility(View.GONE);
        binding.btnEditarPlaca.setVisibility(View.GONE);
        binding.txtPlaca.requestFocus();
    }

    private void changeBackgroundView(View view, int drawable) {
        view.setBackground(ContextCompat.getDrawable(PlanDeViajeActivity.this, drawable));
    }
}