package dev.lucianosantos.intervaltimer

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationChannel.DEFAULT_CHANNEL_ID
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_NO_CREATE
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.wear.ongoing.OngoingActivity
import androidx.wear.ongoing.Status
import dev.lucianosantos.intervaltimer.MainActivity.Companion.EXTRA_LAUNCH_FROM_NOTIFICATION
import dev.lucianosantos.intervaltimer.core.data.DefaultTimerSettings
import dev.lucianosantos.intervaltimer.core.data.TimerSettings
import dev.lucianosantos.intervaltimer.core.data.TimerState
import dev.lucianosantos.intervaltimer.core.service.CountDownTimer
import dev.lucianosantos.intervaltimer.core.utils.AlertUserHelper
import dev.lucianosantos.intervaltimer.core.utils.CountDownTimerHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.security.SecureRandom

class CountDownTimerServiceWear : LifecycleService() {

    private var countDownTimer = CountDownTimer(
        DefaultTimerSettings.settings,
        CountDownTimerHelper(),
        AlertUserHelper(this)
    )

    private val binder = CountDownTimerBinder()
    private lateinit var notificationManager: NotificationManager
    private var configurationChange = false
    private var serviceRunningInForeground = false
    private var walkingWorkoutActive = true

    val timerState = countDownTimer.timerState
    val remainingSections = countDownTimer.remainingSections
    val currentTimeSeconds = countDownTimer.currentTimeSeconds
    val isPaused = countDownTimer.isPaused

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "On create!")
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        startService(Intent(applicationContext, CountDownTimerServiceWear::class.java))
    }
    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        Log.d(TAG, "On bind!")
        notForegroundService()
        return binder
    }

    override fun onRebind(intent: Intent) {
        super.onRebind(intent)
        notForegroundService()
    }
    override fun onUnbind(intent: Intent): Boolean {
        Log.d(TAG, "onUnbind()")
        Log.d(TAG, "$configurationChange")
        Log.d(TAG, "$walkingWorkoutActive")

        if (!configurationChange && timerState.value != TimerState.STOPED) {
            Log.d(TAG, "Start foreground service")
            val notification = generateNotification("")
            startForeground(NOTIFICATION_ID, notification)
            serviceRunningInForeground = true
        }

        return true
    }
    private fun generateNotification(mainText: String?): Notification {
        Log.d(TAG, "generateNotification()")
        val titleText =getString(R.string.app_name)
        val notificationChannel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            titleText,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(notificationChannel)

        val bigTextStyle = NotificationCompat.BigTextStyle()
            .bigText(mainText)
            .setBigContentTitle(titleText)


        val launchActivityIntent = Intent(this, MainActivity::class.java)
        launchActivityIntent.putExtra(EXTRA_LAUNCH_FROM_NOTIFICATION, true)
        val activityPendingIntent = PendingIntent.getActivity(
            this, 0, launchActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 4. Build and issue the notification.
        val notificationCompatBuilder =
            NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL_ID)

        // TODO: Review Notification builder code.
        val notificationBuilder = notificationCompatBuilder
            .setStyle(bigTextStyle)
            .setContentTitle(titleText)
            .setContentText(mainText)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setOngoing(true)
            .setCategory(NotificationCompat.CATEGORY_WORKOUT)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
//            .addAction(
//                R.drawable.plat, getString(R.string.launch_activity),
//                activityPendingIntent
//            )
//            .addAction(
//                R.drawable.ic_cancel,
//                getString(R.string.stop_walking_workout_notification_text),
//                servicePendingIntent
//            )

        val ongoingActivityStatus = Status.Builder()
//            .addTemplate(mainText)
            .build()

        val ongoingActivity =
            OngoingActivity.Builder(applicationContext, NOTIFICATION_ID, notificationBuilder)
                .setStaticIcon(R.drawable.ic_launcher_foreground)
                .setTouchIntent(activityPendingIntent)
                .setStatus(ongoingActivityStatus)
                .build()

        ongoingActivity.apply(applicationContext)
        return notificationBuilder.build()
    }

    inner class CountDownTimerBinder : Binder() {
        fun getService(): CountDownTimerServiceWear = this@CountDownTimerServiceWear
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        configurationChange = true
    }

    private fun notForegroundService() {
        stopForeground(STOP_FOREGROUND_REMOVE)
        serviceRunningInForeground = false
        configurationChange = false
    }

    fun setTimerSettings(newTimerSettings: TimerSettings) {
        countDownTimer.setTimerSettings(newTimerSettings)
    }
    fun start() {
        CoroutineScope(Dispatchers.Default).launch {
            countDownTimer.start()
        }
    }

    fun pause() {
        countDownTimer.pause()
    }

    fun resume() {
        countDownTimer.resume()
    }

    fun stop() {
        countDownTimer.stop()
    }

    companion object {
        private const val TAG = "CountDownTimerService"

        private val NOTIFICATION_ID = SecureRandom().nextInt()

        private const val EXTRA_CANCEL_WORKOUT_FROM_NOTIFICATION = "EXTRA_CANCEL_WORKOUT_FROM_NOTIFICATION"

        private const val NOTIFICATION_CHANNEL_ID = "interval_timer_workout_channel_01"
    }
}