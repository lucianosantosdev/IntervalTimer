package dev.lucianosantos.intervaltimer.core.service

import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationCompat
import dev.lucianosantos.intervaltimer.core.data.TimerState

interface OngoingActivityWrapper {
    fun allowForegroundService(): Boolean

    fun setOngoingActivity(
        timerState: TimerState,
        applicationContext: Context,
        onTouchIntent: PendingIntent,
        message: String,
        notificationBuilder: NotificationCompat.Builder
    )
}