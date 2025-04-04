package com.urbanoexpress.iridio3.pe.model.entity;

public class ClienteRuta {
    private String cliente;
    private String guia;
    private int totalPiezas;

    public ClienteRuta(String cliente, String guia, int totalPiezas) {
        this.cliente = cliente;
        this.guia = guia;
        this.totalPiezas = totalPiezas;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getGuia() {
        return guia;
    }

    public void setGuia(String guia) {
        this.guia = guia;
    }

    public int getTotalPiezas() {
        return totalPiezas;
    }

    public void setTotalPiezas(int totalPiezas) {
        this.totalPiezas = totalPiezas;
    }
}
