package dev.lucianosantos.intervaltimer.core.utils

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import kotlinx.coroutines.delay

class AlertUserHelper(val context: Context) : IAlertUserHelper {

    private var toneGenerator: ToneGenerator? = null

    private fun vibrate(duration: Long) {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE))
    }

    private suspend fun safeBeepAndVibrate(tone: Int, duration: Long) {
        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_ALARM, ToneGenerator.MAX_VOLUME)
        } catch (e: Exception) {
            Log.d("AlertUserHelper", "Exception while creating ToneGenerator: $e")
            e.printStackTrace()
        }

        if(toneGenerator != null) {
            toneGenerator!!.startTone(ToneGenerator.TONE_CDMA_ABBR_ALERT, 100)
            vibrate(duration)
            delay(duration)
            toneGenerator!!.release()
        }
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