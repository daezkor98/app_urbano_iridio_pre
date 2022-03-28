package com.urbanoexpress.iridio3.ui.adapter.model;

public class GalleryButtonItem implements GalleryWrapperItem {

    private int resourceId;
    private int action;

    public GalleryButtonItem(int resourceId) {
        this.resourceId = resourceId;
        this.action = Action.CAMERA;
    }

    public GalleryButtonItem(int resourceId, int action) {
        this.resourceId = resourceId;
        this.action = action;
    }

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public interface Action {
        int CAMERA = 100;
        int GALLERY = 101;
    }

    @Override
    public int getType() {
        return GalleryWrapperItem.TYPE_BUTTON;
    }
}
