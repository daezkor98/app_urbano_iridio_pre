package com.urbanoexpress.iridio3.pre.model.entity;

import java.util.List;

public class ParadaRuta {
    private final List<ClienteRuta> clientes;
    private double dirPx;
    private double dirPy;
    private int geoId;
    private String direccion;
    private List<WaypointRuta> waypoints;

    public ParadaRuta(List<ClienteRuta> clientes, double dirPx, double dirPy, int geoId, String direccion, List<WaypointRuta> waypoints) {
        this.clientes = clientes;
        this.dirPx = dirPx;
        this.dirPy = dirPy;
        this.geoId = geoId;
        this.direccion = direccion;
        this.waypoints = waypoints;
    }

    public List<ClienteRuta> getClientes() {
        return clientes;
    }

    public double getDirPx() {
        return dirPx;
    }

    public void setDirPx(double dirPx) {
        this.dirPx = dirPx;
    }

    public double getDirPy() {
        return dirPy;
    }

    public void setDirPy(double dirPy) {
        this.dirPy = dirPy;
    }

    public int getGeoId() {
        return geoId;
    }

    public void setGeoId(int geoId) {
        this.geoId = geoId;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public List<WaypointRuta> getWaypoints() {
        return waypoints;
    }

    public void setWaypoints(List<WaypointRuta> waypoints) {
        this.waypoints = waypoints;
    }
}
