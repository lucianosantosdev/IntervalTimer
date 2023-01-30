package dev.lucianosantos.intervaltimer.core.utils

interface IAlertUserHelper {
    suspend fun startPrepareAlert()

    suspend fun startTrainAlert()

    suspend fun startRestAlert()

    suspend fun finishedAlert()

    suspend fun timerAlmostFinishingAlert()
}