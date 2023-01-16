package dev.lucianosantos.intervaltimer.core.data

interface ICountDownTimerHelper {
    interface CountDownTimerListener {
        fun onTick(secondsUntilFinished: Long)
        fun onFinish()
    }

    fun startCountDown(seconds: Long, callback: CountDownTimerListener)
}