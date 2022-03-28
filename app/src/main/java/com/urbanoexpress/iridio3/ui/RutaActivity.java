package com.urbanoexpress.iridio3.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import com.google.android.material.appbar.AppBarLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.urbanoexpress.iridio3.R;
import com.urbanoexpress.iridio3.databinding.ActivityRutaBinding;
import com.urbanoexpress.iridio3.presenter.QRScannerPresenter;
import com.urbanoexpress.iridio3.presenter.RutaPresenter;
import com.urbanoexpress.iridio3.ui.adapter.ViewPagerAdapter;
import com.urbanoexpress.iridio3.ui.fragment.RutaGestionadaFragment;
import com.urbanoexpress.iridio3.ui.fragment.RutaPendienteFragment;
import com.urbanoexpress.iridio3.ui.helpers.ModalHelper;
import com.urbanoexpress.iridio3.ui.interfaces.OnActionModeListener;
import com.urbanoexpress.iridio3.util.AnimationUtils;
import com.urbanoexpress.iridio3.util.CommonUtils;
import com.urbanoexpress.iridio3.util.LocationUtils;
import com.urbanoexpress.iridio3.util.PermissionUtils;
import com.urbanoexpress.iridio3.util.Preferences;
import com.urbanoexpress.iridio3.util.components.searchview.MaterialSearchView;
import com.urbanoexpress.iridio3.util.constant.LocalAction;
import com.urbanoexpress.iridio3.view.RutaView;

public class RutaActivity extends BaseActivity implements RutaView, OnActionModeListener {

    private final String TAG = RutaActivity.class.getSimpleName();

    private ActivityRutaBinding binding;
    private RutaPresenter presenter;
    private Menu menu;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRutaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initUI();

        if (savedInstanceState != null) {
            Preferences.getInstance().init(this, "UserProfile");
        }

