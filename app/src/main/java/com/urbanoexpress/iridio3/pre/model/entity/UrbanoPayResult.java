package com.urbanoexpress.iridio3.pre.model.entity;

import java.io.Serializable;

/**
 * Output de {@code UrbanoPayActivity}. Se retorna via {@code setResult(RESULT_OK, intent.putExtra("result", ...))}.
 * El caller (ej: EntregaGEDialog) decide qué hacer según el {@code status}.
 */
public class UrbanoPayResult implements Serializable {

    public enum Status {
        /** El motorizado cobró el total por QR. */
        PAGADO_TOTAL,
        /** Cobró parcialmente por QR y cambia a efectivo para el resto. */
        PAGADO_PARCIAL_Y_EFECTIVO,
        /** No cobró nada por QR, todo se pagará en efectivo. */
        EFECTIVO,
        /** Cerró la pantalla sin completar (back button). */
        CANCELADO
    }

    private final Status status;
    private final double montoPagadoQR;
    private final double montoPendienteEfectivo;
    private final String rqIdCodeFinal;
    private final String docNumero;
    private final String nombre;

    public UrbanoPayResult(Status status, double montoPagadoQR, double montoPendienteEfectivo,
                           String rqIdCodeFinal, String docNumero, String nombre) {
        this.status = status;
        this.montoPagadoQR = montoPagadoQR;
        this.montoPendienteEfectivo = montoPendienteEfectivo;
        this.rqIdCodeFinal = rqIdCodeFinal;
        this.docNumero = docNumero;
        this.nombre = nombre;
    }

    public Status getStatus() { return status; }
    public double getMontoPagadoQR() { return montoPagadoQR; }
    public double getMontoPendienteEfectivo() { return montoPendienteEfectivo; }
    public String getRqIdCodeFinal() { return rqIdCodeFinal; }
    public String getDocNumero() { return docNumero; }
    public String getNombre() { return nombre; }
}
