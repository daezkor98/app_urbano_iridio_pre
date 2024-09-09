package com.urbanoexpress.iridio3.pe.presenter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.urbanoexpress.iridio3.pe.util.async.AsyncTaskCoroutine;
import com.urbanoexpress.iridio3.pe.R;
import com.urbanoexpress.iridio3.pe.application.AndroidApplication;
import com.urbanoexpress.iridio3.pe.model.entity.Data;
import com.urbanoexpress.iridio3.pe.model.entity.Ruta;
import com.urbanoexpress.iridio3.pe.model.interactor.ConsideracionesImportantesRutaInteractor;
import com.urbanoexpress.iridio3.pe.model.interactor.RutaPendienteInteractor;
import com.urbanoexpress.iridio3.pe.presenter.helpers.ForzarCierreRutaHelper;
import com.urbanoexpress.iridio3.pe.ui.ConsideracionesImportantesRutaActivity;
import com.urbanoexpress.iridio3.pe.ui.InformacionRutaActivity;
import com.urbanoexpress.iridio3.pe.ui.MapaRutaDelDiaActivity;
import com.urbanoexpress.iridio3.pe.ui.TransferirGuiaActivity;
import com.urbanoexpress.iridio3.pe.ui.adapter.ManifiestoAdapter;
import com.urbanoexpress.iridio3.pe.ui.dialogs.AsignarRutaDialog;
import com.urbanoexpress.iridio3.pe.ui.dialogs.CodigoQRRutaDialog;
import com.urbanoexpress.iridio3.pe.ui.interfaces.OnClickItemListener;
import com.urbanoexpress.iridio3.pe.ui.model.ManifiestoItem;
import com.urbanoexpress.iridio3.pe.util.CommonUtils;
import com.urbanoexpress.iridio3.pe.util.constant.LocalAction;
import com.urbanoexpress.iridio3.pe.view.BaseModalsView;
import com.urbanoexpress.iridio3.pe.view.RutaRuralView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RutaRuralPresenter implements OnClickItemListener {

    private static final String TAG = RutaPresenter.class.getSimpleName();

    private RutaRuralView view;

    private RutaPendienteInteractor interactor;

    private List<Ruta> rutas = Collections.emptyList();
    private List<Ruta> manifiestos = Collections.emptyList();

    private AppCompatActivity activity;

    private AlertDialog dialog;

    private final int MOTIVO_CERRAR_RUTA = 1;
    private final int MOTIVO_ELIMINAR_MANIFIESTO = 2;

    public RutaRuralPresenter(RutaRuralView view) {
        this.view = view;
        this.activity = (AppCompatActivity) this.view.getViewContext();
        interactor = new RutaPendienteInteractor(this.view.getViewContext());
        init();
    }

    private void init() {
        LocalBroadcastManager.getInstance(view.getViewContext())
                .registerReceiver(transferenciaGuiaFinalizadaReceiver,
                        new IntentFilter(LocalAction.TRANSFERENCIA_GUIA_FINALIZADA_ACTION));
        LocalBroadcastManager.getInstance(view.getViewContext())
                .registerReceiver(resultScannReceiver,
                        new IntentFilter(LocalAction.LOCAL_ACTION_BARCODE_SCAN_RESULT));
    }

    public void onClickBoxConsideracionesImportantesRuta() {
        activity.startActivity(new Intent(activity, ConsideracionesImportantesRutaActivity.class));
        activity.overridePendingTransition(R.anim.slide_enter_from_right, R.anim.not_slide);
        view.setVisibilityBoxConsideracionesImportantesRuta(View.GONE);
    }

    public void onActionEliminarManifiesto() {
        long totalPendientes = interactor.getTotalRutasPendientes();
        manifiestos = interactor.selectManifiestos();

        if (totalPendientes > 0) {
            RecyclerView lvManifiestos;
            View view = activity.getLayoutInflater().inflate(R.layout.modal_eliminar_manifiesto, null);
            lvManifiestos = (RecyclerView) view.findViewById(R.id.lvManifiestos);
            lvManifiestos.setLayoutManager(new LinearLayoutManager(activity));

            ArrayList<ManifiestoItem> data = new ArrayList<>();

            for (Ruta ruta: manifiestos) {
                ManifiestoItem manifiestoItem = new ManifiestoItem(
                        ruta.getIdManifiesto(),
                        "Despachado el " + ruta.getFechaRuta());
                data.add(manifiestoItem);
            }

            ManifiestoAdapter adapter = new ManifiestoAdapter(data, this);
            lvManifiestos.setAdapter(adapter);

            dialog = new AlertDialog.Builder(activity).setView(view).create();
            dialog.show();
        } else {
            view.showToast(R.string.fragment_ruta_pendiente_message_no_hay_rutas);
        }
    }

    public void onActionInfoRutaDelDia() {
        long totalPendientes = interactor.getTotalRutasPendientes();
        long totalGestionados = interactor.getTotalRutasGestionadas();

        if (totalPendientes > 0 || totalGestionados > 0) {
            activity.startActivity(new Intent(activity, InformacionRutaActivity.class));
            activity.overridePendingTransition(R.anim.slide_enter_from_right, R.anim.not_slide);
        } else {
            view.showToast(R.string.fragment_ruta_pendiente_message_no_hay_rutas);
        }
    }

/*    public void onActionManifestarGuia() {
        long totalGuias = interactor.getTotalRutasPendientes();

        if (totalGuias == 0) {
            totalGuias = interactor.getTotalRutasGestionadas();
        }

        if (totalGuias == 0) {
            view.showToast(R.string.fragment_ruta_pendiente_message_no_hay_rutas);
            return;
        }

        activity.startActivity(new Intent(activity, ManifestarGuiaActivity.class));
        activity.overridePendingTransition(R.anim.slide_enter_from_right, R.anim.not_slide);
    }*/

    public void onActionConsideracionesImportantes() {
        long totalPendientes = interactor.getTotalRutasPendientes();
        long totalGestionados = interactor.getTotalRutasGestionadas();

        if (totalPendientes > 0 || totalGestionados > 0) {
            if (ConsideracionesImportantesRutaInteractor.getTotalRutasImportantes() > 0) {
                onClickBoxConsideracionesImportantesRuta();
            } else {
                view.showToast(R.string.fragment_ruta_pendiente_message_no_hay_consideraciones_importantes_ruta);
            }
        } else {
            view.showToast(R.string.fragment_ruta_pendiente_message_no_hay_rutas);
        }
    }

    public void onActionTransferirGuias() {
        long totalPendientes = interactor.getTotalRutasPendientes();

        if (totalPendientes > 0) {
            activity.startActivity(new Intent(activity, TransferirGuiaActivity.class));
            activity.overridePendingTransition(R.anim.slide_enter_from_right, R.anim.not_slide);
        } else {
            view.showToast(R.string.fragment_ruta_pendiente_message_no_hay_rutas);
        }
    }

    public void onActionAsignarRuta() {
        // No debe haber rutas pendientes
        // No debe haber ruta iniciada
        long totalEstadoRutas = interactor.getTotalAllEstadoRuta();

        if (totalEstadoRutas == 0) {
            if (!isRutasPendientes()) {
                AsignarRutaDialog dialog = new AsignarRutaDialog();
                dialog.show(activity.getSupportFragmentManager(), AsignarRutaDialog.TAG);
            } else {
                view.showToast(R.string.activity_ruta_message_no_puede_asignar_ruta);
            }
        } else {
            view.showToast(R.string.activity_ruta_message_ruta_iniciada_asignar_ruta);
        }
    }

/*    public void onActionEditarPlaca() {
        // Debe haber rutas pendientes
        // No debe haber ruta iniciada
        long totalEstadoRutas = interactor.getTotalAllEstadoRuta();

        if (totalEstadoRutas == 0) {
            List<Ruta> rutasPendientes = interactor.selectRutasPendientes();
            if (rutasPendientes.size() > 0) {
                EditarPlacaDialog dialog = EditarPlacaDialog.newInstance();
                dialog.show(activity.getSupportFragmentManager(), EditarPlacaDialog.TAG);
            } else {
                view.showToast(R.string.fragment_ruta_pendiente_message_no_hay_rutas);
            }
        } else {
            view.showToast(R.string.activity_ruta_message_ruta_iniciada_editar_placa);
        }
    }*/

    public void onActionCodigoQRRuta() {
        List<Ruta> pendientes = interactor.selectRutasPendientes();

        if (pendientes.size() > 0) {
            CodigoQRRutaDialog dialog = new CodigoQRRutaDialog();
            Bundle bundle = new Bundle();
            bundle.putString("idRuta", pendientes.get(0).getIdRuta());
            dialog.setArguments(bundle);
            dialog.show(activity.getSupportFragmentManager(), "CodigoQRRutaDialog");
        } else {
            view.showToast(R.string.fragment_ruta_pendiente_message_no_hay_rutas);
        }
    }

    public void onActionForzarCierreRuta() {
        new ForzarCierreRutaHelper(view.getViewContext(),
                ForzarCierreRutaHelper.ActionContext.RUTA_DEL_DIA).init();
    }

    public void onActionMapaRutaDelDia() {
        long totalPendientes = interactor.getTotalRutasPendientes();
        long totalGestionados = interactor.getTotalRutasGestionadas();

        if (totalPendientes > 0 || totalGestionados > 0) {
            activity.startActivity(new Intent(activity, MapaRutaDelDiaActivity.class));
            activity.overridePendingTransition(R.anim.slide_enter_from_right, R.anim.not_slide);
        } else {
            view.showToast(R.string.fragment_ruta_pendiente_message_no_hay_rutas);
        }
    }

    public void onDestroyActivity() {
        LocalBroadcastManager.getInstance(view.getViewContext())
                .unregisterReceiver(transferenciaGuiaFinalizadaReceiver);
        LocalBroadcastManager.getInstance(view.getViewContext())
                .unregisterReceiver(resultScannReceiver);
    }

    /**
     * Receiver
     *
     * {@link RutaPendientePresenter#manifiestoEliminadoReceiver}
     */
    private void sendOnManifiestoEliminadoReceiver(String idManifiesto) {
        Intent intent = new Intent("OnManifiestoEliminado");
        intent.putExtra("idManifiesto", idManifiesto);
        LocalBroadcastManager.getInstance(view.getViewContext()).sendBroadcast(intent);
    }

    private boolean isRutasPendientes() {
        //List<Ruta> rutasPendientes = interactor.selectRutasPendientes();
        rutas = interactor.selectRutasPendientes();
        if (rutas.size() > 0) {
            return true;
        }
        return false;
    }

    @Override
    public void onClickIcon(View view, final int position) {
        BaseModalsView.showAlertDialog(this.view.getViewContext(),
                R.string.activity_ruta_title_eliminar_manifiesto,
                R.string.activity_ruta_message_eliminar_manifiesto,
                R.string.text_eliminar, (dialog, which) -> {
                    new Thread(() -> {
                        List<Ruta> rutas = interactor.selectRutasByIDManifiesto(
                                manifiestos.get(position).getIdManifiesto());
                        Log.d(TAG, "Total rutas: " + rutas.size());

                        for (Ruta ruta: rutas) {
                            Log.d(TAG, "Guia: " + ruta.getGuia());
                            ruta.setEliminado(Data.Delete.YES);
                            ruta.save();
                        }

                        sendOnManifiestoEliminadoReceiver(
                                manifiestos.get(position).getIdManifiesto());

                        if (manifiestos.size() == 1) {
                            showProgressEliminandoDatosRuta();
                            new DeleteDatosRutaTask(MOTIVO_ELIMINAR_MANIFIESTO).execute();
                        }
                    }).start();
                    RutaRuralPresenter.this.dialog.dismiss();
                }, R.string.text_cancelar, null);
    }

    @Override
    public void onClickItem(View view, int position) {

    }

    private void showProgressEliminandoDatosRuta() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                view.showProgressDialog(R.string.activity_ruta_message_eliminando_datos_ruta);
            }
        });
    }

    /**
     * Broadcast
     *
     * {@link TransferirGuiaDialog#sendTransferenciaGuiaFinalizadaAction}
     */
    private BroadcastReceiver transferenciaGuiaFinalizadaReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    };

    /**
     * Broadcast
     *
     * {@link CodeScannerImpl#sendOnResultScannListener}
     */
    private BroadcastReceiver resultScannReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                CommonUtils.vibrateDevice(view.getViewContext(), 100);
            } catch (NullPointerException ex) {
                ex.printStackTrace();
            }

            Intent newIntent = new Intent(LocalAction.BUSCAR_GUIA_ACTION);
            newIntent.putExtra("tipoBusqueda", view.getSelectedTabPosition());
            newIntent.putExtra("value", intent.getStringExtra("value"));
            LocalBroadcastManager.getInstance(AndroidApplication.getAppContext()).sendBroadcast(newIntent);
        }
    };

    private class DeleteDatosRutaTask extends AsyncTaskCoroutine<String, String> {

        private int motivoEliminarDatosRuta = 0;

        public DeleteDatosRutaTask(int motivoEliminarDatosRuta) {
            this.motivoEliminarDatosRuta = motivoEliminarDatosRuta;
        }

        @Override
        public String doInBackground(String... strings) {
            ForzarCierreRutaHelper.deleteAllDataRuta(view.getViewContext());
            return "";
        }

        @Override
        public void onPostExecute(String s) {
            super.onPostExecute(s);

            if (motivoEliminarDatosRuta == MOTIVO_CERRAR_RUTA) {
                view.showToast(R.string.activity_ruta_message_ruta_finalizado_exitosamente);
            } else if (motivoEliminarDatosRuta == MOTIVO_ELIMINAR_MANIFIESTO) {
                view.showToast(R.string.activity_ruta_message_ruta_eliminada_correctamente);
            }

            view.dismissProgressDialog();
        }
    }

}