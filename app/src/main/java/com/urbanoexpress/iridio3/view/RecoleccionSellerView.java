package com.urbanoexpress.iridio3.view;

import androidx.fragment.app.Fragment;

import com.urbanoexpress.iridio3.ui.adapter.model.GalleryWrapperItem;
import com.urbanoexpress.iridio3.ui.model.PiezaRecolectadaItem;

import java.util.List;

public interface RecoleccionSellerView extends BaseV5View {

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
    void setTextBottomMessage(String text);
    void setTextBtnSiguiente(String text);

    void setValueFormPaquete(int value);

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
    void showGuiasSinAlistar(List<PiezaRecolectadaItem> items);

    void showMsgError(String msg);
    void showMsgSuccess(String msg);

    void notifyGalleryAllItemChanged();
    void notifyGalleryItemInserted(int position);
    void notifyGalleryItemRemoved(int position);

    void notifyPiezasAllItemChanged();
    void notifyPiezasItemChanged(int position);
    void notifyPiezasItemInserted(int position);
    void notifyPiezasItemRemoved(int position);

    void setVisibilityBottomMsg(boolean visible);
    void setVisibilityStepGuias(boolean visible);
    void setVisibilityStepGuiasMalEmbaladas(boolean visible);
    void setVisibilityStepGuiasSinAlistar(boolean visible);
    void setVisibilityStepFormulario(boolean visible);
    void setVisibilityStepFotosProducto(boolean visible);
    void setVisibilityStepFirmaCliente(boolean visible);
    void setVisibilityStepFotosCargo(boolean visible);
    void setVisibilityStepFotosDomicilio(boolean visible);
    void setVisibilityMoreActionsMenu(boolean visibility);

    Fragment getFragment();

    void dismiss();

    void hideKeyboard();
    void showKeyboard();

    void showMessageCantTakePhoto();
    void showMessageCantTakeSigning();
    void showWrongDateAndTimeMessage();
    void showDialogConfirmNoSelectedGuias();
    void showModalConfirmarEliminacionPieza(String title, String message, int position);
    void showModalConfirmarPiezasRecolectadas(PiezaRecolectadaItem item, int position);
}