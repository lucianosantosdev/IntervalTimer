package dev.lucianosantos.intervaltimer

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import dev.lucianosantos.intervaltimer.core.data.TimerSettingsRepositoryImpl
import dev.lucianosantos.intervaltimer.core.service.ICountDownTimerService
import dev.lucianosantos.intervaltimer.core.ui.PickerType
import dev.lucianosantos.intervaltimer.core.viewmodels.SettingsViewModel

@Composable
fun WearNavHost(
    countDownTimerService: ICountDownTimerService,
    navController: NavHostController,
    startDestination: IntervalTimerDestination,
    modifier: Modifier = Modifier
) {
    Log.d("WEAR NAV HOST",navController.currentBackStack.toString() )

    val settingsViewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModel.Factory(TimerSettingsRepositoryImpl(LocalContext.current))
    )
    val settings by settingsViewModel.uiState.collectAsState()

    SwipeDismissableNavHost(
        navController = navController,
        startDestination = startDestination.route,
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
                icon = Icons.Rounded.ArrowForward
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
                icon = Icons.Rounded.ArrowForward
            )
        }

        composable(route = SetRestTime.route) {
            countDownTimerService.reset()
            PickerScreenComponent(
                title = LocalContext.current.getString(R.string.label_rest_number_picker),
                value = settings.timerSettings.restTimeSeconds,
                onValueChange = {
                    settingsViewModel.setRestTime(it)
                    navController.navigate(TimerRunning.route)
                },
                type = PickerType.TIME,
                icon = Icons.Rounded.PlayArrow
            )
        }
        
        composable(route = TimerRunning.route) {
            TimerRunningScreen(
                countDownTimerService = countDownTimerService,
                onRefreshClicked = {
                    navController.navigate(TimerRunning.route) {
                        popUpTo(TimerRunning.route) { inclusive = true }
                    }
                },
                onStopClicked = {
                    navController.navigate(SetSections.route) {
                        popUpTo(SetSections.route) { inclusive = true }
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