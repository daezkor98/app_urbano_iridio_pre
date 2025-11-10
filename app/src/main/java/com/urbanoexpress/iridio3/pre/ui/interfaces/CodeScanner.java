package com.urbanoexpress.iridio3.pre.ui.interfaces;

/**
 * Created by mick on 22/12/16.
 */

public interface CodeScanner {
    void handleResult(String value, String barcodeFormat);
    void onActivityDestroy();
}
