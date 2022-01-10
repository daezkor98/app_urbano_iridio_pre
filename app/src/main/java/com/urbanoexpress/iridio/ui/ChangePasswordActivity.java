package com.urbanoexpress.iridio.ui;

import androidx.annotation.Nullable;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;

import com.google.android.material.textfield.TextInputLayout;
import com.urbanoexpress.iridio.R;
import com.urbanoexpress.iridio.databinding.ActivityChangePasswordBinding;
import com.urbanoexpress.iridio.presenter.ChangePasswordPresenter;
import com.urbanoexpress.iridio.ui.helpers.ModalHelper;
import com.urbanoexpress.iridio.util.CommonUtils;
import com.urbanoexpress.iridio.view.ChangePasswordView;

public class ChangePasswordActivity extends AppThemeBaseActivity implements ChangePasswordView {

    private ActivityChangePasswordBinding binding;
    private ChangePasswordPresenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChangePasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupViews();

        if (presenter == null) {
            presenter = new ChangePasswordPresenter(this);
        }
    }

    @Override
    public String getTextCurrentPassword() {
        return binding.currentPasswordTextField.getEditText().getText().toString().trim();
    }

    @Override
    public String getTextNewPassword() {
        return binding.newPasswordTextField.getEditText().getText().toString().trim();
    }

    @Override
    public String getTextRepeatPassword() {
        return binding.repeatPasswordTextField.getEditText().getText().toString().trim();
    }

    @Override
    public void setErrorCurrentPassword(String error) {
        binding.currentPasswordTextField.setError(error);
        CommonUtils.vibrateDevice(getViewContext(), 100);
    }

    @Override
    public void setErrorNewPassword(String error) {
        binding.newPasswordTextField.setError(error);
        CommonUtils.vibrateDevice(getViewContext(), 100);
    }

    @Override
    public void setErrorRepeatPassword(String error) {
        binding.repeatPasswordTextField.setError(error);
        CommonUtils.vibrateDevice(getViewContext(), 100);
    }

    @Override
    public void showMsgError(String error) {
        binding.msgErrorText.setText(error);
        binding.msgErrorText.setVisibility(View.VISIBLE);
        CommonUtils.vibrateDevice(getViewContext(), 100);
    }

    @Override
    public void showModalPasswordChangedSuccess() {
        ModalHelper.getBuilderAlertDialog(this)
                .setTitle("Contraseña actualizada")
                .setMessage("Tu contraseña ha sido actualizada exitosamente.")
                .setCancelable(false)
                .setPositiveButton(R.string.text_aceptar, (dialog, which) -> {
                    finish();
                    overridePendingTransition(R.anim.not_slide, R.anim.slide_exit_out_right);
                })
                .show();
    }

    private void setupViews() {
        setupToolbar(binding.toolbar);
        setScreenTitle("Cambiar contraseña");

        binding.currentPasswordTextField.getEditText().addTextChangedListener(
                new DisableErrorTextWatcher(binding.currentPasswordTextField));
        binding.newPasswordTextField.getEditText().addTextChangedListener(
                new DisableErrorTextWatcher(binding.newPasswordTextField));
        binding.repeatPasswordTextField.getEditText().addTextChangedListener(
                new DisableErrorTextWatcher(binding.repeatPasswordTextField));

        binding.repeatPasswordTextField.getEditText().setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                changePassword();
            }
            return false;
        });

        binding.msgErrorText.setOnClickListener(v -> v.setVisibility(View.GONE));
        binding.saveButton.setOnClickListener(v -> changePassword());
    }

    private void changePassword() {
        hideKeyboard();
        binding.msgErrorText.setVisibility(View.GONE);
        presenter.onSaveButtonClick();
    }

    private static class DisableErrorTextWatcher implements TextWatcher {

        private final TextInputLayout textField;

        public DisableErrorTextWatcher(TextInputLayout textField) {
            this.textField = textField;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            textField.setErrorEnabled(false);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }
}