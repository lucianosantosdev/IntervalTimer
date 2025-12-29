package dev.lucianosantos.intervaltimer.core.data

import dev.lucianosantos.intervaltimer.core.viewmodels.SoundMode

data class TimerSettings(
    val sections: Int,
    val prepareTimeSeconds: Int,
    val trainTimeSeconds: Int,
    val restTimeSeconds: Int,
    val volume: Int,
    val soundMode: SoundMode
)

object DefaultTimerSettings {
    val settings = TimerSettings(
        sections = 1,
        prepareTimeSeconds = 5,
        trainTimeSeconds = 60,
        restTimeSeconds = 60,
        volume = 100,
        soundMode = SoundMode.SOUND_AND_VIBRATE
    )
}