package com.urbanoexpress.iridio3.presenter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import com.urbanoexpress.iridio3.R;
import com.urbanoexpress.iridio3.model.entity.IncidenteRuta;
import com.urbanoexpress.iridio3.model.entity.ParadaProgramada;
import com.urbanoexpress.iridio3.model.entity.PlanDeViaje;
import com.urbanoexpress.iridio3.model.interactor.PlanDeViajeInteractor;
import com.urbanoexpress.iridio3.ui.ParadaProgramadaActivity;
import com.urbanoexpress.iridio3.ui.ReportarIncidenteActivity;
import com.urbanoexpress.iridio3.ui.dialogs.InfoIncidenteRutaBottomSheetDialog;
import com.urbanoexpress.iridio3.ui.dialogs.InfoParadaProgramadaBottomSheetDialog;
import com.urbanoexpress.iridio3.ui.dialogs.IniciarTerminarRutaPlanDeViajeDialog;
import com.urbanoexpress.iridio3.util.CommonUtils;
import com.urbanoexpress.iridio3.util.LocationUtils;
import com.urbanoexpress.iridio3.util.constant.LocalAction;
import com.urbanoexpress.iridio3.view.GoogleMapView;

/**
 * Created by mick on 26/08/16.
 */

public class GoogleMapPresenter {

    private static final String TAG = GoogleMapPresenter.class.getSimpleName();

    private GoogleMapView view;
    private AppCompatActivity activity;
    private Bundle args;
    private PlanDeViaje planDeViaje;
    private ArrayList<ParadaProgramada> paradasProgramadas;
    private List<IncidenteRuta> incidentes;

    private int positionSelectedMarkerParadaProgramada = -1;

    public GoogleMapPresenter(GoogleMapView view, Bundle args) {
        this.view = view;
        this.activity = (AppCompatActivity) view;
        this.args = args;

        init();
    }

    private void init() {
        planDeViaje = (PlanDeViaje) args.getSerializable("planDeViaje");
        paradasProgramadas = (ArrayList<ParadaProgramada>) args.getSerializable("paradasProgramadas");
        incidentes = PlanDeViajeInteractor.selectIncidentesByIdPlanViaje(planDeViaje.getIdPlanViaje());

        view.showFABIniciarNavegacion();

        LocalBroadcastManager.getInstance(view.getViewContext())
                .registerReceiver(iniciarTerminarPlanDeViajeReceiver,
                        new IntentFilter(LocalAction.INICIAR_TERMINAR_PLAN_DE_VIAJE_ACTION));
        LocalBroadcastManager.getInstance(view.getViewContext())
                .registerReceiver(llegadaSalidaParadaProgramadaReceiver,
                        new IntentFilter(LocalAction.LLEGADA_SALIDA_PARADA_PROGRAMADA_ACTION));
        LocalBroadcastManager.getInstance(view.getViewContext())
                .registerReceiver(incidenteReportadoReceiver,
                        new IntentFilter(LocalAction.INCIDENTE_REPORTADO_ACTION));
    }

    public void onMapReady() {
        showMarkersMap();
    }

