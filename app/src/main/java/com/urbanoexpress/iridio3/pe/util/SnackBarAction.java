package com.urbanoexpress.iridio3.pe.util;

import android.view.View;

/**
 * Created by mick on 30/05/16.
 */
public class SnackBarAction {
    private String text;
    private View.OnClickListener clickListener;

    public SnackBarAction(String text, View.OnClickListener clickListener) {
        this.text = text;
        this.clickListener = clickListener;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public View.OnClickListener getClickListener() {
        return clickListener;
    }

    public void setClickListener(View.OnClickListener clickListener) {
        this.clickListener = clickListener;
    }
}