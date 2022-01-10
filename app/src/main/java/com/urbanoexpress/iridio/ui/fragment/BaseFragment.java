package com.urbanoexpress.iridio.ui.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.urbanoexpress.iridio.R;
import com.urbanoexpress.iridio.application.AndroidApplication;
import com.urbanoexpress.iridio.ui.helpers.ModalHelper;
import com.urbanoexpress.iridio.ui.helpers.ModalInterface;
import com.urbanoexpress.iridio.util.CommonUtils;
import com.urbanoexpress.iridio.util.network.Connection;

public class BaseFragment extends Fragment implements ModalInterface {

    private ProgressDialog progressDialog = null;

    @Override
    public void onDestroy() {
        super.onDestroy();
        dismissProgressDialog();
    }

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

    public void finishActivity() {
        try {
            getActivity().finish();
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    public void showMessageNotConnectedToNetwork() {
        Connection.showMessageNotConnectedToNetwork(getView());
    }

    public void hideKeyboard() {
        try {
            InputMethodManager imm = (InputMethodManager) getContext()
                    .getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getView().getRootView().getWindowToken(), 0);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    public void showKeyboard(View view) {
        try {
            InputMethodManager imm = (InputMethodManager) getContext()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }
}