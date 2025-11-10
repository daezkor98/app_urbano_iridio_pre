package com.urbanoexpress.iridio3.pre.view;

import androidx.fragment.app.Fragment;

import java.util.List;

import com.urbanoexpress.iridio3.pre.ui.adapter.model.GalleryWrapperItem;
import com.urbanoexpress.iridio3.pre.ui.model.MotivoDescargaItem;

/**
 * Created by mick on 25/07/16.
 */
public interface DescargaNoEntregaView extends BaseV5View {

    void setScreenTitle(String title);

    void setTextGuiaElectronica(String text);
    void setTextBtnSiguiente(String text);

    String getTextComentarios();

    void showListaMotivos(List<MotivoDescargaItem> motivos);
    void showGaleria(List<GalleryWrapperItem> items);

    void showStepMotivos();
    void showStepGaleria();

    void hideStepMotivos();
    void hideStepGaleria();

    void notifyGalleryItemChanged(int position);
    void notifyGalleryItemInsert(int position);
    void notifyGalleryItemRemove(int position);
    void notifyGalleryAllItemChanged();
    void notifyMotivosAllItemChanged();

    Fragment getFragment();

    void dismiss();

    void hideKeyboard();
    void showKeyboard();

    void showDialogEstadoShipper();
    void showDialogUltimaGestionEfectiva(String latitude, String longitude);

    void showMessageCantTakePhoto();
    void showWrongDateAndTimeMessage();
}