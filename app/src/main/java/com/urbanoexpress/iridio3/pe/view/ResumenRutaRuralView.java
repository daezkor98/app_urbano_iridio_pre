package com.urbanoexpress.iridio3.pe.view;

public interface ResumenRutaRuralView extends BaseView2 {

    void setTextDate(String text);
    void setTextCourier(String text);
    void setTextZona(String text);
    void setTextFecha(String text);
    void setTextTotalGuias(String text);
    void setTextTotalPiezas(String text);
    void setTextPesoSeco(String text);
    void setTextTotalCashGuias(String text);
    void setTextTotalCashImporte(String text);

    void setVisibilitySwipeRefreshLayout(boolean visible);

    void openDatePicker(int year, int month, int dayOfMonth);
}
