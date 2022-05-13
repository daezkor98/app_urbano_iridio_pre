package com.urbanoexpress.iridio3.urbanocore.extentions

import android.view.View
import android.widget.TextView


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

/**Asserts the visibility before set text
 * */
fun TextView.assertText(text: String) {
    this.visibility = View.VISIBLE
    this.text = text
}

/** Sets Visibility on GONE
 * */
fun View.gone() {
    this.visibility = View.GONE
}

fun View.visisble() {
    this.visibility = View.VISIBLE
}

fun View.goneIf(condition: () -> Boolean) {
    if (condition()) {
        this.visibility = View.GONE
    }
}

fun View.disable(){
    this.isEnabled =false
}