package com.urbanoexpress.iridio.presenter;

import android.util.Log;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import com.urbanoexpress.iridio.R;
import com.urbanoexpress.iridio.model.entity.Despacho;
import com.urbanoexpress.iridio.model.entity.ParadaProgramada;
import com.urbanoexpress.iridio.model.entity.PlanDeViaje;
import com.urbanoexpress.iridio.model.interactor.ParadaProgramadaInteractor;
import com.urbanoexpress.iridio.model.interactor.PlanDeViajeInteractor;
import com.urbanoexpress.iridio.model.interactor.callback.RequestCallback;
import com.urbanoexpress.iridio.ui.model.DespachoItem;
import com.urbanoexpress.iridio.util.CommonUtils;
import com.urbanoexpress.iridio.util.Preferences;
import com.urbanoexpress.iridio.util.network.Connection;
import com.urbanoexpress.iridio.view.ParadaProgramadaView;

/**
 * Created by mick on 02/06/16.
 */
public class ParadaProgramadaPresenter {

    private final String TAG = PlanDeViajePresenter.class.getSimpleName();
    private ParadaProgramadaView view;
    private ParadaProgramadaInteractor paradaProgramadaInteractor;

    private List<Despacho> dbDespachoBajadas;
    private List<Despacho> dbDespachoSubidas;

    private boolean activeActionModeDespachoBajada = false,
            activeActionModeDespachoSubida = false;

    private ArrayList<Integer> idDespachosSeleccionados = new ArrayList<>();

    private PlanDeViaje planDeViaje;
    private ParadaProgramada paradaProgramada;

    public interface MenuActionMode {
        int MENU_DESPACHO_BAJADA = 0;
        int MENU_DESPACHO_SUBIDA = 1;
    }

    public ParadaProgramadaPresenter(ParadaProgramadaView view, PlanDeViaje planDeViaje,
                                     ParadaProgramada paradaProgramada) {
        this.view = view;
        this.planDeViaje = planDeViaje;
        this.paradaProgramada = paradaProgramada;
        this.paradaProgramadaInteractor = new ParadaProgramadaInteractor(view.getViewContext());
    }

    public void init() {
        view.setTitleActivity(paradaProgramada.getAgencia());
        showInfoNoLlegaParada();
        getDespachos(false);
    }

    public void onSwipeRefresh() {
        if (isActiveActionModeDespachoBajada() || isActiveActionModeDespachoSubida()) {
            view.setVisibilitySwipeRefreshLayout(false);
        } else {
            getDespachos(true);
        }
    }

    public boolean onBackPressed() {
        resetActionMode();
        return isActiveActionModeDespachoBajada() || isActiveActionModeDespachoSubida();
    }

    public void onClickDespacho(int position, int menuActionMode) {
        if (getMenuActionMode() >= 0 && getMenuActionMode() == menuActionMode) {
            if (isActiveActionModeDespachoBajada() || isActiveActionModeDespachoSubida()) {
                selectDespacho(position, menuActionMode);
                hideActionMode();
            }
        }
    }

    public void onLongClickDespacho(int position, int menuActionMode) {
        Log.d(TAG, "SELECT DESPACHO");
        setActiveActionModeDespacho(menuActionMode, true);
        if (getMenuActionMode() >= 0 && getMenuActionMode() == menuActionMode) {
            if (isActiveActionModeDespachoBajada() || isActiveActionModeDespachoSubida()) {
                Log.d(TAG, "SELECT DESPACHO 2");
                CommonUtils.vibrateDevice(view.getViewContext(), CommonUtils.VIBRATE_LONG_CLICK);
                view.showActionMode(menuActionMode);
                selectDespacho(position, menuActionMode);
                hideActionMode();
            }
        }
    }

    public void onClickSelectAllDespachos() {
        selectAllDespachos(getMenuActionMode());
        hideActionMode();
    }

    public void onClickActionAgregarFotos() {
        if (paradaProgramada.getEstadoLlegada() == ParadaProgramada.Status.LLEGO_AGENCIA) {
            view.navigateToGaleriaParadaProgramadaModal(planDeViaje.getIdPlanViaje(), paradaProgramada.getIdStop(),
                    paradaProgramada.getEstadoLlegada());
        } else if (paradaProgramada.getEstadoLlegada() == ParadaProgramada.Status.SALIO_AGENCIA) {
            view.showToast(R.string.act_parada_programda_msg_no_confirmo_llegada_parada);
        } else {
            view.showToast(R.string.act_parada_programda_message_no_confirmo_llegada_parada);
        }
    }

