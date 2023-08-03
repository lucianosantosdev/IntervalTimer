package dev.lucianosantos.intervaltimer.core.viewmodels

import androidx.lifecycle.*
import dev.lucianosantos.intervaltimer.core.data.ITimerSettingsRepository
import dev.lucianosantos.intervaltimer.core.data.TimerSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsViewModel(private val timerSettingsRepository: ITimerSettingsRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState(timerSettingsRepository.loadSettings()))
    val uiState : StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun incrementSections() {
        setSections( _uiState.value.timerSettings.sections + 1)
    }

    fun decrementSections() {
        _uiState.value.let { currentUiState ->
            if (currentUiState.timerSettings.sections == 1) {
                return
            }
            setSections( currentUiState.timerSettings.sections - 1)
        }
    }

    fun setSections(sections: Int) {
        _uiState.value.let { currentUiState ->
            _uiState.value = currentUiState.copy(
                timerSettings = currentUiState.timerSettings.copy(
                    sections = sections
                )
            )
        }
        persistSettings()
    }

    fun incrementRestTime() {
        setRestTime( _uiState.value.timerSettings.restTimeSeconds + 1)
    }

    fun decrementRestTime() {
        _uiState.value.let { currentUiState ->
            if (currentUiState.timerSettings.restTimeSeconds == 0) {
                return
            }
            setRestTime( currentUiState.timerSettings.restTimeSeconds - 1)
        }
    }

    fun setRestTime(restTimeSeconds: Int) {
        _uiState.value.let { currentUiState ->
            _uiState.value = currentUiState.copy(
                timerSettings = currentUiState.timerSettings.copy(
                    restTimeSeconds = restTimeSeconds
                )
            )
        }
        persistSettings()
    }

    fun incrementTrainTime() {
        setTrainTime( _uiState.value.timerSettings.trainTimeSeconds + 1)
    }

    fun decrementTrainTime() {
        _uiState.value.let { currentUiState ->
            if (currentUiState.timerSettings.trainTimeSeconds == 1) {
                return
            }
            setTrainTime(currentUiState.timerSettings.trainTimeSeconds - 1)
        }
    }

    fun setTrainTime(trainTimeSeconds: Int) {
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
