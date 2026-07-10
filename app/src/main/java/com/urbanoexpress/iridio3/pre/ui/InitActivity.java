package com.urbanoexpress.iridio3.pre.ui;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.gms.tasks.Task;
import com.urbanoexpress.iridio3.pre.R;
import com.urbanoexpress.iridio3.pre.databinding.ActivityInitBinding;
import com.urbanoexpress.iridio3.pre.presenter.InitPresenter;
import com.urbanoexpress.iridio3.pre.ui.fragment.LogInFragment;
import com.urbanoexpress.iridio3.pre.ui.fragment.RequestLocationPermissionFragment;
import com.urbanoexpress.iridio3.pre.ui.fragment.RequestPermissionFragment;
import com.urbanoexpress.iridio3.pre.ui.fragment.BienvenidaFragment;
import com.urbanoexpress.iridio3.pre.ui.fragment.ConfigPhoneFragment;
import com.urbanoexpress.iridio3.pre.ui.fragment.TurnOnGPSFragment;
import com.urbanoexpress.iridio3.pre.view.InitView;

/**
 * Created by mick on 24/08/16.
 */

public class InitActivity extends BaseActivity implements InitView {

    private static final int REQUEST_CODE_UPDATE = 500;

    private ActivityInitBinding binding;
    private InitPresenter presenter;
    private AppUpdateManager appUpdateManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInitBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        try {
            appUpdateManager = AppUpdateManagerFactory.create(this);
        } catch (Exception e) {
            appUpdateManager = null;
        }
        verificarActualizacion();
    }

    private void verificarActualizacion() {
        if (appUpdateManager == null) {
            iniciarPresenter();
            return;
        }
        Task<AppUpdateInfo> infoTask = appUpdateManager.getAppUpdateInfo();
        infoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                try {
                    appUpdateManager.startUpdateFlowForResult(
                            appUpdateInfo, AppUpdateType.IMMEDIATE, this, REQUEST_CODE_UPDATE);
                } catch (Exception e) {
                    iniciarPresenter();
                }
            } else {
                iniciarPresenter();
            }
        });
        infoTask.addOnFailureListener(e -> iniciarPresenter());
    }

    private void iniciarPresenter() {
        if (presenter == null) {
            presenter = new InitPresenter(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (appUpdateManager != null) {
            appUpdateManager.getAppUpdateInfo().addOnSuccessListener(appUpdateInfo -> {
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                    try {
                        appUpdateManager.startUpdateFlowForResult(
                                appUpdateInfo, AppUpdateType.IMMEDIATE, this, REQUEST_CODE_UPDATE);
                    } catch (Exception ignored) {}
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_UPDATE && resultCode != RESULT_OK) {
            verificarActualizacion();
            return;
        }

        Fragment fragment = getSupportFragmentManager().findFragmentByTag(TurnOnGPSFragment.TAG);
        if (fragment instanceof TurnOnGPSFragment) {
            ((TurnOnGPSFragment) fragment).activityResult(requestCode, resultCode, data);
        }

        fragment = getSupportFragmentManager().findFragmentByTag(ConfigPhoneFragment.TAG);
        if (fragment instanceof ConfigPhoneFragment) {
            ((ConfigPhoneFragment) fragment).activityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void navigateToWelcomeFragment() {
        getSupportFragmentManager().beginTransaction().add(R.id.container,
                BienvenidaFragment.newInstance(), BienvenidaFragment.TAG).commit();
    }

    @Override
    public void navigateToConfigPhoneFragment() {
        getSupportFragmentManager().beginTransaction().add(R.id.container,
                ConfigPhoneFragment.newInstance(), ConfigPhoneFragment.TAG).commit();
    }

    @Override
    public void navigateToRequestPermissionFragment() {
        getSupportFragmentManager().beginTransaction().add(R.id.container,
                RequestPermissionFragment.newInstance(), RequestPermissionFragment.TAG).commit();
    }

    @Override
    public void navigateToRequestLocationPermissionFragment() {
        getSupportFragmentManager().beginTransaction().add(R.id.container,
                RequestLocationPermissionFragment.newInstance(),
                RequestLocationPermissionFragment.TAG).commit();
    }

    @Override
    public void navigateToLogInFragment() {
        getSupportFragmentManager().beginTransaction().add(R.id.container,
                LogInFragment.newInstance(), LogInFragment.TAG).commit();
    }

    @Override
    public void navigateToTurnOnGPSFragment() {
        getSupportFragmentManager().beginTransaction().add(R.id.container,
                TurnOnGPSFragment.newInstance(), TurnOnGPSFragment.TAG).commit();
    }
}
