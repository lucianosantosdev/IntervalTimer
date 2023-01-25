package dev.lucianosantos.intervaltimer.core.utils

import android.media.AudioManager
import android.media.ToneGenerator
import kotlinx.coroutines.delay

class BeepHelper : IBeepHelper {
    private val toneGenerator = ToneGenerator(AudioManager.STREAM_ALARM, ToneGenerator.MAX_VOLUME)

    override suspend fun shortBeep() {
        toneGenerator.startTone(ToneGenerator.TONE_CDMA_ABBR_ALERT, 100)
    }

    override suspend fun longBeep() {
        toneGenerator.startTone(ToneGenerator.TONE_CDMA_ABBR_ALERT, 400)
    }

    override suspend fun doubleBeep() {
        shortBeep()
        delay(300)
        shortBeep()
    }
}