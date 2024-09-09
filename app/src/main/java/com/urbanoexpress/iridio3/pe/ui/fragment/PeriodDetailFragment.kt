package com.urbanoexpress.iridio3.pe.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.urbanoexpress.iridio3.pe.databinding.FragmentPeriodDetailBinding
import com.urbanoexpress.iridio3.pe.model.dto.Period
import com.urbanoexpress.iridio3.pe.model.dto.completeDays
import com.urbanoexpress.iridio3.pe.presenter.viewmodel.PeriodRevenueViewModel
import com.urbanoexpress.iridio3.pe.ui.adapter.RevenuePeriodDetailAdapter
import com.urbanoexpress.iridio3.urbanocore.extentions.operateOver
import com.urbanoexpress.iridio3.urbanocore.values.AK
import dagger.hilt.android.AndroidEntryPoint

//TODO> agregar penalidades
@AndroidEntryPoint
class PeriodDetailFragment : AppThemeBaseFragment() {

    lateinit var bind: FragmentPeriodDetailBinding

    val periodDetailVM: PeriodRevenueViewModel by viewModels()

    var period: Period? = null
    var isCurrentPeriod = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            period = it.getSerializable(AK.SELECTED_PERIOD) as Period?
            isCurrentPeriod = it.getBoolean(AK.IS_CURRENT)
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
            if (isCurrentPeriod) {
                adapter.revenueDays =
                    it.operateOver(
                        toThenThat = { item -> item.entregas == 0 && item.no_entregas == 0 },
                        operate = { notWorkingMessage = "No trabajó" })
            } else {
                adapter.revenueDays =
                    it.completeDays()
                        .operateOver(
                            toThenThat = { item -> item.entregas == 0 && item.no_entregas == 0 },
                            operate = { notWorkingMessage = "No trabajó" })
            }
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