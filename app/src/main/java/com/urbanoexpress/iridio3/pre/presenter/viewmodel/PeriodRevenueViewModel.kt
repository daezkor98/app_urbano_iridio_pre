package com.urbanoexpress.iridio3.pre.presenter.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.urbanoexpress.iridio3.pre.model.interactor.MisGananciasInteractor
import com.urbanoexpress.iridio3.pre.ui.ResultRevenueDay
import com.urbanoexpress.iridio3.pre.util.Preferences
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Created by Brandon Quintanilla on March/02/2022.
 */
@HiltViewModel
class PeriodRevenueViewModel @Inject constructor() : BaseViewModel() {

    //val periodDetailLD: MutableLiveData<ArrayList<RevenueDay>> = MutableLiveData()

    private val _periodDetailLD = MutableLiveData<ResultRevenueDay>()
    val periodDetailLD: LiveData<ResultRevenueDay> get() = _periodDetailLD

    //    val gananciasInteractor = MisGananciasInteractor()
    val gananciasInteractor by lazy { MisGananciasInteractor() }

//    fun fetchWeekDetail(fechaInicio: String, fechaFin: String, certID: String) = executeIO {
//
//        val idPer = Preferences.getInstance().getString("idPer", "")!!
//        val userID = Preferences.getInstance().getString("idUsuario", "")!!
//
//        val params = mapOf(
//            "vp_per_id" to idPer,
//            "vp_fecha_inicio" to fechaInicio,
//            "vp_fecha_fin" to fechaFin,
//            "vp_cert_id" to certID,
//            "vp_id_user" to userID
//        )
//
////        val data = MisGananciasInteractor.getSemanaDetail(params)
//        try {
//            val data = gananciasInteractor.getSemanaDetail(params)
//            //periodDetailLD.postValue(data)
//            _periodDetailLD.postValue(ResultRevenueDay.Success(data))
//
//        } catch (e: Exception) {
//
//            _periodDetailLD.postValue(ResultRevenueDay.Error("Error"))
//
//        }
//    }

    fun fetchWeekDetail(fechaInicio: String, fechaFin: String, certID: String) = executeIO {

        val idPer = Preferences.getInstance().getString("idPer", "")!!
        val userID = Preferences.getInstance().getString("idUsuario", "")!!

        try {
            val data = gananciasInteractor.getSemanaDetail(idPer, fechaInicio, fechaFin, certID)
            _periodDetailLD.postValue(ResultRevenueDay.Success(data))
        } catch (e: Exception) {
            _periodDetailLD.postValue(ResultRevenueDay.Error("Error: ${e.message}"))
        }
    }
}