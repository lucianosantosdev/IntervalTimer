package dev.lucianosantos.intervaltimer.heartrate

import android.content.Context
import android.util.Log
import androidx.health.services.client.ExerciseUpdateCallback
import androidx.health.services.client.HealthServices
import androidx.health.services.client.data.Availability
import androidx.health.services.client.data.DataType
import androidx.health.services.client.data.ExerciseConfig
import androidx.health.services.client.data.ExerciseEvent
import androidx.health.services.client.data.ExerciseLapSummary
import androidx.health.services.client.data.ExerciseType
import androidx.health.services.client.data.ExerciseUpdate
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class CalorieMonitor(context: Context) {

    private val exerciseClient = HealthServices.getClient(context).exerciseClient

    fun caloriesKcal(): Flow<Int> = callbackFlow {
        trySend(0)
        val callback = object : ExerciseUpdateCallback {
            override fun onRegistered() {
                Log.d(TAG, "ExerciseUpdateCallback registered")
            }

            override fun onRegistrationFailed(throwable: Throwable) {
                Log.w(TAG, "ExerciseUpdateCallback registration failed", throwable)
            }

            override fun onExerciseUpdateReceived(update: ExerciseUpdate) {
                val kcal = update.latestMetrics.getData(DataType.CALORIES_TOTAL)?.total?.toInt()
                Log.d(TAG, "calories total=$kcal")
                if (kcal != null) trySend(kcal)
            }

            override fun onLapSummaryReceived(lapSummary: ExerciseLapSummary) {}

            override fun onAvailabilityChanged(
                dataType: DataType<*, *>,
                availability: Availability
            ) {
                Log.d(TAG, "availability dataType=$dataType availability=$availability")
            }

            override fun onExerciseEventReceived(event: ExerciseEvent) {}
        }

        exerciseClient.setUpdateCallback(callback)

        val config = ExerciseConfig.Builder(ExerciseType.HIGH_INTENSITY_INTERVAL_TRAINING)
            .setDataTypes(setOf(DataType.CALORIES_TOTAL, DataType.HEART_RATE_BPM))
            .setIsAutoPauseAndResumeEnabled(false)
            .setIsGpsEnabled(false)
            .build()

        try {
            exerciseClient.startExerciseAsync(config)
            Log.d(TAG, "startExerciseAsync requested")
        } catch (t: Throwable) {
            Log.w(TAG, "Failed to start exercise session", t)
        }

        awaitClose {
            try {
                exerciseClient.endExerciseAsync()
                Log.d(TAG, "endExerciseAsync requested")
            } catch (t: Throwable) {
                Log.w(TAG, "Failed to end exercise session", t)
            }
        }
    }

    companion object {
        private const val TAG = "CalorieMonitor"
    }
}
