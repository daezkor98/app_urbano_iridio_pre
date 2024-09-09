package com.urbanoexpress.iridio3.pe.presenter;

import static com.urbanoexpress.iridio3.pe.model.NavigationMenuModel.TypeAction;

import android.util.Log;

import com.urbanoexpress.iridio3.pe.util.async.AsyncTaskCoroutine;
import com.urbanoexpress.iridio3.R;
import com.urbanoexpress.iridio3.pe.model.NavigationMenuModel;
import com.urbanoexpress.iridio3.pe.model.entity.MenuApp;
import com.urbanoexpress.iridio3.pe.ui.ConfiguracionActivity;
import com.urbanoexpress.iridio3.pe.ui.UserProfileActivity;
import com.urbanoexpress.iridio3.pe.util.MenuAppHelper;
import com.urbanoexpress.iridio3.pe.util.Preferences;
import com.urbanoexpress.iridio3.pe.util.Session;
import com.urbanoexpress.iridio3.pe.view.NavigationMenuView;

import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mick on 21/06/16.
 */
public class NavigationMenuPresenter {

    private static final String TAG = NavigationMenuPresenter.class.getSimpleName();

    private NavigationMenuView view;
    private List<MenuApp> menuAppDB;
    private ArrayList<NavigationMenuModel> sideMenuItems;
    private ArrayList<NavigationMenuModel> menuItems;

    public NavigationMenuPresenter(NavigationMenuView view) {
        this.view = view;

        Preferences.getInstance().init(view.getViewContext(), "UserProfile");
    }

    public void init() {
        setDrawerHeader();
        new QueryDrawerMenuTask().execute();
    }

    private void setDrawerHeader() {
        String tipoUsuario = Preferences.getInstance()
                .getString("tipoUsuario", "")
                .equals("I") ? "Interno" : "Externo";

        String nombreUsuario = "";

        if (Preferences.getInstance().getString("nombre", "").length() > 0) {
            nombreUsuario = WordUtils.capitalize(Preferences.getInstance().getString("nombre", "").toLowerCase());
        } else {
            nombreUsuario = Preferences.getInstance().getString("usuario", "");
        }

        view.setDrawerHeader(tipoUsuario, nombreUsuario);
    }

    private void buildSideMenu() {

        MenuAppHelper menuAppHelper;
        sideMenuItems = new ArrayList<>();

        for (int i = 0, idItem = 0; i < menuAppDB.size(); i++, idItem++) {
            menuAppHelper = MenuAppHelper.buildMenu(menuAppDB.get(i).getMenuClass());

            view.addMenuSideMenu(1, idItem, idItem, menuAppDB.get(i).getNombre(),
                    menuAppHelper.getIcon());

            sideMenuItems.add(new NavigationMenuModel.Builder(TypeAction.INTENT, menuAppHelper.getCls())
                    .setTitle(menuAppDB.get(i).getNombre())
                    .setIcon(menuAppHelper.getIcon())
                    .build());
        }

        addDefaultSideMenu();

        Log.d(TAG, "TOTAL DB MENU: " + menuAppDB.size());
        Log.d(TAG, "TOTAL ITEMS MENU: " + sideMenuItems.size());
    }

    public void onItemSideMenuClick(int position) {
        Log.d(TAG, "position: " + position);
        Log.d(TAG, view.getViewContext().getPackageName());
        if (position < 50) {
            switch (sideMenuItems.get(position).getTypeAction()) {
                case NavigationMenuModel.TypeAction.INTENT:
                    try {
                        Class<?> cls = Class.forName(view.getViewContext().getPackageName() + ".ui." +
                                sideMenuItems.get(position).getCls().getSimpleName());
                        view.navigateToMenu(cls, sideMenuItems.get(position).getTitle());
                    } catch (ClassNotFoundException ex) {
                        ex.printStackTrace();
                        view.showMessageMenuNotAvailible();
                    } catch (NullPointerException ex) {
                        ex.printStackTrace();
                        view.showMessageMenuNotAvailible();
                    }
                    break;
                case NavigationMenuModel.TypeAction.CLOSE_SESSION:
                    view.showDialogLogout();
                    break;
            }
        } else {
            if (position == 50) {
                view.navigateToMenu(UserProfileActivity.class);
            }
        }
    }

    public void onItemMenuClick(int position) {
        try {
            Class<?> cls = Class.forName(view.getViewContext().getPackageName() + ".ui." +
                    menuItems.get(position).getCls().getSimpleName());
            view.navigateToMenu(cls, menuItems.get(position).getTitle());
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            view.showMessageMenuNotAvailible();
        }
    }

    private void addDefaultSideMenu() {

        int indexMenu = sideMenuItems.size();

        view.addMenuSideMenu(2, indexMenu, indexMenu, "Configuración",
                R.drawable.ic_settings_grey);

        sideMenuItems.add(new NavigationMenuModel.Builder(TypeAction.INTENT, ConfiguracionActivity.class)
                .setTitle("Configuración")
                .setIcon(R.drawable.ic_settings_grey)
                .build());

        indexMenu = sideMenuItems.size();

        view.addMenuSideMenu(2, indexMenu, indexMenu, "Cerrar sesión",
                R.drawable.ic_logout_grey);

        sideMenuItems.add(new NavigationMenuModel.Builder(TypeAction.CLOSE_SESSION)
                .setTitle("Cerrar Sesión")
                .setIcon(R.drawable.ic_logout_grey)
                .build());
    }

    public void updateBadgeTotalNotifications() {
        for (int i = 0; i < menuItems.size(); i++) {
            if (MenuAppHelper.isMenuNotificaciones(menuItems.get(i).getCls())) {
                menuItems.get(i).setBadgeText(String.valueOf(Session.getUser().getTotalNotificaciones()));
                view.notifyMainMenuItemChanged(i);
                break;
            }
        }
    }

    //Task
    private class QueryDrawerMenuTask extends AsyncTaskCoroutine<String, String> {
        @Override
        public String doInBackground(String... strings) {
            Session.getUser();
            menuItems = new ArrayList<>();
            menuAppDB = MenuApp.listAll(MenuApp.class);

            // Here we transform MenuApp register to a NavigationMenuModel
            for (int i = 0; i < menuAppDB.size(); i++) {

                if (MenuAppHelper.isValidMenu(menuAppDB.get(i).getMenuClass())) {

                    MenuAppHelper menuAppHelper = MenuAppHelper.buildMenu(menuAppDB.get(i).getMenuClass());

                    String badge = "";

                    if (MenuAppHelper.isMenuNotificaciones(menuAppHelper.getCls())) {
                        int totalNotificaciones = 0;

                        if (Session.getUser() != null) {
                            totalNotificaciones = Session.getUser().getTotalNotificaciones();
                        }

                        if (totalNotificaciones > 0) {
                            badge = String.valueOf(totalNotificaciones);
                        }
                    }

                    menuItems.add(new NavigationMenuModel.Builder(TypeAction.INTENT, menuAppHelper.getCls())
                            .setTitle(menuAppDB.get(i).getNombre())
                            .setDescription(MenuAppHelper.buildDescription(view.getViewContext(), menuAppDB.get(i).getMenuClass()))
                            .setBadgeText(badge)
                            .setIcon(MenuAppHelper.builIDResIconLinear(menuAppDB.get(i).getMenuClass()))
                            .build());
                }
            }

            return null;
        }

        @Override
        public void onPostExecute(String s) {
            super.onPostExecute(s);
            view.showMainMenu(menuItems);
            buildSideMenu();
            view.initializeServices();
            view.setValidateFeatures(true);
            view.validateFeatures();
        }
    }

}
