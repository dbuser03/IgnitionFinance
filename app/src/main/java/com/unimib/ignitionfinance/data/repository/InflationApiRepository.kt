package com.unimib.ignitionfinance.data.repository

import com.unimib.ignitionfinance.data.remote.service.InflationApiService
import com.unimib.ignitionfinance.data.remote.mapper.InflationApiMapper
import com.unimib.ignitionfinance.domain.model.InflationData

class InflationRepository(private val inflationApiService: InflationApiService) {

    suspend fun fetchInflationData(): Result<List<InflationData>> {
        val response = inflationApiService.getInflationData()

        if (response.isSuccessful) {
            val inflationData = response.body()

            if (inflationData != null) {
                val domainData = InflationApiMapper.mapToDomain(inflationData)
                return Result.success(domainData)
            }
        }

        return Result.failure(Throwable("Failed to fetch inflation data"))
    }
}