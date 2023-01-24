package dev.lucianosantos.intervaltimer.core.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import dev.lucianosantos.intervaltimer.core.data.DefaultTimerSettings
import org.junit.Rule
import org.junit.Test

class SettingsViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Test
    fun `Verify uiStati is initialized with default values`() {
        val viewModel = SettingsViewModel()
        assert(viewModel.uiState.value?.timerSettings == DefaultTimerSettings.settings)
    }
}