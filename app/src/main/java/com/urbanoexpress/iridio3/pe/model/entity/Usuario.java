package com.urbanoexpress.iridio3.pe.model.entity;

import com.orm.SugarRecord;

import java.io.Serializable;

/**
 * Created by mick on 20/05/16.
 */
public class Usuario extends SugarRecord implements Serializable {

    private static final long serialVersionUID = -6044991214522609372L;

    private String idUsuario;
    private String usuario;
    private String nombre;
    private String tipoUsuario;
    private String codigoProvincia;
    private String nombreProvincia;
    private String siglaProvincia;
    private String perfil;
    private String tiempoRequestGPS;
    private String tiempoRequesDatos;
    private String lineaPostal;
    private String lineaValores;
    private String lineaLogistica;
    private String lineaLogisticaEspecial;
    private String devicePhone;
    private boolean menuAppAvailable;
    private String idRuta;
    private int totalNotificaciones;

    public Usuario() { }

    public Usuario(String idUsuario, String usuario, String nombre,
                   String tipoUsuario, String codigoProvincia, String nombreProvincia,
                   String siglaProvincia, String perfil, String tiempoRequestGPS,
                   String tiempoRequesDatos, String lineaPostal, String lineaValores,
                   String lineaLogistica, String lineaLogisticaEspecial, String devicePhone,
                   boolean menuAppAvailable, String idRuta, int totalNotificaciones) {
        this.idUsuario = idUsuario;
        this.usuario = usuario;
        this.nombre = nombre;
        this.tipoUsuario = tipoUsuario;
        this.codigoProvincia = codigoProvincia;
        this.nombreProvincia = nombreProvincia;
        this.siglaProvincia = siglaProvincia;
        this.perfil = perfil;
        this.tiempoRequestGPS = tiempoRequestGPS;
        this.tiempoRequesDatos = tiempoRequesDatos;
        this.lineaPostal = lineaPostal;
        this.lineaValores = lineaValores;
        this.lineaLogistica = lineaLogistica;
        this.lineaLogisticaEspecial = lineaLogisticaEspecial;
        this.devicePhone = devicePhone;
        this.menuAppAvailable = menuAppAvailable;
        this.idRuta = idRuta;
        this.totalNotificaciones = totalNotificaciones;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(String tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    public String getCodigoProvincia() {
        return codigoProvincia;
    }

    public void setCodigoProvincia(String codigoProvincia) {
        this.codigoProvincia = codigoProvincia;
    }

    public String getNombreProvincia() {
        return nombreProvincia;
    }

    public void setNombreProvincia(String nombreProvincia) {
        this.nombreProvincia = nombreProvincia;
    }

    public String getSiglaProvincia() {
        return siglaProvincia;
    }

    public void setSiglaProvincia(String siglaProvincia) {
        this.siglaProvincia = siglaProvincia;
    }

    public String getPerfil() {
        return perfil;
    }

    public void setPerfil(String perfil) {
        this.perfil = perfil;
    }

    public String getTiempoRequestGPS() {
        return tiempoRequestGPS;
    }

    public void setTiempoRequestGPS(String tiempoRequestGPS) {
        this.tiempoRequestGPS = tiempoRequestGPS;
    }

    public String getTiempoRequesDatos() {
        return tiempoRequesDatos;
    }

    public void setTiempoRequesDatos(String tiempoRequesDatos) {
        this.tiempoRequesDatos = tiempoRequesDatos;
    }

    public String getLineaPostal() {
        return lineaPostal;
    }

    public void setLineaPostal(String lineaPostal) {
        this.lineaPostal = lineaPostal;
    }

    public String getLineaValores() {
        return lineaValores;
    }

    public void setLineaValores(String lineaValores) {
        this.lineaValores = lineaValores;
    }

    public String getLineaLogistica() {
        return lineaLogistica;
    }

    public void setLineaLogistica(String lineaLogistica) {
        this.lineaLogistica = lineaLogistica;
    }

    public String getLineaLogisticaEspecial() {
        return lineaLogisticaEspecial;
    }

    public void setLineaLogisticaEspecial(String lineaLogisticaEspecial) {
        this.lineaLogisticaEspecial = lineaLogisticaEspecial;
    }

    public String getDevicePhone() {
        return devicePhone;
    }

    public void setDevicePhone(String devicePhone) {
        this.devicePhone = devicePhone;
    }

    public boolean isMenuAppAvailable() {
        return menuAppAvailable;
    }

    public void setMenuAppAvailable(boolean menuAppAvailable) {
        this.menuAppAvailable = menuAppAvailable;
    }

    public String getIdRuta() {
        return idRuta;
    }

    public void setIdRuta(String idRuta) {
        this.idRuta = idRuta;
    }

    public int getTotalNotificaciones() {
        return totalNotificaciones;
    }

    public void setTotalNotificaciones(int totalNotificaciones) {
        this.totalNotificaciones = totalNotificaciones;
    }
}
