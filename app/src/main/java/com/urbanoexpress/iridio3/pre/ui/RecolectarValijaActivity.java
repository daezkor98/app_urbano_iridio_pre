package com.urbanoexpress.iridio3.pre.ui;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import com.urbanoexpress.iridio3.pre.R;
import com.urbanoexpress.iridio3.pre.databinding.ActivityRecolectarValijaBinding;
import com.urbanoexpress.iridio3.pre.model.RecolectarValijaExpressViewModel;
import com.urbanoexpress.iridio3.pre.ui.fragment.RVECompleteFragment;
import com.urbanoexpress.iridio3.pre.ui.fragment.RVEPhotoValijaFragment;
import com.urbanoexpress.iridio3.pre.ui.fragment.RVEScanBarcodeFragment;

public class RecolectarValijaActivity extends AppThemeBaseActivity {

    private ActivityRecolectarValijaBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRecolectarValijaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        final RecolectarValijaExpressViewModel model = new ViewModelProvider(this)
                .get(RecolectarValijaExpressViewModel.class);

        final Observer<RecolectarValijaExpressViewModel.Step> nextStep = step -> {
            switch (step) {
                case SCAN_BARCODE:
                    backFragment(RVEScanBarcodeFragment.newInstance(), RVEScanBarcodeFragment.TAG);
                    break;
                case TAKE_PHOTO:
                    nextFragment(RVEPhotoValijaFragment.newInstance(), RVEPhotoValijaFragment.TAG);
                    break;
                case COMPLETED:
                    nextFragment(RVECompleteFragment.newInstance(), RVECompleteFragment.TAG);
                    break;
            }
        };

        model.getNextStep().observe(this, nextStep);

        setupViews();
    }

    private void setupViews() {
        setupToolbar(binding.toolbar);
        setScreenTitle("Recolectar valija");
        addFragment(RVEScanBarcodeFragment.newInstance(), RVEScanBarcodeFragment.TAG);
    }

    private void addFragment(Fragment fragment, String tag) {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container_view, fragment, tag).commit();
    }

    private void backFragment(Fragment fragment, String tag) {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.fragment_slide_enter_from_left,
                        R.anim.fragment_slide_exit_out_right,
                        R.anim.fragment_slide_enter_from_right,
                        R.anim.fragment_slide_exit_out_left)
                .replace(R.id.fragment_container_view, fragment, tag).commit();
    }

    private void nextFragment(Fragment fragment, String tag) {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.fragment_slide_enter_from_right,
                        R.anim.fragment_slide_exit_out_left,
                        R.anim.fragment_slide_enter_from_left,
                        R.anim.fragment_slide_exit_out_right)
                .replace(R.id.fragment_container_view, fragment, tag).commit();
    }
}