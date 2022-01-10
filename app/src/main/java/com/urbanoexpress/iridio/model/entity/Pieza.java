package com.urbanoexpress.iridio.model.entity;

import com.orm.SugarRecord;

import java.io.Serializable;

public class Pieza extends SugarRecord implements Serializable {

    private static final long serialVersionUID = 5362776448971928368L;

    private String idUsuario;
    private String idServicioGuia;
    private String idPieza;
    private String barra;
    private String chkEstado;
    private String descripcionEstado;
    private String fechaEstado;
    private String lineaNegocio;
    private int estadoManifiesto;

    public Pieza() { }

    public Pieza(String idUsuario, String idServicioGuia, String idPieza, String barra,
                 String chkEstado, String descripcionEstado, String fechaEstado,
                 String lineaNegocio, int estadoManifiesto) {
        this.idUsuario = idUsuario;
        this.idServicioGuia = idServicioGuia;
        this.idPieza = idPieza;
        this.barra = barra;
        this.chkEstado = chkEstado;
        this.descripcionEstado = descripcionEstado;
        this.fechaEstado = fechaEstado;
        this.lineaNegocio = lineaNegocio;
        this.estadoManifiesto = estadoManifiesto;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getIdServicioGuia() {
        return idServicioGuia;
    }

    public void setIdServicioGuia(String idServicioGuia) {
        this.idServicioGuia = idServicioGuia;
    }

    public String getIdPieza() {
        return idPieza;
    }

    public void setIdPieza(String idPieza) {
        this.idPieza = idPieza;
    }

    public String getBarra() {
        return barra;
    }

    public void setBarra(String barra) {
        this.barra = barra;
    }

    public String getChkEstado() {
        return chkEstado;
    }

    public void setChkEstado(String chkEstado) {
        this.chkEstado = chkEstado;
    }

    public String getDescripcionEstado() {
        return descripcionEstado;
    }

    public void setDescripcionEstado(String descripcionEstado) {
        this.descripcionEstado = descripcionEstado;
    }

    public String getFechaEstado() {
        return fechaEstado;
    }

    public void setFechaEstado(String fechaEstado) {
        this.fechaEstado = fechaEstado;
    }

    public String getLineaNegocio() {
        return lineaNegocio;
    }

    public void setLineaNegocio(String lineaNegocio) {
        this.lineaNegocio = lineaNegocio;
    }

    public int getEstadoManifiesto() {
        return estadoManifiesto;
    }

    public void setEstadoManifiesto(int estadoManifiesto) {
        this.estadoManifiesto = estadoManifiesto;
    }
}
