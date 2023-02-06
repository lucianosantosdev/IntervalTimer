package dev.lucianosantos.intervaltimer.core.viewmodels

import androidx.lifecycle.*
import dev.lucianosantos.intervaltimer.core.data.ITimerSettingsRepository
import dev.lucianosantos.intervaltimer.core.data.TimerSettings

class SettingsViewModel(private val timerSettingsRepository: ITimerSettingsRepository) : ViewModel() {

    private val _uiState: MutableLiveData<UiState> by lazy {
        MutableLiveData<UiState>(UiState(timerSettingsRepository.loadSettings()))
    }
    val uiState get() : LiveData<UiState> = _uiState

    fun incrementSections() {
        _uiState.value?.let { currentUiState ->
            setSections( currentUiState.timerSettings.sections + 1)
        }
    }

    fun decrementSections() {
        _uiState.value?.let { currentUiState ->
            if (currentUiState.timerSettings.sections == 1) {
                return
            }
            setSections( currentUiState.timerSettings.sections - 1)
        }
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

    fun incrementRestTime() {
        _uiState.value?.let { currentUiState ->
            setRestTime( currentUiState.timerSettings.restTimeSeconds + 1)
        }
    }

    fun decrementRestTime() {
        _uiState.value?.let { currentUiState ->
            if (currentUiState.timerSettings.restTimeSeconds == 0) {
                return
            }
            setRestTime( currentUiState.timerSettings.restTimeSeconds - 1)
        }
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

    fun incrementTrainTime() {
        _uiState.value?.let { currentUiState ->
            setTrainTime( currentUiState.timerSettings.trainTimeSeconds + 1)
        }
    }

    fun decrementTrainTime() {
        _uiState.value?.let { currentUiState ->
            if (currentUiState.timerSettings.trainTimeSeconds == 1) {
                return
            }
            setTrainTime(currentUiState.timerSettings.trainTimeSeconds - 1)
        }
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

    private fun persistSettings() {
        _uiState.value?.let { currentUiState ->
            timerSettingsRepository.saveSettings(currentUiState.timerSettings)
        }
    }

    data class UiState(
        val timerSettings: TimerSettings
    )

    @Suppress("UNCHECKED_CAST")
    class Factory(private val timerSettingsRepository: ITimerSettingsRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SettingsViewModel(timerSettingsRepository) as T
        }
    }
}
