package com.urbanoexpress.iridio3.pre.presenter

import com.urbanoexpress.iridio3.pre.ui.model.MotivoDescargaItem


/**
 * Created by Brandon Quintanilla on Marzo/10/2025.
 */
class NoEntregaListMotivosContract {

    interface NoEntregaListMotivosView {
        fun showListMotivosNoEntrega(motivos: List<MotivoDescargaItem> )
        fun showErrorEmptyList()
    }

    interface NoEntregaListMotivosPresenter {
        fun getListMotivosNoEntrega()
    }
}