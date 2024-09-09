package com.urbanoexpress.iridio3.pe.util.components;

import android.content.Context;
import com.google.android.material.appbar.AppBarLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import android.util.AttributeSet;

/**
 * Created by mick on 11/07/16.
 */
public class CustomAppBarLayout extends AppBarLayout
    implements AppBarLayout.OnOffsetChangedListener {

        private State state;
        private OnStateChangeListener onStateChangeListener;

        public CustomAppBarLayout(Context context) {
            super(context);
        }

        public CustomAppBarLayout(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            if (!(getLayoutParams() instanceof CoordinatorLayout.LayoutParams)
                    || !(getParent() instanceof CoordinatorLayout)) {
                throw new IllegalStateException(
                        "CustomAppBarLayout must be a direct child of CoordinatorLayout.");
            }
            addOnOffsetChangedListener(this);
        }

        @Override
        public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
            if (verticalOffset == 0) {
                if (onStateChangeListener != null && state != State.EXPANDED) {
                    onStateChangeListener.onStateChange(State.EXPANDED);
                }
                state = State.EXPANDED;
            } else if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()) {
                if (onStateChangeListener != null && state != State.COLLAPSED) {
                    onStateChangeListener.onStateChange(State.COLLAPSED);
                }
                state = State.COLLAPSED;
            } else {
                if (onStateChangeListener != null && state != State.IDLE) {
                    onStateChangeListener.onStateChange(State.IDLE);
                }
                state = State.IDLE;
            }
        }

    public void setOnStateChangeListener(OnStateChangeListener listener) {
        this.onStateChangeListener = listener;
    }

    public interface OnStateChangeListener {
        void onStateChange(State toolbarChange);
    }

    public enum State {
        COLLAPSED,
        EXPANDED,
        IDLE
    }
}
