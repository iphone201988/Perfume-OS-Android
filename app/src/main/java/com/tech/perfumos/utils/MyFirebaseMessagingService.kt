package com.tech.perfumos.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.tech.perfumos.R

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "Token: $token")
        // Send this token to your app server to send targeted notifications later
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        // Handle FCM message and display notification
        Log.d("onMessageReceived", "onMessageReceived: ${Gson().toJson(remoteMessage)}")
        val title = remoteMessage.data["title"]
        val body = remoteMessage.data["body"]

        if (remoteMessage.data.isNotEmpty()) {
            val data = remoteMessage.data

            val quizId = data["quizId"]
            val type = data["type"]
            val title = data["title"]
            val body = data["body"]

            Log.d("FCM", "Quiz ID: $quizId")
            Log.d("FCM", "Type: $type")
            Log.d("FCM", "Title: $title")
            Log.d("FCM", "Body: $body")
        }

        // ✅ If it's a notification payload
        remoteMessage.notification?.let {
            val notificationTitle = it.title
            val notificationBody = it.body
            Log.d("FCM", "Notification Title: $notificationTitle")
            Log.d("FCM", "Notification Body: $notificationBody")
        }
        showNotification(title, body)
    }

    private fun showNotification(title: String?, body: String?) {
        val channelId = "default_channel"
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Default Channel",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }
        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.notifcation_icon)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
        notificationManager.notify(0, notification)
    }
}