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
import dev.lucianosantos.intervaltimer.core.data.TimerSettings
import dev.lucianosantos.intervaltimer.core.data.TimerSettingsRepository
import dev.lucianosantos.intervaltimer.core.utils.AlertUserHelper
import dev.lucianosantos.intervaltimer.core.utils.CountDownTimerHelper
import dev.lucianosantos.intervaltimer.core.viewmodels.SettingsViewModel
import dev.lucianosantos.intervaltimer.core.viewmodels.TimerViewModel

@Composable
fun IntervalTimerNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {

    val settingsViewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModel.Factory(TimerSettingsRepository(LocalContext.current))
    )
    val settings by settingsViewModel.uiState.collectAsState()

    NavHost(
        navController = navController,
        startDestination = "Settings",
        modifier = modifier
    ) {

        composable(route = Settings.route) {
            SettingsScreen(
                settingsViewModel = settingsViewModel,
                onStartClicked = { navController.navigate(TimerRunning.route) })
        }

        composable(route = TimerRunning.route) {
            TimerRunningScreen(
                settings.timerSettings,
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