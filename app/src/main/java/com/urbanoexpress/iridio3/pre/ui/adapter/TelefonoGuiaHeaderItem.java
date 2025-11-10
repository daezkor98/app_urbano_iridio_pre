package com.urbanoexpress.iridio3.pre.ui.adapter;

public class TelefonoGuiaHeaderItem implements TelefonoGuiaV2Adapter.WrapperItem {

    private String title;

    public TelefonoGuiaHeaderItem(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public int getType() {
        return TelefonoGuiaV2Adapter.WrapperItem.TYPE_HEADER;
    }
}
