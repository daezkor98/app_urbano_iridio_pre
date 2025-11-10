package com.urbanoexpress.iridio3.pre.presenter.mapper

import com.urbanoexpress.iridio3.pre.model.response.PlanRutaCamaraResponse
import com.urbanoexpress.iridio3.pre.ui.model.DataItemForView


/**
 * Created by Brandon Quintanilla on Febrero/25/2025.
 */

fun PlanRutaCamaraResponse.toView(): DataItemForView {
    return DataItemForView(
        rouId = this.data.rouId,
        ciuId = this.data.ciuId,
        ciuNombre = this.data.ciuNombre,
        zonaId = this.data.zonaId,
        zonaCodigo = this.data.zonaCodigo,
        timeRuta = this.data.timeRuta,
        kmRuta = this.data.kmRuta.toString(),
        estado = this.data.estado,
        totParadas = this.data.totParadas,
        totGuias = this.data.totGuias,
        totPiezas = this.data.totPiezas
    )
}