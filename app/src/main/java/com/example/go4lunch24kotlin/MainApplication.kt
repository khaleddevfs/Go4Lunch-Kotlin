package com.example.go4lunch24kotlin

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager

class MainApplication : Application() {


    companion object {
        private var sApplication: Application? = null
        fun getApplication(): Application? = sApplication
        private const val CHANNEL_ID = "notif"
    }

    override fun onCreate() {
        super.onCreate()
        sApplication = this
        createNotificationChannel()
    }

    // FOR ANDROID 8.0 AND HIGHER, REGISTER APP'S NOTIFICATION CHANNEL
    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        val name = getString(R.string.channel)
        val descriptionText = getString(R.string.channel_description)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        val notificationManager: NotificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }
}