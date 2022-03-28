package com.urbanoexpress.iridio3.view;

import androidx.fragment.app.Fragment;

import java.util.List;

import com.urbanoexpress.iridio3.ui.model.GaleriaDescargaRutaItem;

/**
 * Created by mick on 08/08/16.
 */
public interface GaleriaDescargaView extends BaseView {

    void showGaleria(List<GaleriaDescargaRutaItem> galeria);
    void notifyGaleryItemChanged(int position);
    void notifyGaleryItemInsert(int position);
    void notifyGaleryItemRemove(int position);
    void notifyGaleryAllItemChanged();

    Fragment getFragment();

}
