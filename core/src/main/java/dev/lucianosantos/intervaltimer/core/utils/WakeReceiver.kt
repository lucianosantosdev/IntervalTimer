package dev.lucianosantos.intervaltimer.core.utils

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import android.util.Log
import dev.lucianosantos.intervaltimer.core.service.NotificationHelper

class WakeReceiver : BroadcastReceiver() {

    private var wakeLock: PowerManager.WakeLock? = null

    private var context: Context? = null

    override fun onReceive(context: Context?, intent: Intent?) {
        this.context = context
        wakeUp(context)
    }

    private fun wakeUp(context: Context?) {
        Log.d("WakeReceiver", "Wake up!")
        val powerManager = context!!.getSystemService(Context.POWER_SERVICE) as PowerManager?
        wakeLock = powerManager!!.newWakeLock(PowerManager.ON_AFTER_RELEASE, "IntervalTimer:WakeReceiver")
        wakeLock!!.acquire(5 * 1000L /* 5 seconds */)
    }
}