        if (presenter == null) {
            Preferences.getInstance().init(RutaActivity.this, "UserProfile");
            presenter = new RutaPresenter(this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.menu_ruta_del_dia, menu);
        this.menu = menu;
        MenuItem menuItemBuscarGE = menu.findItem(R.id.action_buscar);
        binding.searchView.setMenuItem(menuItemBuscarGE);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.not_slide, R.anim.slide_exit_out_bottom);
            return true;
        } else if (item.getItemId() == R.id.action_info_ruta_del_dia) {
            presenter.onActionInfoRutaDelDia();
            return true;
        } else if (item.getItemId() == R.id.action_manifestar_guia) {
            presenter.onActionManifestarGuia();
            return true;
        } else if (item.getItemId() == R.id.action_recolectar_valija) {
            presenter.onActionRecolectarValija();
            return true;
        } else if (item.getItemId() == R.id.action_consideraciones_importantes) {
            presenter.onActionConsideracionesImportantes();
            return true;
        } else if (item.getItemId() == R.id.action_mapa_de_ruta) {
            presenter.onActionMapaRutaDelDia();
            return true;
        } else if (item.getItemId() == R.id.action_eliminar_manifiesto) {
            presenter.onActionEliminarManifiesto();
            return true;
        } else if (item.getItemId() == R.id.action_transferir_guias) {
            presenter.onActionTransferirGuias();
            return true;
        } else if (item.getItemId() == R.id.action_editar_placa) {
            presenter.onActionEditarPlaca();
            return true;
        } else if (item.getItemId() == R.id.action_asignar_ruta) {
            presenter.onActionAsignarRuta();
            return true;
        } else if (item.getItemId() == R.id.action_codigo_qr_ruta) {
            presenter.onActionCodigoQRRuta();
            return true;
        } else if (item.getItemId() == R.id.action_forzar_terminar_ruta) {
            presenter.onActionForzarCierreRuta();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!PermissionUtils.checkAppPermissions(this)) {
            startActivity(new Intent(this, RequestPermissionActivity.class));
            overridePendingTransition(R.anim.slide_enter_from_bottom, R.anim.not_slide);
        }

        LocationUtils.validateSwitchedOnGPS(RutaActivity.this, new LocationUtils.OnSwitchedOnGPSListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(Exception ex) {
                startActivity(new Intent(RutaActivity.this, TurnOnGPSActivity.class));
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissProgressDialog();
        presenter.onDestroyActivity();
    }

    // Listeners from RutaPendienteFragment
    @Override
    public void onShowActionMode() {
        binding.fabIniciarTerminarRuta.hide();
        binding.fabIniciarTerminarRuta.setTag("disable");
        LinearLayout tabStrip = ((LinearLayout) binding.tabLayout.getChildAt(0));
        for(int i = 0; i < tabStrip.getChildCount(); i++) {
            tabStrip.getChildAt(i).setOnTouchListener((v, event) -> true);
        }
        binding.viewPager.setPagingEnabled(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(RutaActivity.this, R.color.gris_7));
        }
        binding.tabLayout.setBackgroundColor(ContextCompat.getColor(RutaActivity.this, R.color.gris));
    }

    @Override
    public void onCloseActionMode() {
        binding.fabIniciarTerminarRuta.show();
        binding.fabIniciarTerminarRuta.setTag("");
        LinearLayout tabStrip = ((LinearLayout) binding.tabLayout.getChildAt(0));
        for(int i = 0; i < tabStrip.getChildCount(); i++) {
            tabStrip.getChildAt(i).setOnTouchListener((v, event) -> false);
        }
        binding.viewPager.setPagingEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getColorDefaultMaterialActivity()[1]);
        }
        binding.tabLayout.setBackgroundColor(getColorDefaultMaterialActivity()[0]);
    }

    @Override
    public void setVisibilityBoxConsideracionesImportantesRuta(int visibility) {
        binding.boxConsideracionesImportantesRuta.setVisibility(visibility);
    }

    @Override
    public void setVisibilityBoxRutaNoIniciada(int visibility) {
        binding.boxAlertRutaNoIniciada.setVisibility(visibility);
    }

    @Override
    public void setMsgBoxRutaNoIniciada(String msg) {
        binding.lblMsgRuta.setText(msg);
    }

    @Override
    public void showMessageIniciarRuta() {
        ModalHelper.getBuilderAlertDialog(this)
                .setTitle(R.string.activity_detalle_ruta_title_ruta_no_iniciada)
                .setMessage("Lo sentimos, la ruta aún no ha sido iniciada.\n\nDebe iniciar la ruta antes de realizar recolección.")
                .setPositiveButton(R.string.text_aceptar, null)
                .show();
    }

    @Override
    public void navigateToRecolectarValijaActivity() {
        startActivity(new Intent(this, RecolectarValijaActivity.class));
        overridePendingTransition(R.anim.slide_enter_from_right, R.anim.not_slide);
    }

    public Toolbar getToolbar() {
        return binding.toolbar;
    }

    public void showSnackBarEstadoRuta() {
        presenter.loadConfigUIEstadoRuta();
    }

    public void setTitleTabVisitados(String title) {
        binding.tabLayout.getTabAt(1).setText(title);
    }

    private void initUI() {
        setupToolbar(binding.toolbar);
        try {
            setScreenTitle(getIntent().getExtras().getString("module_name"));
        } catch (NullPointerException ex) {
            setScreenTitle(R.string.title_activity_ruta);
        }

        binding.boxConsideracionesImportantesRuta.setOnClickListener(v ->
                presenter.onClickBoxConsideracionesImportantesRuta());

        binding.fabIniciarTerminarRuta.setOnClickListener(v -> presenter.onClickFab());

        setupViewPager(binding.viewPager);
        binding.tabLayout.setupWithViewPager(binding.viewPager);

        binding.searchView.setVoiceSearch(false);

        binding.searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                binding.searchView.closeSearch();
                Intent newIntent = new Intent(LocalAction.BUSCAR_GUIA_ACTION);
                newIntent.putExtra("tipoBusqueda", binding.tabLayout.getSelectedTabPosition());
                newIntent.putExtra("value", query);
                LocalBroadcastManager.getInstance(RutaActivity.this).sendBroadcast(newIntent);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        binding.searchView.setOnSearchViewActionListener(() -> {
            Intent intent = new Intent(RutaActivity.this, QRScannerActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("typeImpl", QRScannerPresenter.IMPLEMENT.READ_ONLY);
            intent.putExtra("args", bundle);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_enter_from_bottom, R.anim.not_slide);
        });

        binding.searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                binding.fabIniciarTerminarRuta.hide();
                AppBarLayout appBarLayout = findViewById(R.id.appBarLayout);
                appBarLayout.setExpanded(false, true);
                /*Log.d("MainAcitivity", "Z INDEX appBarLayout: " + appBarLayout.getZ());
                Log.d("MainAcitivity", "Z INDEX toolbar: " + toolbar.getZ());
                Log.d("MainAcitivity", "Z INDEX tabLayout: " + tabLayout.getZ());
                Log.d("MainAcitivity", "Z INDEX searchView: " + searchView.getZ());*/
            }

            @Override
            public void onSearchViewClosed() {
                binding.fabIniciarTerminarRuta.show();
            }
        });

        if (CommonUtils.isAndroidLollipop()) {
            binding.searchView.setZ(10);
        }

        new Handler().postDelayed(() -> binding.fabIniciarTerminarRuta.show(), 500);
    }

    public void animateActionBar(int position) {
        AnimationUtils.AnimateActionBar animateActionBar
                = new AnimationUtils.AnimateActionBar(RutaActivity.this);

        int colorViewsFrom = ContextCompat.getColor(RutaActivity.this, R.color.colorBlackUrbano);
        int colorViewsTo = ContextCompat.getColor(RutaActivity.this, R.color.colorPrimary);

        int colorStatusBarFrom = ContextCompat.getColor(RutaActivity.this, R.color.colorBlackDarkUrbano);
        int colorStatusBarTo = ContextCompat.getColor(RutaActivity.this, R.color.colorPrimaryDark);

        animateActionBar.setDuration(500);
        animateActionBar.animationColorViews(colorViewsFrom, colorViewsTo, binding.toolbar, binding.tabLayout);
        animateActionBar.animationColorStatusBar(colorStatusBarFrom, colorStatusBarTo);

        switch (position) {
            case 1:
                colorViewsFrom = ContextCompat.getColor(RutaActivity.this, R.color.colorPrimary);
                colorViewsTo = ContextCompat.getColor(RutaActivity.this, R.color.colorBlackUrbano);

                colorStatusBarFrom = ContextCompat.getColor(RutaActivity.this, R.color.colorPrimaryDark);
                colorStatusBarTo = ContextCompat.getColor(RutaActivity.this, R.color.colorBlackDarkUrbano);

                animateActionBar.animationColorViews(colorViewsFrom, colorViewsTo, binding.toolbar, binding.tabLayout);
                animateActionBar.animationColorStatusBar(colorStatusBarFrom, colorStatusBarTo);
                break;
        }

        animateActionBar.start();
    }

    private void setupViewPager(ViewPager viewPager) {
        final ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new RutaPendienteFragment(), "Pendientes");
        adapter.addFragment(new RutaGestionadaFragment(), "Gestionados");
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    CommonUtils.setVisibilityOptionMenu(
                            menu, R.id.action_eliminar_manifiesto, true);
                    binding.fabIniciarTerminarRuta.show();
                    binding.fabIniciarTerminarRuta.setTag("");
                } else {
                    CommonUtils.setVisibilityOptionMenu(
                            menu, R.id.action_eliminar_manifiesto, false);
                    binding.fabIniciarTerminarRuta.hide();
                    binding.fabIniciarTerminarRuta.setTag("disable");
                }
                animateActionBar(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private int[] getColorDefaultMaterialActivity() {
        Log.d(TAG, "getSelectedTabPosition: " + binding.tabLayout.getSelectedTabPosition());
        switch (binding.tabLayout.getSelectedTabPosition()) {
            case 0:
                return new int[]{
                        ContextCompat.getColor(RutaActivity.this, R.color.colorPrimary),
                        ContextCompat.getColor(RutaActivity.this, R.color.colorPrimaryDark)
                    };
            case 1:
                return new int[]{
                        ContextCompat.getColor(RutaActivity.this, R.color.colorBlackUrbano),
                        ContextCompat.getColor(RutaActivity.this, R.color.colorBlackDarkUrbano)
                };
        }
        return new int[]{-1, -1};
    }

    @Override
    public void onBackPressed() {
        if (binding.searchView.isSearchOpen()) {
            binding.searchView.closeSearch();
        } else {
            super.onBackPressed();
            overridePendingTransition(R.anim.not_slide, R.anim.slide_exit_out_bottom);
        }
    }

    @Override
    public Context getContextView() {
        return this;
    }

    @Override
    public View baseFindViewById(int id) {
        return findViewById(id);
    }
}
