package com.capcorp.utils

import android.app.Application
import android.content.IntentFilter
import android.location.LocationManager
import com.capcorp.R
import com.facebook.FacebookSdk
import com.google.android.libraries.places.api.Places
import java.util.*


class MyApplication : Application() {

    private var activityVisible = false
    override fun onCreate() {
        super.onCreate()

        instance = this

        applicationContext.registerReceiver(GpsLocationReceiver(), IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION))

        FacebookSdk.sdkInitialize(applicationContext)
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, getString(R.string.google_api_key))
        }
        SharedPrefs.with(applicationContext)

    }

    fun isActivityVisible(): Boolean {
        return activityVisible
    }

    fun activityResumed() {
        activityVisible = true
    }

    fun activityPaused() {
        activityVisible = false
    }


    companion object {
        private var instance: MyApplication? = null

        fun getInstnace(): MyApplication? {
            return instance
        }
    }
}