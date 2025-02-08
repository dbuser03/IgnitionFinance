package com.unimib.ignitionfinance.domain.usecase.simulation

import android.os.Build
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
    fun execute(apiKey: String): Flow<Result<SimulationResult>> = flow {
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

        val (cumulativeReturnsMatrix, annualReturnsMatrix) = AnnualReturnsMatrixGenerator.generateMatrices( // DUBBIO 1 parametro non usato
            dataset = dataset,
            numSimulations = numSimulations,
            simulationLength = simulationLength,
            daysPerYear = params.daysPerYear
        )

        val inflationMatrix = InflationModel.generateInflationMatrix(
            scenarioInflation = settings.inflationModel.lowercase(),
            inflationMean = params.averageInflation,
            historicalInflation = inflationData,
            numSimulations = numSimulations,
            simulationLength = simulationLength
        )

        val withdrawalMatrix = WithdrawalCalculator.calculateWithdrawals( // Qui sicuramente è scorretto il calcolo dei withdrawal -> non viene settings.intervals.yearsInFIRE / distinzione tra anni con e senza pensionegi
            initialWithdrawal = settings.withdrawals.withoutPension.toDouble(),
            yearsWithoutPension = settings.intervals.yearsInPaidRetirement.toInt(),
            pensionWithdrawal = settings.withdrawals.withPension.toDouble(),
            inflationMatrix = inflationMatrix
        )

        return FireSimulator.simulatePortfolio(
            config = config,
            marketReturnsMatrix = annualReturnsMatrix,
            withdrawalMatrix = withdrawalMatrix
        )
    }
}