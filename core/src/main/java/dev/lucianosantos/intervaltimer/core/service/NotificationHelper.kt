package dev.lucianosantos.intervaltimer.core.service

import android.app.Notification
import android.app.Notification.Builder
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.media.app.NotificationCompat.MediaStyle
import dev.lucianosantos.intervaltimer.core.R
import dev.lucianosantos.intervaltimer.core.data.TimerState
import dev.lucianosantos.intervaltimer.core.utils.formatMinutesAndSeconds
import java.security.SecureRandom

class NotificationHelper(
    private val applicationContext: Context,
    private val serviceContext: Context,
    private val ongoingActivityWrapper: OngoingActivityWrapper,
    private val mainActivity: Class<*>
) {
    private val notificationManager =  applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return
        }
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            applicationContext.getString(R.string.app_name),
            NotificationManager.IMPORTANCE_LOW
        )
        channel.description = "Used to display ongoing timer"
        notificationManager.createNotificationChannel(channel)
    }

    private fun activityLauncherIntent() : PendingIntent {
        val launchActivityIntent = Intent(applicationContext, mainActivity)
        launchActivityIntent.putExtra(EXTRA_LAUNCH_FROM_NOTIFICATION, true)
        return PendingIntent.getActivity(
            applicationContext,
            1,
            launchActivityIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun pauseIntent() : PendingIntent {
        val pauseIntent = Intent(applicationContext, CountDownTimerService::class.java)
        pauseIntent.action = ACTION_PAUSE
        return PendingIntent.getBroadcast(
            applicationContext,
            2,
            pauseIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun resumeIntent() : PendingIntent {
        val resumeIntent = Intent(applicationContext, CountDownTimerService::class.java)
        resumeIntent.action = ACTION_RESUME
        return PendingIntent.getBroadcast(
            applicationContext,
            3,
            resumeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun stopIntent() : PendingIntent {
        val stopIntent = Intent(applicationContext, CountDownTimerService::class.java)
        stopIntent.action = ACTION_STOP
        return PendingIntent.getBroadcast(
            applicationContext,
            4,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun setNotificationActions(isPaused: Boolean,
                                       notificationBuilder: NotificationCompat.Builder) {
        val style = MediaStyle().setMediaSession(null)
        if (isPaused) {
            style.setShowActionsInCompactView(0, 1)
            notificationBuilder
                .addAction(
                    R.drawable.ic_baseline_play_arrow_24,
                    "play",
                    resumeIntent()
                )
                .addAction(
                    R.drawable.ic_baseline_stop_24,
                    "stop",
                    stopIntent()
                )
        } else {
            style.setShowActionsInCompactView(0, 1)
            notificationBuilder
                .addAction(
                    R.drawable.ic_baseline_pause_24,
                    "pause",
                    pauseIntent()
                )
                .addAction(
                R.drawable.ic_baseline_stop_24,
                "stop",
                stopIntent()
            )
        }
        notificationBuilder.setStyle(style)
    }

    fun generateNotification(timerState: TimerState,
                             timeSeconds: Int,
                             isPaused: Boolean
    ) : Notification {
        val titleText = getTitleText(timerState)
        val contentText = getContentText(timeSeconds)

        val notificationBuilder = NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(titleText)
            .setContentText(contentText)
            .setContentIntent(activityLauncherIntent())
            .setSmallIcon(R.mipmap.ic_launcher)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setOngoing(true)
            .setCategory(NotificationCompat.CATEGORY_WORKOUT)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        setNotificationActions(isPaused, notificationBuilder)

        ongoingActivityWrapper.setOngoingActivity(
            applicationContext = applicationContext,
            onTouchIntent = activityLauncherIntent(),
            message = "$titleText · $contentText",
            notificationBuilder = notificationBuilder
        )
        return notificationBuilder.build()
    }

    private fun getTitleText(timerState: TimerState) : String {
        return when(timerState) {
            TimerState.STOPED -> ""
            TimerState.PREPARE -> applicationContext.getString(R.string.state_prepare_text)
            TimerState.TRAIN -> applicationContext.getString(R.string.state_train_text)
            TimerState.REST -> applicationContext.getString(R.string.state_rest_text)
            TimerState.FINISHED -> applicationContext.getString(R.string.state_finished_text)
        }
    }

    private fun getContentText(seconds: Int) : String {
        return formatMinutesAndSeconds(seconds)
    }

    fun notify(notification: Notification) {
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    fun cancel() {
        notificationManager.cancel(NOTIFICATION_ID)
    }

    companion object {
        val NOTIFICATION_ID = 1
        const val EXTRA_LAUNCH_FROM_NOTIFICATION = "EXTRA_LAUNCH_FROM_NOTIFICATION"
        const val ACTION_PAUSE = "INTERVAL_TIMER_ACTION_PAUSE"
        const val ACTION_RESUME = "INTERVAL_TIMER_ACTION_RESUME"
        const val ACTION_STOP = "INTERVAL_TIMER_ACTION_STOP"
        private const val NOTIFICATION_CHANNEL_ID = "interval_timer_workout_channel_01"
    }
}