    public void onClickIniciarNavegacion() {
        if (planDeViaje.getEstadoRecorrido() == PlanDeViaje.EstadoRuta.INICIO_RUTA) {
            for (ParadaProgramada parada: paradasProgramadas) {
                if (parada.getEstadoLlegada() == ParadaProgramada.Status.NO_LLEGO_AGENCIA) {
                    if (CommonUtils.isValidCoords(parada.getAgencia_latitude(),
                            parada.getAgencia_longitude())) {
                        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + parada.getAgencia_latitude() + "," + parada.getAgencia_longitude());
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        view.getViewContext().startActivity(mapIntent);
                        break;
                    }
                } else if (parada.getEstadoLlegada() == ParadaProgramada.Status.LLEGO_AGENCIA) {
                    CommonUtils.vibrateDevice(view.getViewContext(), 100);
                    view.showToast("Actualmente esta en la parada (" + parada.getAgencia()
                            + "), debe marcar la salida antes de continuar su ruta.");
                    break;
                }
            }
        } else {
            IniciarTerminarRutaPlanDeViajeDialog dialog = IniciarTerminarRutaPlanDeViajeDialog
                    .newInstance(planDeViaje, paradasProgramadas);
            dialog.show(activity.getSupportFragmentManager(), IniciarTerminarRutaPlanDeViajeDialog.TAG);
        }
    }

    public void onClickGestionarDespachos() {
        switch (planDeViaje.getEstadoRecorrido()) {
            case PlanDeViaje.EstadoRuta.NO_INICIO_RUTA:
                CommonUtils.vibrateDevice(view.getViewContext(), 100);
                view.showToast(R.string.activity_ruta_message_ruta_no_iniciada);
                break;
            case PlanDeViaje.EstadoRuta.INICIO_RUTA:
                for (ParadaProgramada parada: paradasProgramadas) {
                    if (parada.getEstadoLlegada() != ParadaProgramada.Status.SALIO_AGENCIA) {
                        Bundle args = new Bundle();

                        args.putSerializable("plan_de_viaje", planDeViaje);
                        args.putSerializable("parada_programada", parada);

                        view.getViewContext().startActivity(
                                new Intent(view.getViewContext(), ParadaProgramadaActivity.class)
                                        .putExtra("args", args));
                        break;
                    }
                }
                break;
            case PlanDeViaje.EstadoRuta.TERMINO_RUTA:
                CommonUtils.vibrateDevice(view.getViewContext(), 100);
                view.showToast(R.string.activity_ruta_message_ruta_ya_finalizada);
                break;
        }
    }

    public void onClickReportarIncidencia() {
        switch (planDeViaje.getEstadoRecorrido()) {
            case PlanDeViaje.EstadoRuta.NO_INICIO_RUTA:
                CommonUtils.vibrateDevice(view.getViewContext(), 100);
                view.showToast(R.string.activity_ruta_message_ruta_no_iniciada);
                break;
            case PlanDeViaje.EstadoRuta.INICIO_RUTA:
                Intent intent = new Intent(view.getViewContext(), ReportarIncidenteActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("idPlanViaje", planDeViaje.getIdPlanViaje());
                //bundle.putString("idParadaProgramada", paradasProgramadas.get(0).getIdStop());
                intent.putExtra("args", bundle);
                view.getViewContext().startActivity(intent);
                break;
            case PlanDeViaje.EstadoRuta.TERMINO_RUTA:
                CommonUtils.vibrateDevice(view.getViewContext(), 100);
                view.showToast(R.string.activity_ruta_message_ruta_ya_finalizada);
                break;
        }
    }

    public boolean isNavegacionPlanDeViajePermitido() {
        if (planDeViaje.getEstadoRecorrido() != PlanDeViaje.EstadoRuta.TERMINO_RUTA) {
            for (ParadaProgramada parada: paradasProgramadas) {
                if (parada.getTipo().equalsIgnoreCase("U")) { // HUB
                    if (parada.getEstadoLlegada() == ParadaProgramada.Status.NO_LLEGO_AGENCIA) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public boolean isBtnGestionarParadaPermitido() {
        if (planDeViaje.getEstadoRecorrido() != PlanDeViaje.EstadoRuta.TERMINO_RUTA) {
            return true;
        }

        return false;
    }

    public void onClickMarkerMap(Object tag) {
        int tipoMarker = Integer.valueOf(String.valueOf(tag).substring(0, 1));

        if (tipoMarker == 1) { // incidentes
            int position = Integer.valueOf(String.valueOf(tag).substring(1));
            InfoIncidenteRutaBottomSheetDialog bottomSheetDialog = InfoIncidenteRutaBottomSheetDialog
                    .newInstance(incidentes.get(position));
            bottomSheetDialog.show(activity.getSupportFragmentManager(), InfoIncidenteRutaBottomSheetDialog.TAG);
        } else if (tipoMarker == 2) { // paradas
            int position = Integer.valueOf(String.valueOf(tag).substring(1));
            positionSelectedMarkerParadaProgramada = position;
            InfoParadaProgramadaBottomSheetDialog bottomSheetDialog = InfoParadaProgramadaBottomSheetDialog
                    .newInstance(planDeViaje, paradasProgramadas.get(positionSelectedMarkerParadaProgramada));
            bottomSheetDialog.show(activity.getSupportFragmentManager(), InfoParadaProgramadaBottomSheetDialog.TAG);
        }
    }

    public void onDestroyActivity() {
        LocalBroadcastManager.getInstance(view.getViewContext())
                .unregisterReceiver(iniciarTerminarPlanDeViajeReceiver);
        LocalBroadcastManager.getInstance(view.getViewContext())
                .unregisterReceiver(llegadaSalidaParadaProgramadaReceiver);
        LocalBroadcastManager.getInstance(view.getViewContext())
                .unregisterReceiver(incidenteReportadoReceiver);
    }

    private boolean isLlegoAlHub() {
        for (ParadaProgramada parada: paradasProgramadas) {
            if (parada.getTipo().equalsIgnoreCase("U")) { // HUB
                if (parada.getEstadoLlegada() == ParadaProgramada.Status.LLEGO_AGENCIA) {
                    return true;
                }
            }
        }

        return false;
    }

    private int getResIconByTipoParada(ParadaProgramada parada) {
        if (parada.getTipo().equalsIgnoreCase("U")) { // Hub
            return R.drawable.ic_marker_planviaje_destino;
        } else if (parada.getTipo().equalsIgnoreCase("P")) { // Escalas/Paradas
            switch (parada.getEstadoLlegada()) {
                case ParadaProgramada.Status.NO_LLEGO_AGENCIA:
                case ParadaProgramada.Status.LLEGO_AGENCIA:
                    return R.drawable.ic_marker_planviaje_parada_pendiente;
                case ParadaProgramada.Status.SALIO_AGENCIA:
                    return R.drawable.ic_marker_planviaje_parada_gestionada;
            }
        }

        return R.drawable.ic_map_marker_red; // default
    }

    private void showMarkersMap() {
        if (CommonUtils.isValidCoords(planDeViaje.getOrigen_latitude(), planDeViaje.getOrigen_longitude())) {
            if (CommonUtils.isValidCoords(planDeViaje.getOrigen_latitude(),
                    planDeViaje.getOrigen_longitude())) {
                view.addMarker(
                        Double.parseDouble(planDeViaje.getOrigen_latitude()),
                        Double.parseDouble(planDeViaje.getOrigen_longitude()),
                        R.drawable.ic_marker_planviaje_origen,
                        "0"
                );
            }
        }

        for (int i = 0; i < incidentes.size(); i++) {
            if (CommonUtils.isValidCoords(incidentes.get(i).getGpsLatitude(),
                    incidentes.get(i).getGpsLongitude())) {
                view.addMarker(
                        Double.parseDouble(incidentes.get(i).getGpsLatitude()),
                        Double.parseDouble(incidentes.get(i).getGpsLongitude()),
                        R.drawable.ic_marker_planviaje_incidente,
                        "1" + i
                );
            }
        }

        for (int i = 0; i < paradasProgramadas.size(); i++) {
            if (CommonUtils.isValidCoords(paradasProgramadas.get(i).getAgencia_latitude(),
                    paradasProgramadas.get(i).getAgencia_longitude())) {
                view.addMarker(
                        Double.parseDouble(paradasProgramadas.get(i).getAgencia_latitude()),
                        Double.parseDouble(paradasProgramadas.get(i).getAgencia_longitude()),
                        getResIconByTipoParada(paradasProgramadas.get(i)),
                        "2" + i
                );
            }
        }

        if (planDeViaje.getEstadoRecorrido() == PlanDeViaje.EstadoRuta.NO_INICIO_RUTA) {
            view.centerCameraToMarkers();
        } else {
            if (CommonUtils.isValidCoords(LocationUtils.getLatitude(), LocationUtils.getLatitude())) {
                LatLng locationDefault = new LatLng(LocationUtils.getLatitude(), LocationUtils.getLongitude());
                view.centerCameraToMyLocation(locationDefault, 8);
            } else {
                view.centerCameraToMarkers();
            }
        }
    }

    /**
     * Broadcast
     *
     * {@link IniciarTerminarRutaPlanDeViajeDialog#sendIniciarTerminarPlanDeViajeReceiver}
     */
    private final BroadcastReceiver iniciarTerminarPlanDeViajeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            planDeViaje.setEstadoRecorrido(intent.getIntExtra("nuevoEstadoRuta", 0));
        }
    };

    /**
     * Broadcast
     *
     * {@link InfoParadaProgramadaBottomSheetDialog#sendLlegadaSalidaParadaProgramadaReceiver}
     */
    private final BroadcastReceiver llegadaSalidaParadaProgramadaReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final ParadaProgramada paradaProgramada = (ParadaProgramada) intent.getSerializableExtra("paradaProgramada");
            paradasProgramadas.set(positionSelectedMarkerParadaProgramada, paradaProgramada);

            activity.runOnUiThread(() -> view.updateIconMarker("2" + positionSelectedMarkerParadaProgramada,
                    getResIconByTipoParada(paradaProgramada)));
        }
    };

    /**
     * Broadcast
     *
     * {@link ReportarIncidentePresenter#sendIncidenteReportadoReceiver}
     */
    private final BroadcastReceiver incidenteReportadoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, final Intent intent) {
            activity.runOnUiThread(() -> {
                IncidenteRuta incidente = (IncidenteRuta) intent.getSerializableExtra("incidente");

                incidentes.add(incidente);

                if (CommonUtils.isValidCoords(incidente.getGpsLatitude(),
                        incidente.getGpsLongitude())) {
                    view.addMarker(
                            Double.parseDouble(incidente.getGpsLatitude()),
                            Double.parseDouble(incidente.getGpsLongitude()),
                            R.drawable.ic_marker_planviaje_incidente,
                            "1" + (incidentes.size() - 1)
                    );
                }
            });
        }
    };
}