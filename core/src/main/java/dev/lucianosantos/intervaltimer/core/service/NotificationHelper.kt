package dev.lucianosantos.intervaltimer.core.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import dev.lucianosantos.intervaltimer.core.R
import dev.lucianosantos.intervaltimer.core.data.TimerState
import dev.lucianosantos.intervaltimer.core.utils.formatMinutesAndSeconds
import java.security.SecureRandom

class NotificationHelper(
    private val notificationManager: NotificationManager,
    private val applicationContext: Context,
    private val serviceContext: Context,
    private val title: String,
    private val ongoingActivityWrapper: OngoingActivityWrapper,
    private val mainActivity: Class<*>
) {
    fun generateNotification(timerState: TimerState,
                             timeSeconds: Int,
                             isPaused: Boolean
    ) : Notification {
        val message = formatNotification(timerState, timeSeconds)
        val notificationChannel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            title,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(notificationChannel)

        val bigTextStyle = NotificationCompat.BigTextStyle()
            .bigText(message)
            .setBigContentTitle(title)


        val launchActivityIntent = Intent(serviceContext, mainActivity)
        launchActivityIntent.putExtra(EXTRA_LAUNCH_FROM_NOTIFICATION, true)
        val activityPendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            launchActivityIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 4. Build and issue the notification.
        val notificationCompatBuilder =
            NotificationCompat.Builder(applicationContext,
                NOTIFICATION_CHANNEL_ID
            )

        val notificationBuilder = notificationCompatBuilder
            .setStyle(bigTextStyle)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setOngoing(true)
            .setCategory(NotificationCompat.CATEGORY_WORKOUT)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)


        ongoingActivityWrapper.setOngoingActivity(
            applicationContext = applicationContext,
            onTouchIntent = activityPendingIntent,
            message = message,
            notificationBuilder = notificationBuilder
        )
        return notificationBuilder.build()
    }

    private fun formatNotification(timerState: TimerState, seconds: Int) : String {
        val status = when(timerState) {
            TimerState.STOPED -> ""
            TimerState.PREPARE -> applicationContext.getString(R.string.state_prepare_text)
            TimerState.TRAIN -> applicationContext.getString(R.string.state_train_text)
            TimerState.REST -> applicationContext.getString(R.string.state_rest_text)
            TimerState.FINISHED -> applicationContext.getString(R.string.state_finished_text)
        }
        val time = formatMinutesAndSeconds(seconds)
        return "$status · $time"
    }

    fun notify(notification: Notification) {
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    fun startForeground(notification: Notification) {

    }
    companion object {
        val NOTIFICATION_ID = SecureRandom().nextInt()

        const val EXTRA_LAUNCH_FROM_NOTIFICATION = "EXTRA_LAUNCH_FROM_NOTIFICATION"

        private const val EXTRA_CANCEL_WORKOUT_FROM_NOTIFICATION = "EXTRA_CANCEL_WORKOUT_FROM_NOTIFICATION"

        private const val NOTIFICATION_CHANNEL_ID = "interval_timer_workout_channel_01"
    }
}