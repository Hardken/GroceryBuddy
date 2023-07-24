package com.example.grupo10;

import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Verifica si el mensaje contiene datos
        if (remoteMessage.getData().size() > 0) {
            // Aquí puedes manejar los datos del mensaje si es necesario
            // Los datos del mensaje están disponibles en remoteMessage.getData()
        }

        // Verifica si el mensaje contiene una notificación
        if (remoteMessage.getNotification() != null) {
            // Aquí puedes manejar la notificación recibida
            // remoteMessage.getNotification().getTitle() te dará el título de la notificación
            // remoteMessage.getNotification().getBody() te dará el cuerpo de la notificación

            // En este ejemplo, mostraremos la notificación como un Toast en el hilo principal
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();

            // Muestra la notificación en el hilo principal
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MyFirebaseMessagingService.this.getApplicationContext(), title + " -> " + body, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onNewToken(String token) {
        // Aquí obtendrás el nuevo token de registro del dispositivo
        // Puedes guardar este token en tu servidor o en la base de datos para enviar notificaciones a este dispositivo específico
        // El token se actualiza automáticamente cuando cambia, por lo que solo necesitas guardar el último token recibido
        // en tu servidor o base de datos.

        // Aquí puedes implementar el código para enviar el token al servidor, si es necesario.
        // Por ejemplo, podrías usar Retrofit o Volley para hacer una solicitud POST al servidor con el token.
        // En el servidor, puedes guardar el token asociado al usuario para enviar notificaciones a usuarios específicos.
    }
}
