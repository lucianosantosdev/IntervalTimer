package dev.lucianosantos.intervaltimer.core.utils

interface IBeepHelper {
    suspend fun startPrepareBeep()

    suspend fun startTrainBeep()

    suspend fun startRestBeep()

    suspend fun finishedBeep()

    suspend fun timerAlmostFinishingBeep()
}