package com.urbanoexpress.iridio3.pe.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Vibrator;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.urbanoexpress.iridio3.data.local.PreferencesHelper;
import com.urbanoexpress.iridio3.pe.model.entity.Usuario;
import com.urbanoexpress.iridio3.pe.util.constant.Country;
import com.urbanoexpress.iridio3.pe.util.network.Connection;
import com.urbanoexpress.iridio3.pe.util.network.Connectivity;
import com.urbanoexpress.iridio3.pe.view.BaseModalsView;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;

/**
 * Created by mick on 24/05/16.
 */
public class CommonUtils {

    private static final String TAG = CommonUtils.class.getSimpleName();

    public static final int VIBRATE_LONG_CLICK = 22; // milliseconds

    public static String getSHA1(String value) {
        String sha1 = "";
        try {
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(value.getBytes("UTF-8"));
            sha1 = byteToHex(crypt.digest());
        } catch(NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        } catch(UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }
        Log.d(TAG, "getSHA1: " + sha1);
        return sha1;
    }

    public static void vibrateDevice(Context context, long milliseconds) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(milliseconds);
//        vibrator.vibrate(new long[]{0, 200, 50, 200, 50, 200, 50, 200, 50, 200, 50, 200, 50, 200, 50, 200, 50, 200, 50, 200, 50, 200, 50,  }, -1);
    }

    private static String byteToHex(final byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash)
        {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }

    public static void changeColorStatusBar(Activity activity, int color) {
        if (isAndroidLollipop()) {
            activity.getWindow().setStatusBarColor(ContextCompat.getColor(activity, color));
        }
    }

    public static boolean isAndroidLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public static boolean isAndroid10() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q;
    }

    public static boolean isAndroidPie() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.P;
    }

    public static PackageInfo getPackageInfo(Context context) {
        try {
            Log.d(TAG, "PackageName: " + context.getPackageName());
            Log.d(TAG, "PackageCodePath: " + context.getPackageCodePath());
            Log.d(TAG, "PackageResourcePath: " + context.getPackageResourcePath());
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static boolean isActivityDestroyed(Context context) {
        if (context == null) {
            return true;
        }

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            if (((AppCompatActivity) context).isDestroyed()) {
                return true;
            }
        } else {
            if (((AppCompatActivity) context).isFinishing()) {
                return true;
            }
        }

        return false;
    }

    public static boolean isValidCoords(String latitude, String longitude) {

        if (latitude==null || longitude==null){
            return false;
        }

        if (latitude.length() > 0 && longitude.length() > 0) {
            try {
                double dLatitude = Double.parseDouble(latitude);
                double dLongitude = Double.parseDouble(longitude);

                if (dLatitude != 0 && dLongitude != 0) {
                    return true;
                }
            } catch (NumberFormatException ex) {
                ex.printStackTrace();
            }
        }

        return false;
    }

    public static boolean isValidCoords(Double latitude, Double longitude) {
        return latitude != 0 && longitude != 0;
    }

    public static String getCountryIso(Context context) {
        //        return context.getResources().getConfiguration().locale.getCountry();
        //        return Locale.getDefault().getCountry();
        return InfoDevice.getTelephonyManager(context).getNetworkCountryIso();
    }

    public static String getNameCountryCurrent(Context context) {
        int country = new PreferencesHelper(context).getCountry();
        switch (country) {
            case Country.ECUADOR:
                return "Ecuador";
            case Country.PERU:
                return "Perú";
            case Country.CHILE:
                return "Chile";
        }
        return "";
    }

    public static void setVisibilityOptionMenu(Menu menu, int id, boolean visibility) {
        try {
            menu.findItem(id).setVisible(visibility);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    public static Drawable changeColorDrawable(Context context, int resIcon, int resColor) {
        Drawable drawable = ContextCompat.getDrawable(context, resIcon);
        drawable = DrawableCompat.wrap(drawable);
        drawable.setColorFilter(ContextCompat.getColor(context, resColor), PorterDuff.Mode.MULTIPLY);
        return drawable;
    }

    public static Drawable changeColorDrawable(Context context, Drawable icon, int resColor) {
        Drawable drawable = DrawableCompat.wrap(icon);
        drawable.setColorFilter(ContextCompat.getColor(context, resColor), PorterDuff.Mode.MULTIPLY);
        return drawable;
    }

    public static int getActionBarDimensionPixelSize(Context context) {
        // Calculate ActionBar height
        TypedValue tv = new TypedValue();
        if (context.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            return TypedValue.complexToDimensionPixelSize(tv.data, context.getResources().getDisplayMetrics());
        }
        return 0;
    }

    private void setBackground(Context context, View view, int resIcon) {
        view.setBackground(ContextCompat.getDrawable(context, resIcon));
    }

    public static void showOrHideKeyboard(Context context, boolean showKeyboard, View view) {
        InputMethodManager imm = (InputMethodManager)
                context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (showKeyboard) {
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        } else {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static boolean isToday(long when) {
        Calendar calendar = toCalendar(
                new Date(when)
        );

        int thenYear = calendar.get(Calendar.YEAR);
        int thenMonth = calendar.get(Calendar.MONTH);
        int thenMonthDay = calendar.get(Calendar.DAY_OF_MONTH);

        calendar = Calendar.getInstance();
        return (thenYear == calendar.get(Calendar.YEAR))
                && (thenMonth == calendar.get(Calendar.MONTH))
                && (thenMonthDay == calendar.get(Calendar.DAY_OF_MONTH));
    }

    public static Calendar toCalendar(final Date date) {
        final Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c;
    }

    public static boolean validateConnectivity(final Context context) {
        if (Connectivity.isConnectedFast(context)) {
            return true;
        } else {
            try {
                ((AppCompatActivity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        BaseModalsView.hideProgressDialog();
                        Connection.showMessageNotConnectedToNetwork(context);
                    }
                });
            } catch (ClassCastException ex) {
                ex.printStackTrace();
            }
        }
        return false;
    }

    public static String getFormatHora(String horario) {
        if (!horario.isEmpty()) {
            try {
                Date dateHora = new SimpleDateFormat("h:mm").parse(horario);
                horario = new SimpleDateFormat("h:mm a").format(dateHora);
            } catch (ParseException ex) {
                ex.printStackTrace();
            }
        } else {
            horario = "00:00";
        }
        return horario;
    }

    public static String fomartHorarioAproximado(long horarioAproximado, boolean isFormatLarge) {
        String horario = "";
        if (horarioAproximado != 0L) {
            try {
                Date dateFechaActual = new SimpleDateFormat("dd/MM/yyyy").parse(
                        new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
                Date dateHorarioAproximado = new SimpleDateFormat("dd/MM/yyyy").parse(
                        new SimpleDateFormat("dd/MM/yyyy").format(new Date(horarioAproximado)));

                if (dateHorarioAproximado.before(dateFechaActual)) {
                    dateHorarioAproximado = new Date(horarioAproximado);
                    if (isFormatLarge) {
                        horario = new SimpleDateFormat("dd/MM/yyyy h:mm a").format(dateHorarioAproximado);
                    } else {
                        horario = new SimpleDateFormat("dd/MM/yyyy").format(dateHorarioAproximado);
                    }
                } else {
                    dateHorarioAproximado = new Date(horarioAproximado);
                    horario = new SimpleDateFormat("h:mm a").format(dateHorarioAproximado);
                }
            } catch (ParseException ex) {
                ex.printStackTrace();
            }
        } else {
            horario = "00:00";
        }
        return horario;
    }

    public static long getNoOfDaysBetweenDateAndNow(String dateString) {
        if (!dateString.isEmpty()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            LocalDate dateBefore = LocalDate.parse(dateString, formatter);
            LocalDate dateAfter = LocalDate.now();

            return dateBefore.until(dateAfter, ChronoUnit.DAYS);
        }
        return -1;
    }

    public static String parseDateToElapsedDays(String dateString) {
        long nroDias = CommonUtils.getNoOfDaysBetweenDateAndNow(dateString);
        if (nroDias == 0) {
            return "hoy";
        } else if (nroDias > 0) {
            return nroDias == 1 ? nroDias + " día" : nroDias + " días";
        } else {
            return dateString;
        }
    }

    public static Double parseDouble(String value) {
        double d = 0.0;

        try {
            d = Double.parseDouble(value);
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
        }

        return d;
    }

    public static void deleteUserData() {
        Preferences.getInstance().edit()
                .putString("idUsuario", "")
                //.putString("usuario", "")
                .putString("nombre", "")
                .putString("tipoUsuario", "")
                .putString("codigoProvincia", "")
                .putString("nombreProvincia", "")
                .putString("siglaProvincia", "")
                .putString("perfil", "")
                .putString("tiempoRequestGPS", "")
                .putString("tiempoRequestDatos", "")
                .putString("lineaPostal", "")
                .putString("lineaValores", "")
                .putString("lineaLogistica", "")
                .putString("lineaLogisticaEspecial", "")
                .putLong("inicioSerieRecoleccion", 0)
                .putBoolean("menuAppAvailable", false)
                //.putBoolean("courierDisponibleRutaExpress", false)
                .putInt("idRuta", 0).apply();

        Usuario.deleteAll(Usuario.class);
    }

    public static void playSoundOnScanBarcode(Context context, int resIdSound) {
        AudioManager audioManager =
                (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                audioManager.getStreamMaxVolume(audioManager.STREAM_MUSIC), 0);
        MediaPlayer mp = MediaPlayer.create(context, resIdSound);
        mp.start();
    }

}
