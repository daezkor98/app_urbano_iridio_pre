package com.urbanoexpress.iridio.presenter.model;

/**
 * Created by mick on 14/12/16.
 */

public class AgenciaEntity {

    private String codigo;
    private String nombre;
    private String sigla;
    private String main;
    private String tipo;
    private String dir_id;

    public AgenciaEntity(String codigo, String nombre, String sigla,
                         String main, String tipo, String dir_id) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.sigla = sigla;
        this.main = main;
        this.tipo = tipo;
        this.dir_id = dir_id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }

    public String getMain() {
        return main;
    }

    public void setMain(String main) {
        this.main = main;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDir_id() {
        return dir_id;
    }

    public void setDir_id(String dir_id) {
        this.dir_id = dir_id;
    }

}
