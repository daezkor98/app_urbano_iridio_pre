package com.urbanoexpress.iridio.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.EnumMap;
import java.util.Map;

public class QRCode {

    private QRCode() {}

    public static class Builder {

        private Context context;
        private String value;
        private int size;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setValue(String value) {
            this.value = value;
            return this;
        }

        public Builder setSize(int size) {
            this.size = size;
            return this;
        }

        public Bitmap build() {
            QRCodeWriter writer = new QRCodeWriter();
            Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
            hints.put(EncodeHintType.MARGIN, 0);

            Bitmap bitmap = null;
            try {
                BitMatrix bitMatrix = writer.encode(value, BarcodeFormat.QR_CODE, size, size, hints);
                int width = bitMatrix.getWidth();
                int height = bitMatrix.getHeight();
                bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                    }
                }
                return bitmap;
            } catch (WriterException ex) {
                ex.printStackTrace();
                return null;
            }
        }
    }
}
