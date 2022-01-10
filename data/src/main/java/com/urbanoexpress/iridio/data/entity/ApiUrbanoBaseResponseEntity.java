package com.urbanoexpress.iridio.data.entity;

import com.google.gson.annotations.SerializedName;

public class ApiUrbanoBaseResponseEntity<Data> {

    @SerializedName("success")
    private boolean success;

    @SerializedName("msg_error")
    private String errorMessage;

    @SerializedName("code_error")
    private String errorCode;

    @SerializedName("data")
    private Data data;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }
}
