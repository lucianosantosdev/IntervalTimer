package dev.lucianosantos.intervaltimer.core.data

data class TimerSettings(
    val sections: Int,
    val prepareTimeSeconds: Int,
    val trainTimeSeconds: Int,
    val restTimeSeconds: Int
)

object DefaultTimerSettings {
    val settings = TimerSettings(
        sections = 1,
        prepareTimeSeconds = 5,
        trainTimeSeconds = 60,
        restTimeSeconds = 60
    )
}