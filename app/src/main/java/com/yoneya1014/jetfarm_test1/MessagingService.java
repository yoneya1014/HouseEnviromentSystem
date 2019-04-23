package com.yoneya1014.jetfarm_test1;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MessagingService extends FirebaseMessagingService {

    private FirebaseFirestore mDb;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        RemoteMessage.Notification notification = remoteMessage.getNotification();
        if (notification != null) {
            Intent intent = new Intent(this, EmptyActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
            builder.setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(notification.getTitle())
                    .setContentText(notification.getBody())
                    .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                String channelId = getString(R.string.default_notification_channel_id);
                String channelName = getString(R.string.default_notification_channel_name);
                NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
                channel.enableLights(true);
                channel.enableVibration(true);
                manager.createNotificationChannel(channel);
                builder.setChannelId(channelId);
            }
            manager.notify(0, builder.build());
        }
    }

    @Override
    public void onNewToken(String token) {
        mDb = FirebaseFirestore.getInstance();
        Map<String, Object> userData = new HashMap<>();
        userData.put("token", token);
        mDb.collection("userData").document(UUID.randomUUID().toString())
                .set(userData)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()) {

                        }
                    }
                });
    }
}
