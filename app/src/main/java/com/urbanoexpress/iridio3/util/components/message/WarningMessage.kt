package com.urbanoexpress.iridio3.util.components.message

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.urbanoexpress.iridio3.R
import com.urbanoexpress.iridio3.databinding.ComponentWarningMessageBinding

/**
 * Simple Warning message placeHolder - With top line separator
 */
class WarningMessage @JvmOverloads constructor(ctx: Context, attrs: AttributeSet) :
    FrameLayout(ctx, attrs) {

    lateinit var bind: ComponentWarningMessageBinding

    init {
        initView()
        useAttrs(ctx, attrs)
    }

    private fun initView() {
        bind = ComponentWarningMessageBinding.inflate(LayoutInflater.from(context), this, true)
    }


    private fun useAttrs(ctx: Context, attrs: AttributeSet) {
        val a: TypedArray = ctx.obtainStyledAttributes(attrs, R.styleable.WarningMessage)

        if (a.hasValue(R.styleable.WarningMessage_warningText)) {
            this.bind.tvWarningMessage.text = a.getText(R.styleable.WarningMessage_warningText)
        }

        a.recycle()
    }
}