package dev.lucianosantos.intervaltimer.di

import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.wear.ongoing.OngoingActivity
import androidx.wear.ongoing.Status
import dev.lucianosantos.intervaltimer.R
import dev.lucianosantos.intervaltimer.core.service.NotificationHelper
import dev.lucianosantos.intervaltimer.core.service.OngoingActivityWrapper

class OngoingActivityWrapperImpl : OngoingActivityWrapper {

    override fun setOngoingActivity(
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
                .setStaticIcon(R.mipmap.ic_launcher)
                .setTouchIntent(onTouchIntent)
                .setStatus(ongoingActivityStatus)
                .build()

        ongoingActivity.apply(applicationContext)
    }

}