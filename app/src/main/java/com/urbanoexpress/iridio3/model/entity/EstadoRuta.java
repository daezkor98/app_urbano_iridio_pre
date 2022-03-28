package com.urbanoexpress.iridio3.model.entity;

import com.orm.SugarRecord;

/**
 * Created by mick on 26/07/16.
 */
public class EstadoRuta extends SugarRecord {

    private String idUsuario;
    private String idRuta;
    private String lineaNegocio;
    private String fecha;
    private String hora;
    private String gpsLatitude;
    private String gpsLongitude;
    private int tipoRuta;
    private int estado;

    private int eliminado;
    private int dataSync;

    public EstadoRuta() { }

    /*public EstadoRuta(String idUsuario, String idRuta, String lineaNegocio,
                      String fecha, String hora, String gpsLatitude,
                      String gpsLongitude, int eliminado, int estado) {
        this.idUsuario = idUsuario;
        this.idRuta = idRuta;
        this.lineaNegocio = lineaNegocio;
        this.fecha = fecha;
        this.hora = hora;
        this.gpsLatitude = gpsLatitude;
        this.gpsLongitude = gpsLongitude;
        this.estado = estado;
        this.eliminado = eliminado;
        this.dataSync = Data.Sync.PENDING;
    }*/

    public EstadoRuta(String idUsuario, String idRuta, String lineaNegocio,
                      String fecha, String hora, String gpsLatitude,
                      String gpsLongitude, int tipoRuta, int eliminado, int estado) {
        this.idUsuario = idUsuario;
        this.idRuta = idRuta;
        this.lineaNegocio = lineaNegocio;
        this.fecha = fecha;
        this.hora = hora;
        this.gpsLatitude = gpsLatitude;
        this.gpsLongitude = gpsLongitude;
        this.tipoRuta = tipoRuta;
        this.estado = estado;
        this.eliminado = eliminado;
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

    public int getTipoRuta() {
        return tipoRuta;
    }

    public void setTipoRuta(int tipoRuta) {
        this.tipoRuta = tipoRuta;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public int getEliminado() {
        return eliminado;
    }

    public void setEliminado(int eliminado) {
        this.eliminado = eliminado;
    }

    public int getDataSync() {
        return dataSync;
    }

    public void setDataSync(int dataSync) {
        this.dataSync = dataSync;
    }

    public interface Estado {
        int INICIADO    = 100;
        int FINALIZADO  = 200;
    }

    public interface TipoRuta {
        int RUTA_DEL_DIA    = 1;
        int RUTA_AUDITORIA  = 2;
    }
}
