package com.urbanoexpress.iridio.presenter.model;

/**
 * Created by mick on 14/12/16.
 */

public class RutaEntity {

    private String idLH;
    private String idVia;
    private String via;
    private String ruta;
    private String origen;

    public RutaEntity(String idLH, String idVia, String via,
                      String ruta, String origen) {
        this.idLH = idLH;
        this.idVia = idVia;
        this.via = via;
        this.ruta = ruta;
        this.origen = origen;
    }

    public String getIdLH() {
        return idLH;
    }

    public void setIdLH(String idLH) {
        this.idLH = idLH;
    }

    public String getIdVia() {
        return idVia;
    }

    public void setIdVia(String idVia) {
        this.idVia = idVia;
    }

    public String getVia() {
        return via;
    }

    public void setVia(String via) {
        this.via = via;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }
}
