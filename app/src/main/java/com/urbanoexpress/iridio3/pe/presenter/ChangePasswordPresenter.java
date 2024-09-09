package com.urbanoexpress.iridio3.pe.presenter;

import com.android.volley.VolleyError;
import com.urbanoexpress.iridio3.pe.R;
import com.urbanoexpress.iridio3.pe.model.interactor.UserProfileInteractor;
import com.urbanoexpress.iridio3.pe.model.interactor.callback.RequestCallback;
import com.urbanoexpress.iridio3.pe.util.CommonUtils;
import com.urbanoexpress.iridio3.pe.util.Preferences;
import com.urbanoexpress.iridio3.pe.view.ChangePasswordView;

import org.json.JSONException;
import org.json.JSONObject;

public class ChangePasswordPresenter {

    private ChangePasswordView view;

    public ChangePasswordPresenter(ChangePasswordView view) {
        this.view = view;
    }

    public void onSaveButtonClick() {
        if (!validaForm()) return;
        requestChangePassword();
    }

    private void requestChangePassword() {
        view.showProgressDialog();

        RequestCallback callback = new RequestCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                view.dismissProgressDialog();

                try {
                    if (response.getBoolean("success")) {
                        view.showModalPasswordChangedSuccess();
                    } else {
                        view.showMsgError(response.getString("msg_error"));
                    }
                } catch (JSONException ex) {
                    ex.printStackTrace();
                    view.showMsgError(view.getViewContext().getString(R.string.json_object_exception));
                }
            }

            @Override
            public void onError(VolleyError error) {
                error.printStackTrace();
                view.dismissProgressDialog();
                view.showMsgError(view.getViewContext().getString(R.string.volley_error_message));
            }
        };

        String[] params = {
                CommonUtils.getSHA1(view.getTextCurrentPassword()),
                CommonUtils.getSHA1(view.getTextNewPassword()),
                Preferences.getInstance().getString("idUsuario", "")};

        UserProfileInteractor.changePassword(params, callback);
    }

    private boolean validaForm() {
        if (view.getTextCurrentPassword().isEmpty()) {
            view.setErrorCurrentPassword("Este campo es obligatorio.");
            return false;
        }

        if (view.getTextNewPassword().isEmpty()) {
            view.setErrorNewPassword("Este campo es obligatorio.");
            return false;
        }

        if (view.getTextRepeatPassword().isEmpty()) {
            view.setErrorRepeatPassword("Este campo es obligatorio.");
            return false;
        }

        if (view.getTextCurrentPassword().equals(view.getTextNewPassword())) {
            view.setErrorNewPassword("La contraseña nueva no debe ser la misma al actual.");
            return false;
        }

        if (!view.getTextNewPassword().equals(view.getTextRepeatPassword())) {
            view.setErrorRepeatPassword(view.getViewContext().getString(
                    R.string.act_forgot_password_msg_error_different_password));
            return false;
        }

        if (view.getTextNewPassword().length() < 8) {
            view.setErrorNewPassword(view.getViewContext().getString(
                    R.string.act_forgot_password_msg_error_min_length));
            return false;
        }

        return true;
    }
}
