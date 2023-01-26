package dev.lucianosantos.intervaltimer.core.utils

fun formatMinutesAndSeconds(seconds: Long) : String {
    val minutes = String.format("%02d", seconds / 60)
    val seconds = String.format("%02d", seconds % 60)
    return  "$minutes:$seconds"
}