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

    companion object {
        private const val TAG = "SIMULATION_USECASE"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun execute(): Flow<Result<Pair<List<SimulationResult>, Double>>> = flow {
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

            val capitalIncrements = listOf(0.0, 50_000.0, 100_000.0, 150_000.0)
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
                val errorMessage = validationErrors.joinToString("\n")
                emit(Result.failure(IllegalArgumentException(errorMessage)))
                return@flow
            }

            // Eseguiamo le simulazioni per ogni configurazione.
            // Poiché runSimulation è una funzione suspend, la invochiamo in modo sequenziale.
            val results = mutableListOf<SimulationResult>()
            for (config in configs) {
                results.add(runSimulation(config))
            }

            val startingCapital = configs.last().capital.total
            val fuckYouMoney = calculateFuckYouMoney(baseConfig, startingCapital)

            // Log per stampare il valore del Fuck You Money
            Log.d(TAG, "Calculated Fuck You Money: $fuckYouMoney")

            emit(Result.success(results to fuckYouMoney))
        } catch (e: Exception) {
            Log.e(TAG, "Error during simulation execution", e)
            emit(Result.failure(e))
        }
    }

    // Funzione suspend per eseguire la simulazione per una configurazione data.
    private suspend fun runSimulation(config: SimulationConfig): SimulationResult {
        val settings = config.settings
        val dataset = config.dataset
        val inflationData = config.historicalInflation.values.toList()
        val params = config.simulationParams

        val numSimulations = settings.numberOfSimulations.toInt()
        val simulationLength = 100

        val (_, annualReturnMatrix) = AnnualReturnsMatrixGenerator.generateMatrices(
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

        val withdrawalMatrix = WithdrawalCalculator.calculateWithdrawals(
            initialWithdrawal = settings.withdrawals.withoutPension.toDouble(),
            yearsWithoutPension = settings.intervals.yearsInFIRE.toInt(),
            pensionWithdrawal = settings.withdrawals.withPension.toDouble(),
            inflationMatrix = inflationMatrix
        )

        // Chiamata alla funzione suspend simulatePortfolio del FireSimulator.
        return FireSimulator.simulatePortfolio(
            config = config,
            marketReturnsMatrix = annualReturnMatrix,
            withdrawalMatrix = withdrawalMatrix
        )
    }

    // Funzione suspend per il calcolo del Fuck You Money.
    // Viene effettuato un tentativo per ogni incremento di capitale, con log ad ogni iterazione.
    private suspend fun calculateFuckYouMoney(baseConfig: SimulationConfig, startingCapital: Double): Double {
        val increment = 50_000.0
        val successRateThreshold = 0.95

        val baseTotal = baseConfig.capital.total
        val investedRatio = if (baseTotal > 0) baseConfig.capital.invested / baseTotal else 0.0
        val cashRatio = if (baseTotal > 0) baseConfig.capital.cash / baseTotal else 0.0

        var totalCapital = startingCapital
        var simulationResult: SimulationResult

        for (attempt in 1..4) {
            val config = baseConfig.copy(
                capital = baseConfig.capital.copy(
                    invested = totalCapital * investedRatio,
                    cash = totalCapital * cashRatio
                )
            )

            simulationResult = runSimulation(config)
            val successRate = simulationResult.successRate

            // Log per ogni tentativo
            Log.d(TAG, "Attempt $attempt: totalCapital = $totalCapital, successRate = $successRate")

            if (successRate >= successRateThreshold) {
                return totalCapital
            }

            if (attempt < 4) {
                totalCapital += increment
            }
        }

        return totalCapital
    }
}
