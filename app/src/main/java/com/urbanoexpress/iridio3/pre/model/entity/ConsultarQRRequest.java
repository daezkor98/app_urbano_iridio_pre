package com.urbanoexpress.iridio3.pre.model.entity;

import com.google.gson.annotations.SerializedName;

public class ConsultarQRRequest {

    @SerializedName("rq_id_code")
    private String rqIdCode;

    public ConsultarQRRequest(String rqIdCode) {
        this.rqIdCode = rqIdCode;
    }
}
