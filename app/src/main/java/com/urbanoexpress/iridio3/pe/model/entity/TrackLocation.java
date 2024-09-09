package com.urbanoexpress.iridio3.pe.model.entity;

import com.orm.SugarRecord;

/**
 * Created by mick on 03/08/16.
 */
public class TrackLocation extends SugarRecord {

    private String idUsuario;
    private String idRuta;
    private String lineaNegocio;
    private String fecha;
    private String hora;
    private String gpsLatitude;
    private String gpsLongitude;
    private String gpsExactitud;
    private String celularBateria;
    private int tipo;

    private int dataSync;

    public TrackLocation() { }

    public TrackLocation(String idUsuario, String idRuta, String lineaNegocio, String fecha,
                         String hora, String gpsLatitude, String gpsLongitude,
                         String gpsExactitud, String celularBateria, int tipo) {
        this.idUsuario = idUsuario;
        this.idRuta = idRuta;
        this.lineaNegocio = lineaNegocio;
        this.fecha = fecha;
        this.hora = hora;
        this.gpsLatitude = gpsLatitude;
        this.gpsLongitude = gpsLongitude;
        this.gpsExactitud = gpsExactitud;
        this.celularBateria = celularBateria;
        this.tipo = tipo;
        this.dataSync = Data.Sync.PENDING;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getIdRuta() {
        return idRuta;
    }

    public void setIdRuta(String idRuta) {
        this.idRuta = idRuta;
    }

    public String getLineaNegocio() {
        return lineaNegocio;
    }

    public void setLineaNegocio(String lineaNegocio) {
        this.lineaNegocio = lineaNegocio;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getGpsLatitude() {
        return gpsLatitude;
    }

    public void setGpsLatitude(String gpsLatitude) {
        this.gpsLatitude = gpsLatitude;
    }

    public String getGpsLongitude() {
        return gpsLongitude;
    }

    public void setGpsLongitude(String gpsLongitude) {
        this.gpsLongitude = gpsLongitude;
    }

    public String getGpsExactitud() {
        return gpsExactitud;
    }

    public void setGpsExactitud(String gpsExactitud) {
        this.gpsExactitud = gpsExactitud;
    }

    public String getCelularBateria() {
        return celularBateria;
    }

    public void setCelularBateria(String celularBateria) {
        this.celularBateria = celularBateria;
    }

    public int getDataSync() {
        return dataSync;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    public void setDataSync(int dataSync) {
        this.dataSync = dataSync;
    }

    public interface Tipo {
        int RUTA_DEL_DIA = 1;
        int PLAN_DE_VIAJE = 2;
    }
}
