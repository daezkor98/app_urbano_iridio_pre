package com.urbanoexpress.iridio3.pe.model.entity;

import com.orm.SugarRecord;

import java.io.Serializable;

public class Auditoria extends SugarRecord implements Serializable {

    private static final long serialVersionUID = 882250563614206816L;

    private String idUsuario;
    private String idServicio; // man_id_det/
    private String idMotivo;
    private String idGuia;
    private String idReclamo;
    private String idCliente;
    private String idManifiesto;
    private String lineaNegocio;
    private String shiCodigo;
    private String fechaRuta;
    private String guia;
    private String barraReclamo;
    private String secuencia;
    private String direccion;
    private String gpsLatitude;
    private String gpsLongitude;
    private String distrito;
    private String shipper;
    private String contacto;
    private String piezas;
    private String telefono;
    private String celular;

    private String idImagen;

    private int estadoDescarga;
    private int resultadoGestion;
    private int eliminado;

    /**
     * Variable para validar si los datos estan completos. Ejm:
     * En caso de que la guia(manifiesto) se generó desde el celular.
     */
    private int dataValidate;

    public Auditoria() { }

    public Auditoria(String idUsuario, String idServicio, String idMotivo, String idGuia,
                     String idReclamo, String idCliente, String idManifiesto, String lineaNegocio,
                     String shiCodigo, String fechaRuta, String guia, String barraReclamo,
                     String secuencia, String direccion, String gpsLatitude, String gpsLongitude,
                     String distrito, String shipper, String contacto, String piezas, String telefono,
                     String celular, int estadoDescarga, int resultadoGestion, int eliminado,
                     int dataValidate) {
        this.idUsuario = idUsuario;
        this.idServicio = idServicio;
        this.idMotivo = idMotivo;
        this.idGuia = idGuia;
        this.idReclamo = idReclamo;
        this.idCliente = idCliente;
        this.idManifiesto = idManifiesto;
        this.lineaNegocio = lineaNegocio;
        this.shiCodigo = shiCodigo;
        this.fechaRuta = fechaRuta;
        this.guia = guia;
        this.barraReclamo = barraReclamo;
        this.secuencia = secuencia;
        this.direccion = direccion;
        this.gpsLatitude = gpsLatitude;
        this.gpsLongitude = gpsLongitude;
        this.distrito = distrito;
        this.shipper = shipper;
        this.contacto = contacto;
        this.piezas = piezas;
        this.telefono = telefono;
        this.celular = celular;
        this.estadoDescarga = estadoDescarga;
        this.resultadoGestion = resultadoGestion;
        this.eliminado = eliminado;
        this.dataValidate = dataValidate;
        this.idImagen = "";
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getIdServicio() {
        return idServicio;
    }

    public void setIdServicio(String idServicio) {
        this.idServicio = idServicio;
    }

    public String getIdMotivo() {
        return idMotivo;
    }

    public void setIdMotivo(String idMotivo) {
        this.idMotivo = idMotivo;
    }

    public String getIdGuia() {
        return idGuia;
    }

    public void setIdGuia(String idGuia) {
        this.idGuia = idGuia;
    }

    public String getIdReclamo() {
        return idReclamo;
    }

    public void setIdReclamo(String idReclamo) {
        this.idReclamo = idReclamo;
    }

    public String getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
    }

    public String getIdManifiesto() {
        return idManifiesto;
    }

    public void setIdManifiesto(String idManifiesto) {
        this.idManifiesto = idManifiesto;
    }

    public String getLineaNegocio() {
        return lineaNegocio;
    }

    public void setLineaNegocio(String lineaNegocio) {
        this.lineaNegocio = lineaNegocio;
    }

    public String getShiCodigo() {
        return shiCodigo;
    }

    public void setShiCodigo(String shiCodigo) {
        this.shiCodigo = shiCodigo;
    }

    public String getFechaRuta() {
        return fechaRuta;
    }

    public void setFechaRuta(String fechaRuta) {
        this.fechaRuta = fechaRuta;
    }

    public String getGuia() {
        return guia;
    }

    public void setGuia(String guia) {
        this.guia = guia;
    }

    public String getBarraReclamo() {
        return barraReclamo;
    }

    public void setBarraReclamo(String barraReclamo) {
        this.barraReclamo = barraReclamo;
    }

    public String getSecuencia() {
        return secuencia;
    }

    public void setSecuencia(String secuencia) {
        this.secuencia = secuencia;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getGpsLatitude() {
        return gpsLatitude;
    }

    public void setGpsLatitude(String gpsLatitude) {
        this.gpsLatitude = gpsLatitude;
    }

    public String getGpsLongitude() {
        return gpsLongitude;
    }

    public void setGpsLongitude(String gpsLongitude) {
        this.gpsLongitude = gpsLongitude;
    }

    public String getDistrito() {
        return distrito;
    }

    public void setDistrito(String distrito) {
        this.distrito = distrito;
    }

    public String getShipper() {
        return shipper;
    }

    public void setShipper(String shipper) {
        this.shipper = shipper;
    }

    public String getContacto() {
        return contacto;
    }

    public void setContacto(String contacto) {
        this.contacto = contacto;
    }

    public String getPiezas() {
        return piezas;
    }

    public void setPiezas(String piezas) {
        this.piezas = piezas;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getIdImagen() {
        return idImagen;
    }

    public void setIdImagen(String idImagen) {
        this.idImagen = idImagen;
    }

    public int getEstadoDescarga() {
        return estadoDescarga;
    }

    public void setEstadoDescarga(int estadoDescarga) {
        this.estadoDescarga = estadoDescarga;
    }

    public int getResultadoGestion() {
        return resultadoGestion;
    }

    public void setResultadoGestion(int resultadoGestion) {
        this.resultadoGestion = resultadoGestion;
    }

    public int getEliminado() {
        return eliminado;
    }

    public void setEliminado(int eliminado) {
        this.eliminado = eliminado;
    }

    public int getDataValidate() {
        return dataValidate;
    }

    public void setDataValidate(int dataValidate) {
        this.dataValidate = dataValidate;
    }

    public interface Tipo {
        int ENTREGA     = 0;
        int RECOLECCION = 1;
        int NO_DEFINIDO = 2;
    }

    public interface EstadoDescarga {
        int PENDIENTE   = 10;
        int GESTIONADO  = 20;
    }

    public interface ResultadoGestion {
        int NO_DEFINIDO = 0; // Inicializador
        int EFECTIVA    = 1; // Entregado, Recolectado
        int NO_EFECTIVA = 2; // No Entregado, No Recolectado
    }
}