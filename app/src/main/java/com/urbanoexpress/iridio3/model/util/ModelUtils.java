package com.urbanoexpress.iridio3.model.util;

import android.content.Context;
import android.graphics.Color;

import androidx.core.content.ContextCompat;

import com.urbanoexpress.iridio3.R;
import com.urbanoexpress.iridio3.application.AndroidApplication;
import com.urbanoexpress.iridio3.data.local.PreferencesHelper;
import com.urbanoexpress.iridio3.model.entity.MotivoDescarga;
import com.urbanoexpress.iridio3.model.entity.Ruta;
import com.urbanoexpress.iridio3.util.CommonUtils;
import com.urbanoexpress.iridio3.util.constant.Country;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by mick on 25/10/16.
 */

public class ModelUtils {

    public static String getNameLineaNegocio(String lineaNegocio) {
        try {
            Integer linea = Integer.parseInt(lineaNegocio);

            switch (linea) {
                case 1:
                    return "Postal";
                case 2:
                    return "Valorados";
                case 3:
                    return "Logística";
                case 4:
                    return "Logística Especial";
                default:
                    return "Desconocido";
            }
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
            return "Desconocido";
        }
    }

    public static int getTipoGuia(String tipo) {
        switch (tipo.toUpperCase()) {
            case "E":
                return Ruta.Tipo.ENTREGA;
            case "R":
                return Ruta.Tipo.RECOLECCION;
            default:
                return Ruta.Tipo.NO_DEFINIDO;
        }
    }

    public static boolean isGuiaEntrega(String tipo) {
        return (getTipoGuia(tipo) == Ruta.Tipo.ENTREGA);
    }

    public static boolean isGuiaRecoleccion(String tipo) {
        return (getTipoGuia(tipo) == Ruta.Tipo.RECOLECCION);
    }

    public static boolean isGuiaRecoleccionExpress(String tipo, String tipoEnvio) {
        return isGuiaRecoleccion(tipo)
                && tipoEnvio.toUpperCase().equals(Ruta.TipoEnvio.RECOLECCION_EXPRESS);
    }

