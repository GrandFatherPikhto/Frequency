package com.grandfatherpikhto.frequency

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import kotlin.math.roundToInt

class PlayService: Service() {
    companion object {
        const val TAG:String = "PlayService"
        private val modulationPlayer by lazy {
            ToneModulationPlayer()
        }

        var frequency:Int = 20
            get() = frequency
            set(value: Int) {
                field = value
                modulationPlayer.frequency = value
            }

        fun play() {
            Log.e(TAG, "play()")
            modulationPlayer.play()
        }

        fun stop() {
            Log.e(TAG, "stop()")
            modulationPlayer.stop()
        }

        fun pause() {
            Log.e(TAG, "pause()")
            modulationPlayer.stop()
        }

        fun resume() {
            Log.e(TAG, "resume()")
            modulationPlayer.play()
        }

        fun changeFrequency() {
            Log.e(TAG, "changeFrequency()")
        }
    }


    // Binder given to clients
    private val binder = LocalBinder()
    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    inner class LocalBinder : Binder() {
        // Return this instance of LocalService so clients can call public methods
        fun getService(): PlayService = this@PlayService
    }

    override fun onBind(intent: Intent): IBinder {
        Log.e(TAG, "onBind()")
        stopForeground(true)
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        Log.e(TAG, "onCreate()")
        val CHANNEL_ID = "my_channel_01"
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Channel human readable title",
            NotificationManager.IMPORTANCE_DEFAULT
        )

        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(
            channel
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("")
            .setContentText("").build()

        startForeground(1, notification)
    }

    override fun onDestroy() {
        stopForeground(false)
        stopSelf()
        super.onDestroy()
    }
}