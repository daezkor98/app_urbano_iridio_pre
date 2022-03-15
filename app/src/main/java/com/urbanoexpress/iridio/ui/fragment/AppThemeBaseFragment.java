package com.urbanoexpress.iridio.ui.fragment;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.urbanoexpress.iridio.R;
import com.urbanoexpress.iridio.application.AndroidApplication;
import com.urbanoexpress.iridio.ui.helpers.ModalHelper;
import com.urbanoexpress.iridio.view.BaseView2;

public abstract class AppThemeBaseFragment extends BaseFragment2 implements BaseView2 {

    @Override
    public void dismissProgressDialog() {
        try {
            getViewProgress().setVisibility(View.GONE);

        } catch (NullPointerException ex) {
            Log.e("TAG", "dismissProgressDialog: ",ex );
            ex.printStackTrace();
        }
    }

    @Override
    public void showProgressDialog() {
        try {
            getViewProgress().setVisibility(View.VISIBLE);
        } catch (NullPointerException ex) {
            Log.e("TAG", "showProgressDialog: ",ex );
            ex.printStackTrace();
        }
    }

    protected View getViewProgress() {
        return getView().findViewById(R.id.progress_layout).findViewById(R.id.progress_layout);
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

    @Override
    public Context getViewContext() {
        return getActivity();
    }
}
