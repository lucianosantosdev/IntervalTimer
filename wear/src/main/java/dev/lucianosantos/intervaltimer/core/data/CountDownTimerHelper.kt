package dev.lucianosantos.intervaltimer.core.data

import android.os.CountDownTimer
import kotlin.math.round

class CountDownTimerHelper : ICountDownTimerHelper {
    override fun startCountDown(
        seconds: Long,
        callback: ICountDownTimerHelper.CountDownTimerListener
    ) {
        val timer = object: CountDownTimer(seconds * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsUntilFinished = round(millisUntilFinished / 1000.0).toLong()
                callback.onTick(secondsUntilFinished)
            }

            override fun onFinish() {
                callback.onFinish()
            }
        }
        timer.start()
    }
}