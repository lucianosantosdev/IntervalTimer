package dev.lucianosantos.intervaltimer.core

interface ICountDownTimerHelper {
    fun startCountDown(seconds: Long, onTickCallback: (secondsUntilFinished: Long) -> Unit, onFinishCallback: () -> Unit)
}