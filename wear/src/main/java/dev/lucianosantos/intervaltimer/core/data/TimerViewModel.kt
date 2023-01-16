package dev.lucianosantos.intervaltimer.core.data

import android.media.AudioManager
import android.media.ToneGenerator
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.text.format.DateUtils
import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.ceil
import kotlin.math.round
import kotlin.time.Duration.Companion.seconds


class TimerViewModel : ViewModel() {

    private val TAG: String = javaClass.name

    private val _uiState: MutableLiveData<UiState> by lazy {
        MutableLiveData<UiState>(UiState())
    }
    val uiState get() : LiveData<UiState> = _uiState

    fun startTimer() {
        Log.d(TAG, "Start timer")
        val timer = object: CountDownTimer(5000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = round(millisUntilFinished / 1000.0).toLong()

                _uiState.value?.let { currentUiState ->
                    _uiState.value = currentUiState.copy(
                        currentTime = DateUtils.formatElapsedTime(seconds)
                    )
                }

                verifyAndBeep(seconds)
            }

            override fun onFinish() {
                _uiState.value?.let { currentUiState ->
                    _uiState.value = currentUiState.copy(
                        currentTime = DateUtils.formatElapsedTime(0),
                        timerState = TimerState.FINISHED
                    )
                }
                beep()
                beep(200)
            }
        }
        timer.start()
    }

    private fun verifyAndBeep(seconds: Long) {
        if (seconds > 3) {
            return
        }
        beep()
    }

    private fun beep(delayMilliseconds : Long = 0) {
        viewModelScope.launch {
            delay(delayMilliseconds)
            val toneGen1 = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
            toneGen1.startTone(ToneGenerator.TONE_CDMA_EMERGENCY_RINGBACK, 100)
        }
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