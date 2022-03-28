package com.urbanoexpress.iridio3.presenter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.orm.util.NamingHelper;
import com.urbanoexpress.iridio3.AsyncTaskCoroutine;
import com.urbanoexpress.iridio3.R;
import com.urbanoexpress.iridio3.model.entity.Data;
import com.urbanoexpress.iridio3.model.entity.Ruta;
import com.urbanoexpress.iridio3.model.util.ModelUtils;
import com.urbanoexpress.iridio3.ui.FiltrarGuiaActivity;
import com.urbanoexpress.iridio3.ui.model.RutaItem;
import com.urbanoexpress.iridio3.util.CommonUtils;
import com.urbanoexpress.iridio3.util.Preferences;
import com.urbanoexpress.iridio3.util.constant.LocalAction;
import com.urbanoexpress.iridio3.view.TransferirGuiaView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by mick on 12/06/17.
 */

public class TransferirGuiaPresenter {

    private TransferirGuiaView view;

    private List<Ruta> dbGuias;
    private List<RutaItem> items;

    private ArrayList<Ruta> guiasSeleccionadas = new ArrayList<>();

    private String queryLineaNegocio = "";
    private String queryTipo = "";

    private ArrayList<String> queryParamsList = new ArrayList<>();

    private boolean[] checkedFiltros = new boolean[0];

    public TransferirGuiaPresenter(TransferirGuiaView view) {
        this.view = view;
    }

    public void init() {
        LocalBroadcastManager.getInstance(view.getViewContext())
                .registerReceiver(aplicarFiltroGuiaReceiver,
                        new IntentFilter(LocalAction.APLICAR_FILTRO_GUIA_ACTION));

        buildQueryGuias();

        new LoadGETask().execute();
    }

    public void onSelectedItem(int position) {
//        if (lineaSeleccionada < 0) {
//            lineaSeleccionada = Integer.parseInt(dbGuias.get(position).getLineaNegocio());
//        }
        if (validateRestriccionesSeleccion(position, true)) {
            checkItem(position);
            showActionMode();
            view.setTitleActionMode(guiasSeleccionadas.size() + "");
        }
    }

    public void deselectAllItems() {
        clearItemsSelected();
        showActionMode();
        view.notifyAllItemChanged();
    }

    public void selectAllItems() {
        checkAllItems();
        view.notifyAllItemChanged();
        showActionMode();
        view.setTitleActionMode(guiasSeleccionadas.size() + "");
    }

    public void transferirGuias() {
        String[] guias = new String[guiasSeleccionadas.size()];
        String idZona = guiasSeleccionadas.get(0).getIdZona();
        String lineaNegocio = guiasSeleccionadas.get(0).getLineaNegocio();

        for (int i = 0; i < guiasSeleccionadas.size(); i++) {
            guias[i] = guiasSeleccionadas.get(i).getIdServicio();
        }

        deselectAllItems();
        view.navigateToTransferirGuiaDialog(guias, idZona, lineaNegocio);
    }

    public void onActionFiltros() {
        view.navigateToFiltrarGuiaActivity(checkedFiltros);
    }

    public void onDestroy() {
        LocalBroadcastManager.getInstance(view.getViewContext()).unregisterReceiver(aplicarFiltroGuiaReceiver);
    }

    private boolean validateRestriccionesSeleccion(int position, boolean showMessages) {
        if (guiasSeleccionadas.size() > 0) {
            if (Integer.parseInt(dbGuias.get(0).getLineaNegocio())
                    != Integer.parseInt(dbGuias.get(position).getLineaNegocio())) {
                if (showMessages) {
                    view.showToast(R.string.fragment_ruta_pendiente_message_seleccion_diferentes_lineas);
                }
                return false;
            }
        }
//        if (lineaSeleccionada != Integer.parseInt(dbGuias.get(position).getLineaNegocio())) {
//            if (showMessages) {
//                BaseModalsView.showToast(view.getViewContext(),
//                        R.string.fragment_ruta_pendiente_message_seleccion_diferentes_lineas,
//                        Toast.LENGTH_SHORT);
//            }
//            return false;
//        }

        return true;
    }

