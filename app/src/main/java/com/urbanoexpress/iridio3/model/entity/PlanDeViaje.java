package com.urbanoexpress.iridio3.model.entity;

import com.orm.SugarRecord;

import java.io.Serializable;

/**
 * Created by mick on 30/05/16.
 */
public class PlanDeViaje extends SugarRecord implements Serializable {

    private static final long serialVersionUID = 4952007960990655557L;

    private String idPlanViaje;
    private String idUsuario;
    private String idUnidad;
    private String idOrigen;
    private String placa;
    private String origen;
    private String ruta;
    private String despachos;
    private String gruias;
    private String piezas;
    private String peso;
    private String peso_uso;
    private String volumen;
    private String volumen_uso;
    private String origen_latitude;
    private String origen_longitude;
    private String fecha;
    private String totalKMRuta;
    private int estadoRecorrido; // 0 => Ruta No Iniciado; 1 => Ruta Iniciada; 2 => Ruta Terminada

    public PlanDeViaje() { }

    public PlanDeViaje(String idPlanViaje, String idUsuario, String idUnidad, String idOrigen, String placa,
                       String origen, String ruta, String despachos, String gruias, String piezas,
                       String peso, String peso_uso, String volumen, String volumen_uso,
                       String origen_latitude, String origen_longitude, String fecha, String totalKMRuta, int estadoRecorrido) {
        this.idPlanViaje = idPlanViaje;
        this.idUsuario = idUsuario;
        this.idUnidad = idUnidad;
        this.idOrigen = idOrigen;
        this.placa = placa;
        this.origen = origen;
        this.ruta = ruta;
        this.despachos = despachos;
        this.gruias = gruias;
        this.piezas = piezas;
        this.peso = peso;
        this.peso_uso = peso_uso;
        this.volumen = volumen;
        this.volumen_uso = volumen_uso;
        this.origen_latitude = origen_latitude;
        this.origen_longitude = origen_longitude;
        this.fecha = fecha;
        this.totalKMRuta = totalKMRuta;
        this.estadoRecorrido = estadoRecorrido;
    }

    public String getIdPlanViaje() {
        return idPlanViaje;
    }

    public void setIdPlanViaje(String idPlanViaje) {
        this.idPlanViaje = idPlanViaje;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getIdUnidad() {
        return idUnidad;
    }

    public void setIdUnidad(String idUnidad) {
        this.idUnidad = idUnidad;
    }

    public String getIdOrigen() {
        return idOrigen;
    }

    public void setIdOrigen(String idOrigen) {
        this.idOrigen = idOrigen;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    public String getDespachos() {
        return despachos;
    }

    public void setDespachos(String despachos) {
        this.despachos = despachos;
    }

    public String getGruias() {
        return gruias;
    }

    public void setGruias(String gruias) {
        this.gruias = gruias;
    }

    public String getPiezas() {
        return piezas;
    }

    public void setPiezas(String piezas) {
        this.piezas = piezas;
    }

    public String getPeso() {
        return peso;
    }

    public void setPeso(String peso) {
        this.peso = peso;
    }

    public String getPeso_uso() {
        return peso_uso;
    }

    public void setPeso_uso(String peso_uso) {
        this.peso_uso = peso_uso;
    }

    public String getVolumen() {
        return volumen;
    }

    public void setVolumen(String volumen) {
        this.volumen = volumen;
    }

    public String getVolumen_uso() {
        return volumen_uso;
    }

    public void setVolumen_uso(String volumen_uso) {
        this.volumen_uso = volumen_uso;
    }

    public String getOrigen_latitude() {
        return origen_latitude;
    }

    public void setOrigen_latitude(String origen_latitude) {
        this.origen_latitude = origen_latitude;
    }

    public String getOrigen_longitude() {
        return origen_longitude;
    }

    public void setOrigen_longitude(String origen_longitude) {
        this.origen_longitude = origen_longitude;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getTotalKMRuta() {
        return totalKMRuta;
    }

    public void setTotalKMRuta(String totalKMRuta) {
        this.totalKMRuta = totalKMRuta;
    }

    public int getEstadoRecorrido() {
        return estadoRecorrido;
    }

    public void setEstadoRecorrido(int estadoRecorrido) {
        this.estadoRecorrido = estadoRecorrido;
    }

    public interface EstadoRuta {
        int NO_INICIO_RUTA  = 0;
        int INICIO_RUTA     = 1;
        int TERMINO_RUTA    = 2;
    }

}
