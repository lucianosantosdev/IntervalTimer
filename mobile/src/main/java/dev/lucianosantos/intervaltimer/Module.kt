package dev.lucianosantos.intervaltimer

import dev.lucianosantos.intervaltimer.core.data.ITimerSettingsRepository
import dev.lucianosantos.intervaltimer.core.data.TimerSettingsRepositoryImpl
import dev.lucianosantos.intervaltimer.core.viewmodels.SettingsViewModel
import dev.lucianosantos.intervaltimer.subscription.SubscriptionService
import dev.lucianosantos.intervaltimer.subscription.SubscriptionServiceImpl
import dev.lucianosantos.intervaltimer.subscription.SubscriptionViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module


val appModule = module {
    single<SubscriptionService> { SubscriptionServiceImpl(get()) }
    single<ITimerSettingsRepository> { TimerSettingsRepositoryImpl(get()) }
    viewModel { SettingsViewModel(get()) }
    viewModel { SubscriptionViewModel(get()) }
}