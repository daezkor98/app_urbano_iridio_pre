package com.urbanoexpress.iridio3.pe.model.entity;


import com.orm.SugarRecord;
import com.orm.dsl.Column;

/**
 * Created by Brandon Quintanilla on Marzo/05/2025.
 */
public class GrupoMotivo extends SugarRecord {

    @Column(name = "GRU_ID")
    public int idGrupoMotivo;
    @Column(name = "GRU_DESCRI")
    public String desGrupoMotivo;

    public GrupoMotivo() {
    }
    public GrupoMotivo(int idGrupoMotivo, String desGrupoMotivo) {
        this.idGrupoMotivo = idGrupoMotivo;
        this.desGrupoMotivo = desGrupoMotivo;
    }
}

