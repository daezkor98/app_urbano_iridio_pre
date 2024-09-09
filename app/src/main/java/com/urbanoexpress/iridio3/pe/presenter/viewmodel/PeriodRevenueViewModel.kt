package com.urbanoexpress.iridio3.pe.presenter.viewmodel

import androidx.lifecycle.MutableLiveData
import com.urbanoexpress.iridio3.pe.model.dto.RevenueDay
import com.urbanoexpress.iridio3.pe.model.interactor.MisGananciasInteractor
import com.urbanoexpress.iridio3.pe.util.Preferences
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Created by Brandon Quintanilla on March/02/2022.
 */
@HiltViewModel
class PeriodRevenueViewModel @Inject constructor(): BaseViewModel() {

    val periodDetailLD: MutableLiveData<ArrayList<RevenueDay>> = MutableLiveData()

//    val gananciasInteractor = MisGananciasInteractor()
val gananciasInteractor by lazy { MisGananciasInteractor() }

    fun fetchWeekDetail(fechaInicio: String, fechaFin: String, certID: String) = executeIO {

        val idPer = Preferences.getInstance().getString("idPer", "")!!
        val userID = Preferences.getInstance().getString("idUsuario", "")!!

        val params = mapOf(
            "vp_per_id" to idPer,
            "vp_fecha_inicio" to fechaInicio,
            "vp_fecha_fin" to fechaFin,
            "vp_cert_id" to certID,
            "vp_id_user" to userID
        )

//        val data = MisGananciasInteractor.getSemanaDetail(params)
        val data = gananciasInteractor.getSemanaDetail(params)

        periodDetailLD.postValue(data)
    }
}