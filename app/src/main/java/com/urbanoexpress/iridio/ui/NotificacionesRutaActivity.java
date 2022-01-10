package com.urbanoexpress.iridio.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.View;

import com.urbanoexpress.iridio.R;
import com.urbanoexpress.iridio.databinding.ActivityNotificacionesRutaBinding;
import com.urbanoexpress.iridio.model.entity.Ruta;
import com.urbanoexpress.iridio.presenter.NotificacionesRutaPresenter;
import com.urbanoexpress.iridio.ui.adapter.NotificacionesRutaAdapter;
import com.urbanoexpress.iridio.ui.interfaces.OnClickItemListener;
import com.urbanoexpress.iridio.ui.model.NotificacionRutaItem;
import com.urbanoexpress.iridio.util.Preferences;
import com.urbanoexpress.iridio.view.NotificacionesRutaView;

import java.util.ArrayList;

public class NotificacionesRutaActivity extends AppThemeBaseActivity implements NotificacionesRutaView,
        OnClickItemListener {

    private ActivityNotificacionesRutaBinding binding;
    private NotificacionesRutaPresenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNotificacionesRutaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupViews();

        Preferences.getInstance().init(this, "UserProfile");

        if (presenter == null) {
            presenter = new NotificacionesRutaPresenter(this);
            presenter.init();
        }
    }

    @Override
    protected void animOnLeaveActivity() {
        overridePendingTransition(R.anim.not_slide, R.anim.slide_exit_out_bottom);
    }

    @Override
    public void displayNotificaciones(ArrayList<NotificacionRutaItem> items) {
        NotificacionesRutaAdapter adapter = new NotificacionesRutaAdapter(this, items);
        adapter.setListener(this);
        binding.rvNotificaciones.setAdapter(adapter);
    }

    @Override
    public void notifyItemChanged(int position) {
        binding.rvNotificaciones.getAdapter().notifyItemChanged(position);
    }

    @Override
    public void navigateToDetalleRutaActivity(Ruta ruta) {
        Bundle args = new Bundle();
        args.putSerializable("guias", ruta);
        args.putInt("numVecesGestionado", ruta.getResultadoGestion() == 0 ? 1 : 2);
        startActivity(new Intent(this, DetalleRutaRuralActivity.class).putExtra("args", args));
    }

    @Override
    public void setVisibilitySwipeRefreshLayout(boolean visible) {
        binding.swipeRefreshLayout.setRefreshing(visible);
    }

    @Override
    public void onClickIcon(View view, int position) {

    }

    @Override
    public void onClickItem(View view, int position) {
        presenter.onClickItem(position);
    }

    private void setupViews() {
        setupToolbar(binding.toolbar);
        try {
            setScreenTitle(getIntent().getExtras().getString("module_name"));
        } catch (NullPointerException ex) {
            setScreenTitle(R.string.title_activity_notificaciones_ruta);
        }

        binding.rvNotificaciones.setLayoutManager(new LinearLayoutManager(this));
        binding.rvNotificaciones.setHasFixedSize(true);

        binding.swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorBlackUrbano);
        binding.swipeRefreshLayout.setOnRefreshListener(() -> presenter.onSwipeRefresh());
    }

}
