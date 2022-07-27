package com.mac.ecomadminphp.FCM

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.mac.ecomadminphp.MainActivity
import com.mac.ecomadminphp.R
import com.mac.ecomadminphp.UserArea.Activities.Orders.My_Orders_Activity

import java.util.*

class FCMService : FirebaseMessagingService() {
    private val CHANNEL_ID = "grocery_id"
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        ShowNotification(message)

    }


    private fun ShowNotification(message: RemoteMessage) {
//        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.help_me_please);
//        mp.start();
        val intent = Intent(this, My_Orders_Activity::class.java)
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val notificationId = Random().nextInt()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent =
            PendingIntent.getActivity(baseContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val notification: Notification
        notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(message.data["title"])
            .setStyle(NotificationCompat.BigTextStyle())
            .setContentText(message.data["message"])
            .setSmallIcon(R.drawable.static_image)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()
        notificationManager.notify(notificationId, notification)
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val notificationChannel =
            NotificationChannel(CHANNEL_ID, "Grocery", NotificationManager.IMPORTANCE_HIGH)
        notificationChannel.description = "MyDesc"
        notificationChannel.enableLights(true)
        notificationChannel.lightColor = Color.WHITE
        notificationManager.createNotificationChannel(notificationChannel)
    }

}