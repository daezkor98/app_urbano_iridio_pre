package com.urbanoexpress.iridio.model.dto

import java.io.Serializable

/**
 * Created by Brandon Quintanilla on March/02/2022.
 */
data class GeneralRevenue(
    //val periodRevenue: Double?,
    val Periods: ArrayList<Period>?
)

data class Period(
    val fecha_inicio: String?,
    val fecha_fin: String?,
    val processState: String?,
    val periodo: Int?,
    val liquidacion: String?,
    val cert_estado: String?,
    val monto: Double?,
    val weekPeriodRevenue: String?
) : Serializable

enum class CERT_ESTADO(state: Int) {
    LIQUIDADO(1),
    APROBADO(2),
    FACTURADO(3)
}