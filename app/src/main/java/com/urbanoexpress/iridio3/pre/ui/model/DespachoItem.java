package com.urbanoexpress.iridio3.pre.ui.model;

/**
 * Created by mick on 02/06/16.
 */
public class DespachoItem {

    private String despacho;
    private String origen;
    private String destino;
    private String piezas;
    private String guia;
    private int imgArrow;
    private Boolean selected;

    public DespachoItem(String despacho, String origen, String destino, String piezas, String guia, int imgArrow, Boolean selected) {
        this.despacho = despacho;
        this.origen = origen;
        this.destino = destino;
        this.piezas = piezas;
        this.guia = guia;
        this.imgArrow = imgArrow;
        this.selected = selected;
    }

    public String getDespacho() {
        return despacho;
    }

    public void setDespacho(String despacho) {
        this.despacho = despacho;
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

    public String getPiezas() {
        return piezas;
    }

    public void setPiezas(String piezas) {
        this.piezas = piezas;
    }

    public String getGuia() {
        return guia;
    }

    public void setGuia(String guia) {
        this.guia = guia;
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
