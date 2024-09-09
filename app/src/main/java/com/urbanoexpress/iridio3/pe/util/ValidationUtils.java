package com.urbanoexpress.iridio3.pe.util;

import android.util.Log;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mick on 21/10/16.
 */

public class ValidationUtils {

    private static final String TAG = ValidationUtils.class.getSimpleName();

    public static Long getGuiaSinDigitoVerificador(String vp_guia, Integer vp_operacion) {
        Long vs_guia = 0L;

        String vl_guia = vp_guia.trim();
        Integer vl_dig_c;
        Integer vl_num;
        Integer vl_dig;
        Integer vl_valor = 0;
        Integer vl_factor = 2;
        Integer vl_verif = 0;

        ArrayList<Integer> verificadores = new ArrayList<>(Arrays.asList(
                0,1,2,3,4,5,6,7,8,9
        ));

        if (vl_guia.length() < 3) {
            return 0L;
        }

        if (vp_operacion == 1) {
            vl_guia = vl_guia.substring(0, vl_guia.length() - 1);
            Log.d("ValidationUtils", "Guia sin verificador: " + vl_guia);
            vl_verif = Integer.parseInt(vp_guia.trim().substring(vp_guia.length() - 1, vp_guia.length()));
            Log.d("ValidationUtils", "Digito verificador: " + vl_verif);
        }

        vp_guia = vl_guia;

        for (int i = 0; i < vl_guia.length(); i++) {
            vl_dig_c = Integer.parseInt(vp_guia.trim().substring(vp_guia.length() - 1, vp_guia.length()));
            Log.d("ValidationUtils", "vl_dig_c: " + vl_dig_c + "");

            if (!verificadores.contains(vl_dig_c)) {
                return 0L;
            }

            vl_dig = vl_dig_c;
            vl_valor = vl_valor + (vl_dig * vl_factor);
            vl_factor++;

            if (vl_factor == 8) {
                vl_factor = 2;
            }

            vp_guia = vp_guia.substring(0, vp_guia.length() - 1);
            Log.d("ValidationUtils", "vp_guia: " + vp_guia + "");
        }

        vl_num = vl_valor % 11;
        vl_dig = 11 - vl_num;

        if (vl_dig == 11) {
            vl_dig = 0;
        }
        if (vl_dig == 10) {
            vl_dig = 1;
        }

        if (vp_operacion == 1) {
            if (vl_dig == vl_verif) {
                vs_guia = Long.parseLong(vl_guia);
            } else {
                vs_guia = 0L;
            }
        }
//        else {
//            vs_guia = Long.parseLong(vl_guia + vl_dig);
//        }

        Log.d("ValidationUtils", "VS_GUIA: " + vs_guia);

        return vs_guia;
    }

    public static boolean validateGuia(String vp_guia, Integer vp_operacion) {
        if (getGuiaSinDigitoVerificador(vp_guia, vp_operacion) != 0L) {
            return true;
        }
        return false;
    }

    public static boolean isFormatoCorrectoGuia(String guia) {
        return guia.matches("^(G)?\\d{3,15}$");
    }

    public static Boolean validateCedulaEcuador(String cedula) {
        Map<Boolean, String> result = new HashMap<>();

        int vl_total = 0;
        int vl_cifra = 0;

        cedula = cedula.trim();

        if (cedula.length() != 10) {
            // Mal tamaño de la cedula
            result.put(false, "La Cedula de Identidad no tiene 10 caracteres.");
            return false;
        }

        if (Long.valueOf(cedula) == 0) {
            result.put(false, "El número de Cedula de Identidad es incorrecta.");
            return false;
        }

        Log.d(TAG, "Dos primeros caracteres: " + cedula.substring(0, 2));
        if (Integer.parseInt(cedula.substring(0, 2)) > 24) {
            // Codigo de la provincia no puede ser mas de 25
            result.put(false, "La provincia en la Cedula de Identidad es incorrecta.");
            return false;
        }

        Log.d(TAG, "Tercer caracter: " + cedula.substring(2, 3));
        if (Integer.parseInt(cedula.substring(2, 3)) > 5) {
            // Tercer Digito debe ser menor a 5
            result.put(false, "El número de Cedula de Identidad es incorrecta.");
            return false;
        }

        for (int i = 1; i < 10; i++) {
            if (i % 2 == 0) {
                vl_cifra = Integer.parseInt(cedula.substring(i - 1, i));
            } else {
                vl_cifra = Integer.parseInt(cedula.substring(i - 1, i)) * 2;

                if (vl_cifra > 9) {
                    vl_cifra -= 9;
                }
            }

            vl_total += vl_cifra;
        }

        vl_cifra = vl_total % 10;

        Log.d(TAG, "vl_cifra: " + vl_cifra);
        if (vl_cifra > 0) {
            vl_cifra = 10 - vl_cifra;
        }
        Log.d(TAG, "vl_cifra: " + vl_cifra);

        Log.d(TAG, "Primer Digito: " + cedula.substring(0, 1));
        Log.d(TAG, "Ultimo Digito: " + cedula.substring(9));
        if (vl_cifra == Integer.parseInt(cedula.substring(9))) {
            result.put(true, "Numero de Cedula de Identidad Correcta.");
            return true;
        } else {
            result.put(false, "Numero de Cedula de Identidad es Incorrecta.");
            return false;
        }
    }

