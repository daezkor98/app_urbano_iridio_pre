package com.urbanoexpress.iridio3.pe.presenter

import com.urbanoexpress.iridio3.pe.ui.model.PlacaGeoModel
import com.urbanoexpress.iridio3.pe.util.Exception.BaseException


/**
 * Created by Brandon Quintanilla on Febrero/25/2025.
 */
interface PlanRutaTransporteContract {

    interface View {
        fun showGuideList(successMsg:String)
        fun showError(error: BaseException)
    }

    interface Presenter {
        fun validateRoad(placaGeoModel: PlacaGeoModel)
        fun detachView()
    }
}