    private void checkItem(int position) {
        if (items.get(position).isSelected()) {
            removeItemSelected(position);
            items.get(position).setIcon(
                    ModelUtils.getIconTipoGuia(dbGuias.get(position)));
            items.get(position).setBackgroundColor(
                    ModelUtils.getBackgroundColorGE(dbGuias.get(position).getTipoEnvio(), view.getViewContext()));
            items.get(position).setSelected(false);
        } else {
            guiasSeleccionadas.add(dbGuias.get(position));
            items.get(position).setIcon(R.drawable.ic_checkbox_marked_circle);
            items.get(position).setBackgroundColor(
                    ContextCompat.getColor(view.getViewContext(), R.color.gris_9));
            items.get(position).setSelected(true);
        }
        view.notifyItemChanged(position);
    }

    private void showActionMode() {
        if (guiasSeleccionadas.size() > 0) {
            view.showActionMode();
        } else {
            view.hideActionMode();
        }
    }

    private void removeItemSelected(int position) {
        for (int i = 0; i < guiasSeleccionadas.size(); i++) {
            if (guiasSeleccionadas.get(i).getIdServicio()
                    .equals(dbGuias.get(position).getIdServicio())
                    && guiasSeleccionadas.get(i).getLineaNegocio()
                    .equals(dbGuias.get(position).getLineaNegocio())) {
                guiasSeleccionadas.remove(i);
                break;
            }
        }
//        if (guiasSeleccionadas.size() == 0) {
//            lineaSeleccionada = -1;
//        }
    }

    private void clearItemsSelected() {
        for (int i = 0; i < items.size(); i++) {
            items.get(i).setIcon(
                    ModelUtils.getIconTipoGuia(dbGuias.get(i)));
            items.get(i).setBackgroundColor(
                    ContextCompat.getColor(view.getViewContext(), R.color.lightPrimaryText));
            items.get(i).setBackgroundColor(
                    ModelUtils.getBackgroundColorGE(dbGuias.get(i).getTipoEnvio(), view.getViewContext()));
            items.get(i).setSelected(false);
        }
        guiasSeleccionadas.clear();
//        lineaSeleccionada = -1;
    }

    private void checkAllItems() {
        if (isSelectedAllItems()) {
            clearItemsSelected();
        } else {
            guiasSeleccionadas.clear();
            for (int i = 0; i < items.size(); i++) {
                if (validateRestriccionesSeleccion(i, false)) {
                    guiasSeleccionadas.add(dbGuias.get(i));
                    items.get(i).setIcon(R.drawable.ic_checkbox_marked_circle);
                    items.get(i).setBackgroundColor(
                            ContextCompat.getColor(view.getViewContext(), R.color.gris_9));
                    items.get(i).setSelected(true);
                }
            }
        }
    }

    private boolean isSelectedAllItems() {
        for (int i = 0; i < items.size(); i++) {
            if (validateRestriccionesSeleccion(i, false)) {
                if (!items.get(i).isSelected()) {
                    return false;
                }
            }
        }
        return true;
    }

    /*private class LoadGETask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            BaseModalsView.showProgressDialog(
                    view.getViewContext(),
                    R.string.text_cargando);
        }

        protected Boolean doInBackground(String... urls) {
            dbGuias = interactor.selectRutasPendientes();

            items = new ArrayList<>();

            for (Ruta ruta: dbGuias) {
                int resIcon = ModelUtils.getIconTipoGuia(ruta.getLineaNegocio());

                TransferirGuiaItem item = new TransferirGuiaItem(
                        ruta.getIdServicio(),
                        ruta.getGuia(),
                        resIcon,
                        ContextCompat.getColor(view.getViewContext(), R.color.lightPrimaryText),
                        false
                );

                items.add(item);
            }
            return true;
        }

        protected void onPostExecute(Boolean result) {
            view.showGuias(items);
            BaseModalsView.hideProgressDialog();
        }
    }*/

