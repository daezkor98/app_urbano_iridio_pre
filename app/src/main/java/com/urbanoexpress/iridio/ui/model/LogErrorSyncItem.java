package com.urbanoexpress.iridio.ui.model;

/**
 * Created by mick on 13/10/16.
 */

public class LogErrorSyncItem {

    private String titulo;
    private String mensaje;
    private String fecha;
    private int resIcon;

    public LogErrorSyncItem(String titulo, String mensaje,
                            String fecha, int resIcon) {
        this.titulo = titulo;
        this.mensaje = mensaje;
        this.fecha = fecha;
        this.resIcon = resIcon;
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

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public int getResIcon() {
        return resIcon;
    }

    public void setResIcon(int resIcon) {
        this.resIcon = resIcon;
    }
}
