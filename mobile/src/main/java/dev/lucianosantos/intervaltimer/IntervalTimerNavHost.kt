package dev.lucianosantos.intervaltimer

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import dev.lucianosantos.intervaltimer.core.data.TimerState

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
            TimerRunningScreen(1, "00:00", TimerState.REST, {}, {
                navController.navigate(TimerFinished.route)
            } )
        }

        composable(route = TimerFinished.route) {
            TimerFinishedScreen(
                onStopClicked = { navController.navigate(Settings.route) },
                onRestartClicked = { navController.navigate(TimerRunning.route) }
            )
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

object TimerFinished : IntervalTimerDestination {
    override val route: String = "TimerFinished"
}