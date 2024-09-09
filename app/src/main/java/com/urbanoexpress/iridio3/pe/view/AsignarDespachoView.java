package com.urbanoexpress.iridio3.pe.view;

import com.urbanoexpress.iridio3.pe.ui.model.AsignarDespachoItem;

import java.util.List;

/**
 * Created by mick on 30/11/16.
 */

public interface AsignarDespachoView extends BaseV5View {

    void showDespachos(List<AsignarDespachoItem> despachos);
    void notifyItemChanged(int position);
    void notifyItemInsert(int position);
    void notifyItemRemove(int position);
    void notifyAllItemChanged();
    void setMensaje(String mensaje);
    void setVisibilityMensaje(int visibilityMensaje);
    void dismiss();

}