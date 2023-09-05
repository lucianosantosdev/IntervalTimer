package dev.lucianosantos.intervaltimer

import android.graphics.Typeface
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.lucianosantos.intervaltimer.components.ActionButton
import dev.lucianosantos.intervaltimer.core.data.TimerSettings
import dev.lucianosantos.intervaltimer.core.data.TimerState
import dev.lucianosantos.intervaltimer.core.service.CountDownTimerService
import dev.lucianosantos.intervaltimer.core.utils.formatMinutesAndSeconds
import dev.lucianosantos.intervaltimer.theme.IntervalTimerTheme

@Composable
fun TimerRunningScreen(
    timerSettings: TimerSettings,
    countDownTimerService: CountDownTimerService,
    onStopClicked: () -> Unit,
    onRestartClicked: () -> Unit
) {
    TimerRunningComponent(
        remainingSections = countDownTimerService.remainingSections,
        currentTime = countDownTimerService.currentTimeSeconds,
        timerState = countDownTimerService.timerState,
        isPaused = countDownTimerService.isPaused,
        onPlayClicked = {
            countDownTimerService.resume()
        },
        onPauseClicked = {
            countDownTimerService.pause()
        },
        onStopClicked = {
            countDownTimerService.stop()
            onStopClicked()
        },
        onRestartClicked = {
            countDownTimerService.stop()
            onRestartClicked()
        }
    )

    LaunchedEffect(Unit){
        countDownTimerService.start(timerSettings)
    }
}

@Composable
fun TimerRunningComponent(
    remainingSections : Int,
    currentTime : Int,
    timerState : TimerState,
    isPaused: Boolean,
    onPlayClicked : () -> Unit,
    onPauseClicked : () -> Unit,
    onStopClicked : () -> Unit,
    onRestartClicked : () -> Unit
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
                verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = if (timerState != TimerState.FINISHED) remainingSections.toString() else " ",
                    style = MaterialTheme.typography.headlineLarge,
                    color = colorResource(id = R.color.white)
                )
                Text(
                    text = formatMinutesAndSeconds(currentTime),
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontSize = 100.sp,
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
                    style = MaterialTheme.typography.headlineLarge,
                    color = colorResource(id = R.color.white),
                    fontWeight = FontWeight.Bold
                )
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(32.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if(timerState == TimerState.FINISHED) {
                        ActionButton(
                            onClick = onStopClicked,
                            icon = R.drawable.ic_baseline_stop_24,
                            contentDescription = R.string.stop_content_description
                        )
                        ActionButton(
                            onClick = onRestartClicked,
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
    IntervalTimerTheme {
        TimerRunningComponent(
            remainingSections = 1,
            currentTime = 1234,
            timerState = TimerState.PREPARE,
            true,
            {},
            {},
            {},
            {}
        )
    }
}