package dev.lucianosantos.intervaltimer.di

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.os.SystemClock
import androidx.core.app.NotificationCompat
import androidx.core.content.LocusIdCompat
import androidx.wear.ongoing.OngoingActivity
import androidx.wear.ongoing.Status
import dev.lucianosantos.intervaltimer.R
import dev.lucianosantos.intervaltimer.core.data.TimerState
import dev.lucianosantos.intervaltimer.core.service.NotificationHelper
import dev.lucianosantos.intervaltimer.core.service.OngoingActivityWrapper

class OngoingActivityWrapperImpl : OngoingActivityWrapper {
    override fun allowForegroundService(): Boolean {
        return true
    }

    override fun wakeScreenOnTransition(): Boolean = true

    override fun setOngoingActivity(
        timerState: TimerState,
        applicationContext: Context,
        onTouchIntent: PendingIntent,
        message: String,
        remainingSeconds: Int,
        isPaused: Boolean,
        notificationBuilder: NotificationCompat.Builder
    ) {
        val status = buildStatus(timerState, message, remainingSeconds, isPaused)
        val icon = iconFor(timerState) ?: R.drawable.ic_ongoing_idle

        OngoingActivity.Builder(
            applicationContext,
            NotificationHelper.NOTIFICATION_ID,
            notificationBuilder
        )
            .setTouchIntent(onTouchIntent)
            .setStatus(status)
            .setStaticIcon(icon)
            .setCategory(Notification.CATEGORY_WORKOUT)
            .setLocusId(LocusIdCompat("interval-timer"))
            .build()
            .apply(applicationContext)
    }

    private fun buildStatus(
        timerState: TimerState,
        message: String,
        remainingSeconds: Int,
        isPaused: Boolean
    ): Status {
        val builder = Status.Builder()
        val tickable = timerState == TimerState.PREPARE ||
                timerState == TimerState.TRAIN ||
                timerState == TimerState.REST
        if (!tickable) {
            return builder.addTemplate(message).build()
        }

        val now = SystemClock.elapsedRealtime()
        val endTime = now + remainingSeconds * 1000L
        val timerPart = if (isPaused) {
            Status.TimerPart(endTime, now)
        } else {
            Status.TimerPart(endTime)
        }
        val label = labelFor(timerState)
        return builder
            .addTemplate("$label #time#")
            .addPart("time", timerPart)
            .build()
    }

    private fun labelFor(timerState: TimerState): String = when (timerState) {
        TimerState.PREPARE -> "Prepare"
        TimerState.TRAIN -> "Train"
        TimerState.REST -> "Rest"
        else -> ""
    }

    private fun iconFor(timerState: TimerState): Int? = when (timerState) {
        TimerState.PREPARE -> R.drawable.ic_ongoing_prepare
        TimerState.TRAIN -> R.drawable.ic_ongoing_train
        TimerState.REST -> R.drawable.ic_ongoing_rest
        TimerState.FINISHED -> R.drawable.ic_ongoing_finished
        else -> null
    }
}
