package dev.lucianosantos.intervaltimer.screenshots

import dev.lucianosantos.intervaltimer.SettingsScreenContent
import dev.lucianosantos.intervaltimer.TimerRunningComponent
import dev.lucianosantos.intervaltimer.core.data.TimerState
import dev.lucianosantos.intervaltimer.core.viewmodels.SoundMode
import dev.lucianosantos.intervaltimer.theme.IntervalTimerTheme
import dev.lucianosantos.storescreenshots.FormFactor
import dev.lucianosantos.storescreenshots.Screenshot
import dev.lucianosantos.storescreenshots.ScreenshotRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], application = StubApplication::class)
class PlayStoreScreenshotsTest {

    @get:Rule
    val screenshot = ScreenshotRule(FormFactor.Phone)

    @Test
    @Screenshot(
        locales = ["en-US", "pt-BR"],
        title = "Set up your workout",
        description = "Pick sections, train and rest times in seconds",
        titleByLocale = ["pt-BR=Configure seu treino"],
        descriptionByLocale = ["pt-BR=Escolha sets, tempo de treino e descanso"],
    )
    fun settings() = screenshot.capture {
        IntervalTimerTheme {
            SettingsScreenContent(
                sections = 5,
                trainTimeSeconds = 45,
                restTimeSeconds = 15,
                onSectionsChange = {},
                onTrainTimeChange = {},
                onRestTimeChange = {},
                onStartClicked = {},
                soundMode = SoundMode.SOUND_AND_VIBRATE,
                onSoundModeChange = {},
                volume = 80,
                onVolumeChange = {},
                onAdvancedSettingsClicked = {}
            )
        }
    }

    @Test
    @Screenshot(
        title = "Push through every set",
        description = "Visual cues guide you while you train",
    )
    fun running_train() = screenshot.capture {
        IntervalTimerTheme {
            TimerRunningComponent(
                remainingSections = 4,
                currentTime = 38,
                timerState = TimerState.TRAIN,
                isPaused = false,
                onPlayClicked = {},
                onPauseClicked = {},
                onStopClicked = {},
                onRestartClicked = {},
                soundMode = SoundMode.SOUND_AND_VIBRATE,
                onSoundModeChange = {},
                volume = 80,
                onVolumeChange = {}
            )
        }
    }

    @Test
    @Screenshot(
        title = "Recover, then go again",
        description = "Rest periods keep you on pace",
    )
    fun running_rest() = screenshot.capture {
        IntervalTimerTheme {
            TimerRunningComponent(
                remainingSections = 3,
                currentTime = 12,
                timerState = TimerState.REST,
                isPaused = false,
                onPlayClicked = {},
                onPauseClicked = {},
                onStopClicked = {},
                onRestartClicked = {},
                soundMode = SoundMode.SOUND_AND_VIBRATE,
                onSoundModeChange = {},
                volume = 80,
                onVolumeChange = {}
            )
        }
    }

    @Test
    @Screenshot(
        title = "Get ready",
        description = "A short prepare phase before each round",
    )
    fun running_prepare() = screenshot.capture {
        IntervalTimerTheme {
            TimerRunningComponent(
                remainingSections = 8,
                currentTime = 5,
                timerState = TimerState.PREPARE,
                isPaused = false,
                onPlayClicked = {},
                onPauseClicked = {},
                onStopClicked = {},
                onRestartClicked = {},
                soundMode = SoundMode.SOUND_AND_VIBRATE,
                onSoundModeChange = {},
                volume = 80,
                onVolumeChange = {}
            )
        }
    }

    @Test
    @Screenshot(
        title = "Workout complete",
        description = "Track every session, anywhere",
    )
    fun running_finished() = screenshot.capture {
        IntervalTimerTheme {
            TimerRunningComponent(
                remainingSections = 0,
                currentTime = 0,
                timerState = TimerState.FINISHED,
                isPaused = false,
                onPlayClicked = {},
                onPauseClicked = {},
                onStopClicked = {},
                onRestartClicked = {},
                soundMode = SoundMode.SOUND_AND_VIBRATE,
                onSoundModeChange = {},
                volume = 80,
                onVolumeChange = {}
            )
        }
    }
}
