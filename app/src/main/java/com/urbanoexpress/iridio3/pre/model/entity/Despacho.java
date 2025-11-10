package com.urbanoexpress.iridio3.pre.model.entity;

import com.orm.SugarRecord;

/**
 * Created by mick on 02/06/16.
 */
public class Despacho extends SugarRecord {

    private String idUsuario;
    private String idDespacho;
    private String idParada;
    private String idOrigen;
    private String idDestino;
    private String origen;
    private String destino;
    private String piezas;
    private String guias;
    private String estado;
    private int tipoDespacho;
    private int procesoDespacho;

    public Despacho() { }

    public Despacho(String idUsuario, String idDespacho, String idParada,
                    String idOrigen, String idDestino, String origen,
                    String destino, String piezas, String guias,
                    String estado, int tipoDespacho, int procesoDespacho) {
        this.idUsuario = idUsuario;
        this.idDespacho = idDespacho;
        this.idParada = idParada;
        this.idOrigen = idOrigen;
        this.idDestino = idDestino;
        this.origen = origen;
        this.destino = destino;
        this.piezas = piezas;
        this.guias = guias;
        this.estado = estado;
        this.tipoDespacho = tipoDespacho;
        this.procesoDespacho = procesoDespacho;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getIdDespacho() {
        return idDespacho;
    }

    public void setIdDespacho(String idDespacho) {
        this.idDespacho = idDespacho;
    }

    public String getIdParada() {
        return idParada;
    }

    public void setIdParada(String idParada) {
        this.idParada = idParada;
    }

    public String getIdOrigen() {
        return idOrigen;
    }

    public void setIdOrigen(String idOrigen) {
        this.idOrigen = idOrigen;
    }

    public String getIdDestino() {
        return idDestino;
    }

    public void setIdDestino(String idDestino) {
        this.idDestino = idDestino;
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public String getPiezas() {
        return piezas;
    }

    public void setPiezas(String piezas) {
        this.piezas = piezas;
    }

    public String getGuias() {
        return guias;
    }

    public void setGuias(String guias) {
        this.guias = guias;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public int getTipoDespacho() {
        return tipoDespacho;
    }

    public void setTipoDespacho(int tipoDespacho) {
        this.tipoDespacho = tipoDespacho;
    }

    public int getProcesoDespacho() {
        return procesoDespacho;
    }

    public void setProcesoDespacho(int procesoDespacho) {
        this.procesoDespacho = procesoDespacho;
    }

    public interface Status {
        int NO_DESPACHADO   = 10;
        int DESPACHADO      = 20;

        int DESCARGADO      = 217;
        int SUBIDO          = 216;
    }

    public interface Type {
        int DESPACHO_BAJADA = 1;
        int DESPACHO_SUBIDA = 2;
    }
}
