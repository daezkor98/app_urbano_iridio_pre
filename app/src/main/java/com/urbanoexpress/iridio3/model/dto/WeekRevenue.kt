package com.urbanoexpress.iridio3.model.dto

import com.urbanoexpress.iridio3.urbanocore.ifNull


/**
 * Created by Brandon Quintanilla on March/02/2022.
 */

data class RevenueDay(
    val entregas: Int?,
    val monto_entregas: Double?,
    val no_entregas: Int?,
    val monto_no_entregas: Double,
    val dia_semana: Int?
)

fun ArrayList<RevenueDay>.completeDays() {
    for (dayIndex in 0..6) {
        this
            .find { it.dia_semana == dayIndex }
            .ifNull {
                this.add(RevenueDay(0, 0.0, 0, 0.0, dayIndex))
            }
    }
    this.sortBy { it.dia_semana }
    this.moveItem(0,6)//In remote DB "Domingo" is 0 index
}

/**
 * from: index of element to move
 * to: resultant index
 * */
fun <T> ArrayList<T>.moveItem(from:Int, to:Int){
    val item= this[from]
    this.removeAt(from)
    this.add(to,item)
}
