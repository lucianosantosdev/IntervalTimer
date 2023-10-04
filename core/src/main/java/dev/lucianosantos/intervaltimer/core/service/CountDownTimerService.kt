package dev.lucianosantos.intervaltimer.core.service

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
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

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when(intent?.action) {
                NotificationHelper.ACTION_STOP -> {
                    countDownTimer.stop()
                }
                NotificationHelper.ACTION_PAUSE -> {
                    countDownTimer.pause()
                }
                NotificationHelper.ACTION_RESUME -> {
                    countDownTimer.resume()
                }
            }
        }
    }

    private fun registerReceiver() {
        val intentFilter = IntentFilter().apply {
            addAction(NotificationHelper.ACTION_STOP)
            addAction(NotificationHelper.ACTION_PAUSE)
            addAction(NotificationHelper.ACTION_RESUME)
        }
        registerReceiver(receiver, intentFilter)
    }

    override fun onCreate() {
        super.onCreate()
        registerReceiver()

        Log.d(TAG, "On create!")
        notificationHelper = NotificationHelper(
            applicationContext = applicationContext,
            serviceContext = this,
            ongoingActivityWrapper = ongoingActivityWrapper,
            mainActivity = mainActivity
        )
        notificationHelper.createNotificationChannel()

        startService(Intent(applicationContext, serviceName))

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                currentTimeSeconds.collect {
                    if (timerState.value != TimerState.STOPED) {
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
        unregisterReceiver(receiver)
        Log.d(TAG, "On bind!")
        notForegroundService()
        return binder
    }

    override fun onRebind(intent: Intent) {
        super.onRebind(intent)
        notForegroundService()
    }
    override fun onUnbind(intent: Intent): Boolean {
        registerReceiver()
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
        notificationHelper.cancel()
    }

    companion object {
        private const val TAG = "CountDownTimerService"
    }
}