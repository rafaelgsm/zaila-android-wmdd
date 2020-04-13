package com.zailaapp.zaila.extensions

import android.annotation.SuppressLint
import android.content.Context
import android.nfc.NfcManager
import android.widget.Toast


fun Context.toast(message: CharSequence) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Context.toast(resString: Int) {
    Toast.makeText(this, resources.getString(resString), Toast.LENGTH_SHORT).show()
}

fun Context.hasNfc(): Boolean {

    val manager = getSystemService(Context.NFC_SERVICE) as NfcManager
    val adapter = manager.defaultAdapter

    return (adapter != null && adapter.isEnabled)
}


/**
 * THIS WILL TRIGGER THE GPS RECEIVER!!!
 */
fun Context.isGpsEnabled(): Boolean {
    val locationManager =
        getSystemService(Context.LOCATION_SERVICE) as android.location.LocationManager
    return locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)
}