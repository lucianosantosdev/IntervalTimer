package dev.lucianosantos.intervaltimer.core.utils

import android.os.CountDownTimer
import kotlin.math.round

class CountDownTimerHelperAlarmManager : ICountDownTimerHelper {

    private var timer : CountDownTimer? = null

    private var remainingSeconds : Int = 0

    private lateinit var _onTickCallback: (secondsUntilFinished: Long) -> Unit

    private lateinit var _onFinishCallback: () -> Unit

    override fun startCountDown(
        seconds: Int,
        onTickCallback: (secondsUntilFinished: Long) -> Unit,
        onFinishCallback: () -> Unit
    ) {
        _onTickCallback = onTickCallback
        _onFinishCallback = onFinishCallback
        timer = object: CountDownTimer(seconds * 1000L, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsUntilFinished = round(millisUntilFinished / 1000.0).toLong()
                remainingSeconds = secondsUntilFinished.toInt()
                onTickCallback(secondsUntilFinished)
            }

            override fun onFinish() {
                onFinishCallback()
            }
        }
        timer?.start()
    }

    override fun pause() {
        timer?.cancel()
    }

    override fun resume() {
        startCountDown(remainingSeconds, _onTickCallback, _onFinishCallback)
    }

    override fun stop() {
        timer?.cancel()
        timer = null
        remainingSeconds = 0
    }
}