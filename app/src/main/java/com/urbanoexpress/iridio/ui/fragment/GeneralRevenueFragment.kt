package com.urbanoexpress.iridio.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.urbanoexpress.iridio.R
import com.urbanoexpress.iridio.databinding.FragmentGeneralRevenueBinding
import com.urbanoexpress.iridio.model.GeneralRevenueViewModel
import com.urbanoexpress.iridio.ui.BaseActivity2
import com.urbanoexpress.iridio.ui.adapter.WeeksRevenueAdapter

/**
 * Created by Brandon Quintanilla on March/01/2022.
 */
class GeneralRevenueFragment : AppThemeBaseFragment() {

    lateinit var bind: FragmentGeneralRevenueBinding

    //TODO inject
    val gananciasVM = GeneralRevenueViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        observeViewModel()
    }

    private fun observeViewModel() {

        gananciasVM.fetchMisGanancias()
    }

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

        bind.rvWeeks.adapter = WeeksRevenueAdapter().apply {
            this.onItemClick = this@GeneralRevenueFragment::handleItemClick
        }
    }

    private fun handleItemClick(index: Int) {

        Log.i("TAG", "handleItemClick: $index")
        val args = bundleOf()

        findNavController().navigate(
            R.id.action_generalRevenueFragment_to_weekRevenueFragment,
            args
        )
    }
}