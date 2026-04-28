package dev.lucianosantos.intervaltimer

import android.graphics.Typeface
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.lucianosantos.intervaltimer.components.ActionButton
import dev.lucianosantos.intervaltimer.core.data.TimerState
import dev.lucianosantos.intervaltimer.core.service.ICountDownTimerService
import dev.lucianosantos.intervaltimer.core.utils.formatMinutesAndSeconds
import dev.lucianosantos.intervaltimer.core.viewmodels.SettingsViewModel
import dev.lucianosantos.intervaltimer.core.viewmodels.SoundMode
import dev.lucianosantos.intervaltimer.theme.IntervalTimerTheme
import kotlinx.coroutines.delay
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TimerRunningScreen(
    countDownTimerService: ICountDownTimerService,
    onStop: () -> Unit,
    onAdvancedSettingsClicked: () -> Unit = {}
) {
    if (countDownTimerService.remainingSections == null) {
        return
    }
    val settingsViewModel: SettingsViewModel = koinViewModel()
    val settingsUiState by settingsViewModel.uiState.collectAsState()

    val remainingSections by countDownTimerService.remainingSections!!.collectAsState()
    val currentTime by countDownTimerService.currentTimeSeconds!!.collectAsState()
    val timerState by countDownTimerService.timerState!!.collectAsState()
    val isPaused by countDownTimerService.isPaused!!.collectAsState()

    TimerRunningComponent(
        remainingSections = remainingSections,
        currentTime = currentTime,
        timerState = timerState,
        isPaused = isPaused,
        onPlayClicked = {
            countDownTimerService.resume()
        },
        onPauseClicked = {
            countDownTimerService.pause()
        },
        onStopClicked = {
            countDownTimerService.stop()
        },
        onRestartClicked = {
            countDownTimerService.restart()
        },
        soundMode = settingsUiState.timerSettings.soundMode,
        onSoundModeChange = { settingsViewModel.setSoundMode(it) },
        volume = settingsUiState.timerSettings.volume,
        onVolumeChange = { settingsViewModel.setVolume(it) },
        onAdvancedSettingsClicked = onAdvancedSettingsClicked
    )

    LaunchedEffect(Unit){
        if (countDownTimerService.timerState!!.value == TimerState.NONE) {
            countDownTimerService.start()
        }
    }

    if (timerState == TimerState.STOPPED) {
        onStop()
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
    onRestartClicked : () -> Unit,
    soundMode: SoundMode,
    onSoundModeChange: (SoundMode) -> Unit,
    volume: Int,
    onVolumeChange: (Int) -> Unit,
    onAdvancedSettingsClicked: () -> Unit = {}
) {
    if (timerState == TimerState.NONE || timerState == TimerState.STOPPED) {
        return
    }
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = when(timerState) {
            TimerState.NONE,
            TimerState.STOPPED -> Color.Transparent
            TimerState.PREPARE -> colorResource(id = R.color.prepare_color)
            TimerState.REST -> colorResource(id = R.color.rest_color)
            TimerState.TRAIN -> colorResource(id = R.color.train_color)
            TimerState.FINISHED -> colorResource(id = R.color.finished_color)
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AutoHideContent(
                modifier = Modifier.fillMaxSize(),
                hideDelayMillis = 5_000L,
                alignment = Alignment.TopCenter
            ) { onActivity ->
                VolumeControl(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(start = 16.dp, end = 16.dp, top = 32.dp, bottom = 16.dp),
                    volume = volume,
                    onVolumeChange = {
                        onActivity()
                        onVolumeChange(it)
                    },
                    soundMode = soundMode,
                    onSoundModeChange = {
                        onActivity()
                        onSoundModeChange(it)
                    }
                )
            }
            Column(
                modifier = Modifier.align(Alignment.Center),
                verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = if (timerState != TimerState.FINISHED) remainingSections.toString() else " ",
                    style = MaterialTheme.typography.headlineLarge,
                    color = colorResource(id = R.color.white)
                )
                Text(
                    text = if (timerState != TimerState.FINISHED) {
                        formatMinutesAndSeconds(currentTime)
                    } else {
                        stringResource(id = R.string.state_finished_text)
                    },
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontSize = 100.sp,
                        fontFamily = FontFamily(Typeface.MONOSPACE)

                    ),
                    color = colorResource(id = R.color.white)
                )
                Text(
                    text = when (timerState) {
                        TimerState.PREPARE -> stringResource(id = R.string.state_prepare_text)
                        TimerState.REST -> stringResource(id = R.string.state_rest_text)
                        TimerState.TRAIN -> stringResource(id = R.string.state_train_text)
                        else -> ""
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
                    if (timerState == TimerState.FINISHED) {
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
}

@Composable
fun AutoHideContent(
    modifier: Modifier = Modifier,
    hideDelayMillis: Long = 3_000L,
    alignment: Alignment = Alignment.TopCenter,
    enter: EnterTransition = fadeIn(),
    exit: ExitTransition = fadeOut(),
    onActivityChange: ((Boolean) -> Unit)? = null,
    content: @Composable (onActivity: () -> Unit) -> Unit
) {
    var isVisible by remember { mutableStateOf(true) }
    var activityTrigger by remember { mutableStateOf(0) }

    // Auto-hide timer
    LaunchedEffect(activityTrigger) {
        isVisible = true
        onActivityChange?.invoke(true)
        delay(hideDelayMillis)
        isVisible = false
        onActivityChange?.invoke(false)
    }

    val onActivity: () -> Unit = {
        activityTrigger++
    }

    Box(
        modifier = modifier.pointerInput(Unit) {
            detectTapGestures(onTap = { onActivity() })
        }
    ) {
        AnimatedVisibility(
            visible = isVisible,
            enter = enter,
            exit = exit,
            modifier = Modifier.align(alignment)
        ) {
            content(onActivity)
        }
    }
}

@Composable
@Preview
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
            {},
            volume = 50,
            onVolumeChange = {},
            soundMode = SoundMode.SOUND_AND_VIBRATE,
            onSoundModeChange = {}
        )
    }
}