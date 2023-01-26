package dev.lucianosantos.intervaltimer.core.utils

interface ICountDownTimerHelper {
    fun startCountDown(seconds: Int, onTickCallback: (secondsUntilFinished: Long) -> Unit, onFinishCallback: () -> Unit)

    fun pause()

    fun resume()
}