package dev.lucianosantos.intervaltimer.core.viewmodels

import android.util.Log
import androidx.lifecycle.*
import dev.lucianosantos.intervaltimer.core.data.TimerSettings
import dev.lucianosantos.intervaltimer.core.data.TimerState
import dev.lucianosantos.intervaltimer.core.utils.IAlertUserHelper
import dev.lucianosantos.intervaltimer.core.utils.ICountDownTimerHelper
import dev.lucianosantos.intervaltimer.core.utils.formatMinutesAndSeconds
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class TimerViewModel(
    private val timerSettings: TimerSettings,
    private val countDownTimerHelper: ICountDownTimerHelper,
    private val alertUserHelper: IAlertUserHelper
) : ViewModel() {

    private val eventChannel = Channel<Event>()
    private val eventsFlow = eventChannel.receiveAsFlow()

    private val _uiState: MutableLiveData<TimerUiState> by lazy {
        MutableLiveData<TimerUiState>(TimerUiState(
            remainingSections = timerSettings.sections,
            currentTime = "",
            timerState = TimerState.PREPARE
        ))
    }
    val timerUiState get() : LiveData<TimerUiState> = _uiState
    
    init {
        setEventHandler()
    }

    private fun setEventHandler() {
        eventsFlow.onEach {
            when(it) {
                is Event.Prepare -> {
                    setCurrentState(TimerState.PREPARE)
                    startCountDownTimer(timerSettings.prepareTimeSeconds)
                }
                is Event.Train -> {
                    setCurrentState(TimerState.TRAIN)
                    startCountDownTimer(timerSettings.trainTimeSeconds)
                }
                is Event.Rest -> {
                    setCurrentState(TimerState.REST)
                    startCountDownTimer(timerSettings.restTimeSeconds)
                }
                is Event.Finished -> {
                    setRemainingSections(0)
                    setCurrentState(TimerState.FINISHED)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun startTimer() {
        viewModelScope.launch {
            eventChannel.send(Event.Prepare)
        }
    }

    fun pauseTimer() {
        countDownTimerHelper.pause()
    }

    fun resumeTimer() {
        countDownTimerHelper.resume()
    }

    private fun startCountDownTimer(seconds: Int) {
        setCurrentTime(seconds)
        countDownTimerHelper.startCountDown(seconds, { secondsUntilFinished ->
            setCurrentTime(secondsUntilFinished.toInt())
            if (secondsUntilFinished <= 3) {
                alertUser(null)
            }
        }, onFinishCallback = {
            setCurrentTime(0)
            resolveNextEvent()
        })
    }

    private fun resolveNextEvent() {
        viewModelScope.launch {
            val currentRemainingSections = _uiState.value?.remainingSections ?: 0
            Log.d(TAG, "Current state: ${_uiState.value?.timerState} - Remaining sections : $currentRemainingSections")

            when (_uiState.value?.timerState) {
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
        setRemainingSections(remainingSections - 1)
        eventChannel.send(Event.Train)
    }

    private suspend fun  afterTrain(remainingSections: Int) {
        if (remainingSections > 1) {
            eventChannel.send(Event.Rest)
        } else {
            eventChannel.send(Event.Finished)
        }
    }

    private fun setRemainingSections(sections: Int) {
        _uiState.value?.let { currentUiState ->
            _uiState.value = currentUiState.copy(
                remainingSections = sections
            )
        }
    }

    private fun setCurrentTime(seconds: Int) {
        _uiState.value?.let { currentUiState ->
            _uiState.value = currentUiState.copy(
                currentTime = formatMinutesAndSeconds(seconds)
            )
        }
    }

    private fun setCurrentState(timerState: TimerState) {
        _uiState.value?.let { currentUiState ->
            _uiState.value = currentUiState.copy(
                timerState = timerState
            )
        }
        alertUser(_uiState.value?.timerState)
    }

    private fun alertUser(state: TimerState?) {
        viewModelScope.launch {
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

    @Suppress("UNCHECKED_CAST")
    class Factory(
        val timerSettings: TimerSettings,
        val countDownTimerHelper: ICountDownTimerHelper,
        val beepHelper: IAlertUserHelper
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return TimerViewModel(timerSettings, countDownTimerHelper, beepHelper) as T
        }
    }

    companion object {
        private const val TAG = "TimerViewModel"
    }
}
