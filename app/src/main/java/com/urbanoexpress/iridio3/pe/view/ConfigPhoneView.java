package com.urbanoexpress.iridio3.pe.view;

public interface ConfigPhoneView extends BaseView2 {
    String getTextPhone();

    void showMessageNotConnectedToNetwork();

    void navigateToVerficationCodeFragment(String codePhone, String phone);
}
