package dev.lucianosantos.intervaltimer

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import dev.lucianosantos.intervaltimer.core.BaseActivity
import dev.lucianosantos.intervaltimer.core.service.ICountDownTimerService
import dev.lucianosantos.intervaltimer.core.service.NotificationHelper
import dev.lucianosantos.intervaltimer.theme.IntervalTimerTheme

class MainActivity : BaseActivity() {
    override val serviceName: Class<*>
        get() = CountDownTimerServiceMobile::class.java

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val fromNotification = intent.getBooleanExtra(NotificationHelper.EXTRA_LAUNCH_FROM_NOTIFICATION, false)

        setContent {
            IntervalTimerTheme {
                val navController = rememberNavController()
                MainApp(
                    countDownTimerService = countDownTimerServiceProxy,
                    navController = navController
                )
                if (fromNotification) {
                    navController.navigate(TimerRunning.route)
                }
            }
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }
}

@Composable
fun MainApp(
    navController: NavHostController,
    countDownTimerService: ICountDownTimerService
) {
    MobileNavHost(
        countDownTimerService = countDownTimerService,
        navController = navController,
        modifier = Modifier.fillMaxSize()
    )
}