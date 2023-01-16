package dev.lucianosantos.intervaltimer.core.data

import android.media.AudioManager
import android.media.ToneGenerator
import android.text.format.DateUtils
import androidx.lifecycle.*
import kotlinx.coroutines.delay
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
        startCountDownTimer(timerSettings.trainTimeSeconds)
    }

    private fun startCountDownTimer(seconds: Long) {
        longBeep()
        countDownTimerHelper.startCountDown(seconds, object: ICountDownTimerHelper.CountDownTimerListener {
            override fun onTick(secondsUntilFinished: Long) {
                _uiState.value?.let { currentUiState ->
                    _uiState.value = currentUiState.copy(
                        currentTime = DateUtils.formatElapsedTime(secondsUntilFinished)
                    )
                }
                if (secondsUntilFinished <= 3) {
                    shortBeep()
                }
            }
            override fun onFinish() {
                _uiState.value?.let { currentUiState ->
                    _uiState.value = currentUiState.copy(
                        currentTime = DateUtils.formatElapsedTime(0),
                        timerState = TimerState.FINISHED
                    )
                }
                doubleBeep()
            }
        })
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