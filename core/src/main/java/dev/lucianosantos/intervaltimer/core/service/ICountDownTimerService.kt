package dev.lucianosantos.intervaltimer.core.service

import dev.lucianosantos.intervaltimer.core.data.TimerSettings
import dev.lucianosantos.intervaltimer.core.data.TimerState
import kotlinx.coroutines.flow.StateFlow

interface ICountDownTimerService {

    val timerState : StateFlow<TimerState>?

    val remainingSections : StateFlow<Int>?

    val currentTimeSeconds : StateFlow<Int>?

    val isPaused : StateFlow<Boolean>?

    fun setTimerSettings(newTimerSettings: TimerSettings)

    fun start()

    fun pause()

    fun resume()

    fun stop()

    fun reset()
}