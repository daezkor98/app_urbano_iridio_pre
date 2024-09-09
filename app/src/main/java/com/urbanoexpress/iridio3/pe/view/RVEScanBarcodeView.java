package com.urbanoexpress.iridio3.pe.view;

import com.urbanoexpress.iridio3.pe.ui.model.DetailsItem;

import java.util.List;

public interface RVEScanBarcodeView extends BaseV5View {

    void clearBarra();

    void setErrorBarra(String error);
    void setDetails(List<DetailsItem> items);

    void setEnabledButtonNext(boolean enabled);

    void showMsgError(String msg);
}
