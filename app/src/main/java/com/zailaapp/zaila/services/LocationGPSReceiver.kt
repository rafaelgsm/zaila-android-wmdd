package com.zailaapp.zaila.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * If the GPS status is checked by the app, it will trigger this receiver again!
 */
class LocationGPSReceiver : BroadcastReceiver() {

    companion object {

        var listener: () -> Unit = {}
    }

    override fun onReceive(context: Context?, intent: Intent?) {

        if (intent?.action == "android.location.PROVIDERS_CHANGED") {
            listener()
        }

    }

}