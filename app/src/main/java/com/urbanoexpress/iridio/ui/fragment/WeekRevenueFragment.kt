package com.urbanoexpress.iridio.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.urbanoexpress.iridio.databinding.FragmentWeekRevenueBinding

class WeekRevenueFragment : BaseFragment2() {

    lateinit var bind: FragmentWeekRevenueBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        bind = FragmentWeekRevenueBinding.inflate(inflater, container, false)

        return bind.root
    }
}