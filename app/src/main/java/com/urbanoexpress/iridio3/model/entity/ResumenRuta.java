package com.urbanoexpress.iridio3.model.entity;

import com.orm.SugarRecord;

/**
 * Created by mick on 11/07/16.
 */
public class ResumenRuta extends SugarRecord {

    private String idRuta;
    private String idZona;
    private String codZona;
    private String placa;
    private String courier;
    private String chofer;
    private String totalGuias;
    private String totalPiezas;
    private String pesoSeco;
    private String volumen;
    private String totalCashGuias;
    private String totalCashImporte;
    private String totalCardGuias;
    private String totalCardImporte;
    private String fechaInicioRuta;
    private String tiempoRuta;

    public ResumenRuta() { }

    public ResumenRuta(String idRuta, String idZona, String codZona,
                       String placa, String courier, String chofer,
                       String totalGuias, String totalPiezas, String pesoSeco,
                       String volumen, String totalCashGuias, String totalCashImporte,
                       String totalCardGuias, String totalCardImporte, String fechaInicioRuta,
                       String tiempoRuta) {
        this.idRuta = idRuta;
        this.idZona = idZona;
        this.codZona = codZona;
        this.placa = placa;
        this.courier = courier;
        this.chofer = chofer;
        this.totalGuias = totalGuias;
        this.totalPiezas = totalPiezas;
        this.pesoSeco = pesoSeco;
        this.volumen = volumen;
        this.totalCashGuias = totalCashGuias;
        this.totalCashImporte = totalCashImporte;
        this.totalCardGuias = totalCardGuias;
        this.totalCardImporte = totalCardImporte;
        this.fechaInicioRuta = fechaInicioRuta;
        this.tiempoRuta = tiempoRuta;
    }

    public String getIdRuta() {
        return idRuta;
    }

    public void setIdRuta(String idRuta) {
        this.idRuta = idRuta;
    }

    public String getIdZona() {
        return idZona;
    }

    public void setIdZona(String idZona) {
        this.idZona = idZona;
    }

    public String getCodZona() {
        return codZona;
    }

    public void setCodZona(String codZona) {
        this.codZona = codZona;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getCourier() {
        return courier;
    }

    public void setCourier(String courier) {
        this.courier = courier;
    }

    public String getChofer() {
        return chofer;
    }

    public void setChofer(String chofer) {
        this.chofer = chofer;
    }

    public String getTotalGuias() {
        return totalGuias;
    }

    public void setTotalGuias(String totalGuias) {
        this.totalGuias = totalGuias;
    }

    public String getTotalPiezas() {
        return totalPiezas;
    }

    public void setTotalPiezas(String totalPiezas) {
        this.totalPiezas = totalPiezas;
    }

    public String getPesoSeco() {
        return pesoSeco;
    }

    public void setPesoSeco(String pesoSeco) {
        this.pesoSeco = pesoSeco;
    }

    public String getVolumen() {
        return volumen;
    }

    public void setVolumen(String volumen) {
        this.volumen = volumen;
    }

    public String getTotalCashGuias() {
        return totalCashGuias;
    }

    public void setTotalCashGuias(String totalCashGuias) {
        this.totalCashGuias = totalCashGuias;
    }

    public String getTotalCashImporte() {
        return totalCashImporte;
    }

    public void setTotalCashImporte(String totalCashImporte) {
        this.totalCashImporte = totalCashImporte;
    }

    public String getTotalCardGuias() {
        return totalCardGuias;
    }

    public void setTotalCardGuias(String totalCardGuias) {
        this.totalCardGuias = totalCardGuias;
    }

    public String getTotalCardImporte() {
        return totalCardImporte;
    }

    public void setTotalCardImporte(String totalCardImporte) {
        this.totalCardImporte = totalCardImporte;
    }

    public String getFechaInicioRuta() {
        return fechaInicioRuta;
    }

    public void setFechaInicioRuta(String fechaInicioRuta) {
        this.fechaInicioRuta = fechaInicioRuta;
    }

    public String getTiempoRuta() {
        return tiempoRuta;
    }

    public void setTiempoRuta(String tiempoRuta) {
        this.tiempoRuta = tiempoRuta;
    }
}
