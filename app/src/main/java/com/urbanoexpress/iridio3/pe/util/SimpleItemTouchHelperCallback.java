package com.urbanoexpress.iridio3.pe.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.urbanoexpress.iridio3.R;
import com.urbanoexpress.iridio3.pe.util.androidsdkfixs.itemtouchelper.ItemTouchHelper;

/**
 * Created by mick on 22/06/16.
 */
public class SimpleItemTouchHelperCallback extends ItemTouchHelper.Callback {

    private static final String TAG = SimpleItemTouchHelperCallback.class.getSimpleName();

    private final OnTouchItemRutasListener listenerAdapter;
    private final OnTouchItemRutasListener listener;

    private Context context;

    public SimpleItemTouchHelperCallback(Context context,
                                         OnTouchItemRutasListener listenerAdapter,
                                         OnTouchItemRutasListener listener) {
        this.context = context;
        this.listenerAdapter = listenerAdapter;
        this.listener = listener;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
//        int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
//        int swipeFlags = ItemTouchHelper.START;
        int swipeFlags = 0; // Desactivar funcionalidad de eliminar guia
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                          RecyclerView.ViewHolder target) {
        if (!viewHolder.itemView.isSelected()) {
            listener.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
            listenerAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        }
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
//        if (!viewHolder.itemView.isSelected()) {
//            int positionDelete = viewHolder.getAdapterPosition();
//            listenerAdapter.onItemDismiss(positionDelete);
//            listener.onItemDismiss(positionDelete);
//        }
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (!viewHolder.itemView.isSelected()) {
            final float ALPHA_FULL = 1.0f;

            if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                // Get RecyclerView item from the ViewHolder
                View itemView = viewHolder.itemView;

                Paint p = new Paint();
                Bitmap icon;

                if (dX > 0) {
                    icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_delete_white);

                /* Set your color for positive displacement */
                    p.setARGB(255, 233, 62, 70);

                    // Draw Rect with varying right side, equal to displacement dX
                    c.drawRect((float) itemView.getLeft(), (float) itemView.getTop(), dX,
                            (float) itemView.getBottom(), p);

                    // Set the image icon for Right swipe
                    c.drawBitmap(icon,
                            (float) itemView.getLeft() + MetricsUtils.dpToPx(context, 16),
                            (float) itemView.getTop() + ((float) itemView.getBottom() - (float) itemView.getTop() - icon.getHeight())/2,
                            p);
                } else {
                    icon = BitmapFactory.decodeResource(
                            context.getResources(), R.drawable.ic_delete_white);

                    icon = Bitmap.createScaledBitmap(icon, MetricsUtils.dpToPx(context, 35), MetricsUtils.dpToPx(context, 35), false);

                /* Set your color for negative displacement */
                    p.setARGB(255, 232, 50, 58);

                    // Draw Rect with varying left side, equal to the item's right side
                    // plus negative displacement dX
                    c.drawRect((float) itemView.getRight() + dX, (float) itemView.getTop(),
                            (float) itemView.getRight(), (float) itemView.getBottom(), p);

                    //Set the image icon for Left swipe
                    c.drawBitmap(icon,
                            (float) itemView.getRight() - MetricsUtils.dpToPx(context, 16) - icon.getWidth(),
                            (float) itemView.getTop() + ((float) itemView.getBottom() - (float) itemView.getTop() - icon.getHeight())/2,
                            p);
                }

                // Fade out the view as it is swiped out of the parent's bounds
                final float alpha = ALPHA_FULL - Math.abs(dX) / (float) viewHolder.itemView.getWidth();
                viewHolder.itemView.setAlpha(alpha);
                viewHolder.itemView.setTranslationX(dX);
            } else {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        Log.d(TAG, "ON SELECTED CHANGED");
        listener.onItemSelectChanged(viewHolder, actionState);
        super.onSelectedChanged(viewHolder, actionState);
    }
}
