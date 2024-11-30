package com.unimib.ignitionfinance.data.repository

import com.unimib.ignitionfinance.data.remote.api_service.InflationApiService
import com.unimib.ignitionfinance.data.remote.api_mapper.InflationApiMapper
import com.unimib.ignitionfinance.domain.model.InflationData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface InflationRepository {
    suspend fun fetchInflationData(): Result<List<InflationData>>
}

class InflationRepositoryImpl(
    private val inflationApiService: InflationApiService,
    private val inflationApiMapper: InflationApiMapper
) : InflationRepository {

    override suspend fun fetchInflationData(): Result<List<InflationData>> =
        withContext(Dispatchers.IO) {
            try {
                val response = inflationApiService.getInflationData()
                if (response.isSuccessful) {
                    val inflationData = response.body()
                    if (inflationData != null) {
                        val domainData = inflationApiMapper.mapToDomain(inflationData)
                        return@withContext Result.success(domainData)
                    }
                }
                Result.failure(Throwable("Error: Empty or invalid response"))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}
