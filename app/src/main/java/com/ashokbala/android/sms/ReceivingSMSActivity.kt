package com.ashokbala.android.sms

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.telephony.SmsManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.ashokbala.android.sms.Utils.toFormattedDate

class ReceivingSMSActivity : AppCompatActivity() {
    val smsList = mutableListOf<SMSModel>()
    private val READ_SMS_PERMISSION_REQUEST_CODE = 1001
    lateinit var recyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_receiving_smsactivity)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        recyclerView = findViewById(R.id.recyclerView)

        if (checkReadSmsPermission()) {
            loadMsg()
        } else {
            // Permission not granted, request again
            requestReadSmsPermission()
        }

    }

    fun loadMsg()
    {
        smsList.clear()
        val cursor = contentResolver.query(
            Uri.parse("content://sms/inbox"),
            null,
            null,
            null,
            null
        )
        if (cursor != null && cursor.moveToFirst()) {
            do {
                val sender = cursor.getString(cursor.getColumnIndexOrThrow("address"))
                val content = cursor.getString(cursor.getColumnIndexOrThrow("body"))
                val timestamp = cursor.getLong(cursor.getColumnIndexOrThrow("date"))
                smsList.add(SMSModel(sender, content, timestamp))
            } while (cursor.moveToNext())
            cursor.close()
        }


        val adapter = SMSAdapter(smsList) // Create your custom adapter
        recyclerView.adapter = adapter
    }


    // Check if READ_SMS permission is granted
    private fun checkReadSmsPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_SMS
        ) == PackageManager.PERMISSION_GRANTED
    }

    // Request READ_SMS permission
    private fun requestReadSmsPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_SMS),
            READ_SMS_PERMISSION_REQUEST_CODE
        )
    }

    // Handle permission request result
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == READ_SMS_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadMsg()
            } else {
                // Permission denied
                Toast.makeText(this, "READ_SMS permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }



}