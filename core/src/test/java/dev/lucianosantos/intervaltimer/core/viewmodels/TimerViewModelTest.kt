package dev.lucianosantos.intervaltimer.core.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import dev.lucianosantos.intervaltimer.core.BeepHelper
import dev.lucianosantos.intervaltimer.core.CountDownTimerHelper
import dev.lucianosantos.intervaltimer.core.IBeepHelper
import dev.lucianosantos.intervaltimer.core.ICountDownTimerHelper
import dev.lucianosantos.intervaltimer.core.data.DefaultTimerSettings
import dev.lucianosantos.intervaltimer.core.data.TimerSettings
import dev.lucianosantos.intervaltimer.core.data.TimerState
import dev.lucianosantos.intervaltimer.core.utils.MainDispatcherRule
import dev.lucianosantos.intervaltimer.core.utils.getOrAwaitValue
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import org.mockito.kotlin.*
import java.util.Timer

class TimerViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

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

    @Test
    fun `Verify prepare countdown is started with 5 seconds when 'startTimer' is called`() = runTest {
        // Arrange
        val timerSettings = DefaultTimerSettings.settings.copy(
            sections = 2,
            trainTimeSeconds = 10,
            restTimeSeconds = 20,
        )

        // CountDownTimerHelper mock
        doAnswer { }.`when`(mockCountDownTimerHelper).startCountDown(eq(5L), anyOrNull(), anyOrNull())

        // BeepHelper mock
        mockBeepHelper.stub {
            onBlocking { longBeep() }.doReturn(Unit)
        }

        // Act
        val viewModel = TimerViewModel(timerSettings, mockCountDownTimerHelper, mockBeepHelper)
        viewModel.startTimer()

        val uiState = viewModel.uiState.getOrAwaitValue()
        assert(uiState.currentTime == "00:05")
        assert(uiState.timerState == TimerState.PREPARE)
    }
//
//    @Test
//    fun `Verify prepare countdowned`() = runTest {
//        // Arrange
//        val timerSettings = DefaultTimerSettings.settings.copy(
//            sections = 2,
//            trainTimeSeconds = 10,
//            restTimeSeconds = 20,
//        )
//        // Start prepare countdown
//
//        val viewModel = TimerViewModel(timerSettings, mockCountDownTimerHelper, mockBeepHelper)
//
//        doAnswer {
//            val onTickCallback = it.arguments[1] as (secondsUntilFinished: Long) -> Unit
//            val onFinishCallback = it.arguments[2] as () -> Unit
//
//            // Assert
//            val uiState = viewModel.uiState.getOrAwaitValue()
//            assert(uiState.timerState == TimerState.PREPARE)
//            onFinishCallback()
//
//            assert(uiState.currentTime == "00:00")
//            assert(uiState.timerState == TimerState.FINISHED)
//        }.`when`(mockCountDownTimerHelper).startCountDown(eq(5L), anyOrNull(), anyOrNull())
//
//        mockBeepHelper.stub {
//            onBlocking { longBeep() }.doReturn(Unit)
//        }
//
//        // Act
//        viewModel.startTimer()
//    }
}