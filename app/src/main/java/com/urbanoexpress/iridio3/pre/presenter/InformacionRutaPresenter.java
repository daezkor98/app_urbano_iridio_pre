package com.urbanoexpress.iridio3.pre.presenter;

import com.urbanoexpress.iridio3.pre.util.async.AsyncTaskCoroutine;
import com.urbanoexpress.iridio3.pre.model.entity.Data;
import com.urbanoexpress.iridio3.pre.model.interactor.InformacionRutaInteractor;
import com.urbanoexpress.iridio3.pre.view.InformacionRutaView;

/**
 * Created by mick on 11/01/17.
 */

public class InformacionRutaPresenter {

    private InformacionRutaView view;
    private InformacionRutaInteractor interactor;

    private long totalGuias;
    private long totalGuiasPendientes;
    private long totalGuiasGestionados;
    private long totalImagenes;
    private long totalGestiones;
    private long totalTramas;
    private long totalGestionLlamadas;
    private long totalSyncImagenes;
    private long totalSyncGestiones;
    private long totalSyncTramas;
    private long totalSyncGestionLlamadas;

    public InformacionRutaPresenter(InformacionRutaView view) {
        this.view = view;
        this.interactor = new InformacionRutaInteractor("(2, 3)");
    }

    public void init() {
        new LoadDatosRuta(true).execute();
    }

    public void onSwipeRefresh() {
        new LoadDatosRuta(false).execute();
    }

    private class LoadDatosRuta extends AsyncTaskCoroutine<String, String> {

        private boolean showProgressDialog;

        public LoadDatosRuta(boolean showProgressDialog) {
            this.showProgressDialog = showProgressDialog;
        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            if (showProgressDialog) {
                view.showProgressDialog();
            }
        }

        @Override
        public String doInBackground(String... strings) {
            totalGuias = interactor.getTotalGuias();
            totalGuiasPendientes = interactor.getTotalGuiasPendientes();
            totalGuiasGestionados = interactor.getTotalGuiasGestionadas();
            totalImagenes = interactor.getTotalImagenes();
            totalGestiones = interactor.getTotalGestiones();
            totalTramas = interactor.getTotalTramasGPS();
            totalGestionLlamadas = interactor.getTotalGestionLlamadas();
            totalSyncImagenes = interactor.getTotatImagenesBySync(Data.Sync.SYNCHRONIZED);
            totalSyncGestiones = interactor.getTotalGestionesBySync(Data.Sync.SYNCHRONIZED);
            totalSyncTramas = interactor.getTotalTramasBySync(Data.Sync.SYNCHRONIZED);
            totalSyncGestionLlamadas = interactor.getTotalGestionLlamadasBySync(Data.Sync.SYNCHRONIZED);
            return null;
        }

        @Override
        public void onPostExecute(String s) {
            super.onPostExecute(s);
            view.setTotalGuias(String.valueOf(totalGuias));
            view.setTotalPendientes(String.valueOf(totalGuiasPendientes));
            view.setTotalGestionados(String.valueOf(totalGuiasGestionados));
            view.setTotalGestionadosFallidos(String.valueOf(interactor.getTotalGuiasGestionadasFallidas()));

            view.setProgressRoute(Math.round(totalGuiasGestionados * 100 / totalGuias));

            view.setValueSyncGestiones((int) totalSyncGestiones);
            view.setValuePendingGestiones((int) (totalGestiones - totalSyncGestiones));

            view.setValueSyncImagenes((int) totalSyncImagenes);
            view.setValuePendingImagenes((int) (totalImagenes - totalSyncImagenes));

            view.setValueSyncLlamadas((int) totalSyncGestionLlamadas);
            view.setValuePendingLlamadas((int) (totalGestionLlamadas - totalSyncGestionLlamadas));

            view.setValueSyncGPS((int) totalSyncTramas);
            view.setValuePendingGPS((int) (totalTramas - totalSyncTramas));

            if (totalSyncGestiones > 0) {
                view.setProgressGestiones(Math.round(totalSyncGestiones * 100 / totalGestiones));
            }
            if (totalSyncImagenes > 0) {
                view.setProgressImagenes(Math.round(totalSyncImagenes * 100 / totalImagenes));
            }
            if (totalSyncGestionLlamadas > 0) {
                view.setProgressLlamadas(Math.round(totalSyncGestionLlamadas * 100 / totalGestionLlamadas));
            }
            if (totalSyncTramas > 0) {
                view.setProgressGPS(Math.round(totalSyncTramas * 100 / totalTramas));
            }
            view.dismissProgressDialog();
            view.hideSwipeRefreshLayout();
        }
    }
}
