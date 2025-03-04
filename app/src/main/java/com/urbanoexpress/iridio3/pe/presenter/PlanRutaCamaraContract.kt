package com.urbanoexpress.iridio3.pe.presenter

import com.urbanoexpress.iridio3.pe.ui.model.DataItemForView


/**
 * Created by Brandon Quintanilla on Febrero/25/2025.
 */
interface PlanRutaCamaraContract {

    interface View {
        fun showRutaDetail(first: DataItemForView)
        fun showGuideList()
        fun enableQrCamera()
        fun showError(error: String)
    }

    interface Presenter {
        fun getRutaDetail(idRutaQR: String)
        fun validateGuideList()
        fun detachView()
    }

}