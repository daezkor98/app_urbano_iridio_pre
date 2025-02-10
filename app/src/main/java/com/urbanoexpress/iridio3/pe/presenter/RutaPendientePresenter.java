package com.urbanoexpress.iridio3.pe.presenter;

import static java.util.Collections.singletonList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.orm.util.NamingHelper;
import com.urbanoexpress.iridio3.pe.util.async.AsyncTaskCoroutine;
import com.urbanoexpress.iridio3.pe.R;
import com.urbanoexpress.iridio3.pe.model.entity.Data;
import com.urbanoexpress.iridio3.pe.model.entity.DescargaRuta;
import com.urbanoexpress.iridio3.pe.model.entity.EstadoRuta;
import com.urbanoexpress.iridio3.pe.model.entity.GuiaGestionada;
import com.urbanoexpress.iridio3.pe.model.entity.Imagen;
import com.urbanoexpress.iridio3.pe.model.entity.Pieza;
import com.urbanoexpress.iridio3.pe.model.entity.Ruta;
import com.urbanoexpress.iridio3.pe.model.entity.RutaEliminada;
import com.urbanoexpress.iridio3.pe.model.entity.SecuenciaRuta;
import com.urbanoexpress.iridio3.pe.model.interactor.ConsideracionesImportantesRutaInteractor;
import com.urbanoexpress.iridio3.pe.model.interactor.RutaPendienteInteractor;
import com.urbanoexpress.iridio3.pe.model.interactor.callback.RequestCallback;
import com.urbanoexpress.iridio3.pe.model.util.ModelUtils;
import com.urbanoexpress.iridio3.pe.presenter.helpers.ActionMenuRutaPendienteHelper;
import com.urbanoexpress.iridio3.pe.presenter.helpers.ForzarCierreRutaHelper;
import com.urbanoexpress.iridio3.pe.ui.DetalleRutaRuralActivity;
import com.urbanoexpress.iridio3.pe.ui.RutaActivity;
import com.urbanoexpress.iridio3.pe.ui.model.RutaItem;
import com.urbanoexpress.iridio3.pe.util.CommonUtils;
import com.urbanoexpress.iridio3.pe.util.LocationUtils;
import com.urbanoexpress.iridio3.pe.util.MyLocation;
import com.urbanoexpress.iridio3.pe.util.OnTouchItemRutasListener;
import com.urbanoexpress.iridio3.pe.util.Preferences;
import com.urbanoexpress.iridio3.pe.util.Session;
import com.urbanoexpress.iridio3.pe.util.constant.LocalAction;
import com.urbanoexpress.iridio3.pe.view.BaseModalsView;
import com.urbanoexpress.iridio3.pe.view.RutaPendienteView;

import org.apache.commons.text.WordUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by mick on 22/06/16.
 */
public class RutaPendientePresenter implements OnTouchItemRutasListener {

    private static final String TAG = RutaPendientePresenter.class.getSimpleName();

    private RutaPendienteView view;
    private RutaPendienteInteractor interactor;

    private List<RutaItem> rutaItems;
    private List<Ruta> dbRuta;

    private Ruta rutaEliminada = null;

    private int positionRutaEliminada = -1;

    private boolean itemMove = false;

    private boolean visibleModalNuevaRutaAsignada = false;
    private boolean addMinutosRefrigerio = true;

    private ActionMenuRutaPendienteHelper actionMenuRutaPendienteHelper;

    private boolean isMostrarAlerta = false;

    public RutaPendientePresenter(RutaPendienteView view) {
        this.view = view;
        interactor = new RutaPendienteInteractor(view.getViewContext());
        actionMenuRutaPendienteHelper = new ActionMenuRutaPendienteHelper(view);
        init();
    }

    public void init() {
        LocalBroadcastManager.getInstance(view.getViewContext())
                .registerReceiver(descargaFinalizadaReceiver, new IntentFilter("OnDescargaFinalizada"));
        LocalBroadcastManager.getInstance(view.getViewContext())
                .registerReceiver(rutaFinalizadaReceiver, new IntentFilter("OnRutaFinalizada"));
        LocalBroadcastManager.getInstance(view.getViewContext())
                .registerReceiver(manifiestoEliminadoReceiver, new IntentFilter("OnManifiestoEliminado"));
        LocalBroadcastManager.getInstance(view.getViewContext())
                .registerReceiver(transferenciaGuiaFinalizadaReceiver,
                        new IntentFilter(LocalAction.TRANSFERENCIA_GUIA_FINALIZADA_ACTION));
        LocalBroadcastManager.getInstance(view.getViewContext())
                .registerReceiver(rutaIniciadaReceiver,
                        new IntentFilter(LocalAction.INICIAR_RUTA_DEL_DIA_ACTION));
        LocalBroadcastManager.getInstance(view.getViewContext())
                .registerReceiver(definirPosicionGuiaReceiver,
                        new IntentFilter(LocalAction.DEFINIR_POSICION_DE_GUIA_ACTION));
        LocalBroadcastManager.getInstance(view.getViewContext())
                .registerReceiver(guardarOrdenGuiasReceiver,
                        new IntentFilter(LocalAction.GUARDAR_ORDEN_GUIAS_ACTION));
        LocalBroadcastManager.getInstance(view.getViewContext())
                .registerReceiver(sincronizarNumeroTelefonoReceiver,
                        new IntentFilter(LocalAction.SINCRONIZAR_NUMERO_TELEFONO_LISTA_GUIAS_PENDIENTES_ACTION));
        LocalBroadcastManager.getInstance(view.getViewContext())
                .registerReceiver(buscarGuiaReceiver,
                        new IntentFilter(LocalAction.BUSCAR_GUIA_ACTION));

        new LoadGETask(false, true).execute();
    }

