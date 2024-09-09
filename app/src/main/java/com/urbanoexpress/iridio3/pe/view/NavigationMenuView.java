package com.urbanoexpress.iridio3.pe.view;

import com.urbanoexpress.iridio3.pe.model.NavigationMenuModel;

import java.util.List;

/**
 * Created by mick on 21/06/16.
 */
public interface NavigationMenuView extends BaseView2 {

    void setDrawerHeader(String typeUser, String userName);
    void showMainMenu(List<NavigationMenuModel> menus);
    void notifyMainMenuItemChanged(int position);
    void initializeServices();
    void setValidateFeatures(boolean validateFeatures);
    void validateFeatures();
    void addMenuSideMenu(int groupId, int itemId, int order, String title, int iconRes);

    void navigateToMenu(Class<?> cls);
    void navigateToMenu(Class<?> cls, String moduleName);

    void showDialogLogout();
    void showMessageMenuNotAvailible();
}
