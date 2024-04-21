package com.ashokbala.android.sms

import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.telephony.SmsManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import android.Manifest
import android.util.Base64
import androidx.activity.contextaware.withContextAvailable
import kotlinx.coroutines.delay


class SendSMSActivity : AppCompatActivity() {

    lateinit var recyclerView:RecyclerView
    lateinit var mPhoneNumber:EditText
    lateinit var mTextMessage: EditText
    lateinit var mSendSMS: Button
    val smsList = mutableListOf<SMSModel>()
    private val SEND_SMS_PERMISSION_REQUEST_CODE = 1001
    private val READ_SMS_PERMISSION_REQUEST_CODE = 1002

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sender_smsactivity)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        recyclerView=findViewById(R.id.mList)
        if (checkReadSmsPermission()) {
            loadMsg()
        } else {
            // Permission not granted, request again
            requestReadSmsPermission()
        }

        mPhoneNumber=findViewById(R.id.editTextPhoneNumber)
        mTextMessage=findViewById(R.id.editTextMessage)
        mSendSMS =findViewById(R.id.buttonSend)

        mSendSMS.setOnClickListener {

            if(mPhoneNumber.text.isNullOrBlank() || mPhoneNumber.text.length!=10)
            {
              Toast.makeText(this,"Please Check Mobile Number",Toast.LENGTH_SHORT).show();
            }else if(mTextMessage.text.isNullOrBlank())
            {
                Toast.makeText(this,"Invalid Message",Toast.LENGTH_SHORT).show();
            }else{
                val phoneNumber= mPhoneNumber.text.toString()
                val message= mTextMessage.text.toString()
                if (checkSendSmsPermission()) {
                    sendSms(phoneNumber, message)
                } else {
                    // Permission not granted, request again
                    requestSendSmsPermission()
                }

            }

        }



    }


    private fun loadMsg()
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

      val secureMsg= smsList.filter { it ->
            val key=PreferenceHelper.getKey(this, "secureKey","")
            // Decrypt the message
            val decryptedMessage = AESCrypt.decrypt(it.msg, key)
            decryptedMessage.isNotEmpty()
        }
        val adapter = SMSAdapter(secureMsg) // Create your custom adapter
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



    // Check if SEND_SMS permission is granted
    private fun checkSendSmsPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED
    }

    // Request SEND_SMS permission
    private fun requestSendSmsPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.SEND_SMS), SEND_SMS_PERMISSION_REQUEST_CODE)
    }

    // Handle permission request result
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == SEND_SMS_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, send SMS
                val phoneNumber = mPhoneNumber.text.toString()
                val message = mTextMessage.text.toString()
                sendSms(phoneNumber, message)
            } else {
                // Permission denied
                Toast.makeText(this, "SEND_SMS permission denied", Toast.LENGTH_SHORT).show()
            }
        }else if (requestCode == READ_SMS_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadMsg()
            } else {
                // Permission denied
                Toast.makeText(this, "READ_SMS permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Send SMS
    fun sendSms(phoneNumber: String, message: String) {
        try {
            val key=PreferenceHelper.getKey(this, "secureKey","")

            val encryptedMessage = AESCrypt.encrypt(message, key)
            println("Encrypted message: $encryptedMessage")

            val smsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(phoneNumber, null, encryptedMessage, null, null)
            Toast.makeText(this, "SMS sent successfully", Toast.LENGTH_SHORT).show()

            cleardata()
            loadMsg()
        } catch (e: Exception) {
            Toast.makeText(this, "Failed to send SMS", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }


    private fun cleardata()
    {
        mPhoneNumber.text.clear()
        mTextMessage.text.clear()

        onBackPressedDispatcher.onBackPressed()
    }


}