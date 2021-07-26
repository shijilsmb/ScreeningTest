package com.example.screeningtest

/**
 * Created by Shijil Kadambath on 03/08/2018
 * for bigtime
 * Email : shijilkadambath@gmail.com
 */

import android.app.Activity
import android.app.Application
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import com.example.screeningtest.utils.SessionUtils

class ScreenApplication : Application() {


    override fun onCreate() {
        super.onCreate()
        SessionUtils.init(this)
    }

}
