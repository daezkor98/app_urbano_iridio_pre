package com.urbanoexpress.iridio3.pre.ui.model;

import com.urbanoexpress.iridio3.pre.model.entity.Ruta;

import java.util.ArrayList;
import java.util.List;

public class ParadaRutaItem {
    private String idParada;
    private String secuenciaParada;
    private String idServicio;
    private String idManifiesto;
    private String guia;
    private String distrito;
    private String direccion;
    private String horaLlegadaEstimada;
    private String tipoRuta;
    private String counterItem;
    private String simboloMoneda;
    private int icon;
    private int idResIcon;
    private int iconTipoEnvio;
    private int backgroundColor;
    private int lblColorHorario;
    private int gestionEfectiva;
    private boolean showIconGestionGuia;
    private boolean showCounterItem;
    private boolean showTipoEnvio;
    private boolean showImportePorCobrar;
    private boolean selected;
    private List<RutaItem> guias;

    public ParadaRutaItem() {
        this.guias = new ArrayList<>();
        this.selected = false;
    }

    public String getIdParada() {
        return idParada;
    }

    public void setIdParada(String idParada) {
        this.idParada = idParada;
    }

    public String getSecuenciaParada() {
        return secuenciaParada;
    }

    public void setSecuenciaParada(String secuenciaParada) {
        this.secuenciaParada = secuenciaParada;
    }

    public String getIdServicio() {
        return idServicio;
    }

    public void setIdServicio(String idServicio) {
        this.idServicio = idServicio;
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

    public String getSimboloMoneda() {
        return simboloMoneda;
    }

    public void setSimboloMoneda(String simboloMoneda) {
        this.simboloMoneda = simboloMoneda;
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

    public int getLblColorHorario() {
        return lblColorHorario;
    }

    public void setLblColorHorario(int lblColorHorario) {
        this.lblColorHorario = lblColorHorario;
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

    public List<RutaItem> getGuias() {
        return guias;
    }

    public void setGuias(List<RutaItem> guias) {
        this.guias = guias;
    }

    public void addGuia(RutaItem guia) {
        if (guias == null) guias = new ArrayList<>();
        guias.add(guia);
    }
}