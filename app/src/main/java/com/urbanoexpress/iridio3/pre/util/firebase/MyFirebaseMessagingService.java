package com.urbanoexpress.iridio3.pre.util.firebase;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.urbanoexpress.iridio3.pre.R;
import com.urbanoexpress.iridio3.pre.model.entity.Ruta;
import com.urbanoexpress.iridio3.pre.model.interactor.NotificacionesRutaInteractor;
import com.urbanoexpress.iridio3.pre.ui.NotificacionesRutaActivity;
import com.urbanoexpress.iridio3.pre.util.NotificationUtils;
import com.urbanoexpress.iridio3.pre.util.Session;
import com.urbanoexpress.iridio3.pre.util.constant.LocalAction;

import org.apache.commons.text.WordUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMessagingService";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "CollapseKey: " + remoteMessage.getCollapseKey());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            sendNotification(remoteMessage.getData().get("title"),
                    remoteMessage.getData().get("body"),
                    remoteMessage.getData().get("guia"));

            if (Session.getUser() != null) {
                Session.getUser().setTotalNotificaciones(
                        Session.getUser().getTotalNotificaciones() + 1);
                Session.getUser().save();

                try {
                    if (remoteMessage.getData().get("id_servicio_notificacion").equals("3")) {
                        JSONObject data = new JSONObject(remoteMessage.getData().get("extra_data"));

                        Ruta guia = NotificacionesRutaInteractor.selectGuia(
                                Session.getUser().getIdUsuario(),
                                remoteMessage.getData().get("guia"),
                                remoteMessage.getData().get("linea_negocio"));

                        if (guia != null) {
                            guia.setGuiaRequerimiento("1");
                            guia.setMostrarAlerta(1);
                            guia.setGuiaRequerimientoComentario(data.getString("comentarios").toLowerCase());
                            guia.setGuiaRequerimientoHorario(data.getString("arco_horario"));
                            guia.setGuiaRequerimientoMotivo(WordUtils.capitalize(data.getString("tipo_motivo").toLowerCase()));
                            guia.setGuiaRequerimientoNuevaDireccion(data.getString("flag_direccion"));
                            guia.setDireccion(WordUtils.capitalize(data.getString("direccion").toLowerCase()));
                            guia.setGpsLatitude(data.getString("dir_px"));
                            guia.setGpsLongitude(data.getString("dir_py"));
                            guia.save();
                            Log.d(TAG, "Datos de la coordinacion fue grabada correctamente.");
                        } else {
                            Log.d(TAG, "No se encontro la guia.");
                        }

                    } else if (remoteMessage.getData().get("id_servicio_notificacion").equals("4")) {
                        JSONObject data = new JSONObject(remoteMessage.getData().get("extra_data"));

                        Ruta guia = NotificacionesRutaInteractor.selectGuia(
                                Session.getUser().getIdUsuario(),
                                remoteMessage.getData().get("guia"),
                                remoteMessage.getData().get("linea_negocio"));

                        if (guia != null) {
                            guia.setGuiaRequerimiento("1");
                            guia.setMostrarAlerta(1);
                            guia.setGuiaRequerimientoCHK(data.getString("chk_id_gestion"));
                            guia.setGuiaRequerimientoMotivo(WordUtils.capitalize(data.getString("tipo_motivo").toUpperCase()));
                            guia.save();
                            Log.d(TAG, "Datos de la coordinacion fue grabada correctamente.");
                        } else {
                            Log.d(TAG, "No se encontro la guia.");
                        }
                    }
                } catch (JSONException ex) {
                    ex.printStackTrace();
                    Log.d(TAG, "Error al procesar los datos extra de la notificacion.");
                }

                Intent intent = new Intent(LocalAction.REFRESCAR_CONTADOR_NOTIFICACIONES_ACTION);
                intent.putExtra("totalNotificaciones", String.valueOf(Session.getUser().getTotalNotificaciones()));
                LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(intent);
            }
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendNotification(String title, String messageBody, String guia) {
        /*Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);*/

        int idNotificacion = new Random().nextInt();

        Intent intent = new Intent(this, NotificacionesRutaActivity.class);
        //intent.putExtra("guia", guia);

        // Create the TaskStackBuilder and add the intent, which inflates the back stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(intent);
        // Get the PendingIntent containing the entire back stack
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        final String channelId = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                ? NotificationUtils.ROUTES_NOTIFICATIONS_CHANNEL_ID : "";

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setContentTitle(title)
                        .setContentText(messageBody)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody))
                        .setSmallIcon(R.drawable.ic_urbano_notification)
                        .setVibrate(new long[]{100, 100, 100})
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent)
                        .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                        .setCategory(NotificationCompat.CATEGORY_SOCIAL);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(idNotificacion, notificationBuilder.build());
    }

}