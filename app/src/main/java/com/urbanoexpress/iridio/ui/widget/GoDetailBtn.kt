package com.urbanoexpress.iridio.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.appcompat.widget.LinearLayoutCompat
import com.urbanoexpress.iridio.R

class GoDetailBtn(context: Context, attrs: AttributeSet) : LinearLayoutCompat(context, attrs) {

    init {
        LayoutInflater.from(context).inflate(R.layout.go_detail_btn, this, true)
    }

}