package com.urbanoexpress.iridio.ui.model;

/**
 * Created by mick on 14/07/16.
 */
public class MotivoDescargaItem {

    private String descripcion;
    private boolean selected;

    public MotivoDescargaItem(String descripcion, boolean selected) {
        this.descripcion = descripcion;
        this.selected = selected;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
