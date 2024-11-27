package com.unimib.ignitionfinance.data.repository

import com.unimib.ignitionfinance.data.remote.inflation_api.InflationApiResponseData
import com.unimib.ignitionfinance.data.remote.inflation_api.InflationApiService
import retrofit2.Response

class InflationRepository(private val inflationApiService: InflationApiService) {

    suspend fun fetchInflationData(): Result<List<InflationData>> {
        val response = inflationApiService.getInflationData()

        if (response.isSuccessful) {
            val inflationData = response.body()

            if (inflationData != null) {
                return Result.success(processInflationData(inflationData))
            }
        }

        return Result.failure(Throwable("Failed to fetch inflation data"))
    }

    private fun processInflationData(inflationData: InflationApiResponseData): List<InflationData> {
        val processedData = mutableListOf<InflationData>()

        inflationData.dataSets.forEach { dataSet ->
            dataSet.series.forEach { (seriesKey, seriesData) ->
                seriesData.observations.forEach { (date, values) ->
                    values.firstOrNull()?.let { inflationRate ->
                        processedData.add(InflationData(date, inflationRate))
                    }
                }
            }
        }

        return processedData
    }
}

data class InflationData(
    val date: String,
    val inflationRate: Double
)
