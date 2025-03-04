package com.urbanoexpress.iridio3.pe.presenter

import android.util.Log
import com.urbanoexpress.iridio3.pe.model.interactor.PlanRutasDetallesInteractor
import com.urbanoexpress.iridio3.pe.ui.model.PlacaGeoModel
import com.urbanoexpress.iridio3.pe.util.constant.PlanRutaConstants.GENERIC_ERROR
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


/**
 * Created by Brandon Quintanilla on Febrero/25/2025.
 */
class PlanRutaTransportePresenter(
    private val planRutaDetallesView: PlanRutaTransporteContract.View,
    private val planRutasDetallesInteractor: PlanRutasDetallesInteractor,
    private val mainDispatcher: CoroutineDispatcher = Dispatchers.Main,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : PlanRutaTransporteContract.Presenter {

    override fun validateRoad(placaGeoModel: PlacaGeoModel) {
        CoroutineScope(mainDispatcher).launch {
            try {
                val validate = withContext(ioDispatcher) {
                    planRutasDetallesInteractor.validateRoad(road = placaGeoModel)
                }
                planRutaDetallesView.showGuideList(validate.data.codeMsg)

            } catch (e: Exception) {
                planRutaDetallesView.showError(e.message ?: GENERIC_ERROR)
            }
        }
    }

    override fun detachView() {
        Log.d("Hola", "detachView")
    }
}