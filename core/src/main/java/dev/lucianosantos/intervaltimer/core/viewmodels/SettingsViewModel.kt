package dev.lucianosantos.intervaltimer.core.viewmodels

import androidx.lifecycle.*
import dev.lucianosantos.intervaltimer.core.data.ITimerSettingsRepository
import dev.lucianosantos.intervaltimer.core.data.TimerSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class SoundMode {
    SOUND, MUTE, VIBRATE
}

class SettingsViewModel(
    private val timerSettingsRepository: ITimerSettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState(
        timerSettings = timerSettingsRepository.loadSettings(),
        volume = 0,
        soundMode = SoundMode.SOUND
    ))
    val uiState : StateFlow<SettingsUiState> = _uiState.asStateFlow()

    data class SettingsUiState(
        val timerSettings: TimerSettings,
        val volume: Int,
        val soundMode: SoundMode
    )

    fun incrementSections() {
        setSections( _uiState.value.timerSettings.sections + 1)
    }

    fun decrementSections() {
        setSections( _uiState.value.timerSettings.sections - 1)
    }

    fun setSections(sections: Int) {
        if(sections <= 0) {
            return
        }

        _uiState.value.let { currentUiState ->
            _uiState.value = currentUiState.copy(
                timerSettings = currentUiState.timerSettings.copy(
                    sections = sections
                )
            )
        }
        persistSettings()
    }


    fun setSoundMode(newMode: SoundMode) {
        _uiState.value.let { currentUiState ->
            _uiState.value = currentUiState.copy(
                soundMode = newMode
            )
        }
    }

    fun setVolume(newVolume: Float) {
        _uiState.value.let { currentUiState ->
            _uiState.value = currentUiState.copy(
                volume = newVolume.toInt()
            )
        }
    }

    fun setRestTime(restTimeSeconds: Int) {
        if(restTimeSeconds < 0) {
            return
        }

        _uiState.value.let { currentUiState ->
            _uiState.value = currentUiState.copy(
                timerSettings = currentUiState.timerSettings.copy(
                    restTimeSeconds = restTimeSeconds
                )
            )
        }
        persistSettings()
    }

    fun setTrainTime(trainTimeSeconds: Int) {
        if (trainTimeSeconds < 0) {
            return
        }

        _uiState.value.let { currentUiState ->
            _uiState.value = currentUiState.copy(
                timerSettings = currentUiState.timerSettings.copy(
                    trainTimeSeconds = trainTimeSeconds
                )
            )
        }
        persistSettings()
    }

    private fun persistSettings() {
        _uiState.value.let { currentUiState ->
            timerSettingsRepository.saveSettings(currentUiState.timerSettings)
        }
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(private val timerSettingsRepository: ITimerSettingsRepository) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            SettingsViewModel(timerSettingsRepository) as T
    }
}
