package com.urbanoexpress.iridio3.presenter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.urbanoexpress.iridio3.AsyncTaskCoroutine;
import com.urbanoexpress.iridio3.R;
import com.urbanoexpress.iridio3.model.entity.DescargaRuta;
import com.urbanoexpress.iridio3.model.entity.Pieza;
import com.urbanoexpress.iridio3.model.entity.Ruta;
import com.urbanoexpress.iridio3.model.interactor.RutaPendienteInteractor;
import com.urbanoexpress.iridio3.model.util.ModelUtils;
import com.urbanoexpress.iridio3.ui.DetalleRutaRuralActivity;
import com.urbanoexpress.iridio3.ui.RutaRuralActivity;
import com.urbanoexpress.iridio3.ui.model.RutaItem;
import com.urbanoexpress.iridio3.util.CommonUtils;
import com.urbanoexpress.iridio3.util.constant.LocalAction;
import com.urbanoexpress.iridio3.view.BaseModalsView;
import com.urbanoexpress.iridio3.view.RutaGestionadaView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RutaRuralVisitadoPresenter extends BaseModalsView {

    private static final String TAG = RutaRuralVisitadoPresenter.class.getSimpleName();

    private RutaGestionadaView view;
    private RutaPendienteInteractor interactor;

    private List<RutaItem> rutaItems;
    private List<Ruta> dbRuta = Collections.emptyList();

    public RutaRuralVisitadoPresenter(RutaGestionadaView view) {
        this.view = view;
        interactor = new RutaPendienteInteractor(view.getContextView());

        init();
    }

    private void init() {
        new LoadGETask(false).execute();

        LocalBroadcastManager.getInstance(view.getContextView())
                .registerReceiver(descargaFinalizadaReceiver, new IntentFilter("OnDescargaFinalizada"));
        LocalBroadcastManager.getInstance(view.getContextView())
                .registerReceiver(buscarGuiaReceiver, new IntentFilter(LocalAction.BUSCAR_GUIA_ACTION));
    }

    public void onClickItem(int position) {
        Bundle args = new Bundle();
        args.putSerializable("guias", dbRuta.get(position));
        args.putInt("numVecesGestionado", 2);
        view.getContextView().startActivity(new Intent(
                view.getContextView(), DetalleRutaRuralActivity.class).putExtra("args", args));
        ((AppCompatActivity) view.getContextView()).overridePendingTransition(
                R.anim.slide_enter_from_right, R.anim.not_slide);
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

                        BaseModalsView.showAlertDialog(this.view.getContextView(),
                                dbRuta.get(position).getGuiaRequerimientoMotivo().trim(),
                                message,
                                this.view.getContextView().getString(R.string.text_aceptar), null);
                    } else if (dbRuta.get(position).getGuiaRequerimientoCHK().equals("30")) {
                        String message = "Esta guía por motivo de " +
                                dbRuta.get(position).getGuiaRequerimientoMotivo().toUpperCase() + ", debe ser devuelta al shipper.";

                        BaseModalsView.showAlertDialog(this.view.getContextView(),
                                "Devolución al Shipper",
                                message,
                                this.view.getContextView().getString(R.string.text_aceptar), null);
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

                    BaseModalsView.showAlertDialog(this.view.getContextView(),
                            dbRuta.get(position).getGuiaRequerimientoMotivo().trim(),
                            message,
                            this.view.getContextView().getString(R.string.text_aceptar), null);
                }
            }
        }
    }

    public void onSwipeRefresh() {
        new LoadGETask(true).execute();
    }

    public void onDestroyActivity() {
        LocalBroadcastManager.getInstance(view.getContextView())
                .unregisterReceiver(descargaFinalizadaReceiver);
        LocalBroadcastManager.getInstance(view.getContextView())
                .unregisterReceiver(buscarGuiaReceiver);
    }

    private class LoadGETask extends AsyncTaskCoroutine<String, Boolean> {

        private boolean showMsgNoHayGuiasGestionadas = false;

        public LoadGETask(boolean showMsgNoHayGuiasGestionadas) {
            this.showMsgNoHayGuiasGestionadas = showMsgNoHayGuiasGestionadas;
        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            view.setVisibilitySwipeRefreshLayout(true);
        }

        public Boolean doInBackground(String... urls) {
            dbRuta = interactor.selectRutasVisitadas();

            rutaItems = new ArrayList<>();

            for (int i = 0; i < dbRuta.size(); i++) {
                DescargaRuta descargaRuta = interactor.selectDescargaRuta(
                        dbRuta.get(i).getIdServicio(), dbRuta.get(i).getLineaNegocio());
                if (descargaRuta != null) {
                    descargaRuta.setProcesoDescarga(DescargaRuta.Entrega.ENTREGA_EFECTIVA);
                    descargaRuta.save();
                }

                int resIcon = ModelUtils.getIconTipoGuia(dbRuta.get(i));

                int resIconTipoEnvio = ModelUtils.getIconTipoEnvio(dbRuta.get(i));

                int backgroundColorGuia = ModelUtils.getBackgroundColorGE(
                        dbRuta.get(i).getTipoEnvio(), view.getContextView());

                String horario = dbRuta.get(i).getHorarioEntrega();

                if (ModelUtils.isGuiaEntrega(dbRuta.get(i).getTipo())) {
                    long nroDias = CommonUtils.getNoOfDaysBetweenDateAndNow(dbRuta.get(i).getFechaRuta());
                    if (nroDias == 0) {
                        horario = "hoy";
                    } else if (nroDias > 0) {
                        horario = nroDias == 1 ? nroDias + " día" : nroDias + " días";
                    } else {
                        horario = dbRuta.get(i).getFechaRuta();
                    }
                }

                int lblColorHorario = view.getContextView() != null
                        ? ContextCompat.getColor(view.getContextView(), R.color.gris_2) : 0;

                String simboloMoneda = ModelUtils.getSimboloMoneda(view.getContextView());

                RutaItem item = new RutaItem(
                        dbRuta.get(i).getIdServicio(),
                        dbRuta.get(i).getIdManifiesto(),
                        (ModelUtils.isGuiaEntrega(dbRuta.get(i).getTipo())
                                ? dbRuta.get(i).getGuia() : dbRuta.get(i).getShipper()
                                + "(" + dbRuta.get(i).getGuia() + ")"),
                        dbRuta.get(i).getDistrito(),
                        dbRuta.get(i).getDireccion(),
                        horario,
                        dbRuta.get(i).getPiezas(),
                        (i + 1) + "",
                        simboloMoneda,
                        resIcon,
                        resIcon,
                        resIconTipoEnvio,
                        backgroundColorGuia,
                        lblColorHorario,
                        dbRuta.get(i).getResultadoGestion(),
                        true,
                        false,
                        ModelUtils.isTipoEnvioValija(dbRuta.get(i).getTipoEnvio()),
                        false,
                        false
                );

                rutaItems.add(item);
            }
            return true;
        }

        public void onPostExecute(Boolean result) {
            view.showRutasGestionadas(rutaItems);
            view.setVisibilitySwipeRefreshLayout(false);
            if (rutaItems.size() > 0) {
                try {
                    ((RutaRuralActivity) view.getContextView()).setTitleTabVisitados(
                            String.format("Visitados (%s)", rutaItems.size()));
                } catch (NullPointerException ex) {
                    ex.printStackTrace();
                }
            } else if (showMsgNoHayGuiasGestionadas) {
                showToast(view.getContextView(),
                        R.string.fragment_ruta_gestionada_msg_no_hay_gestionados,
                        Toast.LENGTH_SHORT);
            }
        }
    }

    /**
     * Broadcast
     *
     * {@link EntregaGEPresenter#sendOnDescargaFinalizadaReceiver()}
     */
    private BroadcastReceiver descargaFinalizadaReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        new LoadGETask(false).execute();
                    } catch (NullPointerException ex) {
                        ex.printStackTrace();
                    }
                }
            }, 1000);
        }
    };

    /**
     * Broadcast
     *
     * {@link RutaPresenter#resultScannReceiver}
     */
    private BroadcastReceiver buscarGuiaReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getIntExtra("tipoBusqueda", 0) == 1) {
                String barra = intent.getStringExtra("value").trim();
                Pieza pieza = interactor.selectPiezaByBarra(barra);

                if (pieza != null) {
                    for (int i = 0; i < dbRuta.size(); i++) {
                        if (dbRuta.get(i).getIdServicio().equals(pieza.getIdServicioGuia())) {
                            onClickItem(i);
                        }
                    }
                } else {
                    for (int i = 0; i < dbRuta.size(); i++) {
                        if (dbRuta.get(i).getGuia().equalsIgnoreCase(barra)) {
                            onClickItem(i);
                        }
                    }
                }
            }
        }
    };
}