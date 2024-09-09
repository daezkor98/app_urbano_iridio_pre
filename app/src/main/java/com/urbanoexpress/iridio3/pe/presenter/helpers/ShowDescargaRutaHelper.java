package com.urbanoexpress.iridio3.pe.presenter.helpers;

import android.content.Context;

import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import com.orm.util.NamingHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.urbanoexpress.iridio3.R;
import com.urbanoexpress.iridio3.pe.model.entity.Data;
import com.urbanoexpress.iridio3.pe.model.entity.DescargaRuta;
import com.urbanoexpress.iridio3.pe.model.entity.Ruta;
import com.urbanoexpress.iridio3.pe.model.entity.GuiaGestionada;
import com.urbanoexpress.iridio3.pe.model.interactor.RutaPendienteInteractor;
import com.urbanoexpress.iridio3.pe.model.util.ModelUtils;
import com.urbanoexpress.iridio3.pe.ui.adapter.GestionGEHabilitanteAdapter;
import com.urbanoexpress.iridio3.pe.ui.dialogs.EntregaGEDialog;
import com.urbanoexpress.iridio3.pe.ui.dialogs.NoEntregaGEDialog;
import com.urbanoexpress.iridio3.pe.ui.dialogs.RecoleccionCounterDialog;
import com.urbanoexpress.iridio3.pe.ui.dialogs.RecoleccionGEDialog;
import com.urbanoexpress.iridio3.pe.ui.dialogs.RecoleccionLogisticaInversaDialog;
import com.urbanoexpress.iridio3.pe.ui.dialogs.RecoleccionSellerDialog;
import com.urbanoexpress.iridio3.pe.ui.dialogs.RecolectaGEDialog;
import com.urbanoexpress.iridio3.pe.ui.helpers.ModalHelper;
import com.urbanoexpress.iridio3.pe.util.CommonUtils;
import com.urbanoexpress.iridio3.pe.util.LocationUtils;
import com.urbanoexpress.iridio3.pe.util.Preferences;
import com.urbanoexpress.iridio3.pe.view.BaseModalsView;

/**
 * Created by mick on 12/07/16.
 */
public class ShowDescargaRutaHelper extends BaseModalsView {

    private static final String TAG = ShowDescargaRutaHelper.class.getSimpleName();

    private ArrayList<Ruta> rutas = new ArrayList<>();
    private Map<String, String> mapIDsGE;

    private ArrayList<DescargaRuta> descargaRutas = new ArrayList<>();

    private View viewModal;

    private AppCompatActivity activity;

    private LayoutInflater layoutInflater;

    private int numVecesGestionado = 0;

    private RutaPendienteInteractor rutaPendienteInteractor;

    public ShowDescargaRutaHelper(Context context, ArrayList<Ruta> rutas, int numVecesGestionado) {
        this.activity = (AppCompatActivity) context;
        this.rutas = rutas;
        this.layoutInflater = activity.getLayoutInflater();
        this.numVecesGestionado = numVecesGestionado;
        this.rutaPendienteInteractor = new RutaPendienteInteractor(context);
        loadDataPreguntaDescargaRuta();
    }

    public ShowDescargaRutaHelper(Context context, LinkedHashMap<String, String> mapIDsGE, int numVecesGestionado) {
        this.activity = (AppCompatActivity) context;
        this.mapIDsGE = mapIDsGE;
        this.layoutInflater = activity.getLayoutInflater();
        this.numVecesGestionado = numVecesGestionado;
        this.rutaPendienteInteractor = new RutaPendienteInteractor(context);
        loadDataPreguntaDescargaRutaFromHashMap();
    }

