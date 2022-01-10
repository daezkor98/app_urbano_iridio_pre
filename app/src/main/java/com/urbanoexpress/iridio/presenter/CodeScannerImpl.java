package com.urbanoexpress.iridio.presenter;

import android.content.Intent;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.urbanoexpress.iridio.ui.interfaces.CodeScanner;
import com.urbanoexpress.iridio.util.constant.LocalAction;
import com.urbanoexpress.iridio.view.QRScannerView;

/**
 * Created by mick on 22/12/16.
 */

public class CodeScannerImpl implements CodeScanner {

    private QRScannerView view;

    public CodeScannerImpl(QRScannerView view) {
        this.view = view;
    }

    @Override
    public void handleResult(String value, String barcodeFormat) {
        sendOnResultScannListener(value, barcodeFormat);
    }

    @Override
    public void onActivityDestroy() { }

    /**
     * Receiver
     *
     * {@link GenerarManifiestoPresenter#resultScannReceiver}
     */
    private void sendOnResultScannListener(String value, String barcodeFormat) {
        Intent intent = new Intent(LocalAction.LOCAL_ACTION_BARCODE_SCAN_RESULT);
        intent.putExtra("value", value);
        intent.putExtra("barcodeFormat", barcodeFormat);
        LocalBroadcastManager.getInstance(view.getViewContext()).sendBroadcast(intent);
    }
}
