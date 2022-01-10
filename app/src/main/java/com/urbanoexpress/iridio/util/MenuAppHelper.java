package com.urbanoexpress.iridio.util;

import android.content.Context;

import com.urbanoexpress.iridio.R;
import com.urbanoexpress.iridio.ui.NotificacionesRutaActivity;
import com.urbanoexpress.iridio.ui.PlanDeViajeActivity;
import com.urbanoexpress.iridio.ui.ResumenRutaActivity;
import com.urbanoexpress.iridio.ui.ResumenRutaRuralActivity;
import com.urbanoexpress.iridio.ui.RutaActivity;
import com.urbanoexpress.iridio.ui.RutaRuralActivity;

/**
 * Created by mick on 24/05/16.
 */
public class MenuAppHelper {

    private Class<?> cls;
    private int iconRes;

    public MenuAppHelper(Class<?> cls, int iconRes) {
        this.cls = cls;
        this.iconRes = iconRes;
    }

    public Class<?> getCls() {
        return cls;
    }

    public void setCls(Class<?> cls) {
        this.cls = cls;
    }

    public int getIcon() {
        return iconRes;
    }

    public void setIcon(int iconRes) {
        this.iconRes = iconRes;
    }

    public static MenuAppHelper buildMenu(String menuClass) {
        if (menuClass.equalsIgnoreCase("plan_de_viaje")) {
            return new MenuAppHelper(PlanDeViajeActivity.class, R.drawable.ic_truck_grey);
        } else if (menuClass.equalsIgnoreCase("ruta_del_dia")) {
            return new MenuAppHelper(RutaActivity.class, R.drawable.ic_truck_grey);
        } else if (menuClass.equalsIgnoreCase("ruta_gestor")) {
            return new MenuAppHelper(RutaRuralActivity.class, R.drawable.ic_truck_grey);
        } else if (menuClass.equalsIgnoreCase("resumen_ruta")) {
            return new MenuAppHelper(ResumenRutaActivity.class, R.drawable.ic_library_books_grey);
        } else if (menuClass.equalsIgnoreCase("ruta_express")) {
            return new MenuAppHelper(ResumenRutaRuralActivity.class, R.drawable.ic_library_books_grey);
        } else if (menuClass.equalsIgnoreCase("notificaciones")) {
            return new MenuAppHelper(NotificacionesRutaActivity.class, R.drawable.ic_bell_grey);
        } else {
            return new MenuAppHelper(null, R.drawable.ic_code_tags_grey);
        }
    }

    public static String buildDescription(Context context, String menuClass) {
        if (menuClass.equalsIgnoreCase("plan_de_viaje")) {
            return context.getString(R.string.text_menu_plan_de_viaje);
        } else if (menuClass.equalsIgnoreCase("ruta_del_dia")
                || menuClass.equalsIgnoreCase("ruta_gestor")) {
            return context.getString(R.string.text_menu_ruta_del_dia);
        } else if (menuClass.equalsIgnoreCase("resumen_ruta")) {
            return context.getString(R.string.text_menu_resumen_ruta);
        } else if (menuClass.equalsIgnoreCase("ruta_express")) {
            return context.getString(R.string.text_menu_ruta_del_dia);
        } else if (menuClass.equalsIgnoreCase("notificaciones")) {
            return context.getString(R.string.text_menu_notificaciones);
        } else {
            return "No definido.";
        }
    }

    public static int builIDResIconLinear(String menuClass) {
        if (menuClass.equalsIgnoreCase("plan_de_viaje")) {
            return R.drawable.ic_linear_truck;
        } else if (menuClass.equalsIgnoreCase("ruta_del_dia")
                || menuClass.equalsIgnoreCase("ruta_gestor")) {
            return R.drawable.ic_linear_car;
        } else if (menuClass.equalsIgnoreCase("resumen_ruta")) {
            return R.drawable.ic_linear_clipboard;
        } else if (menuClass.equalsIgnoreCase("ruta_express")) {
            return R.drawable.ic_linear_car;
        } else if (menuClass.equalsIgnoreCase("notificaciones")) {
            return R.drawable.ic_linear_alarm;
        } else {
            return 0;
        }
    }

    public static boolean isValidMenu(String menuClass) {
        if (menuClass.equalsIgnoreCase("plan_de_viaje")) {
            return true;
        } else if (menuClass.equalsIgnoreCase("ruta_del_dia")) {
            return true;
        } else if (menuClass.equalsIgnoreCase("ruta_gestor")) {
            return true;
        } else if (menuClass.equalsIgnoreCase("resumen_ruta")) {
            return true;
        } else if (menuClass.equalsIgnoreCase("ruta_express")) {
            return true;
        } else if (menuClass.equalsIgnoreCase("notificaciones")) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isMenuNotificaciones(Class<?> mClass) {
        return mClass == NotificacionesRutaActivity.class;
    }
}
