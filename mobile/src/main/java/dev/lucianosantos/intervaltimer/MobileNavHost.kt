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
import dev.lucianosantos.intervaltimer.core.data.TimerSettingsRepositoryImpl
import dev.lucianosantos.intervaltimer.core.service.ICountDownTimerService
import dev.lucianosantos.intervaltimer.core.viewmodels.SettingsViewModel

@Composable
fun MobileNavHost(
    countDownTimerService: ICountDownTimerService,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val settingsViewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModel.Factory(TimerSettingsRepositoryImpl(LocalContext.current))
    )
    val settings by settingsViewModel.uiState.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Settings.route,
        modifier = modifier
    ) {
        composable(route = Settings.route) {
            countDownTimerService.reset()
            SettingsScreen(
                onStartClicked = {
                    countDownTimerService.setTimerSettings(settings.timerSettings)
                    navController.navigate(TimerRunning.route)
                }
            )
        }
        composable(route = TimerRunning.route) {
            TimerRunningScreen(
                countDownTimerService = countDownTimerService,
                onStop = {
                    navController.navigate(Settings.route) {
                        popUpTo(Settings.route) { inclusive = true }
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