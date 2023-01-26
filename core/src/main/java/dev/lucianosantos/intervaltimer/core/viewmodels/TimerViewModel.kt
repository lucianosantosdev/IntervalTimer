package dev.lucianosantos.intervaltimer.core.viewmodels

import androidx.lifecycle.*
import dev.lucianosantos.intervaltimer.core.data.TimerSettings
import dev.lucianosantos.intervaltimer.core.data.TimerState
import dev.lucianosantos.intervaltimer.core.utils.IBeepHelper
import dev.lucianosantos.intervaltimer.core.utils.ICountDownTimerHelper
import dev.lucianosantos.intervaltimer.core.utils.formatMinutesAndSeconds
import kotlinx.coroutines.launch

class TimerViewModel(
    private val timerSettings: TimerSettings,
    private val countDownTimerHelper: ICountDownTimerHelper,
    private val beepHelper: IBeepHelper
) : ViewModel() {

    private val _uiState: MutableLiveData<UiState> by lazy {
        MutableLiveData<UiState>(UiState(
            remainingSections = timerSettings.sections,
            currentTime = "",
            timerState = TimerState.PREPARE
        ))
    }
    val uiState get() : LiveData<UiState> = _uiState

    fun startTimer() {
        setCurrentState(TimerState.PREPARE)
        startCountDownTimer(timerSettings.prepareTimeSeconds) {
            trainAndRest(timerSettings.sections)
        }
    }

    fun pauseTimer() {
        countDownTimerHelper.pause()
    }

    fun resumeTimer() {
        countDownTimerHelper.resume()
    }

    private fun trainAndRest(section: Int) {
        setRemainingSections(section)
        setCurrentState(TimerState.TRAIN)

        startCountDownTimer(timerSettings.trainTimeSeconds) {
            if ( section == 1 ) {
                setRemainingSections(0)
                setCurrentState(TimerState.FINISHED)
                return@startCountDownTimer
            }
            setCurrentState(TimerState.REST)
            startCountDownTimer(timerSettings.restTimeSeconds) {
                trainAndRest(section - 1)
            }
        }
    }

    private fun startCountDownTimer(seconds: Int, onFinished: () -> Unit) {
        setCurrentTime(seconds)
        countDownTimerHelper.startCountDown(seconds, { secondsUntilFinished ->
            setCurrentTime(secondsUntilFinished.toInt())
            if (secondsUntilFinished <= 3) {
                notifyUserWithBeep(null)
            }
        }, onFinishCallback = {
            setCurrentTime(0)
            onFinished()
        })
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
        notifyUserWithBeep(_uiState.value?.timerState)
    }

    private fun notifyUserWithBeep(state: TimerState?) {
        viewModelScope.launch {
            when (state) {
                TimerState.PREPARE -> beepHelper.startPrepareBeep()
                TimerState.TRAIN -> beepHelper.startTrainBeep()
                TimerState.REST -> beepHelper.startRestBeep()
                TimerState.FINISHED -> beepHelper.finishedBeep()
                else -> beepHelper.timerAlmostFinishingBeep()
            }
        }
    }

    data class UiState(
        val remainingSections : Int,
        val currentTime : String,
        val timerState : TimerState,
    )

    @Suppress("UNCHECKED_CAST")
    class Factory(
        val timerSettings: TimerSettings,
        val countDownTimerHelper: ICountDownTimerHelper,
        val beepHelper: IBeepHelper
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return TimerViewModel(timerSettings, countDownTimerHelper, beepHelper) as T
        }
    }
}
