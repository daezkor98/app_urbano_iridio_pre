package com.urbanoexpress.iridio.model.entity;

import com.orm.SugarRecord;

import java.io.Serializable;

/**
 * Created by mick on 12/07/16.
 */
public class DescargaRuta extends SugarRecord implements Serializable {

    private static final long serialVersionUID = 6828681276979188678L;

    private String idUsuario;
    private String idServicio;
    private String lineaNegocio;
    private int tipoDescarga;
    private int procesoDescarga;

    public DescargaRuta() { }

    public DescargaRuta(String idUsuario, String idServicio, String lineaNegocio,
                        int tipoDescarga, int procesoDescarga) {
        this.idUsuario = idUsuario;
        this.idServicio = idServicio;
        this.lineaNegocio = lineaNegocio;
        this.tipoDescarga = tipoDescarga;
        this.procesoDescarga = procesoDescarga;
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

    public String getLineaNegocio() {
        return lineaNegocio;
    }

    public void setLineaNegocio(String lineaNegocio) {
        this.lineaNegocio = lineaNegocio;
    }

    public int getTipoDescarga() {
        return tipoDescarga;
    }

    public void setTipoDescarga(int tipoDescarga) {
        this.tipoDescarga = tipoDescarga;
    }

    public int getProcesoDescarga() {
        return procesoDescarga;
    }

    public void setProcesoDescarga(int procesoDescarga) {
        this.procesoDescarga = procesoDescarga;
    }

    public interface Entrega extends Finalizado {
        int RECAUDO_IMPORTE     = 10; // Paso 1
        int ENTREGA_EFECTIVA    = 11; // Paso 3
        int ENTREGAR            = 12; // Paso 4
        int NO_ENTREGO          = 13; // Paso 4
        int RECOGIO_HABILITANTES = 14; // Paso 2
    }

    public interface Recoleccion extends Finalizado {
        int LLEGO_DIRECCION_RECOJO          = 20;
        int RECOLECCION_EFECTIVA            = 21;
        int GUIA_ELECTRONICA_DISPONIBLE     = 22;
        int RECOLECTAR_SIN_GUIA_ELECTRONICA = 23;
        int RECOLECTAR_CON_GUIA_MANUAL      = 26;
        int RECOLECTAR_CON_GUIA_ELECTRONICA = 24;
        int RECOLECTAR_LOGISTICA_INVERSA    = 27;
        int RECOLECTAR_VALIJA               = 28;
        int RECOLECTAR_SELLER               = 29;
        int RECOLECTAR_COUNTER              = 30;
        int NO_RECOLECTO                    = 25;
    }

    private interface Finalizado {
        int FINALIZADO = 50;
    }

}
