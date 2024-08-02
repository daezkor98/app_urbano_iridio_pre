package com.urbanoexpress.iridio3.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.urbanoexpress.iridio3.R;
import com.urbanoexpress.iridio3.databinding.ActivityMainBinding;
import com.urbanoexpress.iridio3.model.NavigationMenuModel;
import com.urbanoexpress.iridio3.presenter.NavigationMenuPresenter;
import com.urbanoexpress.iridio3.presenter.NotificacionesRutaPresenter;
import com.urbanoexpress.iridio3.services.DataSyncService;
import com.urbanoexpress.iridio3.ui.adapter.MainMenuAdapter;
import com.urbanoexpress.iridio3.ui.dialogs.EncuestaTipoUsuarioDialog;
import com.urbanoexpress.iridio3.ui.dialogs.LogoutDialog;
import com.urbanoexpress.iridio3.ui.helpers.ModalHelper;
import com.urbanoexpress.iridio3.ui.interfaces.OnClickItemListener;
import com.urbanoexpress.iridio3.urbanocore.values.Val;
import com.urbanoexpress.iridio3.util.DateSystemHelper;
import com.urbanoexpress.iridio3.util.InfoDevice;
import com.urbanoexpress.iridio3.util.LocationUtils;
import com.urbanoexpress.iridio3.util.NotificationUtils;
import com.urbanoexpress.iridio3.util.PermissionUtils;
import com.urbanoexpress.iridio3.util.Preferences;
import com.urbanoexpress.iridio3.util.constant.LocalAction;
import com.urbanoexpress.iridio3.view.NavigationMenuView;
import com.urbanoexpress.iridio3.work.UserStatusWorker;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppThemeBaseActivity implements NavigationView.OnNavigationItemSelectedListener,
        NavigationMenuView, OnClickItemListener {

    private ActivityMainBinding binding;
    private NavigationMenuPresenter presenter;
    private View headerMenuView;
    private int positionItemClicked = -1;
    private Intent serviceDataSync;

    private boolean validateFeatures = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupViews();

        LocalBroadcastManager.getInstance(this)
                .registerReceiver(refrescarContadorNotificacionesReceiver,
                        new IntentFilter(LocalAction.REFRESCAR_CONTADOR_NOTIFICACIONES_ACTION));

        if (savedInstanceState != null) {
            Preferences.getInstance().init(this, "UserProfile");
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            new NotificationUtils(this).createChannels();
        }

        validateLicenciaMotorizado();
        launchValidatorWorker();

        if (presenter == null) {
            presenter = new NavigationMenuPresenter(this);
            presenter.init();
        }
    }

    private void launchValidatorWorker() {
        WorkManager workManager = WorkManager.getInstance(this);
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        PeriodicWorkRequest workRequest = new PeriodicWorkRequest
                .Builder(UserStatusWorker.class, 1, TimeUnit.DAYS)
                .addTag(UserStatusWorker.TAG)
                .setConstraints(constraints)
                .build();

        workManager.enqueueUniquePeriodicWork(
                UserStatusWorker.TAG, ExistingPeriodicWorkPolicy.KEEP, workRequest);

        new DateSystemHelper().validateDateSytem(this);
    }

    private void validateLicenciaMotorizado() {

        String mostrarEncuesta = Preferences.getInstance().getString("mostrarEncuesta", "");

        if (mostrarEncuesta.equals(Val.TRUE)) {
            EncuestaTipoUsuarioDialog
                    .newInstance()
                    .show(getSupportFragmentManager(), "EncuestaTipoUsua");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.view_user_profile) {
            startActivity(new Intent(this, UserProfileActivity.class));
            overridePendingTransition(R.anim.slide_enter_from_bottom, R.anim.not_slide);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        validateFeatures();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this)
                .unregisterReceiver(refrescarContadorNotificacionesReceiver);
    }

    @Override
    public boolean onBackButtonPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }
        return false;
    }

    @Override
    protected void animOnStartActivity() {
        overridePendingTransition(R.anim.slide_enter_from_bottom, R.anim.not_slide);
    }

    @Override
    protected void animOnLeaveActivity() {
        overridePendingTransition(R.anim.not_slide, R.anim.slide_exit_out_bottom);
    }

    private void setupViews() {
        setupToolbar(binding.toolbar);

        Glide.with(MainActivity.this)
                .load(R.drawable.logo_urbano_white)
                .dontAnimate()
                .into(binding.imgLogoUrbano);

        configImgCarUrbano();

        binding.rvMenu.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        binding.rvMenu.setItemAnimator(new DefaultItemAnimator());

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, binding.drawerLayout,
                binding.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (positionItemClicked >= 0) {
                    presenter.onItemSideMenuClick(positionItemClicked);
                    positionItemClicked = -1;
                }
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                binding.toolbar.setAlpha(1 - slideOffset / 2);
            }
        };

        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        headerMenuView = getLayoutInflater().inflate(R.layout.navigation_header_main, null);
        binding.navigationView.addHeaderView(headerMenuView);

        Glide.with(MainActivity.this)
                .load(R.drawable.bg_header_navigation_new)
                .centerCrop()
                .into((ImageView) headerMenuView.findViewById(R.id.imgBGHeader));

        binding.navigationView.setNavigationItemSelectedListener(this);

        headerMenuView.findViewById(R.id.nav_header_container).setOnClickListener(v -> {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            positionItemClicked = 50;
        });
    }

    private void configImgCarUrbano() {
        binding.boxImgCarUrbano.setVisibility(View.VISIBLE);
        Glide.with(this)
                .load(R.drawable.bg_bottom_urbano_transport)
                .dontAnimate()
                .into(binding.imgCarUrbano);

        /*Log.d("MERRY", "INIT");
        try {
            Date initDateMerryChristmas = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse("01/12/2019 00:00:00");
            Date lastDateMerryChristmas = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse("31/12/2019 23:59:59");
            Date now = new Date();

            if (now.getTime() >= initDateMerryChristmas.getTime()
                    && now.getTime() <= lastDateMerryChristmas.getTime()) {
                Log.d("MERRY", "MERRY CHRISTMAS");
                boxImgCarUrbanoChristmas.setVisibility(View.VISIBLE);
                Glide.with(this)
                        .load(R.drawable.bg_bottom_urbano_transport_merry_christmas)
                        .dontAnimate()
                        .into(imgCarUrbanoChristmas);

                imgCarUrbanoChristmas.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        BaseModalsView.showToast(MainActivity.this,
                                "Que pases una Feliz Navidad y un Próspero Año Nuevo Jo Jo Jo...",
                                Toast.LENGTH_LONG);
                    }
                });
            } else {
                Log.d("MERRY", "MERRY CHRISTMAS FINISH");
                boxImgCarUrbano.setVisibility(View.VISIBLE);
                Glide.with(this)
                        .load(R.drawable.bg_bottom_urbano_transport)
                        .dontAnimate()
                        .into(imgCarUrbano);
            }
        } catch (ParseException ex) {
            ex.printStackTrace();
            Log.d("MERRY", "MERRY CHRISTMAS FAIL");
            boxImgCarUrbano.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(R.drawable.bg_bottom_urbano_transport)
                    .dontAnimate()
                    .into(imgCarUrbano);
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.d("MERRY", "MERRY CHRISTMAS ERROR FATAL");
            boxImgCarUrbano.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(R.drawable.bg_bottom_urbano_transport)
                    .dontAnimate()
                    .into(imgCarUrbano);
        }*/
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        if (item.isCheckable()) item.setChecked(true);
        binding.drawerLayout.closeDrawer(GravityCompat.START);
        positionItemClicked = item.getItemId();

        return true;
    }

    @Override
    public void setDrawerHeader(String typeUser, String userName) {
        ((TextView) headerMenuView.findViewById(R.id.txtTipoUsuario)).setText(typeUser);
        ((TextView) headerMenuView.findViewById(R.id.txtNomApeUsuario)).setText(userName);
    }

    @Override
    public void showMainMenu(List<NavigationMenuModel> menuItems) {

//        Log.i("TAG", "showMainMenu: " + new Gson().toJson(menuItems));
        Log.i("TAG", "showMainMenu: " + menuItems.toString());

        MainMenuAdapter adapter = new MainMenuAdapter(this, menuItems);
        adapter.setListener(MainActivity.this);

        binding.rvMenu.setAdapter(adapter);
    }

    @Override
    public void notifyMainMenuItemChanged(int position) {
        binding.rvMenu.getAdapter().notifyItemChanged(position);
    }

    @Override
    public void initializeServices() {
        if (!InfoDevice.isServiceRunning(this, DataSyncService.class)) {
            if (serviceDataSync == null) {
                serviceDataSync = new Intent(MainActivity.this, DataSyncService.class);
                ContextCompat.startForegroundService(MainActivity.this, serviceDataSync);
            }
        } else {
            Log.d("ACTIVITY", "HAY SERVICIO DE SINCRONIZACION");
        }
    }

    @Override
    public void validateFeatures() {
        if (validateFeatures) {
            Log.d("Main Activity", "VALIDATE SERVICES");
            if (!PermissionUtils.checkAppPermissions(this)) {
                startActivity(new Intent(this, RequestPermissionActivity.class));
                overridePendingTransition(R.anim.slide_enter_from_bottom, R.anim.not_slide);
            }

            LocationUtils.validateSwitchedOnGPS(MainActivity.this, new LocationUtils.OnSwitchedOnGPSListener() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onFailure(Exception ex) {
                    startActivity(new Intent(MainActivity.this, TurnOnGPSActivity.class));
                }
            });
        }
    }

    public void setValidateFeatures(boolean validateFeatures) {
        this.validateFeatures = validateFeatures;
    }

    @Override
    public void addMenuSideMenu(int groupId, int itemId, int order, String title, int iconRes) {
        binding.navigationView.getMenu().add(groupId, itemId, order, title)
                .setIcon(iconRes)
                .setCheckable(false);
    }

    @Override
    public void navigateToMenu(Class<?> cls) {
        startActivity(new Intent(this, cls));
    }

    @Override
    public void navigateToMenu(Class<?> cls, String moduleName) {
        Intent intent = new Intent(this, cls);
        intent.putExtra("module_name", moduleName);
        startActivity(intent);
    }

    @Override
    public void showDialogLogout() {
        new LogoutDialog().show(getSupportFragmentManager(), LogoutDialog.TAG);
    }

    @Override
    public void showMessageMenuNotAvailible() {
        ModalHelper.getBuilderAlertDialog(this)
                .setTitle(R.string.nav_drawer_fragment_not_found_class_menu_title)
                .setMessage(R.string.nav_drawer_fragment_not_found_class_menu)
                .setIcon(R.mipmap.ic_launcher)
                .setPositiveButton(R.string.text_aceptar, null)
                .show();
    }

    @Override
    public void onClickIcon(View view, int position) {

    }

    @Override
    public void onClickItem(View view, int position) {
        presenter.onItemMenuClick(position);
    }

    /**
     * Broadcast
     * <p>
     * {@link NotificacionesRutaPresenter#handleDataNotificaciones}
     */
    private final BroadcastReceiver refrescarContadorNotificacionesReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, final Intent intent) {
            runOnUiThread(() -> presenter.updateBadgeTotalNotifications());
        }
    };

}