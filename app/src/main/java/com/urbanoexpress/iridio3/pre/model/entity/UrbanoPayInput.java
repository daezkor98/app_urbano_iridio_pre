package com.urbanoexpress.iridio3.pre.model.entity;

import java.io.Serializable;

/**
 * Input para {@code UrbanoPayActivity}. Se pasa via {@code Intent.putExtra("input", ...)}.
 * Contiene todo lo que la pantalla de pago QR necesita para funcionar sin acceso al dbRuta.
 */
public class UrbanoPayInput implements Serializable {

    private final String guia;
    private final double montoTotal;
    private final String nombreCliente;
    private final String dniCliente;

    public UrbanoPayInput(String guia, double montoTotal, String nombreCliente, String dniCliente) {
        this.guia = guia;
        this.montoTotal = montoTotal;
        this.nombreCliente = nombreCliente;
        this.dniCliente = dniCliente;
    }

    public String getGuia() { return guia; }
    public double getMontoTotal() { return montoTotal; }
    public String getNombreCliente() { return nombreCliente; }
    public String getDniCliente() { return dniCliente; }
}
