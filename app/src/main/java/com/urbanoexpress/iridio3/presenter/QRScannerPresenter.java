package com.urbanoexpress.iridio3.presenter;

import com.urbanoexpress.iridio3.ui.interfaces.CodeScanner;
import com.urbanoexpress.iridio3.ui.interfaces.OnResultScannListener;
import com.urbanoexpress.iridio3.view.QRScannerView;

/**
 * Created by mick on 11/08/16.
 */

public class QRScannerPresenter implements OnResultScannListener {

    private QRScannerView view;
    private CodeScanner codeScannerImpl;
    private int typeImpl;

    public interface IMPLEMENT {
        int DEFAULT = 1;
        int READ_ONLY = 2;
        int CONTINUOUS = 3;
    }

    public  QRScannerPresenter(QRScannerView view, int typeImpl) {
        this.view = view;
        this.typeImpl = typeImpl;
        init();
    }

    private void init() {
        switch (typeImpl) {
            case IMPLEMENT.DEFAULT:
                codeScannerImpl = new CodeScannerImpl(view);
                break;
            case IMPLEMENT.READ_ONLY:
                codeScannerImpl = new BarcodeScannerReadOnlyImpl(view);
                break;
            case IMPLEMENT.CONTINUOUS:
                codeScannerImpl = new BarcodeScannerContinuosImpl(view);
                break;
        }
    }

    public void onActivityDestroy() {
        codeScannerImpl.onActivityDestroy();
    }

    @Override
    public void onResult(String value, String barcodeFormat) {
        synchronized (this) {
            codeScannerImpl.handleResult(value, barcodeFormat);
        }
    }
}
