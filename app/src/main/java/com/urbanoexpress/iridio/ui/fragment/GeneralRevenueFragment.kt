package com.urbanoexpress.iridio.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.urbanoexpress.iridio.R
import com.urbanoexpress.iridio.databinding.FragmentGeneralRevenueBinding
import com.urbanoexpress.iridio.model.GeneralRevenueViewModel
import com.urbanoexpress.iridio.ui.BaseActivity2
import com.urbanoexpress.iridio.ui.adapter.WeeksRevenueAdapter
import com.urbanoexpress.iridio.urbanocore.values.AK
import java.lang.Exception

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

        gananciasVM.isLoadingLD.observe(this) { isLoading ->
            if (isLoading) {
                //TODO
                //               showProgressDialog()
/*                requireActivity()
                    .findViewById<View>(R.id.progress_lay)
                    .findViewById<View>(R.id.progress_layout)
                    .visibility =  View.VISIBLE*/
            } else {
//                dismissProgressDialog()
            }
        }

        gananciasVM.generalRevenueDataLD.observe(this) {
            Log.i("TAG", "observeViewModel: " + Gson().toJson(it))
            bind.tvWeekRevenue.text = "S/ ${it.periodRevenue}"
            periodAdaper.periods = it.prevPeriod!!
        }
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

    lateinit var periodAdaper: WeeksRevenueAdapter

    private fun setupView() {
        periodAdaper = WeeksRevenueAdapter().apply {
            this.onItemClick = this@GeneralRevenueFragment::handleItemClick
        }

        bind.rvWeeks.adapter = periodAdaper

        gananciasVM.fetchMisGanancias()//TODO manage one time fetch data
    }

    private fun handleItemClick(index: Int) {

        try {

            val args = bundleOf(
                AK.PERIOD to periodAdaper.periods[index]
            )

            findNavController().navigate(
                R.id.action_generalRevenueFragment_to_weekRevenueFragment,
                args)

        } catch (e: Exception) {
            Log.e("TAG", "handleItemClick: " + e.message, e)
        }
    }
}