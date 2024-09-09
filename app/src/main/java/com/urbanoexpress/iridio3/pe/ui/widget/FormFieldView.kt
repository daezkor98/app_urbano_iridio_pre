package com.urbanoexpress.iridio3.pe.ui.widget

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.urbanoexpress.iridio3.pe.R
import com.urbanoexpress.iridio3.urbanocore.extentions.onExclusiveClick


/**
 * Utility Widget used to normalize form views and behavior
 */
class FormFieldView(ctx: Context, attrs: AttributeSet) : ConstraintLayout(ctx, attrs) {

    var tf_containter: TextInputLayout? = null
    var et_field: TextInputEditText? = null
    var click_field: View? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.form_field_view, this, true)
        initViews()
        userAttrs(ctx, attrs)
    }

    private fun initViews() {
        tf_containter = findViewById(R.id.tf_container)
        et_field = findViewById(R.id.et_field)
        click_field = findViewById(R.id.click_field)
    }

    private fun userAttrs(ctx: Context, attrs: AttributeSet) {
        val a: TypedArray = ctx.obtainStyledAttributes(attrs, R.styleable.FormFieldView, 0, 0)
        if (a.hasValue(R.styleable.FormFieldView_headerHint)) {
            tf_containter?.hint = a.getText(R.styleable.FormFieldView_headerHint)
        }
        a.recycle()
    }

    /**Utilities*********************/

    fun setText(text: String) {
        et_field?.setText(text)
    }

    fun text(): String {
        return et_field?.text.toString()
    }

    fun inputError(error: String?) {
        this.tf_containter?.error = error
    }
}

/**
 * Used when the form field needs to be clickeable and not editable
 * For example, when it is used as date field
 * */
fun FormFieldView.enableClickMode(withText: String? = null, action: (v: View) -> Unit) {
    et_field?.isEnabled = false
    click_field?.visibility = View.VISIBLE
    click_field?.onExclusiveClick(action)
    withText?.let {
        setText(it)
    }
}