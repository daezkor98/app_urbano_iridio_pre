package com.urbanoexpress.iridio3.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.urbanoexpress.iridio3.R
import com.urbanoexpress.iridio3.databinding.ActivityMotorizadoDocFormBinding

//import com.urbanoexpress.iridio3.databinding.ActivityMotorizadoDocFormBinding
import com.urbanoexpress.iridio3.presenter.viewmodel.LicenciaFormViewModel
import com.urbanoexpress.iridio3.ui.dialogs.DATE_PICKER_MODE
import com.urbanoexpress.iridio3.ui.dialogs.DatePickerDailogFragment
import com.urbanoexpress.iridio3.ui.dialogs.FileTypePickerDialog
import com.urbanoexpress.iridio3.ui.dialogs.MessageDialog
import com.urbanoexpress.iridio3.ui.helpers.ModalHelper
import com.urbanoexpress.iridio3.ui.widget.enableClickMode
import com.urbanoexpress.iridio3.urbanocore.onExclusiveClick
import com.urbanoexpress.iridio3.util.Preferences

/*
* Formulario para registro de Licencia de conducir
* */
class MotorizadoDocFormActivity : BaseActivity2() {

    lateinit var bind: ActivityMotorizadoDocFormBinding

    val licenciaMotorizadoVM = LicenciaFormViewModel()

    companion object {
        fun show(from: Activity) {
            val i = Intent(from, MotorizadoDocFormActivity::class.java)
            from.startActivity(i)
            from.overridePendingTransition(R.anim.slide_enter_from_bottom, R.anim.not_slide)
        }
    }

    private fun loading(isloading: Boolean) {
        bind.progressLayout.progressLayout.visibility = if (isloading) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityMotorizadoDocFormBinding.inflate(layoutInflater)

        setContentView(bind.root)

        setupToolbar(bind.toolbar)
        setScreenTitle(R.string.motorizado_encuest)

        configUI()

        licenciaMotorizadoVM.isLoadingLD.observe(this) {
            loading(it)
        }

        licenciaMotorizadoVM.exceptionLD.observe(this) {
            ModalHelper.showToast(this, it.message, Toast.LENGTH_LONG)
        }

        licenciaMotorizadoVM.onRegisterSuccess.observe(this) {
            val messageDialog = MessageDialog.newInstance("Información registrada")
            messageDialog.completion = {
                finish()
                Preferences
                    .getInstance()
                    .edit()
                    .putString("mostrarEncuesta", "0")//Should not request again
                    .apply()
            }
            messageDialog.show(supportFragmentManager, "messageD")
        }
    }

    private fun configUI() {

        bind.fieldLicenciaEmit.setText("00/00/0000")
        bind.fieldLicenciaEmit.enableClickMode {
            val newFragment = DatePickerDailogFragment.newInstance(DATE_PICKER_MODE.SPINNER)
            newFragment.dateListener = DatePickerDailogFragment
                .OnDatePickerDailogFragmentListener { view, year, month, dayOfMonth ->
                    bind.fieldLicenciaEmit.setText("$dayOfMonth/$month/$year")
                }
            newFragment.show(supportFragmentManager, "datePicker")
        }

        bind.fieldLicenciaExp.setText("00/00/0000")
        bind.fieldLicenciaExp.enableClickMode {
            val newFragment = DatePickerDailogFragment.newInstance(DATE_PICKER_MODE.SPINNER)
            newFragment.dateListener = DatePickerDailogFragment
                .OnDatePickerDailogFragmentListener { view, year, month, dayOfMonth ->
                    bind.fieldLicenciaExp.setText("$dayOfMonth/$month/$year")
                }
            newFragment.show(supportFragmentManager, "datePicker")
        }

        bind.btnAddFile.onExclusiveClick {
            showFilePickerDialog()
        }

        bind.btnRegistrar.onExclusiveClick {

            val docEmit = bind.fieldLicenciaEmit.text()
            val docExp = bind.fieldLicenciaExp.text()

            if (areValid(docEmit, docExp)) {
                licenciaMotorizadoVM.postLicence(imageBytes, docEmit, docExp)
            }
        }
    }

    private fun areValid(docEmit: String, docExp: String): Boolean {

        if (docEmit.isEmpty()) {
            bind.fieldLicenciaEmit.inputError("Complete fecha de expedición")
            return false
        } else {
            bind.fieldLicenciaEmit.inputError(null)
        }

        if (docExp.isEmpty()) {
            bind.fieldLicenciaExp.inputError("Complete fecha de revalidación")
            return false
        } else {
            bind.fieldLicenciaExp.inputError(null)
        }

        if (imageBytes.isEmpty()) {
            ModalHelper.showToast(this, "Adjunte imagen", Toast.LENGTH_LONG)
            return false
        }

        return true
    }

    private fun showFilePickerDialog() {
        FileTypePickerDialog.Builder()
            .pdfRbVisibility(View.GONE)
            .build()
            .apply {
                completion = ::onImageBytes
            }
            .show(supportFragmentManager, "FileTypePi")
    }

    var imageBytes: ByteArray = ByteArray(0)

    private fun onImageBytes(bytes: ByteArray, name: String) {
        imageBytes = bytes

        Glide
            .with(this)
            .load(bytes)
            .into(bind.selectedImg)
    }
}