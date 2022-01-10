package com.urbanoexpress.iridio.ui;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import com.urbanoexpress.iridio.R;
import com.urbanoexpress.iridio.application.AndroidApplication;
import com.urbanoexpress.iridio.ui.helpers.ModalHelper;
import com.urbanoexpress.iridio.ui.helpers.OnBackButtonPressedListener;
import com.urbanoexpress.iridio.view.BaseView2;

public abstract class AppThemeBaseActivity extends BaseActivity2 implements BaseView2,
        OnBackButtonPressedListener {

    @Override
    public void finish() {
        super.finish();
        animOnLeaveActivity();
    }

    protected void animOnLeaveActivity() {
        overridePendingTransition(R.anim.slide_enter_from_left, R.anim.slide_exit_out_right);
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        animOnStartActivity();
    }

    protected void animOnStartActivity() {
        overridePendingTransition(R.anim.slide_enter_from_right, R.anim.slide_exit_out_left);
    }

    @Override
    public void onBackPressed() {
        try {
            if (!onBackButtonPressed() && getViewProgress().getVisibility() != View.VISIBLE) {
                super.onBackPressed();
            }
        } catch (NullPointerException ex) {
            if (!onBackButtonPressed()) {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onBackButtonPressed() {
        return false;
    }

    @Override
    public Context getViewContext() {
        return this;
    }

    @Override
    public void dismissProgressDialog() {
        try {
            getViewProgress().setVisibility(View.GONE);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void showProgressDialog() {
        try {
            getViewProgress().setVisibility(View.VISIBLE);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    protected View getViewProgress() {
        return findViewById(R.id.progress_layout).findViewById(R.id.progress_layout);
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

}
