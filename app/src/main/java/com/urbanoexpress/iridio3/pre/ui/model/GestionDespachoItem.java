package com.urbanoexpress.iridio3.pre.ui.model;

/**
 * Created by mick on 14/11/16.
 */

public class GestionDespachoItem {

    private String idDespacho;
    private String fechaDespacho;
    private String guias;
    private String piezas;
    private String peso;
    private String origen;
    private String destino;
    private int iconTipoVia;

    public GestionDespachoItem(String idDespacho, String fechaDespacho, String guias,
                               String piezas, String peso, String origen, String destino,
                               int iconTipoVia) {
        this.idDespacho = idDespacho;
        this.fechaDespacho = fechaDespacho;
        this.guias = guias;
        this.piezas = piezas;
        this.peso = peso;
        this.origen = origen;
        this.destino = destino;
        this.iconTipoVia = iconTipoVia;
    }

    public String getIdDespacho() {
        return idDespacho;
    }

    public void setIdDespacho(String idDespacho) {
        this.idDespacho = idDespacho;
    }

    public String getFechaDespacho() {
        return fechaDespacho;
    }

    public void setFechaDespacho(String fechaDespacho) {
        this.fechaDespacho = fechaDespacho;
    }

    public String getGuias() {
        return guias;
    }

    public void setGuias(String guias) {
        this.guias = guias;
    }

    public String getPiezas() {
        return piezas;
    }

    public void setPiezas(String piezas) {
        this.piezas = piezas;
    }

    public String getPeso() {
        return peso;
    }

    public void setPeso(String peso) {
        this.peso = peso;
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public int getIconTipoVia() {
        return iconTipoVia;
    }

    public void setIconTipoVia(int iconTipoVia) {
        this.iconTipoVia = iconTipoVia;
    }
}
