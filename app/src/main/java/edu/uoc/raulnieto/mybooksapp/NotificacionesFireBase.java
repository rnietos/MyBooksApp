package edu.uoc.raulnieto.mybooksapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.provider.Settings;
import androidx.core.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import edu.uoc.raulnieto.mybooksapp.model.LibroDatos;

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
        Log.i("TAG", "NOTIFICACION");
        int id = 0;
        if (bookpos != null)
            id = Integer.parseInt(bookpos);
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

        long[] patronVibracion = new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400};

        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(getApplicationContext().NOTIFICATION_SERVICE);
        //Comprobamos si es necesario crear un canal según la versión de android
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.i("TAG", "crea canal");
            CharSequence name = LibroDatos.CHANNEL_ID;
            String description = "Descripcion canal";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(LibroDatos.CHANNEL_ID, name, importance);
            /*AudioAttributes att = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build();
            channel.setSound(Settings.System.DEFAULT_NOTIFICATION_URI, att );*/
            channel.setDescription(description);
            channel.enableLights(true);
            channel.setLightColor(Color.BLUE);
            channel.enableVibration(true);
            channel.setVibrationPattern(patronVibracion);
            //Registramos el canal
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
        }

        //Preparamos la notificación
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, LibroDatos.CHANNEL_ID)
                .setContentTitle(titulo)
                .setContentText(cuerpo)
                .setSmallIcon(R.drawable.libro)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setVibrate(patronVibracion)
                .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.libro))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setLights(Color.BLUE,200,200)
                .setAutoCancel(true)
                .addAction(R.drawable.libro, LibroDatos.ACTION_BORRAR,
                        borrarPendingIntent)
                .addAction(R.drawable.libro, LibroDatos.ACTION_VER,
                verPendingIntent);
        //Generamos la notificación.
        assert notificationManager != null;
       // mBuilder.setChannelId(LibroDatos.CHANNEL_ID);
        notificationManager.notify(LibroDatos.TAGNOTIF_ID,LibroDatos.NOTIF_ID, mBuilder.build());
        //notificationManager.notify("noti");

        Log.d("TAG","Notificacion enviada");
    }
}
