package com.urbanoexpress.iridio3.pe.ui;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.google.android.material.appbar.AppBarLayout;
import com.urbanoexpress.iridio3.R;
import com.urbanoexpress.iridio3.databinding.ActivityRutaRuralBinding;
import com.urbanoexpress.iridio3.pe.presenter.QRScannerPresenter;
import com.urbanoexpress.iridio3.pe.presenter.RutaRuralPresenter;
import com.urbanoexpress.iridio3.pe.ui.fragment.RutaRuralPendienteFragment;
import com.urbanoexpress.iridio3.pe.ui.fragment.RutaRuralVisitadoFragment;
import com.urbanoexpress.iridio3.pe.ui.interfaces.OnActionModeListener;
import com.urbanoexpress.iridio3.pe.ui.adapter.ViewPagerAdapter;
import com.urbanoexpress.iridio3.pe.util.AnimationUtils;
import com.urbanoexpress.iridio3.pe.util.CommonUtils;
import com.urbanoexpress.iridio3.pe.util.PermissionUtils;
import com.urbanoexpress.iridio3.pe.util.Preferences;
import com.urbanoexpress.iridio3.pe.util.components.searchview.MaterialSearchView;
import com.urbanoexpress.iridio3.pe.util.constant.LocalAction;
import com.urbanoexpress.iridio3.pe.view.RutaRuralView;

