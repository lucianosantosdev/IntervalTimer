package dev.lucianosantos.intervaltimer.core.utils

import android.content.Context
import android.media.AudioManager
import android.media.RingtoneManager
import android.media.ToneGenerator
import android.os.VibrationEffect
import android.os.Vibrator
import kotlinx.coroutines.delay


class AlertUserHelper(val context: Context) : IAlertUserHelper {

    private fun vibrate(duration: Long) {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE))
    }

    private suspend fun safeBeepAndVibrate(tone: Int, duration: Long) {
        var toneGenerator: ToneGenerator? = null
        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_ALARM, ToneGenerator.MAX_VOLUME)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if(toneGenerator == null) {
            return
        }
        toneGenerator.startTone(ToneGenerator.TONE_CDMA_ABBR_ALERT, 100)
        vibrate(duration)
        delay(duration)
        toneGenerator.stopTone()
        toneGenerator.release()
    }

    private suspend fun shortBeep() {
        safeBeepAndVibrate(ToneGenerator.TONE_CDMA_ABBR_ALERT, 100)
    }

    private suspend fun longBeep() {
        safeBeepAndVibrate(ToneGenerator.TONE_CDMA_ABBR_ALERT, 400)
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
        for (i in 1..5) {
            shortBeep()
            delay(100)
        }
        longBeep()
        delay(200)
        longBeep()
    }

    override suspend fun timerAlmostFinishingAlert() {
        shortBeep()
    }
}