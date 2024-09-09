package com.urbanoexpress.iridio3.pe.ui;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.urbanoexpress.iridio3.pe.util.network.Connection;

public abstract class BaseActivity2 extends AppCompatActivity {

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
        getSupportActionBar().setTitle("");
//        toolbar.setNavigationOnClickListener(v -> finish());
        toolbar.setNavigationOnClickListener(v -> onBackPressed());//TODO check change
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