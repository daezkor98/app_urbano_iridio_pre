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
import com.urbanoexpress.iridio3.pe.model.entity.Ruta;
import com.urbanoexpress.iridio3.pe.model.interactor.DataSyncInteractor;
import com.urbanoexpress.iridio3.pe.model.interactor.callback.RequestCallback;
import com.urbanoexpress.iridio3.pe.ui.RutaActivity;
import com.urbanoexpress.iridio3.pe.util.NotificationUtils;
import com.urbanoexpress.iridio3.pe.util.Session;
import com.urbanoexpress.iridio3.pe.util.network.Connectivity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by mick on 09/09/16.
 */

public class NuevasGuiasSync extends DataSyncModel<Ruta> {

    private static final String TAG = NuevasGuiasSync.class.getSimpleName();

    private static NuevasGuiasSync nuevasGuiasSync;

    private Context context;

    private Notification notification = null;
    private NotificationCompat.Builder notiBuilder = null;
    private PendingIntent pIntent;
    private TaskStackBuilder stackBuilder;
    private Uri soundUri;

    private DataSyncInteractor dataSyncInteractor;

    private NuevasGuiasSync(Context context) {
        this.context = context;
        this.dataSyncInteractor = new DataSyncInteractor(context);
    }

    public static NuevasGuiasSync getInstance(Context context) {
        if (nuevasGuiasSync == null) {
            nuevasGuiasSync = new NuevasGuiasSync(context);
        }
        return nuevasGuiasSync;
    }

    @Override
    public void sync() {
        Log.d(TAG, "TOTAL GUIAS ACTUALES: " + getTotalData());
        Log.d(TAG, "INITIALIZED_DATA_SYNC: " + isSyncDone());
        if (isSyncDone() && Session.getUser() != null) {
            Log.d(TAG, "INIT SYNC GUIAS ACTUALES");
            setSyncDone(false);
            loadData();
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
            RequestCallback callback = new RequestCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    try {
                        if (response.getBoolean("success")) {
                            Log.d(TAG, "NuevasGuiasSync UPLOADED");

                            JSONObject data = response.getJSONObject("data");
                            int totalGuiasNuevas = data.getInt("total_nuevas_guias");

                            if (totalGuiasNuevas > 0) {
                                if (totalGuiasNuevas == 1) {
                                    displayNotification("Ruta del Día",
                                            "Tiene " + totalGuiasNuevas + " guía pendiente por gestionar.");
                                } else {
                                    displayNotification("Ruta del Día",
                                            "Tiene " + totalGuiasNuevas + " guías pendientes por gestionar.");
                                }
                            }
                        } else {
                            Log.d(TAG, "NuevasGuiasSync");
                            Log.d(TAG, "success: false");
                        }
                        finishSync();
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                        finishSync();
                        Log.d(TAG, "NuevasGuiasSync");
                        Log.d(TAG, "JSONException");
                    }
                }

                @Override
                public void onError(VolleyError error) {
                    error.printStackTrace();
                    finishSync();
                }
            };

            String guias = "";

            for (int i = 0; i < getTotalData(); i++) {
                if ((i + 1) == getTotalData()) {
                    guias += getData().get(i).getIdServicio();
                } else {
                    guias += getData().get(i).getIdServicio() + "|";
                }
            }

            String params[] = new String[]{
                    Session.getUser().getIdRuta(),
                    Session.getUser().getDevicePhone(),
                    guias,
                    Session.getUser().getLineaValores(),
                    Session.getUser().getLineaLogistica(),
                    Session.getUser().getIdUsuario()
            };

            dataSyncInteractor.syncNuevasGuias(params, callback);
        } else {
            Log.d(TAG, "LA CONEXIÓN NO ES RAPIDA!!!");
            finishSync();
        }
    }

    @Override
    public void loadData() {
        if (dataSyncInteractor != null) {
            setData(dataSyncInteractor.selectAllRutas());
            setTotalData(getData().size());
        }
    }

    private void displayNotification(final String notTitulo, final String notMensaje) {
        // prepare intent which is triggered if the
        // notification is selected
        Intent intent = new Intent(context, RutaActivity.class);
//        PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        stackBuilder = TaskStackBuilder.create(context);
        // Adds the back stack
        stackBuilder.addParentStack(RutaActivity.class);
        // Adds the Intent to the top of the stack
        stackBuilder.addNextIntent(intent);
        // Gets a PendingIntent containing the entire back stack
        pIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // build notification
        // the addAction re-use the same intent to keep the example short
        soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        final String channelId = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? NotificationUtils.NEW_ROUTES_CHANNEL_ID : "";

        Glide.with(context)
                .asBitmap()
                .load(R.drawable.ic_package_notification)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        notiBuilder = new NotificationCompat.Builder(context, channelId)
                                .setContentTitle(notTitulo)
                                .setContentText(notMensaje)
                                .setSmallIcon(R.drawable.ic_urbano_notification)
                                .setLargeIcon(resource)
                                .setContentIntent(pIntent)
                                .setVibrate(new long[]{100, 100, 100})
                                .setPriority(Notification.PRIORITY_HIGH)
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

                        notificationManager.notify(0, notification);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
    }
}