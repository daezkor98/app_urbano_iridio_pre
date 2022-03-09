package com.urbanoexpress.iridio.model

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.urbanoexpress.iridio.model.interactor.BaseViewModel
import com.urbanoexpress.iridio.model.interactor.MisGananciasInteractor
import com.urbanoexpress.iridio.urbanocore.ST


/**
 * Created by Brandon Quintanilla on March/02/2022.
 */

class GeneralRevenueViewModel : BaseViewModel() {

//    var misGananciasInteractor = MisGananciasInteractor()

    val misGananciasLD = MutableLiveData<Throwable>()

    fun fetchMisGanancias() = executeIO {

        val param = HashMap<String, Any>()
        val data = MisGananciasInteractor.getMisGanancias(param)
        Log.i("TAG", "fethcData: " + ST.gson.toJson(data))
        //TODO
//        ST
    }

}