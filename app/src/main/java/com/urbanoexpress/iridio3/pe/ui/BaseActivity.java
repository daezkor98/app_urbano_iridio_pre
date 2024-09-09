package com.urbanoexpress.iridio3.pe.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.urbanoexpress.iridio3.R;
import com.urbanoexpress.iridio3.pe.application.AndroidApplication;
import com.urbanoexpress.iridio3.pe.ui.helpers.ModalHelper;
import com.urbanoexpress.iridio3.pe.ui.helpers.ModalInterface;
import com.urbanoexpress.iridio3.pe.util.CommonUtils;
import com.urbanoexpress.iridio3.pe.util.network.Connection;

public abstract class BaseActivity extends AppCompatActivity implements ModalInterface {

    ProgressDialog progressDialog = null;

    private void buildProgressDialog() {
        if (progressDialog == null) {
            if (CommonUtils.isAndroidLollipop()) {
                progressDialog = new ProgressDialog(this, R.style.AppCompatAlertDialogStyle);
            } else {
                progressDialog = new ProgressDialog(this);
            }
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(true);
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
        showProgressDialog(getString(messageId));
    }

    @Override
    public void showProgressDialog(int titleId, int messageId) {
        showProgressDialog(getString(titleId), getString(messageId));
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
        showSnackBar(getString(messageId));
    }

    @Override
    public void showSnackBar(String message) {
        new ModalHelper.BottomPopup.Builder(findViewById(android.R.id.content), message)
                .build().show();
    }

    @Override
    public void showToast(int messageId) {
        showToast(getString(messageId));
    }

    @Override
    public void showToast(String message) {
        ModalHelper.showToast(AndroidApplication.getAppContext(), message, Toast.LENGTH_LONG);
    }

    public void setScreenTitle(int resId) {
        setScreenTitle(getString(resId));
    }

    public void setScreenTitle(String title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }

    public void setupToolbar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public Context getViewContext() {
        return this;
    }

    public void showMessageNotConnectedToNetwork() {
        Connection.showMessageNotConnectedToNetwork(findViewById(android.R.id.content));
    }

    public void hideKeyboard() {
        try {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Activity.INPUT_METHOD_SERVICE);
            View view = getCurrentFocus();
            if (view == null) {
                view = new View(this);
            }
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    public void showKeyboard(View view) {
        try {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }
}