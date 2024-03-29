package dev.lucianosantos.intervaltimer

import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationCompat
import dev.lucianosantos.intervaltimer.core.data.TimerState
import dev.lucianosantos.intervaltimer.core.service.CountDownTimerService
import dev.lucianosantos.intervaltimer.core.service.OngoingActivityWrapper

class CountDownTimerServiceMobile : CountDownTimerService(CountDownTimerServiceMobile::class.java) {
    override val ongoingActivityWrapper = object : OngoingActivityWrapper {
        override fun allowForegroundService(): Boolean = true

        override fun setOngoingActivity(
            timerState: TimerState,
            applicationContext: Context,
            onTouchIntent: PendingIntent,
            message: String,
            notificationBuilder: NotificationCompat.Builder
        ) {
            /* Do nothing */
        }
    }

    override val mainActivity: Class<*> = MainActivity::class.java
}