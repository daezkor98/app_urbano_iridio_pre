package com.urbanoexpress.iridio3.pre.view;

import androidx.fragment.app.Fragment;

import com.urbanoexpress.iridio3.pre.ui.adapter.model.GalleryWrapperItem;
import com.urbanoexpress.iridio3.pre.ui.model.PiezaRecolectadaItem;

import java.util.List;

/**
 * Created by mick on 03/08/17.
 */

public interface RecoleccionGEView extends BaseV5View {

    void clearBarra();

    void setEnabledFormSobre(boolean enabled);
    void setEnabledFormPaquete(boolean enabled);
    void setEnabledFormValija(boolean enabled);
    void setEnabledFormOtros(boolean enabled);

    void setErrorBarra(String error);
    void setErrorFormGuiaRecoleccion(String error);

    void setTextGuiaElectronica(String text);
    void setTextTotalRecolectados(String text);
    void setTextFormGuiaRecoleccion(String text);
    void setTextBtnSiguiente(String text);

    void setValueFormPaquete(int value);

    void setVisibilityMoreActionsMenu(boolean visibility);

    void setSelectionModeScan();
    void setSelectionModeCheck();

    String getTextFormGuiaRecoleccion();
    String getTextComentarios();

    int getValueFormSobre();
    int getValueFormPaquete();
    int getValueFormValija();
    int getValueFormOtros();

    void showGallery(List<GalleryWrapperItem> items);
    void showPiezasRecolectadas(List<PiezaRecolectadaItem> items);
    void showGuiasRecolectadas(List<PiezaRecolectadaItem> items);

    void showMsgError(String msg);
    void showMsgSuccess(String msg);

    void showStepGuias();
    void showStepFormulario();
    void showStepFotosProducto();
    void showStepFirmaCliente();
    void showStepFotosCargo();
    void showStepFotosDomicilio();

    void hideStepGuias();
    void hideStepFormulario();

    void notifyGalleryAllItemChanged();
    void notifyGalleryItemInserted(int position);
    void notifyGalleryItemRemoved(int position);

    void notifyPiezasAllItemChanged();
    void notifyPiezasItemChanged(int position);
    void notifyPiezasItemInserted(int position);
    void notifyPiezasItemRemoved(int position);

    Fragment getFragment();

    void dismiss();

    void hideKeyboard();
    void showKeyboard();

    void showMessageCantTakePhoto();
    void showMessageCantTakeSigning();
    void showWrongDateAndTimeMessage();
    void showModalConfirmarEliminacionPieza(String title, String message, int position);
    void showModalConfirmarPiezasRecolectadas(PiezaRecolectadaItem item, int position);
}