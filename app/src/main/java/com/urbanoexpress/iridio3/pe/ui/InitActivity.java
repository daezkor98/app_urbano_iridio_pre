package com.urbanoexpress.iridio3.pe.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.urbanoexpress.iridio3.pe.R;
import com.urbanoexpress.iridio3.pe.databinding.ActivityInitBinding;
import com.urbanoexpress.iridio3.pe.presenter.InitPresenter;
import com.urbanoexpress.iridio3.pe.ui.fragment.LogInFragment;
import com.urbanoexpress.iridio3.pe.ui.fragment.RequestLocationPermissionFragment;
import com.urbanoexpress.iridio3.pe.ui.fragment.RequestPermissionFragment;
import com.urbanoexpress.iridio3.pe.ui.fragment.BienvenidaFragment;
import com.urbanoexpress.iridio3.pe.ui.fragment.ConfigPhoneFragment;
import com.urbanoexpress.iridio3.pe.ui.fragment.TurnOnGPSFragment;
import com.urbanoexpress.iridio3.pe.view.InitView;

/**
 * Created by mick on 24/08/16.
 */

public class InitActivity extends BaseActivity implements InitView {

    private ActivityInitBinding binding;
    private InitPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInitBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (presenter == null) {
            presenter = new InitPresenter(this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

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
