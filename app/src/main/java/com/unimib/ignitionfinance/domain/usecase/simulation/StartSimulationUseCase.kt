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
    fun execute(): Flow<Result<List<SimulationResult>>> = flow {
        try {
            val datasetResult = buildDatasetUseCase.execute(BuildConfig.ALPHAVANTAGE_API_KEY).first()
            datasetResult.getOrElse {
                emit(Result.failure(it))
                return@flow
            }

            val configResult = configFactory.createConfig().first()
            val baseConfig = configResult.getOrElse {
                emit(Result.failure(it))
                return@flow
            }

            val capitalIncrements = listOf(0.0, 100_000.0, 200_000.0, 300_000.0)
            val configs = capitalIncrements.map { increment ->
                baseConfig.copy(
                    capital = baseConfig.capital.copy(
                        cash = baseConfig.capital.cash + increment
                    )
                )
            }

            val validationErrors = configs.flatMap { config ->
                SimulationConfigValidator.validate(config).map {
                    "Config with capital ${config.capital.total}: $it"
                }
            }

            if (validationErrors.isNotEmpty()) {
                emit(Result.failure(IllegalArgumentException(validationErrors.joinToString("\n"))))
                return@flow
            }

            val results = configs.map { runSimulation(it) }
            emit(Result.success(results))

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
        val tag = "SIMULATION_LOG"
        val returnsTag = "SIMULATION_LOG"

        val (_, annualReturnMatrix) = AnnualReturnsMatrixGenerator.generateMatrices(
            dataset = dataset,
            numSimulations = numSimulations,
            simulationLength = simulationLength,
            daysPerYear = params.daysPerYear
        )

        for (t in 0 until simulationLength) {
            val annualAverageReturn = annualReturnMatrix[t].average()
            Log.d(returnsTag, "Year $t - Avg Return: $annualAverageReturn")
        }

        val inflationMatrix = InflationModel.generateInflationMatrix(
            scenarioInflation = settings.inflationModel.lowercase(),
            inflationMean = params.averageInflation,
            historicalInflation = inflationData,
            numSimulations = numSimulations,
            simulationLength = simulationLength
        )
        val inflationAverage = inflationMatrix.flatMap { it.asList() }.average()
        Log.d(tag, "Inflation Matrix Average: $inflationAverage")

        val withdrawalMatrix = WithdrawalCalculator.calculateWithdrawals(
            initialWithdrawal = settings.withdrawals.withoutPension.toDouble(),
            yearsWithoutPension = settings.intervals.yearsInFIRE.toInt(),
            pensionWithdrawal = settings.withdrawals.withPension.toDouble(),
            inflationMatrix = inflationMatrix
        )
        val withdrawalAverage = withdrawalMatrix.flatMap { it.asList() }.average()
        Log.d(tag, "Withdrawal Matrix Average: $withdrawalAverage")

        return FireSimulator.simulatePortfolio(
            config = config,
            marketReturnsMatrix = annualReturnMatrix,
            withdrawalMatrix = withdrawalMatrix
        )
    }
}