package com.urbanoexpress.iridio3.pe.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.fragment.app.Fragment;

import com.urbanoexpress.iridio3.pe.util.network.Connection;

public abstract class BaseFragment2 extends Fragment {

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
