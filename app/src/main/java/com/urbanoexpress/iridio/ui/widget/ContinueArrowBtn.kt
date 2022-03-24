package com.urbanoexpress.iridio.ui.widget

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import com.urbanoexpress.iridio.R

class ContinueArrowBtn(ctx: Context, attrs: AttributeSet) : LinearLayoutCompat(ctx, attrs) {

    init {
        LayoutInflater.from(context).inflate(R.layout.continue_arrow_btn, this, true)
        val a: TypedArray = ctx.obtainStyledAttributes(attrs, R.styleable.ContinueArrowBtn, 0, 0)

        if (a.hasValue(R.styleable.ContinueArrowBtn_text)) {
            findViewById<TextView>(R.id.tv_text).text = a.getText(R.styleable.ContinueArrowBtn_text)
        }

        val textSizeDP = a.getInt(R.styleable.ContinueArrowBtn_textSizeDp, 15).toFloat()
        findViewById<TextView>(R.id.tv_text).setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSizeDP)

        a.recycle()
    }

}