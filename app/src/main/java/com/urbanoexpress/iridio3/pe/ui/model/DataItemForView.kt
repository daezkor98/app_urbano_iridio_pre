package com.urbanoexpress.iridio3.pe.ui.model

import java.io.Serializable


/**
 * Created by Brandon Quintanilla on Febrero/27/2025.
 */
data class DataItemForView(
    val rouId: Int,
    val ciuId: Int,
    val ciuNombre: String,
    val zonaId: Int,
    val zonaCodigo: String,
    val timeRuta: String,
    val kmRuta: String,
    val estado : Int,
    val totParadas: Int,
    val totGuias: Int,
    val totPiezas: Int
) : Serializable