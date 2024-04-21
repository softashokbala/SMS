package com.ashokbala.android.sms

import android.content.Context
import android.content.SharedPreferences

object PreferenceHelper {


    private const val PREFERENCE_NAME = "MyPrefs"

    // Function to get SharedPreferences instance
    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
    }

    // Function to save a string value to SharedPreferences
    fun saveKey(context: Context, key: String, value: String) {
        val editor = getSharedPreferences(context).edit()
        editor.putString(key, value)
        editor.apply()
    }

    // Function to retrieve a string value from SharedPreferences
    fun getKey(context: Context, key: String, defaultValue: String): String {
        return getSharedPreferences(context).getString(key, defaultValue) ?: defaultValue
    }


    // Function to clear all SharedPreferences values
    fun clearSharedPreferences(context: Context) {
        val editor = getSharedPreferences(context).edit()
        editor.clear()
        editor.apply()
    }
}