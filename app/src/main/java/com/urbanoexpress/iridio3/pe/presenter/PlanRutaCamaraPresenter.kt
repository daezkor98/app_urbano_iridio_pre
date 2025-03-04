package com.urbanoexpress.iridio3.pe.presenter

import com.urbanoexpress.iridio3.pe.model.interactor.PlanRutaCamaraInteractor
import com.urbanoexpress.iridio3.pe.model.interactor.RutaPendienteInteractor
import com.urbanoexpress.iridio3.pe.presenter.mapper.toView
import com.urbanoexpress.iridio3.pe.util.constant.PlanRutaConstants.GENERIC_ERROR
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


/**
 * Created by Brandon Quintanilla on Febrero/25/2025.
 */
class PlanRutaCamaraPresenter(
    private val planRutaCamaraView: PlanRutaCamaraContract.View,
    private val planRutaCamaraInteractor: PlanRutaCamaraInteractor,
    private val rutaPendienteInteractor: RutaPendienteInteractor,
    private val mainDispatcher: CoroutineDispatcher = Dispatchers.Main,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : PlanRutaCamaraContract.Presenter {

    override fun getRutaDetail(idRutaQR: String) {

        CoroutineScope(mainDispatcher).launch {
            try {
                val users = withContext(ioDispatcher) {
                    planRutaCamaraInteractor.getRutaDetail(idRutaQr = idRutaQR)
                }
                planRutaCamaraView.showRutaDetail(users.toView())

            } catch (e: Exception) {
                planRutaCamaraView.showError(e.message ?: GENERIC_ERROR)
            }
        }
    }

    override fun validateGuideList() {
        if (rutaPendienteInteractor.selectAllRutas().isNotEmpty()){
            planRutaCamaraView.showGuideList()
        }else{
           planRutaCamaraView.enableQrCamera()
        }
    }

    override fun detachView() {
        //
    }
}
