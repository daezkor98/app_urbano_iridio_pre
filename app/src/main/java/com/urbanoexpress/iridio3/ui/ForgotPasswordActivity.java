package com.urbanoexpress.iridio3.ui;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.android.volley.VolleyError;
import com.urbanoexpress.iridio3.R;
import com.urbanoexpress.iridio3.data.rest.ApiRequest;
import com.urbanoexpress.iridio3.data.rest.ApiRest;
import com.urbanoexpress.iridio3.databinding.ActivityForgotPasswordBinding;
import com.urbanoexpress.iridio3.ui.helpers.ModalHelper;
import com.urbanoexpress.iridio3.util.CommonUtils;
import com.urbanoexpress.iridio3.util.Preferences;
import com.urbanoexpress.iridio3.util.network.Connection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ForgotPasswordActivity extends AppThemeBaseActivity {

    private ActivityForgotPasswordBinding binding;
    private String idUser = "";
    private String devicePhone = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Preferences.getInstance().init(this, "GlobalConfigApp");
        devicePhone = Preferences.getInstance().getString("phone", "");

        Preferences.getInstance().init(this, "UserProfile");

        setupViews();
    }

    @Override
    public boolean onBackButtonPressed() {
        if (binding.boxInputPasswords.getVisibility() == View.VISIBLE) {
            ModalHelper.getBuilderAlertDialog(this)
                    .setTitle(R.string.act_forgot_password_title_cancelar_restablecer_password)
                    .setMessage(R.string.act_forgot_password_msg_cancelar_restablecer_password)
                    .setPositiveButton(R.string.text_salir, (dialog, which) -> finish())
                    .setNegativeButton(R.string.text_cancelar, null)
                    .show();
            return true;
        }
        return false;
    }

    @Override
    protected void animOnLeaveActivity() {
        overridePendingTransition(R.anim.not_slide, R.anim.slide_exit_out_bottom);
    }

    private void setupViews() {
        binding.txtUser.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER
                    && event.getAction() == KeyEvent.ACTION_UP) {
                if (validateInputUserName()) {
                    validateUserName();
                }
            }
            return false;
        });

        binding.txtRepeatPassword.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER
                    && event.getAction() == KeyEvent.ACTION_UP) {
                if (validateInputNewPassword()) {
                    changeUserPassword();
                }
            }
            return false;
        });

        binding.btnContinuar.setOnClickListener(v -> {
            if (validateInputUserName()) {
                validateUserName();
            }
        });

        binding.btnRestablecer.setOnClickListener(v -> {
            if (validateInputNewPassword()) {
                changeUserPassword();
            }
        });
    }

    private void showPageInputNewPassword() {
        binding.imgForgotPassword.setImageResource(R.drawable.img_edit_password);
        binding.lblTitle.setText(R.string.act_forgot_password_title_reset_password);
        binding.lblDescription.setText(R.string.act_forgot_password_msg_reset_password);
        binding.txtUser.setVisibility(View.GONE);
        binding.boxInputPasswords.setVisibility(View.VISIBLE);
        binding.btnContinuar.setVisibility(View.GONE);
        binding.btnRestablecer.setVisibility(View.VISIBLE);
    }

    private boolean validateInputUserName() {
        if (binding.txtUser.getText().toString().trim().isEmpty()) {
            showToast(R.string.act_forgot_password_msg_error_user_name);
            return false;
        }
        return true;
    }

    private boolean validateInputNewPassword() {
        if (binding.txtNewPassword.getText().toString().trim().isEmpty() ||
                binding.txtRepeatPassword.getText().toString().trim().isEmpty()) {
            showToast(R.string.act_forgot_password_msg_error_new_password);
            return false;
        }

        if (!binding.txtNewPassword.getText().toString().trim().equals(
                binding.txtRepeatPassword.getText().toString().trim())) {
            showToast(R.string.act_forgot_password_msg_error_different_password);
            return false;
        }

        if (binding.txtNewPassword.getText().toString().trim().length() < 8 ||
                binding.txtRepeatPassword.getText().toString().trim().length() < 8) {
            showToast(R.string.act_forgot_password_msg_error_min_length);
            return false;
        }
        return true;
    }

    private void validateUserName() {
        if (Connection.hasNetworkConnectivity(ForgotPasswordActivity.this)) {
            showProgressDialog();
            ApiRequest.getInstance().newParams();
            ApiRequest.getInstance().putParams("user_name", binding.txtUser.getText().toString().trim());
            ApiRequest.getInstance().putParams("device_imei", devicePhone);
            ApiRequest.getInstance().request(ApiRest.getInstance().getApiBaseUrl() + ApiRest.Api.VALIDATE_USER_NAME_COURIER,
                    ApiRequest.TypeParams.FORM_DATA, new ApiRequest.ResponseListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            dismissProgressDialog();
                            try {
                                if (response.getBoolean("success")) {
                                    JSONArray data = response.getJSONArray("data");
                                    if (data.length() == 1) {
                                        idUser = data.getJSONObject(0).getString("id_user");
                                        showPageInputNewPassword();
                                    } else {
                                        showToast(R.string.act_forgot_password_msg_not_found_user_data);
                                    }
                                } else {
                                    showToast(response.getString("msg_error"));
                                }
                            } catch (JSONException ex) {
                                ex.printStackTrace();
                                showToast(R.string.json_object_exception);
                                CommonUtils.vibrateDevice(ForgotPasswordActivity.this, 100);
                            }
                        }

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                            showToast(R.string.volley_error_message);
                            CommonUtils.vibrateDevice(ForgotPasswordActivity.this, 100);
                        }
                    });
        } else {
            showMessageNotConnectedToNetwork();
        }
    }

    private void changeUserPassword() {
        if (Connection.hasNetworkConnectivity(ForgotPasswordActivity.this)) {
            showProgressDialog();
            ApiRequest.getInstance().newParams();
            ApiRequest.getInstance().putParams("id_user", idUser);
            ApiRequest.getInstance().putParams("new_password",
                    CommonUtils.getSHA1(binding.txtRepeatPassword.getText().toString().trim()));
            ApiRequest.getInstance().putParams("device_imei", devicePhone);
            ApiRequest.getInstance().request(ApiRest.getInstance().getApiBaseUrl() + ApiRest.Api.CHANGE_USER_COURIER_PASSWORD,
                    ApiRequest.TypeParams.FORM_DATA, new ApiRequest.ResponseListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            dismissProgressDialog();
                            try {
                                if (response.getBoolean("success")) {
                                    ModalHelper.getBuilderAlertDialog(ForgotPasswordActivity.this)
                                            .setTitle(R.string.act_forgot_password_title_restablecer_password)
                                            .setMessage(R.string.act_forgot_password_msg_restablecer_password)
                                            .setCancelable(false)
                                            .setPositiveButton(R.string.text_aceptar, (dialog, which) -> {
                                                dialog.dismiss();
                                                finish();
                                            })
                                            .show();
                                } else {
                                    showToast(response.getString("msg_error"));
                                }
                            } catch (JSONException ex) {
                                ex.printStackTrace();
                                showToast(R.string.json_object_exception);
                                CommonUtils.vibrateDevice(ForgotPasswordActivity.this, 100);
                            }
                        }

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                            showToast(R.string.volley_error_message);
                            CommonUtils.vibrateDevice(ForgotPasswordActivity.this, 100);
                        }
                    });
        } else {
            showMessageNotConnectedToNetwork();
        }
    }
}