    @Override
    public boolean onItemMove(final int fromPosition, final int toPosition) {
        Log.d(TAG, "MOVE ITEM RUTA");
        Log.d(TAG, "From: " + fromPosition);
        Log.d(TAG, "To: " + toPosition);

        try {
            itemMove = true;

            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(dbRuta, i, i + 1);
                    Collections.swap(rutaItems, i, i + 1);
                }
                Log.d(TAG, "FINISH REORDER LIST GUIAS");
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(dbRuta, i, i - 1);
                    Collections.swap(rutaItems, i, i - 1);
                }
                Log.d(TAG, "FINISH REORDER LIST GUIAS");
            }
        } catch (IndexOutOfBoundsException ex) {
            ex.printStackTrace();
        }

        return false;
    }

    @Override
    public void onItemDismiss(final int position) {
        Log.d(TAG, "DELETE ITEM RUTA");
        Log.d(TAG, "Position: " + position);
        Log.d(TAG, "");

        new Thread(() -> {
            positionRutaEliminada = position;

            rutaEliminada = dbRuta.get(position);
            rutaEliminada.setEliminado(Data.Delete.YES);
            rutaEliminada.save();

            RutaEliminada rEliminado = new RutaEliminada(
                    Preferences.getInstance().getString("idUsuario", ""),
                    rutaEliminada.getIdServicio()
            );
            rEliminado.save();

            dbRuta.remove(position);

            if (view.getViewContext() != null) {
                ((AppCompatActivity) view.getViewContext()).runOnUiThread(() ->
                        view.showSnackBar(R.string.fragment_ruta_pendiente_message_ruta_eliminada));
            }
        }).start();
    }

    @Override
    public void onItemSelect(View view, int position, boolean isSelected) {

    }

    @Override
    public void onItemSelectChanged(RecyclerView.ViewHolder view, int actionState) {
        Log.d(TAG, "onItemSelectChanged");
        if (actionState == ItemTouchHelper.ACTION_STATE_IDLE && itemMove) {
            this.view.showProgressDialog(R.string.fragment_ruta_pendiente_message_ordenando_guias);
            updateSecuenciaAllRutasPendientes();
            registerNewSecuencia();
            itemMove = false;
        }
    }

    public void onSwipeRefresh() {
        if (itemMove) {
            view.setVisibilitySwipeRefreshLayout(false);
        } else {
            if (CommonUtils.validateConnectivity(view.getViewContext())) {
                getRutas();
            } else {
                view.setVisibilitySwipeRefreshLayout(false);
            }
        }
    }

    public void onSelectedItem(int position) {
        try {
            actionMenuRutaPendienteHelper.selectItem(position);
        } catch (IndexOutOfBoundsException ex) {
            ex.printStackTrace();
        }
    }

    public void onClickHomeButtonOnSelectedItems() {
        actionMenuRutaPendienteHelper.deselectAllItems();
    }

    public void onActionGestionMultiple() {
        if (isIniciadoRuta()) {
            actionMenuRutaPendienteHelper.gestionMultiple();
        }
    }

    public void onActionSelectAllRutasPendientes() {
        actionMenuRutaPendienteHelper.selectAllItems();
    }

    public void onActionOrdenarGuias() {
        actionMenuRutaPendienteHelper.ordenarGuias();
    }

    public void onActionDefinirPosicionGuia() {
        actionMenuRutaPendienteHelper.definirPosicionGuia();
    }

    public void onActionGuardarOrdenGuias() {
        actionMenuRutaPendienteHelper.guardarOrdenGuias();
    }

    public void onActionDeleteRuta() {
        actionMenuRutaPendienteHelper.deleteItems();
    }

    public void onClickItem(int position) {
        try {
            AppCompatActivity activity = (AppCompatActivity) view.getViewContext();
            Bundle args = new Bundle();
            args.putSerializable("guias", dbRuta.get(position));
            args.putInt("numVecesGestionado", 1);
            activity.startActivity(new Intent(activity, DetalleRutaRuralActivity.class).putExtra("args", args));
            activity.overridePendingTransition(R.anim.slide_enter_from_right, R.anim.not_slide);
        } catch (IndexOutOfBoundsException ex) {
            ex.printStackTrace();
        }
    }

    public void onClickImportePorCobrar(int position) {
        view.showSnackBar("Importe por cobrar: " + ModelUtils.getSimboloMoneda(view.getViewContext()) +
                " " + dbRuta.get(position).getImporte());
    }

    public void onClickTipoEnvio(int position) {
        if (dbRuta.get(position).getGuiaRequerimiento() != null) {
            if (dbRuta.get(position).getGuiaRequerimiento().equals("1")) {
                if (dbRuta.get(position).getGuiaRequerimientoCHK() != null) {
                    if (dbRuta.get(position).getGuiaRequerimientoCHK().equals("22")) {
                        String comentarios = dbRuta.get(position).getGuiaRequerimientoComentario().trim().toLowerCase();

                        if (comentarios.isEmpty()) {
                            comentarios = "ninguno";
                        }

                        String message = "La guía tiene un requerimiento con los siguientes datos:" +
                                "\n\nComentarios: " + comentarios +
                                "\n\nHorario de entrega: " + dbRuta.get(position).getGuiaRequerimientoHorario().trim().toLowerCase();

                        if (Integer.parseInt(dbRuta.get(position).getGuiaRequerimientoNuevaDireccion()) == 1) {
                            message += "\n\nNueva dirección: " + dbRuta.get(position).getDireccion();
                        }

                        BaseModalsView.showAlertDialog(this.view.getViewContext(),
                                dbRuta.get(position).getGuiaRequerimientoMotivo().trim(),
                                message,
                                this.view.getViewContext().getString(R.string.text_aceptar), null);
                    } else if (dbRuta.get(position).getGuiaRequerimientoCHK().equals("30")) {
                        String message = "Esta guía por motivo de " +
                                dbRuta.get(position).getGuiaRequerimientoMotivo().toUpperCase() + ", debe ser devuelta al shipper.";

                        BaseModalsView.showAlertDialog(this.view.getViewContext(),
                                "Devolución al Shipper",
                                message,
                                this.view.getViewContext().getString(R.string.text_aceptar), null);
                    }
                } else {
                    // Icono a modo de compatibilidad a versiones de iridio que no soportan requirimiento chk.
                    String comentarios = dbRuta.get(position).getGuiaRequerimientoComentario().trim().toLowerCase();

                    if (comentarios.isEmpty()) {
                        comentarios = "ninguno";
                    }

                    String message = "La guía tiene un requerimiento con los siguientes datos:" +
                            "\n\nComentarios: " + comentarios +
                            "\n\nHorario de entrega: " + dbRuta.get(position).getGuiaRequerimientoHorario().trim().toLowerCase();

                    if (Integer.parseInt(dbRuta.get(position).getGuiaRequerimientoNuevaDireccion()) == 1) {
                        message += "\n\nNueva dirección: " + dbRuta.get(position).getDireccion();
                    }

                    BaseModalsView.showAlertDialog(this.view.getViewContext(),
                            dbRuta.get(position).getGuiaRequerimientoMotivo().trim(),
                            message,
                            this.view.getViewContext().getString(R.string.text_aceptar), null);
                }
            }
        }
    }

    public void onDestroyActivity() {
        LocalBroadcastManager.getInstance(view.getViewContext())
                .unregisterReceiver(descargaFinalizadaReceiver);
        LocalBroadcastManager.getInstance(view.getViewContext())
                .unregisterReceiver(rutaFinalizadaReceiver);
        LocalBroadcastManager.getInstance(view.getViewContext())
                .unregisterReceiver(manifiestoEliminadoReceiver);
        LocalBroadcastManager.getInstance(view.getViewContext())
                .unregisterReceiver(transferenciaGuiaFinalizadaReceiver);
        LocalBroadcastManager.getInstance(view.getViewContext())
                .unregisterReceiver(rutaIniciadaReceiver);
        LocalBroadcastManager.getInstance(view.getViewContext())
                .unregisterReceiver(definirPosicionGuiaReceiver);
        LocalBroadcastManager.getInstance(view.getViewContext())
                .unregisterReceiver(guardarOrdenGuiasReceiver);
        LocalBroadcastManager.getInstance(view.getViewContext())
                .unregisterReceiver(sincronizarNumeroTelefonoReceiver);
        LocalBroadcastManager.getInstance(view.getViewContext())
                .unregisterReceiver(buscarGuiaReceiver);
    }

    private void getRutas() {
        view.setVisibilitySwipeRefreshLayout(true);
        RequestCallback callback = new RequestCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                new SaveGETask().execute(response);
            }

            @Override
            public void onError(VolleyError error) {
                new LoadGETask(false, false).execute();
                if (error.networkResponse!=null){
                    if (error.networkResponse.statusCode == 403) {
                        view.showAuthenticationError();
                    }else if(error.networkResponse.statusCode == 401){
                        view.showAuthenticationError();

                    } else{
                        view.showSnackBar(R.string.volley_error_message);
                    }
                }else{
                    view.showSnackBar(R.string.volley_error_message);
                }
            }
        };

        String[] params = new String[]{
                Preferences.getInstance().getInt("idRuta", 0) + "",
                Preferences.getInstance().getString("lineaValores", ""),
                Preferences.getInstance().getString("lineaLogistica", ""),
                Preferences.getInstance().getString("lineaLogisticaEspecial", ""),
                Preferences.getInstance().getString("idUsuario", ""),
                Session.getUser().getDevicePhone()
        };

        interactor.getRutas(params, callback);
    }

    private class SaveGETask extends AsyncTaskCoroutine<JSONObject, String> {

        JSONObject response;

        @Override
        public String doInBackground(JSONObject... jsonObjects) {
            response = jsonObjects[0];

            try {
                if (response.getBoolean("success")) {
                    visibleModalNuevaRutaAsignada = false;
                    saveRutas(response.getJSONArray("data"));
                } else {
                    final String msg = response.getString("msg_error");
                    if (view.getViewContext() != null) {
                        ((AppCompatActivity) view.getViewContext()).runOnUiThread(() ->
                                view.showToast(msg));
                    }
                }
            } catch (JSONException ex) {
                ex.printStackTrace();
                if (view.getViewContext() != null) {
                    ((AppCompatActivity) view.getViewContext()).runOnUiThread(() ->
                            view.showSnackBar(R.string.json_object_exception));
                }
            }

            return null;
        }

        @Override
        public void onPostExecute(String s) {
            super.onPostExecute(s);
            new LoadGETask(false, false).execute();
        }
    }

    private void saveRutas(JSONArray jsonRutas) throws JSONException {
        dbRuta = interactor.selectAllRutas();
        if (dbRuta.size() > 0) {
            if (existeRutasPendientes(jsonRutas)) {
                Log.d("PRUEBARUTA", "RUTAS SIN PROCESAR: " + dbRuta.size());
                ArrayList<Ruta> rutasActuales = getRutasActuales(dbRuta);
                Log.d("PRUEBARUTA", "RUTAS ACTUALES: " + rutasActuales.size());
                ArrayList<JSONObject> nuevasRutas = getNuevasRutas(jsonRutas);
                Log.d("PRUEBARUTA", "RUTAS NUEVAS: " + nuevasRutas.size());


                for (int i = 0; i < nuevasRutas.size(); i++) {

                    boolean nuevaRutaRegistrada = false;

                    for (int j = 0; j < rutasActuales.size(); j++) {

                        if (rutasActuales.get(j).getLineaNegocio().equals(
                                nuevasRutas.get(i).getString("linea_negocio"))) {

                            Log.d("PRUEBARUTA", "PRIMER IF");

                            nuevaRutaRegistrada = true;

                            JSONArray jsonGuias = getNuevasGuiasByLineaNegocio(
                                    jsonRutas, rutasActuales.get(j).getLineaNegocio());
                            Log.d("PRUEBARUTA", "Guias Nuevas: " + jsonGuias.length());

                            if (isNuevaFechaRuta(rutasActuales.get(j).getIdRuta(),
                                    rutasActuales.get(j).getFechaRuta(), jsonGuias)) {

                                Log.d("PRUEBARUTA", "SEGUNDO IF");

                                boolean newGuias = false;
                                int tipoCalculoHorarioAproximado = 0;

                                for (int k = 0; k < jsonGuias.length(); k++) {
                                    JSONObject jsonRuta = jsonGuias.getJSONObject(k);

                                    // Verificar si la guia esta eliminada
                                    if (jsonRuta.getString("mot_id").equals("246")) {
                                        newGuias = true;
                                        tipoCalculoHorarioAproximado = 1;

                                        eliminarGuiaListaPendientes(jsonRuta);

                                        deleteGEAnuladas(jsonRuta);
                                    } else {
                                        if (!isRutaSaved(jsonRuta)) {
                                            newGuias = true;
                                            tipoCalculoHorarioAproximado = 2;

                                            saveRuta(jsonRuta);
                                        }
                                    }
                                }

                                if (newGuias) {
                                    // Calcular los horarios aproximados de las guias
                                    switch (tipoCalculoHorarioAproximado) {
                                        case 1:
                                            updateSecuenciaAllRutasPendientes();
                                            break;
                                        case 2:
                                            calcularHorarioAproximadoNuevasGE(jsonGuias.length());
                                            break;
                                    }

                                    registerNewSecuencia();

                                    if (jsonGuias.length() > 0) {
                                        JSONObject jsonRuta = jsonGuias.getJSONObject(0);
                                        registerEstadoRuta(
                                                jsonRuta.getString("ruta_id"),
                                                jsonRuta.getString("linea_negocio"));
                                    }

                                    if (view.getViewContext() != null) {
                                        ((AppCompatActivity) view.getViewContext()).runOnUiThread(() ->
                                                ((RutaActivity) view.getViewContext()).showSnackBarEstadoRuta());
                                    }
                                }
                            } else {
                                if (!visibleModalNuevaRutaAsignada) {
                                    if (view.getViewContext() != null) {
                                        ((AppCompatActivity) view.getViewContext()).runOnUiThread(() -> {
                                            try {
                                                view.showMessageNuevaRutaAsignada();
                                            } catch (WindowManager.BadTokenException ex) {
                                                ex.printStackTrace();
                                            }
                                        });
                                        visibleModalNuevaRutaAsignada = true;
                                    }
                                }
                            }
                        }
                    }

                    if (!nuevaRutaRegistrada) {
                        Log.d("PRUEBARUTA", "Ruta No Registrada, Id Ruta: " +
                                nuevasRutas.get(i).getString("ruta_id") + " " +
                                "Linea: " + nuevasRutas.get(i).getString("linea_negocio"));

                        JSONArray jsonGuias = getNuevasGuiasByLineaNegocio(
                                jsonRutas, nuevasRutas.get(i).getString("linea_negocio"));
                        Log.d("PRUEBARUTA", "Guias Nuevas: " + jsonGuias.length());

                        if (isNuevaFechaRuta(rutasActuales.get(0).getIdRuta(),
                                rutasActuales.get(0).getFechaRuta(), jsonGuias)) {

                            boolean newGuias = false;
                            int tipoCalculoHorarioAproximado = 0;

                            for (int k = 0; k < jsonGuias.length(); k++) {
                                JSONObject jsonRuta = jsonGuias.getJSONObject(k);

                                // Verificar si la guia esta eliminada
                                if (jsonRuta.getString("mot_id").equals("246")) {
                                    newGuias = true;
                                    tipoCalculoHorarioAproximado = 1;

                                    eliminarGuiaListaPendientes(jsonRuta);

                                    deleteGEAnuladas(jsonRuta);
                                } else {
                                    if (!isRutaSaved(jsonRuta)) {
                                        newGuias = true;
                                        tipoCalculoHorarioAproximado = 2;

                                        saveRuta(jsonRuta);
                                    }
                                }
                            }

                            if (newGuias) {
                                // Calcular los horarios aproximados de las guias
                                switch (tipoCalculoHorarioAproximado) {
                                    case 1:
                                        updateSecuenciaAllRutasPendientes();
                                        break;
                                    case 2:
                                        calcularHorarioAproximadoNuevasGE(jsonGuias.length());
                                        break;
                                }

                                registerNewSecuencia();

                                if (jsonGuias.length() > 0) {
                                    JSONObject jsonRuta = jsonGuias.getJSONObject(0);
                                    registerEstadoRuta(
                                            jsonRuta.getString("ruta_id"),
                                            jsonRuta.getString("linea_negocio"));
                                }

                                if (view.getViewContext() != null) {
                                    ((AppCompatActivity) view.getViewContext()).runOnUiThread(() ->
                                            ((RutaActivity) view.getViewContext()).showSnackBarEstadoRuta());
                                }
                            }
                        } else {
                            if (!visibleModalNuevaRutaAsignada) {
                                if (view.getViewContext() != null) {
                                    ((AppCompatActivity) view.getViewContext()).runOnUiThread(() -> {
                                        try {
                                            view.showMessageNuevaRutaAsignada();
                                        } catch (WindowManager.BadTokenException ex) {
                                            ex.printStackTrace();
                                        }
                                    });
                                    visibleModalNuevaRutaAsignada = true;
                                }
                            }
                        }
                    }
                }
            } else {
                if (view.getViewContext() != null) {
                    ((AppCompatActivity) view.getViewContext()).runOnUiThread(() ->
                            view.showToast(R.string.fragment_ruta_pendiente_message_no_hay_nuevas_rutas));
                }
            }
        } else {
            if (existeRutasPendientes(jsonRutas)) {
                boolean newGuias = false;

                for (int i = 0; i < jsonRutas.length(); i++) {
                    JSONObject jsonRuta = jsonRutas.getJSONObject(i);

                    // Verificar si la guia esta eliminada
                    if (!jsonRuta.getString("mot_id").equals("246")) {
                        if (!isRutaSaved(jsonRuta)) {
                            newGuias = true;
                            saveRuta(jsonRuta);
                        }
                    }
                }

                if (newGuias) {
                    registerNewSecuencia();
                    if (view.getViewContext() != null) {
                        ((AppCompatActivity) view.getViewContext()).runOnUiThread(() ->
                                ((RutaActivity) view.getViewContext()).showSnackBarEstadoRuta());
                    }
                }
            } else {
                if (view.getViewContext() != null) {
                    ((AppCompatActivity) view.getViewContext()).runOnUiThread(() -> {
                        try {
                            view.showMessageNoHayRutaDisponible();
                        } catch (WindowManager.BadTokenException ex) {
                            ex.printStackTrace();
                        }
                    });
                }
            }
        }
    }

    private ArrayList<Ruta> getRutasActuales(List<Ruta> rutas)
            throws JSONException {
        ArrayList<Ruta> rutasActuales = new ArrayList<>();

        for (int i = 0; i < rutas.size(); i++) {
            if (rutasActuales.size() == 0) {
                rutasActuales.add(rutas.get(i));
            } else {
                boolean existRuta = false;

                for (int j = 0; j < rutasActuales.size(); j++) {

                    if (rutasActuales.get(j).getLineaNegocio().equals(
                            rutas.get(i).getLineaNegocio())) {
                        existRuta = true;

                    } else if (rutasActuales.get(j).getLineaNegocio().equals(
                            rutas.get(i).getLineaNegocio())
                            && rutasActuales.get(j).getIdRuta().equals(
                            rutas.get(i).getIdRuta())) {
                        existRuta = true;
                    }
                }

                if (!existRuta) {
                    rutasActuales.add(rutas.get(i));
                }

            }
        }

        return rutasActuales;
    }

    private ArrayList<JSONObject> getNuevasRutas(JSONArray jsonArray)
            throws JSONException {
        ArrayList<JSONObject> nuevasRutas = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            if (nuevasRutas.size() == 0) {
                nuevasRutas.add(jsonArray.getJSONObject(i));
            } else {
                boolean existRuta = false;
                for (int j = 0; j < nuevasRutas.size(); j++) {
                    if (nuevasRutas.get(j).getString("linea_negocio")
                            .equals(jsonArray.getJSONObject(i).getString("linea_negocio"))) {
                        existRuta = true;
                    } else if (nuevasRutas.get(j).getString("linea_negocio")
                            .equals(jsonArray.getJSONObject(i).getString("linea_negocio"))
                            && nuevasRutas.get(j).getString("ruta_id")
                            .equals(jsonArray.getJSONObject(i).getString("ruta_id"))) {
                        existRuta = true;
                    }
                }

                if (!existRuta) {
                    nuevasRutas.add(jsonArray.getJSONObject(i));
                }
            }
        }

        return nuevasRutas;
    }

    private JSONArray getNuevasGuiasByLineaNegocio(JSONArray jsonGuias, String lineaNegocio)
            throws JSONException {
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < jsonGuias.length(); i++) {
            if (lineaNegocio.equals(
                    jsonGuias.getJSONObject(i).getString("linea_negocio"))) {
                jsonArray.put(jsonGuias.getJSONObject(i));
            }
        }
        return jsonArray;
    }

    private void calcularHorarioAproximadoNuevasGE(int totalNuevasGE) {
        // Iniciar desde la primera guia de las nuevas guias.
        int positionInit, positionPreviuos;

        Date horarioAproximado = null;
        Date horarioOrdenamiento = new Date();

        if (dbRuta.size() == 0) {
            positionInit = 0;
            positionPreviuos = -1;
        } else {
            positionInit = dbRuta.size() - totalNuevasGE;
            positionPreviuos = positionInit - 1;
            //horarioAproximado = new Date(dbRuta.get(positionPreviuos).getHorarioAproximado());
            horarioAproximado = new Date();
        }

        for (int k = positionInit; k < dbRuta.size(); k++) {
            horarioAproximado = calculateTimeArriveGE(k, positionPreviuos, horarioAproximado);
            Instant instant = Instant.ofEpochMilli(horarioAproximado.getTime());
            instant = instant.plus(addMinutosRefrigerio(horarioAproximado), ChronoUnit.MINUTES);
            horarioAproximado = Date.from(instant);

            Log.d(TAG, "HORA CALCULADA (" + new SimpleDateFormat("h:mm a").format(horarioAproximado) + ") POSITION: " + k);

            positionPreviuos = k;

            dbRuta.get(k).setHorarioAproximado(horarioAproximado.getTime());
            dbRuta.get(k).setHorarioOrdenamiento(horarioOrdenamiento.getTime());

//            if (ModelUtils.isGuiaEntrega(dbRuta.get(k).getTipo())) {
//                //dbRuta.get(i).setHorarioEntrega(new SimpleDateFormat("h:mm a").format(horarioAproximado));
//                rutaItems.get(k).setHoraLlegadaEstimada(new SimpleDateFormat("h:mm a").format(horarioAproximado));
//            }

            dbRuta.get(k).save();
        }
    }

    private void eliminarGuiaListaPendientes(JSONObject jsonRuta) throws JSONException {
        String flag_scaneo_pck;
        try {
            flag_scaneo_pck = jsonRuta.getString("flag_scaneo_pck");
            if (flag_scaneo_pck.equals("")) {
                flag_scaneo_pck = "0";
            }
        } catch (Throwable t) {
            flag_scaneo_pck = "0";
        }
        Ruta ruta = new Ruta(
                Preferences.getInstance().getString("idUsuario", ""),
                jsonRuta.getString("id_servicio"),
                jsonRuta.getString("man_id_det_rec"),
                jsonRuta.getString("mot_id"),
                jsonRuta.getString("id_agencia"),
                jsonRuta.getString("zon_id"),
                jsonRuta.getString("ruta_id"),
                jsonRuta.getString("guia_id"),
                jsonRuta.getString("id_medio_pago"),
                jsonRuta.getString("id_cliente"),
                jsonRuta.getString("id_manifiesto"),
                jsonRuta.getString("linea_negocio"),
                jsonRuta.getString("shi_codigo"),
                jsonRuta.getString("fec_ruta"),
                Ruta.ZONA.URBANO,
                jsonRuta.getString("guia"),
                jsonRuta.getString("tipo"),
                "",
                WordUtils.capitalize(jsonRuta.getString("direccion").toLowerCase()),
                jsonRuta.getString("geo_px"),
                jsonRuta.getString("geo_py"),
                jsonRuta.getString("radio_gps"),
                WordUtils.capitalize(jsonRuta.getString("distrito").toLowerCase()),
                WordUtils.capitalize(jsonRuta.getString("shipper").toLowerCase()),
                WordUtils.capitalize(jsonRuta.getString("centro_actividad").toLowerCase()),
                jsonRuta.getString("estado_shipper").toLowerCase(),
                WordUtils.capitalize(jsonRuta.getString("contacto").toLowerCase()),
                jsonRuta.getString("piezas"),
                jsonRuta.getString("horario"),
                0L,
                0L,
                jsonRuta.getString("tel_contacto"),
                jsonRuta.getString("nom_contacto"),
                jsonRuta.getString("telefono"),
                jsonRuta.getString("celular"),
                jsonRuta.getString("medio_pago"),
                jsonRuta.getString("importe"),
                jsonRuta.getString("tipo_envio"),
                jsonRuta.getString("anotaciones"),
                jsonRuta.getString("servicio_sms"),
                jsonRuta.getString("habilitantes"),
                jsonRuta.getString("chk_id_ult_entrega"),
                jsonRuta.getString("px_ult_entrega"),
                jsonRuta.getString("py_ult_entrega"),
                jsonRuta.getString("solicita_km"),
                jsonRuta.getString("flag_receptor"),
                jsonRuta.getString("flag_gestion"),
                jsonRuta.getString("chk_id_gestion"),
                WordUtils.capitalize(jsonRuta.getString("ges_motivo").toLowerCase()),
                jsonRuta.getString("ges_comentario").toLowerCase(),
                jsonRuta.getString("ges_arco_horario"),
                jsonRuta.getString("flag_direccion"),
                jsonRuta.getString("premios"),
                jsonRuta.getString("flag_firma"),
                jsonRuta.getString("cant_fotos"),
                jsonRuta.getString("descripcion").toLowerCase(),
                jsonRuta.getString("observacion").toLowerCase(),
                jsonRuta.getString("secuencia_ruteo"),
                flag_scaneo_pck,
                Integer.parseInt(jsonRuta.getString("flag_alerta")),
                Ruta.EstadoDescarga.PENDIENTE,
                Ruta.ResultadoGestion.NO_DEFINIDO,
                Data.Delete.NO,
                Data.Validate.VALID,
                jsonRuta.getString("mensaje_custom_fotos")
        );

        removeRutaOnDescargaFinalizada(new ArrayList<Ruta>(singletonList(ruta)));
    }

    private void saveRuta(JSONObject jsonRuta) throws JSONException {
        String flag_scaneo_pck;
        try {
            flag_scaneo_pck = jsonRuta.getString("flag_scaneo_pck");
            if (flag_scaneo_pck.equals("")) {
                flag_scaneo_pck = "0";
            }
        } catch (Throwable t) {
            flag_scaneo_pck = "0";
        }

        String idMedioPago = jsonRuta.getString("id_medio_pago");

        Ruta ruta = new Ruta(//aqui se guarda la ruta en local
                Preferences.getInstance().getString("idUsuario", ""),
                jsonRuta.getString("id_servicio"),
                jsonRuta.getString("man_id_det_rec"),
                jsonRuta.getString("mot_id"),
                jsonRuta.getString("id_agencia"),
                jsonRuta.getString("zon_id"),
                jsonRuta.getString("ruta_id"),
                jsonRuta.getString("guia_id"),
                jsonRuta.getString("id_medio_pago"),//
                jsonRuta.getString("id_cliente"),
                jsonRuta.getString("id_manifiesto"),
                jsonRuta.getString("linea_negocio"),
                jsonRuta.getString("shi_codigo"),
                jsonRuta.getString("fec_ruta"),
                Ruta.ZONA.URBANO,
                jsonRuta.getString("guia"),
                jsonRuta.getString("tipo"),
                (dbRuta.size() + 1) + "",
                WordUtils.capitalize(jsonRuta.getString("direccion").toLowerCase()),
                jsonRuta.getString("geo_px"),
                jsonRuta.getString("geo_py"),
                jsonRuta.getString("radio_gps"),
                WordUtils.capitalize(jsonRuta.getString("distrito").toLowerCase()),
                WordUtils.capitalize(jsonRuta.getString("shipper").toLowerCase()),
                WordUtils.capitalize(jsonRuta.getString("centro_actividad").toLowerCase()),
                jsonRuta.getString("estado_shipper").toLowerCase(),
                WordUtils.capitalize(jsonRuta.getString("contacto").toLowerCase()),
                jsonRuta.getString("piezas"),
                jsonRuta.getString("horario"),
                0L,
                0L,
                jsonRuta.getString("tel_contacto"),
                jsonRuta.getString("nom_contacto"),
                jsonRuta.getString("telefono"),
                jsonRuta.getString("celular"),
                jsonRuta.getString("medio_pago"),//
                jsonRuta.getString("importe"),
                jsonRuta.getString("tipo_envio"),
                jsonRuta.getString("anotaciones"),
                jsonRuta.getString("servicio_sms"),
                jsonRuta.getString("habilitantes"),
                jsonRuta.getString("chk_id_ult_entrega"),
                jsonRuta.getString("px_ult_entrega"),
                jsonRuta.getString("py_ult_entrega"),
                jsonRuta.getString("solicita_km"),
                jsonRuta.getString("flag_receptor"),
                jsonRuta.getString("flag_gestion"),
                jsonRuta.getString("chk_id_gestion"),
                WordUtils.capitalize(jsonRuta.getString("ges_motivo").toLowerCase()),
                jsonRuta.getString("ges_comentario").toLowerCase(),
                jsonRuta.getString("ges_arco_horario"),
                jsonRuta.getString("flag_direccion"),
                jsonRuta.getString("premios"),
                jsonRuta.getString("flag_firma"),
                jsonRuta.getString("cant_fotos"),
                jsonRuta.getString("descripcion").toLowerCase(),
                jsonRuta.getString("observacion"),
                jsonRuta.getString("secuencia_ruteo"),
                flag_scaneo_pck,
                Integer.parseInt(jsonRuta.getString("flag_alerta")),
                Ruta.EstadoDescarga.PENDIENTE,
                Ruta.ResultadoGestion.NO_DEFINIDO,
                Data.Delete.NO,
                Data.Validate.VALID,
                jsonRuta.getString("mensaje_custom_fotos")
        );

        if (jsonRuta.has("pck")) {
            JSONArray pcks = jsonRuta.getJSONArray("pck");

            for (int i = 0; i < pcks.length(); i++) {
                JSONObject pck = pcks.getJSONObject(i);
                Pieza pieza = new Pieza(
                        Preferences.getInstance().getString("idUsuario", ""),
                        jsonRuta.getString("id_servicio"),
                        pck.getString("pck_numero"),
                        pck.getString("pck_barra"),
                        pck.getString("pck_chk_id"),
                        pck.getString("pck_estado"),
                        pck.getString("pck_fecha"),
                        jsonRuta.getString("linea_negocio"),
                        pck.getString("pck_ruta").equalsIgnoreCase("s") ? 1 : 0
                );
                pieza.save();
            }
        }

        ruta.save();
        dbRuta.add(ruta);
    }

    private void deleteGEAnuladas(final JSONObject jsonRuta) {
        new Thread(() -> {
            try {
                // Eliminar todos los datos de la guia (Del dispositivo)
                Ruta.deleteAll(Ruta.class,
                        NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                                NamingHelper.toSQLNameDefault("idServicio") + " = ? and" +
                                NamingHelper.toSQLNameDefault("lineaNegocio") + " = ?",
                        Preferences.getInstance().getString("idUsuario", ""),
                        jsonRuta.getString("id_servicio"),
                        jsonRuta.getString("linea_negocio"));

                Imagen.deleteAll(Imagen.class,
                        NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                                NamingHelper.toSQLNameDefault("clasificacion") + " = ? and " +
                                NamingHelper.toSQLNameDefault("idSuperior") + " = ? and" +
                                NamingHelper.toSQLNameDefault("lineaNegocio") + " = ?",
                        Preferences.getInstance().getString("idUsuario", ""),
                        Imagen.Tipo.GESTION_GUIA + "",
                        jsonRuta.getString("id_servicio"),
                        jsonRuta.getString("linea_negocio"));

                DescargaRuta.deleteAll(DescargaRuta.class,
                        NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                                NamingHelper.toSQLNameDefault("idServicio") + " = ? and" +
                                NamingHelper.toSQLNameDefault("lineaNegocio") + " = ?",
                        Preferences.getInstance().getString("idUsuario", ""),
                        jsonRuta.getString("id_servicio"),
                        jsonRuta.getString("linea_negocio"));

                GuiaGestionada.deleteAll(GuiaGestionada.class,
                        NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                                NamingHelper.toSQLNameDefault("idServicio") + " = ? and" +
                                NamingHelper.toSQLNameDefault("lineaNegocio") + " = ?",
                        Preferences.getInstance().getString("idUsuario", ""),
                        jsonRuta.getString("id_servicio"),
                        jsonRuta.getString("linea_negocio"));
            } catch (JSONException ex) {
                Log.d(TAG, "OCURRIO UN ERROR AL ELIMINAR LA GUIA ANULADA.");
                ex.printStackTrace();
            }
        }).start();
    }

    private void registerNewSecuencia() {
        SecuenciaRuta secuenciaRuta = new SecuenciaRuta(
                Preferences.getInstance().getString("idUsuario", ""),
                Data.Delete.NO);
        secuenciaRuta.save();
    }

    private void registerEstadoRuta(String idRuta, String lineaNegocio) {
        List<EstadoRuta> estadoRutas = interactor.selectAllEstadoRuta();

        if (estadoRutas.size() > 0) {
            boolean existEstadoRuta = false;

            for (int i = 0; i < estadoRutas.size(); i++) {
                if (idRuta.equals(estadoRutas.get(i).getIdRuta())) {
                    existEstadoRuta = true;
                }
            }

            if (!existEstadoRuta) {
                EstadoRuta estadoRuta = new EstadoRuta(
                        Preferences.getInstance().getString("idUsuario", ""),
                        idRuta,
                        lineaNegocio,
                        estadoRutas.get(0).getFecha(),
                        estadoRutas.get(0).getHora(),
                        estadoRutas.get(0).getGpsLatitude(),
                        estadoRutas.get(0).getGpsLongitude(),
                        EstadoRuta.TipoRuta.RUTA_DEL_DIA,
                        Data.Delete.NO,
                        EstadoRuta.Estado.INICIADO
                );
                estadoRuta.save();
            }
        }
    }

    private void onDescargaFinalizada(ArrayList<Ruta> rutas, int msg) {
        removeRutaOnDescargaFinalizada(rutas);

        if (dbRuta.size() > 0) {
            // Validar si el horario aproximado de la primera guia es menor al horario actual
            // Si es menor, recalcular los horarios
            Date horarioAproximadoPrimeraGE = new Date(dbRuta.get(0).getHorarioAproximado());
            Log.d(TAG, "VALIDAR HORARIO APROXIMADO CON EL HORARIO ACTUAL");
            if (horarioAproximadoPrimeraGE.before(new Date())) {
                Log.d(TAG, "EL HORARIO APROXIMADO ES MENOR AL HORARIO ACTUAL");
                updateSecuenciaAllRutasPendientes();
                registerNewSecuencia();
            } else {
                // Actualizar los contadores
                new Thread(() -> {
                    for (int i = 0; i < dbRuta.size(); i++) {
                        Log.d(TAG, "UPDATE SECUENCIA GUIA (" + dbRuta.get(i).getGuia() + ") SECUENCIA: " + dbRuta.get(i).getSecuencia());

                        dbRuta.get(i).setSecuencia((i + 1) + "");
                        rutaItems.get(i).setCounterItem((i + 1) + "");

                        Log.d(TAG, "UPDATE SECUENCIA GUIA (" + dbRuta.get(i).getGuia() + ") SECUENCIA: " + dbRuta.get(i).getSecuencia());

                        dbRuta.get(i).save();
                    }
                    if (view.getViewContext() != null) {
                        ((AppCompatActivity) view.getViewContext()).runOnUiThread(() -> {
                            view.dismissProgressDialog();
                            view.notifyAllItemChanged();
                        });
                    }
                }).start();
            }
        }

        view.showToast(msg);
    }

    private void onRutaFinalizada() {
        Log.d(TAG, "RUTA FINALIZADA");
        Log.d(TAG, "TOTAL ITEMS: " + rutaItems.size());

        rutaItems.clear();
        dbRuta.clear();
        view.notifyAllItemChanged();
    }

    private void restoreRutaEliminada() {
        new Thread(() -> {
            Log.d(TAG, "restoreRutaEliminada");
            rutaEliminada.setEliminado(Data.Delete.NO);
            rutaEliminada.save();
            dbRuta.add(positionRutaEliminada, rutaEliminada);

            List<RutaEliminada> rutasEliminadas = RutaEliminada.find(RutaEliminada.class,
                    NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                            NamingHelper.toSQLNameDefault("idServicio") + " = ? ",
                    Preferences.getInstance().getString("idUsuario", ""),
                    rutaEliminada.getIdServicio());

            Log.d(TAG, "rutasEliminadas total: " + rutasEliminadas.size());

            for (int i = 0; i < rutasEliminadas.size(); i++) {
                rutasEliminadas.get(i).delete();
            }

            int resIcon = ModelUtils.getIconTipoGuia(rutaEliminada);

            int resIconValija = ModelUtils.isGuiaEntrega(rutaEliminada.getTipo())
                    ? R.drawable.ic_valija_entrega
                    : R.drawable.ic_valija_recoleccion;

            int lblColorHorario = ModelUtils.getLblColorHorario(rutaEliminada.getTipo(),
                    rutaEliminada.getTipoEnvio(), rutaEliminada.getFechaRuta(),
                    rutaEliminada.getHorarioEntrega());

            String simboloMoneda = ModelUtils.getSimboloMoneda(view.getViewContext());

            RutaItem item = new RutaItem(
                    rutaEliminada.getIdServicio(),
                    rutaEliminada.getIdManifiesto(),
                    rutaEliminada.getGuia(),
                    rutaEliminada.getDistrito(),
                    rutaEliminada.getDireccion(),
                    (rutaEliminada.getHorarioEntrega().length() == 0) ? "00:00" : rutaEliminada.getHorarioEntrega(),
                    rutaEliminada.getPiezas(),
                    "",
                    simboloMoneda,
                    resIcon,
                    resIcon,
                    resIconValija,
                    lblColorHorario,
                    Color.parseColor("#00FFFFFF"),
                    rutaEliminada.getResultadoGestion(),
                    false,
                    true,
                    ModelUtils.isTipoEnvioValija(rutaEliminada.getTipoEnvio()),
                    CommonUtils.parseDouble(rutaEliminada.getImporte()) > 0,
                    false
            );

            rutaItems.add(positionRutaEliminada, item);

            view.notifyItemInsert(positionRutaEliminada);

            updateSecuenciaAllRutasPendientes();
        }).start();
    }

    private void removeRutaOnDescargaFinalizada(ArrayList<Ruta> rutas) {
        for (int i = 0; i < rutas.size(); i++) {
            for (int j = 0; j < dbRuta.size(); j++) {
                if (rutas.get(i).getIdServicio().equals(dbRuta.get(j).getIdServicio()) &&
                        rutas.get(i).getLineaNegocio().equals(dbRuta.get(j).getLineaNegocio())) {
                    rutaItems.remove(j);
                    dbRuta.remove(j);
                    view.notifyItemRemove(j);
                }
            }
        }
    }

    private void updateSecuenciaAllRutasPendientes() {
        new Thread(() -> {
            int previousPositionGuia = -1;
            Date horarioAproximado = null;
            Date horarioOrdenamiento = new Date();

            try {
                if (dbRuta != null) {
                    for (int i = 0; i < dbRuta.size(); i++) {
                        Log.d(TAG, "UPDATE SECUENCIA GUIA (" + dbRuta.get(i).getGuia() + ") SECUENCIA: " + dbRuta.get(i).getSecuencia());

                        dbRuta.get(i).setSecuencia((i + 1) + "");
                        rutaItems.get(i).setCounterItem((i + 1) + "");

                        Log.d(TAG, "UPDATE SECUENCIA GUIA (" + dbRuta.get(i).getGuia() + ") SECUENCIA: " + dbRuta.get(i).getSecuencia());

                        horarioAproximado = calculateTimeArriveGE(i, previousPositionGuia, horarioAproximado);
                        Instant instant = Instant.ofEpochMilli(horarioAproximado.getTime());
                        instant = instant.plus(addMinutosRefrigerio(horarioAproximado), ChronoUnit.MINUTES);
                        horarioAproximado = Date.from(instant);

                        Log.d(TAG, "HORA CALCULADA (" + new SimpleDateFormat("h:mm a").format(horarioAproximado) + ") POSITION: " + i);

                        previousPositionGuia = i;

                        dbRuta.get(i).setHorarioAproximado(horarioAproximado.getTime());
                        dbRuta.get(i).setHorarioOrdenamiento(horarioOrdenamiento.getTime());

                        if (ModelUtils.isGuiaEntrega(dbRuta.get(i).getTipo())) {
                            //dbRuta.get(i).setHorarioEntrega(new SimpleDateFormat("h:mm a").format(horarioAproximado));
                            rutaItems.get(i).setHoraLlegadaEstimada(new SimpleDateFormat("h:mm a").format(horarioAproximado));
                        }

                        dbRuta.get(i).save();
                    }
                }
            } catch (IndexOutOfBoundsException ex) {
                ex.printStackTrace();
            }

            if (view.getViewContext() != null) {
                ((AppCompatActivity) view.getViewContext()).runOnUiThread(() -> {
                    view.dismissProgressDialog();
                    view.notifyAllItemChanged();
                });
            }
        }).start();
    }

    private Date calculateTimeArriveGE(int currentPosition, int previousPosition, Date previousHour) {
        double latitudeFrom, longitudeFrom, latitudeTo, longitudeTo;

        if (previousPosition == -1) {
            /*GoogleApiClient googleApiClient = ((RutaActivity) viewgetViewContext()).getGoogleApiClient();

            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            if (mLastLocation != null) {
                Log.d(TAG, "LATITUDE: " + mLastLocation.getLatitude());
                Log.d(TAG, "LATITUDE: " + mLastLocation.getLongitude());*/

            if (CommonUtils.isValidCoords(LocationUtils.getLatitude(), LocationUtils.getLongitude())) {
                if (CommonUtils.isValidCoords(dbRuta.get(currentPosition).getGpsLatitude(),
                        dbRuta.get(currentPosition).getGpsLongitude())) {
                    latitudeFrom = LocationUtils.getLatitude();
                    longitudeFrom = LocationUtils.getLongitude();
                    latitudeTo = Double.parseDouble(dbRuta.get(currentPosition).getGpsLatitude());
                    longitudeTo = Double.parseDouble(dbRuta.get(currentPosition).getGpsLongitude());

                    int tiempoEstimado = (int) Math.ceil(MyLocation.calculateTimeBetweenTwoLocations(latitudeFrom,
                            longitudeFrom, latitudeTo, longitudeTo, 25));

                    Instant instant = Instant.now();
                    instant = instant.plus(tiempoEstimado, ChronoUnit.MINUTES);
                    return Date.from(instant);
                } else {
                    Instant instant = Instant.now();
                    instant = instant.plus(5, ChronoUnit.MINUTES);
                    return Date.from(instant);
                }
            } else {
                Instant instant = Instant.now();
                instant = instant.plus(5, ChronoUnit.MINUTES);
                return Date.from(instant);
            }
        } else {
            if (MyLocation.isValidLocations(dbRuta.get(previousPosition).getGpsLatitude(),
                    dbRuta.get(previousPosition).getGpsLongitude()) &&
                    MyLocation.isValidLocations(dbRuta.get(currentPosition).getGpsLatitude(),
                            dbRuta.get(currentPosition).getGpsLongitude())) {
                latitudeFrom = Double.parseDouble(dbRuta.get(previousPosition).getGpsLatitude());
                longitudeFrom = Double.parseDouble(dbRuta.get(previousPosition).getGpsLongitude());
                latitudeTo = Double.parseDouble(dbRuta.get(currentPosition).getGpsLatitude());
                longitudeTo = Double.parseDouble(dbRuta.get(currentPosition).getGpsLongitude());

                int tiempoEstimado = (int) Math.ceil(MyLocation.calculateTimeBetweenTwoLocations(latitudeFrom,
                        longitudeFrom, latitudeTo, longitudeTo, 25));
                Instant instant = Instant.ofEpochMilli(previousHour.getTime());
                instant = instant.plus(tiempoEstimado, ChronoUnit.MINUTES);
                return Date.from(instant);
            } else {
                Instant instant = Instant.ofEpochMilli(previousHour.getTime());
                instant = instant.plus(5, ChronoUnit.MINUTES);
                return Date.from(instant);
            }
        }
    }

    private int addMinutosRefrigerio(Date horarioAproximdo) {
        int hora = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int minuto = Calendar.getInstance().get(Calendar.MINUTE);
        Log.d(TAG, "VALIDATE HORARIO ACTUAL");
        Log.d(TAG, "VALIDATE HORA: " + hora);
        Log.d(TAG, "VALIDATE MINUTO: " + minuto);

        if (addMinutosRefrigerio) {
            if (hora <= 13 && minuto <= 59) {
                Log.d(TAG, "VALIDATE HORARIO ACTUAL OK");
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(horarioAproximdo);
                hora = calendar.get(Calendar.HOUR_OF_DAY);
                minuto = calendar.get(Calendar.MINUTE);
                Log.d(TAG, "VALIDATE HORARIO GUIA");
                Log.d(TAG, "VALIDATE HORA: " + hora);
                Log.d(TAG, "VALIDATE MINUTO: " + minuto);
                if (hora == 13 && minuto >= 30 && minuto <= 59) {
                    Log.d(TAG, "VALIDATE HORARIO GUIA OK");
                    addMinutosRefrigerio = false;
                    return 30;
                } else {
                    Log.d(TAG, "VALIDATE HORARIO GUIA FAIL");
                }
            } else {
                Log.d(TAG, "VALIDATE HORARIO ACTUAL FAIL");
            }
        } else {
            Log.d(TAG, "ADD MINUTOS REFRIGERIO FAIL");
        }

//        if (addMinutosRefrigerio) {
//            if (hora <= 20 && minuto <= 59) {
//                Log.d(TAG, "VALIDATE HORARIO ACTUAL OK");
//                Calendar calendar = Calendar.getInstance();
//                calendar.setTime(horarioAproximdo);
//                hora = calendar.get(Calendar.HOUR_OF_DAY);
//                minuto = calendar.get(Calendar.MINUTE);
//                Log.d(TAG, "VALIDATE HORARIO GUIA");
//                Log.d(TAG, "VALIDATE HORA: " + hora);
//                Log.d(TAG, "VALIDATE MINUTO: " + minuto);
//                if (hora == 19 && minuto >= 30 && minuto <= 59) {
//                    Log.d(TAG, "VALIDATE HORARIO GUIA OK");
//                    addMinutosRefrigerio = false;
//                    return 30;
//                } else {
//                    Log.d(TAG, "VALIDATE HORARIO GUIA FAIL");
//                }
//            } else {
//                Log.d(TAG, "VALIDATE HORARIO ACTUAL FAIL");
//            }
//        } else {
//            Log.d(TAG, "ADD MINUTOS REFRIGERIO FAIL");
//        }

        return 0;
    }

    private class LoadGETask extends AsyncTaskCoroutine<String, Boolean> {

        private boolean showMsgDialogNoHayRutaPendiente = false;

        private boolean getGuiasAfterLoad = false;

        public LoadGETask(boolean showMsgDialogNoHayRutaPendiente, boolean getGuiasAfterLoad) {
            this.showMsgDialogNoHayRutaPendiente = showMsgDialogNoHayRutaPendiente;
            this.getGuiasAfterLoad = getGuiasAfterLoad;
        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            view.setVisibilitySwipeRefreshLayout(true);
        }

        public Boolean doInBackground(String... urls) {
            dbRuta = interactor.selectRutasPendientes();

            isMostrarAlerta = ConsideracionesImportantesRutaInteractor.isMostrarAlerta();

//            Test
//            for (int i = 0; i < dbRuta.size(); i++) {
//                Log.d(TAG, "GUIA: " + dbRuta.get(i).getGuia() + " SECUENCIA: " + dbRuta.get(i).getSecuencia());
//            }

            // Verificar si una guia no se actualizo correctamente el estado de gestion.
            for (int i = 0; i < dbRuta.size(); i++) {
                GuiaGestionada guiaGestionada =
                        interactor.selectRutaGestionada(dbRuta.get(i).getIdServicio(),
                                dbRuta.get(i).getLineaNegocio());
                if (guiaGestionada != null) {
                    dbRuta.get(i).setEstadoDescarga(Ruta.EstadoDescarga.GESTIONADO);
                    dbRuta.get(i).save();
                    dbRuta.remove(i);
                }
            }

//            dbRuta = interactor.selectRutasPendientes();

            rutaItems = new ArrayList<>();

//            Log.d(TAG, "LOAD RUTAS TOTAL: " + dbRuta.size());

            if (dbRuta.size() > 0) {
                for (int i = 0; i < dbRuta.size(); i++) {
                    int resIcon = ModelUtils.getIconTipoGuia(dbRuta.get(i));

                    int idResIconTipoEnvio = ModelUtils.getIconTipoEnvio(dbRuta.get(i));

                    int backgroundColorGuia = ModelUtils.getBackgroundColorGE(
                            dbRuta.get(i).getTipoEnvio(), view.getViewContext());

                    String horario = dbRuta.get(i).getHorarioEntrega();

                    if (ModelUtils.isGuiaEntrega(dbRuta.get(i).getTipo())) {
                        horario = CommonUtils.fomartHorarioAproximado(
                                dbRuta.get(i).getHorarioAproximado(), false);
                    }

                    int lblColorHorario = ModelUtils.getLblColorHorario(dbRuta.get(i).getTipo(),
                            dbRuta.get(i).getTipoEnvio(), dbRuta.get(i).getFechaRuta(),
                            dbRuta.get(i).getHorarioEntrega());

                    String guia = ModelUtils.isGuiaEntrega(dbRuta.get(i).getTipo())
                            ? dbRuta.get(i).getGuia()
                            : dbRuta.get(i).getShipper() + " (" + dbRuta.get(i).getGuia() + ")";

                    try {
                        if (dbRuta.get(i).getSecuenciaRuteo() != null &&
                                Integer.parseInt(dbRuta.get(i).getSecuenciaRuteo()) > 0) {
                            guia += " (" + dbRuta.get(i).getSecuenciaRuteo() + ")";
                        }
                    } catch (NumberFormatException ex) {
                        ex.printStackTrace();
                    }

                    String simboloMoneda = ModelUtils.getSimboloMoneda(view.getViewContext());

                    RutaItem rutaItem = new RutaItem(
                            dbRuta.get(i).getIdServicio(),
                            dbRuta.get(i).getIdManifiesto(),
                            guia,
                            dbRuta.get(i).getDistrito(),
                            dbRuta.get(i).getDireccion(),
                            horario,
                            dbRuta.get(i).getPiezas(),
                            (i + 1) + "",
                            simboloMoneda,
                            resIcon,
                            resIcon,
                            idResIconTipoEnvio,
                            backgroundColorGuia,
                            lblColorHorario,
                            dbRuta.get(i).getResultadoGestion(),
                            false,
                            true,
                            ModelUtils.isTipoEnvioValija(dbRuta.get(i).getTipoEnvio()),
                            ModelUtils.isShowIconImportePorCobrar(dbRuta.get(i).getImporte()),
                            false
                    );
                    rutaItems.add(rutaItem);
//                    Log.d(TAG, "Estado Descarga: " + dbRuta.get(i).getEstadoDescarga() + "");
//                    Log.d(TAG, "GUIA: " + dbRuta.get(i).getGuia() + "");
//                    Log.d(TAG, "ID_RUTA: " + dbRuta.get(i).getIdRuta() + "");
//                    Log.d(TAG, "SECUENCIA: " + dbRuta.get(i).getSecuencia() + "");
                    dbRuta.get(i).setSecuencia(i + 1 + "");
                    dbRuta.get(i).save();
//                    Log.d(TAG, "SECUENCIA POST: " + dbRuta.get(i).getSecuencia() + "");
                }

//                Log.d(TAG, "TOTAL GE: " + rutaItems.size());
                actionMenuRutaPendienteHelper.setDbRuta(dbRuta);
                actionMenuRutaPendienteHelper.setRutaItems(rutaItems);
            }
            return true;
        }

        public void onPostExecute(Boolean result) {
            setTitleActivity();
            showAlerta();
            view.showDatosRutasPendientes(rutaItems);
            if (showMsgDialogNoHayRutaPendiente && rutaItems.size() == 0) {
                view.showToast(R.string.fragment_ruta_pendiente_message_no_hay_rutas);
            }
            if (getGuiasAfterLoad) {
                if (view.getViewContext() != null &&
                        CommonUtils.validateConnectivity(view.getViewContext())) {
                    Log.d(TAG, "INIT GET GUIAS");
                    getRutas();
                } else {
                    view.setVisibilitySwipeRefreshLayout(false);
                }
            } else {
                view.setVisibilitySwipeRefreshLayout(false);
            }
        }

        private void setTitleActivity() {
            if (dbRuta.size() > 0) {
                String title = "";
                for (int i = 0; i < dbRuta.size(); i++) {
                    if (dbRuta.get(i).getLineaNegocio().equals("3")) {
                        title = "Ruta: " + dbRuta.get(i).getIdRuta();
                        break;
                    }
                }
                if (title.length() == 0) {
                    title = "Ruta: " + dbRuta.get(0).getIdRuta();
                }
                if (view.getViewContext() != null) {
                    ((RutaActivity) view.getViewContext()).getToolbar().setTitle(title);
                }
            }
        }
    }

    private void showAlerta() {
        try {
            if (isMostrarAlerta) {
                ((RutaActivity) view.getViewContext()).setVisibilityBoxConsideracionesImportantesRuta(View.VISIBLE);
            } else {
                ((RutaActivity) view.getViewContext()).setVisibilityBoxConsideracionesImportantesRuta(View.GONE);
            }
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    private Ruta getRutaByID(List<Ruta> rutas, String idServicio, String lineaNegocio) {
        Log.d(TAG, "getRutaByID: " + rutas.size());
        Log.d(TAG, "idServicio: " + idServicio);
        Log.d(TAG, "lineaNegocio: " + lineaNegocio);
        for (int i = 0; i < rutas.size(); i++) {
            if (rutas.get(i).getIdServicio().equals(idServicio)
                    && rutas.get(i).getLineaNegocio().equals(lineaNegocio)) {
                return rutas.get(i);
            }
        }
        return null;
    }

    private boolean isRutaSaved(JSONObject jsonRuta) throws JSONException {
        return interactor.selectRuta(jsonRuta.getString("id_servicio"),
                jsonRuta.getString("linea_negocio")) != null;
//        return getRutaByID(dbRuta, jsonRuta.getString("id_servicio"), jsonRuta.getString("linea_negocio")) != null;
    }

    private boolean existeRutasPendientes(JSONArray jsonRutas) throws JSONException {
        if (jsonRutas.length() > 0) {
            if (jsonRutas.getJSONObject(0).getInt("error_sql") == 0) {
                return true;
            }
        }
        return false;
    }

    private boolean isNuevaFechaRuta(String idRutaActual, String fechaRutaActual, JSONArray jsonGuias) throws JSONException {
        try {
            String fechaRuta = fechaRutaActual == null
                    ? "16/09/2014" : fechaRutaActual;
            Date fechaActualRuta = new SimpleDateFormat("dd/MM/yyyy").parse(fechaRuta);
            Date fechaNuevaRuta = new SimpleDateFormat("dd/MM/yyyy").parse(
                    jsonGuias.getJSONObject(0).getString("fec_ruta"));

            if (fechaActualRuta.before(fechaNuevaRuta)) {
                Log.d(TAG, "FECHA RUTA ANTIGUA");
                return false;
            } else if (fechaActualRuta.equals(fechaNuevaRuta)) {
                Log.d(TAG, "FECHA RUTA IGUAL");
//                if (idRutaActual.equals(
//                        jsonGuias.getJSONObject(0).getString("ruta_id"))) {
//                    Log.d(TAG, "ID RUTA IGUAL");
//                    return true;
//                } else {
//                    Log.d(TAG, "ID RUTA NO ES IGUAL");
//                    return false;
//                }
                return true;
            }
        } catch (ParseException exParse) {
            exParse.printStackTrace();
        } catch (JSONException exJSON) {
            exJSON.printStackTrace();
        } catch (IndexOutOfBoundsException exIndex) {
            exIndex.printStackTrace();
        }

        Log.d(TAG, "ERROR VALIDANDO FECHAS");
        return false;
    }

    private boolean isIniciadoRuta() {
        List<EstadoRuta> estadoRuta = interactor.selectAllEstadoRuta();

        int estados = 0;
        boolean validateEstado = false;

        if (estadoRuta.size() > 0) {
            for (EstadoRuta estado : estadoRuta) {
                if (estado.getEstado() == EstadoRuta.Estado.INICIADO) {
                    estados = 1;
                }

                if (estado.getEstado() == EstadoRuta.Estado.FINALIZADO) {
                    estados = 2;
                }
            }
        } else {
            Log.d(TAG, "estadoRuta: " + estadoRuta.size());
        }

        switch (estados) {
            case 0:
                view.showMessageRutaNoIniciada();
                break;
            case 1:
                validateEstado = true;
                break;
            case 2:
                view.showMessageRutaFinalizada();
                break;
        }

        return validateEstado;
    }

    /**
     * Broadcast
     * <p>
     * {@link EntregaGEPresenter#sendOnDescargaFinalizadaReceiver}
     * {@link NoEntregaGEPresenter#sendOnDescargaFinalizadaReceiver}
     * {@link RecolectaGEPresenter#sendOnDescargaFinalizadaReceiver}
     * {@link NoRecolectaGEPresenter#sendOnDescargaFinalizadaReceiver}
     */
    private BroadcastReceiver descargaFinalizadaReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<Ruta> rutas = (ArrayList<Ruta>)
                    intent.getExtras().getBundle("args").getSerializable("guias");

            /*Log.d(TAG, "TOTAL GUIAS 1: " + interactor.selectAllRutas().size());
            Log.d(TAG, "TOTAL RUTAS BROADCAST: " + rutas.size());
            Log.d(TAG, "TOTAL GUIAS 2: " + interactor.selectAllRutas().size());
            Log.d(TAG, "TOTAL GESTIONADAS: " + interactor.selectRutasGestionadas().size());
            Log.d(TAG, "TOTAL PENDIENTES: " + interactor.selectRutasPendientes().size());*/

            int msg = R.string.activity_detalle_ruta_message_descarga_finalizado_exitosamente;
            if (rutas.get(0).getTipo().equalsIgnoreCase("R")) {
                msg = R.string.activity_detalle_ruta_message_recoleccion_finalizado_exitosamente;
            }

            isMostrarAlerta = ConsideracionesImportantesRutaInteractor.isMostrarAlerta();
            showAlerta();

            onDescargaFinalizada(rutas, msg);
        }
    };

    /**
     * Broadcast
     * <p>
     * {@link RutaPresenter#sendOnRutaIniciadaReceiver()}
     */
    private BroadcastReceiver rutaIniciadaReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateSecuenciaAllRutasPendientes();
            registerNewSecuencia();
        }
    };

    /**
     * Broadcast
     * <p>
     * {@link RutaPresenter#sendOnRutaFinalizadaReceiver}
     * {@link ForzarCierreRutaHelper#sendOnRutaFinalizadaReceiver}
     */
    private BroadcastReceiver rutaFinalizadaReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            onRutaFinalizada();
        }
    };

    /**
     * Broadcast
     * <p>
     * {@link RutaPresenter#sendOnManifiestoEliminadoReceiver}
     */
    private BroadcastReceiver manifiestoEliminadoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String idManifiesto = intent.getStringExtra("idManifiesto");
            //Log.d(TAG, "ID MANIFIESTO: " + idManifiesto);

            //Log.d(TAG, "TOTAL GUIAS" + rutaItems.size());

            for (int i = 0; i < rutaItems.size(); i++) {
                //Log.d(TAG, "GUIA: " + rutaItems.get(i).getGuia() + " MANIFIESTO: " + rutaItems.get(i).getIdManifiesto() + ".");
                if (rutaItems.get(i).getIdManifiesto().equals(idManifiesto)) {
                    //Log.d(TAG, "GUIA ELIMINADA DE MANIFIESTO: " + rutaItems.get(i).getGuia());
                    rutaItems.remove(i);
                    dbRuta.remove(i);
                    i -= 1;
                }
                //Log.d(TAG, "TOTAL GUIAS REMOVE: " + rutaItems.size() + " CONTADOR: " + i);
            }

            //Log.d(TAG, "TOTAL GUIAS REMOVE: " + rutaItems.size());
            view.notifyAllItemChanged();

            isMostrarAlerta = ConsideracionesImportantesRutaInteractor.isMostrarAlerta();
            showAlerta();

            updateSecuenciaAllRutasPendientes();
            registerNewSecuencia();
        }
    };

    /**
     * Broadcast
     * <p>
     * {@link TransferirGuiaDialog#sendTransferenciaGuiaFinalizadaAction}
     */
    private BroadcastReceiver transferenciaGuiaFinalizadaReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String guias[] = (String[]) intent.getSerializableExtra("guias");
            String lineaNegocio = intent.getStringExtra("lineaNegocio");

            //Log.d(TAG, "TOTAL GUIAS: " + guias.length);

            for (int i = 0; i < guias.length; i++) {
                //Log.d(TAG, "MAN_ID_DET: " + guias[i]);
                for (int j = 0; j < dbRuta.size(); j++) {
                    if (dbRuta.get(j).getIdServicio().equals(guias[i]) &&
                            dbRuta.get(j).getLineaNegocio().equals(lineaNegocio)) {
                        //Log.d(TAG, "GUIA ELIMINADA: " + rutaItems.get(j).getGuia());
                        rutaItems.remove(j);
                        dbRuta.remove(j);
                    }
                }
            }
            view.notifyAllItemChanged();

            isMostrarAlerta = ConsideracionesImportantesRutaInteractor.isMostrarAlerta();
            showAlerta();

            updateSecuenciaAllRutasPendientes();
            registerNewSecuencia();
        }
    };

    /**
     * Broadcast
     * <p>
     * {@link DefinirPosicionGuiaDialog#sendOnDefinirPosicionGuiaReceiver}
     */
    private BroadcastReceiver definirPosicionGuiaReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int actualPosicion = intent.getIntExtra("actualPosicion", 0);
            int nuevaPosicion = intent.getIntExtra("nuevaPosicion", 0);
            onItemMove(actualPosicion, nuevaPosicion);
            onItemSelectChanged(null, ItemTouchHelper.ACTION_STATE_IDLE);
        }
    };

    /**
     * Broadcast
     * <p>
     * {@link ActionMenuRutaPendienteHelper#sendOnGuardarOrdenGuiasReceiver}
     */
    private BroadcastReceiver guardarOrdenGuiasReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            new LoadGETask(false, false).execute();
            onItemSelectChanged(null, ItemTouchHelper.ACTION_STATE_IDLE);
        }
    };

    /**
     * Broadcast
     * <p>
     * {@link DetalleRutaPresenter#sendSincronizarNumeroTelefonoListaGuiasPendientesReceiver}
     */
    private BroadcastReceiver sincronizarNumeroTelefonoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            for (int i = 0; i < dbRuta.size(); i++) {
                if (dbRuta.get(i).getIdServicio().equals(intent.getStringExtra("idServicio")) &&
                        dbRuta.get(i).getLineaNegocio().equals(intent.getStringExtra("lineaNegocio"))) {
                    if (intent.getIntExtra("type", 0) == 1) {
                        dbRuta.get(i).setTelefono(intent.getStringExtra("phone"));
                    } else {
                        dbRuta.get(i).setCelular(intent.getStringExtra("phone"));
                    }
                }
            }
        }
    };

    /**
     * Broadcast
     * <p>
     * {@link RutaPresenter#resultScannReceiver}
     */
    private BroadcastReceiver buscarGuiaReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, final Intent intent) {
            if (intent.getIntExtra("tipoBusqueda", 0) == 0) {
                String barra = intent.getStringExtra("value").trim();
                Pieza pieza = interactor.selectPiezaByBarra(barra);

                if (pieza != null) {
                    for (int i = 0; i < dbRuta.size(); i++) {
                        if (dbRuta.get(i).getIdServicio().equals(pieza.getIdServicioGuia())) {
                            view.scrollToPosition(i);
                        }
                    }
                } else {
                    for (int i = 0; i < dbRuta.size(); i++) {
                        if (dbRuta.get(i).getGuia().equalsIgnoreCase(barra)) {
                            view.scrollToPosition(i);
                        }
                    }
                }
            }
        }
    };

}