package dev.lucianosantos.intervaltimer.core.utils

fun formatMinutesAndSeconds(seconds: Long) : String {
    val minutesFormated = String.format("%02d", seconds / 60)
    val secondsFormated = String.format("%02d", seconds % 60)
    return  "$minutesFormated:$secondsFormated"
}