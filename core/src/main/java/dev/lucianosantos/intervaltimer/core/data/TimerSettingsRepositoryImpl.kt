package dev.lucianosantos.intervaltimer.core.data

import android.content.Context
import android.content.SharedPreferences
import dev.lucianosantos.intervaltimer.core.viewmodels.SoundMode
import androidx.core.content.edit
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class TimerSettingsRepositoryImpl(
    private val context: Context
) : ITimerSettingsRepository {

    private val sharedPreference by lazy {
        context.getSharedPreferences("TimerSettings", Context.MODE_PRIVATE)
    }

    override fun observeSettings(): Flow<TimerSettings> = callbackFlow {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, _ ->
            trySend(loadSettings())
        }

        sharedPreference.registerOnSharedPreferenceChangeListener(listener)

        // Emit initial value
        trySend(loadSettings())

        awaitClose {
            sharedPreference.unregisterOnSharedPreferenceChangeListener(listener)
        }
    }

    override fun loadSettings() : TimerSettings{
        return TimerSettings(
            sections = sharedPreference.getInt("sections", DefaultTimerSettings.settings.sections),
            prepareTimeSeconds = sharedPreference.getInt("prepareTimeSeconds", DefaultTimerSettings.settings.prepareTimeSeconds),
            trainTimeSeconds = sharedPreference.getInt("trainTimeSeconds", DefaultTimerSettings.settings.trainTimeSeconds),
            restTimeSeconds = sharedPreference.getInt("restTimeSeconds", DefaultTimerSettings.settings.restTimeSeconds),
            volume = sharedPreference.getInt("volume", DefaultTimerSettings.settings.volume),
            soundMode = sharedPreference.getInt("soundMode", DefaultTimerSettings.settings.soundMode.ordinal).let {
                SoundMode.entries[it]
            }
        )
    }
    override fun saveSettings(timerSettings: TimerSettings) {
        sharedPreference.edit {
            putInt("sections", timerSettings.sections)
            putInt("prepareTimeSeconds", timerSettings.prepareTimeSeconds)
            putInt("trainTimeSeconds", timerSettings.trainTimeSeconds)
            putInt("restTimeSeconds", timerSettings.restTimeSeconds)
            putInt("volume", timerSettings.volume)
            putInt("soundMode", timerSettings.soundMode.ordinal)
        }
    }
}