package com.unimib.ignitionfinance.data.repository

import com.unimib.ignitionfinance.data.remote.api_mapper.StockApiMapper
import com.unimib.ignitionfinance.data.remote.api_service.StockApiService
import com.unimib.ignitionfinance.domain.model.StockData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface StockRepository {
    suspend fun fetchStockData(symbol: String, apiKey: String): Result<Map<String, StockData>>
}

class StockRepositoryImpl(
    private val stockApiService: StockApiService,
    private val stockApiMapper: StockApiMapper
) : StockRepository {

    override suspend fun fetchStockData(symbol: String, apiKey: String): Result<Map<String, StockData>> =
        withContext(Dispatchers.IO) {
            try {
                val response = stockApiService.getStockData(symbol = symbol, apiKey = apiKey)

                if (response.isSuccessful) {
                    val stockData = response.body()
                    if (stockData != null) {
                        return@withContext Result.success(stockApiMapper.mapToDomain(stockData))
                    }
                }
                Result.failure(Throwable("Failed to fetch stock data"))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}
