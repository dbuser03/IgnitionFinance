package com.unimib.ignitionfinance.data.worker

import android.content.Context
import android.util.Log
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object SyncOperationScheduler {
    private const val TAG = "SyncOperationScheduler"
    const val SYNC_WORK_NAME = "sync_operation_work"
    const val MAX_RETRIES = 3
    const val SYNC_TIMEOUT_MS = 30000L
    const val BATCH_SIZE = 10
    const val BATCH_DELAY_MS = 1000L
    private const val INITIAL_BACKOFF_DELAY_MS = 10000L

    fun schedule(context: Context, constraints: Constraints = getDefaultConstraints()) {
        Log.d(TAG, "Scheduling periodic sync work with interval: 15 minutes")
        try {
            val syncWorkRequest = PeriodicWorkRequestBuilder<SyncWorker>(
                repeatInterval = 15,
                repeatIntervalTimeUnit = TimeUnit.MINUTES
            )
                .setConstraints(constraints)
                .setBackoffCriteria(
                    BackoffPolicy.EXPONENTIAL,
                    INITIAL_BACKOFF_DELAY_MS,
                    TimeUnit.MILLISECONDS
                )
                .build()

            Log.d(TAG, "Work request built with constraints: ${constraints.requiredNetworkType}")

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                SYNC_WORK_NAME,
                ExistingPeriodicWorkPolicy.REPLACE,
                syncWorkRequest
            )
            Log.i(TAG, "Periodic sync work scheduled successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error scheduling periodic sync work", e)
        }
    }

    fun scheduleOneTime(context: Context, constraints: Constraints = getDefaultConstraints()) {
        Log.d(TAG, "Scheduling one-time sync work")
        try {
            val syncWorkRequest = OneTimeWorkRequestBuilder<SyncWorker>()
                .setConstraints(constraints)
                .setBackoffCriteria(
                    BackoffPolicy.EXPONENTIAL,
                    INITIAL_BACKOFF_DELAY_MS,
                    TimeUnit.MILLISECONDS
                )
                .build()

            Log.d(TAG, "One-time work request built with constraints: ${constraints.requiredNetworkType}")

            WorkManager.getInstance(context).enqueue(syncWorkRequest)
            Log.i(TAG, "One-time sync work scheduled successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error scheduling one-time sync work", e)
        }
    }

    fun cancel(context: Context) {
        Log.d(TAG, "Cancelling sync work: $SYNC_WORK_NAME")
        try {
            WorkManager.getInstance(context).cancelUniqueWork(SYNC_WORK_NAME)
            Log.i(TAG, "Sync work cancelled successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error cancelling sync work", e)
        }
    }

    private fun getDefaultConstraints() = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .setRequiresBatteryNotLow(true)
        .build()
}