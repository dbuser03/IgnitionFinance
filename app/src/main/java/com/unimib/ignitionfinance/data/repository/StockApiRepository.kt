package com.unimib.ignitionfinance.data.repository

import com.unimib.ignitionfinance.data.remote.api_mapper.StockApiMapper
import com.unimib.ignitionfinance.data.remote.api_service.StockApiService
import com.unimib.ignitionfinance.data.model.StockData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

interface StockRepository {
    suspend fun fetchStockData(symbol: String, apiKey: String): Flow<Result<Map<String, StockData>>>
}

class StockRepositoryImpl(
    private val stockApiService: StockApiService,
    private val stockApiMapper: StockApiMapper
) : StockRepository {

    override suspend fun fetchStockData(symbol: String, apiKey: String): Flow<Result<Map<String, StockData>>> = flow {
        try {
            val response = stockApiService.getStockData(symbol = symbol, apiKey = apiKey)

            if (response.isSuccessful) {
                val stockData = response.body()
                if (stockData != null) {
                    emit(Result.success(stockApiMapper.mapToDomain(stockData)))
                } else {
                    emit(Result.failure(Throwable("Error: Empty response body")))
                }
            } else {
                emit(Result.failure(Throwable("Failed to fetch stock data")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)
}
