package com.urbanoexpress.iridio3.view;

import android.view.Menu;

import com.urbanoexpress.iridio3.ui.model.DetailsItem;

import java.util.List;

public interface DetalleRutaRuralView extends BaseView2 {

    void setTextBarra(String text);
    void setTextDireccion(String text);
    void setTextHorario(String text);
    void setTextMedioPago(String text);
    void setTextImporte(String text);
    void setTextBtnGestionar(String text);
    void setTextMsgReqDevolucionShipper(String text);

    void showDetalleRuta(List<DetailsItem> items);
    void showAlertaReqDevolucionShipper();
    void showCobrarClienteContainer();
    void showActionPiezasContainer();
    void showActionRequerimientoContainer();
    void showActionDevolverContainer();
    void showBtnGestionar();

    void hideTextHorario();
    void hideIconHorario();
    void hideBtnGestionar();

    void setColorTextHorario(int color);
    void setColorIconHorario(int color);

    void showAlertaEstadoShipper(int idResIcon, int bg, String msg);

    void showMsgIniciarRuta();
    void showMsgRutaFinalizada();
    void showMsgGaleriaNodisponible();
    void showMsgGestionNoPermitidaPorOrdenDescarga();
    void showDialogDescargarMotivosGestionLlamada();
    void showDialogSeleccionarResultadoLlamada(String[] resultados);
    void showDialogSeleccionarMotivoLlamadaNoContactada(String[] motivos);

    Menu getMenuToolbar();

    void finishActivity();
}
