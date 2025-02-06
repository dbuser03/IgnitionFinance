package com.unimib.ignitionfinance.data.repository.interfaces

import com.unimib.ignitionfinance.data.remote.model.InflationData
import kotlinx.coroutines.flow.Flow

interface InflationRepository {
    suspend fun fetchInflationData(): Flow<Result<List<InflationData>>>
}