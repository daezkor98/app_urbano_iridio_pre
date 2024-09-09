package com.urbanoexpress.iridio3.pe.ui;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.zxing.Result;

import com.urbanoexpress.iridio3.pe.R;
import com.urbanoexpress.iridio3.pe.databinding.ActivityQrscannerBinding;
import com.urbanoexpress.iridio3.pe.presenter.QRScannerPresenter;
import com.urbanoexpress.iridio3.pe.util.CommonUtils;
import com.urbanoexpress.iridio3.pe.view.QRScannerView;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class QRScannerActivity extends AppThemeBaseActivity implements ZXingScannerView.ResultHandler,
        QRScannerView {

    private static final String TAG = QRScannerActivity.class.getSimpleName();

    private ActivityQrscannerBinding binding;
    private QRScannerPresenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQrscannerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupViews();

        if (presenter == null) {
            int typeImpl;
            try {
                Bundle bundle = getIntent().getExtras().getBundle("args");
                typeImpl = bundle.getInt("typeImpl");
            } catch (NullPointerException ex) {
                typeImpl = QRScannerPresenter.IMPLEMENT.DEFAULT;
            }

            presenter = new QRScannerPresenter(this, typeImpl);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.barcodeScannerView.startCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        binding.barcodeScannerView.stopCamera();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onActivityDestroy();
    }

    @Override
    protected void animOnLeaveActivity() {
        overridePendingTransition(R.anim.not_slide, R.anim.slide_exit_out_bottom);
    }

    @Override
    public void handleResult(Result result) {
        Log.d(TAG, result.getText());
        Log.d(TAG, result.getBarcodeFormat().name());

        presenter.onResult(result.getText(), result.getBarcodeFormat().name());
    }

    @Override
    public void resumeCameraPreview() {
        binding.barcodeScannerView.resumeCameraPreview(this);
    }

    @Override
    public void finishActivity() {
        finish();
    }

    public void setupViews() {
        setupToolbar(binding.toolbar);
        CommonUtils.changeColorStatusBar(this, R.color.gris_11);
        binding.barcodeScannerView.setResultHandler(this);
    }
}
