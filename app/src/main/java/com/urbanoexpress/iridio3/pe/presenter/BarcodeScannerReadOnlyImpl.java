package com.urbanoexpress.iridio3.pe.presenter;

import android.content.Intent;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.urbanoexpress.iridio3.pe.ui.interfaces.CodeScanner;
import com.urbanoexpress.iridio3.pe.util.constant.LocalAction;
import com.urbanoexpress.iridio3.pe.view.QRScannerView;

/**
 * Created by mick on 07/02/17.
 */

public class BarcodeScannerReadOnlyImpl implements CodeScanner {

    private QRScannerView view;

    public BarcodeScannerReadOnlyImpl(QRScannerView view) {
        this.view = view;
    }

    @Override
    public void handleResult(String value, String barcodeFormat) {
        view.finishActivity();
        sendOnResultScannListener(value, barcodeFormat);
    }

    @Override
    public void onActivityDestroy() { }

    /**
     * Receiver
     *
     * {@link LiquidacionClientePendientePresenter#resultScannReceiver}
     */
    private void sendOnResultScannListener(String value, String barcodeFormat) {
        Intent intent = new Intent(LocalAction.LOCAL_ACTION_BARCODE_SCAN_RESULT);
        intent.putExtra("value", value);
        intent.putExtra("barcodeFormat", barcodeFormat);
        LocalBroadcastManager.getInstance(view.getViewContext()).sendBroadcast(intent);
    }

}