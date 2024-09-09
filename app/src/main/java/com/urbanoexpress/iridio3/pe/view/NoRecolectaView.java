package com.urbanoexpress.iridio3.pe.view;

import androidx.fragment.app.Fragment;
import android.widget.EditText;

import java.util.List;

import com.urbanoexpress.iridio3.pe.ui.model.GaleriaDescargaRutaItem;
import com.urbanoexpress.iridio3.pe.ui.model.MotivoDescargaItem;

/**
 * Created by mick on 18/08/16.
 */

public interface NoRecolectaView extends BaseView {

    void setGuiaElectronica(String guiaElectronica);
    void showListaMotivos(List<MotivoDescargaItem> motivos);
    void showGaleria(List<GaleriaDescargaRutaItem> galeria);
    void notifyGaleryItemChanged(int position);
    void notifyGaleryItemInsert(int position);
    void notifyGaleryItemRemove(int position);
    void notifyGaleryAllItemChanged();
    void notifyMotivosAllItemChanged();
    EditText getViewTxtComentarios();

    Fragment getFragment();

}