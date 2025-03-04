package com.urbanoexpress.iridio3.pe.model.response

import com.google.gson.annotations.SerializedName


/**
 * Created by Brandon Quintanilla on Febrero/27/2025.
 */
data class PlanRutaDetallesResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: DataDetallesResponse
)

data class DataDetallesResponse(
    @SerializedName("sql_err") val codeError: Int,
    @SerializedName("sql_msn") val codeMsg: String
)