public class RutaRuralActivity extends BaseActivity implements RutaRuralView,
        OnActionModeListener {

    private final String TAG = RutaRuralActivity.class.getSimpleName();

    private ActivityRutaRuralBinding binding;
    private RutaRuralPresenter presenter;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRutaRuralBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initUI();

        if (savedInstanceState != null) {
            Preferences.getInstance().init(this, "UserProfile");
        }

        if (presenter == null) {
            Preferences.getInstance().init(this, "UserProfile");
            presenter = new RutaRuralPresenter(this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_ruta_rural, menu);
        this.menu = menu;
        MenuItem menuItemBuscarGE = menu.findItem(R.id.action_buscar);
        binding.searchView.setMenuItem(menuItemBuscarGE);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.not_slide, R.anim.slide_exit_out_bottom);
                return true;
            case R.id.action_info_ruta_del_dia:
                presenter.onActionInfoRutaDelDia();
                return true;
            case R.id.action_consideraciones_importantes:
                presenter.onActionConsideracionesImportantes();
                return true;
            case R.id.action_mapa_de_ruta:
                presenter.onActionMapaRutaDelDia();
                return true;
            case R.id.action_eliminar_manifiesto:
                presenter.onActionEliminarManifiesto();
                return true;
            case R.id.action_transferir_guias:
                presenter.onActionTransferirGuias();
                return true;
            case R.id.action_asignar_ruta:
                presenter.onActionAsignarRuta();
                return true;
            case R.id.action_codigo_qr_ruta:
                presenter.onActionCodigoQRRuta();
                return true;
            default:
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissProgressDialog();
        presenter.onDestroyActivity();
    }

    @Override
    public int getSelectedTabPosition() {
        return binding.tabLayout.getSelectedTabPosition();
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
    public void showDialogDescargarMotivosGestionLlamada() {

    }

    @Override
    public void onShowActionMode() {
        LinearLayout tabStrip = ((LinearLayout) binding.tabLayout.getChildAt(0));
        for (int i = 0; i < tabStrip.getChildCount(); i++) {
            tabStrip.getChildAt(i).setOnTouchListener((v, event) -> true);
        }
        binding.viewPager.setPagingEnabled(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(RutaRuralActivity.this, R.color.gris_7));
        }
        binding.tabLayout.setBackgroundColor(ContextCompat.getColor(RutaRuralActivity.this, R.color.gris));
    }

    @Override
    public void onCloseActionMode() {
        LinearLayout tabStrip = ((LinearLayout) binding.tabLayout.getChildAt(0));
        for (int i = 0; i < tabStrip.getChildCount(); i++) {
            tabStrip.getChildAt(i).setOnTouchListener((v, event) -> false);
        }
        binding.viewPager.setPagingEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getColorDefaultMaterialActivity()[1]);
        }
        binding.tabLayout.setBackgroundColor(getColorDefaultMaterialActivity()[0]);
    }

    public Toolbar getToolbar() {
        return binding.toolbar;
    }

    public void setTitleTabPendientes(String title) {
        binding.tabLayout.getTabAt(0).setText(title);
    }

    public void setTitleTabVisitados(String title) {
        binding.tabLayout.getTabAt(1).setText(title);
    }

    private void initUI() {
        setupToolbar(binding.toolbar);
        try {
            setScreenTitle(getIntent().getExtras().getString("module_name"));
        } catch (NullPointerException ex) {
            setScreenTitle(R.string.title_activity_ruta_rural);
        }

        binding.boxConsideracionesImportantesRuta.setOnClickListener(
                v -> presenter.onClickBoxConsideracionesImportantesRuta());

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
                LocalBroadcastManager.getInstance(RutaRuralActivity.this).sendBroadcast(newIntent);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        binding.searchView.setOnSearchViewActionListener(() -> {
            Intent intent = new Intent(RutaRuralActivity.this, QRScannerActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("typeImpl", QRScannerPresenter.IMPLEMENT.READ_ONLY);
            intent.putExtra("args", bundle);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_enter_from_bottom, R.anim.not_slide);
        });

        binding.searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                AppBarLayout appBarLayout = findViewById(R.id.appBarLayout);
                appBarLayout.setExpanded(false, true);
            }

            @Override
            public void onSearchViewClosed() {

            }
        });

        if (CommonUtils.isAndroidLollipop()) {
            binding.searchView.setZ(10);
        }
    }

    public void animateActionBar(int position) {
        AnimationUtils.AnimateActionBar animateActionBar
                = new AnimationUtils.AnimateActionBar(RutaRuralActivity.this);

        int colorViewsFrom = ContextCompat.getColor(RutaRuralActivity.this, R.color.colorBlackUrbano);
        int colorViewsTo = ContextCompat.getColor(RutaRuralActivity.this, R.color.colorPrimary);

        int colorStatusBarFrom = ContextCompat.getColor(RutaRuralActivity.this, R.color.colorBlackDarkUrbano);
        int colorStatusBarTo = ContextCompat.getColor(RutaRuralActivity.this, R.color.colorPrimaryDark);

        animateActionBar.setDuration(500);
        animateActionBar.animationColorViews(colorViewsFrom, colorViewsTo, binding.toolbar, binding.tabLayout);
        animateActionBar.animationColorStatusBar(colorStatusBarFrom, colorStatusBarTo);

        switch (position) {
            case 1:
                colorViewsFrom = ContextCompat.getColor(RutaRuralActivity.this, R.color.colorPrimary);
                colorViewsTo = ContextCompat.getColor(RutaRuralActivity.this, R.color.colorBlackUrbano);

                colorStatusBarFrom = ContextCompat.getColor(RutaRuralActivity.this, R.color.colorPrimaryDark);
                colorStatusBarTo = ContextCompat.getColor(RutaRuralActivity.this, R.color.colorBlackDarkUrbano);

                animateActionBar.animationColorViews(colorViewsFrom, colorViewsTo, binding.toolbar, binding.tabLayout);
                animateActionBar.animationColorStatusBar(colorStatusBarFrom, colorStatusBarTo);
                break;
        }

        animateActionBar.start();
    }

    private void setupViewPager(ViewPager viewPager) {
        final ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new RutaRuralPendienteFragment(), "Pendientes");
        adapter.addFragment(new RutaRuralVisitadoFragment(), "Visitados");
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
                } else {
                    CommonUtils.setVisibilityOptionMenu(
                            menu, R.id.action_eliminar_manifiesto, false);
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
                        ContextCompat.getColor(RutaRuralActivity.this, R.color.colorPrimary),
                        ContextCompat.getColor(RutaRuralActivity.this, R.color.colorPrimaryDark)
                };
            case 1:
                return new int[]{
                        ContextCompat.getColor(RutaRuralActivity.this, R.color.colorBlackUrbano),
                        ContextCompat.getColor(RutaRuralActivity.this, R.color.colorBlackDarkUrbano)
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
}