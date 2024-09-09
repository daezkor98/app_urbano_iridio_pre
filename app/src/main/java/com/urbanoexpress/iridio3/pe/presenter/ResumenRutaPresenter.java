package com.urbanoexpress.iridio3.pe.presenter;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import com.urbanoexpress.iridio3.pe.R;
import com.urbanoexpress.iridio3.pe.model.entity.ResumenRuta;
import com.urbanoexpress.iridio3.pe.model.interactor.ResumenRutaInteractor;
import com.urbanoexpress.iridio3.pe.model.interactor.callback.RequestCallback;
import com.urbanoexpress.iridio3.pe.util.DateUtils;
import com.urbanoexpress.iridio3.pe.util.Preferences;
import com.urbanoexpress.iridio3.pe.util.Session;
import com.urbanoexpress.iridio3.pe.view.ResumenRutaView;

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
                view.showToast(R.string.volley_error_message);
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
                String[] splitDate = resumenRuta.get(0).getFechaInicioRuta().split(" ");
                resumenRuta.get(0).setFechaInicioRuta(
                        DateUtils.formatFullDate(splitDate[0], "yyyy-MM-dd") + " a las "
                        + DateUtils.format24HourTo12Hour(splitDate[1]));
                if (resumenRuta.get(0).getTiempoRuta().contains(":")) {
                    String[] horaParts = resumenRuta.get(0).getTiempoRuta().split(":");
                    resumenRuta.get(0).setTiempoRuta(horaParts[0] + " h " + horaParts[1] + " min");
                }
            }
            view.setDatosResumenRuta(resumenRuta.get(0));
        }
    }
}
