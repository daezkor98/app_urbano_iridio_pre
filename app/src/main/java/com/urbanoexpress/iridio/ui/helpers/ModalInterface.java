package com.urbanoexpress.iridio.ui.helpers;

public interface ModalInterface {

    default void dismissProgressDialog() { }
    default void showProgressDialog(int messageId) { }
    default void showProgressDialog(int titleId, int messageId) { }
    default void showProgressDialog(String message) { }
    default void showProgressDialog(String title, String message) { }
    default void showSnackBar(int messageId) { }
    default void showSnackBar(String message) { }
    default void showToast(int messageId) { }
    default void showToast(String message) { }
}