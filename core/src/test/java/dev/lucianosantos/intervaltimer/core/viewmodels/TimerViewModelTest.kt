package dev.lucianosantos.intervaltimer.core.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import dev.lucianosantos.intervaltimer.core.BeepHelper
import dev.lucianosantos.intervaltimer.core.CountDownTimerHelper
import dev.lucianosantos.intervaltimer.core.IBeepHelper
import dev.lucianosantos.intervaltimer.core.ICountDownTimerHelper
import dev.lucianosantos.intervaltimer.core.data.DefaultTimerSettings
import dev.lucianosantos.intervaltimer.core.data.TimerSettings
import dev.lucianosantos.intervaltimer.core.data.TimerState
import dev.lucianosantos.intervaltimer.core.utils.getOrAwaitValue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import java.util.Timer

class TimerViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var mockCountDownTimerHelper: ICountDownTimerHelper

    private lateinit var mockBeepHelper: IBeepHelper

    private lateinit var viewModel: TimerViewModel

    @Before
    fun init() {
        mockCountDownTimerHelper = Mockito.mock(ICountDownTimerHelper::class.java)
        mockBeepHelper = Mockito.mock(IBeepHelper::class.java)
        viewModel = TimerViewModel(DefaultTimerSettings.settings, mockCountDownTimerHelper, mockBeepHelper)
    }

    @Test
    fun `Verify uiState is initialized with default values`() {
        // Arrange
        val timerSettings = DefaultTimerSettings.settings.copy(
            sections = 10
        )

        // Act
        val viewModel = TimerViewModel(timerSettings, mockCountDownTimerHelper, mockBeepHelper)
        val uiState = viewModel.uiState.getOrAwaitValue()

        // Assert
        assert(uiState.remainingSections == 10)
        assert(uiState.timerState == TimerState.PREPARE)
        assert(uiState.currentTime == "")
    }
}