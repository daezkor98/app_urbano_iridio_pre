package com.urbanoexpress.iridio.model.entity;

import com.orm.SugarRecord;

import java.io.Serializable;

/**
 * Created by mick on 13/12/16.
 */

public class UnidadPlanViaje extends SugarRecord implements Serializable {

    private static final long serialVersionUID = 689415487469893909L;

    private String idOrigen;
    private String idRuta;
    private String estadoRuta;
    private String siglaOrigen;
    private String nombreRuta;
    private String placaUnidad;
    private String nombreChofer;
    private String paradasRealizadas;
    private String paradasRestantes;
    private String fechaGPS;
    private String horaGPS;
    private String unidadLatitude;
    private String unidadLongitude;
    private String hubLatitude;
    private String hubLongitude;
    private String totalNovedades;
    private String horaSalidaOrigen;
    private String horaLlegadaHub;
    private String tiempoTranscurrido;

    public UnidadPlanViaje() { }

    public UnidadPlanViaje(String idOrigen, String idRuta, String estadoRuta,
                           String siglaOrigen, String nombreRuta, String placaUnidad,
                           String nombreChofer, String paradasRealizadas, String paradasRestantes,
                           String fechaGPS, String horaGPS, String unidadLatitude,
                           String unidadLongitude, String hubLatitude, String hubLongitude,
                           String totalNovedades, String horaSalidaOrigen, String horaLlegadaHub,
                           String tiempoTranscurrido) {
        this.idOrigen = idOrigen;
        this.idRuta = idRuta;
        this.estadoRuta = estadoRuta;
        this.siglaOrigen = siglaOrigen;
        this.nombreRuta = nombreRuta;
        this.placaUnidad = placaUnidad;
        this.nombreChofer = nombreChofer;
        this.paradasRealizadas = paradasRealizadas;
        this.paradasRestantes = paradasRestantes;
        this.fechaGPS = fechaGPS;
        this.horaGPS = horaGPS;
        this.unidadLatitude = unidadLatitude;
        this.unidadLongitude = unidadLongitude;
        this.hubLatitude = hubLatitude;
        this.hubLongitude = hubLongitude;
        this.totalNovedades = totalNovedades;
        this.horaSalidaOrigen = horaSalidaOrigen;
        this.horaLlegadaHub = horaLlegadaHub;
        this.tiempoTranscurrido = tiempoTranscurrido;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getIdOrigen() {
        return idOrigen;
    }

    public void setIdOrigen(String idOrigen) {
        this.idOrigen = idOrigen;
    }

    public String getIdRuta() {
        return idRuta;
    }

    public void setIdRuta(String idRuta) {
        this.idRuta = idRuta;
    }

    public String getEstadoRuta() {
        return estadoRuta;
    }

    public void setEstadoRuta(String estadoRuta) {
        this.estadoRuta = estadoRuta;
    }

    public String getSiglaOrigen() {
        return siglaOrigen;
    }

    public void setSiglaOrigen(String siglaOrigen) {
        this.siglaOrigen = siglaOrigen;
    }

    public String getNombreRuta() {
        return nombreRuta;
    }

    public void setNombreRuta(String nombreRuta) {
        this.nombreRuta = nombreRuta;
    }

    public String getPlacaUnidad() {
        return placaUnidad;
    }

    public void setPlacaUnidad(String placaUnidad) {
        this.placaUnidad = placaUnidad;
    }

    public String getNombreChofer() {
        return nombreChofer;
    }

    public void setNombreChofer(String nombreChofer) {
        this.nombreChofer = nombreChofer;
    }

    public String getParadasRealizadas() {
        return paradasRealizadas;
    }

    public void setParadasRealizadas(String paradasRealizadas) {
        this.paradasRealizadas = paradasRealizadas;
    }

    public String getParadasRestantes() {
        return paradasRestantes;
    }

    public void setParadasRestantes(String paradasRestantes) {
        this.paradasRestantes = paradasRestantes;
    }

    public String getFechaGPS() {
        return fechaGPS;
    }

    public void setFechaGPS(String fechaGPS) {
        this.fechaGPS = fechaGPS;
    }

    public String getHoraGPS() {
        return horaGPS;
    }

    public void setHoraGPS(String horaGPS) {
        this.horaGPS = horaGPS;
    }

    public String getUnidadLatitude() {
        return unidadLatitude;
    }

    public void setUnidadLatitude(String unidadLatitude) {
        this.unidadLatitude = unidadLatitude;
    }

    public String getUnidadLongitude() {
        return unidadLongitude;
    }

    public void setUnidadLongitude(String unidadLongitude) {
        this.unidadLongitude = unidadLongitude;
    }

    public String getHubLatitude() {
        return hubLatitude;
    }

    public void setHubLatitude(String hubLatitude) {
        this.hubLatitude = hubLatitude;
    }

    public String getHubLongitude() {
        return hubLongitude;
    }

    public void setHubLongitude(String hubLongitude) {
        this.hubLongitude = hubLongitude;
    }

    public String getTotalNovedades() {
        return totalNovedades;
    }

    public void setTotalNovedades(String totalNovedades) {
        this.totalNovedades = totalNovedades;
    }

    public String getHoraSalidaOrigen() {
        return horaSalidaOrigen;
    }

    public void setHoraSalidaOrigen(String horaSalidaOrigen) {
        this.horaSalidaOrigen = horaSalidaOrigen;
    }

    public String getHoraLlegadaHub() {
        return horaLlegadaHub;
    }

    public void setHoraLlegadaHub(String horaLlegadaHub) {
        this.horaLlegadaHub = horaLlegadaHub;
    }

    public String getTiempoTranscurrido() {
        return tiempoTranscurrido;
    }

    public void setTiempoTranscurrido(String tiempoTranscurrido) {
        this.tiempoTranscurrido = tiempoTranscurrido;
    }
}
