package com.urbanoexpress.iridio3.pe.ui.fragment;

import static android.Manifest.permission.READ_PHONE_STATE;
import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
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
import com.bumptech.glide.Glide;
import com.urbanoexpress.iridio3.pe.R;
import com.urbanoexpress.iridio3.pe.databinding.FragmentConfigPhoneBinding;
import com.urbanoexpress.iridio3.pe.presenter.ConfigPhonePresenter;
import com.urbanoexpress.iridio3.pe.ui.dialogs.ChoiseCountryBottomSheet;
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
                            validatePermissionUSer();
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

        /*
        requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (getViewProgress().getVisibility() != View.VISIBLE) {
                    finishActivity();
                }
            }
        });*/

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
    public void setEnabledButtonNext(boolean enabled) {
        binding.nextButton.setEnabled(enabled);
    }

    @Override
    public void setErrorPhone(String error) {
        binding.nextButton.setError("Debes ingresar tu número de celular.");
        binding.nextButton.requestFocus();
    }

    @Override
    public void setHintPhone(String hint) {
        binding.txtPhone.setHint(hint);
    }

    @Override
    public void setTextPhonePrefix(String text) {
        binding.lblPhonePrefix.setText(text);
    }

    @Override
    public void setIconFlag(int resId) {
        Glide.with(getViewContext())
                .load(resId)
                .transition(withCrossFade())
                .into(binding.iconFlag);
    }

    @Override
    public void requestHint() {
         /*
        HintRequest hintRequest = new HintRequest.Builder()
                .setPhoneNumberIdentifierSupported(true)
                .build();

        try {
            PendingIntent intent = Credentials.getClient(getViewContext()).getHintPickerIntent(hintRequest);
            getActivity().startIntentSenderForResult(intent.getIntentSender(),
                    HINT_REQUEST, null, 0, 0, 0);
        } catch (IntentSender.SendIntentException ex) {
            ex.printStackTrace();
        }*/
    }

    @Override
    public void navigateToChoiseCountryBottomSheet() {
        ChoiseCountryBottomSheet bottomSheet = new ChoiseCountryBottomSheet();
        bottomSheet.show(getChildFragmentManager(), "ChoiseCountryBottomSheet");
    }

    @Override
    public void navigateToVerficationCodeFragment(String isoCountry, String phone,
                                                  String firebaseToken, Boolean isGoogleMock) {
        verificationCodeFragment = VerficationCodeFragment.newInstance(
                isoCountry, phone, firebaseToken, isGoogleMock);

        if (getLifecycle().getCurrentState().equals(Lifecycle.State.STARTED) ||
                getLifecycle().getCurrentState().equals(Lifecycle.State.RESUMED)) {
            showVerificationCodeFragment();
        }
    }

    public void onCountrySelected(String iso) {
        presenter.onCountrySelected(iso);
    }

    private void setupViews() {

        binding.selectCountryLayout.setOnClickListener(v -> presenter.onSelectCountryClick());

        binding.txtPhone.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                hideKeyboard();
                presenter.onBtnContinuarClick();
            }
            return false;
        });

        binding.nextButton.setOnClickListener(v -> {
            hideKeyboard();
            AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) binding.textInputLayoutMenu.getEditText();
            if (autoCompleteTextView != null) {
                if (!autoCompleteTextView.getText().toString().isEmpty()) {
                    presenter.onBtnContinuarClick2();
                }
            }
        });
    }

    private void showVerificationCodeFragment() {
        if (verificationCodeFragment != null) {
            try {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.container,
                        verificationCodeFragment, VerficationCodeFragment.TAG).commit();
            } catch (NullPointerException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void getPhoneNumber1() {
        boolean allPermissionsGranted = true;

        for (String permission : getPermissionList()) {
            if (ContextCompat.checkSelfPermission(requireContext(), permission)
                    != PackageManager.PERMISSION_GRANTED) {
                allPermissionsGranted = false;
            }
        }
        if (allPermissionsGranted) {
            updatePhoneNumberList();
        } else {
            requestPermissionPhoneNumber();
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
                        listPhoneNumber.add(phoneNumber);
                    }
                }
            }

        }

        if (binding != null) {
            setAdapterListNumber(listPhoneNumber);
        }
    }

    private void requestPermissionPhoneNumber() {
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

    private void showPermissionsConfiguration() {
        ModalHelper.getBuilderAlertDialog(requireContext())
                .setTitle("Permiso denegado")
                .setMessage("Para usar esta funcionalidad, necesitas habilitar el permiso manualmente. ¿Quieres ir a la configuración de la aplicación?")
                .setPositiveButton(R.string.text_aceptar, (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", requireActivity().getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                })
                .setNegativeButton("Salir", (dialog, which) -> finishActivity())
                .show();
    }

    private void showPermissionsJustification() {
        ModalHelper.getBuilderAlertDialog(requireContext())
                .setTitle("Permiso necesario")
                .setMessage("Esta aplicación necesita acceso al telefono para conocer su número. ¿Deseas conceder el permiso?")
                .setPositiveButton(R.string.text_aceptar, (dialog, which) -> {
                    requestPermissionPhoneNumber();
                })
                .setNegativeButton("Salir", (dialog, which) -> finishActivity())
                .show();

    }
}
