package com.urbanoexpress.iridio3.pre.view;

import android.content.Context;

/**
 * View contract para UrbanoPayActivity.
 */
public interface UrbanoPayView {

    Context getViewContext();

    // ----- Estados -----
    void showEstadoInicial();         // Card compacto con guía + botón "GENERAR QR"
    void showEstadoQR();              // Card grande con QR + monto + wallets
    void showEstadoPagado();          // Card con check + ¡PAGADO! + info + CONTINUAR

    // ----- Setters -----
    void setTextTituloEntrega(String titulo);     // "GESTIONAR:\nEntrega ENU433470" (título afuera del card)
    void setTextGuiaInicial(String guia);          // "ENU433470" (dentro del card, estado inicial)
    void displayQR(String hash);
    void setTextMonto(String montoFmt);            // "S/45.90"
    void setTextRqIdCode(String rqIdText);         // "ID Operación 3141"
    void setTextProgresoPago(String progreso);     // "Pago 2 de 5" (opcional)
    void setVisibilityProgreso(boolean visible);
    void setDatosComprobante(String estado, String guia, String monto, String docNumero, String nombre);

    // ----- Feedback -----
    void showProgressDialog(String msg);
    void dismissProgressDialog();
    void showToast(String msg);

    // ----- Cierre -----
    void closeWithResult(com.urbanoexpress.iridio3.pre.model.entity.UrbanoPayResult result);
}
