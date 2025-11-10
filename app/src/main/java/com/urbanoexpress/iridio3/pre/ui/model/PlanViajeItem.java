package com.urbanoexpress.iridio3.pre.ui.model;

/**
 * Created by mick on 01/12/16.
 */

public class PlanViajeItem {

    private String idPlanViaje;
    private String origen;
    private String fecha;
    private String transporte;
    private String nombreChofer;
    private String placaUnidad;

    public PlanViajeItem(String idPlanViaje, String origen, String fecha,
                         String transporte, String nombreChofer, String placaUnidad) {
        this.idPlanViaje = idPlanViaje;
        this.origen = origen;
        this.fecha = fecha;
        this.transporte = transporte;
        this.nombreChofer = nombreChofer;
        this.placaUnidad = placaUnidad;
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

    public String getTransporte() {
        return transporte;
    }

    public void setTransporte(String transporte) {
        this.transporte = transporte;
    }

    public String getNombreChofer() {
        return nombreChofer;
    }

    public void setNombreChofer(String nombreChofer) {
        this.nombreChofer = nombreChofer;
    }

    public String getPlacaUnidad() {
        return placaUnidad;
    }

    public void setPlacaUnidad(String placaUnidad) {
        this.placaUnidad = placaUnidad;
    }
}
