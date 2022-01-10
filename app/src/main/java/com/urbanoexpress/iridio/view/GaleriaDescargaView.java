package com.urbanoexpress.iridio.view;

import androidx.fragment.app.Fragment;

import java.util.List;

import com.urbanoexpress.iridio.ui.model.GaleriaDescargaRutaItem;

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
