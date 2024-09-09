package com.urbanoexpress.iridio3.pe.presenter;

import androidx.appcompat.app.AppCompatActivity;

import com.urbanoexpress.iridio3.pe.util.LocationUtils;
import com.urbanoexpress.iridio3.pe.util.PermissionUtils;
import com.urbanoexpress.iridio3.pe.util.Preferences;
import com.urbanoexpress.iridio3.pe.view.InitView;

/**
 * Created by mick on 24/08/16.
 */

public class InitPresenter {

    private final InitView view;

    public InitPresenter(InitView view) {
        this.view = view;
        init();
    }

    private void init() {
        Preferences.getInstance().init(view.getViewContext(), "GlobalConfigApp");

        verifyConfigApp();
    }

    private void verifyConfigApp() {
        if (Preferences.getInstance().getInt("country", -1) < 0) {
            view.navigateToWelcomeFragment();
            return;
        }

        if (Preferences.getInstance().getString("phone", "").length() <= 0) {
            view.navigateToConfigPhoneFragment();
            return;
        }

        if (!PermissionUtils.checkBasicPermissions(view.getViewContext())) {
            view.navigateToRequestPermissionFragment();
            return;
        }

        if (!PermissionUtils.checkBackgroundLocationPermission(view.getViewContext())) {
            view.navigateToRequestLocationPermissionFragment();
            return;
        }

        LocationUtils.validateSwitchedOnGPS(((AppCompatActivity) view.getViewContext()),
                new LocationUtils.OnSwitchedOnGPSListener() {
            @Override
            public void onSuccess() {
                view.navigateToLogInFragment();
            }

            @Override
            public void onFailure(Exception ex) {
                view.navigateToTurnOnGPSFragment();
            }
        });
    }
}