    private void loadDataPreguntaDescargaRuta() {
        Log.d(TAG, "loadDataPreguntaDescargaRuta");
        Log.d(TAG, "rutas: " + rutas.size());
        for (int i = 0; i < rutas.size(); i++) {
            List<DescargaRuta> descargaRutaList = DescargaRuta.find(DescargaRuta.class,
                    NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                            NamingHelper.toSQLNameDefault("idServicio") + " = ? and " +
                            NamingHelper.toSQLNameDefault("lineaNegocio") + " = ? ",
                    Preferences.getInstance().getString("idUsuario", ""),
                    rutas.get(i).getIdServicio(), rutas.get(i).getLineaNegocio());

            if (descargaRutaList.size() > 0) {
                descargaRutas.add(descargaRutaList.get(0));
            } else {
                descargaRutas.add(newDescargaRuta(rutas.get(i)));
            }
        }
        Log.d(TAG, "descargaRutas: " + descargaRutas.size());
    }

    private void loadDataPreguntaDescargaRutaFromHashMap() {
        Log.d(TAG, "loadDataPreguntaDescargaRuta");
        Log.d(TAG, "mapIDsGE: " + mapIDsGE.size());
        for (Map.Entry<String, String> entry : mapIDsGE.entrySet()) {
            Log.d(TAG, "Key = " + entry.getKey() + ", Value = " + entry.getValue());
            Ruta ruta = rutaPendienteInteractor.selectRuta(entry.getKey(), entry.getValue());
            List<DescargaRuta> descargaRutaList = DescargaRuta.find(DescargaRuta.class,
                    NamingHelper.toSQLNameDefault("idUsuario") + " = ? and " +
                            NamingHelper.toSQLNameDefault("idServicio") + " = ? and " +
                            NamingHelper.toSQLNameDefault("lineaNegocio") + " = ? ",
                    Preferences.getInstance().getString("idUsuario", ""),
                    ruta.getIdServicio(), ruta.getLineaNegocio());

            if (descargaRutaList.size() > 0) {
                descargaRutas.add(descargaRutaList.get(0));
            } else {
                descargaRutas.add(newDescargaRuta(ruta));
            }
            rutas.add(ruta);
        }
    }

    private DescargaRuta newDescargaRuta(Ruta ruta) {
        DescargaRuta descargaRuta = null;
        if (ModelUtils.getTipoGuia(ruta.getTipo()) == Ruta.Tipo.ENTREGA) {
            descargaRuta = new DescargaRuta(
                    Preferences.getInstance().getString("idUsuario", ""),
                    ruta.getIdServicio(),
                    ruta.getLineaNegocio(),
                    Ruta.Tipo.ENTREGA,
                    DescargaRuta.Entrega.ENTREGA_EFECTIVA
            );
            descargaRuta.save();
        } else if (ModelUtils.getTipoGuia(ruta.getTipo()) == Ruta.Tipo.RECOLECCION) {
            descargaRuta = new DescargaRuta(
                    Preferences.getInstance().getString("idUsuario", ""),
                    ruta.getIdServicio(),
                    ruta.getLineaNegocio(),
                    Ruta.Tipo.RECOLECCION,
                    DescargaRuta.Recoleccion.LLEGO_DIRECCION_RECOJO
            );
            descargaRuta.save();
        }
        return descargaRuta;
    }

