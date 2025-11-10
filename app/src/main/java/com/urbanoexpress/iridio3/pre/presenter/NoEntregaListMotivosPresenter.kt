package com.urbanoexpress.iridio3.pre.presenter

import com.urbanoexpress.iridio3.pre.model.entity.GrupoMotivo
import com.urbanoexpress.iridio3.pre.model.interactor.RutaPendienteInteractor
import com.urbanoexpress.iridio3.pre.ui.model.MotivoDescargaItem


/**
 * Created by Brandon Quintanilla on Marzo/10/2025.
 */
class NoEntregaListMotivosPresenter(
    private val view: NoEntregaListMotivosContract.NoEntregaListMotivosView,
    private val rutaPendienteInteractor: RutaPendienteInteractor
) :
    NoEntregaListMotivosContract.NoEntregaListMotivosPresenter {
    private lateinit var motivoItems: ArrayList<MotivoDescargaItem>

    override fun getListMotivosNoEntrega() {
        val dbMotivoDescargas: List<GrupoMotivo> = rutaPendienteInteractor.selectAllMotivosNoEntrega()
        motivoItems = ArrayList()

        if (dbMotivoDescargas.isNotEmpty()){
            for (motivo in dbMotivoDescargas) {
               val item = MotivoDescargaItem(motivo.idGrupoMotivo.toString(), motivo.desGrupoMotivo, false)
                motivoItems.add(item)
            }
            view.showListMotivosNoEntrega(motivoItems)
        }else{
            view.showErrorEmptyList()
        }
    }
}