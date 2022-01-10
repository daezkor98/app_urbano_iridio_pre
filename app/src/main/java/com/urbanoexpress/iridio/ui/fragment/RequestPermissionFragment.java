package com.urbanoexpress.iridio.ui.fragment;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.urbanoexpress.iridio.R;
import com.urbanoexpress.iridio.databinding.FragmentRequestPermissionBinding;
import com.urbanoexpress.iridio.ui.InitActivity;
import com.urbanoexpress.iridio.ui.RequestPermissionActivity;
import com.urbanoexpress.iridio.ui.helpers.ModalHelper;
import com.urbanoexpress.iridio.util.CommonUtils;
import com.urbanoexpress.iridio.util.LocationUtils;
import com.urbanoexpress.iridio.util.PermissionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mick on 10/03/17.
 */

public class RequestPermissionFragment extends Fragment {

    public static final String TAG = BienvenidaFragment.class.getSimpleName();

    private FragmentRequestPermissionBinding binding;

    private MultiplePermissionsListener multiplePermissionsListener;

    private boolean isSwitchedOnGPS = false;

    public static RequestPermissionFragment newInstance() {
        return new RequestPermissionFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().getWindow().setBackgroundDrawable(
                ContextCompat.getDrawable(getActivity(), R.drawable.bg_red));
        CommonUtils.changeColorStatusBar(getActivity(), R.color.statusBarColor);
        binding = FragmentRequestPermissionBinding.inflate(inflater, container, false);

        initUI();

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
        if (PermissionUtils.checkBasicPermissions(getActivity())) {
            if (getActivity() instanceof InitActivity) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                if (PermissionUtils.checkBackgroundLocationPermission(getActivity())) {
                    if (isSwitchedOnGPS) {
                        fragmentManager.beginTransaction().replace(R.id.container,
                                LogInFragment.newInstance(),
                                LogInFragment.TAG).commit();
                    } else {
                        fragmentManager.beginTransaction().replace(R.id.container,
                                new TurnOnGPSFragment(),
                                TurnOnGPSFragment.TAG).commit();
                    }
                } else {
                    fragmentManager.beginTransaction().replace(R.id.container,
                            RequestLocationPermissionFragment.newInstance(),
                            RequestLocationPermissionFragment.TAG).commit();
                }
            } else if (getActivity() instanceof RequestPermissionActivity) {
                if (PermissionUtils.checkBackgroundLocationPermission(getActivity())) {
                    getActivity().finish();
                } else {
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container,
                            RequestLocationPermissionFragment.newInstance(),
                            RequestLocationPermissionFragment.TAG).commit();
                }
            }
        }
    }

    private void initUI() {
        binding.btnOtorgarAcceso.setOnClickListener(v -> validatePermission());
    }

    private void validatePermission() {
        multiplePermissionsListener = new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(final MultiplePermissionsReport report) {
                if (report.areAllPermissionsGranted()) {
                    Log.d(TAG, "onPermissionGranted");
                    binding.btnOtorgarAcceso.setEnabled(false);
                } else if (report.isAnyPermissionPermanentlyDenied()) {
                    Log.d(TAG, "onPermissionDenied");
                    ModalHelper.getBuilderAlertDialog(getActivity())
                            .setTitle(R.string.fragment_request_permission_title_requerimos_su_permiso)
                            .setMessage(getString(R.string.fragment_request_permission_msg_requerimos_su_permiso_default)
                                    + getString(R.string.fragment_request_permission_msg_requerimos_su_permiso_config))
                            .setPositiveButton(R.string.text_configurar, (dialog, which) -> {
                                try {
                                    Intent intent = new Intent();
                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                                    intent.setData(uri);
                                    getActivity().startActivity(intent);
                                } catch (NullPointerException ex) {
                                    ex.printStackTrace();
                                }
                            })
                            .setNegativeButton(R.string.text_denegar, null)
                            .show();
                } else {
                    Log.d(TAG, "onPermissionDenied");
                    ModalHelper.getBuilderAlertDialog(getActivity())
                            .setTitle(R.string.fragment_request_permission_title_requerimos_su_permiso)
                            .setMessage(R.string.fragment_request_permission_msg_requerimos_su_permiso_default)
                            .setPositiveButton(R.string.text_permitir, (dialog, which) -> {
                                ArrayList<String> requirePermissions = new ArrayList<>();

                                for (int j = 0; j < report.getDeniedPermissionResponses().size(); j++) {
                                    if (ContextCompat.checkSelfPermission(getActivity(),
                                            report.getDeniedPermissionResponses().get(j).getPermissionName())
                                            == PackageManager.PERMISSION_DENIED) {
                                        requirePermissions.add(
                                                report.getDeniedPermissionResponses().get(j).getPermissionName()
                                        );
                                    }
                                }

                                Dexter.withActivity(getActivity())
                                        .withPermissions(requirePermissions)
                                        .withListener(multiplePermissionsListener).check();
                            })
                            .setNegativeButton(R.string.text_denegar, null)
                            .show();
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                Log.d(TAG, "onPermissionRationaleShouldBeShown");
                token.continuePermissionRequest();
            }
        };

        ArrayList<String> requirePermissions = new ArrayList<>();

        ArrayList<String> permissions = PermissionUtils.getBasicPermissions();

        for (int i = 0; i < permissions.size(); i++) {
            if (ContextCompat.checkSelfPermission(getActivity(),
                    permissions.get(i)) == PackageManager.PERMISSION_DENIED) {
                requirePermissions.add(permissions.get(i));
            }
        }

        Dexter.withActivity(getActivity())
                .withPermissions(requirePermissions)
                .withListener(multiplePermissionsListener).check();
    }
}