package com.urbanoexpress.iridio3.pe.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.urbanoexpress.iridio3.databinding.ModalEncuestaTipoUsuarioBinding
import com.urbanoexpress.iridio3.pe.presenter.viewmodel.LicenciaFormViewModel
import com.urbanoexpress.iridio3.pe.ui.MainActivity
import com.urbanoexpress.iridio3.pe.ui.MotorizadoDocFormActivity
import com.urbanoexpress.iridio3.pe.ui.helpers.ModalHelper
import com.urbanoexpress.iridio3.urbanocore.extentions.onExclusiveClick
import com.urbanoexpress.iridio3.pe.util.Preferences

/**
 * Used to complete license data of "Motorizados"
 */
class EncuestaTipoUsuarioDialog : BaseDialogFragment() {

    lateinit var bind: ModalEncuestaTipoUsuarioBinding

    val licenciaMotorizadoVM = LicenciaFormViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.isCancelable = false

        licenciaMotorizadoVM.onRegisterSuccess.observe(this)
        {
            Preferences
                .getInstance()
                .edit()
                .putString("mostrarEncuesta", "0")//Should not request again
                .apply()

            val act = requireActivity() as MainActivity
            act.dismissProgressDialog()
            dismiss()
        }

        licenciaMotorizadoVM.exceptionLD.observe(this) {
            ModalHelper.showToast(requireContext(), it.message, Toast.LENGTH_LONG)
            dismiss()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        bind = ModalEncuestaTipoUsuarioBinding.inflate(inflater, container, false)

        bind.btnNoSoyMotorizado.onExclusiveClick {
            licenciaMotorizadoVM.notifyUserIsNotMotorizado()
        }
        bind.btnGotoEncuesta.onExclusiveClick {
            dismiss()
            MotorizadoDocFormActivity.show(requireActivity())
        }
        return bind.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = EncuestaTipoUsuarioDialog().apply {}
    }
}