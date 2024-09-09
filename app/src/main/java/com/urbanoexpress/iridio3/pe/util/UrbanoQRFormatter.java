package com.urbanoexpress.iridio3.pe.util;

import android.util.Base64;

import java.nio.charset.Charset;

public final class UrbanoQRFormatter {

    private UrbanoQRFormatter() {}

    private static final String CQR_PREFIX = "URBANO_QR";
    private static final String SEPARATOR = ":";

    public static final String CQR_TYPE_1000 = "CQR_T1000"; // ID USER

    public static String generate(String value, String type) {
        value = CQR_PREFIX + SEPARATOR + value + SEPARATOR + type;
        return Base64.encodeToString(value.getBytes(Charset.forName("UTF-8")),
                Base64.NO_PADDING | Base64.NO_WRAP);
    }

    public static String getContent(String value, String type) throws IllegalArgumentException {
        try {
            value = new String(Base64.decode(value,Base64.NO_PADDING | Base64.NO_WRAP));
            String content = value.substring((CQR_PREFIX + SEPARATOR).length());
            content = content.substring(0, content.length() - (SEPARATOR + type).length());
            return content;
        } catch (IllegalArgumentException | StringIndexOutOfBoundsException ex) {
            throw new IllegalArgumentException("Unrecognized QR Code format.");
        }
    }

    public static boolean validate(String value, String type) {
        try {
            value = new String(Base64.decode(value,Base64.NO_PADDING | Base64.NO_WRAP));
            return value.matches("^(" + CQR_PREFIX + SEPARATOR + ")[a-zA-Z0-9|]+(" + SEPARATOR + type + ")$");
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }
}