    public static boolean isTipoEnvioValija(String tipoEnvio) {
        try {
            tipoEnvio = tipoEnvio.toUpperCase();
            if (tipoEnvio.equals(Ruta.TipoEnvio.VALIJA)) {
                return true;
            }
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public static boolean validateTipoEnvio(String tipoEnvio, String tipoEnvioValidador) {
        try {
            tipoEnvio = tipoEnvio.toUpperCase();
            if (tipoEnvio.equals(tipoEnvioValidador)) {
                return true;
            }
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public static int getTipoMotivoDescarga(String tipoEnvio, int tipoGestion) {
        // tipoGestion => 1 (Entrega), 2 (No Entrega)
        if (tipoGestion == 1) { // Entrega
            switch (tipoEnvio.toUpperCase()) {
                case Ruta.TipoEnvio.PAQUETE:
                case "E":
                case Ruta.TipoEnvio.VALIJA:
                case Ruta.TipoEnvio.LOGISTICA_INVERSA:
                    return MotivoDescarga.Tipo.ENTREGA;
                case Ruta.TipoEnvio.LIQUIDACION:
                    return MotivoDescarga.Tipo.ENTREGA_LIQUIDACION;
                case Ruta.TipoEnvio.DEVOLUCION:
                    return MotivoDescarga.Tipo.ENTREGA_DEVOLUCION;
                default:
                    return 0;
            }
        } else { // No Entrega
            switch (tipoEnvio.toUpperCase()) {
                case Ruta.TipoEnvio.PAQUETE:
                case "E":
                case Ruta.TipoEnvio.VALIJA:
                case Ruta.TipoEnvio.LOGISTICA_INVERSA:
                    return MotivoDescarga.Tipo.NO_ENTREGA;
                case Ruta.TipoEnvio.LIQUIDACION:
                    return MotivoDescarga.Tipo.NO_ENTREGA_LIQ_DEV;
                case Ruta.TipoEnvio.DEVOLUCION:
                    return MotivoDescarga.Tipo.NO_ENTREGA_LIQ_DEV;
                default:
                    return 0;
            }
        }
    }

    public static int getIconTipoGuia(Ruta guia) {
        if (isGuiaEntrega(guia.getTipo())) {
            switch (guia.getTipoEnvio().toUpperCase()) {
                case Ruta.TipoEnvio.VALIJA:
                    return R.drawable.ic_tipo_guia_valija;
                case Ruta.TipoEnvio.LIQUIDACION:
                    return R.drawable.ic_tipo_guia_liquidacion;
                case Ruta.TipoEnvio.DEVOLUCION:
                    return R.drawable.ic_tipo_guia_paquete_devolucion;
                case Ruta.TipoEnvio.LOGISTICA_INVERSA:
                    return R.drawable.ic_tipo_guia_paquete_logistica_inversa;
                default:
                    return R.drawable.ic_tipo_guia_paquete;
            }
        } else if (isGuiaRecoleccion(guia.getTipo())) {
            if (Ruta.TipoEnvio.VALIJA.equals(guia.getTipoEnvio().toUpperCase())) {
                return R.drawable.ic_tipo_guia_valija_recoleccion;
            } else {
                return R.drawable.ic_tipo_guia_recoleccion;
            }
        }

        return -1;
    }

    public static int getIconLineaNegocio(Ruta guia) {
        int idResIcon = -1;

        if (guia.getLineaNegocio().equals("1")) {
            idResIcon =  R.drawable.ic_linea_postal;
        } else if (guia.getLineaNegocio().equals("2")) {
            idResIcon =  R.drawable.ic_linea_valorados;
        } else if (guia.getLineaNegocio().equals("3")) {
            idResIcon =  R.drawable.ic_linea_logistica;
        } else {
            idResIcon =  R.drawable.ic_linea_logistica_especial;
        }

        if (ModelUtils.isGuiaRecoleccionExpress(guia.getTipo(), guia.getTipoEnvio())) {
            idResIcon = R.drawable.ic_linea_logistica_recoleccion_express;
        }

        if (guia.getEstadoShipper() != null) {
            if (guia.getEstadoShipper().equals("4")) {
                idResIcon = R.drawable.ic_estado_cliente_critico;
            }
        }

        return idResIcon;
    }

    public static int getIconTipoEnvio(Ruta guia) {
        int idResIcon = -1;

        if (guia.getGuiaRequerimiento() != null) {
            if (guia.getGuiaRequerimiento().equals("1")) {
                if (guia.getGuiaRequerimientoCHK() != null) {
                    if (guia.getGuiaRequerimientoCHK().equals("22")) {
                        idResIcon = R.drawable.ic_calendar_red;
                    } else if (guia.getGuiaRequerimientoCHK().equals("30")) {
                        idResIcon = R.drawable.ic_package_not_avalible;
                    }
                } else {
                    idResIcon = R.drawable.ic_calendar_red; // Icono a modo de compatibilidad a versiones de iridio que no soportan requirimiento chk.
                }
            }
        }

        return idResIcon;
    }

    public static int getIconEstadoShipper(Ruta guia) {
        if (guia.getEstadoShipper() != null) {
            if (guia.getEstadoShipper().equals("1")) { // Inicial
                return R.drawable.ic_estado_cliente_inicial;
            } else if (guia.getEstadoShipper().equals("2")) { // Normal
                return 0;
            } else if (guia.getEstadoShipper().equals("3")) { // Vip
                return R.drawable.ic_estado_cliente_vip;
            } else if (guia.getEstadoShipper().equals("4")) { // Critico
                return R.drawable.ic_estado_cliente_critico;
            }
        }

        return 0;
    }

    public static int getBackgroundColorGE(String tipoEnvio, Context context) {
        if (context != null) {
            switch (tipoEnvio.toUpperCase()) {
                case Ruta.TipoEnvio.VALIJA:
                    return ContextCompat.getColor(context, R.color.oro_valija);
                case Ruta.TipoEnvio.DEVOLUCION:
                    return ContextCompat.getColor(context, R.color.rojo_devolucion);
                default:
                    return ContextCompat.getColor(context, R.color.lightPrimaryText);
            }
        } else {
            return Color.WHITE;
        }
    }

    public static int getLblColorHorario(String tipo, String tipoEnvio, String fechaRuta, String horario) {
        Context context = AndroidApplication.getAppContext();
        if (context != null) {
            int color = ContextCompat.getColor(context, R.color.gris_2);

            if (ModelUtils.isGuiaRecoleccion(tipo)) {
                if (horario.trim().matches("^\\d\\d:\\d\\d a \\d\\d:\\d\\d$")) {
                    String[] horarios = horario.trim().split("a");
                    String strHoraDesde = horarios[0].trim();
                    String strHoraHasta = horarios[1].trim();

                    try {
                        String strHoraActual = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date());
                        Date horaActual = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(strHoraActual);
                        Date dateHoraDesde = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(fechaRuta + " " + strHoraDesde);
                        Date dateHorahasta = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(fechaRuta + " " + strHoraHasta);

                    /*Calendar calendar = Calendar.getInstance();
                    calendar.setTime(dateHorahasta);
                    calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) - 10);

                    dateHorahasta.setTime(calendar.getTimeInMillis());*/

                        if (horaActual.before(dateHoraDesde)) {
                            color = ContextCompat.getColor(context, R.color.green_4);
                        } else if (horaActual.getTime() >= dateHoraDesde.getTime() &&
                                horaActual.getTime() <= dateHorahasta.getTime()) {
                            color = ContextCompat.getColor(context, R.color.yellow_2);
                        } else if (horaActual.after(dateHorahasta)) {
                            color = ContextCompat.getColor(context, R.color.colorPrimary);
                        }
                    } catch (ParseException ex) {
                        ex.printStackTrace();
                    }
                }
            }

            return color;
        }

        return 0;
    }

    public static boolean isShowIconImportePorCobrar(String importe) {
        return CommonUtils.parseDouble(importe) > 0;
    }

    public static boolean hasGuiaReqDevolucionShipper(Ruta guia) {
        if (guia.getGuiaRequerimiento() != null) {
            if (guia.getGuiaRequerimiento().equals("1")) {
                if (guia.getGuiaRequerimientoCHK() != null) {
                    if (guia.getGuiaRequerimientoCHK().equals("30")) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public static String getNameLblCargoGuia(Context context) {
        int country = new PreferencesHelper(context).getCountry();
        switch (country) {
            case Country.ECUADOR:
                return "Documento";
            case Country.PERU:
                return "Cargo";
            case Country.CHILE:
                return "Cedible";
        }
        return "";
    }

    public static String getSimboloMoneda(Context context) {
        try {
            int country = new PreferencesHelper(context).getCountry();
            if (country == Country.CHILE) {
                return "$";
            } else if (country == Country.PERU) {
                return "S/";
            }
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
        return "";
    }

}
