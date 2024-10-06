package dev.lucianosantos.intervaltimer.core.service

import dev.lucianosantos.intervaltimer.core.data.TimerSettings
import dev.lucianosantos.intervaltimer.core.data.TimerState
import dev.lucianosantos.intervaltimer.core.utils.IAlertUserHelper
import dev.lucianosantos.intervaltimer.core.utils.ICountDownTimerHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class CountDownTimer(
    private var timerSettings: TimerSettings,
    private val countDownTimer: ICountDownTimerHelper,
    private val alertUserHelper: IAlertUserHelper,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main)
) {
    fun setTimerSettings(newTimerSettings: TimerSettings) {
        timerSettings = newTimerSettings
    }

    private val _timerState = MutableStateFlow(TimerState.NONE)
    val timerState : StateFlow<TimerState> = _timerState.asStateFlow()

    private val _remainingSections = MutableStateFlow(timerSettings.sections)
    val remainingSections : StateFlow<Int> = _remainingSections.asStateFlow()

    private val _currentTimeSeconds = MutableStateFlow(0)
    val currentTimeSeconds : StateFlow<Int> = _currentTimeSeconds.asStateFlow()

    private val _isPaused = MutableStateFlow(false)
    val isPaused : StateFlow<Boolean> = _isPaused.asStateFlow()

    private val eventChannel = Channel<Event>()
    private val eventsFlow = eventChannel.receiveAsFlow()

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
                    _remainingSections.value = 0
                    setTimerStateAndAlert(TimerState.FINISHED)
                }
            }
        }.launchIn(coroutineScope)
    }

    suspend fun start() {
        reset()
        _remainingSections.value = timerSettings.sections
        eventChannel.send(Event.Prepare)
    }

    fun pause() {
        _isPaused.value = true
        countDownTimer.pause()
    }

    fun resume() {
        _isPaused.value = false
        countDownTimer.resume()
    }

    fun stop() {
        _isPaused.value = false
        _timerState.value = TimerState.STOPPED
        countDownTimer.stop()
    }

    fun reset() {
        _isPaused.value = false
        _timerState.value = TimerState.NONE
        countDownTimer.stop()
    }

    private fun setTimerStateAndAlert(state: TimerState) {
        _timerState.value = state
        alertUser(state)
    }

    private fun startCountDownTimer(seconds: Int) {
        _currentTimeSeconds.value = seconds
        countDownTimer.startCountDown(seconds, { secondsUntilFinished ->
            _currentTimeSeconds.value = secondsUntilFinished.toInt()
            if (secondsUntilFinished <= 3) {
                alertUser(null)
            }
        }, onFinishCallback = {
            _currentTimeSeconds.value = 0
            resolveNextEvent()
        })
    }

    private fun resolveNextEvent() {
        coroutineScope.launch {
            val currentRemainingSections = _remainingSections.value
            when (_timerState.value) {
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
        _remainingSections.value = remainingSections - 1
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
        coroutineScope.launch {
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