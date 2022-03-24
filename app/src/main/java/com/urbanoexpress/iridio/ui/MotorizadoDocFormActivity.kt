package com.urbanoexpress.iridio.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.urbanoexpress.iridio.R
import com.urbanoexpress.iridio.databinding.ActivityMotorizadoDocFormBinding
import com.urbanoexpress.iridio.ui.dialogs.FileTypePickerDialog
import com.urbanoexpress.iridio.urbanocore.onExclusiveClick

/*
* Formulario para registro de Licencia de conducir
* */
class MotorizadoDocFormActivity : AppCompatActivity() {

    lateinit var bind: ActivityMotorizadoDocFormBinding

    companion object {
        fun show(from: Activity) {
            val i = Intent(from, MotorizadoDocFormActivity::class.java)
            from.startActivity(i)
            from.overridePendingTransition(R.anim.slide_enter_from_bottom, R.anim.not_slide)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bind = ActivityMotorizadoDocFormBinding.inflate(layoutInflater)
        //setContentView(R.layout.activity_motorizado_doc_form)
        //       bind.btnAddFile.//TODO
        bind.btnAddFile.onExclusiveClick {
            showFilePickerDialog()
        }
        setContentView(bind.root)
    }

    fun showFilePickerDialog() {
        val dialog = FileTypePickerDialog.Builder().pdfRbVisibility(View.GONE).build()
        dialog.show(supportFragmentManager, "FileTypePi")
    }
}