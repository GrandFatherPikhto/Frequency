package com.grandfatherpikhto.frequency

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class BcReceiver: BroadcastReceiver()  {
    companion object {
        const val TAG: String = "BcReceiver"
    }
    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.let { it ->
            Log.e(TAG, "${it.action}")
            when(it.action) {
                PlayService.ACTION_STOP -> {
                    Log.e(TAG, "PlayService stop")
                    context?.let { ctx ->
                        MainActivity.getInstance()?.finish()
                        Intent(ctx, PlayService::class.java).let { intent ->
                            ctx.stopService(intent)
                        }
                    }
                }
                else -> {

                }
            }
        }
    }
}