package com.j.fibatest1.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.j.fibatest1.R
import com.j.fibatest1.service.MyFirebaseMessagingService
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity(), View.OnTouchListener {

    companion object {
        const val REQUEST_SCAN_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA),
                REQUEST_SCAN_CODE
            )

        setViewEvent()
        startService(Intent(this, MyFirebaseMessagingService::class.java))

    }

    private fun setViewEvent() {
        tvBarChart.setOnTouchListener(this)
        tvQrCodeScan.setOnTouchListener(this)
        tvFirebase.setOnTouchListener(this)
        tvWeather.setOnTouchListener(this)

        tvBarChart.setOnClickListener {
            startActivity(Intent(this, BarChartActivity::class.java))
        }
        tvQrCodeScan.setOnClickListener {
            startActivityForResult(Intent(this, QrCodeActivity::class.java), REQUEST_SCAN_CODE)
        }
        tvFirebase.setOnClickListener {
            startActivity(Intent(this, FirebaseActivity::class.java))
        }
        tvWeather.setOnClickListener {
            startActivity(Intent(this, WeatherViewSearchActivity::class.java))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_SCAN_CODE && resultCode == Activity.RESULT_OK) {
            edtUsername.setText(data?.getStringExtra(QrCodeActivity.SCAN_CODE))
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        val textView = v as TextView
        val rawText = textView.text.toString()

        when (event!!.action) {
            MotionEvent.ACTION_DOWN -> {
                val content = SpannableString(rawText)
                textView.setTextColor(Color.GREEN)
                content.setSpan(UnderlineSpan(), 0, content.length, 0)
                textView.text = content
            }
            MotionEvent.ACTION_UP -> {
                textView.setTextColor(Color.WHITE)
                textView.text = rawText
            }
        }
        return false
    }

}