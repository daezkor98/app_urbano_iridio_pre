package com.urbanoexpress.iridio.view;

import java.util.List;

import com.urbanoexpress.iridio.ui.model.PaisItem;

/**
 * Created by mick on 24/08/16.
 */

public interface ConfigPaisView extends BaseView {

    void showPaises(List<PaisItem> paisItems);
    void notifyItemChanged(int position);
    void notifyItemInsert(int position);
    void notifyItemRemove(int position);
    void notifyAllItemChanged();

}
