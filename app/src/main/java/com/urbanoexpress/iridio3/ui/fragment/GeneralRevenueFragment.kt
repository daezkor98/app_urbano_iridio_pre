package com.urbanoexpress.iridio3.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.urbanoexpress.iridio3.R
import com.urbanoexpress.iridio3.databinding.FragmentGeneralRevenueBinding
import com.urbanoexpress.iridio3.model.dto.CERT_ESTADO.APROBADO
import com.urbanoexpress.iridio3.model.dto.Period
import com.urbanoexpress.iridio3.presenter.viewmodel.GeneralRevenueViewModel
import com.urbanoexpress.iridio3.ui.BaseActivity2
import com.urbanoexpress.iridio3.ui.adapter.PeriodsRevenueAdapter
import com.urbanoexpress.iridio3.ui.dialogs.FacturaPeriodoResumenDialog
import com.urbanoexpress.iridio3.urbanocore.onExclusiveClick
import com.urbanoexpress.iridio3.urbanocore.values.AK

/**
 * Created by Brandon Quintanilla on March/01/2022.
 */
class GeneralRevenueFragment : AppThemeBaseFragment() {

    lateinit var bind: FragmentGeneralRevenueBinding

    val gananciasVM = GeneralRevenueViewModel()//TODO inject

    lateinit var periodsAdaper: PeriodsRevenueAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        observeViewModel()
    }

    lateinit var currentPeriod: Period
    private fun observeViewModel() {

        gananciasVM.isLoadingLD.observe(this) { isLoading ->
            if (isLoading) {
                showProgressDialog()
            } else {
                dismissProgressDialog()
            }
        }

        gananciasVM.generalRevenueDataLD.observe(this) {

            currentPeriod = it.Periods?.get(0)!!
            val currentAmount = currentPeriod.monto
            bind.tvWeekRevenue.text = "S/ ${currentAmount}"
            periodsAdaper.periods = it.Periods.filter { item -> item.periodo!! > 0 }
            areApproved = periodsAdaper.periods
                .filter { item -> item.cert_estado == APROBADO.state_id }
            bind.btnRegistrarFac.isEnabled = areApproved.isNotEmpty()
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

        periodsAdaper = PeriodsRevenueAdapter().apply {
            this.onItemClick = ::handleItemClick
            bind.rvWeeks.adapter = this@apply
        }

        configUI()

        gananciasVM.fetchMisGanancias()
    }

    private fun onCurrentPeriodClick() {
        val dialog = FacturaPeriodoResumenDialog.getInstance(
            currentPeriod,
            ::navigateToPeriodDetail
        )
        dialog.show(childFragmentManager, "RESS")
    }


    private fun configUI() {

        bind.tvWeekRevenue.onExclusiveClick { onCurrentPeriodClick() }
        bind.tvWeekSub.onExclusiveClick { onCurrentPeriodClick() }

        bind.btnRegistrarFac.onExclusiveClick {

            if (areApproved.size == 1) {
//            if (false) {//TODO fix

                findNavController()
                    .navigate(
                        R.id.action_generalRevenueFragment_to_registroFacturaFragment,
                        bundleOf(AK.SELECTED_PERIOD to areApproved.single())//TODO
//                        bundleOf(AK.SELECTED_PERIOD to periodsAdaper.periods[1])
                    )
            } else {

                findNavController().navigate(
                    R.id.action_generalRevenueFragment_to_selectPeriodFragment,
                    bundleOf(AK.PERIODS to areApproved)
//                    bundleOf(AK.PERIODS to periodsAdaper.periods)
                )
            }
        }
    }

    private fun handleItemClick(index: Int) {

        val dialog = FacturaPeriodoResumenDialog.getInstance(
            periodsAdaper.periods[index],
            ::navigateToPeriodDetail
        )
        dialog.show(childFragmentManager, "RESS")
    }

    fun navigateToPeriodDetail(period: Period) {

        val args = bundleOf(
            AK.SELECTED_PERIOD to period
        )

        findNavController().navigate(
            R.id.action_generalRevenueFragment_to_periodDetailFragment,
            args
        )
    }
}