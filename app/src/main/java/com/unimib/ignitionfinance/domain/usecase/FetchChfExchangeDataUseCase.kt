package com.unimib.ignitionfinance.domain.usecase

import com.unimib.ignitionfinance.data.repository.ExchangeRepository
import com.unimib.ignitionfinance.domain.model.ExchangeData
import kotlinx.coroutines.flow.Flow

class FetchChfExchangeDataUseCase(
    private val repository: ExchangeRepository
) {
    suspend operator fun invoke(): Flow<Result<List<ExchangeData>>> {
        return repository.fetchExchangeData("D.CHF.EUR.SP00.A")
    }
}
