package com.unimib.ignitionfinance.data.repository

import com.unimib.ignitionfinance.data.remote.service.InflationApiService
import com.unimib.ignitionfinance.data.remote.mapper.InflationMapper
import com.unimib.ignitionfinance.data.model.InflationData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

interface InflationRepository {
    suspend fun fetchInflationData(): Flow<Result<List<InflationData>>>
}

class InflationRepositoryImpl(
    private val inflationApiService: InflationApiService,
    private val inflationApiMapper: InflationMapper
) : InflationRepository {

    override suspend fun fetchInflationData(): Flow<Result<List<InflationData>>> = flow {
        try {
            val response = inflationApiService.getInflationData()
            if (response.isSuccessful) {
                val inflationData = response.body()
                if (inflationData != null) {
                    emit(Result.success(inflationApiMapper.mapToDomain(inflationData)))
                } else {
                    emit(Result.failure(Throwable("Error: Empty response body")))
                }
            } else {
                emit(Result.failure(Throwable("Error: Failed to fetch inflation data")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)
}
