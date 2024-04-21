package com.ashokbala.android.sms

class SMSList : ArrayList<SMSModel>()

data class SMSModel(
    val name: String,
    val msg: String,
    val timestamp: Long? =  System.currentTimeMillis() / 1000 // Current timestamp in seconds
)