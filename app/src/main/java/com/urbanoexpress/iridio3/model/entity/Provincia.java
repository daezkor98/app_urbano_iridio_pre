package com.urbanoexpress.iridio3.model.entity;

import com.orm.SugarRecord;

import java.io.Serializable;

/**
 * Created by mick on 14/03/17.
 */

public class Provincia extends SugarRecord implements Serializable {

    private String codProvincia;
    private String siglaProvincia;
    private String nombre;
    private String mainProvincia;
    private String tipo;

    public Provincia() { }

    public Provincia(String codProvincia, String siglaProvincia, String nombre,
                     String mainProvincia, String tipo) {
        this.codProvincia = codProvincia;
        this.siglaProvincia = siglaProvincia;
        this.nombre = nombre;
        this.mainProvincia = mainProvincia;
        this.tipo = tipo;
    }

    public String getCodProvincia() {
        return codProvincia;
    }

    public void setCodProvincia(String codProvincia) {
        this.codProvincia = codProvincia;
    }

    public String getSiglaProvincia() {
        return siglaProvincia;
    }

    public void setSiglaProvincia(String siglaProvincia) {
        this.siglaProvincia = siglaProvincia;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getMainProvincia() {
        return mainProvincia;
    }

    public void setMainProvincia(String mainProvincia) {
        this.mainProvincia = mainProvincia;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
