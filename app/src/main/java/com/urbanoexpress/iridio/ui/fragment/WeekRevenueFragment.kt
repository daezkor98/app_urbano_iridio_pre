package com.urbanoexpress.iridio.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.urbanoexpress.iridio.databinding.FragmentWeekRevenueBinding
import com.urbanoexpress.iridio.model.WeekRevenueViewModel
import com.urbanoexpress.iridio.model.dto.Period
import com.urbanoexpress.iridio.ui.adapter.DayRevenueAdapter
import com.urbanoexpress.iridio.urbanocore.values.AK

/**
 * Created by Brandon Quintanilla on March/01/2022.
 */
class WeekRevenueFragment : BaseFragment2() {

    lateinit var bind: FragmentWeekRevenueBinding

    //TODO inject
    val gananciasVM = WeekRevenueViewModel()

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

        bind.tvWeekRevenue.text = "S/ ${perd.weekPeriodRevenue}"

        dayAdapter = DayRevenueAdapter()
        bind.rvDays.adapter = dayAdapter

        gananciasVM.fetchWeekDetail()
    }
}