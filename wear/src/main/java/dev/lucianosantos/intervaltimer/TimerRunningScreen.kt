    package dev.lucianosantos.intervaltimer

import WearAppTheme
import android.graphics.Typeface
import android.text.format.DateFormat
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.TimeTextDefaults
import androidx.wear.compose.ui.tooling.preview.WearPreviewLargeRound
import dev.lucianosantos.intervaltimer.core.data.TimerSettings
import dev.lucianosantos.intervaltimer.core.data.TimerState
import dev.lucianosantos.intervaltimer.core.service.CountDownTimerService
import dev.lucianosantos.intervaltimer.core.utils.AlertUserHelper
import dev.lucianosantos.intervaltimer.core.utils.ICountDownTimerHelper
import dev.lucianosantos.intervaltimer.core.utils.formatMinutesAndSeconds
import java.util.Locale

@Composable
fun TimerRunningScreen(
    countDownTimerService: CountDownTimerServiceWear,
    onRefreshClicked: () -> Unit
) {
    val remainingSections by countDownTimerService.remainingSections.collectAsState()
    val currentTime by countDownTimerService.currentTimeSeconds.collectAsState()
    val timerState by countDownTimerService.timerState.collectAsState()
    val isPaused by countDownTimerService.isPaused.collectAsState()
    TimerRunningComponent(
        remainingSections = remainingSections,
        currentTimeSeconds = currentTime,
        timerState = timerState,
        isPaused = isPaused,
        onPlayClicked = {
            countDownTimerService.resume()
        },
        onPauseClicked = {
            countDownTimerService.pause()
        },
        onRefreshClicked = {
            countDownTimerService.stop()
            onRefreshClicked()
        }
    )

    LaunchedEffect(Unit){
        if (countDownTimerService.timerState.value == TimerState.STOPED) {
            countDownTimerService.start()
        }
    }
}

@Composable
fun TimerRunningComponent(
    remainingSections : Int,
    currentTimeSeconds : Int,
    timerState : TimerState,
    isPaused: Boolean,
    onPlayClicked : () -> Unit,
    onPauseClicked : () -> Unit,
    onRefreshClicked: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = when (timerState) {
                    TimerState.STOPED -> colorResource(id = R.color.prepare_color)
                    TimerState.PREPARE -> colorResource(id = R.color.prepare_color)
                    TimerState.REST -> colorResource(id = R.color.rest_color)
                    TimerState.TRAIN -> colorResource(id = R.color.train_color)
                    TimerState.FINISHED -> colorResource(id = R.color.finished_color)
                }
            ),
    ) {
        TimeText(
            timeSource = TimeTextDefaults.timeSource(
                DateFormat.getBestDateTimePattern(Locale.getDefault(), "hh:mm")
            )
        )
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
                text = if (timerState != TimerState.FINISHED) {
                    formatMinutesAndSeconds(currentTimeSeconds)
                } else {
                    stringResource(id = R.string.state_finished_text)
                },
                style = MaterialTheme.typography.title3.copy(
                    fontSize = 40.sp,
                    fontFamily = FontFamily(Typeface.MONOSPACE)

                ),
                color = colorResource(id = R.color.white)
            )
            if(timerState != TimerState.FINISHED) {
                Text(
                    text = when(timerState) {
                        TimerState.STOPED -> stringResource(id = R.string.state_prepare_text)
                        TimerState.PREPARE -> stringResource(id = R.string.state_prepare_text)
                        TimerState.REST -> stringResource(id = R.string.state_rest_text)
                        TimerState.TRAIN -> stringResource(id = R.string.state_train_text)
                        else -> ""
                    },
                    style = MaterialTheme.typography.title3,
                    color = colorResource(id = R.color.white),
                    fontWeight = FontWeight.Bold
                )
            }
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
                        onClick = onRefreshClicked
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Refresh,
                            contentDescription = stringResource(id = R.string.refresh_icon_content_description),
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
                                contentDescription = stringResource(id = R.string.pause_icon_content_description),
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
                                contentDescription = stringResource(id = R.string.play_icon_content_description),
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
            currentTimeSeconds = 1234,
            timerState = TimerState.PREPARE,
            true,
            {},
            {},
            {}
        )
    }
}