    public void onClickDescarga() {
        if (validateEqualsEstadoDescargaRuta()) {
            Log.d(TAG, "Proceso descarga: " + descargaRutas.get(0).getProcesoDescarga());

            DialogFragment dialogFragment = null;
            String tagFragment = "";

            switch (descargaRutas.get(0).getProcesoDescarga()) {
                case DescargaRuta.Entrega.ENTREGA_EFECTIVA:
                    ModalHelper.getBuilderAlertDialog(activity)
                            .setTitle("¿Se va entregar el producto?")
                            .setPositiveButton("Si, entregar", (dialog, which) -> {
                                if (isCobrosPendientesGuias()) {
                                    updateProcesoDescarga(DescargaRuta.Entrega.RECAUDO_IMPORTE);
                                } else if (isHabilitantes()) {
                                    updateProcesoDescarga(DescargaRuta.Entrega.RECOGIO_HABILITANTES);
                                } else {
                                    updateProcesoDescarga(DescargaRuta.Entrega.ENTREGAR);
                                }
                                onClickDescarga();
                            })
                            .setNegativeButton("No, registrar visita", (dialog, which) -> {
                                updateProcesoDescarga(DescargaRuta.Entrega.NO_ENTREGO);
                                onClickDescarga();
                            })
                            .setNeutralButton(R.string.text_cancelar, null)
                            .show();
                    break;
                case DescargaRuta.Entrega.RECAUDO_IMPORTE:
                    /*no-op*/

                    if (isHabilitantes()) {
                        updateProcesoDescarga(DescargaRuta.Entrega.RECOGIO_HABILITANTES);
                    } else {
                        updateProcesoDescarga(DescargaRuta.Entrega.ENTREGAR);
                    }
                    onClickDescarga();

                    /*
                    viewModal = layoutInflater.inflate(R.layout.modal_recaudo_importe, null);

                    final RadioButton rBtnSiRecaudo = (RadioButton) viewModal.findViewById(R.id.rBtnSi);

                    showAlertDialog(activity, viewModal,
                            R.string.text_siguiente, (dialog, which) -> {
                                if (rBtnSiRecaudo.isChecked()) {
                                    if (isHabilitantes()) {
                                        updateProcesoDescarga(DescargaRuta.Entrega.RECOGIO_HABILITANTES);
                                    } else {
                                        updateProcesoDescarga(DescargaRuta.Entrega.ENTREGAR);
                                    }
                                    onClickDescarga();
                                } else {
                                    showToast(activity, "No se olvide recaudar el importe de la entrega.", Toast.LENGTH_LONG);
                                }
                            }, R.string.text_cancelar, null);*/
                    break;
                case DescargaRuta.Entrega.RECOGIO_HABILITANTES:
                    viewModal = layoutInflater.inflate(R.layout.modal_recogio_habilitantes, null);

                    final RecyclerView rvHabilitantes = (RecyclerView) viewModal.findViewById(R.id.rvHabilitantes);
                    final RadioButton rBtnSiRecogioHabilitante = (RadioButton) viewModal.findViewById(R.id.rBtnSi);

                    rvHabilitantes.setLayoutManager(new LinearLayoutManager(activity));

                    ArrayList<String> habilitantes = generateListaHabilitantes();
                    GestionGEHabilitanteAdapter adapter = new GestionGEHabilitanteAdapter(activity, habilitantes);
                    rvHabilitantes.setAdapter(adapter);

                    showAlertDialog(activity, viewModal,
                            R.string.text_siguiente, (dialog, which) -> {
                                if (rBtnSiRecogioHabilitante.isChecked()) {
                                    updateProcesoDescarga(DescargaRuta.Entrega.ENTREGAR);
                                    onClickDescarga();
                                } else {
                                    showToast(activity, "No se olvide recoger los habilitantes.", Toast.LENGTH_LONG);
                                }
                            }, R.string.text_cancelar, null);
                    break;
                case DescargaRuta.Entrega.ENTREGAR:
                    dialogFragment = EntregaGEDialog.newInstance(rutas, numVecesGestionado);
                    tagFragment = EntregaGEDialog.TAG;
                    break;
                case DescargaRuta.Entrega.NO_ENTREGO:
                case DescargaRuta.Recoleccion.NO_RECOLECTO:
                    dialogFragment = NoEntregaGEDialog.newInstance(rutas, numVecesGestionado);
                    tagFragment = NoEntregaGEDialog.TAG;
                    break;
                case DescargaRuta.Recoleccion.LLEGO_DIRECCION_RECOJO:
                    ModalHelper.getBuilderAlertDialog(activity)
                            .setTitle("¿Llego a la dirección de recolección?")
                            .setPositiveButton("Si, llegue", (dialog, which) -> {
                                updateProcesoDescarga(DescargaRuta.Recoleccion.RECOLECCION_EFECTIVA);
                                generateLLegadaRecoleccion();
                                onClickDescarga();
                            })
                            .setNegativeButton(R.string.text_cancelar, null)
                            .show();
                    break;
                case DescargaRuta.Recoleccion.RECOLECCION_EFECTIVA:
                    ModalHelper.getBuilderAlertDialog(activity)
                            .setTitle("¿Se va recolectar el producto?")
                            .setPositiveButton("Si, recolectar", (dialog, which) -> {
                                if (isRecoleccionLogisticaInversa()) {
                                    updateProcesoDescarga(DescargaRuta.Recoleccion.RECOLECTAR_LOGISTICA_INVERSA);
                                } else if (isRecoleccionValija()) {
                                    updateProcesoDescarga(DescargaRuta.Recoleccion.RECOLECTAR_VALIJA);
                                } else if (isRecoleccionSeller()) {
                                    updateProcesoDescarga(DescargaRuta.Recoleccion.RECOLECTAR_SELLER);
                                } else if (isRecoleccionCounter()) {
                                    updateProcesoDescarga(DescargaRuta.Recoleccion.RECOLECTAR_COUNTER);
                                } else {
                                    updateProcesoDescarga(DescargaRuta.Recoleccion.GUIA_ELECTRONICA_DISPONIBLE);
                                }
                                onClickDescarga();
                            })
                            .setNegativeButton("No, registrar visita", (dialog, which) -> {
                                updateProcesoDescarga(DescargaRuta.Recoleccion.NO_RECOLECTO);
                                onClickDescarga();
                            })
                            .setNeutralButton(R.string.text_cancelar, null)
                            .show();
                    break;
                case DescargaRuta.Recoleccion.GUIA_ELECTRONICA_DISPONIBLE:
                    ModalHelper.getBuilderAlertDialog(activity)
                            .setTitle("¿Se va marcar las guías electrónicas?")
                            .setPositiveButton("Si, marcar", (dialog, which) -> {
                                updateProcesoDescarga(DescargaRuta.Recoleccion.RECOLECTAR_CON_GUIA_ELECTRONICA);
                                onClickDescarga();
                            })
                            .setNegativeButton("No, es sin guía electrónica", (dialog, which) -> {
                                updateProcesoDescarga(DescargaRuta.Recoleccion.RECOLECTAR_SIN_GUIA_ELECTRONICA);
                                onClickDescarga();
                            })
                            .setNeutralButton(R.string.text_cancelar, null)
                            .show();
                    break;
                case DescargaRuta.Recoleccion.RECOLECTAR_CON_GUIA_MANUAL:
                    dialogFragment = RecolectaGEDialog.newInstance(rutas, numVecesGestionado, true);
                    tagFragment = RecolectaGEDialog.TAG;
                    break;
                case DescargaRuta.Recoleccion.RECOLECTAR_SIN_GUIA_ELECTRONICA:
                    dialogFragment = RecoleccionGEDialog.newInstance(rutas, numVecesGestionado, false);
                    tagFragment = RecoleccionGEDialog.TAG;
                    break;
                case DescargaRuta.Recoleccion.RECOLECTAR_CON_GUIA_ELECTRONICA:
                    dialogFragment = RecoleccionGEDialog.newInstance(rutas, numVecesGestionado, true);
                    tagFragment = RecoleccionGEDialog.TAG;
                    break;
                case DescargaRuta.Recoleccion.RECOLECTAR_LOGISTICA_INVERSA:
                case DescargaRuta.Recoleccion.RECOLECTAR_VALIJA:
                    dialogFragment = RecoleccionLogisticaInversaDialog.newInstance(rutas);
                    tagFragment = RecoleccionLogisticaInversaDialog.TAG;
                    break;
                case DescargaRuta.Recoleccion.RECOLECTAR_SELLER:
                    dialogFragment = RecoleccionSellerDialog.newInstance(rutas, numVecesGestionado);
                    tagFragment = RecoleccionSellerDialog.TAG;
                    break;
                case DescargaRuta.Recoleccion.RECOLECTAR_COUNTER:
                    dialogFragment = RecoleccionCounterDialog.newInstance(rutas, numVecesGestionado);
                    tagFragment = RecoleccionCounterDialog.TAG;
                    break;
                case DescargaRuta.Recoleccion.FINALIZADO:
                    showToast(activity, "La guía ya fue gestionada.", Toast.LENGTH_LONG);
                    break;
            }

            if (dialogFragment != null) {
                dialogFragment.show(activity.getSupportFragmentManager(), tagFragment);
            }
        } else {
            showAlertDialog(activity,
                    R.string.text_advertencia,
                    R.string.fragment_ruta_pendiente_message_ruta_estado_diferentes,
                    R.string.text_aceptar, null);
        }
    }

