package com.urbanoexpress.iridio3.pre.presenter;

import android.content.Intent;

import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.VolleyError;
import com.urbanoexpress.iridio3.pre.util.async.AsyncTaskCoroutine;
import com.urbanoexpress.iridio3.pre.R;
import com.urbanoexpress.iridio3.pre.model.entity.Ruta;
import com.urbanoexpress.iridio3.pre.model.interactor.NotificacionesRutaInteractor;
import com.urbanoexpress.iridio3.pre.model.interactor.callback.RequestCallback;
import com.urbanoexpress.iridio3.pre.ui.model.NotificacionRutaItem;
import com.urbanoexpress.iridio3.pre.util.CommonUtils;
import com.urbanoexpress.iridio3.pre.util.Preferences;
import com.urbanoexpress.iridio3.pre.util.Session;
import com.urbanoexpress.iridio3.pre.util.constant.LocalAction;
import com.urbanoexpress.iridio3.pre.view.NotificacionesRutaView;

import org.apache.commons.lang3.text.WordUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class NotificacionesRutaPresenter {

    private NotificacionesRutaView view;
    private NotificacionesRutaInteractor interactor;
    private ArrayList<NotificacionRutaItem> notificacionRutaItems;

    public NotificacionesRutaPresenter(NotificacionesRutaView view) {
        this.view = view;
        this.interactor = new NotificacionesRutaInteractor();
    }

    public void init() {
        requestGetNotificaciones();
    }

    public void onClickItem(int position) {
        requestMarcarNotificacionComoLeida(position);
    }

    public void onSwipeRefresh() {
        requestGetNotificaciones();
    }

    private void requestGetNotificaciones() {
        view.setVisibilitySwipeRefreshLayout(true);
        if (CommonUtils.validateConnectivity(view.getViewContext())) {
            RequestCallback callback = new RequestCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    try {
                        view.setVisibilitySwipeRefreshLayout(false);
                        if (response.getBoolean("success")) {
                            handleDataNotificaciones(response.getJSONArray("data"));
                        } else {
                            view.showToast(response.getString("msg_error"));
                        }
                    } catch (JSONException ex) {
                        view.setVisibilitySwipeRefreshLayout(false);
                        ex.printStackTrace();
                        view.showToast(R.string.json_object_exception);
                    }
                }
                @Override
                public void onError(VolleyError error) {
                    view.setVisibilitySwipeRefreshLayout(false);
                    error.printStackTrace();
                    view.showToast(R.string.volley_error_message);
                }
            };

            String[] params = new String[] {
                    Preferences.getInstance().getString("idUsuario", ""),
                    Session.getUser().getDevicePhone()
            };

            interactor.getNotificaciones(params, callback);
        }
    }

    private void requestMarcarNotificacionComoLeida(final int position) {
        view.showProgressDialog();
        if (CommonUtils.validateConnectivity(view.getViewContext())) {
            RequestCallback callback = new RequestCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    view.dismissProgressDialog();
                    try {
                        if (response.getBoolean("success")) {
                            new TaskMarcarNotificacionLeido(position).execute();
                        } else {
                            view.showToast(response.getString("msg_error"));
                        }
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                        view.showToast(R.string.json_object_exception);
                    }
                }
                @Override
                public void onError(VolleyError error) {
                    error.printStackTrace();
                    view.dismissProgressDialog();
                    view.showToast(R.string.volley_error_message);
                }
            };

            String[] params = new String[] {
                    notificacionRutaItems.get(position).getIdNotificacion(),
                    notificacionRutaItems.get(position).getLineaNegocio(),
                    Session.getUser().getIdUsuario(),
                    Session.getUser().getDevicePhone()
            };

            interactor.requestMarcarNotificacionComoLeida(params, callback);
        }
    }

    private void handleDataNotificaciones(JSONArray data) throws JSONException {
        notificacionRutaItems = new ArrayList<>();

        for (int i = 0; i < data.length(); i++) {
            NotificacionRutaItem item = new NotificacionRutaItem(
                    data.getJSONObject(i).getString("id_notify"),
                    data.getJSONObject(i).getString("id_servicio"),
                    data.getJSONObject(i).getString("guia"),
                    data.getJSONObject(i).getString("titulo"),
                    data.getJSONObject(i).getString("texto"),
                    formatFecha(data.getJSONObject(i).getString("fecha")),
                    data.getJSONObject(i).getString("linea"),
                    data.getJSONObject(i).getString("flag_gestion"),
                    Integer.parseInt(data.getJSONObject(i).getString("nro_veces")),
                    generateBackgroundColorNotify(Integer.parseInt(data.getJSONObject(i).getString("nro_veces"))),
                    generateBackgroundColorIconNotify(Integer.parseInt(data.getJSONObject(i).getString("id_servicio"))),
                    generateIconNotify(Integer.parseInt(data.getJSONObject(i).getString("id_servicio")))
            );
            notificacionRutaItems.add(item);
        }

        if (notificacionRutaItems.size() != 0) {
            view.displayNotificaciones(notificacionRutaItems);
        } else {
            view.showToast(R.string.act_notificaciones_ruta_msg_no_hay_notificaciones);

            if (Session.getUser() != null) {
                Session.getUser().setTotalNotificaciones(0);
                Session.getUser().save();
            }

            Intent intent = new Intent(LocalAction.REFRESCAR_CONTADOR_NOTIFICACIONES_ACTION);
            intent.putExtra("totalNotificaciones", "");
            LocalBroadcastManager.getInstance(view.getViewContext()).sendBroadcast(intent);
        }
    }

