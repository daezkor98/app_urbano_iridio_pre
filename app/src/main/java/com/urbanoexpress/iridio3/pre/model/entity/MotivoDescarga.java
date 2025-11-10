package com.urbanoexpress.iridio3.pre.model.entity;

import com.orm.SugarRecord;
import com.orm.dsl.Column;

/**
 * Created by mick on 13/07/16.
 */
public class MotivoDescarga extends SugarRecord {

    private String idUsuario;
    private int tipo;
    private String idMotivo;
    private String codigo;
    private String descripcion;
    private String lineaNegocio;
    @Column(name = "GRU_ID")
    private int gruId;

    public MotivoDescarga() { }

    public MotivoDescarga(String idUsuario, int tipo, String idMotivo,
                          String codigo, String descripcion, String lineaNegocio, int gruId) {
        this.idUsuario = idUsuario;
        this.tipo = tipo;
        this.idMotivo = idMotivo;
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.lineaNegocio = lineaNegocio;
        this.gruId = gruId;
    }

    public MotivoDescarga(String idUsuario, int tipo, String idMotivo,
                          String codigo, String descripcion, String lineaNegocio) {
        this.idUsuario = idUsuario;
        this.tipo = tipo;
        this.idMotivo = idMotivo;
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.lineaNegocio = lineaNegocio;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    public String getIdMotivo() {
        return idMotivo;
    }

    public void setIdMotivo(String idMotivo) {
        this.idMotivo = idMotivo;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getLineaNegocio() {
        return lineaNegocio;
    }

    public void setLineaNegocio(String lineaNegocio) {
        this.lineaNegocio = lineaNegocio;
    }

    public int getGruId() {
        return gruId;
    }

    public void setGruId(int gruId) {
        this.gruId = gruId;
    }

    public interface Tipo {
        int ENTREGA             = 10;
        int NO_ENTREGA          = 20;
        int RECOLECTA           = 30;
        int NO_RECOLECTA        = 40;
        int ENTREGA_DEVOLUCION  = 50;
        int ENTREGA_LIQUIDACION = 60;
        int NO_ENTREGA_LIQ_DEV  = 70;
        int GESTION_CON_LLAMADA = 120;
        int OBSERVACION_ENTREGA = 130;
        int NO_HUBO_TIEMPO      = 140;
        int ENTREGA_PARCIAL     = 150;
        int ENTREGA_DEVOLUCION_PARCIAL = 160;
    }
}
