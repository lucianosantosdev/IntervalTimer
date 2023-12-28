package dev.lucianosantos.intervaltimer

import WearAppTheme
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavHostController
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import androidx.wear.compose.ui.tooling.preview.WearPreviewLargeRound
import dev.lucianosantos.intervaltimer.core.BaseActivity
import dev.lucianosantos.intervaltimer.core.service.ICountDownTimerService
import dev.lucianosantos.intervaltimer.core.service.NotificationHelper


class MainActivity : BaseActivity() {

    override val serviceName = CountDownTimerServiceWear::class.java

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        Log.d(TAG, "OnCreate!")
        setTheme(R.style.Theme_App)

        val fromNotification = intent.getBooleanExtra(NotificationHelper.EXTRA_LAUNCH_FROM_NOTIFICATION, false)

        setContent {
            val navController = rememberSwipeDismissableNavController()
            MainApp(
                countDownTimerService = countDownTimerServiceProxy,
                navHostController = navController,
                startDestination = if (fromNotification) {
                    TimerRunning
                } else {
                    SetSections
                }
            )
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return when(keyCode) {
            KeyEvent.KEYCODE_STEM_1 -> {
                Log.d(TAG, "KEYCODE_STEM_1")
                true
            }
            KeyEvent.KEYCODE_STEM_2 -> {
                Log.d(TAG, "KEYCODE_STEM_2")
                true
            }
            KeyEvent.KEYCODE_STEM_3 -> {
                Log.d(TAG, "KEYCODE_STEM_3")
                true
            }
            KeyEvent.KEYCODE_STEM_PRIMARY -> {
                Log.d(TAG, "KEYCODE_STEM_PRIMARY")
                true
            }
            else -> {
                super.onKeyDown(keyCode, event)
            }
        }
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        return when (keyCode) {
            else -> super.onKeyUp(keyCode, event)
        }
    }

    companion object {
        const val TAG = "MAIN ACTIVITY!"
    }
}

@Composable
fun MainApp(
    countDownTimerService: ICountDownTimerService,
    navHostController: NavHostController,
    startDestination: IntervalTimerDestination
) {
    WearAppTheme {
        WearNavHost(
            countDownTimerService = countDownTimerService,
            navController = navHostController,
            startDestination = startDestination,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@WearPreviewLargeRound
@Composable
fun MainAppPreview() {
    val navController = rememberSwipeDismissableNavController()
    MainApp(CountDownTimerServiceWear(), navController, SetSections)
}