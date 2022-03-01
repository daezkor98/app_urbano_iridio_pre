package com.urbanoexpress.iridio.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.urbanoexpress.iridio.databinding.FragmentWeekRevenueBinding
import com.urbanoexpress.iridio.ui.adapter.DayRevenueAdapter

class WeekRevenueFragment : BaseFragment2() {

    lateinit var bind: FragmentWeekRevenueBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        bind = FragmentWeekRevenueBinding.inflate(inflater, container, false)

        setupView()

        return bind.root
    }

    private fun setupView() {
        bind.rvDays.adapter = DayRevenueAdapter()
    }
}