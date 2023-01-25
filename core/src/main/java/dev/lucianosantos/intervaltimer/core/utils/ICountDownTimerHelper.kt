package dev.lucianosantos.intervaltimer.core.utils

interface ICountDownTimerHelper {
    fun startCountDown(seconds: Long, onTickCallback: (secondsUntilFinished: Long) -> Unit, onFinishCallback: () -> Unit)
}