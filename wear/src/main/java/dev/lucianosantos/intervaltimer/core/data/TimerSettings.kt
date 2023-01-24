package dev.lucianosantos.intervaltimer.core.data

data class TimerSettings(
    val sections: Int,
    val trainTimeSeconds: Long,
    val restTimeSeconds: Long
)