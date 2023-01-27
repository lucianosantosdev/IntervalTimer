package dev.lucianosantos.intervaltimer.core.data

interface ITimerSettingsRepository {
    fun loadSettings() : TimerSettings
    fun saveSettings(timerSettings: TimerSettings)
}