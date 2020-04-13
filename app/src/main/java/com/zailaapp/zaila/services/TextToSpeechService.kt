package com.zailaapp.zaila.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import com.zailaapp.zaila.R
import com.zailaapp.zaila.ZailaApplication
import com.zailaapp.zaila.views.activities.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

/**
 *
 * "Zaila Player"
 *
 * Note that as per SDK 26+ we are forced to bind a notification to a service.
 * @see TextToSpeechService.goForeground()
 *
 *
 * To initialize the service right away, and display the notification, use:
 * @see TextToSpeechService.init()
 */
class TextToSpeechService : Service() {

    companion object {

        private const val TAG = "TextToSpeechService"
        private const val FOREGROUND_NOTIFICATION_ID = 3

        var listener: (utteranceId: String) -> Unit = {}
        var listenerFinish: () -> Unit = {}

        private var _listOfPhrases: ArrayList<String> = arrayListOf()
        private var _localeString: String = ""
        private var _pausedIndex: Int = -1

        ////////////////////////////////////////////////////////////////////////////////////////////////
        //region Public methods (start, stop)
        ////////////////////////////////////////////////////////////////////////////////////////////////

        private const val ACTION_INIT =
            "com.zailaapp.zaila.services.TextToSpeechService.ACTION_INIT"
        private const val ACTION_START_SPEAK =
            "com.zailaapp.zaila.services.TextToSpeechService.ACTION_START_SPEAK"
        private const val ACTION_STOP =
            "com.zailaapp.zaila.services.TextToSpeechService.ACTION_STOP"
        private const val ACTION_STOP_TTS =
            "com.zailaapp.zaila.services.TextToSpeechService.ACTION_STOP_TTS"

        /**
         * Used just to have the required service's notification to be displayed ahead of time.
         */
        fun init() {

            Log.d(TAG, "init")

            _listOfPhrases = arrayListOf()
            _localeString = ""
            _pausedIndex = -1

            startServiceAction(ACTION_INIT)
        }

        /**
         * Starts the service and also starts TTS speaking.
         */
        fun startSpeak(textArray: ArrayList<String>, locale: String, pausedIndex: Int = -1) {

            _listOfPhrases = textArray
            _localeString = locale
            _pausedIndex = pausedIndex

            Log.d(TAG, "start")

            startServiceAction(ACTION_START_SPEAK)
        }

        /**
         * Kills the service.
         */
        fun killService() {
            Log.d(TAG, "killService")

            startServiceAction(ACTION_STOP)
        }

        /**
         * Just stops the TTS service from playing. Doesn't kill the service.
         */
        fun stopTTS() {
            Log.d(TAG, "stopTTS")

            startServiceAction(ACTION_STOP_TTS)
        }

        private fun startServiceAction(ACTION: String) {

            val intent = Intent(
                ACTION, null, ZailaApplication.instance,
                TextToSpeechService::class.java
            )

            if (Build.VERSION.SDK_INT >= 26) {
                ZailaApplication.instance.startForegroundService(intent)
            } else {
                ZailaApplication.instance.startService(intent)
            }
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
                ACTION_START_SPEAK -> {
                    setupSpeech()
                }
                ACTION_STOP -> {
                    tts?.stop()
                    tts?.shutdown()
                    stopSelf()
                }
                ACTION_STOP_TTS -> {
                    tts?.stop()
                    tts?.shutdown()

                    return START_NOT_STICKY
                }

                ACTION_INIT -> {
                    return START_NOT_STICKY
                }

                else -> throw IllegalStateException("Undefined constant used: " + action!!)
            }

        } else {
            //If the intent is NULL, then it means that the service was killed by the android OS, and is now being restarted.
//            startSpeak(_listOfPhrases, _localeString)
            tts?.stop()
            tts?.shutdown()
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

    //region notification
    /**
     * Creates a notification and starts the service with the notification attached.
     */
    private fun goForeground() {

        val notificationIntent = Intent(this, MainActivity::class.java)

        notificationIntent.putExtra("isOpeningNotification", true)

        val pendingIntent = PendingIntent.getActivity(
            this, 0,
            notificationIntent, 0
        )

        val channelId = "zaila_tts_channel" // The id of the channel.

        val builder =

            if (Build.VERSION.SDK_INT >= 26) {
                Notification.Builder(this, channelId)
            } else {
                Notification.Builder(this)
            }
                .setContentTitle("Zaila Player")
                .setContentText("Zaila player is active")
                .setSmallIcon(R.drawable.ic_img_zaila)
                .setContentIntent(pendingIntent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= 26) {
            val name = "Zaila TTS"            // The user-visible name of the channel.
            val importance = NotificationManager.IMPORTANCE_DEFAULT

            val channel = NotificationChannel(channelId, name, importance)
            notificationManager.createNotificationChannel(channel)
        }

        startForeground(FOREGROUND_NOTIFICATION_ID, builder.build())
    }

    //endregion notification

    //endregion Service methods
    ////////////////////////////////////////////////////////////////////////////////

    //region text-to-speech
    private var tts: TextToSpeech? = null

    private fun setupSpeech() {

        tts?.let {
            it.stop()
            it.shutdown()
        }

        tts = TextToSpeech(ZailaApplication.instance,
            TextToSpeech.OnInitListener { status ->

                if (status != TextToSpeech.ERROR) {

                    //Running in background so it wont freeze the visuals:
                    CoroutineScope(Dispatchers.IO).launch {
                        addPhrasesToTTSAndStart()
                    }

                }

            })
    }

    //region addPhrasesToTTSAndStart
    private fun addPhrasesToTTSAndStart() {

        tts!!.language = getLocale(_localeString)

        ////////////////////////////////////////////////////////////////////////////////
        // Passing each phrase to the TTS one by one... So we can get separate callbacks
        ////////////////////////////////////////////////////////////////////////////////

        //If there is a pause index, then use the list of phrases from that index.
        val listUsed =
            if (_pausedIndex >= 0)
                _listOfPhrases.subList(_pausedIndex, _listOfPhrases.size)
            else _listOfPhrases

        _pausedIndex = -1

        for (phrase in listUsed) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                val paramBundle = Bundle()

                paramBundle.putString(
                    TextToSpeech.Engine.KEY_PARAM_STREAM,
                    AudioManager.STREAM_NOTIFICATION.toString()
                )

                tts!!.speak(phrase, TextToSpeech.QUEUE_ADD, paramBundle, phrase)

            } else {

                val parameterHash = HashMap<String, String>()

                parameterHash[TextToSpeech.Engine.KEY_PARAM_STREAM] =
                    AudioManager.STREAM_NOTIFICATION.toString()

                parameterHash[TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID] = phrase

                tts!!.speak(phrase, TextToSpeech.QUEUE_ADD, parameterHash)
            }

            //...

        }

        //...

        tts!!.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onDone(utteranceId: String?) {
                if (_listOfPhrases.indexOf(utteranceId) == _listOfPhrases.size - 1) {
                    listenerFinish()
                }

            }

            override fun onError(utteranceId: String?) {
                //todo
            }

            override fun onStart(utteranceId: String?) {
                listener("" + utteranceId)
            }

        })

        //...
    }

    //endregion addPhrasesToTTSAndStart

    private fun getLocale(s: String): Locale {
        return when (s) {
            "en-US" -> Locale("en", "US")
            "fr-CA" -> Locale("fr", "CA")
            "es-ES" -> Locale("es", "ES")
            "zh-CN" -> Locale("zh", "CN")
            else -> Locale.US
        }
    }
    //endregion text-to-speech
}