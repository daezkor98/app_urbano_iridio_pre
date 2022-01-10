package com.urbanoexpress.iridio.ui.model;

public class PremioItem {

    private String idRuta;
    private String guiaNumero;
    private String idProducto;
    private String producto;
    private String piezas;
    private boolean selected;

    public PremioItem(String idRuta, String guiaNumero, String idProducto, String producto,
                      String piezas, boolean selected) {
        this.idRuta = idRuta;
        this.guiaNumero = guiaNumero;
        this.idProducto = idProducto;
        this.producto = producto;
        this.piezas = piezas;
        this.selected = selected;
    }

    public String getIdRuta() {
        return idRuta;
    }

    public void setIdRuta(String idRuta) {
        this.idRuta = idRuta;
    }

    public String getGuiaNumero() {
        return guiaNumero;
    }

    public void setGuiaNumero(String guiaNumero) {
        this.guiaNumero = guiaNumero;
    }

    public String getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(String idProducto) {
        this.idProducto = idProducto;
    }

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

    public String getPiezas() {
        return piezas;
    }

    public void setPiezas(String piezas) {
        this.piezas = piezas;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
