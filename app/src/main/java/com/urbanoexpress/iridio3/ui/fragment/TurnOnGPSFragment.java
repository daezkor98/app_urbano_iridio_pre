package com.urbanoexpress.iridio3.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.api.ResolvableApiException;
import com.urbanoexpress.iridio3.R;
import com.urbanoexpress.iridio3.databinding.ActivityTurnOnGpsBinding;
import com.urbanoexpress.iridio3.ui.helpers.ModalHelper;
import com.urbanoexpress.iridio3.util.CommonUtils;
import com.urbanoexpress.iridio3.util.LocationUtils;

/**
 * Created by mick on 17/07/17.
 */

public class TurnOnGPSFragment extends Fragment {

    public static final String TAG = "TurnOnGPSFragment";

    private ActivityTurnOnGpsBinding binding;

    private Exception exception;

    public TurnOnGPSFragment() {}

    public static TurnOnGPSFragment newInstance() {
        return new TurnOnGPSFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocationUtils.validateSwitchedOnGPS(getActivity(), new LocationUtils.OnSwitchedOnGPSListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(Exception ex) {
                exception = ex;
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        getActivity().getWindow().setBackgroundDrawable(
                ContextCompat.getDrawable(getActivity(), R.drawable.bg_red));
        CommonUtils.changeColorStatusBar(getActivity(), R.color.statusBarColor);
        binding = ActivityTurnOnGpsBinding.inflate(inflater, container, false);
        setupViews();
        return binding.getRoot();
    }

    public void activityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LocationUtils.REQUEST_CHECK_GPS_SETTINGS) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    Fragment fragment = LogInFragment.newInstance();
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, fragment, LogInFragment.TAG).commit();
                    break;
                case Activity.RESULT_CANCELED:
                    ModalHelper.getBuilderAlertDialog(getActivity())
                            .setTitle(R.string.text_alert_enciende_gps)
                            .setMessage(R.string.text_msg_enciende_gps)
                            .setPositiveButton(R.string.text_aceptar, (dialog, which) -> dialog.dismiss())
                            .setIcon(R.mipmap.ic_launcher)
                            .show();
                    break;
            }
        }
    }

    private void setupViews() {
        binding.turnOnGpsButton.setOnClickListener(v -> {
            try {
                ResolvableApiException resolvable = (ResolvableApiException) exception;
                resolvable.startResolutionForResult(
                        getActivity(), LocationUtils.REQUEST_CHECK_GPS_SETTINGS);
            } catch (IntentSender.SendIntentException ex) {
                ex.printStackTrace();
            } catch (ClassCastException ex) {
                ex.printStackTrace();
            } catch (NullPointerException ex) {
                ex.printStackTrace();
            }
        });
    }

}
