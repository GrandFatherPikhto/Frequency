package com.grandfatherpikhto.frequency

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import kotlin.math.roundToInt
import kotlin.properties.Delegates

class PlayService: Service() {
    companion object {
        const val TAG:String = "PlayService"
        const val ONGOING_NOTIFICATION_ID = 1
        const val EXTRA_NOTIFICATION_ID = "stopService"
        const val ACTION_STOP = "com.rqd.Frequency.ACTION_STOP"

        private val modulationPlayer by lazy {
            ToneModulationPlayer()
        }

        var frequency by Delegates.observable(20) { property, oldValue, newValue ->
            Log.e(TAG, "Frequency: old = $oldValue, new = $newValue")
            if(oldValue != newValue) {
                modulationPlayer.frequency = newValue
            }
        }

        var enable by Delegates.observable(false) { property, oldValue, newValue ->
            Log.e(TAG, "Enable: old = $oldValue, new = $newValue")
            if(oldValue != newValue) {
                modulationPlayer.enable = newValue
            }
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

        val stopIntent = Intent(this, BcReceiver::class.java).apply {
            action = ACTION_STOP
            putExtra(EXTRA_NOTIFICATION_ID, 0)
        }
        val stopPendingIntent: PendingIntent =
            PendingIntent.getBroadcast(this, 0, stopIntent, 0)

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Генератор частоты")
            .setContentText("Генератор низких частот")
            .setSmallIcon(R.drawable.basin)
            .addAction(R.drawable.ic_baseline_stop_circle_24, getString(R.string.stop_button),
                stopPendingIntent)
            .build()

        startForeground(ONGOING_NOTIFICATION_ID, notification)
    }

    override fun onDestroy() {
        modulationPlayer.enable = false
        stopForeground(false)
        stopSelf()
        super.onDestroy()
        Log.e(TAG, "onDestroy()")
    }
}