package com.urbanoexpress.iridio3.pre.ui.model;

/**
 * Created by mick on 27/04/17.
 */

public class OrigenPlanViajeItem {

    private String idPlanViaje;
    private String origen;
    private String fecha;
    private String descripcionRuta;
    private int backgroundColor;
    private boolean selected;

    public OrigenPlanViajeItem(String idPlanViaje, String origen, String fecha,
                               String descripcionRuta, int backgroundColor, boolean selected) {
        this.idPlanViaje = idPlanViaje;
        this.origen = origen;
        this.fecha = fecha;
        this.descripcionRuta = descripcionRuta;
        this.backgroundColor = backgroundColor;
        this.selected = selected;
    }

    public String getIdPlanViaje() {
        return idPlanViaje;
    }

    public void setIdPlanViaje(String idPlanViaje) {
        this.idPlanViaje = idPlanViaje;
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getDescripcionRuta() {
        return descripcionRuta;
    }

    public void setDescripcionRuta(String descripcionRuta) {
        this.descripcionRuta = descripcionRuta;
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
