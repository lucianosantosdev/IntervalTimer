package dev.lucianosantos.intervaltimer.core.service

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.lucianosantos.intervaltimer.core.data.TimerSettings
import dev.lucianosantos.intervaltimer.core.data.TimerState
import dev.lucianosantos.intervaltimer.core.utils.AlertUserHelper
import dev.lucianosantos.intervaltimer.core.utils.CountDownTimerHelper
import dev.lucianosantos.intervaltimer.core.utils.IAlertUserHelper
import dev.lucianosantos.intervaltimer.core.utils.ICountDownTimerHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class CountDownTimer(
    val timerSettings: TimerSettings,
    val countDownTimer: ICountDownTimerHelper,
    val alertUserHelper: IAlertUserHelper
) {
    var timerState by mutableStateOf(TimerState.PREPARE)
        private set
    var remainingSections by mutableStateOf(timerSettings.sections)
        private set
    var currentTimeSeconds by mutableStateOf(0)
        private set
    var isPaused by mutableStateOf(false)
        private set

    private val eventChannel = Channel<Event>()
    private val eventsFlow = eventChannel.receiveAsFlow()

    private val corroutineScope = CoroutineScope(Dispatchers.Main)

    init {
        eventsFlow.onEach {
            when(it) {
                is Event.Prepare -> {
                    setTimerStateAndAlert(TimerState.PREPARE)
                    startCountDownTimer(timerSettings.prepareTimeSeconds)
                }
                is Event.Train -> {
                    setTimerStateAndAlert(TimerState.TRAIN)
                    startCountDownTimer(timerSettings.trainTimeSeconds)
                }
                is Event.Rest -> {
                    setTimerStateAndAlert(TimerState.REST)
                    startCountDownTimer(timerSettings.restTimeSeconds)
                }
                is Event.Finished -> {
                    remainingSections = 0
                    setTimerStateAndAlert(TimerState.FINISHED)
                }
            }
        }.launchIn(corroutineScope)
    }

    suspend fun start() {
        isPaused = false
        eventChannel.send(Event.Prepare)
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

    private fun setTimerStateAndAlert(state: TimerState) {
        timerState = state
        alertUser(state)
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
}