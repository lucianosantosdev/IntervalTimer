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

    fun setAlarm (
        time: Long,
        intent: PendingIntent
    ) {
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            time,
            intent
        )
    }
}