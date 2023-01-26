package dev.lucianosantos.intervaltimer.core.utils

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import kotlinx.coroutines.delay

class BeepHelper(val context: Context) : IBeepHelper {
    private val toneGenerator = ToneGenerator(AudioManager.STREAM_ALARM, ToneGenerator.MAX_VOLUME)

    private fun shortBeep() {
        toneGenerator.startTone(ToneGenerator.TONE_CDMA_ABBR_ALERT, 100)
    }

    private fun longBeep() {
        toneGenerator.startTone(ToneGenerator.TONE_CDMA_ABBR_ALERT, 400)
    }

    private fun vibratePhone() {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= 26) {
            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(200)
        }
    }

    override suspend fun startPrepareBeep() {
        longBeep()
    }

    override suspend fun startTrainBeep() {
        longBeep()
    }

    override suspend fun startRestBeep() {
        shortBeep()
        delay(300)
        shortBeep()
    }

    override suspend fun finishedBeep() {
        for (i in 1..3) {
            shortBeep()
            vibratePhone()
            delay(300)
        }
        longBeep()
    }

    override suspend fun timerAlmostFinishingBeep() {
        shortBeep()
    }
}