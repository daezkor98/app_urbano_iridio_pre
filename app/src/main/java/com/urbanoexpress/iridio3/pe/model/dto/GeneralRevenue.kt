package com.urbanoexpress.iridio3.pe.model.dto

import java.io.Serializable

/**
 * Created by Brandon Quintanilla on March/02/2022.
 */
data class GeneralRevenue(
    val Periods: ArrayList<Period>?
)

data class Period(
    val fecha_inicio: String?,
    val fecha_fin: String?,
    val periodo: Int?,
    val liquidacion: String?,
    val cert_estado: String?,
    val entregas: Int?,
    val monto_entregas: Double?,
    val no_entregas: Int?,
    val monto_no_entregas: Double?,
    var monto: Double?,
    val fac_id: String,
    val fac_numero: String,
    val fac_total: String,
    val fac_fecha: String
) : Serializable

enum class CERT_ESTADO(val stateId: String, val stateName: String) {
    EN_PROCESO("0", "En proceso"),
    LIQUIDADO("1", "Liquidado"),
    APROBADO("2", "Aprobado"),
    FACTURADO("3", "Facturado"),
    PAGADO("5", "Pagado")
}

val certEstadosMap: Map<String, CERT_ESTADO> = mapOf(
    "0" to CERT_ESTADO.EN_PROCESO,
    "1" to CERT_ESTADO.LIQUIDADO,
    "2" to CERT_ESTADO.APROBADO,
    "3" to CERT_ESTADO.FACTURADO,
    "5" to CERT_ESTADO.PAGADO
)