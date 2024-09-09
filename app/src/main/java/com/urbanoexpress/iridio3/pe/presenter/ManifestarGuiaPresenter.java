package com.urbanoexpress.iridio3.pe.presenter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.VolleyError;
import com.urbanoexpress.iridio3.R;
import com.urbanoexpress.iridio3.pe.data.rest.ApiRequest;
import com.urbanoexpress.iridio3.pe.data.rest.ApiRest;
import com.urbanoexpress.iridio3.pe.model.entity.Ruta;
import com.urbanoexpress.iridio3.pe.model.interactor.RutaPendienteInteractor;
import com.urbanoexpress.iridio3.pe.ui.QRScannerActivity;
import com.urbanoexpress.iridio3.pe.ui.adapter.PiezasAdapter;
import com.urbanoexpress.iridio3.pe.ui.model.PiezaItem;
import com.urbanoexpress.iridio3.pe.util.CommonUtils;
import com.urbanoexpress.iridio3.pe.util.Session;
import com.urbanoexpress.iridio3.pe.util.constant.LocalAction;
import com.urbanoexpress.iridio3.pe.view.ManifestarGuiaView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ManifestarGuiaPresenter implements PiezasAdapter.OnPiezaListener {

    private ManifestarGuiaView view;
    private List<PiezaItem> piezaItems = new ArrayList<>();
    private String guia = "";
    private boolean pckReadFromScanner = false;
    private int actionScanBarCode; // 1 => scan guia, 2 => scan pck

    public ManifestarGuiaPresenter(ManifestarGuiaView view) {
        this.view = view;
        init();
    }

    private void init() {
        LocalBroadcastManager.getInstance(view.getViewContext())
                .registerReceiver(resultScannReceiver,
                        new IntentFilter(LocalAction.LOCAL_ACTION_BARCODE_SCAN_RESULT));
    }

    @Override
    public void onPiezaClick(int position) {
        if (piezaItems.get(position).isSelectable()) {
            if (piezaItems.size() <= 10) {
                boolean selected = !piezaItems.get(position).isSelected();
                piezaItems.get(position).setSelected(selected);
                view.notifyItemChanged(position);
            } else {
                view.showSnackBar(R.string.activity_manifestar_guia_msg_limite_seleccion_piezas);
            }
        } else {
            view.showSnackBar(R.string.activity_detalle_ruta_msg_no_puede_seleccionar_pieza);
            CommonUtils.vibrateDevice(view.getViewContext(), 100);
        }
    }

    public void onFabScanBarCodeClick() {
        Intent intent = new Intent(view.getViewContext(), QRScannerActivity.class);
        if (piezaItems.size() == 0) {
            actionScanBarCode = 1;
            Bundle bundle = new Bundle();
            bundle.putInt("typeImpl", QRScannerPresenter.IMPLEMENT.READ_ONLY);
            intent.putExtra("args", bundle);

        } else {
            actionScanBarCode = 2;
            Bundle bundle = new Bundle();
            bundle.putInt("typeImpl", QRScannerPresenter.IMPLEMENT.CONTINUOUS);
            intent.putExtra("args", bundle);
        }
        view.getViewContext().startActivity(intent);
    }

    public void onBtnManifestarClick() {
        if (validateSelectedPiezas()) {
            requestManifestarGuia();
        }
    }

    public void onResume() {
        if (pckReadFromScanner) {
            pckReadFromScanner = false;
            view.notifyAllItemChanged();
        }
    }

    public void onDestroy() {
        LocalBroadcastManager.getInstance(view.getViewContext())
                .unregisterReceiver(resultScannReceiver);
    }

    private void loadDataPiezas(JSONArray pcks) throws JSONException {
        for (int i = 0; i < pcks.length(); i++) {
            JSONObject pck = pcks.getJSONObject(i);
            piezaItems.add(
                    new PiezaItem(
                            pck.getString("pck_numero"),
                            "",
                            pck.getString("pck_barra"),
                            pck.getString("pck_chk_id"),
                            pck.getString("pck_estado").toLowerCase(),
                            pck.getString("pck_fecha"),
                            pck.getString("pck_ruta").equalsIgnoreCase("s"),
                            false,
                            !pck.getString("pck_ruta").equals("16"),
                            false
                    )
            );
        }

        view.showPiezas(piezaItems);
    }

    private boolean validateSelectedPiezas() {
        for (int i = 0; i < piezaItems.size(); i++) {
            if (piezaItems.get(i).isSelected()) {
                return true;
            }
        }
        view.showSnackBar(R.string.activity_manifestar_guia_msg_producto_no_seleccionado);
        CommonUtils.vibrateDevice(view.getViewContext(), 100);
        return false;
    }

    private void requestValidateGuia(String guia) {
        view.showProgressDialog();

        this.guia = guia;

        ApiRequest.getInstance().newParams();
        ApiRequest.getInstance().putParams("guia", guia);
        ApiRequest.getInstance().putParams("device_phone", Session.getUser().getDevicePhone());
        ApiRequest.getInstance().putParams("id_user", Session.getUser().getIdUsuario());

        ApiRequest.getInstance().request(ApiRest.getInstance().getApiBaseUrl() +
                        ApiRest.Api.VALIDATE_MANIFESTAR_GUIA,
                ApiRequest.TypeParams.FORM_DATA, new ApiRequest.ResponseListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        view.dismissProgressDialog();
                        try {
                            if (response.getBoolean("success")) {
                                view.setVisibilityTopMessage(false);
                                view.setVisibilityAddToRouteButton(true);
                                loadDataPiezas(response.getJSONArray("data"));
                            } else {
                                view.showToast(response.getString("msg_error"));
                            }
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                            view.showToast(R.string.json_object_exception);
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        view.dismissProgressDialog();
                        view.showToast(R.string.volley_error_message);
                    }
                });
    }

    private void requestManifestarGuia() {
        view.showProgressDialog();

        RutaPendienteInteractor interactor = new RutaPendienteInteractor(view.getViewContext());
        List<Ruta> rutas = interactor.selectIdRutas();

        JSONArray piezasJSONArray = new JSONArray();
        for (int i = 0; i < piezaItems.size(); i++) {
            piezasJSONArray.put(piezaItems.get(i).getIdPieza());
        }

        ApiRequest.getInstance().newParams();
        ApiRequest.getInstance().putParams("vp_piezas", piezasJSONArray.toString());
        ApiRequest.getInstance().putParams("vp_id_ruta", rutas.get(0).getIdRuta());
        ApiRequest.getInstance().putParams("device_phone", Session.getUser().getDevicePhone());
        ApiRequest.getInstance().putParams("id_user", Session.getUser().getIdUsuario());

        ApiRequest.getInstance().request(ApiRest.getInstance().getApiBaseUrl() +
                        ApiRest.Api.MANIFESTAR_GUIA,
                ApiRequest.TypeParams.FORM_DATA, new ApiRequest.ResponseListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        view.dismissProgressDialog();
                        try {
                            if (response.getBoolean("success")) {
                                view.showToast("La guía " + guia + " fue agregado a tu ruta exitosamente.");
                                view.finishActivity();
                            } else {
                                view.showToast(response.getString("msg_error"));
                            }
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                            view.showToast(R.string.json_object_exception);
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        view.dismissProgressDialog();
                        view.showToast(R.string.volley_error_message);
                    }
                });
    }

    /**
     * Broadcast
     *
     * {@link CodeScannerImpl#sendOnResultScannListener}
     */
    private final BroadcastReceiver resultScannReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (actionScanBarCode == 1) {
                requestValidateGuia(intent.getStringExtra("value"));
            } else if (actionScanBarCode == 2) {
                try {
                    boolean isPCKFound = false;
                    for (int i = 0; i < piezaItems.size(); i++) {
                        if (piezaItems.get(i).getBarra().equals(intent.getStringExtra("value"))) {
                            if (piezaItems.get(i).isSelectable()) {
                                isPCKFound = true;
                                pckReadFromScanner = true;
                                piezaItems.get(i).setSelected(true);
                                CommonUtils.playSoundOnScanBarcode(view.getViewContext(), R.raw.scan_barcode_add_pck);
                                break;
                            }
                        }
                    }
                    if (!isPCKFound) {
                        CommonUtils.playSoundOnScanBarcode(view.getViewContext(), R.raw.scan_barcode_error);
                    }
                    LocalBroadcastManager.getInstance(view.getViewContext()).sendBroadcast(
                            new Intent(LocalAction.NEXT_BARCODE_SCAN_CONTINUOS));
                } catch (NullPointerException ex) {
                    ex.printStackTrace();
                }
            }
        }
    };
}
