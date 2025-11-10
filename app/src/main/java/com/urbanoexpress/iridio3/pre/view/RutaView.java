package com.urbanoexpress.iridio3.pre.view;

/**
 * Created by mick on 26/07/16.
 */
public interface RutaView extends BaseView {

    void setVisibilityBoxConsideracionesImportantesRuta(int visibility);
    void setVisibilityBoxRutaNoIniciada(int visibility);
    void setMsgBoxRutaNoIniciada(String msg);

    void showMessageIniciarRuta();
    void navigateToRecolectarValijaActivity();
}
