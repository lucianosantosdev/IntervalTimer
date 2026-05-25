package dev.lucianosantos.intervaltimer.screenshots

import WearAppTheme
import dev.lucianosantos.intervaltimer.TimerRunningComponent
import dev.lucianosantos.intervaltimer.WearSettingsScreen
import dev.lucianosantos.intervaltimer.core.data.TimerState
import dev.lucianosantos.intervaltimer.core.viewmodels.SoundMode
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
    val screenshot = ScreenshotRule(FormFactor.Wear)

    @Test
    @Screenshot
    fun running_train() = screenshot.capture {
        WearAppTheme {
            TimerRunningComponent(
                remainingSections = 5,
                currentTimeSeconds = 42,
                timerState = TimerState.TRAIN,
                isPaused = false,
                onPlayClicked = {},
                onPauseClicked = {},
                heartRate = 132,
                calories = 18,
                onSettingsClick = {}
            )
        }
    }

    @Test
    @Screenshot
    fun running_rest() = screenshot.capture {
        WearAppTheme {
            TimerRunningComponent(
                remainingSections = 3,
                currentTimeSeconds = 12,
                timerState = TimerState.REST,
                isPaused = false,
                onPlayClicked = {},
                onPauseClicked = {},
                heartRate = 108,
                calories = 47,
                onSettingsClick = {}
            )
        }
    }

    @Test
    @Screenshot
    fun running_prepare() = screenshot.capture {
        WearAppTheme {
            TimerRunningComponent(
                remainingSections = 8,
                currentTimeSeconds = 5,
                timerState = TimerState.PREPARE,
                isPaused = false,
                onPlayClicked = {},
                onPauseClicked = {},
                heartRate = 82,
                calories = 0,
                onSettingsClick = {}
            )
        }
    }

    @Test
    @Screenshot
    fun running_finished() = screenshot.capture {
        WearAppTheme {
            TimerRunningComponent(
                remainingSections = 0,
                currentTimeSeconds = 0,
                timerState = TimerState.FINISHED,
                isPaused = false,
                onPlayClicked = {},
                onPauseClicked = {},
                heartRate = 95,
                calories = 152,
                onSettingsClick = {}
            )
        }
    }

    @Test
    @Screenshot
    fun settings() = screenshot.capture {
        WearAppTheme {
            WearSettingsScreen(
                volume = 70,
                soundMode = SoundMode.SOUND_AND_VIBRATE,
                wakeScreenOnTransition = true,
                onVolumeChange = {},
                onSoundModeChange = {},
                onWakeScreenChange = {}
            )
        }
    }
}
