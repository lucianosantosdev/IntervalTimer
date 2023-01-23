package dev.lucianosantos.intervaltimer.core.data

import android.text.format.DateUtils
import android.util.Log
import androidx.lifecycle.*
import dev.lucianosantos.intervaltimer.core.IBeepHelper
import dev.lucianosantos.intervaltimer.core.ICountDownTimerHelper
import kotlinx.coroutines.launch

class TimerViewModel(
    private val timerSettings: TimerSettings,
    private val countDownTimerHelper: ICountDownTimerHelper,
    private val beepHelper: IBeepHelper
) : ViewModel() {

    private val _uiState: MutableLiveData<UiState> by lazy {
        MutableLiveData<UiState>(UiState())
    }
    val uiState get() : LiveData<UiState> = _uiState

    fun startTimer() {
        setCurrentState(TimerState.PREPARE)
        startCountDownTimer(5) {
            trainAndRest(timerSettings.sets)
        }
    }

    private fun trainAndRest(set: Int) {
        setCurrentState(TimerState.TRAIN)

        startCountDownTimer(timerSettings.trainTimeSeconds) {
            Log.d("TIMER SET", "$set")
            if ( set == 1 ) {
                setCurrentState(TimerState.FINISHED)
                return@startCountDownTimer
            }
            setCurrentState(TimerState.REST)
            startCountDownTimer(timerSettings.restTimeSeconds) {
                trainAndRest(set - 1)
            }
        }
    }

    private fun startCountDownTimer(seconds: Long, onFinished: () -> Unit) {
        countDownTimerHelper.startCountDown(seconds, { secondsUntilFinished ->
            setCurrentTime(secondsUntilFinished)
            if (secondsUntilFinished <= 3) {
                shortBeep()
            }
        }, onFinishCallback = {
            setCurrentTime(0)
            onFinished()
        })
    }

    private fun setCurrentTime(seconds: Long) {
        _uiState.value?.let { currentUiState ->
            _uiState.value = currentUiState.copy(
                currentTime = DateUtils.formatElapsedTime(seconds)
            )
        }
    }

    private fun setCurrentState(timerState: TimerState) {
        _uiState.value?.let { currentUiState ->
            _uiState.value = currentUiState.copy(
                timerState = timerState
            )
        }
        notifyUserStateChanged()
    }

    private fun notifyUserStateChanged() {
        when(_uiState.value?.timerState) {
            TimerState.PREPARE -> longBeep()
            TimerState.TRAIN -> longBeep()
            TimerState.REST -> doubleBeep()
            TimerState.FINISHED -> longBeep()
            else -> {}
        }
    }

    private fun shortBeep() {
        viewModelScope.launch {
            beepHelper.shortBeep()
        }
    }

    private fun longBeep() {
        viewModelScope.launch {
            beepHelper.longBeep()
        }
    }

    private fun doubleBeep() {
        viewModelScope.launch {
            beepHelper.doubleBeep()
        }
    }

    data class UiState(
        val currentTime : String = "",
        val timerState : TimerState = TimerState.PREPARE,
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
