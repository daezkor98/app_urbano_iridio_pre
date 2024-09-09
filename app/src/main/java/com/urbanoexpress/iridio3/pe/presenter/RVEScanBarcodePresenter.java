package com.urbanoexpress.iridio3.pe.presenter;

import com.android.volley.VolleyError;
import com.urbanoexpress.iridio3.pe.R;
import com.urbanoexpress.iridio3.pe.model.RecolectarValijaExpressViewModel;
import com.urbanoexpress.iridio3.pe.model.interactor.RecolectarValijaExpressInteractor;
import com.urbanoexpress.iridio3.pe.model.interactor.callback.RequestCallback;
import com.urbanoexpress.iridio3.pe.ui.model.DetailsItem;
import com.urbanoexpress.iridio3.pe.util.CommonUtils;
import com.urbanoexpress.iridio3.pe.util.Preferences;
import com.urbanoexpress.iridio3.pe.view.RVEScanBarcodeView;

import org.apache.commons.text.WordUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RVEScanBarcodePresenter {

    private RVEScanBarcodeView view;
    private RecolectarValijaExpressViewModel model;
    private String barra;

    public RVEScanBarcodePresenter(RVEScanBarcodeView view, RecolectarValijaExpressViewModel model) {
        this.view = view;
        this.model = model;
    }

    public void processBarra(String barra) {
        if (!validarDatosBarra(barra)) {
            return;
        }

        if (!CommonUtils.validateConnectivity(view.getViewContext())) {
            return;
        }

        requestReadBarra(barra);
    }

    public void onNextButtonClick() {
        requestConfirmRecoleccion();
    }

    private boolean validarDatosBarra(String barra) {
        if (barra.trim().length() == 0) {
            view.setErrorBarra(view.getViewContext().getString(R.string.msg_ingrese_barra_correctamente));
            return false;
        }
        return true;
    }

    private void requestReadBarra(String barra) {
        this.barra = barra;
        view.showProgressDialog("");

        RequestCallback callback = new RequestCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                view.dismissProgressDialog();

                try {
                    view.clearBarra();

                    if (response.getBoolean("success")) {
                        JSONObject data = response.getJSONObject("data");

                        List<DetailsItem> details = new ArrayList<>();
                        String shipper = WordUtils.capitalize(
                                data.getString("shipper").toLowerCase());

                        details.add(new DetailsItem("Recolección", data.getString("srec_barra")));
                        details.add(new DetailsItem("Valija", barra));
                        details.add(new DetailsItem("Shipper", shipper));
                        details.add(new DetailsItem("Agencia Banco",
                                WordUtils.capitalize(data.getString("agencia_banco").toLowerCase())));
                        details.add(new DetailsItem("Agencia Urbano",
                                WordUtils.capitalize(data.getString("comuna_destino").toLowerCase())));

                        model.setIdRecoleccion(data.getString("srec_id"));
                        model.setBarraRecoleccion(data.getString("srec_barra"));
                        model.setBarraValija(barra);
                        model.setShipperName(shipper);

                        view.setDetails(details);
                        view.setEnabledButtonNext(true);
                    } else {
                        view.showMsgError(response.getString("msg_error"));
                    }
                } catch (JSONException ex) {
                    ex.printStackTrace();
                    handleRequestError(R.string.json_object_exception);
                }
            }

            @Override
            public void onError(VolleyError error) {
                error.printStackTrace();
                handleRequestError(R.string.volley_error_message);
            }
        };

        String[] params = {
                barra,
                "0",
                Preferences.getInstance().getString("idUsuario", "")};

        RecolectarValijaExpressInteractor.readBarra(params, callback);
    }

    private void requestConfirmRecoleccion() {
        view.showProgressDialog("");

        RequestCallback callback = new RequestCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                view.dismissProgressDialog();

                try {
                    if (response.getBoolean("success")) {
                        JSONObject data = response.getJSONObject("data");

                        int idServicio = 0;
                        try {
                            idServicio = Integer.parseInt(data.getString("man_id_det"));
                        } catch (NumberFormatException ex) {
                            ex.printStackTrace();
                        }

                        if (idServicio != 0) {
                            model.setIdServicio(String.valueOf(idServicio));
                            model.setNextStep(RecolectarValijaExpressViewModel.Step.TAKE_PHOTO);
                        } else {
                            view.showMsgError("Lo sentimos, no se pudo generar la recolección de la valija.");
                        }
                    } else {
                        view.showMsgError(response.getString("msg_error"));
                    }
                } catch (JSONException ex) {
                    ex.printStackTrace();
                    handleRequestError(R.string.json_object_exception);
                }
            }

            @Override
            public void onError(VolleyError error) {
                error.printStackTrace();
                handleRequestError(R.string.volley_error_message);
            }
        };

        String[] params = {
                barra,
                "1",
                Preferences.getInstance().getString("idUsuario", "")};

        RecolectarValijaExpressInteractor.readBarra(params, callback);
    }

    private void handleRequestError(int idResError) {
        view.dismissProgressDialog();
        view.clearBarra();
        view.showMsgError(view.getViewContext().getString(idResError));
    }
}
