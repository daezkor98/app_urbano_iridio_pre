package com.urbanoexpress.iridio.util;

import android.content.Context;

import com.urbanoexpress.iridio.R;
import com.urbanoexpress.iridio.ui.MisGananciasActivity;
import com.urbanoexpress.iridio.ui.NotificacionesRutaActivity;
import com.urbanoexpress.iridio.ui.PlanDeViajeActivity;
import com.urbanoexpress.iridio.ui.ResumenRutaActivity;
import com.urbanoexpress.iridio.ui.ResumenRutaRuralActivity;
import com.urbanoexpress.iridio.ui.RutaActivity;
import com.urbanoexpress.iridio.ui.RutaRuralActivity;
import com.urbanoexpress.iridio.urbanocore.values.Menu;

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
        if (menuClass.equalsIgnoreCase(Menu.PLAN_DE_VIAJE)) {
            return new MenuAppHelper(PlanDeViajeActivity.class, R.drawable.ic_truck_grey);
        } else if (menuClass.equalsIgnoreCase(Menu.RUTA_DEL_DIA)) {
            return new MenuAppHelper(RutaActivity.class, R.drawable.ic_truck_grey);
        } else if (menuClass.equalsIgnoreCase(Menu.RUTA_GESTOR)) {
            return new MenuAppHelper(RutaRuralActivity.class, R.drawable.ic_truck_grey);
        } else if (menuClass.equalsIgnoreCase(Menu.RESUMEN_RUTA)) {
            return new MenuAppHelper(ResumenRutaActivity.class, R.drawable.ic_library_books_grey);
        } else if (menuClass.equalsIgnoreCase(Menu.RUTA_EXPRESS)) {
            return new MenuAppHelper(ResumenRutaRuralActivity.class, R.drawable.ic_library_books_grey);
        } else if (menuClass.equalsIgnoreCase(Menu.NOTIFICACIONES)) {
            return new MenuAppHelper(NotificacionesRutaActivity.class, R.drawable.ic_bell_grey);
        } else if (menuClass.equalsIgnoreCase(Menu.MIS_GANANCIAS)) {
            return new MenuAppHelper(MisGananciasActivity.class, R.drawable.ic_coin);
        } else {
            return new MenuAppHelper(null, R.drawable.ic_code_tags_grey);
        }
    }

    public static String buildDescription(Context context, String menuClass) {
        if (menuClass.equalsIgnoreCase(Menu.PLAN_DE_VIAJE)) {
            return context.getString(R.string.text_menu_plan_de_viaje);
        } else if (menuClass.equalsIgnoreCase(Menu.RUTA_DEL_DIA)
                || menuClass.equalsIgnoreCase(Menu.RUTA_GESTOR)) {
            return context.getString(R.string.text_menu_ruta_del_dia);
        } else if (menuClass.equalsIgnoreCase(Menu.RESUMEN_RUTA)) {
            return context.getString(R.string.text_menu_resumen_ruta);
        } else if (menuClass.equalsIgnoreCase(Menu.RUTA_EXPRESS)) {
            return context.getString(R.string.text_menu_ruta_del_dia);
        } else if (menuClass.equalsIgnoreCase(Menu.NOTIFICACIONES)) {
            return context.getString(R.string.text_menu_notificaciones);
        } else if (menuClass.equalsIgnoreCase(Menu.MIS_GANANCIAS)) {
            return context.getString(R.string.text_menu_ganancias);
        } else {
            return "No definido.";
        }
    }

    public static int builIDResIconLinear(String menuClass) {
        if (menuClass.equalsIgnoreCase(Menu.PLAN_DE_VIAJE)) {
            return R.drawable.ic_linear_truck;
        } else if (menuClass.equalsIgnoreCase(Menu.RUTA_DEL_DIA)
                || menuClass.equalsIgnoreCase(Menu.RUTA_GESTOR)) {
            return R.drawable.ic_linear_car;
        } else if (menuClass.equalsIgnoreCase(Menu.RESUMEN_RUTA)) {
            return R.drawable.ic_linear_clipboard;
        } else if (menuClass.equalsIgnoreCase(Menu.RUTA_EXPRESS)) {
            return R.drawable.ic_linear_car;
        } else if (menuClass.equalsIgnoreCase(Menu.NOTIFICACIONES)) {
            return R.drawable.ic_linear_alarm;
        } else if (menuClass.equalsIgnoreCase(Menu.MIS_GANANCIAS)) {
            return R.drawable.coin;
        } else {
            return 0;
        }
    }

    public static boolean isValidMenu(String menuClass) {
        if (menuClass.equalsIgnoreCase(Menu.PLAN_DE_VIAJE)) {
            return true;
        } else if (menuClass.equalsIgnoreCase(Menu.RUTA_DEL_DIA)) {
            return true;
        } else if (menuClass.equalsIgnoreCase(Menu.RUTA_GESTOR)) {
            return true;
        } else if (menuClass.equalsIgnoreCase(Menu.RESUMEN_RUTA)) {
            return true;
        } else if (menuClass.equalsIgnoreCase(Menu.RUTA_EXPRESS)) {
            return true;
        } else if (menuClass.equalsIgnoreCase(Menu.NOTIFICACIONES)) {
            return true;
        } else if (menuClass.equalsIgnoreCase(Menu.MIS_GANANCIAS)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isMenuNotificaciones(Class<?> mClass) {
        return mClass == NotificacionesRutaActivity.class;
    }
}
