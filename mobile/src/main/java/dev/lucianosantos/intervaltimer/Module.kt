package dev.lucianosantos.intervaltimer

import dev.lucianosantos.intervaltimer.core.data.ITimerSettingsRepository
import dev.lucianosantos.intervaltimer.core.data.TimerSettingsRepositoryImpl
import dev.lucianosantos.intervaltimer.core.viewmodels.SettingsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module


val appModule = module {
    single<ITimerSettingsRepository> { TimerSettingsRepositoryImpl(get()) }
    viewModel { SettingsViewModel(get()) }
}