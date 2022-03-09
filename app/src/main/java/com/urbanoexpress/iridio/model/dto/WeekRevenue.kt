package com.urbanoexpress.iridio.model.dto


/**
 * Created by Brandon Quintanilla on March/02/2022.
 */
data class WeekRevenue(val weekDays: List<Day>)

data class Day(
    val dayRevenue: Double,
    val dayName: String,
    val dayIndex: String
)