    public void onClickVolverConfirmarGestion() {
        if (validateDescargaRuta()) {
            if (descargaRutas.get(0).getProcesoDescarga() == DescargaRuta.Entrega.FINALIZADO) {
                showToast(activity, "La guía ya fue gestionada.", Toast.LENGTH_LONG);
            } else {
                if (numVecesGestionado == 1) {
                    switch (descargaRutas.get(0).getTipoDescarga()) {
                        case Ruta.Tipo.ENTREGA:
                            if (descargaRutas.get(0).getProcesoDescarga()
                                    >= DescargaRuta.Entrega.ENTREGAR) {
                                updateProcesoDescarga(DescargaRuta.Entrega.ENTREGA_EFECTIVA);
                                showToast(activity,
                                        "Vuelva a confirmar la gestión de la guía.",
                                        Toast.LENGTH_SHORT);
                            } else {
                                showToast(activity,
                                        "La gestión de la guía tiene preguntas pendientes.",
                                        Toast.LENGTH_SHORT);
                            }
                            break;
                        case Ruta.Tipo.RECOLECCION:
                            if (descargaRutas.get(0).getProcesoDescarga()
                                    >= DescargaRuta.Recoleccion.RECOLECTAR_SIN_GUIA_ELECTRONICA) {
                                updateProcesoDescarga(DescargaRuta.Recoleccion.RECOLECCION_EFECTIVA);
                                showToast(activity,
                                        "Vuelva a confirmar la gestión de la recolección.",
                                        Toast.LENGTH_SHORT);
                            } else {
                                showToast(activity,
                                        "La gestión de la recolección tiene preguntas pendientes.",
                                        Toast.LENGTH_SHORT);
                            }
                            break;
                    }
                } else {
                    showToast(activity,
                            "La acción solo es permitida para la primera visita.",
                            Toast.LENGTH_SHORT);
                }
            }
        } else {
            showToast(activity,
                    "Lo sentimos, ocurrió un error al validar el estado de gestion de la guia.",
                    Toast.LENGTH_SHORT);
        }
    }

