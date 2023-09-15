package dev.lucianosantos.intervaltimer.core.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.lucianosantos.intervaltimer.core.data.DefaultTimerSettings
import dev.lucianosantos.intervaltimer.core.data.TimerSettings
import dev.lucianosantos.intervaltimer.core.data.TimerState
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

class CountDownTimerService : Service() {
    private var countDownTimer = CountDownTimer(
        DefaultTimerSettings.settings,
        CountDownTimerHelper(),
        AlertUserHelper(this)
    )

    val timerState = countDownTimer.timerState
    val remainingSections = countDownTimer.remainingSections
    val currentTimeSeconds = countDownTimer.currentTimeSeconds
    val isPaused = countDownTimer.isPaused

    private val binder = CountDownTimerBinder()

    override fun onBind(intent: Intent?): IBinder = binder

    inner class CountDownTimerBinder : Binder() {
        fun getService(): CountDownTimerService = this@CountDownTimerService
    }

    fun start(newTimerSettings: TimerSettings) {
        countDownTimer.timerSettings = newTimerSettings
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
    }
}