package com.urbanoexpress.iridio3.util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.os.Build;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.urbanoexpress.iridio3.R;

public class NotificationUtils extends ContextWrapper {

    private NotificationManager mManager;

    public static final String DATA_SYNC_CHANNEL_ID                 = "com.urbanoexpress.iridio3.channel.id.DATA_SYNC";

    public static final String GENERAL_NOTIFICATIONS_CHANNEL_ID     = "com.urbanoexpress.iridio3.channel.id.GENERAL_NOTIFICATIONS";

    public static final String NEW_ROUTES_CHANNEL_ID                = "com.urbanoexpress.iridio3.channel.id.NEW_ROUTES";

    public static final String ROUTES_NOTIFICATIONS_CHANNEL_ID      = "com.urbanoexpress.iridio3.channel.id.ROUTES_NOTIFICATIONS";

    public static final String DATA_SYNC_CHANNEL_NAME               = "Sincronización de datos";

    public static final String GENERAL_NOTIFICATIONS_CHANNEL_NAME   = "Notificaciones generales";

    public static final String NEW_ROUTES_CHANNEL_NAME              = "Rutas nuevas";

    public static final String ROUTES_NOTIFICATIONS_CHANNEL_NAME    = "Notificaciones en ruta";

    public NotificationUtils(Context base) {
        super(base);
    }

    @RequiresApi(Build.VERSION_CODES.O)
    public void createChannels() {
        // create android channel
        NotificationChannel androidChannel = new NotificationChannel(DATA_SYNC_CHANNEL_ID,
                DATA_SYNC_CHANNEL_NAME, NotificationManager.IMPORTANCE_NONE);
        /*// Sets whether notifications posted to this channel should display notification lights
        androidChannel.enableLights(true);
        // Sets whether notification posted to this channel should vibrate.
        androidChannel.enableVibration(true);
        // Sets the notification light color for notifications posted to this channel
        androidChannel.setLightColor(Color.GREEN);*/
        // Sets whether notifications posted to this channel appear on the lockscreen or not
        androidChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        getManager().createNotificationChannel(androidChannel);

        androidChannel = new NotificationChannel(GENERAL_NOTIFICATIONS_CHANNEL_ID,
                GENERAL_NOTIFICATIONS_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
        androidChannel.enableLights(true);
        androidChannel.enableVibration(true);
        androidChannel.setLightColor(Color.RED);
        androidChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        getManager().createNotificationChannel(androidChannel);

        androidChannel = new NotificationChannel(NEW_ROUTES_CHANNEL_ID,
                NEW_ROUTES_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
        androidChannel.enableLights(true);
        androidChannel.enableVibration(true);
        androidChannel.setLightColor(Color.RED);
        androidChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        getManager().createNotificationChannel(androidChannel);

        androidChannel = new NotificationChannel(ROUTES_NOTIFICATIONS_CHANNEL_ID,
                ROUTES_NOTIFICATIONS_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
        androidChannel.enableLights(true);
        androidChannel.enableVibration(true);
        androidChannel.setLightColor(Color.RED);
        androidChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        getManager().createNotificationChannel(androidChannel);
    }


    public static Notification getDataSyncChannelNotification(Context context) {
        String channelId = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? DATA_SYNC_CHANNEL_ID : "";

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, channelId);

        return notificationBuilder.setOngoing(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .build();
    }

    private NotificationManager getManager() {
        if (mManager == null) {
            mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return mManager;
    }
}