package com.unimib.ignitionfinance.domain.simulation.inflation

import com.unimib.ignitionfinance.domain.usecase.fetch.FetchInflationUseCase
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class InflationDataProvider @Inject constructor(
    private val fetchInflationUseCase: FetchInflationUseCase
){
    suspend fun getHistoricalInflationData(): DoubleArray {
        return try {
            val inflationData = fetchInflationUseCase.execute().first()
            inflationData.fold(
                onSuccess = { data ->
                    data.values.map { it / 100.0 }.toDoubleArray()
                },
                onFailure = { exception ->
                    doubleArrayOf()
                }
            )
        } catch (_: Exception) {
            doubleArrayOf()
        }
    }
}