package com.urbanoexpress.iridio3.pe.presenter;

import androidx.core.content.ContextCompat;
import android.view.View;

import com.urbanoexpress.iridio3.pe.util.async.AsyncTaskCoroutine;
import com.urbanoexpress.iridio3.R;
import com.urbanoexpress.iridio3.pe.model.entity.GuiaGestionada;
import com.urbanoexpress.iridio3.pe.model.entity.Ruta;
import com.urbanoexpress.iridio3.pe.model.interactor.ConsideracionesImportantesRutaInteractor;
import com.urbanoexpress.iridio3.pe.model.interactor.RutaPendienteInteractor;
import com.urbanoexpress.iridio3.pe.model.util.ModelUtils;
import com.urbanoexpress.iridio3.pe.ui.adapter.RutaAdapter;
import com.urbanoexpress.iridio3.pe.ui.model.RutaItem;
import com.urbanoexpress.iridio3.pe.util.CommonUtils;
import com.urbanoexpress.iridio3.pe.view.BaseModalsView;
import com.urbanoexpress.iridio3.pe.view.ConsideracionesImportantesRutaView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ConsideracionesImportantesRutaPresenter implements RutaAdapter.OnClickGuiaItemListener {

    private ConsideracionesImportantesRutaView view;

    private List<RutaItem> rutaItems;
    private List<Ruta> dbRuta;

    private long totalRecoleccionesExpress = 0, totalGuiasRequerimiento = 0;

    public ConsideracionesImportantesRutaPresenter(ConsideracionesImportantesRutaView view) {
        this.view = view;

        new LoadGETask().execute();
    }

    public void onSwipeRefresh() {
        new LoadGETask().execute();
    }

    @Override
    public void onClickGuiaItem(View view, int position) {
        onClickGuia(position);
    }

    @Override
    public void onClickGuiaIconLinea(View view, int position) {
        onClickGuia(position);
    }

    @Override
    public void onClickGuiaIconImporte(View view, int position) {

    }

    @Override
    public void onClickGuiaIconTipoEnvio(View view, int position) {

    }

    private void onClickGuia(int position) {
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

                        BaseModalsView.showAlertDialog(view.getViewContext(),
                                dbRuta.get(position).getGuiaRequerimientoMotivo().trim(),
                                message,
                                view.getViewContext().getString(R.string.text_aceptar), null);
                    } else if (dbRuta.get(position).getGuiaRequerimientoCHK().equals("30")) {
                        String message = "Esta guía por motivo de " +
                                dbRuta.get(position).getGuiaRequerimientoMotivo().toUpperCase() + ", debe ser devuelta al shipper.";

                        BaseModalsView.showAlertDialog(view.getViewContext(),
                                "Devolución al Shipper",
                                message,
                                view.getViewContext().getString(R.string.text_aceptar), null);
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

                    BaseModalsView.showAlertDialog(view.getViewContext(),
                            dbRuta.get(position).getGuiaRequerimientoMotivo().trim(),
                            message,
                            view.getViewContext().getString(R.string.text_aceptar), null);
                }
            }
        }
    }

    private class LoadGETask extends AsyncTaskCoroutine<String, Boolean> {

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            view.setVisibilitySwipeRefreshLayout(true);
        }

        public Boolean doInBackground(String... urls) {
            dbRuta = ConsideracionesImportantesRutaInteractor.selectRutasImportantes();

            totalRecoleccionesExpress = ConsideracionesImportantesRutaInteractor.getTotalRecoleccionesExpress();
            totalGuiasRequerimiento = ConsideracionesImportantesRutaInteractor.getTotalGuiasRequerimiento();

            rutaItems = new ArrayList<>();

            for (int i = 0; i < dbRuta.size(); i++) {
                int resIcon = ModelUtils.getIconTipoGuia(dbRuta.get(i));

                int resIconTipoEnvio = ModelUtils.getIconTipoEnvio(dbRuta.get(i));

                int backgroundColorGuia = ModelUtils.getBackgroundColorGE(
                        dbRuta.get(i).getTipoEnvio(), view.getViewContext());

                if (dbRuta.get(i).getMostrarAlerta() == 1) {
                    backgroundColorGuia = ContextCompat.getColor(view.getViewContext(), R.color.blue_1_dark50);
                }

                String horario = dbRuta.get(i).getHorarioEntrega();

                if (dbRuta.get(i).getResultadoGestion() != 0) {
                    GuiaGestionada guiaGestionada =
                            RutaPendienteInteractor.selectRutaGestionada(dbRuta.get(i).getIdServicio(),
                                    dbRuta.get(i).getLineaNegocio());
                    if (guiaGestionada != null) {
                        try {
                            Date fechaHoraGestion = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(
                                    guiaGestionada.getFecha() + " " + guiaGestionada.getHora());
                            horario = CommonUtils.fomartHorarioAproximado(fechaHoraGestion.getTime(), false);
                        } catch (ParseException ex) {
                            ex.printStackTrace();
                        }
                    }
                }

                int lblColorHorario = ModelUtils.getLblColorHorario(dbRuta.get(i).getTipo(),
                        dbRuta.get(i).getTipoEnvio(), dbRuta.get(i).getFechaRuta(),
                        dbRuta.get(i).getHorarioEntrega());

                String simboloMoneda = ModelUtils.getSimboloMoneda(view.getViewContext());

                RutaItem rutaItem = new RutaItem(
                        dbRuta.get(i).getIdServicio(),
                        dbRuta.get(i).getIdManifiesto(),
                        (ModelUtils.isGuiaEntrega(dbRuta.get(i).getTipo())
                                ? dbRuta.get(i).getGuia()
                                : dbRuta.get(i).getShipper() + " (" + dbRuta.get(i).getGuia() + ")"),
                        dbRuta.get(i).getDistrito(),
                        dbRuta.get(i).getDireccion(),
                        horario,
                        dbRuta.get(i).getPiezas(),
                        dbRuta.get(i).getSecuencia(),
                        simboloMoneda,
                        resIcon,
                        resIcon,
                        resIconTipoEnvio,
                        backgroundColorGuia,
                        lblColorHorario,
                        dbRuta.get(i).getResultadoGestion(),
                        dbRuta.get(i).getResultadoGestion() > 0,
                        dbRuta.get(i).getResultadoGestion() == 0,
                        ModelUtils.isTipoEnvioValija(dbRuta.get(i).getTipoEnvio()),
                        ModelUtils.isShowIconImportePorCobrar(dbRuta.get(i).getImporte()),
                        false
                );

                rutaItems.add(rutaItem);

                dbRuta.get(i).setMostrarAlerta(0);
                dbRuta.get(i).save();
            }
            return true;
        }

        public void onPostExecute(Boolean result) {
            view.showDatosRutas(rutaItems);
            view.showTotalRecoleccionesExpress(totalRecoleccionesExpress);
            view.showTotalGuiasRequerimiento(totalGuiasRequerimiento);
            view.setVisibilitySwipeRefreshLayout(false);
        }
    }
}