package com.urbanoexpress.iridio3.view;

import androidx.fragment.app.Fragment;
import java.util.ArrayList;
import java.util.List;
import com.urbanoexpress.iridio3.ui.adapter.model.GalleryWrapperItem;
import com.urbanoexpress.iridio3.ui.model.MotivoDescargaItem;
import com.urbanoexpress.iridio3.ui.model.PiezaItem;
import com.urbanoexpress.iridio3.ui.model.PremioItem;

/**
 * Created by mick on 14/07/16.
 */
public interface DescargaEntregaView extends BaseV5View {

    void setErrorTxtNombre(String error);
    void setErrorTxtDNI(String error);

    void setTextGuiaElectronica(String text);
    void setTextNombre(String text);
    void setTextDNI(String text);
    void setTextComentarios(String text);
    void setTextBtnSiguiente(String text);
    void setTitleStepFotoCargo(String title);

    String getTextNombre();
    String getTextDNI();
    String getTextNumVoucher();
    String getTextComentarios();

    void showListaMotivos(List<MotivoDescargaItem> motivos);
    void showPiezas(List<PiezaItem> items);
    void showPremios(List<PremioItem> items);
    void showFotosEnGaleria(List<GalleryWrapperItem> items);
    void showImagenFirmaEnGaleria(List<GalleryWrapperItem> items);
    void showFotosCargoEnGaleria(List<GalleryWrapperItem> items);
    void showFotosDomicilioEnGaleria(List<GalleryWrapperItem> items);
    void showFotosComprobantePago(List<GalleryWrapperItem> items);
    void showTipoDocIdentificacion(ArrayList<String> tipoDocIdentificacion);
    void showTipoDireccion(ArrayList<String> tipoDireccion);
    void showTipoMedioPago(ArrayList<String> tipoMedioPago);

    void setVisibilityContainerMsgEntregaParcial(int visible);
    void setVisibilityWarningScanBarcodeMandatory(int visible);
    void setVisibilityLayoutInputRecibidoPor(int visible);
    void setVisibilityLayoutInputTipoDocIndentidad(int visible);
    void setVisibilityLayoutInputDocIndentidad(int visible);
    void setVisibilityLayoutInputTipoDireccion(int visible);
    void setVisibilityLayoutInputTipoMedioPago(int visible);
    void setVisibilityLayoutInputObservarEntrega(int visible);
    void setVisibilityLayoutInputVoucher(int visible);
    void setVisibilityBoxStepPiezas(int visible);
    void setVisibilityBoxStepProductosEntregados(int visible);
    void setVisibilityBoxStepTipoEntrega(int visible);
    void setVisibilityBoxStepDatosEntrega(int visible);
    void setVisibilityBoxStepFotosEntrega(int visible);
    void setVisibilityBoxStepFirmaEntrega(int visible);
    void setVisibilityBoxStepFotoCargoEntrega(int visible);
    void displayQR(String yapeHash);
    void setTextImporte(String importe);
    void setVisibilityBoxStepFotoComprobantePago(int visible);
    void setVisibilityBoxYapeQR(int visible);
    void setVisibilityBoxStepFotosDomicilio(int visible);

    void notifyPiezaItemChanged(int position);
    void notifyPiezasAllItemChanged();

    void notifyPremioItemChanged(int position);
    void notifyPremiosAllItemChanged();

    void notifyGaleriaFotosItemRemove(int position);
    void notifyGaleriaFotosAllItemChanged();

    void notifyGaleriaFirmaItemRemove(int position);
    void notifyGaleriaFirmaAllItemChanged();

    void notifyGaleriaCargoItemRemove(int position);
    void notifyGaleriaCargoAllItemChanged();

    void notifyGaleriaDomicilioItemRemove(int position);
    void notifyGaleriaDomicilioAllItemChanged();

    void notifyGaleriaPagoItemRemove(int position);
    void notifyGaleriaPagoAllItemChanged();


    void notifyMotivosAllItemChanged();

    Fragment getFragment();

    void dismiss();

    void hideKeyboard();

    void showMessageCantTakePhoto();
    void showMessageCantTakeSigning();
    void showWrongDateAndTimeMessage();
}
