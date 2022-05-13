package com.urbanoexpress.iridio3.model.dto

import com.urbanoexpress.iridio3.urbanocore.extentions.moveItem
import com.urbanoexpress.iridio3.urbanocore.extentions.ifNull


/**
 * Created by Brandon Quintanilla on March/02/2022.
 */

data class RevenueDay(
    val entregas: Int?,
    val monto_entregas: Double?,
    val no_entregas: Int?,
    val monto_no_entregas: Double,
    val dia_semana: Int?,
    var notWorkingMessage: String? = null
)

fun ArrayList<RevenueDay>.completeDays(): ArrayList<RevenueDay> {
    for (dayIndex in 0..6) {
        this
            .find { it.dia_semana == dayIndex }
            .ifNull {
                this.add(RevenueDay(0, 0.0, 0, 0.0, dayIndex))
            }
    }
    this.sortBy { it.dia_semana }
    this.moveItem(from = 0, to = 6)//In remote DB "Domingo" is 0 index so it have to be 6
    return this
}