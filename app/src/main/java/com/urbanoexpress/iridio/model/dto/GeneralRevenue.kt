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
//    val processState: String?,
    val periodo: Int?,
    val liquidacion: String?,
    val cert_estado: String?,
    val entregas: Int?,
    val monto_entregas: Double?,
    val no_entregas: Int?,
    val monto_no_entregas: Double?,
    val monto: Double?//, val weekPeriodRevenue: String?
) : Serializable

enum class CERT_ESTADO(val state_id: String) {
    EN_PROCESO("0"),
    LIQUIDADO("1"),
    APROBADO("2"),
    FACTURADO("3")
}

val certEstadosMap: Map<String, String> = mapOf(
    "0" to "En proceso",
    "1" to "Liquidado",
    "2" to "Aprobado",
    "3" to "Facturado"
)