    public void onClickVolverConfirmarGestionGuiaVisitada() {
        if (validateDescargaRuta()) {
            if (descargaRutas.get(0).getProcesoDescarga() == DescargaRuta.Entrega.FINALIZADO) {
                showToast(activity, "La guía ya fue gestionada.", Toast.LENGTH_LONG);
            } else {
                if (descargaRutas.get(0).getTipoDescarga() == Ruta.Tipo.ENTREGA) {
                    if (descargaRutas.get(0).getProcesoDescarga()
                            >= DescargaRuta.Entrega.ENTREGAR) {
                        updateProcesoDescarga(DescargaRuta.Entrega.ENTREGA_EFECTIVA);
                        showToast(activity,
                                "Vuelva a confirmar la gestión de la guía.",
                                Toast.LENGTH_SHORT);
                    } else {
                        showToast(activity,
                                "La gestión de la guía tiene preguntas pendientes.",
                                Toast.LENGTH_SHORT);
                    }
                }
            }
        } else {
            showToast(activity,
                    "Lo sentimos, ocurrió un error al validar el estado de gestion de la guia.",
                    Toast.LENGTH_SHORT);
        }
    }

    private boolean validateEqualsEstadoDescargaRuta() {
        boolean equalsEstadoDescargaRuta = true;
        if (descargaRutas.size() > 1) {
            for (int i = 0; i < descargaRutas.size(); i++) {
                if (i < descargaRutas.size() - 1) {
                    if (descargaRutas.get(i).getProcesoDescarga() !=
                            descargaRutas.get(i + 1).getProcesoDescarga()) {
                        equalsEstadoDescargaRuta = false;
                    }
                }
            }
        }
        return equalsEstadoDescargaRuta;
    }

