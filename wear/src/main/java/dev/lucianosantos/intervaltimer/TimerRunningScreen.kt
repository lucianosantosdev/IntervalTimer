package dev.lucianosantos.intervaltimer

import WearAppTheme
import android.graphics.Typeface
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.ui.tooling.preview.WearPreviewLargeRound
import dev.lucianosantos.intervaltimer.core.data.TimerSettings
import dev.lucianosantos.intervaltimer.core.data.TimerState
import dev.lucianosantos.intervaltimer.core.utils.AlertUserHelper
import dev.lucianosantos.intervaltimer.core.utils.CountDownTimerHelper
import dev.lucianosantos.intervaltimer.core.viewmodels.TimerViewModel
@Composable
fun TimerRunningScreen(
    timerSettings: TimerSettings,
    onStopClicked: () -> Unit
) {
    val timerViewModel : TimerViewModel = viewModel(
        factory = TimerViewModel.Factory(
            timerSettings = timerSettings,
            countDownTimerHelper = CountDownTimerHelper(),
            beepHelper = AlertUserHelper(LocalContext.current),
        )
    )
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
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = when (timerState) {
                    TimerState.PREPARE -> colorResource(id = R.color.prepare_color)
                    TimerState.REST -> colorResource(id = R.color.rest_color)
                    TimerState.TRAIN -> colorResource(id = R.color.train_color)
                    TimerState.FINISHED -> colorResource(id = R.color.finished_color)
                }
            ),
    ) {
        TimeText()
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(
                    Modifier
                        .fillMaxWidth()
                        .weight(.5F)
                )
                Text(
                    text = if (timerState != TimerState.FINISHED) remainingSections.toString() else " ",
                    style = MaterialTheme.typography.title3,
                    color = colorResource(id = R.color.white)
                )
                Text(
                    text = currentTime,
                    style = MaterialTheme.typography.title3.copy(
                        fontSize = 40.sp,
                        fontFamily = FontFamily(Typeface.MONOSPACE)

                    ),
                    color = colorResource(id = R.color.white)
                )
                Text(
                    text = when(timerState) {
                        TimerState.PREPARE -> stringResource(id = R.string.state_prepare_text)
                        TimerState.REST -> stringResource(id = R.string.state_rest_text)
                        TimerState.TRAIN -> stringResource(id = R.string.state_train_text)
                        TimerState.FINISHED -> stringResource(id = R.string.state_finished_text)
                    },
                    style = MaterialTheme.typography.title3,
                    color = colorResource(id = R.color.white),
                    fontWeight = FontWeight.Bold
                )
                Spacer(
                    Modifier
                        .fillMaxWidth()
                        .weight(.2F)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if(timerState == TimerState.FINISHED) {
                        Button(
                            onClick = onStopClicked
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Stop,
                                contentDescription = "",
                                modifier = Modifier
                                    .size(24.dp)
                                    .wrapContentSize(align = Alignment.Center)
                            )
                        }
                    } else {
                        if (!isPaused) {
                            Button(
                                onClick = onPauseClicked
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Pause,
                                    contentDescription = "",
                                    modifier = Modifier
                                        .size(24.dp)
                                        .wrapContentSize(align = Alignment.Center)
                                )
                            }
                        } else {
                            Button(
                                onClick = onPlayClicked
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.PlayArrow,
                                    contentDescription = "",
                                    modifier = Modifier
                                        .size(24.dp)
                                        .wrapContentSize(align = Alignment.Center)
                                )
                            }
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
            }
    }
}

@Composable
@WearPreviewLargeRound
fun TimerRunningScreenPreview() {
    WearAppTheme {
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
}