package com.urbanoexpress.iridio.ui

import android.os.Bundle
import com.urbanoexpress.iridio.databinding.ActivityMisGananciasBinding

class MisGananciasActivity : BaseActivity2() {

    lateinit var bind: ActivityMisGananciasBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bind = ActivityMisGananciasBinding.inflate(layoutInflater)
        setContentView(bind.root)

        setupToolbar(bind.toolbar)
    }
}