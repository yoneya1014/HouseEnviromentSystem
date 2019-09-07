package com.yoneya1014.jetfarm_test1

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.*

class MessagingService : FirebaseMessagingService() {

    private var mDb: FirebaseFirestore? = null

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        val notification = remoteMessage.notification
        if (notification != null) {
            val intent = Intent(this, EmptyActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            val builder = NotificationCompat.Builder(applicationContext)
            builder.setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(notification.title)
                    .setContentText(notification.body)
                    .setDefaults(Notification.DEFAULT_SOUND or Notification.DEFAULT_VIBRATE or Notification.DEFAULT_LIGHTS)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channelId = getString(R.string.default_notification_channel_id)
                val channelName = getString(R.string.default_notification_channel_name)
                val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
                channel.enableLights(true)
                channel.enableVibration(true)
                manager.createNotificationChannel(channel)
                builder.setChannelId(channelId)
            }
            manager.notify(0, builder.build())
        }
    }

    override fun onNewToken(token: String) {
        mDb = FirebaseFirestore.getInstance()
        val userData = HashMap<String, Any>()
        userData["token"] = token
        mDb!!.collection("userData").document(UUID.randomUUID().toString())
                .set(userData)
                .addOnCompleteListener { task ->
                    if (!task.isSuccessful) {

                    }
                }
    }
}
