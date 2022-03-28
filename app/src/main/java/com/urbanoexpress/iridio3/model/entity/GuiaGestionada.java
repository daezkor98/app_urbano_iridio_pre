package com.urbanoexpress.iridio3.model.entity;

import com.orm.SugarRecord;

/**
 * Created by mick on 12/07/16.
 */
public class GuiaGestionada extends SugarRecord {

    private String idUsuario;
    private String idServicio;
    private String idMotivo;
    private int tipoZona;
    private String tipoGuia;
    private String lineaNegocio;
    private String fecha;
    private String hora;
    private String gpsLatitude;
    private String gpslongitude;
    private String nombre;
    private String dni;
    private String numVaucherPOS;
    private String piezas;
    private String peso;
    private String recoleccion;
    private String guiaElectronica;
    private String tipoDireccion;
    private String tipoMedioPago;
    private String comentario;
    private String idMotivoObservacionEntrega;
    private String comentarioObservacionEntrega;

    /**
     * Contador del numero de veces de gestionado la ruta
     */
    private int numVecesGestionado;

    private int dataSync;
    private int eliminado;

    /**
     * Variable para validar si los datos estan completos. Ejm:
     * En caso de que la guia(manifiesto) se generó desde el celular.
     */
    private int dataValidate;

    public GuiaGestionada() { }

    public GuiaGestionada(String idUsuario, String idServicio, String idMotivo, int tipoZona,
                          String tipoGuia, String lineaNegocio, String fecha, String hora,
                          String gpsLatitude, String gpslongitude, String nombre, String dni,
                          String numVaucherPOS, String piezas, String peso, String recoleccion,
                          String guiaElectronica, String tipoDireccion, String tipoMedioPago,
                          String comentario, String idMotivoObservacionEntrega,
                          String comentarioObservacionEntrega, int eliminado, int dataValidate,
                          int numVecesGestionado) {
        this.idUsuario = idUsuario;
        this.idServicio = idServicio;
        this.idMotivo = idMotivo;
        this.tipoZona = tipoZona;
        this.tipoGuia = tipoGuia;
        this.lineaNegocio = lineaNegocio;
        this.fecha = fecha;
        this.hora = hora;
        this.gpsLatitude = gpsLatitude;
        this.gpslongitude = gpslongitude;
        this.nombre = nombre;
        this.dni = dni;
        this.numVaucherPOS = numVaucherPOS;
        this.piezas = piezas;
        this.peso = peso;
        this.recoleccion = recoleccion;
        this.guiaElectronica = guiaElectronica;
        this.tipoDireccion = tipoDireccion;
        this.tipoMedioPago = tipoMedioPago;
        this.comentario = comentario;
        this.idMotivoObservacionEntrega = idMotivoObservacionEntrega;
        this.comentarioObservacionEntrega = comentarioObservacionEntrega;
        this.eliminado = eliminado;
        this.dataValidate = dataValidate;
        this.numVecesGestionado = numVecesGestionado;
        this.dataSync = Data.Sync.PENDING;
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

    public int getTipoZona() {
        return tipoZona;
    }

    public void setTipoZona(int tipoZona) {
        this.tipoZona = tipoZona;
    }

    public String getTipoGuia() {
        return tipoGuia;
    }

    public void setTipoGuia(String tipoGuia) {
        this.tipoGuia = tipoGuia;
    }

    public String getLineaNegocio() {
        return lineaNegocio;
    }

    public void setLineaNegocio(String lineaNegocio) {
        this.lineaNegocio = lineaNegocio;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getGpsLatitude() {
        return gpsLatitude;
    }

    public void setGpsLatitude(String gpsLatitude) {
        this.gpsLatitude = gpsLatitude;
    }

    public String getGpslongitude() {
        return gpslongitude;
    }

    public void setGpslongitude(String gpslongitude) {
        this.gpslongitude = gpslongitude;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNumVaucherPOS() {
        return numVaucherPOS;
    }

    public void setNumVaucherPOS(String numVaucherPOS) {
        this.numVaucherPOS = numVaucherPOS;
    }

    public String getPiezas() {
        return piezas;
    }

    public void setPiezas(String piezas) {
        this.piezas = piezas;
    }

    public String getPeso() {
        return peso;
    }

    public void setPeso(String peso) {
        this.peso = peso;
    }

    public String getRecoleccion() {
        return recoleccion;
    }

    public void setRecoleccion(String recoleccion) {
        this.recoleccion = recoleccion;
    }

    public String getGuiaElectronica() {
        return guiaElectronica;
    }

    public void setGuiaElectronica(String guiaElectronica) {
        this.guiaElectronica = guiaElectronica;
    }

    public String getTipoDireccion() {
        return tipoDireccion;
    }

    public void setTipoDireccion(String tipoDireccion) {
        this.tipoDireccion = tipoDireccion;
    }

    public String getTipoMedioPago() {
        return tipoMedioPago;
    }

    public void setTipoMedioPago(String tipoMedioPago) {
        this.tipoMedioPago = tipoMedioPago;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public String getIdMotivoObservacionEntrega() {
        return idMotivoObservacionEntrega;
    }

    public void setIdMotivoObservacionEntrega(String idMotivoObservacionEntrega) {
        this.idMotivoObservacionEntrega = idMotivoObservacionEntrega;
    }

    public String getComentarioObservacionEntrega() {
        return comentarioObservacionEntrega;
    }

    public void setComentarioObservacionEntrega(String comentarioObservacionEntrega) {
        this.comentarioObservacionEntrega = comentarioObservacionEntrega;
    }

    public int getNumVecesGestionado() {
        return numVecesGestionado;
    }

    public void setNumVecesGestionado(int numVecesGestionado) {
        this.numVecesGestionado = numVecesGestionado;
    }

    public int getDataSync() {
        return dataSync;
    }

    public void setDataSync(int dataSync) {
        this.dataSync = dataSync;
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

    public interface Recoleccion {
        int SIN_GUIA                = 1;
        int GUIA_ELECTRONICA        = 2;
        int GUIA_MANUAL             = 3;
        int NO_EFECTIVA             = 4;
        int LLEGADA_PUNTO_RECOJO    = 5;
        int LOGISTICA_INVERSA       = 6;
        int VALIJA                  = 7;
        int GUIA_NO_RECOLECTADA     = 10;
        int FORMULARIO_RECOLECCION  = 11;
        int RECOLECCION_GUIA_VALIJA = 12;
        int CONTENEDOR              = 13;
    }
}
