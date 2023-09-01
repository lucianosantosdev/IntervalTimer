package dev.lucianosantos.intervaltimer.core.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import dev.lucianosantos.intervaltimer.core.utils.CountDownTimerHelper
import dev.lucianosantos.intervaltimer.core.utils.ICountDownTimerHelper

class CountDownTimerService : Service(), ICountDownTimerHelper {

    private val binder = CountDownTimerBinder()

    private val countDownTimer = CountDownTimerHelper()

    private val countDown

    override fun onBind(intent: Intent?): IBinder? = binder

    inner class CountDownTimerBinder : Binder() {
        fun getService(): CountDownTimerService = this@CountDownTimerService
    }

    override fun startCountDown(
        seconds: Int,
        onTickCallback: (secondsUntilFinished: Long) -> Unit,
        onFinishCallback: () -> Unit
    ) {
        countDownTimer.startCountDown(seconds, onTickCallback, onFinishCallback)
    }

    override fun pause() {
        countDownTimer.pause()
    }

    override fun resume() {
        countDownTimer.resume()
    }

    override fun stop() {
        countDownTimer.stop()
    }
}