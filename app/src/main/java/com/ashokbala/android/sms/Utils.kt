package com.ashokbala.android.sms


import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object Utils {

    fun Long.toFormattedDate(): String {
        return try {
            val simpleDateFormat = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.ENGLISH)
            simpleDateFormat.timeZone = TimeZone.getTimeZone("Asia/Kolkata") // Set Indian time zone
            simpleDateFormat.format(Date(this ))
        } catch (e: Exception) {
            ""
        }
    }





}