package dev.lucianosantos.intervaltimer.core.data

import android.os.CountDownTimer
import android.text.format.DateUtils
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class TimerViewModel : ViewModel() {

    private val TAG: String = javaClass.name

    private val _uiState: MutableLiveData<UiState> by lazy {
        MutableLiveData<UiState>(UiState())
    }
    val uiState get() : LiveData<UiState> = _uiState

    fun startTimer() {
        Log.d(TAG, "Start timer")
        val timer = object: CountDownTimer(20000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                _uiState.value?.let { currentUiState ->
                    _uiState.value = currentUiState.copy(
                        currentTime = DateUtils.formatElapsedTime(millisUntilFinished / 1000)
                    )
                }
                Log.d(TAG, "${_uiState.value?.currentTime}")
            }

            override fun onFinish() {
            }
        }
        timer.start()
    }

    data class UiState(
        val timerSettings: TimerSettings = TimerSettings(1, 10.seconds, 10.seconds),
        val currentTime : String = "",
        val timerState : TimerState = TimerState.PREPARE,
    )

    class Factory() : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return TimerViewModel() as T
        }
    }
}