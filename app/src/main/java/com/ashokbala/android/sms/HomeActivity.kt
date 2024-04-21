package com.ashokbala.android.sms


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val context: Context = applicationContext
        if(PreferenceHelper.getKey(context, "secureKey","").isEmpty()) {
            val key = Base64.encodeToString(AESCrypt.generateKey().encoded, Base64.DEFAULT)
            PreferenceHelper.saveKey(context, "secureKey", key)
            println("newKey:${PreferenceHelper.getKey(context, "secureKey","")}")
        }else
        {
            println("keyalreadyhave:${PreferenceHelper.getKey(context, "secureKey","")}")
        }

        val sendSMS = findViewById<Button>(R.id.SendSMS)
        val recevingSMS = findViewById<Button>(R.id.RecevingSMS)

        sendSMS.setOnClickListener { // Create an intent to navigate to another activity (replace SecondActivity.class with your target activity)
            val intent = Intent(
                applicationContext,
                SendSMSActivity::class.java
            )
            startActivity(intent)
        }

        recevingSMS.setOnClickListener { // Create an intent to navigate to another activity (replace SecondActivity.class with your target activity)
            val intent = Intent(
                applicationContext,
                ReceivingSMSActivity::class.java
            )
            startActivity(intent)
        }




    }
}