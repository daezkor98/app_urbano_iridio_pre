package com.urbanoexpress.iridio3.pe.view;

/**
 * Created by mick on 19/05/16.
 */
public interface SplashLogInView extends BaseV5View {

    void showFormLogin();
    void animateSplashScreen();
    void setUserName(String userName);
    void showOptionsMenu();
    void setEnabledBtnLogIn(boolean enabled);

    void finishActivity();
}
