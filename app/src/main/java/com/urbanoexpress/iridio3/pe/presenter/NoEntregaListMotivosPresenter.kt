package com.urbanoexpress.iridio3.pe.presenter

import com.urbanoexpress.iridio3.pe.model.entity.GrupoMotivo
import com.urbanoexpress.iridio3.pe.model.interactor.RutaPendienteInteractor
import com.urbanoexpress.iridio3.pe.ui.model.MotivoDescargaItem


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