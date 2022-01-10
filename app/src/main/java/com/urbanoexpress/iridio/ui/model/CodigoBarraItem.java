package com.urbanoexpress.iridio.ui.model;

/**
 * Created by mick on 12/08/16.
 */

public class CodigoBarraItem {

    private String codigo;
    private int icon;
    private int backgroundColor;
    private boolean selected;

    public CodigoBarraItem(String codigo, int icon, int backgroundColor, boolean selected) {
        this.codigo = codigo;
        this.icon = icon;
        this.backgroundColor = backgroundColor;
        this.selected = selected;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
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
