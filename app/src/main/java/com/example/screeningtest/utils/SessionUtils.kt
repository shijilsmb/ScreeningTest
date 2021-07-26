package com.example.screeningtest.utils

import android.content.Context
import android.content.SharedPreferences

class SessionUtils {

    companion object {

        const val PREF_NAME = "test_pref"
        const val PREF_SESSION = "sessionLogin"

        lateinit var preferences: SharedPreferences

        fun init(context: Context) {
            preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

            val prefsEditor = preferences.edit()
            prefsEditor.putString("email", "admin@admin.com")
            prefsEditor.putString("password", "password")
            prefsEditor.apply()
        }

        fun checkLogin(context: Context?,email:String,password:String ):Boolean {

            //if (session == null) return
            if (preferences==null){
                preferences = context!!.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            }

            val preEmail = preferences.getString("email","")
            val prePassword = preferences.getString("password","")

            return preEmail.equals(email) && prePassword.equals(password)
        }

        val isSessionActive: Boolean
            get() = preferences.getBoolean(PREF_SESSION, false)


        fun saveSession( login: Boolean) {
            preferences.edit()
                .putBoolean(PREF_SESSION, login).apply()
        }
    }
}