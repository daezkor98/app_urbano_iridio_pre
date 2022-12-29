package com.urbanoexpress.iridio3.presenter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import com.urbanoexpress.iridio3.R;
import com.urbanoexpress.iridio3.model.entity.Data;
import com.urbanoexpress.iridio3.model.entity.Ruta;
import com.urbanoexpress.iridio3.model.interactor.GenerarManifiestoInteractor;
import com.urbanoexpress.iridio3.ui.interfaces.OnActionModeListener;
import com.urbanoexpress.iridio3.ui.interfaces.OnResultScannListener;
import com.urbanoexpress.iridio3.ui.model.CodigoBarraItem;
import com.urbanoexpress.iridio3.util.CommonUtils;
import com.urbanoexpress.iridio3.util.Preferences;
import com.urbanoexpress.iridio3.util.constant.LocalAction;
import com.urbanoexpress.iridio3.view.GenerarManifiestoView;

/**
 * Created by mick on 12/08/16.
 */

public class GenerarManifiestoPresenter implements OnResultScannListener, OnActionModeListener {

    private static final String TAG = GenerarManifiestoPresenter.class.getSimpleName();

    private GenerarManifiestoView view;
    private GenerarManifiestoInteractor generarManifiestoInteractor;
    private ArrayList<CodigoBarraItem> codigoBarraItemsSelected = new ArrayList<>();
    private List<CodigoBarraItem> codigoBarraItems = new ArrayList<>();
    private ArrayList<String> codigos = new ArrayList<>();

    public GenerarManifiestoPresenter(GenerarManifiestoView view) {
        this.view = view;
        this.generarManifiestoInteractor = new GenerarManifiestoInteractor(view.getViewContext());

        init();
    }

    private void init() {
        LocalBroadcastManager.getInstance(view.getViewContext())
                .registerReceiver(resultScannReceiver,
                        new IntentFilter(LocalAction.LOCAL_ACTION_BARCODE_SCAN_RESULT));
    }

    public void onClickGenerarManifiesto() {
        if (existGuiasEnLista()) {
            if (existGuias()) {
                if (validatePlaca()) {
//                    saveGuias();
                }
            }
        }
    }

    public void onClickItem(int position) {

    }

    public void onSelectedItem(int position) {
        setItemSelected(position);
        view.notifyItemChanged(position);
        showActionMode();
        view.setTitleActionMode(codigoBarraItemsSelected.size() + "");
    }

    public void onClickActionMode(String option) {
        switch (option) {
            case "seleccionar_todos":
                selectAllCodigoRutas();
                break;
            case "eliminar":
                deleteItems();
                break;
        }
    }

    @Override
    public void onResult(String value, String barcodeFormat) {
        if (!codigos.contains(value)) {
            CommonUtils.vibrateDevice(view.getViewContext(), 100);

            CodigoBarraItem item = new CodigoBarraItem(
                    value,
                    R.drawable.ic_barcode_black,
                    Color.parseColor("#00FFFFFF"),
                    false
            );
            codigoBarraItems.add(item);

            codigos.add(value);
            view.showCodigosBarra(codigoBarraItems);
            Log.d(TAG, codigos.toString());
        }
    }

    @Override
    public void onShowActionMode() {

    }

    @Override
    public void onCloseActionMode() {
        clearItemsSelected();
        view.notifyAllItemChanged();
    }
    
/*    private void saveGuias() {
        for (String guia: codigos) {
            Ruta ruta = new Ruta(
                    Preferences.getInstance().getString("idUsuario", ""),
                    guia,
                    "", // ID SERVICIO RECOLECCION
                    "", // MOT ID
                    "", // ID AGENCIA
                    "", // ID RUTA
                    "", // ID RUTA
                    "", // ID GUIA
                    "0", // ID MEDIO DE PAGO
                    "", // ID CLIENTE
                    "", // ID MANIFIESTO
                    "", // LINEA NEGOCIO
                    "", // SHIPPER CODIGO
                    "", // FECHA RUTA
                    0,
                    guia,
                    "E",
                    "", // SECUENCIA
                    "Dirección: No definido",
                    "", // GPS LATITUDE
                    "", // GPS LONGITUDE
                    "", // RADIO GPS
                    "Distrito: No definido",
                    "", // SHIPPER
                    "", // CENTRO DE ACTIVIDAD
                    "", // ESTADO SHIPPER
                    "", // CONTACTO
                    "0", // PIEZAS
                    "OO:OO", // HORARIO ENTREGA
                    0L, // HORARIO APROXIMADO
                    0L, // HORARIO ORDENAMIENTO
                    "", // TELEFONO CONTACTO GESTION
                    "", // NOMBRE TELEFONO CONTACTO GESTION
                    "", // TELEFONO
                    "", // CELULAR
                    "NO COD", // MEDIO DE PAGO
                    "0", // IMPORTE
                    "", // TIPO_ENVIO
                    "", // ANOTACIONES
                    "", // SERVICIO SMS
                    "", // HABILITANTES
                    "", // CHK ULTIMA GESTION
                    "", // GPS LATITUDE ULTIMA GESTION
                    "", // GPS LONGITUDE ULTIMA GESTION
                    "", // SOLICITA KILOMETRAJE
                    "", // REQUIERE DATOS CLIENTE
                    "", // GUIA REQUERIMIENTO
                    "", // GUIA REQUERIMIENTO CHK
                    "", // GUIA REQUERIMIENTO MOTIVO
                    "", // GUIA REQUERIMIENTO COMENTARIO
                    "", // GUIA REQUERIMIENTO HORARIO
                    "", // GUIA REQUERIMIENTO NUEVA DIRECCION
                    "", // PREMIOS GESTION GUIA
                    "", // FIRMA CLIENTE GESTION GUIA
                    "", // MINIMO FOTOS PRODUCTO GESTION GUIA
                    "", // DESCRIPCION
                    "", // OBSERVACIONES
                    "", // SECUENCIA RUTEO
                    0, // MOSTRAR ALERTA
                    Ruta.EstadoDescarga.PENDIENTE,
                    Ruta.ResultadoGestion.NO_DEFINIDO,
                    Data.Delete.NO,
                    Data.Validate.PENDING
            );
            ruta.save();
        }
    }*/

