package dev.lucianosantos.intervaltimer.core

interface ICountDownTimerHelper {
    fun startCountDown(seconds: Long, onTick: (secondsUntilFinished: Long) -> Unit, onFinish: () -> Unit)
}