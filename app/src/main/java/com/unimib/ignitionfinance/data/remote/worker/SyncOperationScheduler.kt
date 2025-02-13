package com.unimib.ignitionfinance.data.remote.worker

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object SyncOperationScheduler {
    const val SYNC_WORK_NAME = "sync_operation_work"
    const val MAX_RETRIES = 3
    const val SYNC_TIMEOUT_MS = 30000L
    const val BATCH_SIZE = 10
    const val BATCH_DELAY_MS = 1000L
    const val INITIAL_BACKOFF_DELAY_MS = 10000L
    const val PERIODIC_SYNC_INTERVAL = 15L
    const val MIN_BACKOFF_DELAY_MS = 5000L
    const val MAX_BACKOFF_DELAY_MS = 300000L

    inline fun <reified T> schedule(
        context: Context,
        constraints: Constraints = getDefaultConstraints()
    ) {
        try {
            val syncWorkRequest = PeriodicWorkRequestBuilder<SyncWorker<T>>(
                repeatInterval = PERIODIC_SYNC_INTERVAL,
                repeatIntervalTimeUnit = TimeUnit.MINUTES,
                flexTimeInterval = 5,
                flexTimeIntervalUnit = TimeUnit.MINUTES
            )
                .setConstraints(constraints)
                .setBackoffCriteria(
                    BackoffPolicy.EXPONENTIAL,
                    INITIAL_BACKOFF_DELAY_MS,
                    TimeUnit.MILLISECONDS
                )
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                SYNC_WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                syncWorkRequest
            )
        } catch (_: Exception) {
        }
    }

    inline fun <reified T> scheduleOneTime(
        context: Context,
        constraints: Constraints = getDefaultConstraints(),
        initialDelay: Long = 0L
    ) {
        try {
            val syncWorkRequest = OneTimeWorkRequestBuilder<SyncWorker<T>>()
                .setConstraints(constraints)
                .setBackoffCriteria(
                    BackoffPolicy.EXPONENTIAL,
                    INITIAL_BACKOFF_DELAY_MS.coerceIn(MIN_BACKOFF_DELAY_MS, MAX_BACKOFF_DELAY_MS),
                    TimeUnit.MILLISECONDS
                )
                .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                .build()


            WorkManager.getInstance(context).enqueue(syncWorkRequest)
        } catch (_: Exception) {
        }
    }

    fun cancel(context: Context) {
        try {
            WorkManager.getInstance(context).cancelUniqueWork(SYNC_WORK_NAME)
        } catch (_: Exception) {
        }
    }

    fun getDefaultConstraints() = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .setRequiresBatteryNotLow(true)
        .build()
}