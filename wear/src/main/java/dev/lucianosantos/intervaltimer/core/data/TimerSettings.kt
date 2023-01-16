package dev.lucianosantos.intervaltimer.core.data

data class TimerSettings(
    val sets: Int,
    val trainTimeSeconds: Long,
    val restTimeSeconds: Long
)