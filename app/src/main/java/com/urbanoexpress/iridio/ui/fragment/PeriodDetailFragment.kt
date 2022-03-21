package com.urbanoexpress.iridio.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.urbanoexpress.iridio.databinding.FragmentPeriodDetailBinding
import com.urbanoexpress.iridio.model.PeriodRevenueViewModel
import com.urbanoexpress.iridio.model.dto.Period
import com.urbanoexpress.iridio.ui.adapter.RevenuePeriodDetailAdapter
import com.urbanoexpress.iridio.urbanocore.values.AK

class PeriodDetailFragment : AppThemeBaseFragment() {

    lateinit var bind: FragmentPeriodDetailBinding

    val periodDetailVM = PeriodRevenueViewModel()
    var period: Period? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            period = it.getSerializable(AK.SELECTED_PERIOD) as Period?
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        periodDetailVM.isLoadingLD.observe(this) { isLoading ->
            if (isLoading) {
                showProgressDialog()
            } else {
                dismissProgressDialog()
            }
        }

        periodDetailVM.periodDetailLD.observe(this) {
            adapter.revenueDays = it
        }

        period?.let {
            periodDetailVM.fetchWeekDetail(it.fecha_inicio!!, it.fecha_fin!!, it.liquidacion!!)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bind = FragmentPeriodDetailBinding.inflate(inflater, container, false)
        return bind.root
    }

    lateinit var adapter: RevenuePeriodDetailAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = RevenuePeriodDetailAdapter().apply {
            bind.rvDays.adapter = this
        }
        bind.tvTittle.text = "Del:   ${period?.fecha_inicio} \nHasta: ${period?.fecha_fin}"
    }
}