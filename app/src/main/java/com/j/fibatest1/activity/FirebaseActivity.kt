package com.j.fibatest1.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.ktx.messaging
import com.j.fibatest1.R
import com.j.fibatest1.data.DataFirebase
import kotlinx.android.synthetic.main.activity_firebase.*

class FirebaseActivity : AppCompatActivity() {
    private var token: String? = ""
    private var tempTopic = ""
//    private lateinit var dbReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firebase)

        setViewEvent()

        val title = intent?.getStringExtra("title")
        val body = intent?.getStringExtra("body")
        val data = intent?.getParcelableExtra<DataFirebase>("data")

        if (title != null && body != null)
            Toast.makeText(this, "$title, $body\n$data.name, $data.age, $data.favorite", Toast.LENGTH_SHORT).show()
        Log.d("FIREBASELOG", "$title, $body")
    }

    private fun setViewEvent() {
        btnToken.setOnClickListener { getToken() }
        btnSubscribe.setOnClickListener { subOrUnsubTopic(true) }
        btnUnsubscribe.setOnClickListener { subOrUnsubTopic(false) }
    }

    /***************************************** Event Click ****************************************/
    private fun getToken() {
        var msg: String? = ""
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FIREBASELOG", getString(R.string.token_error), task.exception)
                return@addOnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log and toast
            msg = getString(R.string.msg_token_fmt, token)
            Log.d("FIREBASELOG", msg ?: "")
            Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
        }
        token = msg
    }

    private fun subOrUnsubTopic(subscribed: Boolean) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_topic)
        dialog.show()

        val edtTopic = dialog.findViewById<EditText>(R.id.edtTopic)
        val btnOk = dialog.findViewById<Button>(R.id.btnOk)
        val btnCancel = dialog.findViewById<Button>(R.id.btnCancel)
        edtTopic?.setText(tempTopic)

        btnOk.setOnClickListener {
            tempTopic = edtTopic?.text.toString()
            if (tempTopic.isNotEmpty()) {
                if (subscribed) subscribeToTopic(tempTopic)
                else unSubscribeFromTopic(tempTopic)
                dialog.dismiss()
            }
        }
        btnCancel.setOnClickListener { dialog.cancel() }
    }

    /*************************************** Class Function ***************************************/
    @SuppressLint("SetTextI18n")
    private fun subscribeToTopic(topic: String) {
        Firebase.messaging.subscribeToTopic(topic)
                .addOnCompleteListener { task ->
                    var msg = "Subscribe to \"$topic\" successfully!"
                    if (!task.isSuccessful) {
                        msg = "Subscribe to \"$topic\" failed!"
                        Log.w("FIREBASELOG", msg, task.exception)
                    }
                    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                }
    }

    @SuppressLint("SetTextI18n")
    private fun unSubscribeFromTopic(topic: String) {
        Firebase.messaging.unsubscribeFromTopic(topic)
                .addOnCompleteListener { task ->
                    var msg = "Unsubscribe from \"$topic\" successfully!"
                    if (task.isSuccessful) {
                        tempTopic = ""
                    } else {
                        msg = "Unsubscribe from \"$topic\" failed!"
                        Log.w("FIREBASELOG", msg, task.exception)
                    }
                    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                }
    }

}