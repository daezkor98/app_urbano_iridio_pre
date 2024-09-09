package com.urbanoexpress.iridio3.pe.view;

import androidx.fragment.app.Fragment;

import com.urbanoexpress.iridio3.pe.ui.adapter.model.GalleryWrapperItem;

import java.util.List;

public interface RecoleccionLogisticaInversaView extends BaseV5View {

    void setTextGuiaElectronica(String text);
    void setTextFormSobre(String text);
    void setTextFormValija(String text);
    void setTextFormPaquete(String text);
    void setTextFormOtros(String text);
    void setTextBtnSiguiente(String text);

    String getTextFormSobre();
    String getTextFormValija();
    String getTextFormPaquete();
    String getTextFormOtros();
    String getTextComentarios();

    void showFotosEnGaleria(List<GalleryWrapperItem> items);
    void showFotosDomicilioEnGaleria(List<GalleryWrapperItem> items);

    void showStepFormulario();
    void showStepFotosProducto();
    void showStepFotosDomicilio();

    void hideStepFormulario();
    void hideStepFotosProducto();
    void hideStepFotosDomicilio();

    void notifyGaleriaFotosItemRemove(int position);
    void notifyGaleriaFotosAllItemChanged();

    void notifyGaleriaDomicilioItemRemove(int position);
    void notifyGaleriaDomicilioAllItemChanged();

    void setBackgroundBtnMinusPaquetes(int color);
    void setBackgroundBtnPlusPaquetes(int color);

    void setBackgroundBtnMinusSobres(int color);
    void setBackgroundBtnPlusSobres(int color);

    void setBackgroundBtnMinusValijas(int color);
    void setBackgroundBtnPlusValijas(int color);

    void setBackgroundBtnMinusOtros(int color);
    void setBackgroundBtnPlusOtros(int color);

    Fragment getFragment();

    void dismiss();

    void hideKeyboard();
    void showKeyboard();

    void showMessageCantTakePhoto();
    void showWrongDateAndTimeMessage();
}