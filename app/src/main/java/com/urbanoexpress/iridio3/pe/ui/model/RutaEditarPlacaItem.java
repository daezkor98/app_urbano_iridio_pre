package com.urbanoexpress.iridio3.pe.ui.model;

public class RutaEditarPlacaItem {

    private String idRuta;
    private int resIconLinea;

    public RutaEditarPlacaItem(String idRuta, int resIconLinea) {
        this.idRuta = idRuta;
        this.resIconLinea = resIconLinea;
    }

    public String getIdRuta() {
        return idRuta;
    }

    public void setIdRuta(String idRuta) {
        this.idRuta = idRuta;
    }

    public int getResIconLinea() {
        return resIconLinea;
    }

    public void setResIconLinea(int resIconLinea) {
        this.resIconLinea = resIconLinea;
    }
}
