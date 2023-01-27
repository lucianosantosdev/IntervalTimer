package dev.lucianosantos.intervaltimer.core.viewmodels

import androidx.lifecycle.*
import dev.lucianosantos.intervaltimer.core.data.TimerSettings
import dev.lucianosantos.intervaltimer.core.data.TimerSettingsRepository

class SettingsViewModel(private val timerSettingsRepository: TimerSettingsRepository) : ViewModel() {

    private val _uiState: MutableLiveData<UiState> by lazy {
        MutableLiveData<UiState>(UiState(timerSettingsRepository.loadSettings()))
    }
    val uiState get() : LiveData<UiState> = _uiState

    fun incrementSections() {
        _uiState.value?.let { currentUiState ->
            _uiState.value = currentUiState.copy(
                timerSettings = currentUiState.timerSettings.copy(
                    sections = currentUiState.timerSettings.sections + 1
                )
            )
        }
        persistSettings()
    }

    fun decrementSections() {
        _uiState.value?.let { currentUiState ->
            if (currentUiState.timerSettings.sections == 1) {
                return
            }
            _uiState.value = currentUiState.copy(
                timerSettings = currentUiState.timerSettings.copy(
                    sections = currentUiState.timerSettings.sections - 1
                )
            )
        }
        persistSettings()
    }

    fun setSections(sections: Int) {
        _uiState.value?.let { currentUiState ->
            _uiState.value = currentUiState.copy(
                timerSettings = currentUiState.timerSettings.copy(
                    sections = sections
                )
            )
        }
        persistSettings()
    }

    fun setRestTime(restTimeSeconds: Int) {
        _uiState.value?.let { currentUiState ->
            _uiState.value = currentUiState.copy(
                timerSettings = currentUiState.timerSettings.copy(
                    restTimeSeconds = restTimeSeconds
                )
            )
        }
        persistSettings()
    }

    fun setTrainTime(trainTimeSeconds: Int) {
        _uiState.value?.let { currentUiState ->
            _uiState.value = currentUiState.copy(
                timerSettings = currentUiState.timerSettings.copy(
                    trainTimeSeconds = trainTimeSeconds
                )
            )
        }
        persistSettings()
    }

    fun persistSettings() {
        _uiState.value?.let { currentUiState ->
            timerSettingsRepository.saveSettings(currentUiState.timerSettings)
        }
    }

    data class UiState(
        val timerSettings: TimerSettings
    )

    @Suppress("UNCHECKED_CAST")
    class Factory(private val timerSettingsRepository: TimerSettingsRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SettingsViewModel(timerSettingsRepository) as T
        }
    }
}
