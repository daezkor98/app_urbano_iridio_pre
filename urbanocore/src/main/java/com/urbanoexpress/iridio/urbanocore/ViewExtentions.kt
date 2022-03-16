package com.urbanoexpress.iridio.urbanocore

import android.view.View


/**
 * Created by Brandon Quintanilla on February/28/2022.
 */
fun View.onExclusiveClick(action: (v: View) -> Unit) {
    setOnClickListener { v ->
        isEnabled = false
        action(v)
        postDelayed({ isEnabled = true }, 900)
    }
}
