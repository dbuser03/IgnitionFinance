// MyApplication.kt

package com.unimib.ignitionfinance

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.work.Configuration
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.unimib.ignitionfinance.data.remote.service.FirestoreService
import com.unimib.ignitionfinance.data.repository.interfaces.FirestoreRepository
import com.unimib.ignitionfinance.data.repository.interfaces.SyncQueueItemRepository
import com.unimib.ignitionfinance.data.worker.SyncWorker
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MyApplication : Application(), Configuration.Provider {
    @Inject
    lateinit var workerFactory: SyncWorkerFactory

    // val workManagerConfiguration =
        //Configuration.Builder()
            //.setMinimumLoggingLevel(Log.DEBUG)
            //.setWorkerFactory(workerFactory)
            //.build()
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setMinimumLoggingLevel(Log.DEBUG)
            .setWorkerFactory(workerFactory)
            .build()
}

class SyncWorkerFactory @Inject constructor(
    private val firestoreService: FirestoreService,
    private val syncQueueItemRepository: SyncQueueItemRepository,
    private val firestoreRepository: FirestoreRepository
) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? = when (workerClassName) {
        SyncWorker::class.java.name -> SyncWorker(
            firestoreService,
            appContext,
            workerParameters,
            syncQueueItemRepository,
            firestoreRepository
        )
        else -> null
    }
}