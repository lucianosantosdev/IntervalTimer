package dev.lucianosantos.intervaltimer.core.service

import android.app.PendingIntent
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
import dev.lucianosantos.intervaltimer.core.data.DefaultTimerSettings
import dev.lucianosantos.intervaltimer.core.data.TimerSettings
import dev.lucianosantos.intervaltimer.core.data.TimerSettingsRepositoryImpl
import dev.lucianosantos.intervaltimer.core.data.TimerState
import dev.lucianosantos.intervaltimer.core.utils.AlarmManagerHelper
import dev.lucianosantos.intervaltimer.core.utils.AlertUserHelper
import dev.lucianosantos.intervaltimer.core.utils.CountDownTimerHelper
import dev.lucianosantos.intervaltimer.core.utils.WakeReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneId

abstract class CountDownTimerService(
    private val serviceName: Class<*>
) : ICountDownTimerService, LifecycleService() {
    private val serviceJob = Job()
    private val coroutineScope = CoroutineScope(serviceJob + Dispatchers.Main)

    private val settingsRepository by lazy {
        TimerSettingsRepositoryImpl(this)
    }
    private val countDownTimer by lazy {
        CountDownTimer(
            DefaultTimerSettings.settings,
            CountDownTimerHelper(),
            AlertUserHelper(this),
            coroutineScope
        )
    }

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

    private var receiverRegistered = false

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when(intent?.action) {
                ACTION_STOP -> {
                    stop()
                }
                ACTION_PAUSE -> {
                    pause()
                }
                ACTION_RESUME -> {
                    resume()
                }
                ACTION_RESTART -> {
                    restart()
                }
            }
        }
    }

    private lateinit var alarmManagerHelper: AlarmManagerHelper
    private fun registerReceiver() {
        val intentFilter = IntentFilter().apply {
            addAction(ACTION_STOP)
            addAction(ACTION_PAUSE)
            addAction(ACTION_RESUME)
            addAction(ACTION_RESTART)
        }
        registerReceiver(receiver, intentFilter, RECEIVER_EXPORTED)
        receiverRegistered = true
    }

    private fun unregisterReceiver() {
        unregisterReceiver(receiver)
        receiverRegistered = false
    }

    override fun onCreate() {
        super.onCreate()
        registerReceiver()

        Log.d(TAG, "On create!")
        alarmManagerHelper = AlarmManagerHelper(this)
        notificationHelper = NotificationHelper(
            applicationContext = applicationContext,
            ongoingActivityWrapper = ongoingActivityWrapper,
            mainActivity = mainActivity
        )
        notificationHelper.createNotificationChannel()

        startService(Intent(applicationContext, serviceName))

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                currentTimeSeconds.collect {
                    updateNotification()
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                isPaused.collect {
                    updateNotification()
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                timerState.collect {
                    if(timerState.value != TimerState.STOPPED && timerState.value != TimerState.NONE) {
                        updateNotification()
                        scheduleWakeAlarm()
                    }
                }
            }
        }
    }

    private fun scheduleWakeAlarm() {
        Log.d(TAG, "Schedule wake alarm")
        val wakeIntent: PendingIntent = Intent(applicationContext, WakeReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(applicationContext, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        }
        val remainingSeconds = currentTimeSeconds.value.toLong()
        val alarmTime = remainingSeconds - 5L
        val time = LocalDateTime.now().plusSeconds(alarmTime).atZone(ZoneId.systemDefault()).toEpochSecond() * 1000
        alarmManagerHelper.setAlarm(time, wakeIntent)
    }

    private fun updateNotification() {
        if (timerState.value != TimerState.STOPPED && timerState.value != TimerState.NONE) {
            val notification = notificationHelper.generateNotification(
                timeSeconds = currentTimeSeconds.value,
                timerState = timerState.value,
                isPaused = isPaused.value
            )
            notificationHelper.notify(notification)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceJob.cancel()
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

        if (ongoingActivityWrapper.allowForegroundService() &&
            !configurationChange &&
            timerState.value != TimerState.STOPPED
        ) {
            Log.d(TAG, "Start foreground service")
            val notification = notificationHelper.generateNotification(
                timerState = timerState.value,
                timeSeconds = currentTimeSeconds.value,
                isPaused = isPaused.value
            )
            startForeground(NotificationHelper.NOTIFICATION_ID, notification)
            serviceRunningInForeground = true
        } else {
            countDownTimer.stop()
            Log.d(TAG, "Not foreground service")
            notForegroundService()
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
        countDownTimer.setTimerSettings(settingsRepository.loadSettings())
        CoroutineScope(Dispatchers.Default).launch {
            countDownTimer.start()
        }
        registerReceiver()
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
        if (receiverRegistered) {
            unregisterReceiver()
        }
    }

    override fun restart() {
        start()
    }

    override fun reset() {
        stop()
        countDownTimer.reset()
    }

    companion object {
        private const val TAG = "CountDownTimerService"

        const val ACTION_PAUSE = "INTERVAL_TIMER_ACTION_PAUSE"
        const val ACTION_RESUME = "INTERVAL_TIMER_ACTION_RESUME"
        const val ACTION_STOP = "INTERVAL_TIMER_ACTION_STOP"
        const val ACTION_RESTART = "INTERVAL_TIMER_ACTION_RESTART"
    }
}