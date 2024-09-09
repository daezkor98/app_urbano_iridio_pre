package com.urbanoexpress.iridio3.pe.view;

public interface RutaRuralView extends BaseV5View {

    int getSelectedTabPosition();

    void setVisibilityBoxConsideracionesImportantesRuta(int visibility);
    void setVisibilityBoxRutaNoIniciada(int visibility);
    void setMsgBoxRutaNoIniciada(String msg);

    void showDialogDescargarMotivosGestionLlamada();
}