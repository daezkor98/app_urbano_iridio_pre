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

    public GenerarQRRequest(String guia, double monto, String nombre, String dni) {
        this.guia = guia;
        this.monto = monto;
        this.tipo = "Iridio";
        this.nombre = nombre;
        this.dni = dni;
        this.divisa = "PEN";
        this.fintechId = 1;
    }
}
