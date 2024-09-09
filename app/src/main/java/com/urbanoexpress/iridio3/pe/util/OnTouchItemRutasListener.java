package com.urbanoexpress.iridio3.pe.util;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

/**
 * Created by mick on 22/06/16.
 */
public interface OnTouchItemRutasListener {
    boolean onItemMove(int fromPosition, int toPosition);
    void onItemDismiss(int position);
    void onItemSelect(View view, int position, boolean isSelected);
    void onItemSelectChanged(RecyclerView.ViewHolder view, int actionState);
}
