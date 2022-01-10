package com.urbanoexpress.iridio.ui.fragment;

import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.urbanoexpress.iridio.R;
import com.urbanoexpress.iridio.databinding.FragmentRecolectarValijaExpressCompleteBinding;
import com.urbanoexpress.iridio.model.RecolectarValijaExpressViewModel;

public class RVECompleteFragment extends BaseFragment {

    public static final String TAG = "RVECompleteFragment";

    private FragmentRecolectarValijaExpressCompleteBinding binding;
    private RecolectarValijaExpressViewModel model;

    public RVECompleteFragment() {}

    public static RVECompleteFragment newInstance() {
        return new RVECompleteFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentRecolectarValijaExpressCompleteBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        model = new ViewModelProvider(requireActivity()).get(RecolectarValijaExpressViewModel.class);
        setupViews();
    }

    private void setupViews() {
        binding.recoleccionText.setText(model.getBarraRecoleccion().getValue());
        binding.valijaText.setText(model.getBarraValija().getValue());
        binding.shipperText.setText(model.getShipperName().getValue());

        binding.newOperationButton.setOnClickListener(v -> model.newOperation());
        binding.closeButton.setOnClickListener(v -> finishActivity());

        new Handler().postDelayed(() -> {
            Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.avd_success,
                    getContext().getTheme());
            binding.successImage.setImageDrawable(drawable);
            if (drawable instanceof AnimatedVectorDrawable) {
                AnimatedVectorDrawable avd = (AnimatedVectorDrawable) drawable;
                avd.start();
            }
        }, 500);
    }
}