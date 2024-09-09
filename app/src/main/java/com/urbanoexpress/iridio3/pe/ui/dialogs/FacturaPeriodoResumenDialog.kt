package com.urbanoexpress.iridio3.pe.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import com.urbanoexpress.iridio3.databinding.ModalFacturaPeriodoResumenBinding
import com.urbanoexpress.iridio3.pe.model.dto.Period
import com.urbanoexpress.iridio3.urbanocore.extentions.onExclusiveClick
import com.urbanoexpress.iridio3.urbanocore.values.AK

typealias PeridoEvent = ((Period, Boolean) -> Unit)

class FacturaPeriodoResumenDialog : BaseDialogFragment() {

    lateinit var bind: ModalFacturaPeriodoResumenBinding

    lateinit var onVerMas: PeridoEvent

    private var period: Period? = null
    private var isCurrentPeriod = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            period = it.getSerializable(AK.SELECTED_PERIOD) as Period?
            isCurrentPeriod = it.getBoolean(AK.IS_CURRENT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)

        bind = ModalFacturaPeriodoResumenBinding.inflate(inflater, container, false)

        period?.let {
            //TODO format prices
            bind.tvEntregas.text = it.entregas.toString()
            bind.tvMontoEntregas.text = "S/ ${it.monto_entregas}"
            bind.tvVisitas.text = it.no_entregas.toString()
            bind.tvMontoVisitas.text = "S/ ${it.monto_no_entregas}"
        }

        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bind.btnGodetail.onExclusiveClick {
            period?.let {
                onVerMas.invoke(it, isCurrentPeriod)
            }
        }
    }

    companion object {

        fun getInstance(
            period: Period,
            isCurrent: Boolean,
            onVerMas: PeridoEvent
        ): FacturaPeriodoResumenDialog {
            val dialog = FacturaPeriodoResumenDialog()
            dialog.arguments = bundleOf(AK.SELECTED_PERIOD to period, AK.IS_CURRENT to isCurrent)
            dialog.onVerMas = onVerMas
            return dialog
        }
    }
}