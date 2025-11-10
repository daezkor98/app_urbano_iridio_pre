package com.urbanoexpress.iridio3.pre.view;

import android.view.Menu;
import androidx.fragment.app.Fragment;

import java.util.List;

import com.urbanoexpress.iridio3.pre.ui.model.RutaItem;

/**
 * Created by mick on 22/06/16.
 */
//public interface RutaPendienteView<V extends ViewBinding> extends BaseV2View<V> {
public interface RutaPendienteView extends BaseV5View {

    Fragment getFragment();

    void showDatosRutasPendientes(List<RutaItem> rutasPendientes);
    void notifyItemChanged(int position);
    void notifyItemInsert(int position);
    void notifyItemRemove(int position);
    void notifyAllItemChanged();
    void scrollToPosition(int position);
    boolean isActiveActionMode();
    void showActionMode();
    void hideActionMode();
    void setTitleActionMode(String title);
    void clearAttachRecyclerView();
    void addAttachRecyclerView();
    void setVisibilitySwipeRefreshLayout(boolean visible);
    Menu getMenuActionMode();

    boolean isRefreshingSwipeRefreshLayout();

    void showMessageNuevaRutaAsignada();
    void showMessageNoHayRutaDisponible();
    void showMessageRutaNoIniciada();
    void showMessageRutaFinalizada();

    default void showAuthenticationError() {}


}
