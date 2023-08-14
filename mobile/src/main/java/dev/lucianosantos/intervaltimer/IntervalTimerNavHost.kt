package dev.lucianosantos.intervaltimer

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import dev.lucianosantos.intervaltimer.core.data.TimerSettings

@Composable
fun IntervalTimerNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = "Settings",
        modifier = modifier
    ) {
        composable(route = Settings.route) {
            SettingsScreen(onStartClicked = { navController.navigate(TimerRunning.route) })
        }

        composable(route = TimerRunning.route) {
            TimerRunningScreen(
                timerSettings = TimerSettings(
                    1, 5, 5, 5
                ),
                onStopClicked = { navController.navigate(Settings.route) })
        }
    }
}

interface IntervalTimerDestination {
    val route: String
}

object Settings : IntervalTimerDestination {
    override val route: String = "Settings"
}

object TimerRunning : IntervalTimerDestination {
    override val route: String = "TimerRunning"
}