    private boolean validateDescargaRuta() {
        return descargaRutas.get(0) != null;
    }

//    private boolean recaudarImporte() {
//        switch (Integer.parseInt(ruta.getIdMedioPago())) {
//            case 1: // Efectivo
//            case 2: // Tarjeta
//            case 3: // Tarjeta
//            case 4: // Tarjeta
//            case 5: // Tarjeta
//            case 6: // Tarjeta
//            case 7: // Tarjeta
//            case 8: // Tarjeta
//                return true;
//        }
//        return false;
//    }

    private boolean isCobrosPendientesGuias() {
        for (int i = 0; i < rutas.size(); i++) {
            if (CommonUtils.parseDouble(rutas.get(i).getImporte()) > 0) return true;
        }
        return false;
    }

    private boolean isRecoleccionLogisticaInversa() {
        return rutas.stream().anyMatch(ruta ->
                ruta.getTipoEnvio().equalsIgnoreCase(Ruta.TipoEnvio.LOGISTICA_INVERSA));
    }

    private boolean isRecoleccionValija() {
        return rutas.stream().anyMatch(ruta ->
                ruta.getTipoEnvio().equalsIgnoreCase(Ruta.TipoEnvio.VALIJA));
    }

    private boolean isRecoleccionSeller() {
        return rutas.stream().anyMatch(ruta ->
                ruta.getTipoEnvio().equalsIgnoreCase(Ruta.TipoEnvio.SELLER));
    }

    private boolean isRecoleccionCounter() {
        return rutas.stream().anyMatch(ruta ->
                ruta.getTipoEnvio().equalsIgnoreCase(Ruta.TipoEnvio.COUNTER));
    }

    private boolean isHabilitantes() {
        for (int i = 0; i < rutas.size(); i++) {
            if (!TextUtils.isEmpty(rutas.get(i).getHabilitantes())) return true;
        }
        return false;
    }

    private void updateProcesoDescarga(int procesoDescarga) {
        for (int i = 0; i < descargaRutas.size(); i++) {
            descargaRutas.get(i).setProcesoDescarga(procesoDescarga);
            descargaRutas.get(i).save();
        }
    }

    private void generateLLegadaRecoleccion() {
        Date date = new Date();
        String fecha = new SimpleDateFormat("dd/MM/yyyy").format(date);
        String hora = new SimpleDateFormat("HH:mm:ss").format(date);

        for (int i = 0; i < rutas.size(); i++) {
            GuiaGestionada guiaGestionada = new GuiaGestionada(
                    Preferences.getInstance().getString("idUsuario", ""),
                    rutas.get(i).getIdServicio(),
                    "181",
                    rutas.get(i).getTipoZona(),
                    rutas.get(i).getTipo(),
                    rutas.get(i).getLineaNegocio(),
                    fecha,
                    hora,
                    LocationUtils.getLatitude() + "",
                    LocationUtils.getLongitude() + "",
                    "", "", "", "", "",
                    GuiaGestionada.Recoleccion.LLEGADA_PUNTO_RECOJO + "",
                    "", "", "", "",
                    "0", "",
                    Data.Delete.NO,
                    rutas.get(i).getDataValidate(),
                    numVecesGestionado
            );
            guiaGestionada.save();
        }
    }

    private ArrayList<String> generateListaHabilitantes() {
        ArrayList<String> habilitantesList = new ArrayList<>();
        for (int i = 0; i < rutas.size(); i++) {
            if (!TextUtils.isEmpty(rutas.get(i).getHabilitantes())) {
                String[] habilitantes = rutas.get(i).getHabilitantes().split("-");

                for (int j = 0; j < habilitantes.length; j++) {
                    if (!habilitantesList.contains("● " + habilitantes[j])) {
                        habilitantesList.add("● " + habilitantes[j]);
                    }
                }
            }
        }
        return habilitantesList;
    }

}
