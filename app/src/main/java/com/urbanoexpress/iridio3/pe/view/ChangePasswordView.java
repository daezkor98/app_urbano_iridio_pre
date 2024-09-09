package com.urbanoexpress.iridio3.pe.view;

public interface ChangePasswordView extends BaseView2 {

    String getTextCurrentPassword();
    String getTextNewPassword();
    String getTextRepeatPassword();

    void setErrorCurrentPassword(String error);
    void setErrorNewPassword(String error);
    void setErrorRepeatPassword(String error);

    void showMsgError(String error);
    void showModalPasswordChangedSuccess();
}