    private void deleteItems() {
        Log.d(TAG, "DELETE ITEMS");
        for (int i = 0; i < codigoBarraItemsSelected.size(); i++) {
            for (int j = 0; j < codigoBarraItems.size(); j++) {
                if (codigoBarraItemsSelected.get(i).getCodigo().equals(
                        codigoBarraItems.get(j).getCodigo()
                )) {
                    codigoBarraItems.remove(j);
                    view.notifyItemRemove(j);
                }
            }
        }
        codigoBarraItemsSelected.clear();
        codigos.clear();
        showActionMode();
    }

    private void setItemSelected(int position) {
        if (codigoBarraItems.get(position).isSelected()) {
            removeItemSelected(position);
            codigoBarraItems.get(position).setIcon(R.drawable.ic_barcode_black);
            codigoBarraItems.get(position).setBackgroundColor(Color.parseColor("#00FFFFFF"));
            codigoBarraItems.get(position).setSelected(false);
        } else {
            codigoBarraItemsSelected.add(codigoBarraItems.get(position));
            codigoBarraItems.get(position).setIcon(R.drawable.ic_checkbox_marked_circle);
            codigoBarraItems.get(position).setBackgroundColor(Color.parseColor("#cccccc"));
            codigoBarraItems.get(position).setSelected(true);
        }
    }

    private void selectAllCodigoRutas() {
        selectAllItems();
        view.notifyAllItemChanged();
        showActionMode();
        view.setTitleActionMode(codigoBarraItemsSelected.size() + "");
    }

    private void selectAllItems() {
        if (isSelectedAllItems()) {
            clearItemsSelected();
        } else {
            codigoBarraItemsSelected.clear();
            for (int i = 0; i < codigoBarraItems.size(); i++) {
                codigoBarraItemsSelected.add(codigoBarraItems.get(i));
                codigoBarraItems.get(i).setIcon(R.drawable.ic_checkbox_marked_circle);
                codigoBarraItems.get(i).setBackgroundColor(Color.parseColor("#cccccc"));
                codigoBarraItems.get(i).setSelected(true);
            }
        }
    }

    private boolean isSelectedAllItems() {
        for (int i = 0; i < codigoBarraItems.size(); i++) {
            if (!codigoBarraItems.get(i).isSelected()) {
                return false;
            }
        }
        return true;
    }

    private void removeItemSelected(int position) {
        for (int i = 0; i < codigoBarraItemsSelected.size(); i++) {
            if (codigoBarraItemsSelected.get(i).getCodigo().equals(codigoBarraItems.get(position).getCodigo())) {
                codigoBarraItemsSelected.remove(i);
                break;
            }
        }
    }

    private void showActionMode() {
        if (codigoBarraItemsSelected.size() > 0) {
            view.showActionMode();
        } else {
            view.hideActionMode();
        }
    }

    private void clearItemsSelected() {
        for (int i = 0; i < codigoBarraItems.size(); i++) {
            codigoBarraItems.get(i).setIcon(R.drawable.ic_barcode_black);
            codigoBarraItems.get(i).setBackgroundColor(Color.parseColor("#00FFFFFF"));
            codigoBarraItems.get(i).setSelected(false);
        }
        codigoBarraItemsSelected.clear();
    }

    private boolean validatePlaca() {
        if (view.getViewTxtPlaca().getText().toString().isEmpty()) {
            view.getViewTxtPlaca().setError("Ingrese el número de placa.");
            return false;
        }
        return true;
    }

    private boolean existGuiasEnLista() {
        if (codigoBarraItems.size() == 0) {
            view.showToast(R.string.activity_generar_man_no_hay_guias);
            return false;
        }
        return true;
    }

    private boolean existGuias() {
        List<Ruta> rutas = generarManifiestoInteractor.selectAllRutaPendiente();
        int guiasExistentes = 0;

        for (int i = 0; i < rutas.size(); i++) {
            for (int j = 0; j < codigos.size(); j++) {
                if (rutas.get(i).getGuia().contains(codigos.get(j))) {
                    guiasExistentes++;
                }
            }
        }

        if (guiasExistentes > 0) {
            view.showToast(R.string.activity_generar_man_existe_guias_generadas);
            return false;
        }
        return true;
    }

    /**
     * Broadcast
     *
     * {@link CodeScannerImpl#sendOnResultScannListener}
     */
    private final BroadcastReceiver resultScannReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            onResult(intent.getStringExtra("value"), intent.getStringExtra("barcodeFormat"));
        }
    };

}
