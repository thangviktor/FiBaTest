    package com.j.fibatest1.service

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.github.mikephil.charting.charts.Chart.LOG_TAG
import com.j.fibatest1.R
import com.j.fibatest1.activity.WeatherViewSearchActivity
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


class WeatherService() : Service() {
    private var context: Context?= null
    private val binder = WeatherBinder()

    private val weathers = arrayOf("Rainy", "Hot", "Cool", "Warm", "Snowy")
    private var curIndex: Int ?= 0
    private val weatherData = HashMap<String, String?>()
    private var weather: String ?= ""


    constructor(context: Context?) : this() {
        this.context = context
    }

    inner class WeatherBinder : Binder() {
        fun getService(context: Context): WeatherService {
            return WeatherService(context)
        }
    }

    override fun onBind(intent: Intent): IBinder {
        Log.d("LIFECYCLE", "onBind")

        return this.binder
    }

    override fun onRebind(intent: Intent?) {
        Log.d("LIFECYCLE", "onBind")
        super.onRebind(intent)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.i(LOG_TAG, "onUnbind")
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(LOG_TAG, "onDestroy")
    }

    @SuppressLint("SimpleDateFormat")
    fun getWeatherToday(location: String): String? {
        val df = SimpleDateFormat("dd-MM-yyyy")
        val dayString: String = df.format(Date())
        val keyLocAndDay = "$location$$dayString"
        weather = weatherData[keyLocAndDay]

        if (weather != null) {
            return weather
        }

        // Random value from 0 to 4
        curIndex = Random().nextInt(5)
        weather = weathers[curIndex?:0]
        weatherData[keyLocAndDay] = weather
        Log.d("createNotificationLog", "weather: $weather")
        createNotification()

        return weather
    }

    fun createNotification() {
        val NOTIFY_ID = 1
        val CHANNEL_ID = "My Channel"
        val CHANNEL_NAME = "My Background Service"

        // Create an explicit intent for an Activity
        val resultIntent = Intent(context, WeatherViewSearchActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(context!!).run {
            addNextIntentWithParentStack(resultIntent)
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }
//        val resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        // Set the notification content
        val builder = NotificationCompat.Builder(context!!, CHANNEL_ID)
            .setContentTitle("Weather")
            .setContentText(weather)
            .setSmallIcon(R.drawable.ic_notify)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(resultPendingIntent)
            .setAutoCancel(true)

        // Create a channel and set the importance
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = "Background"
            }
            // Register the channel with the system
            val notificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
        with(NotificationManagerCompat.from(context!!)) {
            builder.setProgress(5, curIndex?:0.plus(1), false)
            notify(NOTIFY_ID, builder.build())
        }
    }
}