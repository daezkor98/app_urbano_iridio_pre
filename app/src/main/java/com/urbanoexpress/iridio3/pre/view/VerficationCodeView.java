package com.urbanoexpress.iridio3.pre.view;

import androidx.fragment.app.Fragment;

public interface VerficationCodeView extends BaseView2 {
    void showMessageNotConnectedToNetwork();
    void replaceFragment(Fragment fragment, String tag);
}
