package com.urbanoexpress.iridio.model

import androidx.lifecycle.MutableLiveData
import com.urbanoexpress.iridio.model.dto.GeneralRevenue
import com.urbanoexpress.iridio.model.interactor.BaseViewModel
import com.urbanoexpress.iridio.model.interactor.MisGananciasInteractor

/**
 * Created by Brandon Quintanilla on March/02/2022.
 */
class GeneralRevenueViewModel : BaseViewModel() {

    val generalRevenueDataLD = MutableLiveData<GeneralRevenue>()

    fun fetchMisGanancias() = executeIO {

        //TODO no enviar parametros en duro
        val param = mapOf(
            "vp_per_id" to "5446"
            //, "vp_periodo" to "3"
        )

        val data = MisGananciasInteractor.getMisGanancias(param)
        generalRevenueDataLD.postValue(data)
    }
}