package com.urbanoexpress.iridio3.pe.presenter

import com.urbanoexpress.iridio3.pe.model.interactor.PlanRutaTransporteInteractor
import com.urbanoexpress.iridio3.pe.ui.model.PlacaGeoModel
import com.urbanoexpress.iridio3.pe.util.Exception.BaseException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


/**
 * Created by Brandon Quintanilla on Febrero/25/2025.
 */
class PlanRutaTransportePresenter(
    private val planRutaTransporteView: PlanRutaTransporteContract.View,
    private val planRutasTransporteInteractor: PlanRutaTransporteInteractor,
    private val mainDispatcher: CoroutineDispatcher = Dispatchers.Main,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : PlanRutaTransporteContract.Presenter {
    private var job: Job? = null

    override fun validateRoad(placaGeoModel: PlacaGeoModel) {
        job = CoroutineScope(mainDispatcher).launch {
            try {
                val validate = withContext(ioDispatcher) {
                    planRutasTransporteInteractor.validateRoad(road = placaGeoModel)
                }
                planRutaTransporteView.showGuideList(validate.data.codeMsg)

            } catch (exception: BaseException) {
                planRutaTransporteView.showError(exception)
            }
        }
    }

    override fun detachView() {
        job?.cancel()
    }
}