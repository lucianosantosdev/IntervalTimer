package dev.lucianosantos.intervaltimer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import dev.lucianosantos.intervaltimer.core.data.TimerSettingsRepository
import dev.lucianosantos.intervaltimer.core.service.CountDownTimerService
import dev.lucianosantos.intervaltimer.core.viewmodels.SettingsViewModel

@Composable
fun MobileNavHost(
    countDownTimerService: CountDownTimerService,
    navController: NavHostController,
    startDestination: IntervalTimerDestination,
    modifier: Modifier = Modifier
) {
    val settingsViewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModel.Factory(TimerSettingsRepository(LocalContext.current))
    )
    val settings by settingsViewModel.uiState.collectAsState()

    NavHost(
        navController = navController,
        startDestination = startDestination.route,
        modifier = modifier
    ) {
        composable(route = Settings.route) {
            SettingsScreen(
                settingsViewModel = settingsViewModel,
                onStartClicked = { navController.navigate(TimerRunning.route) })
        }
        composable(route = TimerRunning.route) {
            TimerRunningScreen(
                timerSettings = settings.timerSettings,
                countDownTimerService = countDownTimerService,
                onStopClicked = { navController.navigate(Settings.route) },
                onRestartClicked = {
                    navController.navigate(TimerRunning.route) {
                        popUpTo(TimerRunning.route) { inclusive = true }
                    }
                }
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