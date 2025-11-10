package com.urbanoexpress.iridio3.pre.view;

import android.view.Menu;

import com.urbanoexpress.iridio3.pre.model.entity.Ruta;
import com.urbanoexpress.iridio3.pre.ui.model.TelefonoGuiaItem;

import java.util.ArrayList;

/**
 * Created by mick on 09/07/16.
 */
public interface DetalleRutaView extends BaseV5View {

    void showDetalleRuta(Ruta detalleRuta);
    void showTelefonos(ArrayList<TelefonoGuiaItem> telefonosItems);
    void showCelulares(ArrayList<TelefonoGuiaItem> celularesItems);
    void showTelContactoGestion(ArrayList<TelefonoGuiaItem> telefonosItems);
    void showHabilitantes(ArrayList<String> habilitantes);
    void showAlertaEstadoShipper(int idResIcon, int bg, String msg);
    void setLblColorHorario(int color);
    void setVisibilityFabDescarga(int visibility);
    void setVisibilityFabDevolver(int visibility);
    Menu getMenuToolbar();
}