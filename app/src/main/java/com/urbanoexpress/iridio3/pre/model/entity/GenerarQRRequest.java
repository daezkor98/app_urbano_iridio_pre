package com.urbanoexpress.iridio3.pre.model.entity;

import com.google.gson.annotations.SerializedName;

public class GenerarQRRequest {

    @SerializedName("guia")
    private String guia;

    @SerializedName("monto")
    private double monto;

    @SerializedName("tipo")
    private String tipo;

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("dni")
    private String dni;

    @SerializedName("divisa")
    private String divisa;

    @SerializedName("fintech_id")
    private int fintechId;

    @SerializedName("monto_total")
    private double montoTotal;

    /**
     * @param guia número de guía
     * @param monto monto de ESTE QR (máx 500, o el resto si es menor)
     * @param montoTotal monto total de la guía (para el servidor saber cuánto va a cobrar en total)
     * @param nombre nombre del cliente
     * @param dni DNI del cliente
     */
    public GenerarQRRequest(String guia, double monto, double montoTotal, String nombre, String dni) {
        this.guia = guia;
        this.monto = monto;
        this.montoTotal = montoTotal;
        this.tipo = "IRIDIO";
        this.nombre = nombre;
        this.dni = dni;
        this.divisa = "PEN";
        this.fintechId = 1;
    }
}
