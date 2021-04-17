package com.j.fibatest1.activity

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import com.j.fibatest1.R
import com.j.fibatest1.service.WeatherService
import kotlinx.android.synthetic.main.activity_firebase.*
import kotlinx.android.synthetic.main.activity_weather.*

class WeatherViewSearchActivity : AppCompatActivity() {
    private var weatherService: WeatherService ?= null
    private var binded = false

    private val weatherServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as WeatherService.WeatherBinder
            weatherService = binder.getService(this@WeatherViewSearchActivity)
            binded = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            binded = false
        }

    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)

        btnWeather.setOnClickListener { showWeather() }

        var enableView = true
        btnDisableView.setOnClickListener {
            enableView = if (enableView) {
                disableView(llAll)
                btnDisableView.text = "Enable"
                false
            } else {
                enableView(llAll)
                btnDisableView.text = "Disable"
                true
            }
        }

        llAll.setOnClickListener {
            Toast.makeText(this, "LinearLayout Email", Toast.LENGTH_SHORT).show()
        }

        ivEmail.setOnClickListener {
            Toast.makeText(this, "ImageView Email", Toast.LENGTH_SHORT).show()
        }

        ivPassword.setOnClickListener {
            Toast.makeText(this, "ImageView Password", Toast.LENGTH_SHORT).show()
        }

        ivNotify.setOnClickListener {
            Toast.makeText(this, "ImageView Notify", Toast.LENGTH_SHORT).show()
        }

        ivDelete.setOnClickListener {
            Toast.makeText(this, "ImageView Delete", Toast.LENGTH_SHORT).show()
        }

    }

    override fun onStart() {
        super.onStart()
        val weatherIntent = Intent(this, WeatherService::class.java)
        bindService(weatherIntent, weatherServiceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onStop() {
        super.onStop()
        if (binded) {
            unbindService(weatherServiceConnection)
            binded = false
        }
    }

    private fun showWeather() {
        val location: String = edtLocation?.text.toString()
        val weather: String? = weatherService?.getWeatherToday(location)
        this.tvWeather?.text = weather
    }

    private fun disableView(viewGroup: ViewGroup) {
        for (view in viewGroup.children) {
            view.isClickable = false
            if (view is ViewGroup) {
                disableView(view)
            }
        }
    }

    private fun enableView(viewGroup: ViewGroup) {
        for (view in viewGroup.children) {
            view.isClickable = true
            if (view is ViewGroup) {
                enableView(view)
            }
        }
    }
}
