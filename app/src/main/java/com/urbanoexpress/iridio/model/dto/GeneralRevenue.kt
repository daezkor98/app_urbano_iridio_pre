package com.urbanoexpress.iridio.model.dto

import java.io.Serializable


/**
 * Created by Brandon Quintanilla on March/02/2022.
 */
data class GeneralRevenue(
    val periodRevenue: Double?,
    val prevPeriod: ArrayList<Period>?
)

data class Period(
    val beggningDay: String?,
    val endingDay: String?,
    val processState: String?,
    val weekPeriodRevenue: String?
) : Serializable
