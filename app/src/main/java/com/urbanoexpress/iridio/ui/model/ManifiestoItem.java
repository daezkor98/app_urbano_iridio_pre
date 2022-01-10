package com.urbanoexpress.iridio.ui.model;

/**
 * Created by mick on 12/10/16.
 */

public class ManifiestoItem {

    private String idManifiesto;
    private String descripcion;

    public ManifiestoItem(String idManifiesto, String descripcion) {
        this.idManifiesto = idManifiesto;
        this.descripcion = descripcion;
    }

    public String getIdManifiesto() {
        return idManifiesto;
    }

    public void setIdManifiesto(String idManifiesto) {
        this.idManifiesto = idManifiesto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
