package com.unimib.ignitionfinance.data.repository.implementation

import com.unimib.ignitionfinance.data.remote.service.ExchangeService
import com.unimib.ignitionfinance.data.remote.mapper.ExchangeMapper
import com.unimib.ignitionfinance.data.model.ExchangeData
import com.unimib.ignitionfinance.data.repository.interfaces.ExchangeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class ExchangeRepositoryImpl(
    private val apiService: ExchangeService,
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