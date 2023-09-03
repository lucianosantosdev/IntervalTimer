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

    private val countDownTimer = CountDownTimerHelper()

    private val eventChannel = Channel<Event>()

    private val eventsFlow = eventChannel.receiveAsFlow()

    private var timerSettings: TimerSettings? = null

    private val corroutineScope = CoroutineScope(Dispatchers.Main)

    init {
        eventsFlow.onEach {
            if (timerSettings == null) {
                return@onEach
            }
            when(it) {
                is Event.Prepare -> {
                    timerState = TimerState.PREPARE
                    remainingSections = timerSettings!!.sections
                    startCountDownTimer(timerSettings!!.prepareTimeSeconds)
                }
                is Event.Train -> {
                    timerState = TimerState.TRAIN
                    startCountDownTimer(timerSettings!!.trainTimeSeconds)
                }
                is Event.Rest -> {
                    timerState = TimerState.REST
                    startCountDownTimer(timerSettings!!.restTimeSeconds)
                }
                is Event.Finished -> {
                    remainingSections = 0
                    timerState = TimerState.FINISHED
                }
            }
        }.launchIn(corroutineScope)
    }

    override fun onBind(intent: Intent?): IBinder = binder

    inner class CountDownTimerBinder : Binder() {
        fun getService(): CountDownTimerService = this@CountDownTimerService
    }

    fun start(newTimerSettings: TimerSettings) {
        timerSettings = newTimerSettings
        isPaused = false
        CoroutineScope(Dispatchers.Default).launch {
            eventChannel.send(Event.Prepare)
        }

    }

    fun pause() {
        isPaused = true
        countDownTimer.pause()
    }

    fun resume() {
        isPaused = false
        countDownTimer.resume()
    }

    fun stop() {
        isPaused = false
        countDownTimer.stop()
    }

    private fun startCountDownTimer(seconds: Int) {
        currentTimeSeconds = seconds
        countDownTimer.startCountDown(seconds, { secondsUntilFinished ->
            currentTimeSeconds = secondsUntilFinished.toInt()
            if (secondsUntilFinished <= 3) {
                alertUser(null)
            }
        }, onFinishCallback = {
            currentTimeSeconds = 0
            resolveNextEvent()
        })
    }

    private fun resolveNextEvent() {
        corroutineScope.launch {
            val currentRemainingSections = remainingSections
            Log.d(TAG, "Current state: ${remainingSections} - Remaining sections : $currentRemainingSections")

            when (timerState) {
                TimerState.PREPARE -> {
                    afterPrepare()
                }
                TimerState.REST -> {
                    afterRest(currentRemainingSections)
                }
                TimerState.TRAIN -> {
                    afterTrain(currentRemainingSections)
                }
                else -> {
                    // Nothing to do here.
                }
            }
        }
    }

    private suspend fun afterPrepare() {
        eventChannel.send(Event.Train)
    }

    private suspend fun afterRest(remainingSections: Int) {
        this.remainingSections = remainingSections - 1
        eventChannel.send(Event.Train)
    }

    private suspend fun  afterTrain(remainingSections: Int) {
        if (remainingSections > 1) {
            eventChannel.send(Event.Rest)
        } else {
            eventChannel.send(Event.Finished)
        }
    }

    private fun alertUser(state: TimerState?) {
        corroutineScope.launch(Dispatchers.IO) {
            val alertUserHelper = AlertUserHelper(this@CountDownTimerService)
            when (state) {
                TimerState.PREPARE -> alertUserHelper.startPrepareAlert()
                TimerState.TRAIN -> alertUserHelper.startTrainAlert()
                TimerState.REST -> alertUserHelper.startRestAlert()
                TimerState.FINISHED -> alertUserHelper.finishedAlert()
                else -> alertUserHelper.timerAlmostFinishingAlert()
            }
        }
    }

    sealed class Event {
        object Prepare: Event()
        object Train: Event()
        object Rest: Event()
        object Finished: Event()
    }

    companion object {
        private const val TAG = "CountDownTimerService"
    }

}