    public static Boolean validateCedulaChile(String cedula) {
        Map<Boolean, String> result = new HashMap<>();
        cedula = cedula.trim().toUpperCase();

        int vl_total = 0;
        int vl_cifra = 0;
        int vl_multiplica = 0;

        if (cedula.length() != 9) {
            // Mal tamaño de la cedula
            result.put(false, "La Cedula de Identidad no tiene 9 caracteres.");
            return false;
        }

        for (int i = 1; i < 9; i++) {
            if (i == 1) {
                vl_multiplica = 3;
            } else if (i == 2) {
                vl_multiplica = 2;
            } else if (i == 3) {
                vl_multiplica = 7;
            } else if (i == 4) {
                vl_multiplica = 6;
            } else if (i == 5) {
                vl_multiplica = 5;
            } else if (i == 6) {
                vl_multiplica = 4;
            } else if (i == 7) {
                vl_multiplica = 3;
            } else if (i == 8) {
                vl_multiplica = 2;
            }

            try {
                vl_total = vl_total + Integer.parseInt(cedula.substring(i - 1, i)) * vl_multiplica;
//            vl_total = vl_total + substr(va_cedula,a,1) * vl_multiplica;
            } catch (NumberFormatException ex) {
                result.put(false, "Numero de Cedula de Identidad es Incorrecta.");
                return false;
            }
        }

        vl_cifra = 11 - (vl_total % 11);

        if (vl_cifra == 10 || vl_cifra == 11) {
            if (cedula.substring(8).equals("0") || cedula.substring(8).equals("K")) {
                result.put(true, "Numero de Cedula de Identidad Correcta.");
                return true;
            } else {
                result.put(false, "Numero de Cedula de Identidad es Incorrecta.");
                return false;
            }
        }

        if (cedula.substring(8).equals((vl_cifra + ""))) {
            result.put(true, "Numero de Cedula de Identidad Correcta.");
            return true;
        } else {
            result.put(false, "Numero de Cedula de Identidad es Incorrecta.");
            return false;
        }
    }

    public boolean ValidateIdentificationDocumentPeru(String identificationDocument) {
        if (!identificationDocument.isEmpty()) {
            int addition = 0;
            int[] hash = { 5, 4, 3, 2, 7, 6, 5, 4, 3, 2 };
            int identificationDocumentLength = identificationDocument.length();

//            String identificationComponent = identificationDocument.Substring(0, identificationDocumentLength - 1);
            String identificationComponent = identificationDocument.substring(0, identificationDocumentLength - 1);

            int identificationComponentLength = identificationComponent.length();

            int diff = hash.length - identificationComponentLength;

            for (int i = identificationComponentLength - 1; i >= 0; i--) {
                addition += (identificationComponent.charAt(i) - '0') * hash[i + diff];
            }

            addition = 11 - (addition % 11);

            if (addition == 11) {
                addition = 0;
            }

            char last = Character.toUpperCase(identificationDocument.charAt(identificationDocumentLength - 1));

            if (identificationDocumentLength == 11) {
                // The identification document corresponds to a RUC.
                return addition == last - '0';
            } else if (Character.isDigit(last)) {
                // The identification document corresponds to a DNI with a number as verification digit.
                char[] hashNumbers = { '6', '7', '8', '9', '0', '1', '1', '2', '3', '4', '5' };
                return last == hashNumbers[addition];
            } else if (Character.isLetter(last)) {
                // The identification document corresponds to a DNI with a letter as verification digit.
                char[] hashLetters = { 'K', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J' };
                return last == hashLetters[addition];
            }
        }
        return false;
    }

    public static boolean validateDigitoValido(String guia) {
        String vl_guia = guia.trim();
        int vl_dig_c;
        int vl_num;
        int vl_dig;
        int vl_valor;
        int vl_factor;
        int vl_verif;

        try {
            int num = Integer.parseInt(guia);
            if (num == 0) return false;
        } catch (NumberFormatException ex) {
            return false;
        }

        vl_guia = vl_guia.substring(0, guia.trim().length() - 1); // guia sin digito verificador
        vl_verif = Integer.parseInt(guia.trim().substring(guia.trim().length() - 1, guia.trim().length())); // digito verificador

        Log.d("ACTIVITY", "guia sin digito verificador: " + vl_guia);
        Log.d("ACTIVITY", "digito verificador: " + vl_verif);

        guia = vl_guia;

        vl_factor = 2;
        vl_valor = 0;

        for (int i = 1; i <= vl_guia.length(); i++) {
            vl_dig_c = Integer.parseInt(guia.trim().substring(guia.length() - 1, guia.trim().length()));
            Log.d("ACTIVITY", "DIGITO: " + vl_dig_c);

            if (vl_dig_c > 9) {
                return false;
            }

            vl_dig = vl_dig_c;
            vl_valor = vl_valor + (vl_dig * vl_factor);
            vl_factor++;

            if (vl_factor == 8) {
                vl_factor = 2;
                Log.d("ACTIVITY", "FACTOR");
            }

            guia = guia.trim().substring(0, guia.trim().length() - 1);
            Log.d("ACTIVITY", "SUBSTRING: " + guia);
        }

        vl_num = vl_valor % 11;
        Log.d("ACTIVITY", "VL_NUM: " + vl_num);

        vl_dig = 11 - vl_num;

        Log.d("ACTIVITY", "VL_DIG: " + vl_dig);

        if (vl_dig == 11) {
            vl_dig = 0;
        }

        if (vl_dig == 10) {
            vl_dig = 1;
        }

        Log.d("ACTIVITY", "VL_DIG: " + vl_dig);

        return vl_dig == vl_verif;
    }
}


