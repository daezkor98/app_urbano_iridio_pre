package com.urbanoexpress.iridio3.pre.ui.model;

public class GuiasMapaRutaDiaItem {
    private String barra;
    private String secuencia;
    private int flagValidaGestion;
    private double latitude;
    private double longitude;

    public GuiasMapaRutaDiaItem(String barra, String secuencia, int flagValidaGestion, double latitude, double longitude) {
        this.barra = barra;
        this.secuencia = secuencia;
        this.flagValidaGestion = flagValidaGestion;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getBarra() {
        return barra;
    }

    public void setBarra(String barra) {
        this.barra = barra;
    }

    public String getSecuencia() {
        return secuencia;
    }

    public void setSecuencia(String secuencia) {
        this.secuencia = secuencia;
    }

    public int getFlagValidaGestion() {
        return flagValidaGestion;
    }

    public void setFlagValidaGestion(int flagValidaGestion) {
        this.flagValidaGestion = flagValidaGestion;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
