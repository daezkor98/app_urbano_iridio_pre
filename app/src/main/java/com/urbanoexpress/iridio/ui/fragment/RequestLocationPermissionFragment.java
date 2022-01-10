package com.urbanoexpress.iridio.ui.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.urbanoexpress.iridio.R;
import com.urbanoexpress.iridio.databinding.FragmentRequestLocationPermissionBinding;
import com.urbanoexpress.iridio.ui.InitActivity;
import com.urbanoexpress.iridio.ui.RequestPermissionActivity;
import com.urbanoexpress.iridio.ui.helpers.ModalHelper;
import com.urbanoexpress.iridio.util.LocationUtils;
import com.urbanoexpress.iridio.util.PermissionUtils;

public class RequestLocationPermissionFragment extends Fragment {

    public static final String TAG = "RequestLocationPermissionFragment";

    private FragmentRequestLocationPermissionBinding binding;

    private boolean isSwitchedOnGPS = false;

    public RequestLocationPermissionFragment() { }

    public static RequestLocationPermissionFragment newInstance() {
        return new RequestLocationPermissionFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentRequestLocationPermissionBinding.inflate(inflater, container, false);
        setupViews();

        LocationUtils.validateSwitchedOnGPS(getActivity(), new LocationUtils.OnSwitchedOnGPSListener() {
            @Override
            public void onSuccess() {
                isSwitchedOnGPS = true;
            }

            @Override
            public void onFailure(Exception ex) {

            }
        });

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            if (PermissionUtils.checkBackgroundLocationPermission(getActivity())) {
                onLocationPermissionGranted();
            }
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    private void setupViews() {
        binding.btnOtorgarAcceso.setOnClickListener(v -> requestLocationPermission());
        binding.btnNoGracias.setOnClickListener(v -> getActivity().finish());
    }

    private void onLocationPermissionGranted() {
        if (getActivity() instanceof InitActivity) {
            getActivity().getSupportFragmentManager().beginTransaction().replace(
                    R.id.container, LogInFragment.newInstance(), LogInFragment.TAG)
                    .commit();

            if (isSwitchedOnGPS) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(
                        R.id.container, LogInFragment.newInstance(), LogInFragment.TAG)
                        .commit();
            } else {
                getActivity().getSupportFragmentManager().beginTransaction().replace(
                        R.id.container, new TurnOnGPSFragment(), TurnOnGPSFragment.TAG)
                        .commit();
            }
        } else if (getActivity() instanceof RequestPermissionActivity) {
            getActivity().finish();
        }
    }

    private boolean isEveryPermissionGranted(int[] grantResults) {
        for (int grantResult: grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void requestLocationPermission() {
        String[] permissions = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
                ? new String[]{
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION}
                : new String[]{
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION};
        requestPermissionsForResult.launch(permissions);
    }

    private void requestBackgroundLocationPermission() {
        requestPermissionsForResult.launch(new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION});
    }

    private final ActivityResultLauncher<String[]> requestPermissionsForResult = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(), result ->  {
                try {
                    if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                        ModalHelper.getBuilderAlertDialog(getActivity())
                                .setTitle(R.string.modal_title_requerimos_tu_permiso)
                                .setMessage(R.string.modal_msg_requerimos_permiso_ubicacion)
                                .setPositiveButton(R.string.text_permitir,
                                        (dialog, which) -> requestBackgroundLocationPermission())
                                .setNegativeButton(R.string.text_no_gracias,
                                        (dialog, which) -> getActivity().finish())
                                .show();
                    } else if (PermissionUtils.checkBackgroundLocationPermission(getActivity())) {
                        onLocationPermissionGranted();
                    } else {
                        ModalHelper.getBuilderAlertDialog(getActivity())
                                .setTitle(R.string.modal_title_requerimos_tu_permiso)
                                .setMessage(R.string.modal_msg_requerimos_permiso_ubicacion_denegado)
                                .setPositiveButton(R.string.text_configurar, (dialog, which) -> {
                                    Intent intent = new Intent();
                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                                    intent.setData(uri);
                                    startActivity(intent);
                                })
                                .setNegativeButton(R.string.text_no_gracias,
                                        (dialog, which) -> getActivity().finish())
                                .show();
                    }
                } catch (NullPointerException ex) {
                    ex.printStackTrace();
                }
            });
}