package com.urbanoexpress.iridio3.pe.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.urbanoexpress.iridio3.R
import com.urbanoexpress.iridio3.databinding.FragmentSelectPeriodBinding
import com.urbanoexpress.iridio3.pe.model.dto.Period
import com.urbanoexpress.iridio3.pe.ui.adapter.PeriodsRevenueAdapter
import com.urbanoexpress.iridio3.urbanocore.values.AK

class SelectPeriodFragment : Fragment() {

    lateinit var bind: FragmentSelectPeriodBinding

    var areApproved: List<Period>? = null

    lateinit var periodsAdaper: PeriodsRevenueAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            areApproved = it.getSerializable(AK.PERIODS) as List<Period>?
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bind = FragmentSelectPeriodBinding.inflate(inflater, container, false)
        setupView()
        return bind.root
    }

    private fun setupView() {
        periodsAdaper = PeriodsRevenueAdapter().apply {
            this.onItemClick = ::handleItemClick
            bind.rvWeeks.adapter = this@apply
        }

        areApproved?.let {
            periodsAdaper.periods = it
        }
    }

    private fun handleItemClick(index: Int) {
        findNavController()
            .navigate(
                R.id.action_selectPeriodFragment_to_registroFacturaFragment,
                bundleOf(AK.SELECTED_PERIOD to periodsAdaper.periods[index])
            )
    }
}