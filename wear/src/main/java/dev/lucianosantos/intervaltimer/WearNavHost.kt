package dev.lucianosantos.intervaltimer

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import dev.lucianosantos.intervaltimer.core.data.TimerSettingsRepository
import dev.lucianosantos.intervaltimer.core.ui.PickerType
import dev.lucianosantos.intervaltimer.core.utils.ICountDownTimerHelper
import dev.lucianosantos.intervaltimer.core.viewmodels.SettingsViewModel

@Composable
fun WearNavHost(
    countDownTimer: ICountDownTimerHelper,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {

    val settingsViewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModel.Factory(TimerSettingsRepository(LocalContext.current))
    )
    val settings by settingsViewModel.uiState.collectAsState()

    SwipeDismissableNavHost(
        navController = navController,
        startDestination = SetSections.route,
        modifier = modifier
    ) {
        composable(route = SetSections.route) {
            PickerScreenComponent(
                title = LocalContext.current.getString(R.string.label_sections),
                value = settings.timerSettings.sections,
                onValueChange = {
                    settingsViewModel.setSections(it)
                    navController.navigate(SetTrainTime.route)
                },
                type = PickerType.NUMBER,
                icon = Icons.Default.ArrowForward
            )
        }

        composable(route = SetTrainTime.route) {
            PickerScreenComponent(
                title = LocalContext.current.getString(R.string.label_train_number_picker),
                value = settings.timerSettings.trainTimeSeconds,
                onValueChange = {
                    settingsViewModel.setTrainTime(it)
                    navController.navigate(SetRestTime.route)
                },
                type = PickerType.TIME,
                icon = Icons.Default.ArrowForward
            )
        }


        composable(route = SetRestTime.route) {
            PickerScreenComponent(
                title = LocalContext.current.getString(R.string.label_rest_number_picker),
                value = settings.timerSettings.restTimeSeconds,
                onValueChange = {
                    settingsViewModel.setRestTime(it)
                    navController.navigate(TimerRunning.route)
                },
                type = PickerType.TIME,
                icon = Icons.Default.PlayArrow
            )
        }
        
        composable(route = TimerRunning.route) {
            TimerRunningScreen(
                countDownTimer = countDownTimer,
                timerSettings = settings.timerSettings,
                onRefreshClicked = {
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

object SetSections : IntervalTimerDestination {
    override val route: String = "Sections"
}
object SetTrainTime : IntervalTimerDestination {
    override val route: String = "TrainTime"
}

object SetRestTime : IntervalTimerDestination {
    override val route: String = "RestTime"
}

object TimerRunning : IntervalTimerDestination {
    override val route: String = "TimerRunning"
}