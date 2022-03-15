package com.urbanoexpress.iridio.model

import android.util.Log
import com.urbanoexpress.iridio.model.interactor.BaseViewModel
import com.urbanoexpress.iridio.model.interactor.MisGananciasInteractor
import com.urbanoexpress.iridio.urbanocore.ST


/**
 * Created by Brandon Quintanilla on March/02/2022.
 */
class WeekRevenueViewModel : BaseViewModel() {

    fun fetchWeekDetail() = executeIO {
        val param = HashMap<String, Any>()
        val data = MisGananciasInteractor.getSemanaDetail(param)

    }
}