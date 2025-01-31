package com.unimib.ignitionfinance.data.repository.implementation

import com.unimib.ignitionfinance.data.remote.mapper.SearchStockMapper
import com.unimib.ignitionfinance.data.remote.service.SearchStockService
import com.unimib.ignitionfinance.data.model.SearchStockData
import com.unimib.ignitionfinance.data.repository.interfaces.SearchStockRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class SearchStockRepositoryImpl @Inject constructor(
    private val searchStockService: SearchStockService,
    private val searchStockMapper: SearchStockMapper
) : SearchStockRepository {

    override suspend fun fetchSearchStockData(symbol: String, apiKey: String): Flow<Result<List<SearchStockData>>> = flow {
        try {
            val response = searchStockService.getSearchStockData(symbol = symbol, apiKey = apiKey)

            if (response.isSuccessful) {
                val searchStockData = response.body()
                if (searchStockData != null) {
                    emit(Result.success(searchStockMapper.mapToDomain(searchStockData)))
                } else {
                    emit(Result.failure(Throwable("Error: Empty response body")))
                }
            } else {
                emit(Result.failure(Throwable("Failed to fetch search stock data")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)
}