package com.urbanoexpress.iridio3.pe.ui.model;

/**
 * Created by mick on 18/01/17.
 */

public class PaisItem {

    private String nombre;
    private String iso;
    private int icon;
    private int backgroundColor;
    private boolean selected;

    public PaisItem(String nombre, String iso, int icon,
                    int backgroundColor, boolean selected) {
        this.nombre = nombre;
        this.iso = iso;
        this.icon = icon;
        this.backgroundColor = backgroundColor;
        this.selected = selected;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getIso() {
        return iso;
    }

    public void setIso(String iso) {
        this.iso = iso;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

}
