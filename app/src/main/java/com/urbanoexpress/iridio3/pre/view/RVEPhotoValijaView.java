package com.urbanoexpress.iridio3.pre.view;

import com.urbanoexpress.iridio3.pre.model.entity.Ruta;
import com.urbanoexpress.iridio3.pre.ui.adapter.model.GalleryWrapperItem;

import java.util.ArrayList;
import java.util.List;

public interface RVEPhotoValijaView extends BaseV5View {

    void notifyGalleryItemInserted(int position);
    void notifyGalleryItemRemoved(int position);

    void setGallery(List<GalleryWrapperItem> items);
    void openCamera(String directoryNameParent, String imageNamePrefix);

    void showConfirmDeletePhotoModal(int position);
    void showMsgError(String msg);

    void sendBroadcastGestionValijaFinalizada(ArrayList<Ruta> rutas);
}
