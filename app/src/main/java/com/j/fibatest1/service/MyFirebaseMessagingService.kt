package com.j.fibatest1.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.j.fibatest1.R
import com.j.fibatest1.activity.FirebaseActivity
import com.j.fibatest1.data.DataFirebase

class MyFirebaseMessagingService : FirebaseMessagingService() {

    private var notificationManager: NotificationManager ?= null


    override fun onCreate() {
        super.onCreate()
        Log.d("FirebaseServiceLog", "onCreate")
    }

    override fun onDestroy() {
        notificationManager?.cancelAll()
        Log.d("FirebaseServiceLog", "OnDestroy")
        super.onDestroy()
    }

    override fun onMessageReceived(p0: RemoteMessage) {
        Log.d("FirebaseServiceLog", "onMessageReceived: ${p0.notification?.title}" +
                ", ${p0.notification?.body}")

        val data = DataFirebase(p0.data["name"], p0.data["age"]?.toInt(), p0.data["favorite"])
        startActivity(Intent(this, FirebaseActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            putExtra("title", p0.notification?.title)
            putExtra("body", p0.notification?.body)
            putExtra("data", data)
        })
//        sendNotification (p0.notification?.title, p0.notification?.body)
    }

    override fun onNewToken(p0: String) {
        Log.d("FirebaseServiceLog", "onNewToken: $p0")
    }

    private fun sendNotification(messageTitle: String ?= "No title", messageBody: String ?= "No body") {
        val NOTIFY_ID = 1
        val CHANNEL_ID = getString(R.string.default_notification_channel_id)
        val CHANNEL_NAME = "My Background Service"

        // Create an explicit intent for an Activity
        val resultIntent = Intent(this, FirebaseActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        // Set the notification content
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(messageTitle)
            .setContentText(messageBody)
            .setSmallIcon(R.drawable.ic_notify)
            .setContentIntent(resultPendingIntent)
            .setAutoCancel(true)

        // Create a channel and set the importance
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)

            // Register the channel with the system
            notificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager?.createNotificationChannel(channel)
        }
        with(NotificationManagerCompat.from(this)) {
            notify(NOTIFY_ID, builder.build())
        }
    }
}