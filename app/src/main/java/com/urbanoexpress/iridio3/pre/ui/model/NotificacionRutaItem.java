package com.urbanoexpress.iridio3.pre.ui.model;

public class NotificacionRutaItem {

    private String idNotificacion;
    private String idServicioNotificacion;
    private String guiaElectronica;
    private String titulo;
    private String mensaje;
    private String fechaCHK;
    private String lineaNegocio;
    private String gestion;
    private int totalLeido;
    private int backgroundColor;
    private int bgIcon;
    private int iconNotify;

    public NotificacionRutaItem(String idNotificacion, String idServicioNotificacion, String guiaElectronica,
                                String titulo, String mensaje, String fechaCHK, String lineaNegocio,
                                String gestion, int totalLeido, int backgroundColor, int bgIcon, int iconNotify) {
        this.idNotificacion = idNotificacion;
        this.idServicioNotificacion = idServicioNotificacion;
        this.guiaElectronica = guiaElectronica;
        this.titulo = titulo;
        this.mensaje = mensaje;
        this.fechaCHK = fechaCHK;
        this.lineaNegocio = lineaNegocio;
        this.gestion = gestion;
        this.totalLeido = totalLeido;
        this.backgroundColor = backgroundColor;
        this.bgIcon = bgIcon;
        this.iconNotify = iconNotify;
    }

    public String getIdNotificacion() {
        return idNotificacion;
    }

    public void setIdNotificacion(String idNotificacion) {
        this.idNotificacion = idNotificacion;
    }

    public String getIdServicioNotificacion() {
        return idServicioNotificacion;
    }

    public void setIdServicioNotificacion(String idServicioNotificacion) {
        this.idServicioNotificacion = idServicioNotificacion;
    }

    public String getGuiaElectronica() {
        return guiaElectronica;
    }

    public void setGuiaElectronica(String guiaElectronica) {
        this.guiaElectronica = guiaElectronica;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getFechaCHK() {
        return fechaCHK;
    }

    public void setFechaCHK(String fechaCHK) {
        this.fechaCHK = fechaCHK;
    }

    public String getLineaNegocio() {
        return lineaNegocio;
    }

    public void setLineaNegocio(String lineaNegocio) {
        this.lineaNegocio = lineaNegocio;
    }

    public String getGestion() {
        return gestion;
    }

    public void setGestion(String gestion) {
        this.gestion = gestion;
    }

    public int getTotalLeido() {
        return totalLeido;
    }

    public void setTotalLeido(int totalLeido) {
        this.totalLeido = totalLeido;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public int getBgIcon() {
        return bgIcon;
    }

    public void setBgIcon(int bgIcon) {
        this.bgIcon = bgIcon;
    }

    public int getIconNotify() {
        return iconNotify;
    }

    public void setIconNotify(int iconNotify) {
        this.iconNotify = iconNotify;
    }
}
