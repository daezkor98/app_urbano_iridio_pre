package com.urbanoexpress.iridio3.pre.ui.model;

/**
 * Created by mick on 12/06/17.
 */

public class TransferirGuiaItem {

    private String idServicio;
    private String guiaElectronica;
    private int idResIconLinea = -1;
    private int backgroundColor;
    private boolean selected;

    public TransferirGuiaItem(String idServicio, String guiaElectronica, int idResIconLinea,
                              int backgroundColor, boolean selected) {
        this.idServicio = idServicio;
        this.guiaElectronica = guiaElectronica;
        this.idResIconLinea = idResIconLinea;
        this.backgroundColor = backgroundColor;
        this.selected = selected;
    }

    public String getIdServicio() {
        return idServicio;
    }

    public void setIdServicio(String idServicio) {
        this.idServicio = idServicio;
    }

    public String getGuiaElectronica() {
        return guiaElectronica;
    }

    public void setGuiaElectronica(String guiaElectronica) {
        this.guiaElectronica = guiaElectronica;
    }

    public int getIdResIconLinea() {
        return idResIconLinea;
    }

    public void setIdResIconLinea(int idResIconLinea) {
        this.idResIconLinea = idResIconLinea;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
