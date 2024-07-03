package com.urbanoexpress.iridio3.ui.fragment;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import com.bumptech.glide.Glide;
//import com.google.android.gms.auth.api.credentials.Credential;
//import com.google.android.gms.auth.api.credentials.Credentials;
//import com.google.android.gms.auth.api.credentials.HintRequest;
import com.urbanoexpress.iridio3.R;
import com.urbanoexpress.iridio3.databinding.FragmentConfigPhoneBinding;
import com.urbanoexpress.iridio3.presenter.ConfigPhonePresenter;
import com.urbanoexpress.iridio3.ui.dialogs.ChoiseCountryBottomSheet;
import com.urbanoexpress.iridio3.util.CommonUtils;
import com.urbanoexpress.iridio3.view.ConfigPhoneView;

public class ConfigPhoneFragment extends AppThemeBaseFragment implements ConfigPhoneView {

    public static final String TAG = "ConfigPhoneFragment";

    private FragmentConfigPhoneBinding binding;
    private ConfigPhonePresenter presenter;
    private Fragment verificationCodeFragment;

    private final int HINT_REQUEST = 100;

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
        binding = FragmentConfigPhoneBinding.inflate(inflater, container, false);
        return binding.getRoot();
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
        return binding.txtPhone.getText().toString().trim();
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
            presenter.onBtnContinuarClick();
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
}
