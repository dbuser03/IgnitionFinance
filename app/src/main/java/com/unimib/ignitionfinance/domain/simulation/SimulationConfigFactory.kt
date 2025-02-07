package com.unimib.ignitionfinance.domain.simulation

import android.util.Log
// Nota: rimuovi l'import non necessario di PackageManagerCompat.LOG_TAG
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
import javax.inject.Inject

class SimulationConfigFactory @Inject constructor(
    private val getUserDatasetUseCase: GetUserDatasetUseCase,
    private val getUserSettingsUseCase: GetUserSettingsUseCase,
    private val fetchInflationUseCase: FetchInflationUseCase,
    private val getUserInvestedUseCase: GetUserInvestedUseCase,
    private val getUserCashUseCase: GetUserCashUseCase
) {
    companion object {
        private const val LOG_TAG = "SIM_CONFIG"
    }

    fun createConfig(): Flow<Result<SimulationConfig>> =
        combine(
            getUserDatasetUseCase.execute(),
            getUserSettingsUseCase.execute(),
            fetchInflationUseCase.execute(),
            getUserInvestedUseCase.execute(),
            getUserCashUseCase.execute()
        ) { datasetResult, settingsResult, inflationResult, investedResult, cashResult ->
            runCatching {
                val dataset = datasetResult.getOrThrow()
                val settings = settingsResult.getOrThrow()
                Log.d(LOG_TAG, "settings = $settings")

                val inflation = inflationResult.getOrThrow()
                Log.d(LOG_TAG, "inflation = $inflation")

                val invested = investedResult.getOrThrow()
                Log.d(LOG_TAG, "invested = $invested")

                val cash = cashResult.getOrThrow().toDoubleOrNull() ?: 0.0

                val simulationParams = SimulationParams()
                Log.d(LOG_TAG, "simulationParams = $simulationParams")

                SimulationConfig(
                    dataset = dataset,
                    settings = settings,
                    historicalInflation = inflation,
                    capital = Capital(
                        invested = invested,
                        cash = cash
                    ),
                    simulationParams = simulationParams
                )
            }.also { result ->
                if (result.isFailure) {
                    Log.e(LOG_TAG, "Errore nella creazione di SimulationConfig", result.exceptionOrNull())
                }
            }
        }
}
