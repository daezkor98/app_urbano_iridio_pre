package com.urbanoexpress.iridio3.pe.ui.fragment;

import static android.Manifest.permission.READ_PHONE_STATE;

import static com.urbanoexpress.iridio3.pe.util.constant.ConfiAppKt.CODE_ISO_PERU;
import static com.urbanoexpress.iridio3.pe.util.constant.ConfiAppKt.CODE_PHONE_PERU;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.urbanoexpress.iridio3.pe.R;
import com.urbanoexpress.iridio3.pe.databinding.FragmentConfigPhoneBinding;
import com.urbanoexpress.iridio3.pe.presenter.ConfigPhonePresenter;
import com.urbanoexpress.iridio3.pe.ui.helpers.ModalHelper;
import com.urbanoexpress.iridio3.pe.util.CommonUtils;
import com.urbanoexpress.iridio3.pe.view.ConfigPhoneView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ConfigPhoneFragment extends AppThemeBaseFragment implements ConfigPhoneView {

    public static final String TAG = "ConfigPhoneFragment";
    private FragmentConfigPhoneBinding binding;
    private ConfigPhonePresenter presenter;
    private Fragment verificationCodeFragment;
    private final int HINT_REQUEST = 100;
    private boolean isShowingPermissionScreen = false;
    private final ActivityResultLauncher<String[]> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(), permissions -> {
                boolean allPermissionsGranted = true;

                for (String permission : permissions.keySet()) {
                    Boolean isGranted = permissions.get(permission);
                    if (isGranted != null) {
                        if (!isGranted) {
                            allPermissionsGranted = false;
                            break;
                        }
                    }
                }
                isShowingPermissionScreen = false;
                if (allPermissionsGranted) {
                    updatePhoneNumberList();
                }
            }
    );

    public static ConfigPhoneFragment newInstance() {
        return new ConfigPhoneFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        getActivity().getWindow().setBackgroundDrawable(
                ContextCompat.getDrawable(getActivity(), R.drawable.bg_fragment_bienvenida));
        CommonUtils.changeColorStatusBar(getActivity(), R.color.statusBarColor);
        binding = FragmentConfigPhoneBinding.inflate(inflater);
        getPhoneNumber();
        EditText editTextAutocomplete = binding.textInputLayoutMenu.getEditText();
        AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) editTextAutocomplete;
        if (autoCompleteTextView != null) {
            autoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
                        if (!autoCompleteTextView.getText().toString().isEmpty()) {
                            binding.nextButton.setEnabled(true);
                        }
                    }
            );
            autoCompleteTextView.setOnFocusChangeListener((v, hasFocus) -> {
                        if (hasFocus) {
                            if (!isShowingPermissionScreen) {
                                validatePermissionUSer();
                            }
                            autoCompleteTextView.clearFocus();
                        }
                    }
            );
        }

        return binding.getRoot();
    }

    private void setAdapterListNumber(List<String> listNumbers) {
        if (!listNumbers.isEmpty()) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getViewContext(), R.layout.list_item_numbers, listNumbers);
            AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) binding.textInputLayoutMenu.getEditText();
            if (autoCompleteTextView != null) {
                autoCompleteTextView.setAdapter(adapter);
            }
        }

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews();
        if (presenter == null) {
            presenter = new ConfigPhonePresenter(this);
            presenter.init();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        showVerificationCodeFragment();
    }

    public void activityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == HINT_REQUEST) {
            /*if (data != null) {
                Credential cred = data.getParcelableExtra(Credential.EXTRA_KEY);
                if (cred != null) {
                    String unformattedPhone = cred.getId();
                    unformattedPhone = unformattedPhone.replaceAll("[^\\d]|(?<=.)\\+", "");
                    StringBuilder phone = new StringBuilder(unformattedPhone).reverse();
                    phone = new StringBuilder(phone.substring(0, 9)).reverse();
                    binding.txtPhone.setText(phone);
                    hideKeyboard();
                    presenter.onBtnContinuarClick();
                }
            }*/
        }
    }

    @Override
    public String getTextPhone() {
        AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) binding.textInputLayoutMenu.getEditText();
        if (autoCompleteTextView != null) {
            return autoCompleteTextView.getText().toString().trim();
        } else {
            return "";
        }
    }

    @Override
    public void navigateToVerficationCodeFragment(String codePhone, String phone) {
        verificationCodeFragment = VerficationCodeFragment.newInstance(
                codePhone, phone);

        if (getLifecycle().getCurrentState().equals(Lifecycle.State.STARTED) ||
                getLifecycle().getCurrentState().equals(Lifecycle.State.RESUMED)) {
            showVerificationCodeFragment();
        }
    }

    public void onCountrySelected(String iso) {
        presenter.onCountrySelected(iso);
    }

    private void setupViews() {

        binding.nextButton.setOnClickListener(v -> {
            hideKeyboard();
            AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) binding.textInputLayoutMenu.getEditText();
            if (autoCompleteTextView != null) {
                if (!autoCompleteTextView.getText().toString().isEmpty()) {
                    presenter.onBtnContinueClick(CODE_PHONE_PERU);

                }
            }
        });
    }

    private void showVerificationCodeFragment() {
        if (verificationCodeFragment != null) {
            try {
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.container,
                        verificationCodeFragment, VerficationCodeFragment.TAG).commit();
            } catch (NullPointerException ex) {
                ex.printStackTrace();
            }
        }
    }

    private boolean allPermissionsGranted() {
        boolean allPermissionsGranted = true;

        for (String permission : getPermissionList()) {
            if (ContextCompat.checkSelfPermission(requireContext(), permission)
                    != PackageManager.PERMISSION_GRANTED) {
                allPermissionsGranted = false;
            }
        }
        return allPermissionsGranted;
    }

    private void getPhoneNumber() {
        if (allPermissionsGranted()) {
            updatePhoneNumberList();
        } else {
            requestPermissionPhoneNumber();
        }

    }

    private void validatePermissionUSer() {
        if (allPermissionsGranted()) {
            updatePhoneNumberList();
        } else {
            boolean mostrarJustificacion = false;

            for (String permission : getPermissionList()) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), permission)) {
                    mostrarJustificacion = true;
                    break;
                }
            }
            if (!mostrarJustificacion) {
                showPermissionsConfiguration();
            } else {
                showPermissionsJustification();
            }
        }
    }

    private void updatePhoneNumberList() {
        ArrayList<String> listPhoneNumber = new ArrayList<>();
        SubscriptionManager subscriptionManager;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            subscriptionManager = (SubscriptionManager) requireActivity().getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
            if (subscriptionManager != null) {
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissionPhoneNumber();
                    return;
                }
                for (SubscriptionInfo subscriptionInfo : subscriptionManager.getActiveSubscriptionInfoList()) {
                    String phoneNumber = subscriptionInfo.getNumber();

                    if (phoneNumber != null) {
                        listPhoneNumber.add(getNationalNumber(phoneNumber));
                    }
                }
            }

        }

        if (binding != null) {
            setAdapterListNumber(listPhoneNumber);
        }
    }

    private void requestPermissionPhoneNumber() {
        isShowingPermissionScreen = true;
        requestPermissionLauncher.launch(getPermissionList());
    }

    private String[] getPermissionList() {
        String[] permissions = {READ_PHONE_STATE};

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            permissions = Arrays.copyOf(permissions, permissions.length + 1);
            permissions[permissions.length - 1] = Manifest.permission.READ_PHONE_NUMBERS;
        }
        return permissions;
    }

    private boolean isPhoneFromPeru() {
        return getSimCountryCode().equals(CODE_ISO_PERU);
    }


    private String getNationalNumber(String phone) {
        try {
            PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
            Phonenumber.PhoneNumber number = phoneNumberUtil.parse(phone, getSimCountryCode());
            return String.valueOf(number.getNationalNumber());
        } catch (NumberParseException e) {
            return String.valueOf(phone);
        }
    }

    private String getSimCountryCode() {
        TelephonyManager telephonyManager = (TelephonyManager) requireActivity().getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getSimCountryIso().toUpperCase();

    }

    private void showPermissionsConfiguration() {
        ModalHelper.getBuilderAlertDialog(requireContext())
                .setTitle(R.string.text_config_phone_title_permission_denied)
                .setMessage(R.string.text_config_phone_msg_permission_denied)
                .setPositiveButton(R.string.text_config_phone_accept, (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", requireActivity().getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                })
                .setNegativeButton(R.string.text_config_phone_exit, (dialog, which) -> finishActivity())
                .show();
    }

    private void showPermissionsJustification() {
        ModalHelper.getBuilderAlertDialog(requireContext())
                .setTitle(R.string.text_config_phone_title_permissions_justification)
                .setMessage(R.string.text_config_phone_msg_permissions_justification)
                .setPositiveButton(R.string.text_config_phone_accept, (dialog, which) -> {
                    requestPermissionPhoneNumber();
                })
                .setNegativeButton(R.string.text_config_phone_exit, (dialog, which) -> finishActivity())
                .show();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
