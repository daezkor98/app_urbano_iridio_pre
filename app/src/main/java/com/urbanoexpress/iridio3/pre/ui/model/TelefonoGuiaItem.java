package com.urbanoexpress.iridio3.pre.ui.model;

import com.urbanoexpress.iridio3.pre.ui.adapter.TelefonoGuiaV2Adapter;

/**
 * Created by mick on 30/06/17.
 */

public class TelefonoGuiaItem implements TelefonoGuiaV2Adapter.WrapperItem {

    private String telefono;
    private String contacto;

    public TelefonoGuiaItem(String telefono) {
        this.telefono = telefono;
        this.contacto = "";
    }

    public TelefonoGuiaItem(String telefono, String contacto) {
        this.telefono = telefono;
        this.contacto = contacto;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getContacto() {
        return contacto;
    }

    public void setContacto(String contacto) {
        this.contacto = contacto;
    }

    @Override
    public int getType() {
        return TelefonoGuiaV2Adapter.WrapperItem.TYPE_PHONE;
    }
}