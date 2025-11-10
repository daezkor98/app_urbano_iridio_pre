package com.urbanoexpress.iridio3.pre.presenter;

import android.app.Activity;
import android.view.View;

import com.android.volley.VolleyError;
import com.urbanoexpress.iridio3.pre.R;
import com.urbanoexpress.iridio3.pre.model.entity.PlanDeViaje;
import com.urbanoexpress.iridio3.pre.model.interactor.PlanDeViajeInteractor;
import com.urbanoexpress.iridio3.pre.model.interactor.callback.RequestCallback;
import com.urbanoexpress.iridio3.pre.ui.model.AsignarDespachoItem;
import com.urbanoexpress.iridio3.pre.util.CommonUtils;
import com.urbanoexpress.iridio3.pre.util.Preferences;
import com.urbanoexpress.iridio3.pre.view.AsignarDespachoView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mick on 30/11/16.
 */

public class AsignarDespachoPresenter {

    private static final String TAG = AsignarDespachoPresenter.class.getSimpleName();

    private AsignarDespachoView view;
    private PlanDeViajeInteractor interactor;

    private Activity activity;

    private List<PlanDeViaje> dbPlanDeViaje;
    private ArrayList<AsignarDespachoItem> despachoItems = new ArrayList<>();

    public AsignarDespachoPresenter(AsignarDespachoView view) {
        this.view = view;
        this.activity = (Activity) view.getViewContext();
        this.interactor = new PlanDeViajeInteractor(activity);
        init();
    }

    private void init() {
        dbPlanDeViaje = interactor.selectAllPlanViaje();
        if (CommonUtils.validateConnectivity(activity)) {
            getDespachos();
        }
    }

    public void onClickItemDespacho(int position) {
        selectDespacho(position);
    }

    public void onClickAsignarDespachos() {
        if (isDespachosSeleccionados()) {
            uploadDespachosPendientes();
        } else {
            view.showToast(R.string.act_plan_de_viaje_message_no_hay_despachos_pendientes_seleccionados);
        }
    }

    private void getDespachos() {
        RequestCallback callback = new RequestCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if (response.getBoolean("success")) {
                        addDespachos(response.getJSONArray("data"));
                    } else {
                        view.setMensaje("Error al cargar despachos.");
                        view.showToast(response.getString("msg_error"));
                    }
                } catch (JSONException ex) {
                    ex.printStackTrace();
                    view.setMensaje("Error al cargar despachos.");
                    view.showToast(R.string.json_object_exception);
                }
            }

            @Override
            public void onError(VolleyError error) {
                error.getMessage();
                view.setMensaje("Error al cargar despachos.");
                view.showToast(R.string.volley_error_message);
            }
        };

        String[] params = new String[]{
                dbPlanDeViaje.get(0).getIdPlanViaje(),
                Preferences.getInstance().getString("idUsuario", "")
        };

        interactor.getDespachosPendientes(params, callback);
    }

    private void uploadDespachosPendientes() {
        if (CommonUtils.validateConnectivity(activity)) {
            view.showProgressDialog(R.string.text_espere_un_momento);
            RequestCallback callback = new RequestCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    view.dismissProgressDialog();
                    try {
                        if (response.getBoolean("success")) {
                            view.showToast(R.string.act_plan_de_viaje_message_subida_despachos_exitoso);
                            view.dismiss();
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
                    view.dismissProgressDialog();
                    view.showToast(R.string.volley_error_message);
                }
            };

            String[] params = new String[]{
                    dbPlanDeViaje.get(0).getIdPlanViaje(),
                    buildParamIdDestinos(),
                    buildParamIdDespachos(),
                    Preferences.getInstance().getString("idUsuario", "")
            };

            interactor.uploadDespachosPendientes(params, callback);
        }
    }

    private void addDespachos(JSONArray data) throws JSONException {
        despachoItems.clear();

        if (data.length() > 0) {
            for (int i = 0; i < data.length(); i++) {
                JSONObject jsonObject = data.getJSONObject(i);
                despachoItems.add(new AsignarDespachoItem(
                        jsonObject.getString("id_origen"),
                        jsonObject.getString("id_destino"),
                        jsonObject.getString("id_despacho"),
                        jsonObject.getString("fecha"),
                        jsonObject.getString("origen"),
                        jsonObject.getString("destino"),
                        R.drawable.ic_check_white,
                        false));
            }
            view.setVisibilityMensaje(View.GONE);
        } else {
            view.setMensaje("No hay despachos.");
        }

        view.showDespachos(despachoItems);
    }

    private void selectDespacho(int position) {
        despachoItems.get(position).setSelected(
                !despachoItems.get(position).getSelected());

        view.notifyItemChanged(position);
    }

    private String buildParamIdDespachos() {
        String idDespachos = "";
        for (int i = 0; i < despachoItems.size(); i++) {
            if (despachoItems.get(i).getSelected()) {
                idDespachos += despachoItems.get(i).getIdDespacho() + "|";
            }
        }
        return idDespachos;
    }

    private String buildParamIdDestinos() {
        String idDestinos = "";
        for (int i = 0; i < despachoItems.size(); i++) {
            if (despachoItems.get(i).getSelected()) {
                idDestinos += despachoItems.get(i).getIdDestino() + "|";
            }
        }
        return idDestinos;
    }

    private boolean isDespachosSeleccionados() {
        for (int i = 0; i < despachoItems.size(); i++) {
            if (despachoItems.get(i).getSelected()) {
                return true;
            }
        }
        return false;
    }

}
