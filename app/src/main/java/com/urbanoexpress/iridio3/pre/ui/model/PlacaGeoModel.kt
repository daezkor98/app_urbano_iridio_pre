package com.urbanoexpress.iridio3.pre.ui.model

import com.google.gson.annotations.SerializedName


/**
 * Created by Brandon Quintanilla on Febrero/26/2025.
 */
data class PlacaGeoModel(
    @SerializedName("rou_id") val rouId: Int,
    @SerializedName("tot_pza") val totPza: Int,
    @SerializedName("und_placa") val undPlaca: String,
    @SerializedName("celular") val celular: String,
    @SerializedName("per_id") val perId: String,
    @SerializedName("vp_id_user") val vpIdUser: String
)