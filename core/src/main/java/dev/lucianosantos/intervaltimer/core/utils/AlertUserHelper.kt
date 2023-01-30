package dev.lucianosantos.intervaltimer.core.utils

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import kotlinx.coroutines.delay

class AlertUserHelper(val context: Context) : IAlertUserHelper {
    private val toneGenerator = ToneGenerator(AudioManager.STREAM_ALARM, ToneGenerator.MAX_VOLUME)

    private fun vibrate(duration: Long) {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= 26) {
            vibrator.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(duration)
        }
    }

    private fun shortBeep() {
        toneGenerator.startTone(ToneGenerator.TONE_CDMA_ABBR_ALERT, 100)
        vibrate(100)
    }

    private fun longBeep() {
        toneGenerator.startTone(ToneGenerator.TONE_CDMA_ABBR_ALERT, 400)
        vibrate(400)
    }

    override suspend fun startPrepareAlert() {
        longBeep()
    }

    override suspend fun startTrainAlert() {
        longBeep()
    }

    override suspend fun startRestAlert() {
        shortBeep()
        delay(300)
        shortBeep()
    }

    override suspend fun finishedAlert() {
        for (i in 1..3) {
            shortBeep()
            delay(300)
            longBeep()
            delay(800)
        }
    }

    override suspend fun timerAlmostFinishingAlert() {
        shortBeep()
    }
}