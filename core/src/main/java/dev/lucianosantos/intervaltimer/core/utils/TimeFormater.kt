package dev.lucianosantos.intervaltimer.core.utils

fun getMinutesFromSeconds(seconds: Int) = seconds / 60

fun getSecondsFromSeconds(seconds: Int) = seconds % 60

fun getSecondsFromMinutesAndSeconds(minutes: Int, seconds: Int) : Int = minutes * 60 + seconds

fun formatMinutesAndSeconds(seconds: Int) : String {
    val minutesFormated = String.format("%02d", getMinutesFromSeconds(seconds))
    val secondsFormated = String.format("%02d", getSecondsFromSeconds(seconds))
    return  "$minutesFormated:$secondsFormated"
}
