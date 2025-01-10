package com.urbanoexpress.iridio3.pe.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.urbanoexpress.iridio3.pe.R
import com.urbanoexpress.iridio3.pe.databinding.FragmentGeneralRevenueBinding
import com.urbanoexpress.iridio3.pe.model.dto.CERT_ESTADO.APROBADO
import com.urbanoexpress.iridio3.pe.model.dto.Period
import com.urbanoexpress.iridio3.pe.presenter.viewmodel.GeneralRevenueViewModel
import com.urbanoexpress.iridio3.pe.ui.BaseActivity2
import com.urbanoexpress.iridio3.pe.ui.adapter.PeriodsRevenueAdapter
import com.urbanoexpress.iridio3.pe.ui.dialogs.FacturaPeriodoResumenDialog
import com.urbanoexpress.iridio3.pe.ui.dialogs.MessageDialog
import com.urbanoexpress.iridio3.urbanocore.extentions.findMessage
import com.urbanoexpress.iridio3.urbanocore.extentions.goneIf
import com.urbanoexpress.iridio3.urbanocore.extentions.onExclusiveClick
import com.urbanoexpress.iridio3.urbanocore.values.AK
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by Brandon Quintanilla on March/01/2022.
 */
@AndroidEntryPoint
class GeneralRevenueFragment : AppThemeBaseFragment() {

    lateinit var bind: FragmentGeneralRevenueBinding

    private val gananciasVM: GeneralRevenueViewModel by viewModels()

    lateinit var periodsAdapter: PeriodsRevenueAdapter

    lateinit var currentPeriod: Period

    private var itemListClickDialog: FacturaPeriodoResumenDialog? = null
    private var facturaPeriodoResumenDialog: FacturaPeriodoResumenDialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        observeViewModel()
    }

    private fun observeViewModel() {

        gananciasVM.exceptionLD.observe(this) {
            val messageDialog = MessageDialog.newInstance("ERROR : " + it.findMessage())
            messageDialog.show(childFragmentManager, "MsgDial")
        }

        gananciasVM.isLoadingLD.observe(this) { isLoading ->
            if (isLoading) {
                showProgressDialog()
            } else {
                dismissProgressDialog()
            }
        }

        gananciasVM.generalRevenueDataLD.observe(this) {
            currentPeriod = it.Periods?.get(0)!!

            bind.tvWeekRevenue.text = "S/ ${currentPeriod.monto}"
            periodsAdapter.periods =
                it.Periods.filter { item -> item.periodo!! > 0 && item.monto!! > 0 }
            areApproved =
                periodsAdapter.periods.filter { item -> item.cert_estado == APROBADO.stateId }

            this.bind.cardWeeksTitle.goneIf { periodsAdapter.periods.isEmpty() }
            bind.btnRegistrarFac.isEnabled = areApproved.isNotEmpty()
            bind.tvWeekRevenue.isEnabled = true
        }
    }

    lateinit var areApproved: List<Period>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        bind = FragmentGeneralRevenueBinding.inflate(inflater, container, false)

        activity?.let {
            it as BaseActivity2
            it.setScreenTitle(R.string.fragment_title)
        }

        setupView()

        return bind.root
    }

    private fun setupView() {

        periodsAdapter = PeriodsRevenueAdapter().apply {
            this.onItemClick = ::handleItemClick
            bind.rvWeeks.adapter = this@apply
        }

        configUI()

        gananciasVM.fetchMisGanancias()
    }

    private fun onCurrentPeriodClick() {
        facturaPeriodoResumenDialog = FacturaPeriodoResumenDialog.getInstance(
            currentPeriod,
            true,
            ::navigateToPeriodDetail
        )
        facturaPeriodoResumenDialog  ?.show(childFragmentManager, "RESS")

    }

    private fun configUI() {

        bind.tvWeekRevenue.onExclusiveClick { onCurrentPeriodClick() }
        bind.tvWeekSub.onExclusiveClick { onCurrentPeriodClick() }

        //TODO make a secure exclusiveClick
        bind.btnRegistrarFac.onExclusiveClick {
            closeDialog()
            if (areApproved.size == 1) {
                findNavController()
                    .navigate(
                        R.id.action_generalRevenueFragment_to_registroFacturaFragment,
                        bundleOf(AK.SELECTED_PERIOD to areApproved.single())
                    )
            } else {
                findNavController().navigate(
                    R.id.action_generalRevenueFragment_to_selectPeriodFragment,
                    bundleOf(AK.PERIODS to areApproved)
                )
            }
        }
    }

    private fun handleItemClick(index: Int) {

        itemListClickDialog = FacturaPeriodoResumenDialog.getInstance(
            periodsAdapter.periods[index],
            false,
            ::navigateToPeriodDetail
        )
        itemListClickDialog?.show(childFragmentManager, "RESS")
    }

    fun navigateToPeriodDetail(period: Period, isCurrent: Boolean) {
        closeDialog()
        if (period.entregas == 0 && period.no_entregas == 0) {
            showToast("Lo sentimos, sin detalles para mostrar.")

        } else {
            val args = bundleOf(
                AK.SELECTED_PERIOD to period,
                AK.IS_CURRENT to isCurrent
            )

            findNavController().navigate(
                R.id.action_generalRevenueFragment_to_periodDetailFragment,
                args
            )

        }
    }


    private fun closeDialog() {
        itemListClickDialog?.dismiss()
        itemListClickDialog = null

        facturaPeriodoResumenDialog?.dismiss()
        facturaPeriodoResumenDialog = null
    }
}