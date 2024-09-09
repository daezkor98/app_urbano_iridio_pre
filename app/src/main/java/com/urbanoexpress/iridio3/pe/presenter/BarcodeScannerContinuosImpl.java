package com.urbanoexpress.iridio3.pe.presenter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.urbanoexpress.iridio3.pe.ui.interfaces.CodeScanner;
import com.urbanoexpress.iridio3.pe.util.constant.LocalAction;
import com.urbanoexpress.iridio3.pe.view.QRScannerView;

public class BarcodeScannerContinuosImpl implements CodeScanner {

    private QRScannerView view;

    public BarcodeScannerContinuosImpl(QRScannerView view) {
        this.view = view;

        LocalBroadcastManager.getInstance(view.getViewContext())
                .registerReceiver(nextScannReceiver,
                        new IntentFilter(LocalAction.LOCAL_ACTION_BARCODE_SCAN_RESULT));
    }

    @Override
    public void handleResult(String value, String barcodeFormat) {
        sendOnResultScannListener(value, barcodeFormat);
    }

    @Override
    public void onActivityDestroy() {
        LocalBroadcastManager.getInstance(view.getViewContext()).unregisterReceiver(nextScannReceiver);
    }

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

    /**
     * Broadcast
     *
     * {@link CodeScannerImpl#sendOnResultScannListener}
     */
    private final BroadcastReceiver nextScannReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            view.resumeCameraPreview();
        }
    };

}