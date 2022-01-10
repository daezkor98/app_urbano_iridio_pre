package com.urbanoexpress.iridio.view;

import androidx.fragment.app.Fragment;

public interface VerficationCodeView extends BaseView2 {

    String getTextCode1();
    String getTextCode2();
    String getTextCode3();
    String getTextCode4();
    String getTextCode5();
    String getTextCode6();

    void setTextCode1(String text);
    void setTextCode2(String text);
    void setTextCode3(String text);
    void setTextCode4(String text);
    void setTextCode5(String text);
    void setTextCode6(String text);

    void setHtmlLblMsg(String html);

    void setEnabledButtonNext(boolean enabled);

    void hideKeyboard();

    void showMessageNotConnectedToNetwork();

    void replaceFragment(Fragment fragment, String tag);
}
