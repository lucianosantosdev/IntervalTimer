package dev.lucianosantos.intervaltimer.core.data

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class TimerViewModel : ViewModel() {

    private val _uiSate: MutableLiveData<UiState> by lazy {
        MutableLiveData<UiState>(UiState())
    }
    val uiState get() : LiveData<UiState> = _uiSate

    fun startTimer() {
        val timer = object: CountDownTimer(20000, 1000) {
            override fun onTick(millisUntilFinished: Long) {

            }

            override fun onFinish() {

            }
        }
        timer.start()
    }

    class UiState() {
//        var timerSettings: TimerSettings = TimerSettings(1,1,1)
        var currentTimer = 0
    }

    class Factory() : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return TimerViewModel() as T
        }
    }
}