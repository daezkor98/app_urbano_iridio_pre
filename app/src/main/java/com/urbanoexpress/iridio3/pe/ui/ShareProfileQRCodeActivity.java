package com.urbanoexpress.iridio3.pe.ui;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.ViewTreeObserver;

import androidx.annotation.Nullable;

import com.urbanoexpress.iridio3.R;
import com.urbanoexpress.iridio3.databinding.ActivityShareProfileQrcodeBinding;
import com.urbanoexpress.iridio3.pe.util.Preferences;
import com.urbanoexpress.iridio3.pe.util.UrbanoQRFormatter;
import com.urbanoexpress.iridio3.pe.util.graphics.QRCodeGenerator;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ShareProfileQRCodeActivity extends AppThemeBaseActivity {

    private ActivityShareProfileQrcodeBinding binding;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShareProfileQrcodeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupViews();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
    }

    @Override
    protected void animOnLeaveActivity() {
        overridePendingTransition(R.anim.not_slide, R.anim.slide_exit_out_bottom);
    }

    private void setupViews() {
        setupToolbar(binding.toolbar);
        setScreenTitle("Código QR");

        binding.qrCodeImage.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                binding.qrCodeImage.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                String idUserUrbano = UrbanoQRFormatter.generate(
                        Preferences.getInstance().getString("idUsuario", ""),
                        UrbanoQRFormatter.CQR_TYPE_1000);

                Single<Bitmap> observable = new QRCodeGenerator.Builder()
                        .setValue(idUserUrbano)
                        .setSize(binding.qrCodeImage.getWidth())
                        .build()
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread());
                compositeDisposable.add(observable.subscribeWith(new GenerateQRCodeObserver()));
            }
        });
    }

    private class GenerateQRCodeObserver extends DisposableSingleObserver<Bitmap> {

        @Override
        public void onSuccess(@NonNull Bitmap bitmap) {
            binding.qrCodeImage.setImageBitmap(bitmap);
        }

        @Override
        public void onError(@NonNull Throwable e) {

        }
    }
}