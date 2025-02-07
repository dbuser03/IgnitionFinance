package com.unimib.ignitionfinance.domain.simulation

import com.unimib.ignitionfinance.domain.simulation.model.Capital
import com.unimib.ignitionfinance.domain.simulation.model.SimulationConfig
import com.unimib.ignitionfinance.domain.simulation.model.SimulationParams
import com.unimib.ignitionfinance.domain.usecase.fetch.FetchInflationUseCase
import com.unimib.ignitionfinance.domain.usecase.networth.cash.GetUserCashUseCase
import com.unimib.ignitionfinance.domain.usecase.networth.invested.GetUserInvestedUseCase
import com.unimib.ignitionfinance.domain.usecase.settings.GetUserDatasetUseCase
import com.unimib.ignitionfinance.domain.usecase.settings.GetUserSettingsUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SimulationConfigFactory @Inject constructor(
    private val getUserDatasetUseCase: GetUserDatasetUseCase,
    private val getUserSettingsUseCase: GetUserSettingsUseCase,
    private val fetchInflationUseCase: FetchInflationUseCase,
    private val getUserInvestedUseCase: GetUserInvestedUseCase,
    private val getUserCashUseCase: GetUserCashUseCase
) {
    fun createConfig(): Flow<Result<SimulationConfig>> = flow {
        try {
            combine(
                getUserDatasetUseCase.execute(),
                getUserSettingsUseCase.execute(),
                fetchInflationUseCase.execute(),
                getUserInvestedUseCase.execute(),
                getUserCashUseCase.execute()
            ) { datasetResult, settingsResult, inflationResult, investedResult, cashResult ->
                val dataset = datasetResult.getOrThrow()
                val settings = settingsResult.getOrThrow()
                val inflation = inflationResult.getOrThrow()
                val invested = investedResult.getOrThrow()
                val cash = cashResult.getOrThrow().toDoubleOrNull() ?: 0.0

                SimulationConfig(
                    dataset = dataset,
                    settings = settings,
                    historicalInflation = inflation,
                    capital = Capital(
                        invested = invested,
                        cash = cash
                    ),
                    simulationParams = SimulationParams()
                )
            }.collect { config ->
                emit(Result.success(config))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}