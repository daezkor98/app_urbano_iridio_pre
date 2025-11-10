package com.urbanoexpress.iridio3.pre.view;

import com.urbanoexpress.iridio3.pre.ui.model.RutaItem;

import java.util.List;

/**
 * Created by mick on 12/06/17.
 */

public interface TransferirGuiaView extends BaseView2 {
    void showGuias(List<RutaItem> guias);
    void notifyItemChanged(int position);
    void notifyItemInsert(int position);
    void notifyItemRemove(int position);
    void notifyAllItemChanged();
    void showActionMode();
    void hideActionMode();
    void setTitleActionMode(String title);

    void navigateToFiltrarGuiaActivity(boolean[] checkedFiltros);
    void navigateToTransferirGuiaDialog(String[] guias, String idZona, String lineaNegocio);
}
