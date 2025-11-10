package com.urbanoexpress.iridio3.pre.ui

import com.urbanoexpress.iridio3.pre.model.dto.RevenueDay


/**
 * Created by Brandon Quintanilla on Enero/10/2025.
 */
sealed class ResultRevenueDay {
    data class Success(val data:  ArrayList<RevenueDay>) : ResultRevenueDay()
    data class Error(val message: String) : ResultRevenueDay()
}