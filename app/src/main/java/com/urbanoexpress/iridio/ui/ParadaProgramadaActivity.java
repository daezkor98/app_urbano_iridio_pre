package com.urbanoexpress.iridio.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.appcompat.view.ActionMode;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.transition.Fade;
import android.transition.Transition;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import java.util.List;

import com.urbanoexpress.iridio.R;
import com.urbanoexpress.iridio.databinding.ActivityParadaProgramadaBinding;
import com.urbanoexpress.iridio.model.entity.ParadaProgramada;
import com.urbanoexpress.iridio.model.entity.PlanDeViaje;
import com.urbanoexpress.iridio.presenter.ParadaProgramadaPresenter;
import com.urbanoexpress.iridio.ui.adapter.DespachoAdapter;
import com.urbanoexpress.iridio.ui.dialogs.GaleriaParadaProgramadaDialog;
import com.urbanoexpress.iridio.ui.interfaces.OnClickItemDespachoListener;
import com.urbanoexpress.iridio.ui.model.DespachoItem;
import com.urbanoexpress.iridio.util.Preferences;
import com.urbanoexpress.iridio.view.ParadaProgramadaView;

public class ParadaProgramadaActivity extends AppThemeBaseActivity
        implements ParadaProgramadaView, ActionMode.Callback, OnClickItemDespachoListener {

    private ActivityParadaProgramadaBinding binding;
    private ParadaProgramadaPresenter presenter;
    private ActionMode actionMode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityParadaProgramadaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().setEnterTransition(makeEnterTransition());

        setupViews();

        if (savedInstanceState != null) {
            Preferences.getInstance().init(this, "UserProfile");
        }

        if (presenter == null) {
            Bundle args = getIntent().getExtras().getBundle("args");
            presenter = new ParadaProgramadaPresenter(this,
                    (PlanDeViaje) args.getSerializable("plan_de_viaje"),
                    (ParadaProgramada) args.getSerializable("parada_programada"));
            presenter.init();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.menu_parada_programada, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_agregar_fotos_parada_programada) {
            presenter.onClickActionAgregarFotos();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.action_menu_parada_programada, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        switch (presenter.getMenuActionMode()) {
            case ParadaProgramadaPresenter.MenuActionMode.MENU_DESPACHO_BAJADA:
                menu.findItem(R.id.action_confirmar_subida).setVisible(false);
                return true;
            case ParadaProgramadaPresenter.MenuActionMode.MENU_DESPACHO_SUBIDA:
                menu.findItem(R.id.action_confirmar_babaja).setVisible(false);
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_check_all) {
            presenter.onClickSelectAllDespachos();
            return true;
        } else if (id == R.id.action_confirmar_babaja) {
            presenter.onClickConfirmarDespachos();
            return true;
        } else if (id == R.id.action_confirmar_subida) {
            presenter.onClickConfirmarDespachos();
            return true;
        }
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        actionMode = null;
        changeStatusBarColor(R.color.colorPrimaryDark);
        presenter.onBackPressed();
    }

    @Override
    public boolean onBackButtonPressed() {
        return presenter.onBackPressed();
    }

    @Override
    public void onClickItem(View view, int position, int menuActionMode) {
        presenter.onLongClickDespacho(position, menuActionMode);
    }

    @Override
    public void showDatosDespachoBajadas(List<DespachoItem> despachoBajadas) {
        DespachoAdapter adapter = new DespachoAdapter(this, despachoBajadas,
                ParadaProgramadaPresenter.MenuActionMode.MENU_DESPACHO_BAJADA);
        adapter.setListener(this);
        binding.lvDespachoBajadas.setAdapter(adapter);
    }

    @Override
    public void showDatosDespachoSubidas(List<DespachoItem> despachoSubidas) {
        DespachoAdapter adapter = new DespachoAdapter(this, despachoSubidas,
                ParadaProgramadaPresenter.MenuActionMode.MENU_DESPACHO_SUBIDA);
        adapter.setListener(this);
        binding.lvDespachoSubidas.setAdapter(adapter);
    }

    @Override
    public void showBoxInfo() {
        binding.boxInfo.setVisibility(View.VISIBLE);
    }

    @Override
    public void setVisibilitySwipeRefreshLayout(boolean visible) {
        binding.swipeRefreshLayout.setRefreshing(visible);
    }

    @Override
    public void showActionMode(int menuActionMode) {
        if (actionMode == null) {
            actionMode = startSupportActionMode(this);
            changeStatusBarColor(R.color.gris_7);
        }
    }

    @Override
    public void hideActionMode() {
        actionMode.finish();
    }

    @Override
    public void setTitleActionMode(String title) {
        if (actionMode != null) {
            actionMode.setTitle(title);
        }
    }

    @Override
    public void setTitleActivity(String title) {
        setScreenTitle(title);
    }

    @Override
    public void navigateToGaleriaParadaProgramadaModal(String idPlanDeViaje, String idParadaProgramada,
                                                  int paradaProgramadaEstadoLlegada) {
        GaleriaParadaProgramadaDialog fragment = GaleriaParadaProgramadaDialog
                .newInstance(idPlanDeViaje, idParadaProgramada, paradaProgramadaEstadoLlegada);
        fragment.show(getSupportFragmentManager(), GaleriaParadaProgramadaDialog.TAG);
    }

    private void setupViews() {
        setupToolbar(binding.toolbar);

        binding.lvDespachoBajadas.setLayoutManager(new LinearLayoutManager(this));
        binding.lvDespachoBajadas.setHasFixedSize(true);

        binding.lvDespachoSubidas.setLayoutManager(new LinearLayoutManager(this));
        binding.lvDespachoSubidas.setHasFixedSize(true);

        binding.swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorBlackUrbano);
        binding.swipeRefreshLayout.setOnRefreshListener(() -> presenter.onSwipeRefresh());
    }

    private void changeStatusBarColor(int resColor) {
        getWindow().setStatusBarColor(ContextCompat.getColor(this, resColor));
    }

    private Transition makeEnterTransition() {
        Transition fade = new Fade();
        fade.excludeTarget(android.R.id.navigationBarBackground, true);
        fade.excludeTarget(android.R.id.statusBarBackground, true);
        return fade;
    }

}
