package com.urbanoexpress.iridio3.pe.ui.dialogs;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.urbanoexpress.iridio3.pe.util.async.AsyncTaskCoroutine;
import com.urbanoexpress.iridio3.databinding.ModalCodigoQrRutaBinding;
import com.urbanoexpress.iridio3.pe.util.MetricsUtils;

/**
 * Created by mick on 16/06/17.
 */

public class CodigoQRRutaDialog extends DialogFragment {

    public static final String TAG = "CodigoQRRutaDialog";

    private ModalCodigoQrRutaBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        binding = ModalCodigoQrRutaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews();
    }

    private void setupViews() {
        binding.btnAceptar.setOnClickListener(view -> dismiss());
        new GenerateCodeQRTask(getArguments().getString("idRuta")).execute();
    }

    private class GenerateCodeQRTask extends AsyncTaskCoroutine<String, Bitmap> {
        private String value = "";

        public GenerateCodeQRTask(String value) {
            this.value = value;
        }

        @Override
        public Bitmap doInBackground(String... strings) {
            QRCodeWriter writer = new QRCodeWriter();
            Bitmap bitmap = null;
            try {
                BitMatrix bitMatrix = writer.encode(value,
                        BarcodeFormat.QR_CODE, MetricsUtils.dpToPx(getActivity(), 150),
                        MetricsUtils.dpToPx(getActivity(), 150));
                int width = bitMatrix.getWidth();
                int height = bitMatrix.getHeight();
                bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                    }
                }
            } catch (WriterException e) {
                e.printStackTrace();
            }

            return bitmap;
        }

        @Override
        public void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            Drawable drawable = new BitmapDrawable(getResources(), bitmap);
            binding.progressBar.setVisibility(View.GONE);
            binding.imgCodigoQR.setBackground(drawable);
        }
    }
}