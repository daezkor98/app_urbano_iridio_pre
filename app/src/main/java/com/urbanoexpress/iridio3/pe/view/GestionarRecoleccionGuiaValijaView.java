package com.urbanoexpress.iridio3.pe.view;

import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.urbanoexpress.iridio3.pe.ui.model.GaleriaDescargaRutaItem;

import java.util.List;

public interface GestionarRecoleccionGuiaValijaView extends BaseView {

    void setGuiaElectronica(String guiaElectronica);
    void showGaleria(List<GaleriaDescargaRutaItem> galeria);
    void notifyGaleryItemChanged(int position);
    void notifyGaleryItemInsert(int position);
    void notifyGaleryItemRemove(int position);
    void notifyGaleryAllItemChanged();
    EditText getViewTxtFrmGuiaRecoleccion();
    EditText getViewTxtFrmSobre();
    EditText getViewTxtFrmValija();
    EditText getViewTxtFrmPaquete();
    EditText getViewTxtFrmOtros();
    EditText getViewTxtComentarios();

    Fragment getFragment();

}