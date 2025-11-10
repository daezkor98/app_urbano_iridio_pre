package com.urbanoexpress.iridio3.pre.model.response

import com.google.gson.annotations.SerializedName


/**
 * Created by Brandon Quintanilla on Febrero/27/2025.
 */
data class PlanRutaDetallesResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: DataDetallesResponse
)

data class DataDetallesResponse(
    @SerializedName("error_sql") val codeError: Int,
    @SerializedName("error_info") val codeMsg: String
)