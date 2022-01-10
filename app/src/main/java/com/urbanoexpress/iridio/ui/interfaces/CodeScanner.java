package com.urbanoexpress.iridio.ui.interfaces;

/**
 * Created by mick on 22/12/16.
 */

public interface CodeScanner {
    void handleResult(String value, String barcodeFormat);
    void onActivityDestroy();
}
