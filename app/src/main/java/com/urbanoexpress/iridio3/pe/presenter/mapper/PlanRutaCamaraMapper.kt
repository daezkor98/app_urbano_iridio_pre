package com.urbanoexpress.iridio3.pe.presenter.mapper

import com.urbanoexpress.iridio3.pe.model.response.PlanRutaCamaraResponse
import com.urbanoexpress.iridio3.pe.ui.model.DataItemForView


/**
 * Created by Brandon Quintanilla on Febrero/25/2025.
 */

fun PlanRutaCamaraResponse.toView(): DataItemForView {
    return this.data.data.first().let {
        DataItemForView(
            rouId = it.rouId,
            ciuId = it.ciuId,
            ciuNombre = it.ciuNombre,
            zonaId = it.zonaId,
            zonaCodigo = it.zonaCodigo,
            timeRuta = it.timeRuta,
            kmRuta = it.kmRuta.toString(),
            estado = it.estado,
            totParadas = it.totParadas,
            totGuias = it.totGuias,
            totPiezas = it.totPiezas
        )
    }
}