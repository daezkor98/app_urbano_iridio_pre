package com.urbanoexpress.iridio3.view;

public interface ConfigPhoneView extends BaseView2 {

    String getTextPhone();

    void setEnabledButtonNext(boolean enabled);
    void setErrorPhone(String error);
    void setHintPhone(String hint);

    void setTextPhonePrefix(String text);

    void setIconFlag(int resId);

    void requestHint();

    void showMessageNotConnectedToNetwork();

    void navigateToChoiseCountryBottomSheet();
    void navigateToVerficationCodeFragment(String isoCountry, String phone, String firebaseToken, Boolean isGoogleMock);
}
