package com.unimib.ignitionfinance.data.repository.implementation

import com.unimib.ignitionfinance.data.remote.mapper.StockMapper
import com.unimib.ignitionfinance.data.remote.service.StockService
import com.unimib.ignitionfinance.data.remote.model.StockData
import com.unimib.ignitionfinance.data.repository.interfaces.StockRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class StockRepositoryImpl @Inject constructor(
    private val stockService: StockService,
    private val stockApiMapper: StockMapper
) : StockRepository {

    override suspend fun fetchStockData(symbol: String, apiKey: String): Flow<Result<Map<String, StockData>>> = flow {
        try {
            val response = stockService.getStockData(symbol = symbol, apiKey = apiKey)

            if (response.isSuccessful) {
                val stockData = response.body()
                if (stockData != null) {
                    val mappedData = stockApiMapper.mapToDomain(stockData)
                    emit(Result.success(mappedData))
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