package com.unimib.ignitionfinance.domain.usecase.simulation

import android.os.Build
import androidx.annotation.RequiresApi
import com.unimib.ignitionfinance.data.model.user.Settings
import com.unimib.ignitionfinance.data.model.user.SimulationResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class StartSimulationUseCase @Inject constructor(
    private val buildDatasetUseCase: BuildDatasetUseCase
) {
    @RequiresApi(Build.VERSION_CODES.O)
    fun execute(apiKey: String, netWorth: Double, settings: Settings): Flow<Result<SimulationResult>> = flow {
        try {
            // Step 1: Build the dataset...
            val datasetResult = buildDatasetUseCase.execute(apiKey).first()
            datasetResult.getOrElse {
                emit(Result.failure(it))
                return@flow
            }

            // Step 2: Run the simulation logic (to be implemented)
            val simulationResult = runSimulation(netWorth, settings)

            emit(Result.success(simulationResult))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    private fun runSimulation(netWorth: Double, settings: Settings): SimulationResult {
        // Simulation logic implementation (to be added)
        return SimulationResult(
            finalBalance = 0.0,
            investmentGrowth = 0.0
        )
    }
}
