package com.unimib.ignitionfinance.domain.usecase.simulation

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.unimib.ignitionfinance.BuildConfig
import com.unimib.ignitionfinance.domain.simulation.AnnualReturnsMatrixGenerator
import com.unimib.ignitionfinance.domain.simulation.FireSimulator
import com.unimib.ignitionfinance.domain.simulation.InflationModel
import com.unimib.ignitionfinance.domain.simulation.model.SimulationResult
import com.unimib.ignitionfinance.domain.simulation.SimulationConfigFactory
import com.unimib.ignitionfinance.domain.simulation.WithdrawalCalculator
import com.unimib.ignitionfinance.domain.simulation.model.SimulationConfig
import com.unimib.ignitionfinance.domain.validation.SimulationConfigValidator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class StartSimulationUseCase @Inject constructor(
    private val buildDatasetUseCase: BuildDatasetUseCase,
    private val configFactory: SimulationConfigFactory
) {
    @RequiresApi(Build.VERSION_CODES.O)
    fun execute(): Flow<Result<SimulationResult>> = flow {
        try {
            val datasetResult = buildDatasetUseCase.execute(BuildConfig.ALPHAVANTAGE_API_KEY).first()
            datasetResult.getOrElse {
                emit(Result.failure(it))
                return@flow
            }

            val configResult = configFactory.createConfig().first()
            val config = configResult.getOrElse {
                emit(Result.failure(it))
                return@flow
            }

            val validationErrors = SimulationConfigValidator.validate(config)
            if (validationErrors.isNotEmpty()) {
                emit(Result.failure(IllegalArgumentException(validationErrors.joinToString("\n"))))
                return@flow
            }

            val simulationResult = runSimulation(config)
            emit(Result.success(simulationResult))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    private fun runSimulation(config: SimulationConfig): SimulationResult {
        val settings = config.settings
        val dataset = config.dataset
        val inflationData = config.historicalInflation.values.toList()
        val params = config.simulationParams

        val numSimulations = settings.numberOfSimulations.toInt()
        val simulationLength = 100
        val LOG_TAG = "SIMULATION_LOG"


        val (cumulativeReturnMatrix, annualReturnMatrix) = AnnualReturnsMatrixGenerator.generateMatrices(
            dataset = dataset,
            numSimulations = numSimulations,
            simulationLength = simulationLength,
            daysPerYear = params.daysPerYear
        )
        val reverseAnnualReturnMatrix = annualReturnMatrix.reversedArray()
        Log.d(LOG_TAG, "Annual Returns Matrix: ${reverseAnnualReturnMatrix.contentDeepToString()}")

        val inflationMatrix = InflationModel.generateInflationMatrix(
            scenarioInflation = settings.inflationModel.lowercase(),
            inflationMean = params.averageInflation,
            historicalInflation = inflationData,
            numSimulations = numSimulations,
            simulationLength = simulationLength
        )
        Log.d(LOG_TAG, "Inflation Matrix: ${inflationMatrix.contentDeepToString()}")

        val withdrawalMatrix = WithdrawalCalculator.calculateWithdrawals(
            initialWithdrawal = settings.withdrawals.withoutPension.toDouble(),
            yearsWithoutPension = settings.intervals.yearsInFIRE.toInt(),
            pensionWithdrawal = settings.withdrawals.withPension.toDouble(),
            inflationMatrix = inflationMatrix
        )

        val reversedWithdrawalsMatrix = withdrawalMatrix.reversedArray()
        Log.d(LOG_TAG, "Withdrawals Matrix: ${reversedWithdrawalsMatrix.contentDeepToString()}")

        return FireSimulator.simulatePortfolio(
            config = config,
            marketReturnsMatrix = annualReturnMatrix,
            withdrawalMatrix = withdrawalMatrix
        )
    }
}