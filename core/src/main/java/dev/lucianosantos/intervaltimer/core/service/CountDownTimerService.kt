package dev.lucianosantos.intervaltimer.core.service

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dev.lucianosantos.intervaltimer.core.R
import dev.lucianosantos.intervaltimer.core.data.DefaultTimerSettings
import dev.lucianosantos.intervaltimer.core.data.TimerSettings
import dev.lucianosantos.intervaltimer.core.data.TimerState
import dev.lucianosantos.intervaltimer.core.utils.AlertUserHelper
import dev.lucianosantos.intervaltimer.core.utils.CountDownTimerHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

abstract class CountDownTimerService(
    private val serviceName: Class<*>
) : ICountDownTimerService, LifecycleService() {
    private var countDownTimer = CountDownTimer(
        DefaultTimerSettings.settings,
        CountDownTimerHelper(),
        AlertUserHelper(this)
    )

    private val binder = CountDownTimerBinder()
    private lateinit var notificationHelper: NotificationHelper
    private var configurationChange = false
    private var serviceRunningInForeground = false
    private var walkingWorkoutActive = true

    override val timerState = countDownTimer.timerState
    override val remainingSections = countDownTimer.remainingSections
    override val currentTimeSeconds = countDownTimer.currentTimeSeconds
    override val isPaused = countDownTimer.isPaused

    abstract val ongoingActivityWrapper: OngoingActivityWrapper

    abstract val mainActivity: Class<*>

    override fun onCreate() {
        super.onCreate()

        Log.d(TAG, "On create!")
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationHelper = NotificationHelper(
            notificationManager = notificationManager,
            applicationContext = applicationContext,
            serviceContext = this,
            title = getString(R.string.app_name),
            ongoingActivityWrapper = ongoingActivityWrapper,
            mainActivity = mainActivity
        )

        startService(Intent(applicationContext, serviceName))

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                currentTimeSeconds.collect {
                    if (serviceRunningInForeground) {
                        val notification = notificationHelper.generateNotification(
                            timeSeconds = it,
                            timerState = timerState.value,
                            isPaused = isPaused.value
                        )
                        notificationHelper.notify(notification)
                    }
                }
            }
        }
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
            val notification = notificationHelper.generateNotification(
                timerState = timerState.value,
                timeSeconds = currentTimeSeconds.value,
                isPaused = isPaused.value
            )
            startForeground(NotificationHelper.NOTIFICATION_ID, notification)
            serviceRunningInForeground = true
        }

        return true
    }

    inner class CountDownTimerBinder : Binder() {
        fun getService(): CountDownTimerService = this@CountDownTimerService
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

    override fun setTimerSettings(newTimerSettings: TimerSettings) {
        countDownTimer.setTimerSettings(newTimerSettings)
    }

    override fun start() {
        CoroutineScope(Dispatchers.Default).launch {
            countDownTimer.start()
        }
    }

    override fun pause() {
        countDownTimer.pause()
    }

    override fun resume() {
        countDownTimer.resume()
    }

    override fun stop() {
        countDownTimer.stop()
    }

    companion object {
        private const val TAG = "CountDownTimerService"
    }
}