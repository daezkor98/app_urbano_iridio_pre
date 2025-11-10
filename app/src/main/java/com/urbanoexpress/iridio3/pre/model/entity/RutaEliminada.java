package com.urbanoexpress.iridio3.pre.model.entity;

import com.orm.SugarRecord;

/**
 * Created by mick on 27/07/16.
 */
public class RutaEliminada extends SugarRecord {

    private String idUsuario;
    private String idServicio;

    private int dataSync;

    public RutaEliminada() { }

    public RutaEliminada(String idUsuario, String idServicio) {
        this.idUsuario = idUsuario;
        this.idServicio = idServicio;
        this.dataSync = Data.Sync.PENDING;
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

    public int getDataSync() {
        return dataSync;
    }

    public void setDataSync(int dataSync) {
        this.dataSync = dataSync;
    }

}
