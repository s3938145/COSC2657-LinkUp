package com.example.linkup.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.linkup.R;
import com.example.linkup.view.activities.MainActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Handle data payload
        Map<String, String> data = remoteMessage.getData();
        String dataTitle = "Default Title";
        String dataBody = "Default message";
        if (!data.isEmpty()) {
            dataTitle = data.getOrDefault("title", "Default Title");
            dataBody = data.getOrDefault("body", "Default message");
        }

        // Check if message contains a notification payload.
        RemoteMessage.Notification notification = remoteMessage.getNotification();
        if (notification != null) {
            sendNotification(notification.getTitle(), notification.getBody());
        } else {
            // Handle case where there is no notification payload, only data
            sendNotification(dataTitle, dataBody);
        }
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     */
    private void sendNotification(String messageTitle, String messageBody) {
        Intent intent = new Intent(this, MainActivity.class); // MainActivity should be your app's main activity
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        String channelId = getString(R.string.default_notification_channel_id); // Define this string in your strings.xml
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_like_icon_round) // Replace with your notification icon
                .setContentTitle(messageTitle)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since Android Oreo, notification channels are needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Channel human readable title", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        int notificationId = (int) System.currentTimeMillis();

        notificationManager.notify(notificationId, notificationBuilder.build());
        Log.d("FCM", "Notification sent");
    }

    @Override
    public void onNewToken(String token) {
        Log.d("FCM", "Refreshed token: " + token);
        new FirebaseService(getApplicationContext()).updateFcmTokenForCurrentUser(token);
    }
}
