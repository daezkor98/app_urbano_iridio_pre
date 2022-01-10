package com.urbanoexpress.iridio.ui.interfaces;

public interface ActionProgressDialog {

    void showProgressDialog(String message);
    void showProgressDialog(String title, String message);
    void dismissProgressDialog();
}
