package dev.lucianosantos.intervaltimer.di

import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.wear.ongoing.OngoingActivity
import androidx.wear.ongoing.Status
import dev.lucianosantos.intervaltimer.R
import dev.lucianosantos.intervaltimer.core.data.TimerState
import dev.lucianosantos.intervaltimer.core.service.NotificationHelper
import dev.lucianosantos.intervaltimer.core.service.OngoingActivityWrapper

class OngoingActivityWrapperImpl : OngoingActivityWrapper {

    override fun setOngoingActivity(
        timerState: TimerState,
        applicationContext: Context,
        onTouchIntent: PendingIntent,
        message: String,
        notificationBuilder: NotificationCompat.Builder
    ) {
        val ongoingActivityStatus = Status.Builder()
            .addTemplate(message)
            .build()

        val ongoingActivity =
            OngoingActivity.Builder(applicationContext,
                NotificationHelper.NOTIFICATION_ID, notificationBuilder)
                .setTouchIntent(onTouchIntent)
                .setStatus(ongoingActivityStatus)

        val icon = when(timerState) {
            TimerState.PREPARE -> R.drawable.ic_ongoing_prepare
            TimerState.TRAIN -> R.drawable.ic_ongoing_train
            TimerState.REST -> R.drawable.ic_ongoing_rest
            TimerState.FINISHED -> R.drawable.ic_ongoing_finished
            else -> null
        }
        icon?.let {
            ongoingActivity.setStaticIcon(it)
        }
        ongoingActivity.build().apply(applicationContext)
    }

}