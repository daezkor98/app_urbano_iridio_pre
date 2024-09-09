package com.urbanoexpress.iridio3.pe.ui.dialogs;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.urbanoexpress.iridio3.pe.R;
import com.urbanoexpress.iridio3.pe.application.AndroidApplication;
import com.urbanoexpress.iridio3.pe.ui.helpers.ModalHelper;
import com.urbanoexpress.iridio3.pe.ui.helpers.ModalInterface;
import com.urbanoexpress.iridio3.pe.util.CommonUtils;

public abstract class BaseDialogFragment extends DialogFragment implements ModalInterface {

    @Override
    public int getTheme() {
        return R.style.RoundedCornersDialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private ProgressDialog progressDialog = null;

    private void buildProgressDialog() {
        Activity activity = getActivity();
        if (activity != null) {
            if (progressDialog == null) {
                if (CommonUtils.isAndroidLollipop()) {
                    progressDialog = new ProgressDialog(activity, R.style.AppCompatAlertDialogStyle);
                } else {
                    progressDialog = new ProgressDialog(activity);
                }
                progressDialog.setCancelable(false);
                progressDialog.setIndeterminate(true);
            }
        }
    }

    @Override
    public void dismissProgressDialog() {
        if (progressDialog != null) {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
                progressDialog = null;
            }
        }
    }

    @Override
    public void showProgressDialog(int messageId) {
        try {
            showProgressDialog(getContext().getString(messageId));
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void showProgressDialog(int titleId, int messageId) {
        try {
            showProgressDialog(getContext().getString(titleId), getContext().getString(messageId));
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void showProgressDialog(String message) {
        buildProgressDialog();
        if (progressDialog != null) {
            progressDialog.setTitle(null);
            progressDialog.setMessage(message);
            if (!progressDialog.isShowing()) {
                progressDialog.show();
            }
        }
    }

    @Override
    public void showProgressDialog(String title, String message) {
        buildProgressDialog();
        if (progressDialog != null) {
            progressDialog.setTitle(title);
            progressDialog.setMessage(message);
            if (!progressDialog.isShowing()) {
                progressDialog.show();
            }
        }
    }

    @Override
    public void showSnackBar(int messageId) {
        try {
            showSnackBar(getContext().getString(messageId));
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void showSnackBar(String message) {
        new ModalHelper.BottomPopup.Builder(getView(), message).build().show();
    }

    @Override
    public void showToast(int messageId) {
        try {
            showToast(getContext().getString(messageId));
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void showToast(String message) {
        ModalHelper.showToast(AndroidApplication.getAppContext(), message, Toast.LENGTH_LONG);
    }

    public Context getViewContext() {
        return getActivity();
    }
}
