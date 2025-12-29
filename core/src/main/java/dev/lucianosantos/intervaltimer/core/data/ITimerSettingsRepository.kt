package dev.lucianosantos.intervaltimer.core.data

import kotlinx.coroutines.flow.Flow

interface ITimerSettingsRepository {
    fun observeSettings(): Flow<TimerSettings>
    fun loadSettings() : TimerSettings
    fun saveSettings(timerSettings: TimerSettings)
}