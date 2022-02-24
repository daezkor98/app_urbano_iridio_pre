package com.urbanoexpress.iridio.work;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.core.content.ContextCompat;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.urbanoexpress.iridio.R;
import com.urbanoexpress.iridio.data.entity.VerifyUserSessionEntity;
import com.urbanoexpress.iridio.data.local.PreferencesHelper;
import com.urbanoexpress.iridio.data.remote.urbano.UrbanoApiManager;
import com.urbanoexpress.iridio.data.rest.ApiRest;
import com.urbanoexpress.iridio.services.DataSyncService;
import com.urbanoexpress.iridio.ui.InitActivity;
import com.urbanoexpress.iridio.util.CommonUtils;
import com.urbanoexpress.iridio.util.NotificationUtils;
import com.urbanoexpress.iridio.util.Session;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;

public class UserStatusWorker extends Worker {

    public static final String TAG = "UserStatusWorker";

    public UserStatusWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "Worker works");
        PreferencesHelper preferencesHelper = new PreferencesHelper(getApplicationContext());
        UrbanoApiManager.setApiBaseUrl(ApiRest.buildUrbanoApiBaseUrl(
                preferencesHelper.getApiEnvironment(), preferencesHelper.getCountry()));
        UrbanoApiManager urbanoApiManager = new UrbanoApiManager(getApplicationContext());
        Log.d(TAG, "Worker url: " + urbanoApiManager.getApiBaseUrl());

        PackageInfo packageInfo = CommonUtils.getPackageInfo(getApplicationContext());

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("username", "")
                .addFormDataPart("app_version_name", packageInfo.versionName)
                .addFormDataPart("app_version_code",  String.valueOf(packageInfo.versionCode))
                .addFormDataPart("device_phone", Session.getUser().getDevicePhone())
                .addFormDataPart("device_model", Build.MODEL)
                .addFormDataPart("device_version_os", Build.VERSION.RELEASE)
                .addFormDataPart("user_id", Session.getUser().getIdUsuario())
                .build();

        Call<VerifyUserSessionEntity> callSync =
                urbanoApiManager.getUserApi().verifyUserSession(requestBody);

        try {
            Response<VerifyUserSessionEntity> response = callSync.execute();
            VerifyUserSessionEntity apiResponse = response.body();

            Log.d(TAG, "Worker response: " + apiResponse);

            if (apiResponse.getApp().isUpdateRequired()) {
                displayNotification(apiResponse.getApp().getLatestVersionName());
            }

            if (apiResponse.getUser().getStatus().equalsIgnoreCase("inactive")) {
                CommonUtils.deleteUserData();
                Session.clearSession();
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(() -> {
                    Log.d(TAG, "Worker clear all tasks: " + apiResponse);
                    getApplicationContext().stopService(
                            new Intent(getApplicationContext(), DataSyncService.class));

                    Intent intent = new Intent(getApplicationContext(), InitActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    //getApplicationContext().startActivity(intent);
                    //TODO this line causes logout
                });

                WorkManager.getInstance(getApplicationContext()).cancelUniqueWork(UserStatusWorker.TAG);
            }

            return Result.success();
        } catch (Exception ex) {
            ex.printStackTrace();
            return Result.failure();
        }
    }

    private void displayNotification(String latestVersionName) {
        String appPackageName = getApplicationContext().getPackageName();
        Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName));
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
        stackBuilder.addNextIntent(intent);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        String channelId = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                ? NotificationUtils.GENERAL_NOTIFICATIONS_CHANNEL_ID : "";

        String message = "Hemos corregido algunos errores y mejorado el funcionamiento de la app para que tengas la mejor experiencia con esta nueva versión.📲";

        NotificationCompat.Builder builder = new NotificationCompat
                .Builder(getApplicationContext(), channelId)
                .setContentTitle("👋¡Actualiza tu app a la versión " + latestVersionName + "!")
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setSmallIcon(R.drawable.ic_urbano_notification)
                .setContentIntent(pendingIntent)
                .setVibrate(new long[]{100, 100, 100})
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setSound(soundUri)
                .setOngoing(true)
                .setAutoCancel(true)
                .setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary))
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setCategory(NotificationCompat.CATEGORY_SOCIAL);

        Notification notification = builder.build();
        NotificationManager notificationManager = (NotificationManager) getApplicationContext()
                .getSystemService(Service.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);
    }
}
