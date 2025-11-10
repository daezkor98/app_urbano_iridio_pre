package com.urbanoexpress.iridio3.pre.ui.model;

public class PiezaItem {

    private String idPieza;
    private String idServicioGuia;
    private String barra;
    private String chk;
    private String estado;
    private String fecha;
    private boolean onMyRoute;
    private boolean selected;
    private boolean selectable;
    private boolean barcodeScanningIsMandatory;

    public PiezaItem(String idPieza, String idServicioGuia, String barra, String chk, String estado,
                     String fecha, boolean onMyRoute, boolean selected, boolean selectable, boolean barcodeScanningIsMandatory) {
        this.idPieza = idPieza;
        this.idServicioGuia = idServicioGuia;
        this.barra = barra;
        this.chk = chk;
        this.estado = estado;
        this.fecha = fecha;
        this.onMyRoute = onMyRoute;
        this.selected = selected;
        this.selectable = selectable;
        this.barcodeScanningIsMandatory = barcodeScanningIsMandatory;
    }

    public String getIdPieza() {
        return idPieza;
    }

    public void setIdPieza(String idPieza) {
        this.idPieza = idPieza;
    }

    public String getIdServicioGuia() {
        return idServicioGuia;
    }

    public void setIdServicioGuia(String idServicioGuia) {
        this.idServicioGuia = idServicioGuia;
    }

    public String getBarra() {
        return barra;
    }

    public void setBarra(String barra) {
        this.barra = barra;
    }

    public String getChk() {
        return chk;
    }

    public void setChk(String chk) {
        this.chk = chk;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public boolean isOnMyRoute() {
        return onMyRoute;
    }

    public void setOnMyRoute(boolean onMyRoute) {
        this.onMyRoute = onMyRoute;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isSelectable() {
        return selectable;
    }

    public void setSelectable(boolean selectable) {
        this.selectable = selectable;
    }

    public boolean isBarcodeScanningIsMandatory() {
        return barcodeScanningIsMandatory;
    }

    public void setBarcodeScanningIsMandatory(boolean barcodeScanningIsMandatory) {
        this.barcodeScanningIsMandatory = barcodeScanningIsMandatory;
    }
}
