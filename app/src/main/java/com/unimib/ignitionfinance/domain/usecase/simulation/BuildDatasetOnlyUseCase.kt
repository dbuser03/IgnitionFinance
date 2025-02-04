package com.unimib.ignitionfinance.domain.usecase.simulation

import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class BuildDatasetOnlyUseCase @Inject constructor(
    private val buildDatasetUseCase: BuildDatasetUseCase
) {
    @RequiresApi(Build.VERSION_CODES.O)
    fun execute(apiKey: String): Flow<Result<Unit>> = flow {
        try {
            // Step 1: Build the dataset...
            val datasetResult = buildDatasetUseCase.execute(apiKey).first()
            datasetResult.getOrElse {
                emit(Result.failure(it))
                return@flow
            }
            emit(Result.success(Unit))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}