    private void buildQueryGuias() {
        queryLineaNegocio = "2, 3, 4";
        queryTipo = "";

        queryParamsList = new ArrayList<>(Arrays.asList(
                Preferences.getInstance().getString("idUsuario", ""),
                Data.Delete.NO + "",
                Ruta.EstadoDescarga.PENDIENTE + ""));

        checkedFiltros = new boolean[]{true, true, true, true, true, true, true, true, true};
    }

    private class LoadGETask extends AsyncTaskCoroutine<String, Boolean> {

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            view.showProgressDialog();
        }

        public Boolean doInBackground(String... urls) {
            dbGuias = selectGuias();

            items = new ArrayList<>();

            if (dbGuias.size() > 0) {
                for (int i = 0; i < dbGuias.size(); i++) {
                    int resIcon = ModelUtils.getIconTipoGuia(dbGuias.get(i));

                    int resIconTipoEnvio = ModelUtils.getIconTipoEnvio(dbGuias.get(i));

                    int backgroundColorGuia = ModelUtils.getBackgroundColorGE(dbGuias.get(i).getTipoEnvio(), view.getViewContext());

                    int lblColorHorario = ModelUtils.getLblColorHorario(dbGuias.get(i).getTipo(),
                            dbGuias.get(i).getTipoEnvio(), dbGuias.get(i).getFechaRuta(),
                            dbGuias.get(i).getHorarioEntrega());

                    String horario = dbGuias.get(i).getHorarioEntrega();

                    if (ModelUtils.isGuiaEntrega(dbGuias.get(i).getTipo())) {
                        horario = CommonUtils.fomartHorarioAproximado(dbGuias.get(i).getHorarioAproximado(), false);
                    }

                    String simboloMoneda = ModelUtils.getSimboloMoneda(view.getViewContext());

                    RutaItem rutaItem = new RutaItem(
                            dbGuias.get(i).getIdServicio(),
                            dbGuias.get(i).getIdManifiesto(),
                            (ModelUtils.isGuiaEntrega(dbGuias.get(i).getTipo())
                                    ? dbGuias.get(i).getGuia()
                                    : dbGuias.get(i).getShipper() + " (" + dbGuias.get(i).getGuia() + ")"),
                            dbGuias.get(i).getDistrito(),
                            dbGuias.get(i).getDireccion(),
                            horario,
                            dbGuias.get(i).getPiezas(),
                            "",
                            simboloMoneda,
                            resIcon,
                            resIcon,
                            resIconTipoEnvio,
                            backgroundColorGuia,
                            lblColorHorario,
                            dbGuias.get(i).getResultadoGestion(),
                            false,
                            false,
                            ModelUtils.isTipoEnvioValija(dbGuias.get(i).getTipoEnvio()),
                            ModelUtils.isShowIconImportePorCobrar(dbGuias.get(i).getImporte()),
                            false
                    );
                    items.add(rutaItem);
                }
            }
            return true;
        }

        public void onPostExecute(Boolean result) {
            view.showGuias(items);
            view.dismissProgressDialog();
        }
    }

    public List<Ruta> selectGuias() {
        List<Ruta> guias = Ruta.find(Ruta.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                        NamingHelper.toSQLNameDefault("eliminado") + " = ? and " +
                        NamingHelper.toSQLNameDefault("estadoDescarga") + " = ? and " +
                        NamingHelper.toSQLNameDefault("lineaNegocio") + " in (" + queryLineaNegocio + ") " +
                        queryTipo,
                queryParamsList.toArray(new String[0]));

        Collections.sort(guias, (lhs, rhs) ->
                Integer.valueOf(lhs.getSecuencia()).compareTo(Integer.parseInt(rhs.getSecuencia())));

        return guias;
    }

    /**
     * Broadcast
     *
     * {@link FiltrarGuiaActivity#sendOnAplicarFiltroGuiaReceiver}
     */
    private final BroadcastReceiver aplicarFiltroGuiaReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getBundleExtra("args");
            queryLineaNegocio = bundle.getString("queryLineaNegocio");
            queryTipo = bundle.getString("queryTipo");
            queryParamsList = (ArrayList<String>) bundle.getSerializable("queryParamsList");
            checkedFiltros = bundle.getBooleanArray("checkedFiltros");

            new LoadGETask().execute();
        }
    };
}
