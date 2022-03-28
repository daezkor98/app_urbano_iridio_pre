package com.urbanoexpress.iridio3.util;

import android.content.Intent;
import android.provider.Settings;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;

import com.urbanoexpress.iridio3.R;
import com.urbanoexpress.iridio3.ui.helpers.ModalHelper;
import com.urbanoexpress.iridio3.util.network.Connectivity;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

import java.io.IOException;
import java.net.InetAddress;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

/**
 * Created by mick on 13/03/17.
 */

public class DateSystemHelper {

    public static final String TIME_SERVER = "time-a.nist.gov";

    public void validateDateSytem(final AppCompatActivity activity) {
        Log.d("DateSystemHelper", "validateDateSystem");
        if (Connectivity.isConnectedFast(activity)) {
            new Thread(() -> {
                try {
                    NTPUDPClient timeClient = new NTPUDPClient();
                    InetAddress inetAddress = InetAddress.getByName(TIME_SERVER);
                    TimeInfo timeInfo = timeClient.getTime(inetAddress);
                    long localTime = timeInfo.getReturnTime(); //local device time
                    long networkTime = timeInfo.getMessage().getTransmitTimeStamp().getTime(); //server time

                    LocalDateTime ldt = LocalDateTime.now();
                    LocalDateTime ndt = LocalDateTime.ofInstant(Instant.ofEpochMilli(networkTime), ZoneId.systemDefault());

                    Log.d("MainActivity", "Time from LDT: " + LocalDateTime.ofInstant(Instant.ofEpochMilli(localTime), ZoneId.systemDefault()).toString());
                    Log.d("MainActivity", "Time from LDT: " + ldt.toString());
                    Log.d("MainActivity", "Time from NDT: " + ndt.toString());

                    LocalDateTime minLDT = ndt.minusMinutes(5);
                    LocalDateTime maxLDT = ndt.plusMinutes(5);

                    Log.d("MainActivity", "MIN MINUTE FROM NDT: " + minLDT.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)));
                    Log.d("MainActivity", "MAX MINUTE FROM NDT: " + maxLDT.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)));

                    if (!(ldt.getDayOfMonth() == ndt.getDayOfMonth() &&
                            ldt.getMonthValue() == ndt.getMonthValue() &&
                            ldt.getYear() == ndt.getYear() &&
                            ldt.getHour() == ndt.getHour() &&
                            ((ldt.isEqual(minLDT) || ldt.isAfter(minLDT)) &&
                                    (ldt.isEqual(maxLDT) || ldt.isBefore(maxLDT))))) {
                        activity.runOnUiThread(() -> ModalHelper.getBuilderAlertDialog(activity)
                                .setTitle(R.string.text_advertencia)
                                .setMessage(R.string.act_main_message_date_time_incorrect)
                                .setCancelable(false)
                                .setPositiveButton(R.string.text_configurar, (dialog, which) -> {
                                    //intent.setComponent(new ComponentName("com.android.settings",
                                    //        "com.android.settings.DateTimeSettingsSetupWizard"));
                                    activity.startActivity(new Intent(Settings.ACTION_DATE_SETTINGS));
                                }).show());
                    }
                } catch (IOException | DateTimeException ex) {
                    ex.printStackTrace();
                }
            }).start();
        } else {
            Log.d("DateSystemHelper", "CONECT IS LOW");
        }
    }

}
