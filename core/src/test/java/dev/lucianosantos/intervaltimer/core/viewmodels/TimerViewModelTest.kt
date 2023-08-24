package dev.lucianosantos.intervaltimer.core.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.asLiveData
import dev.lucianosantos.intervaltimer.core.data.DefaultTimerSettings
import dev.lucianosantos.intervaltimer.core.data.TimerSettings
import dev.lucianosantos.intervaltimer.core.data.TimerState
import dev.lucianosantos.intervaltimer.core.utils.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import org.mockito.kotlin.*

class TimerViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val timerSettings = DefaultTimerSettings.settings

    private lateinit var mockCountDownTimerHelper: ICountDownTimerHelper

    private lateinit var mockBeepHelper: IAlertUserHelper

    private lateinit var viewModel: TimerViewModel

    @Before
    fun init() {
        mockCountDownTimerHelper = Mockito.mock(ICountDownTimerHelper::class.java)
        mockBeepHelper = Mockito.mock(IAlertUserHelper::class.java)
        viewModel = TimerViewModel(timerSettings, mockCountDownTimerHelper, mockBeepHelper)
    }

    @Test
    fun `Verify uiState is initialized with default values`() {
        // Arrange
        val timerSettings = DefaultTimerSettings.settings.copy(
            sections = 10,
            prepareTimeSeconds = 5,
        )

        // Act
        val viewModel = TimerViewModel(timerSettings, mockCountDownTimerHelper, mockBeepHelper)
        val uiState = viewModel.timerUiState.asLiveData().getOrAwaitValue()

        // Assert
        assert(uiState.remainingSections == 10)
        assert(uiState.timerState == TimerState.PREPARE)
        assert(uiState.currentTime == "")
    }

    @Test
    fun `Verify prepare countdown is started with 5 seconds when 'startTimer' is called`() = runTest {
        // Act
        viewModel.startTimer()

        // Assert
        val uiState = viewModel.timerUiState.asLiveData().getOrAwaitValue()
        verify(mockCountDownTimerHelper, times(1)).startCountDown(eq(timerSettings.prepareTimeSeconds), anyOrNull(), anyOrNull())
        verify(mockBeepHelper, times(1)).startPrepareAlert()
        assert(uiState.currentTime == "00:05")
        assert(uiState.timerState == TimerState.PREPARE)
    }

    @Test
    fun `Verify currentTime is updated when 'onTickCallback' is called`() = runTest {

        // Arrange
        doAnswer {
            val onTickCallback = it.arguments[1] as (secondsUntilFinished: Long) -> Unit

            for (seconds in 10 downTo 4) {
                onTickCallback(seconds.toLong())

                // Assert
                val uiState = viewModel.timerUiState.asLiveData().getOrAwaitValue()
                assert(uiState.currentTime == formatMinutesAndSeconds(seconds))
            }
        }.`when`(mockCountDownTimerHelper).startCountDown(eq(5), anyOrNull(), anyOrNull())

        // Act
        viewModel.startTimer()

        // Assert
        verify(mockBeepHelper, never()).timerAlmostFinishingAlert();
    }

    @Test
    fun `Verify beep is called when 'onTickCallback' is called with seconds less or equal 3`() = runTest {
        // Arrange
        doAnswer {
            val onTickCallback = it.arguments[1] as (secondsUntilFinished: Long) -> Unit

            for (seconds in 3 downTo 1) {
                onTickCallback(seconds.toLong())
            }
        }.`when`(mockCountDownTimerHelper).startCountDown(eq(5), anyOrNull(), anyOrNull())

        // Act
        viewModel.startTimer()

        // Assert
        verify(mockBeepHelper, times(3)).timerAlmostFinishingAlert()
    }

    @Test
    fun `Verify sequence of prepare, train and rest countdown timer are called and beeps are called correcty`() = runTest {
        // Arrange
        val testTimerSettings = timerSettings.copy(
            sections = 2
        )
        val viewModel = TimerViewModel(testTimerSettings, mockCountDownTimerHelper, mockBeepHelper)

        // Prepare timer
        doAnswer {
            // Prepare finished
            val onFinishedCallback = it.arguments[2] as () -> Unit
            onFinishedCallback()

        }.`when`(mockCountDownTimerHelper).startCountDown(eq(timerSettings.prepareTimeSeconds), anyOrNull(), anyOrNull())

        // Train timer
        doAnswer {
            // Train finished
            val onFinishedCallback = it.arguments[2] as () -> Unit
            onFinishedCallback()
        }.`when`(mockCountDownTimerHelper).startCountDown(eq(timerSettings.trainTimeSeconds), anyOrNull(), anyOrNull())

        // Rest timer
        doAnswer {
            // Rest finished
            val onFinishedCallback = it.arguments[2] as () -> Unit
            onFinishedCallback()

        }.`when`(mockCountDownTimerHelper).startCountDown(eq(timerSettings.restTimeSeconds), anyOrNull(), anyOrNull())

        // Act
        viewModel.startTimer()

        // Assert
        val inOrder = inOrder(mockBeepHelper)
        inOrder.verify(mockBeepHelper, times(1)).startPrepareAlert()
        inOrder.verify(mockBeepHelper, times(1)).startTrainAlert()
        inOrder.verify(mockBeepHelper, times(1)).startRestAlert()
        inOrder.verify(mockBeepHelper, times(1)).startTrainAlert()
        inOrder.verify(mockBeepHelper, times(1)).finishedAlert()
    }
}