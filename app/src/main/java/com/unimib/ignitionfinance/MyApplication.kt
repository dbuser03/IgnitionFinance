package com.unimib.ignitionfinance

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.work.Configuration
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.unimib.ignitionfinance.data.worker.SyncWorker
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MyApplication : Application(), Configuration.Provider {

        @Inject
        lateinit var workerFactory: SyncWorkerFactory

    override val workManagerConfiguration =
        Configuration.Builder()
            .setMinimumLoggingLevel(Log.DEBUG)
            .setWorkerFactory(workerFactory)
            .build()

}

class  SyncWorkerFactory @Inject constructor(private val api: Demoapi): WorkerFactory(){ // demo api va sostituito con qualcosa di significativo per il nostro progetto
    //e' un esempio del bro del video
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? = SyncWorker(api, appContext, workerParameters)

}