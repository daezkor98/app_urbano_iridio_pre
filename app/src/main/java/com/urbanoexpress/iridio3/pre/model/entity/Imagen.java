package com.urbanoexpress.iridio3.pre.model.entity;

import com.orm.SugarRecord;

/**
 * Created by mick on 19/07/16.
 */
public class Imagen extends SugarRecord {

    private String idUsuario;
    private String name;
    private String path;
    private int clasificacion; // La clasificacion es para poder agrupar las imagenes a un grupo o modelo.
    private String idSuperior; // Id del registro al que pertenece (esto va depender de su clasificacion)
    private String fechaCreacion;
    private String gpsLatitude;
    private String gpsLongitude;
    private String idServiciosAdjuntos;
    private String lineaNegocio;
    private String anotaciones;

    private int dataSync;
    /**
     * Variable para validar si los datos estan completos. Ejm:
     * En caso de que la guia(manifiesto) se generó desde el celular.
     */
    private int dataValidate;

    public Imagen() { }

    public Imagen(String idUsuario, String name, String path,
                  int clasificacion, String idSuperior, String fechaCreacion,
                  String gpsLatitude, String gpsLongitude, String anotaciones,
                  String idServiciosAdjuntos, String lineaNegocio, int dataValidate,
                  int dataSync) {
        this.idUsuario = idUsuario;
        this.name = name;
        this.path = path;
        this.clasificacion = clasificacion;
        this.idSuperior = idSuperior;
        this.fechaCreacion = fechaCreacion;
        this.gpsLatitude = gpsLatitude;
        this.gpsLongitude = gpsLongitude;
        this.anotaciones = anotaciones;
        this.idServiciosAdjuntos = idServiciosAdjuntos;
        this.lineaNegocio = lineaNegocio;
        this.dataValidate = dataValidate;
        this.dataSync = dataSync;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getClasificacion() {
        return clasificacion;
    }

    public void setClasificacion(int clasificacion) {
        this.clasificacion = clasificacion;
    }

    public String getIdSuperior() {
        return idSuperior;
    }

    public void setIdSuperior(String idSuperior) {
        this.idSuperior = idSuperior;
    }

    public String getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(String fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
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

    public String getIdServiciosAdjuntos() {
        return idServiciosAdjuntos;
    }

    public void setIdServiciosAdjuntos(String idServiciosAdjuntos) {
        this.idServiciosAdjuntos = idServiciosAdjuntos;
    }

    public String getLineaNegocio() {
        return lineaNegocio;
    }

    public void setLineaNegocio(String lineaNegocio) {
        this.lineaNegocio = lineaNegocio;
    }

    public String getAnotaciones() {
        return anotaciones;
    }

    public void setAnotaciones(String anotaciones) {
        this.anotaciones = anotaciones;
    }

    public int getDataSync() {
        return dataSync;
    }

    public void setDataSync(int dataSync) {
        this.dataSync = dataSync;
    }

    public int getDataValidate() {
        return dataValidate;
    }

    public void setDataValidate(int dataValidate) {
        this.dataValidate = dataValidate;
    }

    public String getFullPath() {
        return path + name;
    }

    public interface Tipo {
        int GESTION_GUIA        = 10;
        int PARADA_PROGRAMADA   = 11;
    }

}
