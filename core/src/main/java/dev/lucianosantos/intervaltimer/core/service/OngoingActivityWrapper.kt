package dev.lucianosantos.intervaltimer.core.service

import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationCompat

interface OngoingActivityWrapper {
    fun setOngoingActivity(
        applicationContext: Context,
        onTouchIntent: PendingIntent,
        message: String,
        notificationBuilder: NotificationCompat.Builder
    )
}