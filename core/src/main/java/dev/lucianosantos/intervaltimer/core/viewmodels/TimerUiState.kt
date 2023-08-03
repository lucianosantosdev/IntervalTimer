package dev.lucianosantos.intervaltimer.core.viewmodels

import dev.lucianosantos.intervaltimer.core.data.TimerState

data class TimerUiState(
    val remainingSections : Int,
    val currentTime : String,
    val timerState : TimerState,
)