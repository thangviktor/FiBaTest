package com.j.fibatest1.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Camera
import android.os.Bundle
import android.view.SurfaceHolder
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.MultiDetector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.google.android.gms.vision.text.TextBlock
import com.google.android.gms.vision.text.TextRecognizer
import com.j.fibatest1.R
import com.j.fibatest1.utils.PermissionUtils
import kotlinx.android.synthetic.main.activity_qrcode.*

class QrCodeActivity: AppCompatActivity() {

    companion object {
        const val SCAN_CODE = "SCAN_CODE"
        const val REQUEST_PERMISSION = 2
    }

    private var cameraSource: CameraSource? = null
    private var qrCodeEtem = ""
    private var isScanText = false // kiểm tra có được phép scan text không
    private var timeStamp = 0L // khi scan text timeout = 10s
    private var codeScan = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qrcode)

        if (!PermissionUtils.hasPermission(this, Manifest.permission.CAMERA)
            || !PermissionUtils.hasPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            PermissionUtils.requestPermission(this, arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQUEST_PERMISSION
            )
        }

        val barcodeDetector = BarcodeDetector.Builder(this)
            .setBarcodeFormats(Barcode.ALL_FORMATS)
            .build()
        val textRecognizer = TextRecognizer.Builder(this).build()

        barcodeDetector.setProcessor(object : Detector.Processor<Barcode> {
            override fun release() {
            }

            override fun receiveDetections(p0: Detector.Detections<Barcode>) {
                val barcodes = p0.detectedItems
                if (barcodes.size() > 0) {
                    if (!isScanText) {
                        val barcode = barcodes.valueAt(0).displayValue
                        if (barcode.contains("dev.etem.vn"))
                            finish()

                        if (barcode.contains("tn0.etem.vn")) {
                            timeStamp = System.currentTimeMillis()
                            qrCodeEtem = barcode
                            isScanText = true
                        }
                    }
                }
            }
        })

        textRecognizer.setProcessor(object : Detector.Processor<TextBlock> {
            override fun release() {
            }

            override fun receiveDetections(p0: Detector.Detections<TextBlock>) {
                val textCode = p0.detectedItems
                if (textCode.size() > 0) {
                    if (isScanText) {
                        if (System.currentTimeMillis() - timeStamp > 10000) {
                            isScanText = false
                            return
                        }

                        val textBlock = textCode.valueAt(0).value
                                .replace(".", "")
                                .replace(" ", "")
                                .trim { it <= ' ' }

                        if (textBlock.contains("SP:")) {
                            val temCode = textBlock.split(":")[1].trim()
                            if (temCode.length == 6) {
                                codeScan = "$qrCodeEtem/tracuu?ProdID=$temCode"
                                finish()
                            }
                        }
                    }
                }
            }
        })

        val multiDetector = MultiDetector.Builder() //.add(faceDetector)
            .add(barcodeDetector)
            .add(textRecognizer)
            .build()

        cameraSource = CameraSource.Builder(this, multiDetector)
            .setRequestedPreviewSize(1920, 1080)
            .setFacing(CameraSource.CAMERA_FACING_BACK)
            .setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)
            .build()

        preview.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                cameraSource?.stop()
            }

            override fun surfaceCreated(holder: SurfaceHolder) {
                try {
                    if (ActivityCompat.checkSelfPermission(this@QrCodeActivity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource?.start(preview.holder)
                    } else {
                        ActivityCompat.requestPermissions(this@QrCodeActivity, arrayOf(Manifest.permission.CAMERA), 1)
                        cameraSource?.start(preview.holder)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        })
    }

    override fun finish() {
        val intent = Intent()
        intent.putExtra(SCAN_CODE, codeScan)
        setResult(Activity.RESULT_OK, intent)
        super.finish()
    }

//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == REQUEST_PERMISSION)
//            setContentView(R.layout.activity_qrcode)
//    }
}