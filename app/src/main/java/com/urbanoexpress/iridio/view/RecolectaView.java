package com.urbanoexpress.iridio.view;

import androidx.fragment.app.Fragment;
import android.widget.EditText;

import java.util.List;

import com.urbanoexpress.iridio.ui.model.GaleriaDescargaRutaItem;
import com.urbanoexpress.iridio.ui.model.MotivoDescargaItem;

/**
 * Created by mick on 18/08/16.
 */

public interface RecolectaView extends BaseView {

    void setGuiaElectronica(String guiaElectronica);
    void showListaMotivos(List<MotivoDescargaItem> motivos);
    void showGaleria(List<GaleriaDescargaRutaItem> galeria);
    void setVisibilityLayoutInputGuia(int visible);
    void notifyGaleryItemChanged(int position);
    void notifyGaleryItemInsert(int position);
    void notifyGaleryItemRemove(int position);
    void notifyGaleryAllItemChanged();
    void notifyMotivosAllItemChanged();
    EditText getViewTxtGuiaManual();
    EditText getViewTxtFrmGuiaRecoleccion();
    EditText getViewTxtFrmSobre();
    EditText getViewTxtFrmValija();
    EditText getViewTxtFrmPaquete();
    EditText getViewTxtFrmOtros();
    EditText getViewTxtComentarios();
    Fragment getFragment();

}
