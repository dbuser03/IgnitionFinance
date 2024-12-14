package com.unimib.ignitionfinance.data.repository.interfaces

import com.unimib.ignitionfinance.data.model.SearchStockData
import kotlinx.coroutines.flow.Flow

interface SearchStockRepository {
    suspend fun fetchSearchStockData(symbol: String, apiKey: String): Flow<Result<List<SearchStockData>>>
}