package dev.lucianosantos.intervaltimer.core.utils

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.VibrationEffect
import android.os.Vibrator
import dev.lucianosantos.intervaltimer.core.data.ITimerSettingsRepository
import dev.lucianosantos.intervaltimer.core.viewmodels.SoundMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.math.sqrt

class AlertUserHelper(
    private val context: Context,
    private val settingsRepository: ITimerSettingsRepository,
    private val coroutineScope: CoroutineScope
) : IAlertUserHelper {

    // Backing fields updated from Flow
    @Volatile
    private var volume: Int = 100
    @Volatile
    private var soundMode: SoundMode = SoundMode.SOUND_AND_VIBRATE

    init {
        // Observe settings and keep latest values in memory
        settingsRepository.observeSettings()
            .onEach { settings ->
                volume = settings.volume
                soundMode = settings.soundMode
            }
            .launchIn(coroutineScope)
    }

    private fun vibrate(duration: Long) {
        if (soundMode == SoundMode.MUTE) return  // no vibration in MUTE

        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(
            VibrationEffect.createOneShot(
                duration,
                VibrationEffect.DEFAULT_AMPLITUDE
            )
        )
    }

    private suspend fun safeBeepAndVibrate(tone: Int, duration: Long) {
        when (soundMode) {
            SoundMode.MUTE -> {
                // Completely silent
                delay(duration)
                return
            }

            SoundMode.VIBRATE_ONLY -> {
                vibrate(duration)
                delay(duration)
                return
            }

            SoundMode.SOUND_AND_VIBRATE -> {
                // Fall through to play sound + vibration
            }
        }

        var toneGenerator: ToneGenerator? = null
        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_ALARM, perceptualVolume(volume))
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (toneGenerator == null) {
            // If we can't create sound, at least vibrate
            vibrate(duration)
            delay(duration)
            return
        }

        toneGenerator.startTone(tone, duration.toInt())
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
        repeat(5) {
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

    // ToneGenerator turns this 0..100 into -∞..0 dB via 20*log10(v/100), so
    // a linear slider feels almost silent in its lower half. Apply a sqrt
    // curve so the slider's perceived loudness rises more evenly: 25→50,
    // 50→71, 75→87, 100→100.
    private fun perceptualVolume(slider: Int): Int {
        val clamped = slider.coerceIn(0, 100)
        if (clamped == 0) return 0
        return (sqrt(clamped / 100.0) * ToneGenerator.MAX_VOLUME)
            .toInt()
            .coerceIn(1, ToneGenerator.MAX_VOLUME)
    }
}