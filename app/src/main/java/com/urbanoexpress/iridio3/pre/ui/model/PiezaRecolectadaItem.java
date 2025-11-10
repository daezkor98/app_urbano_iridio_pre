package com.urbanoexpress.iridio3.pre.ui.model;

import android.os.Parcel;
import android.os.Parcelable;

public class PiezaRecolectadaItem implements Parcelable {

    private String idPieza;
    private String barra;
    private String guiaBarra;
    private String estado;
    private String piezas;
    private String fechaSS;
    private Type type; // Tipo de Barra (WYB, PCK)
    private boolean selected;

    public PiezaRecolectadaItem(PiezaRecolectadaItem piezaRecolectadaItem) {
        this.idPieza = piezaRecolectadaItem.getIdPieza();
        this.barra = piezaRecolectadaItem.getBarra();
        this.guiaBarra = piezaRecolectadaItem.getGuiaBarra();
        this.estado = piezaRecolectadaItem.getEstado();
        this.piezas = piezaRecolectadaItem.getPiezas();
        this.fechaSS = piezaRecolectadaItem.getFechaSS();
        this.type = piezaRecolectadaItem.getType();
        this.selected = piezaRecolectadaItem.isSelected();
    }

    public PiezaRecolectadaItem(String idPieza, String barra, String guiaBarra, String estado,
                                String piezas, String fechaSS, Type type, boolean selected) {
        this.idPieza = idPieza;
        this.barra = barra;
        this.guiaBarra = guiaBarra;
        this.estado = estado;
        this.piezas = piezas;
        this.fechaSS = fechaSS;
        this.type = type;
        this.selected = selected;
    }

    protected PiezaRecolectadaItem(Parcel in) {
        idPieza = in.readString();
        barra = in.readString();
        guiaBarra = in.readString();
        estado = in.readString();
        piezas = in.readString();
        fechaSS = in.readString();
        selected = in.readByte() != 0;
    }

    public static final Creator<PiezaRecolectadaItem> CREATOR = new Creator<PiezaRecolectadaItem>() {
        @Override
        public PiezaRecolectadaItem createFromParcel(Parcel in) {
            return new PiezaRecolectadaItem(in);
        }

        @Override
        public PiezaRecolectadaItem[] newArray(int size) {
            return new PiezaRecolectadaItem[size];
        }
    };

    public String getIdPieza() {
        return idPieza;
    }

    public void setIdPieza(String idPieza) {
        this.idPieza = idPieza;
    }

    public String getBarra() {
        return barra;
    }

    public void setBarra(String barra) {
        this.barra = barra;
    }

    public String getGuiaBarra() {
        return guiaBarra;
    }

    public void setGuiaBarra(String guiaBarra) {
        this.guiaBarra = guiaBarra;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getPiezas() {
        return piezas;
    }

    public void setPiezas(String piezas) {
        this.piezas = piezas;
    }

    public String getFechaSS() {
        return fechaSS;
    }

    public void setFechaSS(String fechaSS) {
        this.fechaSS = fechaSS;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(idPieza);
        dest.writeString(barra);
        dest.writeString(guiaBarra);
        dest.writeString(estado);
        dest.writeString(piezas);
        dest.writeString(fechaSS);
        dest.writeByte((byte) (selected ? 1 : 0));
    }

    public enum Type {
        WYB,
        PCK
    }
}