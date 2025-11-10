package com.urbanoexpress.iridio3.pre.presenter;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import com.urbanoexpress.iridio3.pre.R;
import com.urbanoexpress.iridio3.pre.model.entity.ResumenRuta;
import com.urbanoexpress.iridio3.pre.model.interactor.MisGananciasInteractor;
import com.urbanoexpress.iridio3.pre.model.interactor.ResumenRutaInteractor;
import com.urbanoexpress.iridio3.pre.model.interactor.callback.RequestCallback;
import com.urbanoexpress.iridio3.pre.util.DateUtils;
import com.urbanoexpress.iridio3.pre.util.Preferences;
import com.urbanoexpress.iridio3.pre.util.Session;
import com.urbanoexpress.iridio3.pre.view.ResumenRutaView;

/**
 * Created by mick on 11/07/16.
 */
public class ResumenRutaPresenter {

    private ResumenRutaView view;
    private ResumenRutaInteractor resumenRutaInteractor;

    public ResumenRutaPresenter(ResumenRutaView view) {
        this.view = view;
        this.resumenRutaInteractor = new ResumenRutaInteractor(view.getViewContext());
    }

    public void init() {
        getResumenRuta();
    }

    public void onSwipeRefresh() {
        getResumenRuta();
    }

    private void getResumenRuta() {
        view.setVisibilitySwipeRefreshLayout(true);
        RequestCallback callback = new RequestCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if (response.getBoolean("success")) {
                        saveResumenRuta(response.getJSONArray("data"));
                        showResumenRuta();
                        view.setVisibilitySwipeRefreshLayout(false);
                    } else {
                        view.setVisibilitySwipeRefreshLayout(false);
                        view.showToast(response.getString("msg_error"));
                    }
                } catch (JSONException ex) {
                    ex.printStackTrace();
                    view.setVisibilitySwipeRefreshLayout(false);
                    view.showToast(R.string.json_object_exception);
                }
            }

            @Override
            public void onError(VolleyError error) {
                view.setVisibilitySwipeRefreshLayout(false);
                view.showToast(error.getMessage());
            }
        };

        String[] params = {
                Preferences.getInstance().getString("idUsuario", ""),
                Session.getUser().getDevicePhone(),
                "3"
        };

        resumenRutaInteractor.getResumenRuta(params, callback);
    }

    private void saveResumenRuta(JSONArray data) throws JSONException {
        if (data.length() > 0) {
            ResumenRuta.deleteAll(ResumenRuta.class);
            JSONObject jsonResumenRuta = data.getJSONObject(0);
            ResumenRuta resumenRuta = new ResumenRuta(
                    jsonResumenRuta.getString("id_ruta"),
                    jsonResumenRuta.getString("id_zona"),
                    jsonResumenRuta.getString("nombre_zona"),
                    jsonResumenRuta.getString("placa"),
                    jsonResumenRuta.getString("courier"),
                    jsonResumenRuta.getString("chofer"),
                    jsonResumenRuta.getString("tot_guias"),
                    jsonResumenRuta.getString("tot_piezas"),
                    jsonResumenRuta.getString("peso_seco"),
                    jsonResumenRuta.getString("volumen"),
                    jsonResumenRuta.getString("cod_cash_guias"),
                    jsonResumenRuta.getString("cod_cash_monto"),
                    jsonResumenRuta.getString("cod_card_guias"),
                    jsonResumenRuta.getString("cod_card_monto"),
                    jsonResumenRuta.getString("fecha_ruta"),
                    jsonResumenRuta.getString("tiempo")
            );
            resumenRuta.save();
        }
    }

    private void showResumenRuta() {
        List<ResumenRuta> resumenRuta = ResumenRuta.listAll(ResumenRuta.class);

        if (resumenRuta.size() > 0) {
            if (!resumenRuta.get(0).getFechaInicioRuta().isEmpty()) {
                String fechaInicio = resumenRuta.get(0).getFechaInicioRuta();

                // Si viene en formato ISO 8601 (con 'T')
                if (fechaInicio.contains("T")) {
                    String[] splitDate = fechaInicio.split("T");
                    String fecha = splitDate[0]; // "2025-10-07"
                    String hora = splitDate[1];  // "10:35:37"

                    // Formatear la fecha
                    String fechaFormateada = DateUtils.formatFullDate(fecha, "yyyy-MM-dd");
                    // Formatear la hora (quitar segundos si es necesario)
                    String horaFormateada = DateUtils.format24HourTo12Hour(hora.substring(0, 5)); // "10:35"

                    resumenRuta.get(0).setFechaInicioRuta(fechaFormateada + " a las " + horaFormateada);
                } else {
                    // Si ya viene con espacio (formato antiguo)
                    String[] splitDate = fechaInicio.split(" ");
                    resumenRuta.get(0).setFechaInicioRuta(
                            DateUtils.formatFullDate(splitDate[0], "yyyy-MM-dd") + " a las "
                                    + DateUtils.format24HourTo12Hour(splitDate[1]));
                }

                if (resumenRuta.get(0).getTiempoRuta().contains(":")) {

                    String[] horaParts = resumenRuta.get(0).getTiempoRuta().split(":");
                    resumenRuta.get(0).setTiempoRuta(horaParts[0] + " h " + horaParts[1] + " min");
                }
            }
            view.setDatosResumenRuta(resumenRuta.get(0));
        }
    }
}
