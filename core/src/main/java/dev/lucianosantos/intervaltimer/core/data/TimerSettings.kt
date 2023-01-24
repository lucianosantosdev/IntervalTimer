package dev.lucianosantos.intervaltimer.core.data

data class TimerSettings(
    val sections: Int,
    val trainTimeSeconds: Long,
    val restTimeSeconds: Long
)

object DefaultTimerSettings {
    val settings = TimerSettings(
        sections = 1,
        trainTimeSeconds = 60,
        restTimeSeconds = 60
    )
}