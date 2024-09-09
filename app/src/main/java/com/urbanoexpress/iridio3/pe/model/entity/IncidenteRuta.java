package com.urbanoexpress.iridio3.pe.model.entity;

import com.orm.SugarRecord;

import java.io.Serializable;

public class IncidenteRuta extends SugarRecord implements Serializable {

    private static final long serialVersionUID = 1419371964767199238L;

    private String idUsuario;
    private String idRuta;
    private int tipoRuta;
    private String idMotivoIncidente;
    private String imageName;
    private String imagePath;
    private String anotacionesImagen;
    private String fecha;
    private String hora;
    private String comentarios;
    private String gpsLatitude;
    private String gpsLongitude;
    private String lineaNegocio;

    private int dataSync;

    public IncidenteRuta() { }

    public IncidenteRuta(String idUsuario, String idRuta, int tipoRuta, String idMotivoIncidente,
                         String imageName, String imagePath, String anotacionesImagen, String fecha,
                         String hora, String comentarios, String gpsLatitude, String gpsLongitude, String lineaNegocio) {
        this.idUsuario = idUsuario;
        this.idRuta = idRuta;
        this.tipoRuta = tipoRuta;
        this.idMotivoIncidente = idMotivoIncidente;
        this.imageName = imageName;
        this.imagePath = imagePath;
        this.anotacionesImagen = anotacionesImagen;
        this.fecha = fecha;
        this.hora = hora;
        this.comentarios = comentarios;
        this.gpsLatitude = gpsLatitude;
        this.gpsLongitude = gpsLongitude;
        this.lineaNegocio = lineaNegocio;
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

    public int getTipoRuta() {
        return tipoRuta;
    }

    public void setTipoRuta(int tipoRuta) {
        this.tipoRuta = tipoRuta;
    }

    public String getIdMotivoIncidente() {
        return idMotivoIncidente;
    }

    public void setIdMotivoIncidente(String idMotivoIncidente) {
        this.idMotivoIncidente = idMotivoIncidente;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getAnotacionesImagen() {
        return anotacionesImagen;
    }

    public void setAnotacionesImagen(String anotacionesImagen) {
        this.anotacionesImagen = anotacionesImagen;
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

    public String getComentarios() {
        return comentarios;
    }

    public void setComentarios(String comentarios) {
        this.comentarios = comentarios;
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

    public String getLineaNegocio() {
        return lineaNegocio;
    }

    public void setLineaNegocio(String lineaNegocio) {
        this.lineaNegocio = lineaNegocio;
    }

    public int getDataSync() {
        return dataSync;
    }

    public void setDataSync(int dataSync) {
        this.dataSync = dataSync;
    }

    public String getFullPath() {
        return imagePath + imageName;
    }

    public interface TipoRuta {
        int PLAN_DE_VIAJE = 10;
    }
}