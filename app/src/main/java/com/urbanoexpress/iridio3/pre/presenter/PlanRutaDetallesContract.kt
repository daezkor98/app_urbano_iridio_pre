package com.urbanoexpress.iridio3.pre.presenter

import com.urbanoexpress.iridio3.pre.ui.model.PlacaGeoModel
import com.urbanoexpress.iridio3.pre.util.Exception.BaseException


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