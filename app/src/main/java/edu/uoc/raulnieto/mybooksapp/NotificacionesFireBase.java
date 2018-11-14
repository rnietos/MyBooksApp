package edu.uoc.raulnieto.mybooksapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import edu.uoc.raulnieto.mybooksapp.model.LibroDatos;

import static android.app.Notification.EXTRA_NOTIFICATION_ID;

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
        sendNotification(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody(),remoteMessage.getData().get("book_position"));

    }

    private void sendNotification(String titulo, String cuerpo, String bookpos) {

        int id = Integer.parseInt(bookpos);
        //Creamos el intent al que irá la acción Borrar de la notificación
        Intent intentBorrar = new Intent(this, BookListActivity.class);
        intentBorrar.putExtra("book_position",id);
        intentBorrar.setAction(LibroDatos.ACTION_BORRAR).
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        //Creamos el intent al que irá la acción Ver Detalle de la notificación
        Intent intentVer = new Intent(this, BookListActivity.class);
        intentVer.putExtra("book_position",id);
        intentVer.setAction(LibroDatos.ACTION_VER)
        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        //Preparamos las acciones que ejecutarán los botones de la notificación
        PendingIntent borrarPendingIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intentBorrar, 0);
        PendingIntent verPendingIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intentVer, 0);

        //Preparamos la notificación
        NotificationCompat.BigTextStyle estilo = new NotificationCompat.BigTextStyle().bigText(cuerpo);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, LibroDatos.CHANNEL_ID)
                .setContentTitle(titulo)
                .setContentText(cuerpo)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setStyle(estilo)
                .setAutoCancel(true)
                .addAction(R.drawable.libro, LibroDatos.ACTION_BORRAR,
                        borrarPendingIntent)
                .addAction(R.drawable.libro, LibroDatos.ACTION_VER,
                verPendingIntent);
        //Comprobamos si es necesario crear un canal según la versión de android
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.i("TAG", "crea canal");
            CharSequence name = LibroDatos.CHANNEL_ID;
            String description = "Descripcion canal";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(LibroDatos.CHANNEL_ID, name, importance);
            channel.setDescription(description);
            //Registramos el canal
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        //Generamos la notificación.
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1, mBuilder.build());

        Log.d("TAG","Notificacion enviada");
    }
}
