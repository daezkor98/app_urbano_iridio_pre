package com.urbanoexpress.iridio3.pe.data.sync;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.core.content.ContextCompat;
import android.util.Log;

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.urbanoexpress.iridio3.R;
import com.urbanoexpress.iridio3.pe.data.rest.ApiRequest;
import com.urbanoexpress.iridio3.pe.data.rest.ApiRest;
import com.urbanoexpress.iridio3.pe.model.entity.Ruta;
import com.urbanoexpress.iridio3.pe.util.CommonUtils;
import com.urbanoexpress.iridio3.pe.util.NotificationUtils;
import com.urbanoexpress.iridio3.pe.util.Session;
import com.urbanoexpress.iridio3.pe.util.network.Connectivity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by mick on 16/11/16.
 */

public class NewVersionAppSync extends DataSyncModel<Ruta> {

    private static final String TAG = NewVersionAppSync.class.getSimpleName();

    private static NewVersionAppSync newVersionAppSync;

    private Context context;

    private Notification notification = null;
    private NotificationCompat.Builder notiBuilder = null;
    private PendingIntent pIntent;
    private TaskStackBuilder stackBuilder;
    private Uri soundUri;

    private NewVersionAppSync(Context context) {
        this.context = context;
    }

    public static NewVersionAppSync getInstance(Context context) {
        if (newVersionAppSync == null) {
            newVersionAppSync = new NewVersionAppSync(context);
        }
        return newVersionAppSync;
    }

    @Override
    public void sync() {
        if (isSyncDone() && Session.getUser() != null) {
            setSyncDone(false);
            executeSync();
        }
    }

    @Override
    public void finishSync() {
        setCountData(0);
        setTotalData(0);
        setSyncDone(true);
    }

    @Override
    protected void executeSync() {
        if (Connectivity.isConnectedFast(context)) {
            //if (PermissionUtils.checkPermissions(context, Manifest.permission.READ_PHONE_STATE)) {
                ApiRequest.getInstance().newParams();
                ApiRequest.getInstance().putParams("version_name",
                        CommonUtils.getPackageInfo(context).versionName);
                ApiRequest.getInstance().putParams("version_code",
                        CommonUtils.getPackageInfo(context).versionCode + "");
                ApiRequest.getInstance().putParams("device_imei", Session.getUser().getDevicePhone());
                ApiRequest.getInstance().putParams("device_model", Build.MODEL);
                ApiRequest.getInstance().putParams("version_os", Build.VERSION.RELEASE);
                ApiRequest.getInstance().putParams("vp_id_user", Session.getUser().getIdUsuario());
                ApiRequest.getInstance().request(ApiRest.getInstance().getApiBaseUrl() +
                                ApiRest.Api.VALIDATE_VERSION_APP,
                        ApiRequest.TypeParams.FORM_DATA, new ApiRequest.ResponseListener() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    if (!response.getBoolean("is_updated")) {
                                        displayNotification(context.getString(R.string.app_name),
                                                "Hay una nueva versión disponible para instalar.");
                                    }
                                } catch (JSONException ex) {
                                    ex.printStackTrace();
                                }
                                finishSync();
                            }

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                error.printStackTrace();
                                finishSync();
                            }
                        });
            /*} else {
                Log.d(TAG, "ERROR DE PERMISO AL OBTENER EL IMEI");
                finishSync();
            }*/
        } else {
            Log.d(TAG, "LA CONEXIÓN NO ES RAPIDA!!!");
            finishSync();
        }
    }

    @Override
    public void loadData() { }

    private void displayNotification(final String notiTitulo, final String notiMensaje) {
        Intent intent;
        String appPackageName = context.getPackageName();
        try {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName));
        } catch (android.content.ActivityNotFoundException anfe) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName));
        }

        stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntent(intent);
        pIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        final String channelId = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? NotificationUtils.GENERAL_NOTIFICATIONS_CHANNEL_ID : "";

        Glide.with(context)
                .asBitmap()
                .load(R.drawable.ic_package_notification)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        notiBuilder = new NotificationCompat.Builder(context, channelId)
                                .setContentTitle(notiTitulo)
                                .setContentText(notiMensaje)
                                .setSmallIcon(R.drawable.ic_urbano_notification)
                                .setLargeIcon(resource)
                                .setContentIntent(pIntent)
                                .setVibrate(new long[]{100, 100, 100})
                                .setPriority(Notification.PRIORITY_MAX)
                                .setSound(soundUri)
                                .setOngoing(true)
                                .setAutoCancel(true);

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            notiBuilder.setColor(ContextCompat.getColor(context, R.color.colorPrimary));
                            notiBuilder.setVisibility(Notification.VISIBILITY_PUBLIC);
                            notiBuilder.setCategory(Notification.CATEGORY_SOCIAL);
                        }

                        notification = notiBuilder.build();

                        NotificationManager notificationManager =
                                (NotificationManager) context.getSystemService(Service.NOTIFICATION_SERVICE);

                        notificationManager.notify(1, notification);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
    }
}