package com.urbanoexpress.iridio3.pe.model.entity;

public class WaypointRuta {
    private double coorX;
    private double coorY;
    private int secuencia;
    private int idWayPoint;

    public WaypointRuta(double coorX, double coorY, int secuencia, int idWayPoint) {
        this.coorX = coorX;
        this.coorY = coorY;
        this.secuencia = secuencia;
        this.idWayPoint = idWayPoint;
    }

    public double getCoorX() {
        return coorX;
    }

    public void setCoorX(double coorX) {
        this.coorX = coorX;
    }

    public double getCoorY() {
        return coorY;
    }

    public void setCoorY(double coorY) {
        this.coorY = coorY;
    }

    public int getSecuencia() {
        return secuencia;
    }

    public void setSecuencia(int secuencia) {
        this.secuencia = secuencia;
    }

    public int getIdWayPoint() {
        return idWayPoint;
    }

    public void setIdWayPoint(int idWayPoint) {
        this.idWayPoint = idWayPoint;
    }
}
