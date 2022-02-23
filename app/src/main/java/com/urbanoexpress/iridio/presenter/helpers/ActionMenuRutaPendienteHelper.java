package com.urbanoexpress.iridio.presenter.helpers;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;

import com.urbanoexpress.iridio.AsyncTaskCoroutine;
import com.urbanoexpress.iridio.R;
import com.urbanoexpress.iridio.application.AndroidApplication;
import com.urbanoexpress.iridio.model.entity.Data;
import com.urbanoexpress.iridio.model.entity.Ruta;
import com.urbanoexpress.iridio.model.entity.RutaEliminada;
import com.urbanoexpress.iridio.model.interactor.RutaPendienteInteractor;
import com.urbanoexpress.iridio.model.util.ModelUtils;
import com.urbanoexpress.iridio.presenter.RutaPendientePresenter;
import com.urbanoexpress.iridio.ui.dialogs.DefinirPosicionGuiaDialog;
import com.urbanoexpress.iridio.ui.model.RutaItem;
import com.urbanoexpress.iridio.util.CommonUtils;
import com.urbanoexpress.iridio.util.Preferences;
import com.urbanoexpress.iridio.util.constant.LocalAction;
import com.urbanoexpress.iridio.view.BaseModalsView;
import com.urbanoexpress.iridio.view.RutaPendienteView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mick on 19/09/16.
 */

public class ActionMenuRutaPendienteHelper extends BaseModalsView {

    private static final String TAG = ActionMenuRutaPendienteHelper.class.getSimpleName();

    private RutaPendienteView view;

    private int tipoRutaSeleccion = -1;
    private int lastPositionSelectedItem = -1;
    private String tipoEnvioSeleccion = "";
    private boolean activeModeOrdenarGuias = false;
    private boolean activeSelection = false;

    private List<RutaItem> rutaItems;
    private List<Ruta> dbRuta;
    private ArrayList<Ruta> rutasSeleccionadas = new ArrayList<>();

    private ArrayList<RutaEliminada> rutasEliminadas = new ArrayList<>();

    private ArrayList<Integer> codigoShipperSeleccionadas = new ArrayList<>();

    private ShowDescargaRutaHelper showDescargaRutaHelper;

    public ActionMenuRutaPendienteHelper(RutaPendienteView view) {
        this.view = view;
    }

    public void selectItem(int position) {
        Log.d(TAG, "SELECT ITEM: " + position);
        if (activeModeOrdenarGuias) {
            checkItem(position);
            showActionMode();
            view.setTitleActionMode(rutasSeleccionadas.size() + "");
        } else {
            if (!activeSelection) {
                setLastPositionSelectedItem(position);
                setTipoRutaSeleccion(position);
                setTipoEnvioSeleccion(position);
            }

            if (generateTipoRutaSeleccion(position) != Ruta.Tipo.ENTREGA) {
                view.showToast(R.string.fragment_ruta_pendiente_message_seleccion_recoleccion);
                return;
            }

            if (!getTipoEnvioSeleccion().equals("NA")) {
                if (validateRestriccionesDeSeleccion(position, true, false)) {
                    checkItem(position);
                    showActionMode();
                    configActionMode();
                    view.setTitleActionMode(rutasSeleccionadas.size() + "");
                }
                activeSelection = rutasSeleccionadas.size() > 0;
            } else {
                view.showToast(R.string.fragment_ruta_pendiente_message_seleccion_no_valida);
            }
        }
    }

    public void selectAllItems() {
        checkAllItems();
        view.notifyAllItemChanged();
        showActionMode();
        configActionMode();
        view.setTitleActionMode(rutasSeleccionadas.size() + "");
    }

    public void deselectAllItems() {
        clearItemsSelected();
        view.notifyAllItemChanged();
        view.notifyAllItemChanged();
    }

