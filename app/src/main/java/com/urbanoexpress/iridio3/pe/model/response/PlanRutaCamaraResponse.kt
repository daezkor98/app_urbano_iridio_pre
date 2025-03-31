package com.urbanoexpress.iridio3.pe.model.response

import com.google.gson.annotations.SerializedName


/**
 * Created by Brandon Quintanilla on Febrero/25/2025.
 */
data class PlanRutaCamaraResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: Data
)

data class Data(
    @SerializedName("sql_err") val sqlError: Int,
    @SerializedName("sql_msn") val sqlMsn: String,
    @SerializedName("data") val data: List<DataItem>
)

data class DataItem(
    @SerializedName("rou_id") val rouId: Int,
    @SerializedName("ciu_id") val ciuId: Int,
    @SerializedName("ciu_nombre") val ciuNombre: String,
    @SerializedName("zona_id") val zonaId: Int,
    @SerializedName("zona_codigo") val zonaCodigo: String,
    @SerializedName("time_ruta") val timeRuta: String,
    @SerializedName("km_ruta") val kmRuta: Double,
    @SerializedName("estado") val estado: Int,
    @SerializedName("tot_paradas") val totParadas: Int,
    @SerializedName("tot_guias") val totGuias: Int,
    @SerializedName("tot_piezas") val totPiezas: Int
)
