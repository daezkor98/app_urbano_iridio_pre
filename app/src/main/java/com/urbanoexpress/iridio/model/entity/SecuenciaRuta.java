package com.urbanoexpress.iridio.model.entity;

import com.orm.SugarRecord;

/**
 * Created by mick on 18/10/16.
 */

public class SecuenciaRuta extends SugarRecord {

    private String idUsuario;
    private int eliminado;

    public SecuenciaRuta() { }

    public SecuenciaRuta(String idUsuario, int eliminado) {
        this.idUsuario = idUsuario;
        this.eliminado = eliminado;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getEliminado() {
        return eliminado;
    }

    public void setEliminado(int eliminado) {
        this.eliminado = eliminado;
    }
}
