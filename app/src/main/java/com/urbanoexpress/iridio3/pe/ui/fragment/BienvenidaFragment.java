package com.urbanoexpress.iridio3.pe.ui.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.urbanoexpress.iridio3.pe.R;
import com.urbanoexpress.iridio3.pe.databinding.FragmentBienvenidaBinding;
import com.urbanoexpress.iridio3.pe.util.CommonUtils;

/**
 * Created by mick on 24/08/16.
 */

public class BienvenidaFragment extends Fragment {

    public static final String TAG = BienvenidaFragment.class.getSimpleName();

    private FragmentBienvenidaBinding binding;

    public static BienvenidaFragment newInstance() {
        return new BienvenidaFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        getActivity().getWindow().setBackgroundDrawable(
                ContextCompat.getDrawable(getActivity(), R.drawable.bg_fragment_bienvenida));
        CommonUtils.changeColorStatusBar(getActivity(), R.color.statusBarColor);
        binding = FragmentBienvenidaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews();
    }

    private void setupViews() {

        binding.btnSiguiente.setOnClickListener(v -> {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            ConfigPhoneFragment fragment = ConfigPhoneFragment.newInstance();
            fragmentManager.beginTransaction().replace(R.id.container, fragment,
                    ConfigPhoneFragment.TAG).commit();
        });
    }
}
