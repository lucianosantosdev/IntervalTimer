package dev.lucianosantos.intervaltimer.core.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import dev.lucianosantos.intervaltimer.core.data.DefaultTimerSettings
import dev.lucianosantos.intervaltimer.core.data.TimerSettings
import dev.lucianosantos.intervaltimer.core.utils.getOrAwaitValue
import org.junit.Rule
import org.junit.Test

class SettingsViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Test
    fun `Verify uiState is initialized with settings provided in constructor`() {
        // Arrange
        val settings = TimerSettings(1,2,3, 4)

        // Act
        val viewModel = SettingsViewModel(settings)

        // Assert
        assert(viewModel.uiState.value?.timerSettings == settings)
    }

    @Test
    fun `Verify uiState is updated when 'sections' is incremented`() {
        // Arrange
        val viewModel = SettingsViewModel(DefaultTimerSettings.settings.copy(
            sections = 1
        ))

        // Act
        viewModel.incrementSections()
        val uiState = viewModel.uiState.getOrAwaitValue()

        // Assert
        assert(uiState.timerSettings.sections == 2)
    }

    @Test
    fun `Verify uiState is not updated when 'sections' is decremented when equals to '1'`() {
        // Arrange
        val viewModel = SettingsViewModel(DefaultTimerSettings.settings.copy(
            sections = 1
        ))

        // Act
        viewModel.decrementSections()
        val uiState = viewModel.uiState.getOrAwaitValue()

        // Assert
        assert(uiState.timerSettings.sections == 1)
    }

    @Test
    fun `Verify uiState is updated when 'sections' is decremented`() {
        // Arrange
        val viewModel = SettingsViewModel(DefaultTimerSettings.settings.copy(
            sections = 2
        ))

        // Act
        viewModel.decrementSections()
        val uiState = viewModel.uiState.getOrAwaitValue()

        // Assert
        assert(uiState.timerSettings.sections == 1)
    }

    @Test
    fun `Verify uiState is updated when 'sections' is set using 'setSections'`() {
        // Arrange
        val viewModel = SettingsViewModel(DefaultTimerSettings.settings)

        // Act
        viewModel.setSections(10)
        val uiState = viewModel.uiState.getOrAwaitValue()

        // Assert
        assert(uiState.timerSettings == DefaultTimerSettings.settings.copy(
            sections = 10
        ))
    }

    @Test
    fun `Verify uiState is updated when 'trainTimeSeconds' is set using 'setTrainTime'`() {
        // Arrange
        val viewModel = SettingsViewModel(DefaultTimerSettings.settings)

        // Act
        viewModel.setTrainTime(42)
        val uiState = viewModel.uiState.getOrAwaitValue()

        // Assert
        assert(uiState.timerSettings == DefaultTimerSettings.settings.copy(
            trainTimeSeconds = 42L
        ))
    }

    @Test
    fun `Verify uiState is updated when 'restTimeSeconds' is set using 'setRestTime'`() {
        // Arrange
        val viewModel = SettingsViewModel(DefaultTimerSettings.settings)

        // Act
        viewModel.setRestTime(42)
        val uiState = viewModel.uiState.getOrAwaitValue()

        // Assert
        assert(uiState.timerSettings == DefaultTimerSettings.settings.copy(
            restTimeSeconds = 42L
        ))
    }
}