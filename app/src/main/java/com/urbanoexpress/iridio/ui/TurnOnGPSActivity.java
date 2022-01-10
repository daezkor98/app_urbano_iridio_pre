package com.urbanoexpress.iridio.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.api.ResolvableApiException;
import com.urbanoexpress.iridio.R;
import com.urbanoexpress.iridio.databinding.ActivityTurnOnGpsBinding;
import com.urbanoexpress.iridio.ui.helpers.ModalHelper;
import com.urbanoexpress.iridio.util.LocationUtils;

public class TurnOnGPSActivity extends AppCompatActivity {

    private ActivityTurnOnGpsBinding binding;
    private Exception exception;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTurnOnGpsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        LocationUtils.validateSwitchedOnGPS(this,
                new LocationUtils.OnSwitchedOnGPSListener() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onFailure(Exception ex) {
                        exception = ex;
                    }
                });

        setupViews();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LocationUtils.REQUEST_CHECK_GPS_SETTINGS) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    finish();
                    break;
                case Activity.RESULT_CANCELED:
                    ModalHelper.getBuilderAlertDialog(this)
                            .setTitle(R.string.text_alert_enciende_gps)
                            .setMessage(R.string.text_msg_enciende_gps)
                            .setPositiveButton(R.string.text_aceptar, (dialog, which) -> dialog.dismiss())
                            .setIcon(R.mipmap.ic_launcher)
                            .show();
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() { }

    private void setupViews() {
        binding.turnOnGpsButton.setOnClickListener(v -> {
            try {
                ResolvableApiException resolvable = (ResolvableApiException) exception;
                resolvable.startResolutionForResult(this, LocationUtils.REQUEST_CHECK_GPS_SETTINGS);
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
