package com.unimib.ignitionfinance.domain.usecase.simulation

import android.os.Build
import androidx.annotation.RequiresApi
import com.unimib.ignitionfinance.BuildConfig
import com.unimib.ignitionfinance.domain.simulation.model.SimulationResult
import com.unimib.ignitionfinance.domain.simulation.SimulationConfigFactory
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

/*            when (SimulationConfigValidator.validate(config)) {
                else -> {
                    val simulationResult = runSimulation(config)
                    emit(Result.success(simulationResult))
                }
            }*/
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

/*    private fun runSimulation(config: SimulationConfig): SimulationResult {

        val capital = config.capital.total
        val settings = config.settings
        val dataset = config.dataset
        val inflationData = config.historicalInflation
        val params = config.simulationParams

        val numSimulations = settings.numberOfSimulations.toInt()
        val withdrawalWithoutPension = settings.withdrawals.withoutPension.toDouble()
        val withdrawalWithPension = settings.withdrawals.withPension.toDouble()
        val taxRate = settings.expenses.taxRatePercentage.toDouble() / 100
        val stampDuty = settings.expenses.stampDutyPercentage.toDouble() / 100
        val loadPercentage = settings.expenses.loadPercentage.toDouble() / 100

        val yearsInFire = settings.intervals.yearsInFIRE.toInt()
        val yearsInPaidRetirement = settings.intervals.yearsInPaidRetirement.toInt()
        val bufferYears = settings.intervals.yearsOfBuffer.toInt()

        return SimulationResult(
            successRate = 0.0,
            fuckYouMoney = 0.0,
            successRatePlus100k = 0.0,
            successRatePlus200k = 0.0,
            successRatePlus300k = 0.0
        )
    }*/
}