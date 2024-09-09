package com.urbanoexpress.iridio3.pe.view;

import android.view.Menu;

import com.urbanoexpress.iridio3.pe.ui.model.AuditoriaItem;

import java.util.List;

public interface RutaAuditoriaPendienteView extends BaseView {

    void showDatosAuditoriasPendientes(List<AuditoriaItem> auditoriasPendientes);
    void notifyItemChanged(int position);
    void notifyItemInsert(int position);
    void notifyItemRemove(int position);
    void notifyAllItemChanged();
    void showActionMode();
    void hideActionMode();
    void setTitleActionMode(String title);
    void clearAttachRecyclerView();
    void addAttachRecyclerView();
    void setVisibilitySwipeRefreshLayout(boolean visible);
    Menu getMenuActionMode();

}