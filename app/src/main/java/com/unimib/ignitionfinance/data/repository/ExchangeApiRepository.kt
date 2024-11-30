package com.unimib.ignitionfinance.data.repository

import com.unimib.ignitionfinance.data.remote.api_service.ExchangeApiService
import com.unimib.ignitionfinance.data.remote.api_mapper.ExchangeApiMapper
import com.unimib.ignitionfinance.domain.model.ExchangeData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface ExchangeRepository {
    suspend fun fetchExchangeData(seriesKey: String): Result<List<ExchangeData>>
}

class ExchangeRepositoryImpl(
    private val apiService: ExchangeApiService,
    private val apiMapper: ExchangeApiMapper
) : ExchangeRepository {

    override suspend fun fetchExchangeData(seriesKey: String): Result<List<ExchangeData>> =
        withContext(Dispatchers.IO) {
            try {
                val response = apiService.getExchangeRate(seriesKey = seriesKey)
                if (response.isSuccessful) {
                    val exchangeData = response.body()
                    if (exchangeData != null) {
                        return@withContext Result.success(apiMapper.mapToDomain(exchangeData))
                    }
                }
                Result.failure(Throwable("Error: Failed to fetch or parse exchange data"))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}
