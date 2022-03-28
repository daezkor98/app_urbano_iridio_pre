package com.urbanoexpress.iridio3.presenter;

import android.widget.DatePicker;

import com.android.volley.VolleyError;
import com.urbanoexpress.iridio3.R;
import com.urbanoexpress.iridio3.model.interactor.ResumenRutaInteractor;
import com.urbanoexpress.iridio3.model.interactor.callback.RequestCallback;
import com.urbanoexpress.iridio3.ui.dialogs.DatePickerDailogFragment;
import com.urbanoexpress.iridio3.util.Preferences;
import com.urbanoexpress.iridio3.util.Session;
import com.urbanoexpress.iridio3.view.ResumenRutaRuralView;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ResumenRutaRuralPresenter
        implements DatePickerDailogFragment.OnDatePickerDailogFragmentListener {

    private ResumenRutaRuralView view;
    private ResumenRutaInteractor interactor;

    private LocalDate localDate;

    public ResumenRutaRuralPresenter(ResumenRutaRuralView view) {
        this.view = view;
        this.interactor = new ResumenRutaInteractor(view.getViewContext());
        init();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        localDate = LocalDate.of(year, month, dayOfMonth);
        this.view.setTextDate(DateTimeFormatter.ofPattern("dd/MM/yyyy").format(localDate));

        /*if (orders.size() > 0) {
            items.clear();
            this.view.getBinding().rvOrders.getAdapter().notifyDataSetChanged();
            this.view.setVisibilityProgressBar(View.VISIBLE);
            this.view.getBinding().noDataLayout.setAlpha(0f);
            this.view.getBinding().rvOrders.setBackgroundColor(
                    ContextCompat.getColor(view.getContext(), android.R.color.transparent));
        } else {
            this.view.setVisibilityProgressBar(View.VISIBLE);
            this.view.getBinding().noDataLayout.animate().alpha(0f).setDuration(200);
        }

        requestGetOrderHistory();*/

        requestGetResumenRuta();
    }

    private void init() {
        localDate = LocalDate.now();
        view.setTextDate(DateTimeFormatter.ofPattern("dd/MM/yyyy").format(localDate));
        requestGetResumenRuta();
    }

    public void onSwipeRefresh() {
        requestGetResumenRuta();
    }

    public void onActionSelectDateClick() {
        view.openDatePicker(localDate.getYear(), localDate.getMonthValue(), localDate.getDayOfMonth());
    }

    private void requestGetResumenRuta() {
        view.setVisibilitySwipeRefreshLayout(true);

        RequestCallback callback = new RequestCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    view.setVisibilitySwipeRefreshLayout(false);
                    if (response.getBoolean("success")) {
                        showResumenRuta(response.getJSONObject("data"));
                    } else {
                        view.showToast(response.getString("msg_error"));
                    }
                } catch (JSONException ex) {
                    ex.printStackTrace();
                    view.setVisibilitySwipeRefreshLayout(false);
                    view.showSnackBar(view.getViewContext().getString(R.string.json_object_exception));
                }
            }

            @Override
            public void onError(VolleyError error) {
                view.setVisibilitySwipeRefreshLayout(false);
                view.showSnackBar(view.getViewContext().getString(R.string.volley_error_message));
            }
        };

        String[] params = new String[] {
                DateTimeFormatter.ofPattern("dd/MM/yyyy").format(localDate),
                Preferences.getInstance().getString("idUsuario", ""),
                Session.getUser().getDevicePhone(),
        };

        interactor.getResumenRutaRural(params, callback);
    }

    private void showResumenRuta(JSONObject data) throws JSONException {
        view.setTextCourier(data.getString("courier"));
        view.setTextZona(data.getString("nombre_zona"));
        view.setTextFecha(data.getString("fecha_ruta"));
        view.setTextTotalGuias(data.getString("tot_guias"));
        view.setTextTotalPiezas(data.getString("tot_piezas"));
        view.setTextPesoSeco(data.getString("peso_seco"));
        view.setTextTotalCashGuias(data.getString("cod_cash_guias"));
        view.setTextTotalCashImporte(data.getString("cod_cash_monto"));
    }
}