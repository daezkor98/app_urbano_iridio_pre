package com.urbanoexpress.iridio.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import com.urbanoexpress.iridio.databinding.ModalFacturaPeriodoResumenBinding
import com.urbanoexpress.iridio.model.dto.Period
import com.urbanoexpress.iridio.urbanocore.SimpleEvent
import com.urbanoexpress.iridio.urbanocore.onExclusiveClick

class FacturaPeriodoResumenDialog : BaseDialogFragment() {

    lateinit var bind: ModalFacturaPeriodoResumenBinding

    lateinit var onVerMas: SimpleEvent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
/*            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)*/
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

//        return inflater.inflate(R.layout.modal_factura_periodo_resumen, container, false)
        bind = ModalFacturaPeriodoResumenBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bind.btnGodetail.onExclusiveClick { onVerMas.invoke() }
    }

    companion object {

        fun getInstance(period: Period, onVerMas: SimpleEvent): FacturaPeriodoResumenDialog {
            val dialog = FacturaPeriodoResumenDialog()
            dialog.arguments = bundleOf("" to "")
            dialog.onVerMas = onVerMas
            return dialog
        }
    }
}