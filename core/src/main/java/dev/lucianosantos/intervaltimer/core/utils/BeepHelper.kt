package dev.lucianosantos.intervaltimer.core.utils

import android.media.AudioManager
import android.media.ToneGenerator
import kotlinx.coroutines.delay

class BeepHelper : IBeepHelper {
    private val toneGenerator = ToneGenerator(AudioManager.STREAM_ALARM, ToneGenerator.MAX_VOLUME)

    private suspend fun shortBeep() {
        toneGenerator.startTone(ToneGenerator.TONE_CDMA_ABBR_ALERT, 100)
    }

    private suspend fun longBeep() {
        toneGenerator.startTone(ToneGenerator.TONE_CDMA_ABBR_ALERT, 400)
    }

    private suspend fun doubleBeep() {
        shortBeep()
        delay(300)
        shortBeep()
    }

    override suspend fun startPrepareBeep() {
        longBeep()
    }

    override suspend fun startTrainBeep() {
        longBeep()
    }

    override suspend fun startRestBeep() {
        doubleBeep()
    }

    override suspend fun finishedBeep() {
        longBeep()
    }

    override suspend fun timerAlmostFinishingBeep() {
        shortBeep()
    }
}