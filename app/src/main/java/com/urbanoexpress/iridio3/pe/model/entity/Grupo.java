package com.urbanoexpress.iridio3.pe.model.entity;


import com.orm.SugarRecord;

import java.util.List;

/**
 * Created by Brandon Quintanilla on Marzo/05/2025.
 */
public class Grupo extends SugarRecord {
    public int gru_id;
    public String gru_descri;

    public Grupo() {
    }
    public Grupo(int gru_id, String gru_descri) {
        this.gru_id = gru_id;
        this.gru_descri = gru_descri;
    }
}

