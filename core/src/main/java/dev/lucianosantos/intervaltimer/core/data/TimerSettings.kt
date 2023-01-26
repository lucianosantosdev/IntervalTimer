package dev.lucianosantos.intervaltimer.core.data

data class TimerSettings(
    val sections: Int,
    val prepareTimeSeconds: Int = 5,
    val trainTimeSeconds: Int,
    val restTimeSeconds: Int
)

object DefaultTimerSettings {
    val settings = TimerSettings(
        sections = 1,
        trainTimeSeconds = 60,
        restTimeSeconds = 60
    )
}