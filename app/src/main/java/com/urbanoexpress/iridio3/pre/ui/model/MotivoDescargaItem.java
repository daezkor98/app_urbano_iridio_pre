package com.urbanoexpress.iridio3.pre.ui.model;

/**
 * Created by mick on 14/07/16.
 */
public class MotivoDescargaItem {

    private String descripcion;
    private boolean selected;

    private String id;

    public MotivoDescargaItem(String descripcion, boolean selected) {
        this.descripcion = descripcion;
        this.selected = selected;
    }

    public MotivoDescargaItem(String id, String descripcion, boolean selected) {
        this.descripcion = descripcion;
        this.selected = selected;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
