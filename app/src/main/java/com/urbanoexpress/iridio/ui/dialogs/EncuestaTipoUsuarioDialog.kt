package com.urbanoexpress.iridio.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.urbanoexpress.iridio.databinding.ModalEncuestaTipoUsuarioBinding
import com.urbanoexpress.iridio.ui.MotorizadoDocFormActivity
import com.urbanoexpress.iridio.urbanocore.onExclusiveClick

/**
 * A simple [Fragment] subclass.
 * Use the [EncuestaTipoUsuarioDialog.newInstance] factory method to
 * create an instance of this fragment.
 */
class EncuestaTipoUsuarioDialog : BaseDialogFragment() {

    lateinit var bind: ModalEncuestaTipoUsuarioBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.isCancelable = false
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        bind = ModalEncuestaTipoUsuarioBinding.inflate(inflater, container, false)

        bind.btnNoSoyMotorizado.onExclusiveClick {
            //TODO call service that notifies user is not "motorizado"
            dismiss()
        }
        bind.btnGotoEncuesta.onExclusiveClick {
            MotorizadoDocFormActivity.show(requireActivity())
            dismiss()
        }
        return bind.root
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            EncuestaTipoUsuarioDialog().apply {
            }
    }
}