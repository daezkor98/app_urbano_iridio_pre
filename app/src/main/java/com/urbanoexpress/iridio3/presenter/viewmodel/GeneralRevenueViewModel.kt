package com.urbanoexpress.iridio3.presenter.viewmodel

import androidx.lifecycle.MutableLiveData
import com.urbanoexpress.iridio3.model.dto.GeneralRevenue
import com.urbanoexpress.iridio3.model.interactor.MisGananciasInteractor
import com.urbanoexpress.iridio3.util.Preferences

/**
 * Created by Brandon Quintanilla on March/02/2022.
 */
class GeneralRevenueViewModel : BaseViewModel() {

    val generalRevenueDataLD = MutableLiveData<GeneralRevenue>()

    val gananciasInteractor = MisGananciasInteractor()

    fun fetchMisGanancias() = executeIO {

        val idPer = Preferences.getInstance().getString("idPer", "")

        val param = mapOf(
            "vp_per_id" to idPer
        )

        val data = gananciasInteractor.getMisGanancias(param)
        generalRevenueDataLD.postValue(data)
    }
}