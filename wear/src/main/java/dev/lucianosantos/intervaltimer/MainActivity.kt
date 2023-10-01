package dev.lucianosantos.intervaltimer

import WearAppTheme
import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisallowComposableCalls
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavHostController
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import androidx.wear.compose.ui.tooling.preview.WearPreviewLargeRound
import dev.lucianosantos.intervaltimer.core.BaseActivity
import dev.lucianosantos.intervaltimer.core.service.CountDownTimerService
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
            if (serviceBound) {
                Log.d(TAG, "countDownTimerService != null")
                MainApp(
                    countDownTimerService = countDownTimerService!!,
                    navHostController = navController,
                    startDestination = if (fromNotification) {
                        TimerRunning
                    } else {
                        SetSections
                    }
                )
            }
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
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