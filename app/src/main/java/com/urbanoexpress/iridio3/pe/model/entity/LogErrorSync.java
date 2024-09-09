package com.urbanoexpress.iridio3.pe.model.entity;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.orm.SugarRecord;

/**
 * Created by mick on 11/10/16.
 */

public class LogErrorSync extends SugarRecord {

    private String idUsuario;
    private int tipo;
    private String titulo;
    private String mensaje;
    private String seguimiento_pila;
    private String timestamp;

    public LogErrorSync() {
    }

    public LogErrorSync(String locationTag, String idUsuario, int tipo, String titulo,
                        String mensaje, String seguimiento_pila, String timestamp) {
        String locationIdentifier = locationTag.substring (0, Math.min(8, locationTag.length())) + "__";
        this.idUsuario = idUsuario;
        this.tipo = tipo;
        this.titulo = locationIdentifier + titulo;
        this.mensaje = mensaje;
        this.seguimiento_pila = seguimiento_pila;
        this.timestamp = timestamp;
        FirebaseCrashlytics
                .getInstance()
                .log("LogError---- \nthis.idUsuario:_ " + this.idUsuario +
                        "\nthis.tipo:_ " + this.tipo +
                        "\nthis.titulo:_ " + this.titulo +
                        "\nthis.mensaje:_ " + this.mensaje +
                        "\nthis.seguimiento_pila:_ " + this.seguimiento_pila +
                        "\nthis.timestamp:_ " + this.timestamp
                );
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

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getSeguimiento_pila() {
        return seguimiento_pila;
    }

    public void setSeguimiento_pila(String seguimiento_pila) {
        this.seguimiento_pila = seguimiento_pila;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public interface Tipo {
        int ESTADO_RUTA = 0;
        int GUIA_GESTIONADA = 1;
        int SECUENCIA_GUIA = 2;
        int IMAGEN = 3;
        int GPS = 4;
        int GESTION_LLAMADA = 5;
        int INCIDENTE_RUTA = 6;
    }
}
