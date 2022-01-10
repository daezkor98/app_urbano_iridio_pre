package com.urbanoexpress.iridio.util;

import android.content.Context;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.util.AttributeSet;

/**
 * Created by mick on 13/12/16.
 */

public class FabManualHideBehavior extends FloatingActionButton.Behavior {

    public FabManualHideBehavior() {
        super();
        setAutoHideEnabled(false);
    }

    public FabManualHideBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        setAutoHideEnabled(false);
    }

}