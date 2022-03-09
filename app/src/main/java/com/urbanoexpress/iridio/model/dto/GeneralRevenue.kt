package com.urbanoexpress.iridio.model.dto


/**
 * Created by Brandon Quintanilla on March/02/2022.
 */
data class GeneralRevenue(
    val weekRevenue: Double?,
    val prevWeeks: List<Period>?
)

data class Period(
    val beggningDay: String,
    val endingDay: String,
    val processState: String
)