package com.urbanoexpress.iridio3.ui.model;

public class ContenedorRecoleccionItem {

    private String idContenedor;
    private String barra;
    private String guias;
    private String piezas;
    private boolean selected;

    public ContenedorRecoleccionItem(String idContenedor, String barra, String guias, String piezas,
                                     boolean selected) {
        this.idContenedor = idContenedor;
        this.barra = barra;
        this.guias = guias;
        this.piezas = piezas;
        this.selected = selected;
    }

    public String getIdContenedor() {
        return idContenedor;
    }

    public void setIdContenedor(String idContenedor) {
        this.idContenedor = idContenedor;
    }

    public String getBarra() {
        return barra;
    }

    public void setBarra(String barra) {
        this.barra = barra;
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

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
