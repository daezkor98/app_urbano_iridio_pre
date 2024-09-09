package com.urbanoexpress.iridio3.pe.ui.adapter.model;

public class GalleryPhotoItem implements GalleryWrapperItem {

    private String pathImage;

    public GalleryPhotoItem(String pathImage) {
        this.pathImage = pathImage;
    }

    public String getPathImage() {
        return pathImage;
    }

    public void setPathImage(String pathImage) {
        this.pathImage = pathImage;
    }

    @Override
    public int getType() {
        return GalleryWrapperItem.TYPE_PHOTO;
    }
}