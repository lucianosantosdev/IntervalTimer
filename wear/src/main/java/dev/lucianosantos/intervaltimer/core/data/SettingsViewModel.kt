package dev.lucianosantos.intervaltimer.core.data

import android.text.format.DateUtils
import android.util.Log
import androidx.lifecycle.*
import dev.lucianosantos.intervaltimer.core.IBeepHelper
import dev.lucianosantos.intervaltimer.core.ICountDownTimerHelper
import kotlinx.coroutines.launch

class SettingsViewModel() : ViewModel() {

    private val _uiState: MutableLiveData<UiState> by lazy {
        MutableLiveData<UiState>(UiState(TimerSettings(1,1,1)))
    }
    val uiState get() : LiveData<UiState> = _uiState

    fun incrementSections() {
        _uiState.value?.let { currentUiState ->
            _uiState.value = currentUiState.copy(
                timerSettings = currentUiState.timerSettings.copy(
                    sets = currentUiState.timerSettings.sets + 1
                )
            )
        }
    }

    fun decrementSections() {
        _uiState.value?.let { currentUiState ->
            if(currentUiState.timerSettings.sets == 1) {
                return
            }
            _uiState.value = currentUiState.copy(
                timerSettings = currentUiState.timerSettings.copy(
                    sets = currentUiState.timerSettings.sets - 1
                )
            )
        }
    }

    fun setSections(sections: Int) {
        _uiState.value?.let { currentUiState ->
            _uiState.value = currentUiState.copy(
                timerSettings = currentUiState.timerSettings.copy(
                    sets = sections
                )
            )
        }
    }

    fun setRestTime(restTimeSeconds: Long) {
        _uiState.value?.let { currentUiState ->
            _uiState.value = currentUiState.copy(
                timerSettings = currentUiState.timerSettings.copy(
                    restTimeSeconds = restTimeSeconds
                )
            )
        }
    }

    fun setTrainTime(trainTimeSeconds: Long) {
        _uiState.value?.let { currentUiState ->
            _uiState.value = currentUiState.copy(
                timerSettings = currentUiState.timerSettings.copy(
                    trainTimeSeconds = trainTimeSeconds
                )
            )
        }
    }

    data class UiState(
        val timerSettings: TimerSettings
    )

    @Suppress("UNCHECKED_CAST")
    class Factory() : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SettingsViewModel() as T
        }
    }
}
