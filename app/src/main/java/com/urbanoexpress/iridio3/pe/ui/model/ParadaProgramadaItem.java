package com.urbanoexpress.iridio3.pe.ui.model;

/**
 * Created by mick on 25/05/16.
 */
public class ParadaProgramadaItem {

    private String ruta;
    private String horaEstimada;
    private String horaLlegada;
    private String horaSalida;
    private int colorHoraLlegada;

    public ParadaProgramadaItem(String ruta, String horaEstimada, String horaLlegada,
                                String horaSalida, int colorHoraLlegada) {
        this.ruta = ruta;
        this.horaEstimada = horaEstimada;
        this.horaLlegada = horaLlegada;
        this.horaSalida = horaSalida;
        this.colorHoraLlegada = colorHoraLlegada;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    public String getHoraEstimada() {
        return horaEstimada;
    }

    public void setHoraEstimada(String horaEstimada) {
        this.horaEstimada = horaEstimada;
    }

    public String getHoraLlegada() {
        return horaLlegada;
    }

    public void setHoraLlegada(String horaLlegada) {
        this.horaLlegada = horaLlegada;
    }

    public String getHoraSalida() {
        return horaSalida;
    }

    public void setHoraSalida(String horaSalida) {
        this.horaSalida = horaSalida;
    }

    public int getColorHoraLlegada() {
        return colorHoraLlegada;
    }

    public void setColorHoraLlegada(int colorHoraLlegada) {
        this.colorHoraLlegada = colorHoraLlegada;
    }
}
