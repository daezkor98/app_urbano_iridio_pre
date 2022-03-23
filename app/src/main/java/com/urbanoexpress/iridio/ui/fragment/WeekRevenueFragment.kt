package com.urbanoexpress.iridio.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.urbanoexpress.iridio.databinding.FragmentWeekRevenueBinding
import com.urbanoexpress.iridio.model.PeriodRevenueViewModel
import com.urbanoexpress.iridio.model.dto.Period
import com.urbanoexpress.iridio.ui.adapter.DayRevenueAdapter
import com.urbanoexpress.iridio.urbanocore.values.AK

/**
 * Created by Brandon Quintanilla on March/01/2022.
 */
//TODO REMOVE
class WeekRevenueFragment : AppThemeBaseFragment() {

    lateinit var bind: FragmentWeekRevenueBinding

    //TODO inject
    val gananciasVM = PeriodRevenueViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        observeViewModel()
    }

    private fun observeViewModel() {

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        bind = FragmentWeekRevenueBinding.inflate(inflater, container, false)

        setupView()

        return bind.root
    }

    lateinit var dayAdapter: DayRevenueAdapter

    private fun setupView() {
        val perd = arguments?.get(AK.SELECTED_PERIOD) as Period

        bind.tvWeekRevenue.text = "S/ ${perd.monto}"

        dayAdapter = DayRevenueAdapter()
        bind.rvDays.adapter = dayAdapter

//        gananciasVM.fetchWeekDetail()
    }
}