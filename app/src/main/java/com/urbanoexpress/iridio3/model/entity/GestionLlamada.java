package com.urbanoexpress.iridio3.model.entity;

import com.orm.SugarRecord;

/**
 * Created by mick on 28/05/18.
 */
public class GestionLlamada extends SugarRecord {

    private String idUsuario;
    private String idServicio;
    private String idMotivo;
    private String telefono;
    private String fechaLlamada; // long
    private String tiempoLlamada; // long
    private String lineaNegocio;

    private int dataSync;

    public GestionLlamada() {}

    public GestionLlamada(String idUsuario, String idServicio, String idMotivo,
                          String telefono, String fechaLlamada, String tiempoLlamada,
                          String lineaNegocio, int dataSync) {
        this.idUsuario = idUsuario;
        this.idServicio = idServicio;
        this.idMotivo = idMotivo;
        this.telefono = telefono;
        this.fechaLlamada = fechaLlamada;
        this.tiempoLlamada = tiempoLlamada;
        this.lineaNegocio = lineaNegocio;
        this.dataSync = dataSync;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getIdServicio() {
        return idServicio;
    }

    public void setIdServicio(String idServicio) {
        this.idServicio = idServicio;
    }

    public String getIdMotivo() {
        return idMotivo;
    }

    public void setIdMotivo(String idMotivo) {
        this.idMotivo = idMotivo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getFechaLlamada() {
        return fechaLlamada;
    }

    public void setFechaLlamada(String fechaLlamada) {
        this.fechaLlamada = fechaLlamada;
    }

    public String getTiempoLlamada() {
        return tiempoLlamada;
    }

    public void setTiempoLlamada(String tiempoLlamada) {
        this.tiempoLlamada = tiempoLlamada;
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
}
