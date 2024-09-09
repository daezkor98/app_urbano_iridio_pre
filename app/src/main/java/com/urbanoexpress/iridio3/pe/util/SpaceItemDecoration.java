package com.urbanoexpress.iridio3.pe.util;

import android.graphics.Rect;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

/**
 * Created by mick on 14/11/16.
 */

public class SpaceItemDecoration extends RecyclerView.ItemDecoration {

    private final int topSpaceHeight;
    private final int bottomSpaceHeight;
    private final int leftSpaceHeight;
    private final int rightSpaceHeight;

    public SpaceItemDecoration(int allSpace) {
        this.topSpaceHeight = allSpace;
        this.bottomSpaceHeight = allSpace;
        this.leftSpaceHeight = allSpace;
        this.rightSpaceHeight = allSpace;
    }

    public SpaceItemDecoration(int topSpaceHeight,
                               int bottomSpaceHeight,
                               int leftSpaceHeight,
                               int rightSpaceHeight) {
        this.topSpaceHeight = topSpaceHeight;
        this.bottomSpaceHeight = bottomSpaceHeight;
        this.leftSpaceHeight = leftSpaceHeight;
        this.rightSpaceHeight = rightSpaceHeight;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        outRect.top = topSpaceHeight;
        outRect.left = leftSpaceHeight;
        outRect.right = rightSpaceHeight;

        if (parent.getChildAdapterPosition(view) == parent.getAdapter().getItemCount() - 1) {
            outRect.bottom = bottomSpaceHeight;
        }
    }
}
