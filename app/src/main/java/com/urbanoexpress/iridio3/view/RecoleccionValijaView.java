package com.urbanoexpress.iridio3.view;

import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.urbanoexpress.iridio3.ui.model.GaleriaDescargaRutaItem;
import com.urbanoexpress.iridio3.ui.model.MotivoDescargaItem;

import java.util.List;

public interface RecoleccionValijaView extends BaseView {

    void setGuiaElectronica(String guiaElectronica);
    void showListaMotivos(List<MotivoDescargaItem> motivos);
    void showGaleria(List<GaleriaDescargaRutaItem> galeria);
    void notifyGaleryItemChanged(int position);
    void notifyGaleryItemInsert(int position);
    void notifyGaleryItemRemove(int position);
    void notifyGaleryAllItemChanged();
    void notifyMotivosAllItemChanged();
    EditText getViewTxtFrmGuiaRecoleccion();
    EditText getViewTxtFrmSobre();
    EditText getViewTxtFrmValija();
    EditText getViewTxtFrmPaquete();
    EditText getViewTxtFrmOtros();
    EditText getViewTxtComentarios();
    Fragment getFragment();

}