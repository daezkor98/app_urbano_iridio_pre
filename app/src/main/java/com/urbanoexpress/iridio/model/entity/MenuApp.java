package com.urbanoexpress.iridio.model.entity;

import com.orm.SugarRecord;

/**
 * Created by mick on 23/05/16.
 */
public class MenuApp extends SugarRecord {

    private String idUsuario;
    private String idMenu;
    private String padre;
    private String nivel;
    private String nombre;
    private String orden;
    private String menuClass;

    public MenuApp() { }

    public MenuApp(String idUsuario, String idMenu, String padre,
                   String nivel, String nombre, String menuClass,
                   String orden) {
        this.idUsuario = idUsuario;
        this.idMenu = idMenu;
        this.padre = padre;
        this.nivel = nivel;
        this.nombre = nombre;
        this.menuClass = menuClass;
        this.orden = orden;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getIdMenu() {
        return idMenu;
    }

    public void setIdMenu(String idMenu) {
        this.idMenu = idMenu;
    }

    public String getPadre() {
        return padre;
    }

    public void setPadre(String padre) {
        this.padre = padre;
    }

    public String getNivel() {
        return nivel;
    }

    public void setNivel(String nivel) {
        this.nivel = nivel;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getMenuClass() {
        return menuClass;
    }

    public void setMenuClass(String menuClass) {
        this.menuClass = menuClass;
    }

    public String getOrden() {
        return orden;
    }

    public void setOrden(String orden) {
        this.orden = orden;
    }
}
