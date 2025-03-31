package com.urbanoexpress.iridio3.pe.ui

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.urbanoexpress.iridio3.pe.R

class PlanRutaActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plan_ruta)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setupToolbar(toolbar)
        setScreenTitle(R.string.title_activity_plan_ruta)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.planRutaFragmentContainerView)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}