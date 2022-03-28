package com.urbanoexpress.iridio3.ui.model;

import java.io.Serializable;

public class AuditoriaItem implements Serializable {

    private String idRuta;
    private String idManifiesto;
    private String guia;
    private String distrito;
    private String direccion;
    private String horaLlegadaEstimada;
    private String tipoRuta;
    private String counterItem;
    private int icon;
    private int idResIcon;
    private int iconTipoEnvio;
    private int backgroundColor;
    private int gestionEfectiva;
    private boolean showIconGestionGuia;
    private boolean showCounterItem;
    private boolean showTipoEnvio;
    private boolean showImportePorCobrar;
    private boolean selected;

    public AuditoriaItem(String idRuta, String idManifiesto, String guia,
                    String distrito, String direccion, String horaLlegadaEstimada,
                    String tipoRuta, String counterItem,
                    int icon, int idResIcon, int iconTipoEnvio, int backgroundColor,
                    int gestionEfectiva, boolean showIconGestionGuia, boolean showCounterItem,
                    boolean showTipoEnvio, boolean showImportePorCobrar, boolean selected) {
        this.idRuta = idRuta;
        this.idManifiesto = idManifiesto;
        this.guia = guia;
        this.distrito = distrito;
        this.direccion = direccion;
        this.horaLlegadaEstimada = horaLlegadaEstimada;
        this.tipoRuta = tipoRuta;
        this.counterItem = counterItem;
        this.icon = icon;
        this.idResIcon = idResIcon;
        this.iconTipoEnvio = iconTipoEnvio;
        this.backgroundColor = backgroundColor;
        this.gestionEfectiva = gestionEfectiva;
        this.showIconGestionGuia = showIconGestionGuia;
        this.showCounterItem = showCounterItem;
        this.showTipoEnvio = showTipoEnvio;
        this.showImportePorCobrar = showImportePorCobrar;
        this.selected = selected;
    }

    public String getIdRuta() {
        return idRuta;
    }

    public void setIdRuta(String idRuta) {
        this.idRuta = idRuta;
    }

    public String getIdManifiesto() {
        return idManifiesto;
    }

    public void setIdManifiesto(String idManifiesto) {
        this.idManifiesto = idManifiesto;
    }

    public String getGuia() {
        return guia;
    }

    public void setGuia(String guia) {
        this.guia = guia;
    }

    public String getDistrito() {
        return distrito;
    }

    public void setDistrito(String distrito) {
        this.distrito = distrito;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getHoraLlegadaEstimada() {
        return horaLlegadaEstimada;
    }

    public void setHoraLlegadaEstimada(String horaLlegadaEstimada) {
        this.horaLlegadaEstimada = horaLlegadaEstimada;
    }

    public String getTipoRuta() {
        return tipoRuta;
    }

    public void setTipoRuta(String tipoRuta) {
        this.tipoRuta = tipoRuta;
    }

    public String getCounterItem() {
        return counterItem;
    }

    public void setCounterItem(String counterItem) {
        this.counterItem = counterItem;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public int getIdResIcon() {
        return idResIcon;
    }

    public void setIdResIcon(int idResIcon) {
        this.idResIcon = idResIcon;
    }

    public int getIconTipoEnvio() {
        return iconTipoEnvio;
    }

    public void setIconTipoEnvio(int iconTipoEnvio) {
        this.iconTipoEnvio = iconTipoEnvio;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public int getGestionEfectiva() {
        return gestionEfectiva;
    }

    public void setGestionEfectiva(int gestionEfectiva) {
        this.gestionEfectiva = gestionEfectiva;
    }

    public boolean isShowIconGestionGuia() {
        return showIconGestionGuia;
    }

    public void setShowIconGestionGuia(boolean showIconGestionGuia) {
        this.showIconGestionGuia = showIconGestionGuia;
    }

    public boolean isShowCounterItem() {
        return showCounterItem;
    }

    public void setShowCounterItem(boolean showCounterItem) {
        this.showCounterItem = showCounterItem;
    }

    public boolean isShowTipoEnvio() {
        return showTipoEnvio;
    }

    public void setShowTipoEnvio(boolean showTipoEnvio) {
        this.showTipoEnvio = showTipoEnvio;
    }

    public boolean isShowImportePorCobrar() {
        return showImportePorCobrar;
    }

    public void setShowImportePorCobrar(boolean showImportePorCobrar) {
        this.showImportePorCobrar = showImportePorCobrar;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}