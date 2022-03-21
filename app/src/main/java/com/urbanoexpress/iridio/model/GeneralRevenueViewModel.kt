package com.urbanoexpress.iridio.model

import androidx.lifecycle.MutableLiveData
import com.urbanoexpress.iridio.model.dto.GeneralRevenue
import com.urbanoexpress.iridio.model.interactor.BaseViewModel
import com.urbanoexpress.iridio.model.interactor.MisGananciasInteractor
import com.urbanoexpress.iridio.urbanocore.longlog
import com.urbanoexpress.iridio.util.Preferences

/**
 * Created by Brandon Quintanilla on March/02/2022.
 */
class GeneralRevenueViewModel : BaseViewModel() {

    val generalRevenueDataLD = MutableLiveData<GeneralRevenue>()

    fun fetchMisGanancias() = executeIO {

        //TODO no enviar parametros en duro
        val userID = Preferences.getInstance().getString("idUsuario", "")
        val idPer = Preferences.getInstance().getString("idPer", "")


        idPer?.longlog("fetchMisGanancias")

        val param = mapOf(
            "vp_per_id" to idPer
//            "vp_per_id" to "5446"
//            "vp_per_id" to userID
        )

        val data = MisGananciasInteractor.getMisGanancias(param)
        generalRevenueDataLD.postValue(data)
    }
}