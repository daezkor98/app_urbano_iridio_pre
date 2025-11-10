package com.urbanoexpress.iridio3.pre.ui.model;

/**
 * Created by mick on 03/08/17.
 */

public class RecoleccionGEItem {

    private String guiaElectronica;
    private String guiaNumero;
    private String piezas;
    private String fechaSS;
    private boolean isGuiaNoRecolectada;
    private boolean selected;

    public RecoleccionGEItem() {}

    public RecoleccionGEItem(RecoleccionGEItem recoleccionGEItem) {
        this.guiaElectronica = recoleccionGEItem.getGuiaElectronica();
        this.guiaNumero = recoleccionGEItem.getGuiaNumero();
        this.piezas = recoleccionGEItem.getPiezas();
        this.fechaSS = recoleccionGEItem.getFechaSS();
        this.isGuiaNoRecolectada = recoleccionGEItem.isGuiaNoRecolectada();
        this.selected = recoleccionGEItem.isSelected();
    }

    public RecoleccionGEItem(String guiaElectronica, String guiaNumero, String piezas,
                             String fechaSS, boolean isGuiaNoRecolectada, boolean selected) {
        this.guiaElectronica = guiaElectronica;
        this.guiaNumero = guiaNumero;
        this.piezas = piezas;
        this.fechaSS = fechaSS;
        this.isGuiaNoRecolectada = isGuiaNoRecolectada;
        this.selected = selected;
    }

    public String getGuiaElectronica() {
        return guiaElectronica;
    }

    public void setGuiaElectronica(String guiaElectronica) {
        this.guiaElectronica = guiaElectronica;
    }

    public String getGuiaNumero() {
        return guiaNumero;
    }

    public void setGuiaNumero(String guiaNumero) {
        this.guiaNumero = guiaNumero;
    }

    public String getPiezas() {
        return piezas;
    }

    public void setPiezas(String piezas) {
        this.piezas = piezas;
    }

    public String getFechaSS() {
        return fechaSS;
    }

    public void setFechaSS(String fechaSS) {
        this.fechaSS = fechaSS;
    }

    public boolean isGuiaNoRecolectada() {
        return isGuiaNoRecolectada;
    }

    public void setGuiaNoRecolectada(boolean guiaNoRecolectada) {
        isGuiaNoRecolectada = guiaNoRecolectada;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
