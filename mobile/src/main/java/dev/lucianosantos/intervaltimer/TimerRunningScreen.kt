package dev.lucianosantos.intervaltimer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.lucianosantos.intervaltimer.components.ActionButton
import dev.lucianosantos.intervaltimer.core.data.TimerSettings
import dev.lucianosantos.intervaltimer.core.data.TimerState
import dev.lucianosantos.intervaltimer.core.utils.AlertUserHelper
import dev.lucianosantos.intervaltimer.core.utils.CountDownTimerHelper
import dev.lucianosantos.intervaltimer.core.viewmodels.TimerViewModel

@Composable
fun TimerRunningScreen(
    timerSettings: TimerSettings,
    timerViewModel : TimerViewModel = viewModel(
        factory = TimerViewModel.Factory(
            timerSettings = timerSettings,
            countDownTimerHelper = CountDownTimerHelper(),
            beepHelper = AlertUserHelper(LocalContext.current),
        )
    ),
    onStopClicked: () -> Unit
) {
    val timerUiState by timerViewModel.timerUiState.collectAsState()

    TimerRunningComponent(
        remainingSections = timerUiState.remainingSections,
        currentTime = timerUiState.currentTime,
        timerState = timerUiState.timerState,
        isPaused = timerUiState.isPaused,
        onPlayClicked = {
            timerViewModel.resumeTimer()
        },
        onPauseClicked = {
            timerViewModel.pauseTimer()
        },
        onStopClicked = {
            timerViewModel.stopTimer()
            onStopClicked()
        }
    )
}

@Composable
fun TimerRunningComponent(
    remainingSections : Int,
    currentTime : String,
    timerState : TimerState,
    isPaused: Boolean,
    onPlayClicked : () -> Unit,
    onPauseClicked : () -> Unit,
    onStopClicked : () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = when(timerState) {
            TimerState.PREPARE -> colorResource(id = R.color.prepare_color)
            TimerState.REST -> colorResource(id = R.color.rest_color)
            TimerState.TRAIN -> colorResource(id = R.color.train_color)
            TimerState.FINISHED -> colorResource(id = R.color.finished_color)
        }
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = remainingSections.toString())
            Text(text = currentTime)
            Text(text = when(timerState) {
                TimerState.PREPARE -> stringResource(id = R.string.state_prepare_text)
                TimerState.REST -> stringResource(id = R.string.state_rest_text)
                TimerState.TRAIN -> stringResource(id = R.string.state_train_text)
                TimerState.FINISHED -> stringResource(id = R.string.state_finished_text)
            })
            Row {
                if(timerState == TimerState.FINISHED) {
                    ActionButton(
                        onClick = onStopClicked,
                        icon = R.drawable.ic_baseline_stop_24,
                        contentDescription = R.string.stop_content_description
                    )
                    ActionButton(
                        onClick = onPlayClicked,
                        icon = R.drawable.ic_baseline_refresh_24,
                        contentDescription = R.string.restart_content_description
                    )
                } else {
                    if (!isPaused) {
                        ActionButton(
                            onClick = onPauseClicked,
                            icon = R.drawable.ic_baseline_pause_24,
                            contentDescription = R.string.pause_content_description
                        )
                    } else {
                        ActionButton(
                            onClick = onStopClicked,
                            icon = R.drawable.ic_baseline_stop_24,
                            contentDescription = R.string.stop_content_description
                        )
                        ActionButton(
                            onClick = onPlayClicked,
                            icon = R.drawable.ic_baseline_play_arrow_24,
                            contentDescription = R.string.resume_content_description
                        )
                    }
                }
            }
        }
    }
}

@Composable
@Preview(widthDp = 320, heightDp = 640)
fun TimerRunningScreenPreview() {
    TimerRunningComponent(
        remainingSections = 1,
        currentTime = "12:34",
        timerState = TimerState.PREPARE,
        true,
        {},
        {},
        {}
    )
}