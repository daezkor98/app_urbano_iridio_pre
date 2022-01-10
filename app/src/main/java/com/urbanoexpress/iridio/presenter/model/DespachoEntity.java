package com.urbanoexpress.iridio.presenter.model;

/**
 * Created by mick on 08/02/17.
 */

public class DespachoEntity {

    private String idDespacho;
    private String codDespacho;
    private String descripcion;
    private String destino;
    private String origen;
    private String tipoVia;
    private String totalGuias;
    private String totalPiezas;
    private String totalPeso;
    private String fechaDespacho;

    public DespachoEntity(String idDespacho, String codDespacho, String descripcion,
                          String destino, String origen, String tipoVia,
                          String totalGuias, String totalPiezas, String totalPeso,
                          String fechaDespacho) {
        this.idDespacho = idDespacho;
        this.codDespacho = codDespacho;
        this.descripcion = descripcion;
        this.destino = destino;
        this.origen = origen;
        this.tipoVia = tipoVia;
        this.totalGuias = totalGuias;
        this.totalPiezas = totalPiezas;
        this.totalPeso = totalPeso;
        this.fechaDespacho = fechaDespacho;
    }

    public String getIdDespacho() {
        return idDespacho;
    }

    public void setIdDespacho(String idDespacho) {
        this.idDespacho = idDespacho;
    }

    public String getCodDespacho() {
        return codDespacho;
    }

    public void setCodDespacho(String codDespacho) {
        this.codDespacho = codDespacho;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public String getTipoVia() {
        return tipoVia;
    }

    public void setTipoVia(String tipoVia) {
        this.tipoVia = tipoVia;
    }

    public String getTotalGuias() {
        return totalGuias;
    }

    public void setTotalGuias(String totalGuias) {
        this.totalGuias = totalGuias;
    }

    public String getTotalPiezas() {
        return totalPiezas;
    }

    public void setTotalPiezas(String totalPiezas) {
        this.totalPiezas = totalPiezas;
    }

    public String getTotalPeso() {
        return totalPeso;
    }

    public void setTotalPeso(String totalPeso) {
        this.totalPeso = totalPeso;
    }

    public String getFechaDespacho() {
        return fechaDespacho;
    }

    public void setFechaDespacho(String fechaDespacho) {
        this.fechaDespacho = fechaDespacho;
    }

}
