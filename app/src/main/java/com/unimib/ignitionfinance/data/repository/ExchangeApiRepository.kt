package com.unimib.ignitionfinance.data.repository

import com.unimib.ignitionfinance.data.remote.exchange_api.ExchangeApiResponseData
import com.unimib.ignitionfinance.data.remote.exchange_api.ExchangeApiService
import com.unimib.ignitionfinance.data.remote.exchange_api.SeriesData
import retrofit2.Response

class ExchangeRateRepository(private val apiService: ExchangeApiService) {

    suspend fun fetchExchangeRateData(): Result<List<ExchangeRate>> {
        val usdRatesResult = fetchExchangeRates("D.USD.EUR.SP00.A")
        val chfRatesResult = fetchExchangeRates("D.CHF.EUR.SP00.A")

        return if (usdRatesResult.isSuccess && chfRatesResult.isSuccess) {
            val result = usdRatesResult.getOrNull()?.plus(chfRatesResult.getOrNull().orEmpty())
            Result.success(result ?: emptyList())
        } else {
            Result.failure(Exception("Failed to fetch exchange rates"))
        }
    }

    private suspend fun fetchExchangeRates(seriesKey: String): Result<List<ExchangeRate>> {
        val response = apiService.getExchangeRate(seriesKey = seriesKey)

        return if (response.isSuccessful && response.body() != null) {
            val data = response.body()!!
            val cleanedData = processExchangeRateData(data, seriesKey)
            Result.success(cleanedData)
        } else {
            Result.failure(Exception("Failed to fetch data: ${response.code()}"))
        }
    }

    private fun processExchangeRateData(data: ExchangeApiResponseData, seriesKey: String): List<ExchangeRate> {
        val processedData = mutableListOf<ExchangeRate>()

        data.dataSets.forEach { dataSet ->
            dataSet.series.forEach { (key, seriesData) ->
                if (key == seriesKey) {
                    seriesData.observations.forEach { (date, values) ->
                        values.firstOrNull()?.let { rate ->
                            processedData.add(ExchangeRate(date, rate))
                        }
                    }
                }
            }
        }

        return processedData
    }
}

data class ExchangeRate(
    val date: String,
    val rate: Double
)
