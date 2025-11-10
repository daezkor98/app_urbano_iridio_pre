package com.urbanoexpress.iridio3.pre.model.entity;

public class CoordenadasRuta {
    private double dirPx;
    private double dirPy;
    private int geoId;

    public CoordenadasRuta(double dirPx, double dirPy, int geoId) {
        this.dirPx = dirPx;
        this.dirPy = dirPy;
        this.geoId = geoId;
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
}
