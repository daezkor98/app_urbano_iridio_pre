package com.urbanoexpress.iridio.model.dto


/**
 * Created by Brandon Quintanilla on March/02/2022.
 */

data class RevenueDay(
    val entregas: Int?,
    val monto_entregas: Double?,
    val no_entregas: Int?,
    val dia_semana: Int?,
//    val fac_fecha: String,
    val monto_no_entregas: Double,
)