    public void onClickConfirmarDespachos() {
        updateEstadoDespacho();
    }

    public int getMenuActionMode() {
        if (isActiveActionModeDespachoBajada()) {
            return MenuActionMode.MENU_DESPACHO_BAJADA;
        } else if (isActiveActionModeDespachoSubida()) {
            return MenuActionMode.MENU_DESPACHO_SUBIDA;
        }
        return -1;
    }

    private void showInfoNoLlegaParada() {
        if (paradaProgramada.getEstadoLlegada() == ParadaProgramada.Status.NO_LLEGO_AGENCIA) {
            view.showBoxInfo();
        }
    }

    private void getDespachos(boolean showSwipeRefreshLayout) {
        if (showSwipeRefreshLayout) {
            view.setVisibilitySwipeRefreshLayout(true);
        } else {
            view.showProgressDialog();
        }

        if (Connection.hasNetworkConnectivity(view.getViewContext())) {
            requestGetDespachos(paradaProgramada.getIdStop());
        } else {
            loadDespachosBajadas();
            loadDespachosSubidas();
        }
    }

    private void requestGetDespachos(final String idParada) {
        RequestCallback callback = new RequestCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if (response.getBoolean("success")) {
                        saveDespachos(response.getJSONObject("data"));
                        loadDespachosBajadas();
                        loadDespachosSubidas();
                        updateRevisionParadaProgramada(idParada);
                        view.setVisibilitySwipeRefreshLayout(false);
                        view.dismissProgressDialog();
                    } else {
                        view.setVisibilitySwipeRefreshLayout(false);
                        view.dismissProgressDialog();
                        view.showToast(response.getString("msg_error"));
                    }
                } catch (JSONException ex) {
                    ex.printStackTrace();
                    view.setVisibilitySwipeRefreshLayout(false);
                    view.dismissProgressDialog();
                    view.showToast(R.string.json_object_exception);
                }
            }

            @Override
            public void onError(VolleyError error) {
                view.setVisibilitySwipeRefreshLayout(false);
                view.dismissProgressDialog();
                view.showToast(R.string.volley_error_message);
            }
        };

        String[] params = {
                idParada,
                Preferences.getInstance().getString("idUsuario", "")
        };

        paradaProgramadaInteractor.getDespachos(params, callback);
    }

    private void updateEstadoDespacho() {
        view.showProgressDialog();

        if (validateConfirmarDespachos()) {
            RequestCallback callback = new RequestCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    try {
                        if (response.getBoolean("success")) {
                            saveAllCheckedDespachos(dbDespachoBajadas);
                            saveAllCheckedDespachos(dbDespachoSubidas);
                            resetActionMode();
                            hideActionMode();
                            view.dismissProgressDialog();
                            view.showToast(R.string.act_plan_de_viaje_message_success_confirmar_despachos);
                        } else {
                            view.dismissProgressDialog();
                            view.showToast(response.getString("msg_error"));
                        }
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                        view.dismissProgressDialog();
                        view.showToast(R.string.json_object_exception);
                    }
                }

                @Override
                public void onError(VolleyError error) {
                    view.dismissProgressDialog();
                    view.showToast(R.string.volley_error_message);
                }
            };

            int estado = 0;
            String id_parada = "", id_destino = "";

            switch (getMenuActionMode()) {
                case MenuActionMode.MENU_DESPACHO_BAJADA:
                    estado = Despacho.Status.DESCARGADO;
                    id_parada = dbDespachoBajadas.get(0).getIdParada();
                    id_destino = buildParamIdDestinos(dbDespachoBajadas);
                    break;
                case MenuActionMode.MENU_DESPACHO_SUBIDA:
                    estado = Despacho.Status.SUBIDO;
                    id_parada = dbDespachoSubidas.get(0).getIdParada();
                    id_destino = buildParamIdDestinos(dbDespachoSubidas);
                    break;
            }

            String[] params = {
                    id_parada,
                    id_destino,
                    buildParamIdDespachos(),
                    estado + "",
                    Preferences.getInstance().getString("idUsuario", "")
            };

            paradaProgramadaInteractor.updateEstadoDespachos(params, callback);
        }
    }

    private boolean validateConfirmarDespachos() {
        if (!validatePlanDeViajeTerminado()) {
            if (validateConfirmacionLlegadaParada()) {
                return true;
            }
        }
        view.dismissProgressDialog();
        return false;
    }

    private boolean validateConfirmacionLlegadaParada() {
        ParadaProgramada paradaProgramada;

        if (isActiveActionModeDespachoBajada()) {
            paradaProgramada = paradaProgramadaInteractor.selectParadaProgramadaById(
                    dbDespachoBajadas.get(0).getIdParada());
        } else {
            paradaProgramada = paradaProgramadaInteractor.selectParadaProgramadaById(
                    dbDespachoSubidas.get(0).getIdParada());
        }

        if (paradaProgramada.getEstadoLlegada() == ParadaProgramada.Status.LLEGO_AGENCIA) {
            return true;
        } else {
            if (paradaProgramada.getEstadoLlegada() == ParadaProgramada.Status.SALIO_AGENCIA) {
                view.showToast(R.string.act_parada_programda_message_no_puede_confirmar_porque_salio_agencia);
            } else {
                view.showToast(R.string.act_parada_programda_message_no_confirmo_llegada_parada);
            }
        }

        return false;
    }

    private boolean validatePlanDeViajeTerminado() {
        if (existPlanDeViaje()) {
            if (planDeViaje.getEstadoRecorrido() == PlanDeViaje.EstadoRuta.TERMINO_RUTA) {
                view.showToast(R.string.act_parada_programda_message_no_puede_confirmar_porque_finalizo_plan_viaje);
                return true;
            }
        } else {
            view.showToast(R.string.act_plan_de_viaje_no_hay_plan_de_viaje);
            return true;
        }
        return false;
    }

    private boolean existPlanDeViaje() {
        return planDeViaje != null;
    }

    private String buildParamIdDespachos() {
        String paramIdDespachos = "";

        for (int i = 0; i < idDespachosSeleccionados.size(); i++) {
            paramIdDespachos += idDespachosSeleccionados.get(i) + "|";
        }

        return paramIdDespachos;
    }

    private String buildParamIdDestinos(List<Despacho> despachos) {
        String paramIdDestinos = "";

        for (int i = 0; i < idDespachosSeleccionados.size(); i++) {
            for (int j = 0; j < despachos.size(); j++) {
                if (despachos.get(j).getIdDespacho().equals(idDespachosSeleccionados.get(i).toString())) {
                    paramIdDestinos += despachos.get(j).getIdDestino() + "|";
                }
            }
        }

        return paramIdDestinos;
    }

    private void saveAllCheckedDespachos(List<Despacho> despachos) {
        for (int i = 0; i < despachos.size(); i++) {
            despachos.get(i).save();
            Log.d(TAG, "DESPACHO GUARDADO");
        }
    }

    private void saveDespachos(JSONObject data) throws JSONException {
        readJSONArrayAndSaveDespachos(data.getJSONArray("bajadas"), Despacho.Type.DESPACHO_BAJADA);
        readJSONArrayAndSaveDespachos(data.getJSONArray("subidas"), Despacho.Type.DESPACHO_SUBIDA);
    }

    private void loadDespachosBajadas() {
        dbDespachoBajadas = PlanDeViajeInteractor.selectDespachoByIdParada(
                paradaProgramada.getIdStop(), Despacho.Type.DESPACHO_BAJADA + "");
        List<DespachoItem> items = buildDespachoItems(dbDespachoBajadas, R.drawable.ic_arrow_down_black);
        view.showDatosDespachoBajadas(items);
    }

    private void loadDespachosSubidas() {
        dbDespachoSubidas = PlanDeViajeInteractor.selectDespachoByIdParada(
                paradaProgramada.getIdStop(), Despacho.Type.DESPACHO_SUBIDA + "");

        if (isParadaHub()) {
            for (Despacho despacho: dbDespachoSubidas) {
                despacho.setProcesoDespacho(Despacho.Status.DESPACHADO);
                despacho.save();
            }
        }

        List<DespachoItem> items = buildDespachoItems(dbDespachoSubidas, R.drawable.ic_arrow_up_black);
        view.showDatosDespachoSubidas(items);
    }

    private void readJSONArrayAndSaveDespachos(JSONArray despachos, int tipoDespacho) throws JSONException{
        Despacho despacho;
        JSONObject jsonObject;

        List<Despacho> despachosDB = PlanDeViajeInteractor.selectDespachoByIdParada(
                paradaProgramada.getIdStop(), tipoDespacho + "");

        Log.d(TAG, "TOTAL DESPACHOS: " + despachosDB.size());
        if (despachosDB.size() > 0) {
            Log.d(TAG, "ACTUALIZAR DESPACHOS");
            for (int i = 0; i < despachos.length(); i++) {
                jsonObject = despachos.getJSONObject(i);
                try {
                    despacho = getDespachoByID(despachosDB, jsonObject.getString("id_despacho"));
                    despacho.setIdDespacho(jsonObject.getString("id_despacho"));
                    despacho.setIdParada(jsonObject.getString("id_parada"));
                    despacho.setOrigen(jsonObject.getString("origen"));
                    despacho.setDestino(jsonObject.getString("destino"));
                    despacho.setPiezas(jsonObject.getString("piezas"));
                    despacho.setGuias(jsonObject.getString("guias"));
                    despacho.setEstado(jsonObject.getString("estado"));
                    despacho.save();
                } catch (NullPointerException ex) {
                    ex.printStackTrace();
                    jsonObject = despachos.getJSONObject(i);
                    saveDespacho(jsonObject, tipoDespacho);
                }
            }
        } else {
            Log.d(TAG, "GUARDAR DESPACHOS");
            for (int i = 0; i < despachos.length(); i++) {
                jsonObject = despachos.getJSONObject(i);
                saveDespacho(jsonObject, tipoDespacho);
            }
        }
    }

    private void saveDespacho(JSONObject json, int tipoDespacho) throws JSONException {
        Despacho despacho = new Despacho(
                Preferences.getInstance().getString("idUsuario", ""),
                json.getString("id_despacho"),
                json.getString("id_parada"),
                json.getString("id_origen"),
                json.getString("id_destino"),
                json.getString("origen"),
                json.getString("destino"),
                json.getString("piezas"),
                json.getString("guias"),
                json.getString("estado"),
                tipoDespacho,
                Despacho.Status.NO_DESPACHADO
        );
        despacho.save();
    }

    private Despacho getDespachoByID(List<Despacho> despachos, String idDespacho) {
        for (int i = 0; i < despachos.size(); i++) {
            if (despachos.get(i).getIdDespacho().equals(idDespacho)) {
                return despachos.get(i);
            }
        }
        return null;
    }

    private void updateRevisionParadaProgramada(String idParada) {
        ParadaProgramada paradaProgramada = paradaProgramadaInteractor
                .selectParadaProgramadaById(idParada);
        paradaProgramada.setEstadoDespachosRevisado(ParadaProgramada.Status.REVISO_DESPACHOS);
        paradaProgramada.save();
    }

    private void selectDespacho(int position, int menuActionMode) {
        List<DespachoItem> items;
        switch (menuActionMode) {
            case MenuActionMode.MENU_DESPACHO_BAJADA:
                checkProcesoDespacho(dbDespachoBajadas, position);
                items = buildDespachoItems(dbDespachoBajadas, R.drawable.ic_arrow_down_black);
                view.showDatosDespachoBajadas(items);
                break;
            case MenuActionMode.MENU_DESPACHO_SUBIDA:
                checkProcesoDespacho(dbDespachoSubidas, position);
                items = buildDespachoItems(dbDespachoSubidas, R.drawable.ic_arrow_up_black);
                view.showDatosDespachoSubidas(items);
                break;
        }
        view.setTitleActionMode(idDespachosSeleccionados.size() + "");
    }

    private void selectAllDespachos(int menuActionMode) {
        List<DespachoItem> items;
        switch (menuActionMode) {
            case MenuActionMode.MENU_DESPACHO_BAJADA:
                checkAllProcesoDespacho(dbDespachoBajadas);
                items = buildDespachoItems(dbDespachoBajadas, R.drawable.ic_arrow_down_black);
                view.showDatosDespachoBajadas(items);
                break;
            case MenuActionMode.MENU_DESPACHO_SUBIDA:
                checkAllProcesoDespacho(dbDespachoSubidas);
                items = buildDespachoItems(dbDespachoSubidas, R.drawable.ic_arrow_up_black);
                view.showDatosDespachoSubidas(items);
                break;
        }
        view.setTitleActionMode(idDespachosSeleccionados.size() + "");
    }

    private void checkProcesoDespacho(List<Despacho> despachos, int position) {
        if (despachos.get(position).getProcesoDespacho() == Despacho.Status.DESPACHADO) {
            despachos.get(position).setProcesoDespacho(Despacho.Status.NO_DESPACHADO);
            removeSelectedDespacho(Integer.parseInt(despachos.get(position).getIdDespacho()));
        } else {
            despachos.get(position).setProcesoDespacho(Despacho.Status.DESPACHADO);
            addSelectedDespacho(Integer.parseInt(despachos.get(position).getIdDespacho()));
        }
    }

    private void checkAllProcesoDespacho(List<Despacho> despachos) {
        idDespachosSeleccionados.clear();

        if (isSelectAllDespachos(despachos)) {
            for (int i = 0; i < despachos.size(); i++) {
                despachos.get(i).setProcesoDespacho(Despacho.Status.NO_DESPACHADO);
                removeSelectedDespacho(Integer.parseInt(despachos.get(i).getIdDespacho()));
            }
        } else {
            for (int i = 0; i < despachos.size(); i++) {
                despachos.get(i).setProcesoDespacho(Despacho.Status.DESPACHADO);
                addSelectedDespacho(Integer.parseInt(despachos.get(i).getIdDespacho()));
            }
        }
    }

    private boolean isSelectAllDespachos(List<Despacho> despachos) {
        for (int i = 0; i < despachos.size(); i++) {
            if (despachos.get(i).getProcesoDespacho() != Despacho.Status.DESPACHADO) {
                return false;
            }
        }
        return true;
    }

    private void addSelectedDespacho(int idDespacho){
        idDespachosSeleccionados.add(idDespacho);
    }

    private void removeSelectedDespacho(int idDespacho){
        for (int i = 0; i < idDespachosSeleccionados.size(); i++) {
            if (idDespachosSeleccionados.get(i) == idDespacho) {
                idDespachosSeleccionados.remove(i);
                return;
            }
        }
    }

    private void hideActionMode() {
        if (idDespachosSeleccionados.size() == 0) {
            view.hideActionMode();
            activeActionModeDespachoBajada = false;
            activeActionModeDespachoSubida = false;
        }
    }

    private List<DespachoItem> buildDespachoItems(List<Despacho> despachos, int iconArrow) {
        List<DespachoItem> items = new ArrayList<>();

        for (int i = 0; i < despachos.size(); i++) {
            boolean despachoSelected = (despachos.get(i).getProcesoDespacho() == Despacho.Status.DESPACHADO);

            Log.d(TAG, "DESPACHO PROCESO DESPACHO: " + despachos.get(i).getProcesoDespacho());
            Log.d(TAG, "DESPACHO SELECTED: " + despachoSelected);

            items.add(new DespachoItem(
                    despachos.get(i).getIdDespacho(),
                    despachos.get(i).getOrigen(),
                    despachos.get(i).getDestino(),
                    despachos.get(i).getPiezas(),
                    despachos.get(i).getGuias(),
                    iconArrow,
                    despachoSelected
            ));
        }

        return items;
    }

    private void setActiveActionModeDespacho(int menuActionMode, boolean active) {
        switch (menuActionMode) {
            // Activar menu siempre en cuando el otro menu este inactivo
            case MenuActionMode.MENU_DESPACHO_BAJADA:
                if (active) { // Si se quiere activar primero validar si el otro menu esta activo
                    if (!activeActionModeDespachoSubida) {
                        activeActionModeDespachoBajada = active;
                    }
                } else {
                    activeActionModeDespachoBajada = active;
                }
                break;
            case MenuActionMode.MENU_DESPACHO_SUBIDA:
                if (active) {
                    if (!activeActionModeDespachoBajada) {
                        activeActionModeDespachoSubida = active;
                    }
                } else {
                    activeActionModeDespachoSubida = active;
                }
                break;
        }
    }

    private boolean isActiveActionModeDespachoBajada() {
        return activeActionModeDespachoBajada;
    }

    private boolean isActiveActionModeDespachoSubida() {
        return activeActionModeDespachoSubida;
    }

    private boolean isParadaHub() {
        return paradaProgramada.getTipo().equalsIgnoreCase("U");
    }

    private void resetActionMode() {
        if (isActiveActionModeDespachoBajada()){
            setActiveActionModeDespacho(MenuActionMode.MENU_DESPACHO_BAJADA, false);
            loadDespachosBajadas();
        }
        if (isActiveActionModeDespachoSubida()) {
            setActiveActionModeDespacho(MenuActionMode.MENU_DESPACHO_SUBIDA, false);
            loadDespachosSubidas();
        }
        idDespachosSeleccionados.clear();
    }
}
