package dev.lucianosantos.intervaltimer.core.utils

import android.os.CountDownTimer
import kotlin.math.round

class CountDownTimerHelper : ICountDownTimerHelper {
    override fun startCountDown(
        seconds: Long,
        onTickCallback: (secondsUntilFinished: Long) -> Unit,
        onFinishCallback: () -> Unit
    ) {
        val timer = object: CountDownTimer(seconds * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsUntilFinished = round(millisUntilFinished / 1000.0).toLong()
                onTickCallback(secondsUntilFinished)
            }

            override fun onFinish() {
                onFinishCallback()
            }
        }
        timer.start()
    }
}