package com.urbanoexpress.iridio3.pe.view;

import com.urbanoexpress.iridio3.pe.ui.model.PiezaItem;

import java.util.List;

public interface ManifestarGuiaView extends BaseView2 {

    void showPiezas(List<PiezaItem> items);
    void notifyItemChanged(int position);
    void notifyAllItemChanged();

    void setVisibilityTopMessage(boolean visible);
    void setVisibilityAddToRouteButton(boolean visible);

    void finishActivity();
}
