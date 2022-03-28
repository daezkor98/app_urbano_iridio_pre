package com.urbanoexpress.iridio3.model.entity;

import com.orm.SugarRecord;

/**
 * Created by mick on 02/09/17.
 */

public class TipoDireccion extends SugarRecord {

    private String idUsuario;
    private String idTipoDirecion;
    private String descripcion;

    public TipoDireccion() { }

    public TipoDireccion(String idUsuario, String idTipoDirecion, String descripcion) {
        this.idUsuario = idUsuario;
        this.idTipoDirecion = idTipoDirecion;
        this.descripcion = descripcion;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getIdTipoDirecion() {
        return idTipoDirecion;
    }

    public void setIdTipoDirecion(String idTipoDirecion) {
        this.idTipoDirecion = idTipoDirecion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