    public void gestionMultiple() {
        if (rutasSeleccionadas.size() == 1) {
            view.showToast(R.string.fragment_ruta_pendiente_message_gestion_multiple_mayores_a_dos);
        } else {
            Log.d(TAG, "GESTION MULTIPLE");
            Log.d(TAG, "rutasSeleccionadas: " + rutasSeleccionadas.size());

//            LinkedHashMap<String, String> mapRuta = new LinkedHashMap<>();
//
//            for (int i = 0; i < rutasSeleccionadas.size(); i++) {
//                mapRuta.put(rutasSeleccionadas.get(i).getIdServicio(),
//                        rutasSeleccionadas.get(i).getLineaNegocio());
//            }

            ArrayList<Ruta> rutas = new ArrayList<>();

            for (int i = 0; i < rutasSeleccionadas.size(); i++) {
                rutas.add(rutasSeleccionadas.get(i));
            }

            showDescargaRutaHelper = new ShowDescargaRutaHelper(view.getViewContext(), rutas, 1);
            showDescargaRutaHelper.onClickDescarga();
            view.hideActionMode();
        }
    }

    public void ordenarGuias() {
        activeModeOrdenarGuias = true;
        CommonUtils.setVisibilityOptionMenu(
                view.getMenuActionMode(),
                R.id.action_gestion_multiple, false);
        CommonUtils.setVisibilityOptionMenu(
                view.getMenuActionMode(),
                R.id.action_check_all, false);
        CommonUtils.setVisibilityOptionMenu(
                view.getMenuActionMode(),
                R.id.action_ordenar_guias, false);
        CommonUtils.setVisibilityOptionMenu(
                view.getMenuActionMode(),
                R.id.action_definir_posicion_guia, false);
        CommonUtils.setVisibilityOptionMenu(
                view.getMenuActionMode(),
                R.id.action_guardar_orden_guias, true);

        for (int i = 0; i < rutaItems.size(); i++) {
            if (rutaItems.get(i).isSelected()) {
                for (int j = 0; j < rutasSeleccionadas.size(); j++) {
                    if (rutasSeleccionadas.get(j).getIdServicio()
                            .equals(dbRuta.get(i).getIdServicio())
                            && rutasSeleccionadas.get(j).getLineaNegocio()
                            .equals(dbRuta.get(i).getLineaNegocio())) {
                        rutaItems.get(i).setCounterItem((j + 1) + "");
                    }
                }
            } else {
                rutaItems.get(i).setShowCounterItem(false);
            }
        }

        view.notifyAllItemChanged();
    }

    public void definirPosicionGuia() {
        if (rutasSeleccionadas.size() == 1) {
            DefinirPosicionGuiaDialog dialog = new DefinirPosicionGuiaDialog();
            Bundle bundle = new Bundle();
            bundle.putInt("maxPosicion", dbRuta.size());
            bundle.putInt("actualPosicion", lastPositionSelectedItem);
            dialog.setArguments(bundle);
            dialog.show(((AppCompatActivity) view.getViewContext()).getSupportFragmentManager(),
                    "DefinirPosicionGuiaDialog");
            view.hideActionMode();
        }
    }

    public void guardarOrdenGuias() {
        new GuardarOrdenGuiasTask().execute();
    }

    private class GuardarOrdenGuiasTask extends AsyncTaskCoroutine<String, String> {

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            view.showProgressDialog(R.string.fragment_ruta_pendiente_message_ordenando_guias);
        }

        @Override
        public String doInBackground(String... strings) {
            int counterSecuencia = rutasSeleccionadas.size() + 1;

            for (int i = 0; i < rutaItems.size(); i++) {
                if (rutaItems.get(i).isSelected()) {
                    dbRuta.get(i).setSecuencia(rutaItems.get(i).getCounterItem());
                    dbRuta.get(i).save();
                } else {
                    dbRuta.get(i).setSecuencia(counterSecuencia + "");
                    dbRuta.get(i).save();
                    counterSecuencia++;
                }
            }

            return null;
        }

