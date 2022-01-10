package com.urbanoexpress.iridio.model.entity;

import com.orm.SugarRecord;

import java.io.Serializable;

/**
 * Created by mick on 31/05/16.
 */
public class ParadaProgramada extends SugarRecord implements Serializable {

    private static final long serialVersionUID = 1696442926657762244L;

    private String idUsuario;
    private String idStop;
    private String agencia;
    private String siglaAgencia;
    private String horaEstimada;
    private String horaLlegada;
    private String horaSalida;
    private String agencia_latitude;
    private String agencia_longitude;
    private String tipo;
    private int estadoLlegada;
    private int estadoDespachosRevisado;

    public ParadaProgramada() { }

    public ParadaProgramada(String idUsuario, String idStop, String agencia,
                            String siglaAgencia, String horaEstimada, String horaLlegada,
                            String horaSalida, String agencia_latitude, String agencia_longitude,
                            String tipo, int estadoLlegada, int estadoDespachosRevisado) {
        this.idUsuario = idUsuario;
        this.idStop = idStop;
        this.agencia = agencia;
        this.siglaAgencia = siglaAgencia;
        this.horaEstimada = horaEstimada;
        this.horaLlegada = horaLlegada;
        this.horaSalida = horaSalida;
        this.agencia_latitude = agencia_latitude;
        this.agencia_longitude = agencia_longitude;
        this.tipo = tipo;
        this.estadoLlegada = estadoLlegada;
        this.estadoDespachosRevisado = estadoDespachosRevisado;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getIdStop() {
        return idStop;
    }

    public void setIdStop(String idStop) {
        this.idStop = idStop;
    }

    public String getAgencia() {
        return agencia;
    }

    public void setAgencia(String agencia) {
        this.agencia = agencia;
    }

    public String getSiglaAgencia() {
        return siglaAgencia;
    }

    public void setSiglaAgencia(String siglaAgencia) {
        this.siglaAgencia = siglaAgencia;
    }

    public String getHoraEstimada() {
        return horaEstimada;
    }

    public void setHoraEstimada(String horaEstimada) {
        this.horaEstimada = horaEstimada;
    }

    public String getHoraLlegada() {
        return horaLlegada;
    }

    public void setHoraLlegada(String horaLlegada) {
        this.horaLlegada = horaLlegada;
    }

    public String getHoraSalida() {
        return horaSalida;
    }

    public void setHoraSalida(String horaSalida) {
        this.horaSalida = horaSalida;
    }

    public String getAgencia_latitude() {
        return agencia_latitude;
    }

    public void setAgencia_latitude(String agencia_latitude) {
        this.agencia_latitude = agencia_latitude;
    }

    public String getAgencia_longitude() {
        return agencia_longitude;
    }

    public void setAgencia_longitude(String agencia_longitude) {
        this.agencia_longitude = agencia_longitude;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getEstadoLlegada() {
        return estadoLlegada;
    }

    public void setEstadoLlegada(int estadoLlegada) {
        this.estadoLlegada = estadoLlegada;
    }

    public int getEstadoDespachosRevisado() {
        return estadoDespachosRevisado;
    }

    public void setEstadoDespachosRevisado(int estadoDespachosRevisado) {
        this.estadoDespachosRevisado = estadoDespachosRevisado;
    }

    public interface Status {
        int NO_LLEGO_AGENCIA        = 0;
        int LLEGO_AGENCIA           = 217;
        int SALIO_AGENCIA           = 216;

        int NO_REVISO_DESPACHOS     = 0;
        int REVISO_DESPACHOS        = 1;
    }

}
