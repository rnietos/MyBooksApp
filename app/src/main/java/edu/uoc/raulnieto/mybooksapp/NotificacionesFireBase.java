package edu.uoc.raulnieto.mybooksapp;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class NotificacionesFireBase extends FirebaseMessagingService {

    /**
     * Método llamado cuando se recibe un mensaje remoto
     *
     * @param remoteMessage Mensaje recibido de Firebase Cloud Messaging.
     */

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Mostrar una notificación al recibir un mensaje de Firebase, utilzamos la función
        // sendNotification para generar la notificación al recibir la notificación remota
        //Log.d("TAG","Notificacion detectada");

        sendNotification(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody());
    }

    private void sendNotification(String titulo, String cuerpo) {
        //Preparamos la notificación, configurando las opciones que mostrará
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this,"canal_mybooks")
                        .setSmallIcon(android.R.drawable.stat_sys_warning)
                        .setContentTitle(titulo)
                        .setContentText(cuerpo);
        //Accedemos al gestor de notificaciones
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //Lanzamos la notificación.
        notificationManager.notify(0, notificationBuilder.build());
    }
}
