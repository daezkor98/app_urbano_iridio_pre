package com.urbanoexpress.iridio3.pre.ui.helpers;

public interface ModalInterface2 {

    default void dismissProgressDialog() { }
    default void showProgressDialog() { }
    default void showSnackBar(int messageId) { }
    default void showSnackBar(String message) { }
    default void showToast(int messageId) { }
    default void showToast(String message) { }
}
