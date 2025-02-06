package com.unimib.ignitionfinance.data.repository.interfaces

import com.unimib.ignitionfinance.data.remote.model.StockData
import kotlinx.coroutines.flow.Flow

interface StockRepository {
    suspend fun fetchStockData(symbol: String, apiKey: String): Flow<Result<Map<String, StockData>>>
}