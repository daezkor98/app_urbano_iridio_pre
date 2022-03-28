package com.urbanoexpress.iridio3.view;

import android.widget.EditText;

import java.util.List;

import com.urbanoexpress.iridio3.ui.model.CodigoBarraItem;

/**
 * Created by mick on 12/08/16.
 */

public interface GenerarManifiestoView extends BaseView2 {

    void showCodigosBarra(List<CodigoBarraItem> codigosBarra);
    void notifyItemChanged(int position);
    void notifyItemInsert(int position);
    void notifyItemRemove(int position);
    void notifyAllItemChanged();
    void showActionMode();
    void hideActionMode();
    void setTitleActionMode(String title);

    EditText getViewTxtPlaca();

}
