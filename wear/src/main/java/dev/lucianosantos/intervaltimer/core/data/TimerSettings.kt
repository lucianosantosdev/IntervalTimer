package dev.lucianosantos.intervaltimer.core.data

import kotlin.time.Duration

data class TimerSettings(
    private val sets: Int,
    private val trainTime: Duration,
    private val restTime: Duration
)