        @Override
        public void onPostExecute(String s) {
            view.hideActionMode();
            sendOnGuardarOrdenGuiasReceiver();
            super.onPostExecute(s);
        }
    }

    public void deleteItems() {
        if (rutasSeleccionadas.size() == 1) {
            view.showToast(R.string.fragment_ruta_pendiente_message_eliminacion_multiple_mayores_a_dos);
        } else {
            showAlertDialog(view.getViewContext(),
                    R.string.text_confirmar_eliminacion,
                    R.string.fragment_ruta_pendiente_message_eliminar_guias,
                    R.string.text_aceptar,
                    (dialog, which) -> {
                        view.showProgressDialog(R.string.fragment_ruta_pendiente_title_eliminar_guias);
//                            totalRutasSeleccionadas = rutasSeleccionadas.size();
                        deleteRutasSeleccionadas();
                    }, R.string.text_cancelar, null);
        }
    }

    public void setDbRuta(List<Ruta> dbRuta) {
        this.dbRuta = dbRuta;
    }

    public void setRutaItems(List<RutaItem> rutaItems) {
        this.rutaItems = rutaItems;
    }

    private void setTipoRutaSeleccion(int positionRutaSeleccionada) {
        //if (tipoRutaSeleccion == -1) {
            tipoRutaSeleccion = generateTipoRutaSeleccion(positionRutaSeleccionada);
        //}
    }

    private void setTipoEnvioSeleccion(int positionRutaSeleccionada) {
        //if (tipoEnvioSeleccion.isEmpty()) {
            tipoEnvioSeleccion = dbRuta.get(positionRutaSeleccionada).getTipoEnvio().toUpperCase();
        //}
    }

    public void setLastPositionSelectedItem(int lastPositionSelectedItem) {
        this.lastPositionSelectedItem = lastPositionSelectedItem;
    }

    private int generateTipoRutaSeleccion(int positionRutaSeleccionada) {
        return ModelUtils.getTipoGuia(
                dbRuta.get(positionRutaSeleccionada).getTipo());
    }

    private String generateTipoEnvio(int tipo, String tipoEnvio) {
        if (tipo == Ruta.Tipo.ENTREGA) {
            switch (tipoEnvio.toUpperCase()) {
                case Ruta.TipoEnvio.PAQUETE:
                case Ruta.TipoEnvio.VALIJA:
                case Ruta.TipoEnvio.LOGISTICA_INVERSA:
                    return Ruta.TipoEnvio.PAQUETE;
                case Ruta.TipoEnvio.LIQUIDACION:
                    return Ruta.TipoEnvio.LIQUIDACION;
                case Ruta.TipoEnvio.DEVOLUCION:
                    return Ruta.TipoEnvio.DEVOLUCION;
                default:
                    return "NA";
            }
        } else if (tipo == Ruta.Tipo.RECOLECCION) {
            switch (tipoEnvio.toUpperCase()) {
                case Ruta.TipoEnvio.PAQUETE:
                case Ruta.TipoEnvio.RECOLECCION_EXPRESS:
                    return Ruta.TipoEnvio.PAQUETE;
                case Ruta.TipoEnvio.VALIJA:
                    return Ruta.TipoEnvio.VALIJA;
                case Ruta.TipoEnvio.LOGISTICA_INVERSA:
                    return Ruta.TipoEnvio.LOGISTICA_INVERSA;
                default:
                    return "NA";
            }
        }
        return "NA";
    }

    private int getTipoRutaSeleccion() {
        return tipoRutaSeleccion;
    }

    private String getTipoEnvioSeleccion() {
        return tipoEnvioSeleccion;
    }

    private void checkItem(int position) {
        if (rutaItems.get(position).isSelected()) {
            removeItemSelected(position);
            rutaItems.get(position).setIcon(rutaItems.get(position).getIdResIcon());
            rutaItems.get(position).setBackgroundColor(
                    ModelUtils.getBackgroundColorGE(dbRuta.get(position).getTipoEnvio(), view.getViewContext()));
            rutaItems.get(position).setSelected(false);

            if (activeModeOrdenarGuias) {
                rutaItems.get(position).setShowCounterItem(false);
                for (int i = 0; i < rutaItems.size(); i++) {
                    if (rutaItems.get(i).isSelected()) {
                        for (int j = 0; j < rutasSeleccionadas.size(); j++) {
                            if (rutasSeleccionadas.get(j).getIdServicio()
                                    .equals(dbRuta.get(i).getIdServicio())
                                    && rutasSeleccionadas.get(j).getLineaNegocio()
                                    .equals(dbRuta.get(i).getLineaNegocio())) {
                                rutaItems.get(i).setCounterItem((j + 1) + "");
                                view.notifyItemChanged(i);
                            }
                        }
                    }
                }
            }
        } else {
            if (activeModeOrdenarGuias) {
                rutaItems.get(position).setCounterItem((rutasSeleccionadas.size() + 1) + "");
                rutaItems.get(position).setShowCounterItem(true);
            }

            rutasSeleccionadas.add(dbRuta.get(position));
            rutaItems.get(position).setIcon(R.drawable.ic_checkbox_marked_circle);
            rutaItems.get(position).setBackgroundColor(ContextCompat.getColor(view.getViewContext(), R.color.gris_9));
            rutaItems.get(position).setSelected(true);
        }
        view.notifyItemChanged(position);
    }

    private void removeItemSelected(int position) {
        for (int i = 0; i < rutasSeleccionadas.size(); i++) {
            if (rutasSeleccionadas.get(i).getIdServicio()
                    .equals(dbRuta.get(position).getIdServicio())
                    && rutasSeleccionadas.get(i).getLineaNegocio()
                    .equals(dbRuta.get(position).getLineaNegocio())) {
                rutasSeleccionadas.remove(i);
                break;
            }
        }
    }

    private void clearItemsSelected() {
        for (int i = 0; i < rutaItems.size(); i++) {
            rutaItems.get(i).setIcon(rutaItems.get(i).getIdResIcon());
            rutaItems.get(i).setSelected(false);
            rutaItems.get(i).setShowCounterItem(true);
            rutaItems.get(i).setCounterItem((i + 1) + "");
            rutaItems.get(i).setBackgroundColor(
                    ModelUtils.getBackgroundColorGE(dbRuta.get(i).getTipoEnvio(), view.getViewContext()));
        }
        rutasSeleccionadas.clear();
        codigoShipperSeleccionadas.clear();
        tipoRutaSeleccion           = -1;
        tipoEnvioSeleccion          = "";
        lastPositionSelectedItem    = -1;
        activeModeOrdenarGuias      = false;
        activeSelection = false;
    }

    private void showActionMode() {
        Log.d(TAG, "showActionMode: " + rutasSeleccionadas.size());
        if (rutasSeleccionadas.size() > 0) {
            view.showActionMode();
        } else {
            view.hideActionMode();
        }
    }

    private void configActionMode() {
        int totalSeleccionados = rutasSeleccionadas.size();

        CommonUtils.setVisibilityOptionMenu(
                view.getMenuActionMode(),
                R.id.action_guardar_orden_guias, false);

        if (totalSeleccionados == 1) {
            CommonUtils.setVisibilityOptionMenu(
                    view.getMenuActionMode(),
                    R.id.action_gestion_multiple, false);
            CommonUtils.setVisibilityOptionMenu(
                    view.getMenuActionMode(),
                    R.id.action_definir_posicion_guia, true);
        } else if (totalSeleccionados > 1) {
            CommonUtils.setVisibilityOptionMenu(
                    view.getMenuActionMode(),
                    R.id.action_gestion_multiple, true);
            CommonUtils.setVisibilityOptionMenu(
                    view.getMenuActionMode(),
                    R.id.action_definir_posicion_guia, false);
        }
    }

    private void checkAllItems() {
        if (isSelectedAllItems()) {
            clearItemsSelected();
        } else {
            rutasSeleccionadas.clear();
            for (int i = 0; i < rutaItems.size(); i++) {
                if (validateRestriccionesDeSeleccion(i, false, true)) {
                    rutasSeleccionadas.add(dbRuta.get(i));
                    rutaItems.get(i).setIcon(R.drawable.ic_checkbox_marked_circle);
                    rutaItems.get(i).setBackgroundColor(ContextCompat.getColor(view.getViewContext(), R.color.gris_9));
                    rutaItems.get(i).setSelected(true);
                }
            }
        }
    }

    private boolean isSelectedAllItems() {
        for (int i = 0; i < rutaItems.size(); i++) {
            if (validateRestriccionesDeSeleccion(i, false, true)) {
                if (!rutaItems.get(i).isSelected()) {
                    return false;
                }
            }
        }
        return true;
    }

    private void updateSecuenciaAllRutasPendientes() {
        for (int i = 0; i < dbRuta.size(); i++) {
            Log.d(TAG, "UPDATE SECUENCIA GUIA ("+ dbRuta.get(i).getGuia() +") SECUENCIA: " + dbRuta.get(i).getSecuencia());
            dbRuta.get(i).setSecuencia(i + 1 + "");
            dbRuta.get(i).save();
            Log.d(TAG, "UPDATE SECUENCIA GUIA ("+ dbRuta.get(i).getGuia() +") SECUENCIA: " + dbRuta.get(i).getSecuencia());
        }
    }

    private void deleteRutasSeleccionadas() {
        new Thread(() -> {
            for (int i = 0; i < rutasSeleccionadas.size(); i++) {
                rutasSeleccionadas.get(i).setEliminado(Data.Delete.YES);
                rutasSeleccionadas.get(i).save();

                RutaEliminada rEliminado = new RutaEliminada(
                        Preferences.getInstance().getString("idUsuario", ""),
                        rutasSeleccionadas.get(i).getIdServicio()
                );
                rEliminado.save();

                rutasEliminadas.add(rEliminado);
            }

            Log.d(TAG, "TOTAL DBRUTA: " + dbRuta.size());
            Log.d(TAG, "TOTAL RUTAITEMS: " + rutaItems.size());

            for (int i = 0; i < rutasSeleccionadas.size(); i++) {
                for (int j = 0; j < dbRuta.size(); j++) {
                    if (rutasSeleccionadas.get(i).getIdServicio().equals(
                            dbRuta.get(j).getIdServicio()
                    )) {
                        dbRuta.remove(j);
                        rutaItems.remove(j);
//                            posicionRutasSeleccionadas.add(j);
                        try {
                            view.notifyItemRemove(j);
                        } catch (IllegalStateException ex) {
                            ex.printStackTrace();
                        } catch (IndexOutOfBoundsException ex) {
                            ex.printStackTrace();
                        }
                        Log.d(TAG, "ITEM DELETE INDEX: " + j);
                    }
                }
            }

            updateSecuenciaAllRutasPendientes();

            ((AppCompatActivity) view.getViewContext()).runOnUiThread(() -> {
                view.hideActionMode();
                view.dismissProgressDialog();
            });
        }).start();
    }

    private boolean validateRestriccionesDeSeleccion(int position, boolean showMessages, boolean validateShipper) {
        long totalPiezas = RutaPendienteInteractor.getTotalPiezasByGuia(
                dbRuta.get(position).getIdServicio(), dbRuta.get(position).getLineaNegocio());
        if (totalPiezas > 1) {
            if (showMessages) {
                view.showToast(R.string.fragment_ruta_pendiente_message_seleccion_multiples_piezas);
            }
            return false;
        }

        if (!generateTipoEnvio(getTipoRutaSeleccion(), getTipoEnvioSeleccion()).equals(
                generateTipoEnvio(generateTipoRutaSeleccion(position), dbRuta.get(position).getTipoEnvio()))) {
            if (showMessages) {
                view.showToast(R.string.fragment_ruta_pendiente_message_seleccion_diferentes_tipo_envio);
            }
            return false;
        }

        if (!validateShipperSeleccionado(position, showMessages)) {
            return false;
        }

        return true;
    }

    private boolean validateShipperSeleccionado(final int position, boolean showDialog) {
        boolean isValidShipper = true;

        try {
            if (codigoShipperSeleccionadas.size() != 0) {
                if (!codigoShipperSeleccionadas.contains(Integer.parseInt(dbRuta.get(position).getShiCodigo()))) {
                    isValidShipper = false;
                }
            } else {
                codigoShipperSeleccionadas.add(Integer.parseInt(dbRuta.get(position).getShiCodigo()));
            }
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
        }

        if (!isValidShipper) {
            if (showDialog) {
                showAlertDialog(view.getViewContext(),
                        R.string.fragment_ruta_pendiente_title_seleccion_diferentes_shipper,
                        R.string.fragment_ruta_pendiente_message_seleccion_diferentes_shipper,
                        R.string.text_seleccionar,
                        (dialog, which) -> {
                            codigoShipperSeleccionadas.add(Integer.parseInt(dbRuta.get(position).getShiCodigo()));
                            checkItem(position);
                            showActionMode();
                            configActionMode();
                            view.setTitleActionMode(rutasSeleccionadas.size() + "");
                        },
                        R.string.text_cancelar,
                        (dialog, which) -> dialog.dismiss());
            }
        }

        return isValidShipper;
    }

    /**
     * Receiver
     *
     * {@link RutaPendientePresenter#guardarOrdenGuiasReceiver}
     */
    private void sendOnGuardarOrdenGuiasReceiver() {
        Intent intent = new Intent(LocalAction.GUARDAR_ORDEN_GUIAS_ACTION);
        LocalBroadcastManager.getInstance(AndroidApplication.getAppContext()).sendBroadcast(intent);
    }

}
