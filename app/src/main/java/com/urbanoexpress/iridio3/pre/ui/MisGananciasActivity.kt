package com.urbanoexpress.iridio3.pre.ui

import android.os.Bundle
import com.urbanoexpress.iridio3.pre.databinding.ActivityMisGananciasBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MisGananciasActivity : BaseActivity2() {

    val bind: ActivityMisGananciasBinding by lazy {
        ActivityMisGananciasBinding.inflate(
            layoutInflater
        )
    }
//    lateinit var bind: ActivityMisGananciasBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        bind = ActivityMisGananciasBinding.inflate(layoutInflater)
        setContentView(bind.root)

        setupToolbar(bind.toolbar)
    }
}