//    private String formatFecha(String fecha) {
//        String formatFecha = "", hora = "", am_pm = "";
//        String nombreDia, dia, nombreMes, anio;
//        try {
//            DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
//            Date date =  df.parse(fecha);
//            hora = new SimpleDateFormat("h:mm").format(date);
//            am_pm = new SimpleDateFormat("a").format(date);
//
//            nombreDia = WordUtils.capitalize(new SimpleDateFormat("EEEE").format(date));
//            dia = WordUtils.capitalize(new SimpleDateFormat("dd").format(date));
//            nombreMes = WordUtils.capitalize(new SimpleDateFormat("MMMM").format(date));
//            anio = WordUtils.capitalize(new SimpleDateFormat("yyyy").format(date));
//
//            formatFecha =  nombreDia + ", " + dia + " de " + nombreMes + " del " + anio + " a la(s) " + hora + " " + am_pm;
//        } catch (ParseException ex) {
//            ex.printStackTrace();
//            formatFecha = fecha;
//        }
//        return formatFecha;
//    }

    private String formatFecha(String fecha) {
        String formatFecha = "", hora = "", am_pm = "";
        String nombreDia, dia, nombreMes, anio;
        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date date =  df.parse(fecha);
            hora = new SimpleDateFormat("h:mm").format(date);
            am_pm = new SimpleDateFormat("a").format(date);

            nombreDia = WordUtils.capitalize(new SimpleDateFormat("EEEE").format(date));
            dia = WordUtils.capitalize(new SimpleDateFormat("d").format(date));
            nombreMes = new SimpleDateFormat("MMM").format(date);
            anio = WordUtils.capitalize(new SimpleDateFormat("yyyy").format(date));

            formatFecha =  dia + " " + nombreMes + " " + hora + " " + am_pm;
        } catch (ParseException ex) {
            ex.printStackTrace();
            formatFecha = fecha;
        }
        return formatFecha;
    }

    private int generateBackgroundColorIconNotify(int idNotify) {
        switch (idNotify) {
            case 1:
            case 5:
            default:
                return R.drawable.bg_circle_blue;
            case 2:
            case 3:
                return R.drawable.bg_circle_yellow;
            case 4:
                return R.drawable.bg_circle_red;
        }
    }

    private int generateIconNotify(int idNotify) {
        switch (idNotify) {
            case 1:
                return R.drawable.ic_tipo_envio_recoleccion_express_white;
            case 2:
                return R.drawable.ic_clock_outline_white;
            case 3:
                return R.drawable.ic_calendar_white;
            case 4:
                return R.drawable.ic_package_not_avalible_white;
            case 5:
                return R.drawable.ic_ppe_helmet_white;
            default:
                return R.drawable.ic_information_white;
        }
    }

    private int generateBackgroundColorNotify(int totalVisto) {
        if (totalVisto == 0) {
            return ContextCompat.getColor(view.getViewContext(), R.color.notify_new);
        } else {
            return ContextCompat.getColor(view.getViewContext(), android.R.color.white);
        }
    }

    private class TaskMarcarNotificacionLeido extends AsyncTaskCoroutine<String, String> {

        private Ruta guia;

        private int positionSelectedNotificacion;

        public TaskMarcarNotificacionLeido(int positionSelectedNotificacion) {
            this.positionSelectedNotificacion = positionSelectedNotificacion;
        }

        @Override
        public String doInBackground(String... strings) {
            if (notificacionRutaItems.get(positionSelectedNotificacion).getTotalLeido() == 0) {

                if (Session.getUser() != null) {
                    Session.getUser().setTotalNotificaciones(
                            Session.getUser().getTotalNotificaciones() - 1);
                    Session.getUser().save();
                }
            }

            guia = interactor.selectGuia(
                    notificacionRutaItems.get(positionSelectedNotificacion).getGuiaElectronica(),
                    notificacionRutaItems.get(positionSelectedNotificacion).getLineaNegocio());
            return null;
        }

        @Override
        public void onPostExecute(String s) {
            super.onPostExecute(s);
            if (notificacionRutaItems.get(positionSelectedNotificacion).getTotalLeido() == 0) {
                if (Session.getUser() != null) {
                    Intent intent = new Intent(LocalAction.REFRESCAR_CONTADOR_NOTIFICACIONES_ACTION);
                    intent.putExtra("totalNotificaciones", String.valueOf(
                            Session.getUser().getTotalNotificaciones()));
                    LocalBroadcastManager.getInstance(view.getViewContext()).sendBroadcast(intent);
                }

                notificacionRutaItems.get(positionSelectedNotificacion).setTotalLeido(1);
                notificacionRutaItems.get(positionSelectedNotificacion).setBackgroundColor(
                        generateBackgroundColorNotify(1));
                view.notifyItemChanged(positionSelectedNotificacion);
            }

            if (guia != null) {
                view.navigateToDetalleRutaActivity(guia);
            } else {
                view.showToast("Lo sentimos, no se encontró la guía/recolección en su ruta.");
            }
        }
    }
}