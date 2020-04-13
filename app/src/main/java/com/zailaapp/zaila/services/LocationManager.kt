package com.zailaapp.zaila.services

import android.Manifest
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.*
import com.zailaapp.zaila.R
import com.zailaapp.zaila.ZailaApplication
import com.zailaapp.zaila.views.activities.MainActivity

class LocationManager : Service() {

    companion object {

        /**
         * Triggers location listener when a new location has been assigned.
         */
        var currentLocation: Location? = null
            set(value) {
                field = value
                if (value != null) {
                    listener(value)
                }
            }

        var listener: (currentLocation: Location) -> Unit = {}

        private const val TAG = "LocationManager"
        private const val FOREGROUND_NOTIFICATION_ID = 4

        ////////////////////////////////////////////////////////////////////////////////////////////////
        //region Public methods (start, stop)
        ////////////////////////////////////////////////////////////////////////////////////////////////

        private const val ACTION_START =
            "com.zailaapp.zaila.services.LocationManager.ACTION_START"
        private const val ACTION_STOP =
            "com.zailaapp.zaila.services.LocationManager.ACTION_STOP"

        fun start(listenerLocation: (location: Location) -> Unit) {

            listener = listenerLocation

            Log.d(TAG, "start")

            val intent = Intent(
                ACTION_START, null, ZailaApplication.instance,
                LocationManager::class.java
            )

            if (Build.VERSION.SDK_INT >= 26) {
                ZailaApplication.instance.startForegroundService(intent)
            } else {
                ZailaApplication.instance.startService(intent)
            }
        }

        fun stop() {
            Log.d(TAG, "stop")

            listener = {}

            val intent = Intent(
                ACTION_STOP, null, ZailaApplication.instance,
                LocationManager::class.java
            )

            if (Build.VERSION.SDK_INT >= 26) {
                ZailaApplication.instance.startForegroundService(intent)
            } else {
                ZailaApplication.instance.startService(intent)
            }
        }

        fun reset() {
            // TODO: Verify if we can notify after reset.
        }

        //endregion Public methods (start, stop)
        ////////////////////////////////////////////////////////////////////////////////////////////////
    }

    ////////////////////////////////////////////////////////////////////////////////
    //region Service methods
    ////////////////////////////////////////////////////////////////////////////////

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand: $intent")

        if (intent != null) {

            when (val action = intent.action) {
                ACTION_START -> {

                    //todo init
                    if (_googleApiClient == null) {
                        _googleApiClient = GoogleApiClient.Builder(ZailaApplication.instance)
                            .addConnectionCallbacks(_connectionCallbacks!!)
                            .addOnConnectionFailedListener(_connectionFailedListener)
                            .addApi(LocationServices.API)
                            .build()
                    }

                    _locationRequest = LocationRequest.create()
                        .setInterval(60000)
                        .setFastestInterval(60000)
                        .setSmallestDisplacement(10.0f)
                        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

                    connectLocation()

                }
                ACTION_STOP -> {

                    //todo finish
                    disconnectLocation()
                    stopSelf()

                }
                else -> throw IllegalStateException("Undefined constant used: " + action!!)
            }

        } else {
            //If the intent is NULL, then it means that the service was killed by the android OS, and is now being restarted.
            //In this case, well just try to start it normally
            start(listener)
        }

        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()

        if (Build.VERSION.SDK_INT >= 26) {
            goForeground()
        }
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        super.onTaskRemoved(rootIntent)
        Log.d(TAG, "onTaskRemoved")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun goForeground() {
        val notificationIntent = Intent(this, MainActivity::class.java)
        notificationIntent.putExtra("isOpeningNotification", true)
        val pendingIntent = PendingIntent.getActivity(
            this, 0,
            notificationIntent, 0
        )

        val builder = Notification.Builder(this)
            .setContentTitle("Location is Active")
            .setContentText("Location service is Active")
            .setSmallIcon(R.drawable.ic_img_zaila)
            .setContentIntent(pendingIntent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= 26) {
            val CHANNEL_ID = "location_zaila_channel_01" // The id of the channel.
            val name = "Zaila app"            // The user-visible name of the channel.
            val importance = NotificationManager.IMPORTANCE_HIGH

            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            notificationManager.createNotificationChannel(channel)
            builder.setChannelId(CHANNEL_ID)
        }

        startForeground(FOREGROUND_NOTIFICATION_ID, builder.build())
    }

    //endregion Service methods
    ////////////////////////////////////////////////////////////////////////////////

    //region location

    private fun connectLocation() {

        if (_googleApiClient == null) return

        //Disconnect and connect again to force location retrieval:
        if (_googleApiClient!!.isConnected) {
            _googleApiClient!!.disconnect()
        }

        //If its connected already, just recheck permissions. If have them, then try reload
        if (checkPermissions()) {
            Log.d(TAG, "connectLocation... Permission denied")
        } else {
            _googleApiClient!!.connect()
        }
    }

    private fun disconnectLocation() {

        if (_googleApiClient == null) return

        if (_googleApiClient!!.isConnected) {

            getFusedLocationClient().removeLocationUpdates(_locationCallback)

            _googleApiClient!!.disconnect()
        }

        _requestingLocationUpdates = false
    }

    private val _connectionCallbacks: GoogleApiClient.ConnectionCallbacks? = object :
        GoogleApiClient.ConnectionCallbacks {
        override fun onConnected(bundle: Bundle?) {
            Log.d(TAG, "onConnected")
            onGooglePlayServicesConnected()
        }

        override fun onConnectionSuspended(i: Int) {
            Log.d(TAG, "onConnectionSuspended")
            _requestingLocationUpdates = false
        }
    }
    private val _connectionFailedListener =
        GoogleApiClient.OnConnectionFailedListener {
            Log.d(TAG, "OnConnectionFailedListener")
            _requestingLocationUpdates = false
        }

    private var _googleApiClient: GoogleApiClient? = null

    private var _locationRequest: LocationRequest? = null

    private fun onGooglePlayServicesConnected() {
        Log.d(TAG, "onGooglePlayServicesConnected...")

        if (checkPermissions()) {
            Log.d(TAG, "onGooglePlayServicesConnected... Permission denied")
            return
        }

        Log.d(TAG, "onGooglePlayServicesConnected... Permission granted!")

        requestLocationUpdates()

        _requestingLocationUpdates = true

    }

    private fun requestLocationUpdates() {
        getFusedLocationClient().requestLocationUpdates(
            _locationRequest,
            _locationCallback,
            Looper.getMainLooper()
        )

        //todo - SOMETIMES DOESN'T TRIGGER THE NEW LOCATION WHEN GPS STARTS DISABLED.
        //(Should try to reconnect?)

//        tryLoadLastLocation()
    }

    private var _requestingLocationUpdates = false

    private val _locationCallback = object : LocationCallback() {

        override fun onLocationResult(p0: LocationResult?) {
            super.onLocationResult(p0)

            Log.d(TAG, "onLocationResult!!! ${p0?.lastLocation}")

            currentLocation = p0?.lastLocation
        }

    }

    private fun tryLoadLastLocation() {

        getFusedLocationClient().lastLocation
            .addOnSuccessListener { location: Location? ->

                currentLocation = location
            }
    }

    private fun getFusedLocationClient(): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(ZailaApplication.instance)
    }

    //endregion location

    private fun checkPermissions(): Boolean {
        return (
                ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED)
    }
}