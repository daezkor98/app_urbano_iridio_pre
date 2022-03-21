package com.urbanoexpress.iridio.model

import androidx.lifecycle.MutableLiveData
import com.urbanoexpress.iridio.model.dto.RevenueDay
import com.urbanoexpress.iridio.model.interactor.BaseViewModel
import com.urbanoexpress.iridio.model.interactor.MisGananciasInteractor
import com.urbanoexpress.iridio.urbanocore.logJson
import com.urbanoexpress.iridio.util.Preferences


/**
 * Created by Brandon Quintanilla on March/02/2022.
 */
class PeriodRevenueViewModel : BaseViewModel() {

    val periodDetailLD: MutableLiveData<List<RevenueDay>> = MutableLiveData()

    fun fetchWeekDetail(fechaInicio: String, fechaFin: String, certID: String) = executeIO {

        //TODO eliminar data en duro
        val idPer = Preferences.getInstance().getString("idPer", "")!!
        val userID = Preferences.getInstance().getString("idUsuario", "")!!

        /*
        val params = mapOf(
            "vp_per_id" to "5446",
            "vp_fecha_inicio" to "01/01/2022",
            "vp_fecha_fin" to "31/03/2022",
            "vp_cert_id" to "0",
            "vp_id_user" to "12345"
        )
        */

        val params = mapOf(
            "vp_per_id" to idPer,
            "vp_fecha_inicio" to fechaInicio,
            "vp_fecha_fin" to fechaFin,
            "vp_cert_id" to certID,
            "vp_id_user" to userID
        )

        val data = MisGananciasInteractor.getSemanaDetail(params)

        data.logJson("fetchWeekDetail")

        periodDetailLD.postValue(data)
    }
}