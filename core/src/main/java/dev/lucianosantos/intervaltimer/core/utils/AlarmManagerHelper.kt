package dev.lucianosantos.intervaltimer.core.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import dev.lucianosantos.intervaltimer.core.service.CountDownTimerService
import dev.lucianosantos.intervaltimer.core.service.NotificationHelper
import java.time.LocalDateTime
import java.time.ZoneId

class AlarmManagerHelper(
    private val context: Context
) {
    private val alarmManager: AlarmManager = context.getSystemService(AlarmManager::class.java)

    private fun activityLauncherIntent() : PendingIntent {
        val pauseIntent = Intent()
        pauseIntent.action = CountDownTimerService.ACTION_WAKE
        return PendingIntent.getBroadcast(
            context,
            1,
            pauseIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
    }

    fun setAlarm (
        time: LocalDateTime,
    ) {
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            time.atZone(ZoneId.systemDefault()).toEpochSecond() * 1000,
            activityLauncherIntent()
        )
    }

    fun cancelAlarm(pendingIntent: PendingIntent) {
        alarmManager.cancel(pendingIntent)
    }
}