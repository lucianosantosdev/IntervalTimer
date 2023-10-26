package dev.lucianosantos.intervaltimer.core.service

import android.app.Notification
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

class NotificationHelper(
    private val applicationContext: Context,
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
        launchActivityIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        return PendingIntent.getActivity(
            applicationContext,
            1,
            launchActivityIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun pauseIntent() : PendingIntent {
        val pauseIntent = Intent()
        pauseIntent.action = CountDownTimerService.ACTION_PAUSE
        return PendingIntent.getBroadcast(
            applicationContext,
            1,
            pauseIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun resumeIntent() : PendingIntent {
        val resumeIntent = Intent()
        resumeIntent.action = CountDownTimerService.ACTION_RESUME
        return PendingIntent.getBroadcast(
            applicationContext,
            1,
            resumeIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun stopIntent() : PendingIntent {
        val stopIntent = Intent()
        stopIntent.action = CountDownTimerService.ACTION_STOP
        return PendingIntent.getBroadcast(
            applicationContext,
            1,
            stopIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun restartIntent() : PendingIntent {
        val stopIntent = Intent()
        stopIntent.action = CountDownTimerService.ACTION_RESTART
        return PendingIntent.getBroadcast(
            applicationContext,
            1,
            stopIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun setNotificationActions(
        timerState: TimerState,
        isPaused: Boolean,
        notificationBuilder: NotificationCompat.Builder
    ) {
        val style = MediaStyle().setMediaSession(null)
        when {
            isPaused -> {
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
            }
            timerState.equals(TimerState.FINISHED) -> {
                style.setShowActionsInCompactView(0, 1)
                notificationBuilder
                    .addAction(
                        R.drawable.ic_baseline_refresh_24,
                        "refresh",
                        restartIntent()
                    )
                    .addAction(
                        R.drawable.ic_baseline_stop_24,
                        "stop",
                        stopIntent()
                    )
            }
            else -> {
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
        }
        notificationBuilder.setStyle(style)
    }

    fun generateNotification(timerState: TimerState,
                             timeSeconds: Int,
                             isPaused: Boolean
    ) : Notification {
        val titleText = getTitleText(timerState)
        val contentText = getContentText(timerState, timeSeconds)

        val notificationBuilder = NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(titleText)
            .setContentText(contentText)
            .setContentIntent(activityLauncherIntent())
            .setSmallIcon(R.drawable.ic_notification)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setOngoing(true)
            .setCategory(NotificationCompat.CATEGORY_WORKOUT)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        setNotificationActions(timerState, isPaused, notificationBuilder)

        when(timerState) {
            TimerState.PREPARE -> applicationContext.getColor(R.color.prepare_color)
            TimerState.REST -> applicationContext.getColor(R.color.rest_color)
            TimerState.TRAIN -> applicationContext.getColor(R.color.train_color)
            TimerState.FINISHED -> applicationContext.getColor(R.color.finished_color)
            else -> null
        }?.let {
            notificationBuilder.setColor(it)
        }
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
            TimerState.NONE,
            TimerState.STOPPED -> ""
            TimerState.PREPARE -> applicationContext.getString(R.string.state_prepare_text)
            TimerState.TRAIN -> applicationContext.getString(R.string.state_train_text)
            TimerState.REST -> applicationContext.getString(R.string.state_rest_text)
            TimerState.FINISHED -> applicationContext.getString(R.string.state_finished_text)
        }
    }

    private fun getContentText(timerState: TimerState, seconds: Int) : String =
        if(timerState != TimerState.FINISHED) {
            formatMinutesAndSeconds(seconds)
        } else {
            ""
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
        private const val NOTIFICATION_CHANNEL_ID = "interval_timer_workout_channel_01"
    }
}