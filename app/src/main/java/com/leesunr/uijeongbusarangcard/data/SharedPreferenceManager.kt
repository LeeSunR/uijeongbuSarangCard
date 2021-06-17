package com.leesunr.uijeongbusarangcard.data

import android.content.Context

class SharedPreferenceManager(context: Context){
    private val prefs = context.getSharedPreferences(PREF_MY_APP, Context.MODE_PRIVATE)

    var dataVersion: Int
        get() = prefs.getInt(PREF_VERSION,0)
        set(value) = prefs.edit().putInt(PREF_VERSION,value).apply()

    companion object{
        const val PREF_MY_APP = "PREF_MY_APP"
        const val PREF_VERSION = "PREF_VERSION"
    }
}