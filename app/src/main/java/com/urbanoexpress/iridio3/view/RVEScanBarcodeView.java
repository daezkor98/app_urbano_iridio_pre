package com.urbanoexpress.iridio3.view;

import com.urbanoexpress.iridio3.ui.model.DetailsItem;

import java.util.List;

public interface RVEScanBarcodeView extends BaseV5View {

    void clearBarra();

    void setErrorBarra(String error);
    void setDetails(List<DetailsItem> items);

    void setEnabledButtonNext(boolean enabled);

    void showMsgError(String msg);
}
