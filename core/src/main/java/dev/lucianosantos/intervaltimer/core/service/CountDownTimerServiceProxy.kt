package dev.lucianosantos.intervaltimer.core.service

import androidx.compose.runtime.mutableStateOf
import dev.lucianosantos.intervaltimer.core.data.TimerSettings
import dev.lucianosantos.intervaltimer.core.data.TimerState
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow

class CountDownTimerServiceProxy(
    var countDownTimerService: CountDownTimerService? = null
) : ICountDownTimerService {
    override val timerState: StateFlow<TimerState>
        get() = countDownTimerService?.timerState
    override val remainingSections: StateFlow<Int>
        get() = countDownTimerService?.remainingSections
    override val currentTimeSeconds: StateFlow<Int>
        get() = countDownTimerService?.currentTimeSeconds
    override val isPaused: StateFlow<Boolean>
        get() = countDownTimerService?.isPaused

    override fun setTimerSettings(newTimerSettings: TimerSettings) {
        countDownTimerService?.setTimerSettings(newTimerSettings)
    }

    override fun start() {
        countDownTimerService?.start()
    }

    override fun pause() {
        countDownTimerService?.pause()
    }

    override fun resume() {
        countDownTimerService?.resume()
    }

    override fun stop() {
        countDownTimerService?.stop()
    }

}