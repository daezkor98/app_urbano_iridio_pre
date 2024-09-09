package com.urbanoexpress.iridio3.pe.ui.model;

/**
 * Created by mick on 30/11/16.
 */

public class AsignarDespachoItem {

    private String idOrigen;
    private String idDestino;
    private String idDespacho;
    private String fecha;
    private String origen;
    private String destino;
    private int imgArrow;
    private Boolean selected;

    public AsignarDespachoItem(String idOrigen, String idDestino, String idDespacho,
                               String fecha, String origen, String destino,
                               int imgArrow, Boolean selected) {
        this.idOrigen = idOrigen;
        this.idDestino = idDestino;
        this.idDespacho = idDespacho;
        this.fecha = fecha;
        this.origen = origen;
        this.destino = destino;
        this.imgArrow = imgArrow;
        this.selected = selected;
    }

    public String getIdOrigen() {
        return idOrigen;
    }

    public void setIdOrigen(String idOrigen) {
        this.idOrigen = idOrigen;
    }

    public String getIdDestino() {
        return idDestino;
    }

    public void setIdDestino(String idDestino) {
        this.idDestino = idDestino;
    }

    public String getIdDespacho() {
        return idDespacho;
    }

    public void setIdDespacho(String idDespacho) {
        this.idDespacho = idDespacho;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
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

    public int getImgArrow() {
        return imgArrow;
    }

    public void setImgArrow(int imgArrow) {
        this.imgArrow = imgArrow;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }
}
