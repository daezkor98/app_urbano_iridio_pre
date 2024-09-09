package com.urbanoexpress.iridio3.pe.ui;

import androidx.annotation.Nullable;

import android.os.Bundle;

import com.urbanoexpress.iridio3.pe.R;
import com.urbanoexpress.iridio3.pe.databinding.ActivityAcercaDeBinding;
import com.urbanoexpress.iridio3.pe.util.CommonUtils;

public class AcercaDeActivity extends AppThemeBaseActivity {

    private ActivityAcercaDeBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAcercaDeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        CommonUtils.changeColorStatusBar(this, R.color.darkDisabledText);
        setupViews();
    }

    @Override
    protected void animOnLeaveActivity() {
        overridePendingTransition(R.anim.not_slide, R.anim.slide_exit_out_bottom);
    }

    private void setupViews() {
        try {
            binding.lblVersionApp.setText(getString(R.string.text_version) + " " +
                    CommonUtils.getPackageInfo(AcercaDeActivity.this).versionName);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

}
