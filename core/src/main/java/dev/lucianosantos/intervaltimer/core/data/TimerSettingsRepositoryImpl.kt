package dev.lucianosantos.intervaltimer.core.data

import android.content.Context

class TimerSettingsRepositoryImpl(private val context: Context) : ITimerSettingsRepository {
    override fun loadSettings() : TimerSettings{
        val sharedPreference = context.getSharedPreferences("TimerSettings", Context.MODE_PRIVATE)

        return TimerSettings(
            sections = sharedPreference.getInt("sections", DefaultTimerSettings.settings.sections),
            prepareTimeSeconds = sharedPreference.getInt("prepareTimeSeconds", DefaultTimerSettings.settings.prepareTimeSeconds),
            trainTimeSeconds = sharedPreference.getInt("trainTimeSeconds", DefaultTimerSettings.settings.trainTimeSeconds),
            restTimeSeconds = sharedPreference.getInt("restTimeSeconds", DefaultTimerSettings.settings.restTimeSeconds),
        )
    }
    override fun saveSettings(timerSettings: TimerSettings) {
        val sharedPreference = context.getSharedPreferences("TimerSettings", Context.MODE_PRIVATE)
        var editor = sharedPreference.edit()
        editor.putInt("sections",timerSettings.sections)
        editor.putInt("prepareTimeSeconds",timerSettings.prepareTimeSeconds)
        editor.putInt("trainTimeSeconds",timerSettings.trainTimeSeconds)
        editor.putInt("restTimeSeconds",timerSettings.restTimeSeconds)
        editor.commit()
    }
}