package dev.lucianosantos.intervaltimer.core.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.lucianosantos.intervaltimer.core.data.TimerSettings
import dev.lucianosantos.intervaltimer.core.data.TimerState
import dev.lucianosantos.intervaltimer.core.utils.AlertUserHelper
import dev.lucianosantos.intervaltimer.core.utils.CountDownTimerHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class CountDownTimerService : Service() {

    var timerState by mutableStateOf(TimerState.PREPARE)
        private set

    var remainingSections by mutableStateOf(0)
        private set

    var currentTimeSeconds by mutableStateOf(0)
        private set

    var isPaused by mutableStateOf(false)
        private set

    private val binder = CountDownTimerBinder()

    private var countDownTimer: CountDownTimer? = null

    override fun onBind(intent: Intent?): IBinder = binder

    inner class CountDownTimerBinder : Binder() {
        fun getService(): CountDownTimerService = this@CountDownTimerService
    }

    fun start(newTimerSettings: TimerSettings) {
        countDownTimer = CountDownTimer(
            newTimerSettings,
            CountDownTimerHelper(),
            AlertUserHelper(this)
        )

        CoroutineScope(Dispatchers.Default).launch {
            countDownTimer!!.start()
        }
    }

    fun pause() {
        countDownTimer?.pause()
    }

    fun resume() {
        countDownTimer?.resume()
    }

    fun stop() {
        countDownTimer?.stop()
    }

    companion object {
        private const val TAG = "CountDownTimerService"
    }

}