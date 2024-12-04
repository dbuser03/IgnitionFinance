package com.unimib.ignitionfinance.data.repository

import com.unimib.ignitionfinance.data.remote.service.ExchangeApiService
import com.unimib.ignitionfinance.data.remote.mapper.ExchangeMapper
import com.unimib.ignitionfinance.data.model.ExchangeData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

interface ExchangeRepository {
    suspend fun fetchExchangeData(seriesKey: String): Flow<Result<List<ExchangeData>>>
}

class ExchangeRepositoryImpl(
    private val apiService: ExchangeApiService,
    private val apiMapper: ExchangeMapper
) : ExchangeRepository {

    override suspend fun fetchExchangeData(seriesKey: String): Flow<Result<List<ExchangeData>>> = flow {
        try {
            val response = apiService.getExchangeRate(seriesKey = seriesKey)
            if (response.isSuccessful) {
                val exchangeData = response.body()
                if (exchangeData != null) {
                    emit(Result.success(apiMapper.mapToDomain(exchangeData)))
                } else {
                    emit(Result.failure(Throwable("Error: Empty response body")))
                }
            } else {
                emit(Result.failure(Throwable("Error: Failed to fetch exchange data")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)
}
